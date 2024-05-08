package roomescape.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.controller.response.UserResponse;
import roomescape.domain.Member;
import roomescape.domain.Role;

@Repository
public class MemberRepository {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public MemberRepository(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("user_table")
                .usingGeneratedKeyColumns("id");
    }

    public Member save(Member member) {
        Long memberId = jdbcInsert.executeAndReturnKey(Map.of(
                        "name", member.getName(),
                        "email", member.getEmail(),
                        "password", member.getPassword(),
                        "role", member.getRole().name()))
                .longValue();

        return new Member(
                memberId,
                member.getName(),
                member.getEmail(),
                member.getPassword(),
                member.getRole());
    }

    public boolean checkExistMember(String email, String password) {
        String sql = """
                SELECT 
                CASE WHEN EXISTS (
                        SELECT 1
                        FROM user_table
                        WHERE email = ? AND password = ?
                    )
                    THEN TRUE
                    ELSE FALSE
                END
                """;
        boolean b = Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, email, password));
        return b;
    }

    public Member findByEmail(String email) {
        String sql = """
                SELECT
                    id, name, email, password, role
                FROM
                    user_table
                WHERE
                    email = ?
                """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Member(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                Role.getR(rs.getString("role"))
        ), email);
    }

    public Optional<UserResponse> findById(Long memberId) {
        String sql = """
                SELECT
                    id, name, email, password, role
                FROM
                    user_table
                WHERE
                    id = ?
                """;
        UserResponse userResponse = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new UserResponse(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                Role.getR(rs.getString("role"))
        ), memberId);
        return Optional.ofNullable(userResponse);
    }

    public List<Member> findAll() {
        String sql = "SELECT * FROM user_table";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Member(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                Role.getR(rs.getString("role"))
        ));
    }
}
