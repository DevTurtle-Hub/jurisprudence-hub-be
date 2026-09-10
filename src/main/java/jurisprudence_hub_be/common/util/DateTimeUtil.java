package jurisprudence_hub_be.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class DateTimeUtil {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;
    private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter LOCAL_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter LOCAL_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateTimeUtil() {
    }

    public static Instant now() {
        return Instant.now();
    }

    public static ZonedDateTime now(String zoneId) {
        return ZonedDateTime.now(ZoneId.of(zoneId));
    }

    public static LocalDateTime nowLocal() {
        return LocalDateTime.now();
    }

    public static String formatInstant(Instant instant) {
        if (instant == null) {
            return null;
        }
        return ISO_FORMATTER.format(instant);
    }

    public static String formatLocalDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return LOCAL_DATE_FORMATTER.format(dateTime);
    }

    public static String formatLocalTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return LOCAL_TIME_FORMATTER.format(dateTime);
    }

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return LOCAL_DATETIME_FORMATTER.format(dateTime);
    }

    public static Instant parseInstant(String instantString) {
        if (instantString == null) {
            return null;
        }
        return Instant.parse(instantString);
    }

    public static long between(Instant start, Instant end, ChronoUnit unit) {
        if (start == null || end == null) {
            return 0;
        }
        return unit.between(start, end);
    }

    public static boolean isExpired(Instant instant, long durationInMinutes) {
        if (instant == null) {
            return true;
        }
        return Instant.now().isAfter(instant.plus(durationInMinutes, ChronoUnit.MINUTES));
    }

    public static Instant plusMinutes(Instant instant, long minutes) {
        if (instant == null) {
            return null;
        }
        return instant.plus(minutes, ChronoUnit.MINUTES);
    }

    public static Instant plusHours(Instant instant, long hours) {
        if (instant == null) {
            return null;
        }
        return instant.plus(hours, ChronoUnit.HOURS);
    }

    public static Instant plusDays(Instant instant, long days) {
        if (instant == null) {
            return null;
        }
        return instant.plus(days, ChronoUnit.DAYS);
    }
}
