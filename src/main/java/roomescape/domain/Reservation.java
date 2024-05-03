package roomescape.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Reservation {
    private final Long id;
    private final Name name;
    private final LocalDate date;
    private final ReservationTime time;
    private final Theme theme;

    public Reservation(String name, LocalDate date, LocalTime time, Theme theme) {
        this(null, new Name(name), date, new ReservationTime(time), theme);
    }

    public Reservation(String name, LocalDate date, ReservationTime time, Theme theme) {
        this(null, new Name(name), date, time, theme);
    }

    public Reservation(Long id, Name name, LocalDate date, ReservationTime time, Theme theme) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    public boolean hasSameTheme(Reservation reservation) {
        return theme.equals(reservation.theme);
    }

    public boolean hasSameDateTime(Reservation reservation) {
        return getDateTime().equals(reservation.getDateTime());
    }

    public boolean isBeforeNow() {
        return getDateTime().isBefore(LocalDateTime.now());
    }

    public long getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    public LocalDateTime getDateTime() {
        return LocalDateTime.of(date, time.getStartAt());
    }
}
