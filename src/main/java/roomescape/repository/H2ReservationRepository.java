package roomescape.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Role;
import roomescape.domain.Theme;
import roomescape.domain.Member;

@Repository
public class H2ReservationRepository implements ReservationRepository {
    private final ReservationRowMapper rowMapper;
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public H2ReservationRepository(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.rowMapper = new ReservationRowMapper();
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("reservation")
                .usingGeneratedKeyColumns("id");
    }

    public List<Reservation> findAll() {
        return jdbcTemplate.query(getBasicSelectQuery(), rowMapper);
    }

    public Reservation save(Reservation reservation) {
        Long reservationId = jdbcInsert.executeAndReturnKey(Map.of(
                        "date", reservation.getDate(),
                        "member_id", reservation.getMember().getId(),
                        "time_id", reservation.getTimeId(),
                        "theme_id", reservation.getThemeId()))
                .longValue();

        return new Reservation(
                reservationId,
                reservation.getDate(),
                reservation.getMember(),
                reservation.getTime(),
                reservation.getTheme());
    }

    public void deleteById(Long id) {
        String sql = "delete from reservation where id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Reservation> findByDateAndThemeId(LocalDate date, Long themeId) {
        String conditionQuery = " where r.date = ? and tm.id = ?";
        String sql = getBasicSelectQuery() + conditionQuery;

        return jdbcTemplate.query(sql, rowMapper, date, themeId);
    }

    public List<Reservation> findByPeriod(LocalDate startDate, LocalDate endDate) {
        String conditionQuery = " where r.date between ? and ?";
        String sql = getBasicSelectQuery() + conditionQuery;

        return jdbcTemplate.query(sql, rowMapper, startDate, endDate);
    }

    @Override
    public List<Reservation> findSearchReservation(Long themeId, Long memberId, LocalDate dateFrom, LocalDate dateTo) {
        String conditionQuery = " where tm.id = ? and u.id = ? and r.date between ? and ?";
        String sql = getBasicSelectQuery() + conditionQuery;
        List<Reservation> reservations = jdbcTemplate.query(sql, rowMapper, themeId, memberId, dateFrom, dateTo);
        return reservations;
    }

    private String getBasicSelectQuery() {
        return """
                    select 
                        r.id as reservation_id,
                        r.date as reservation_date,
                        t.id as time_id,
                        t.start_at as time_value,
                        tm.id as theme_id,
                        tm.name as theme_name,
                        tm.description as theme_description,
                        tm.thumbnail as theme_thumbnail,
                        u.id as user_id,
                        u.name as user_name,
                        u.email as user_email,
                        u.password as user_password,
                        u.role as user_role
                    from reservation as r
                    inner join reservation_time as t
                    on r.time_id = t.id
                    inner join theme as tm
                    on r.theme_id = tm.id 
                    inner join user_table as u
                    on r.member_id = u.id
                """;
    }

    private static class ReservationRowMapper implements RowMapper<Reservation> {
        @Override
        public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Reservation(
                    rs.getLong("reservation_id"),
                    rs.getDate("date").toLocalDate(),
                    new Member(rs.getLong("user_id"),
                            rs.getString("user_name"),
                            rs.getString("email"),
                            rs.getString("user_password"),
                            Role.getR(rs.getString("user_role"))),
                    new ReservationTime(
                            rs.getLong("time_id"),
                            rs.getTime("time_value").toLocalTime()),
                    new Theme(
                            rs.getLong("theme_id"),
                            rs.getString("theme_name"),
                            rs.getString("theme_description"),
                            rs.getString("theme_thumbnail")
                    ));
        }
    }
}
