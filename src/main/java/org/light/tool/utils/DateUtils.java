package org.light.tool.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * 日期时间工具类
 * @author gaoziyang
 * @since 2022-08-11 15:40:31
 */
public final class DateUtils {
    /**
     * 标准日期时间格式化器
     */
    public static final DateTimeFormatter STANDARD_FORMATTER;

    /**
     * 精确到毫秒的日期时间格式化器
     */
    public static final DateTimeFormatter MILLI_FORMATTER;

    /**
     * ISO_8601标准的日期时间格式化器
     */
    public static final DateTimeFormatter ISO_8601_FORMATTER;

    /**
     * ISO_Local标准的日期时间格式化器
     */
    public static final DateTimeFormatter ISO_LOCAL_FORMATTER;

    /**
     * 系统默认时区
     */
    private static final ZoneId DEFAULT_ZONE_ID;

    /**
     * 上海时区
     */
    private static final ZoneId SHANGHAI_ZONE_ID;

    /**
     * UTC+8偏移量
     */
    private static final ZoneOffset UTC_8;

    static {
        ISO_LOCAL_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        STANDARD_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        MILLI_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        ISO_8601_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DEFAULT_ZONE_ID = ZoneId.systemDefault();
        SHANGHAI_ZONE_ID = ZoneId.of("Asia/Shanghai");
        UTC_8 = ZoneOffset.ofHours(8);
    }

    /* ================================= 转换 ================================= */

    /**
     * 将 Date 类型转换为 LocalDate 类型
     * @param date LocalDateTime 类型的日期时间
     * @return LocalDate 类型
     */
    public static LocalDate dateToLocalDate(Date date) {
        if (date == null) return null;
        return instantToLocalDateTime(date.toInstant()).toLocalDate();
    }

    /**
     * 将 LocalDate 类型转换为 Date 类型
     * @param date LocalDate 类型的日期时间
     * @return Date 类型的日期时间
     */
    public static Date localDateToDate(LocalDate date) {
        if (date == null) return null;
        return Date.from(LocalDateTime.of(date, LocalTime.now()).atZone(DEFAULT_ZONE_ID).toInstant());
    }


    /**
     * 将 LocalDateTime 类型转换为 Date 类型
     * @param dateTime 类型的日期时间
     * @return Date 类型的日期时间
     */
    public static Date localDateTimeToDate(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return Date.from(dateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * 将 Date 类型转换为 LocalDateTime 类型
     * @param date Date 类型的日期时间
     * @return LocalDateTime 类型的日期时间
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) return null;
        return instantToLocalDateTime(date.toInstant());
    }

    /**
     * 将 LocalDateTime 类型转换为 OffsetDateTime 类型
     * @param dateTime LocalDateTime 类型的日期时间
     * @return OffsetDateTime 类型的日期时间
     */
    public static OffsetDateTime localToOffset(LocalDateTime dateTime) {
        return localToOffset(dateTime, UTC_8);
    }

    /**
     * 将 LocalDateTime 类型转换为 OffsetDateTime 类型
     * @param dateTime LocalDateTime 类型的日期时间
     * @param zoneOffset 时间偏移量
     * @return OffsetDateTime 类型的日期时间
     */
    public static OffsetDateTime localToOffset(LocalDateTime dateTime, ZoneOffset zoneOffset) {
        if (dateTime == null) return null;
        return dateTime.atOffset(zoneOffset);
    }

    /**
     * 将 OffsetDateTime 类型转换为 LocalDateTime 类型
     * @param dateTime OffsetDateTime 类型的日期时间
     * @return LocalDateTime 类型的日期时间
     */
    public static LocalDateTime offsetToLocal(OffsetDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.toLocalDateTime();
    }

    /**
     * 将 LocalDateTime 类型转换为 ZonedDateTime 类型
     * @param dateTime LocalDateTime 类型的日期时间
     * @return ZonedDateTime 类型的日期时间
     */
    public static ZonedDateTime localToZoned(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.atZone(SHANGHAI_ZONE_ID);
    }

    /**
     * 将 ZonedDateTime 类型转换为 LocalDateTime 类型
     * @param dateTime ZonedDateTime 类型的日期时间
     * @return LocalDateTime 类型的日期时间
     */
    public static LocalDateTime zonedToLocal(ZonedDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.toLocalDateTime();
    }

    /* ================================= 偏移量和时区 ================================= */

    /**
     * 为日期时间设置默认偏移量
     * @param dateTime 日期时间
     * @return 日期时间
     */
    public static LocalDateTime withDefaultOffset(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return localToOffset(dateTime, ZoneOffset.UTC)
                .withOffsetSameInstant(UTC_8)
                .toLocalDateTime();
    }

    /**
     * 为日期时间设置偏移量
     * @param dateTime 日期时间
     * @param hours 小时偏移量
     * @return 日期时间
     */
    public static LocalDateTime withOffsetHours(LocalDateTime dateTime, int hours) {
        if (dateTime == null) return null;
        return localToOffset(dateTime, ZoneOffset.UTC)
                .withOffsetSameInstant(ZoneOffset.ofHours(hours))
                .toLocalDateTime();
    }

    /**
     * 为日期时间设置偏移量
     * @param dateTime 日期时间
     * @param hours 小时偏移量
     * @param minutes 分钟偏移量
     * @return 日期时间
     */
    public static LocalDateTime withOffsetHoursMinutes(LocalDateTime dateTime, int hours, int minutes) {
        if (dateTime == null) return null;
        return localToOffset(dateTime, ZoneOffset.UTC)
                .withOffsetSameInstant(ZoneOffset.ofHoursMinutes(hours, minutes))
                .toLocalDateTime();
    }

    /**
     * 为日期时间设置偏移量
     * @param dateTime 日期时间
     * @param hours 小时偏移量
     * @param minutes 分钟偏移量
     * @param seconds 秒偏移量
     * @return 日期时间
     */
    public static LocalDateTime withOffsetHoursMinutesSeconds(LocalDateTime dateTime, int hours, int minutes, int seconds) {
        if (dateTime == null) return null;
        return localToOffset(dateTime, ZoneOffset.UTC)
                .withOffsetSameInstant(ZoneOffset.ofHoursMinutesSeconds(hours, minutes, seconds))
                .toLocalDateTime();
    }

    /**
     * 为日期时间设置当前系统默认时区
     * @param dateTime 日期时间
     * @return 日期时间
     */
    public static LocalDateTime withDefaultZoneId(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return localToOffset(dateTime, ZoneOffset.UTC)
                .atZoneSameInstant(DEFAULT_ZONE_ID)
                .toLocalDateTime();
    }

    /**
     * 为日期时间设置中国时区
     * @param dateTime 日期时间
     * @return 日期时间
     */
    public static LocalDateTime withChinaZoneId(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return localToOffset(dateTime, ZoneOffset.UTC)
                .atZoneSameInstant(SHANGHAI_ZONE_ID)
                .toLocalDateTime();
    }

    /**
     * 为日期时间设置时区
     * @param dateTime 日期时间
     * @param zoneId 时区
     * @return 日期时间
     */
    public static LocalDateTime withZoneId(LocalDateTime dateTime, ZoneId zoneId) {
        if (dateTime == null) return null;
        return localToOffset(dateTime, ZoneOffset.UTC)
                .atZoneSameInstant(zoneId)
                .toLocalDateTime();
    }

    /* ================================= 格式化和解析 ================================= */

    /**
     * 格式化日期时间（yyyy-MM-dd HH:mm:ss）
     * @param dateTime 要格式化的日期时间
     * @return 标准格式的日期时间字符串
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return STANDARD_FORMATTER.format(dateTime);
    }

    /**
     * 将 Instant 类型转换为 LocalDateTime 类型
     * @param instant Instant 类型的瞬时
     * @return LocalDateTime 类型的日期时间
     */
    public static LocalDateTime instantToLocalDateTime(Instant instant) {
        if (instant == null) return null;
        return instant.atZone(DEFAULT_ZONE_ID).toLocalDateTime();
    }

    /**
     * 将 LocalDateTime 类型 转换为 Instant 类型
     * @param dateTime LocalDateTime 类型的日期时间
     * @return Instant 类型的瞬时
     */
    public static Instant localDateTimeToInstant(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.atZone(DEFAULT_ZONE_ID).toInstant();
    }

    /**
     * 格式化日期时间（yyyy-MM-dd HH:mm:ss.SSS）
     * @param dateTime 要格式化的日期时间
     * @return 标准格式的日期时间字符串
     */
    public static String formatMilli(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return format(dateTime, MILLI_FORMATTER);
    }

    /**
     * 格式化日期时间（yyyy-MM-ddTHH:mm:ss.SSSZ）
     * @param dateTime 要格式化的日期时间
     * @return 标准格式的日期时间字符串
     */
    public static String formatISO8601(OffsetDateTime dateTime) {
        if (dateTime == null) return null;
        return format(dateTime, ISO_8601_FORMATTER);
    }

    /**
     * 使用指定的格式化器来格式化日期时间
     * @param dateTime 要格式化的日期时间
     * @param dateTimeFormatter 日期时间格式化器
     * @return 标准格式的日期时间字符串
     */
    public static String format(Temporal dateTime, DateTimeFormatter dateTimeFormatter) {
        if (dateTimeFormatter == null || dateTime == null) return null;
        return dateTimeFormatter.format(dateTime);
    }

    /**
     * 使用标准格式解析日期时间（yyyy-MM-dd HH:mm:ss）
     * @param datetime 日期时间字符串
     * @return 解析后的 LocalDateTime 类
     */
    public static LocalDateTime parse(String datetime) {
        if (datetime == null) return null;
        return parse(datetime, STANDARD_FORMATTER);
    }

    /**
     * 使用毫秒格式解析日期时间（yyyy-MM-dd HH:mm:ss.SSS）
     * @param datetime 日期时间字符串
     * @return 解析后的 LocalDateTime 类
     */
    public static LocalDateTime parseMilli(String datetime) {
        if (datetime == null) return null;
        return parse(datetime, MILLI_FORMATTER);
    }

    /**
     * 使用ISO8601格式解析日期时间（yyyy-MM-ddTHH:mm:ss.SSSZ）
     * @param datetime 日期时间字符串
     * @return 解析后的 LocalDateTime 类
     */
    public static LocalDateTime parseISO8601(String datetime) {
        if (datetime == null) return null;
        return parse(datetime, ISO_8601_FORMATTER);
    }

    /**
     * 使用指定日期时间格式来解析日期时间
     * @param dateTime 日期时间字符串
     * @return 解析后的 LocalDateTime 类
     */
    public static LocalDateTime parse(String dateTime, DateTimeFormatter dateTimeFormatter) {
        if (dateTime == null) return null;
        return LocalDateTime.parse(dateTime, dateTimeFormatter);
    }

    /* ================================= 特殊日期 ================================= */

    /**
     * 获取当前周的周一日期
     * @return 周一日期
     */
    public static LocalDate getWeekFirstDay() {
        return getWeekFirstDay(LocalDate.now());
    }

    /**
     * 获取指定日期所在周的周一日期
     * @param date 指定日期
     * @return 周一日期
     */
    public static LocalDate getWeekFirstDay(LocalDate date) {
        return date
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                .plusDays(1L);
    }

    /**
     * 获取当前周的周日日期
     * @return 周日日期
     */
    public static LocalDate getWeekLastDay() {
        return getWeekLastDay(LocalDate.now());
    }

    /**
     * 获取指定日期所在周的周日日期
     * @param date 指定日期
     * @return 周日日期
     */
    public static LocalDate getWeekLastDay(LocalDate date) {
        return date
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))
                .minusDays(1L);
    }

    /**
     * 获取本月第一天
     * @return 日期
     */
    public static LocalDate getMonthFirstDay() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取某月第一天
     * @return 日期
     */
    public static LocalDate getMonthFirstDay(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取本月最小日期时间
     * @return 日期
     */
    public static LocalDateTime getMonthMinDatetime() {
        return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN);
    }

    /**
     * 获取本月最小日期时间
     * @return 日期
     */
    public static LocalDateTime getMonthMinDatetime(LocalDate date) {
        return LocalDateTime.of(date.with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN);
    }

    /**
     * 获取本月最后一天
     * @return 日期
     */
    public static LocalDate getMonthLastDay() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取某月最后一天
     * @return 日期
     */
    public static LocalDate getMonthLastDay(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取本月最大日期时间
     * @return 日期
     */
    public static LocalDateTime getMonthMaxDatetime() {
        return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX);
    }

    /**
     * 获取本月最大日期时间
     * @return 日期
     */
    public static LocalDateTime getMonthMaxDatetime(LocalDate date) {
        return LocalDateTime.of(date.with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX);
    }

    /**
     * 获取下个月的第一天
     * @return 日期
     */
    public static LocalDate getNextMonthFirstDay() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth());
    }

    /**
     * 获取某个日期下个月的第一天
     * @return 日期
     */
    public static LocalDate getNextMonthFirstDay(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfNextMonth());
    }

    /**
     * 获取本年第一天
     * @return 日期
     */
    public static LocalDate getYearFirstDay() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
    }

    /**
     * 获取本年最后一天
     * @return 日期
     */
    public static LocalDate getYearLastDay() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
    }

    /**
     * 获取明年第一天
     * @return 日期
     */
    public static LocalDate getNextYearFirstDay() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfNextYear());
    }

    /* ================================= 区间 ================================= */

    /**
     * 获取两个时间区间纳秒数
     * @param startInclusive 开始区间（包含）
     * @param endExclusive 结束区间（不包含）
     * @return 纳秒数
     */
    public static long getBetweenNanos(Temporal startInclusive, Temporal endExclusive) {
        return ChronoUnit.NANOS.between(startInclusive, endExclusive);
    }

    /**
     * 获取两个时间区间毫秒数
     * @param startInclusive 开始区间（包含）
     * @param endExclusive 结束区间（不包含）
     * @return 毫秒数
     */
    public static long getBetweenMillis(Temporal startInclusive, Temporal endExclusive) {
        return ChronoUnit.MILLIS.between(startInclusive, endExclusive);
    }

    /**
     * 获取两个时间区间秒数
     * @param startInclusive 开始区间（包含）
     * @param endExclusive 结束区间（不包含）
     * @return 秒数
     */
    public static long getBetweenSeconds(Temporal startInclusive, Temporal endExclusive) {
        return ChronoUnit.SECONDS.between(startInclusive, endExclusive);
    }

    /**
     * 获取两个时间区间分钟数
     * @param startInclusive 开始区间（包含）
     * @param endExclusive 结束区间（不包含）
     * @return 分钟数
     */
    public static long getBetweenMinutes(Temporal startInclusive, Temporal endExclusive) {
        return ChronoUnit.MINUTES.between(startInclusive, endExclusive);
    }

    /**
     * 获取两个时间区间小时数
     * @param startInclusive 开始区间（包含）
     * @param endExclusive 结束区间（不包含）
     * @return 小时数
     */
    public static long getBetweenHours(Temporal startInclusive, Temporal endExclusive) {
        return ChronoUnit.HOURS.between(startInclusive, endExclusive);
    }

    /**
     * 获取两个时间区间半天数
     * @param startInclusive 开始区间（包含）
     * @param endExclusive 结束区间（不包含）
     * @return 半天数
     */
    public static long getBetweenHalfDays(Temporal startInclusive, Temporal endExclusive) {
        return ChronoUnit.HALF_DAYS.between(startInclusive, endExclusive);
    }

    /**
     * 获取两个时间区间天数
     * @param startInclusive 开始区间（包含）
     * @param endExclusive 结束区间（不包含）
     * @return 天数
     */
    public static long getBetweenDays(Temporal startInclusive, Temporal endExclusive) {
        return ChronoUnit.DAYS.between(startInclusive, endExclusive);
    }

    /**
     * 获取两个时间区间星期数
     * @param startInclusive 开始区间（包含）
     * @param endExclusive 结束区间（不包含）
     * @return 星期数
     */
    public static long getBetweenWeeks(Temporal startInclusive, Temporal endExclusive) {
        return ChronoUnit.WEEKS.between(startInclusive, endExclusive);
    }

    /**
     * 获取两个时间区间月份数
     * @param startInclusive 开始区间（包含）
     * @param endExclusive 结束区间（不包含）
     * @return 月份数
     */
    public static long getBetweenMonths(Temporal startInclusive, Temporal endExclusive) {
        return ChronoUnit.MONTHS.between(startInclusive, endExclusive);
    }

    /**
     * 获取两个时间区间年数
     * @param startInclusive 开始区间（包含）
     * @param endExclusive 结束区间（不包含）
     * @return 年数
     */
    public static long getBetweenYears(Temporal startInclusive, Temporal endExclusive) {
        return ChronoUnit.YEARS.between(startInclusive, endExclusive);
    }
}