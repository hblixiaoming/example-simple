package com.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * 时间工具类
 * 
 */
public class DateUtil {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss:SSS";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    private DateUtil() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);
    public static final long MINUTE = 60 * 1000L;

    /**
     * 获得当前系统时间 格式yyyy-MM-dd hh:mm:ss
     * 
     * @return Date
     * @author zeng88
     */
    public static final Date getCurrentTime() {
        return Calendar.getInstance().getTime();
    }

    /**
     * 获得当前系统时间 格式 yyyy-MM-dd
     * 
     * @return Date
     * @author zeng88
     */
    public static final Date getCurrentDate() {
        return getDateIgnoreTime(Calendar.getInstance().getTime());
    }

    /**
     * @return yyyy-MM-dd hh:mm:ss
     * @author zeng88
     */
    public static final String getCurrentTimeStr() {
        return formatTime(getCurrentTime());
    }

    /**
     * @return yyyy-MM-dd hh:mm:ss:SSS
     * @author zeng88
     */
    public static final String getCurrentTimeMillStr() {
        return formatTimeMill(getCurrentTime());
    }

    /**
     * @return yyyy-MM-dd
     * @author zeng88
     */
    public static final String getCurrentDateStr() {
        return formatDate(getCurrentDate());
    }

    /**
     * 获取时间上下间隔
     * 
     * @param baseDate
     * @return Date[]
     * @author zeng88
     */
    public static final Date[] getDifferenceDate(Date baseDate) {
        Date[] date = new Date[2];
        Calendar cal = Calendar.getInstance();
        cal.setTime(baseDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        date[0] = cal.getTime();
        cal.add(Calendar.DATE, 1);
        date[1] = cal.getTime();
        return date;
    }

    /**
     * 将时间的时分秒毫秒归零
     * 
     * @param baseDate
     * @return Date
     * @author zeng88
     */
    public static final Date getDateIgnoreTime(Date baseDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(baseDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 比较两个日期差,精确到天
     * 
     * @param date1
     * @param date2
     * @return int
     * @author zeng88
     */
    public static final int compareByDate(Date date1, Date date2) {
        int num = getDaysBetweenDates(date1, date2);
        if (num > 0)
            return 1;
        else if (num < 0)
            return -1;
        return num;
    }

    /**
     * 获得两个日期差几天,精确到秒
     * 
     * @param date1
     * @param date2
     * @return int
     * @author zeng88
     */
    public static final int compareByDateTime(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("Date can not be null.");
        }
        TimeZone timeZone = TimeZone.getDefault();
        long beginOffset = timeZone.getRawOffset();
        long endOffset = beginOffset;
        if (timeZone.inDaylightTime(date1))
            beginOffset += timeZone.getDSTSavings();
        if (timeZone.inDaylightTime(date2))
            endOffset += timeZone.getDSTSavings();
        long milli1 = (date1.getTime() + beginOffset) / 1000;
        long milli2 = (date2.getTime() + endOffset) / 1000;
        int retVal = 0;
        if (milli1 > milli2) {
            retVal = 1;
        } else if (milli1 < milli2) {
            retVal = -1;
        }
        return retVal;
    }

    /**
     * 获得两个日期差几天,精确到毫秒
     * 
     * @param date1
     * @param date2
     * @return int
     * @author zeng88
     */
    public static final int compareByTimestamp(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("Date can not be  null.");
        }
        TimeZone timeZone = TimeZone.getDefault();
        long beginOffset = timeZone.getRawOffset();
        long endOffset = beginOffset;
        if (timeZone.inDaylightTime(date1))
            beginOffset += timeZone.getDSTSavings();
        if (timeZone.inDaylightTime(date2))
            endOffset += timeZone.getDSTSavings();
        long milli1 = date1.getTime() + beginOffset;
        long milli2 = date2.getTime() + endOffset;
        int retVal = 0;
        if (milli1 > milli2) {
            retVal = 1;
        } else if (milli1 < milli2) {
            retVal = -1;
        }
        return retVal;
    }

    /**
     * 获得两个日期差几天
     * 
     * @param endDate
     * @param beginDate
     * @return int
     * @author zeng88
     */
    public static final int getDaysBetweenDates(Date endDate, Date beginDate) {
        if (endDate == null || beginDate == null) {
            throw new IllegalArgumentException("Date cannot be null.");
        }
        return (getDaysBetweenDates((TimeZone) null, endDate, beginDate));
    }

    /**
     * 获得两个日期差几天 不足一天算一天，
     * 
     * @param timeZone
     * @param endDate
     * @param beginDate
     * @return int
     * @author zeng88
     */
    public static final int getDaysBetweenDates(TimeZone timeZone, Date endDate, Date beginDate) {
        if (beginDate == null || endDate == null)
            throw new IllegalArgumentException("Date cannot be null.");
        if (timeZone == null)
            timeZone = TimeZone.getDefault();
        long beginOffset = timeZone.getRawOffset();
        long endOffset = beginOffset;
        if (timeZone.inDaylightTime(beginDate))
            beginOffset += timeZone.getDSTSavings();
        if (timeZone.inDaylightTime(endDate))
            endOffset += timeZone.getDSTSavings();
        long endDays = (endDate.getTime() + endOffset) / 86400000L;
        long beginDays = (beginDate.getTime() + beginOffset) / 86400000L;
        return ((int) (endDays - beginDays));
    }

    /**
     * 获得年份
     * 
     * @param date
     * @return int
     * @author zeng88
     */
    public static final int getYear(Date date) {
        if (date == null)
            return 0;
        return getCalendarField(date, Calendar.YEAR);
    }

    /**
     * 获得月份
     * 
     * @param date
     * @return int
     * @author zeng88
     */
    public static final int getMonth(Date date) {
        if (date == null)
            return 0;
        return getCalendarField(date, Calendar.MONTH) + 1;
    }

    /**
     * 获得天数
     * 
     * @param date
     * @return int
     * @author zeng88
     */
    public static final int getDay(Date date) {
        if (date == null)
            return 0;
        return getCalendarField(date, Calendar.DAY_OF_MONTH);
    }

    /**
     * 获得小时
     * 
     * @param date
     * @return int
     * @author zeng88
     */
    public static final int getHour(Date date) {
        if (date == null)
            return 0;
        return getCalendarField(date, Calendar.HOUR_OF_DAY);
    }

    /**
     * 获得分钟
     * 
     * @param date
     * @return int
     * @author zeng88
     */
    public static final int getMinute(Date date) {
        if (date == null)
            return 0;
        return getCalendarField(date, Calendar.MINUTE);
    }

    /**
     * 获得秒
     * 
     * @param date
     * @return int
     * @author zeng88
     */
    public static final int getSecond(Date date) {
        if (date == null)
            return 0;
        return getCalendarField(date, Calendar.SECOND);
    }

    /**
     * 获得毫秒
     * 
     * @param date
     * @return int
     * @author zeng88
     */
    public static final int getMilliSecond(Date date) {
        if (date == null)
            return 0;
        return getCalendarField(date, Calendar.MILLISECOND);
    }

    /**
     * 返回给定日历字段的值
     * 
     * @param date
     * @param field
     * @return int
     * @author zeng88
     */
    public static final int getCalendarField(Date date, int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(field);
    }

    /**
     * 添加或减去指定的时间量,按年
     * 
     * @param date
     * @param value
     * @return Date
     * @author zeng88
     */
    public static final Date addYear(Date date, int value) {
        return add(date, Calendar.YEAR, value);
    }

    /**
     * 添加或减去指定的时间量,按月
     * 
     * @param date
     * @param value
     * @return Date
     * @author zeng88
     */
    public static final Date addMonth(Date date, int value) {
        return add(date, Calendar.MONTH, value);
    }

    /**
     * 添加或减去指定的时间量,按日
     * 
     * @param date
     * @param value
     * @return Date
     * @author zeng88
     */
    public static final Date addDay(Date date, int value) {
        return add(date, Calendar.DAY_OF_MONTH, value);
    }

    /**
     * 添加或减去指定的时间量,按小时
     * 
     * @param date
     * @param value
     * @return Date
     * @author zeng88
     */
    public static final Date addHour(Date date, int value) {
        return add(date, Calendar.HOUR_OF_DAY, value);
    }

    /**
     * 添加或减去指定的时间量,按分钟
     * 
     * @param date
     * @param value
     * @return Date
     * @author zeng88
     */
    public static final Date addMinute(Date date, int value) {
        return add(date, Calendar.MINUTE, value);
    }

    /**
     * 添加或减去指定的时间量,按秒
     * 
     * @param date
     * @param value
     * @return Date
     * @author zeng88
     */
    public static final Date addSecond(Date date, int value) {
        return add(date, Calendar.SECOND, value);
    }

    /**
     * 添加或减去指定的天数
     * @param date LocalDateTime
     * @param value 天
     * @return date
     */
    public static Date addDay(LocalDateTime date, Long value) {
        return Date.from(date.plusDays(value).atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 添加或减去指定的小时
     * @param date LocalDateTime
     * @param value 小时
     * @return date
     */
    public static Date addHours(LocalDateTime date, Long value) {
        return Date.from(date.plusHours(value).atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 添加或减去指定的时间量,按毫秒
     * 
     * @param date
     * @param value
     * @return Date
     * @author zeng88
     */
    public static final Date addMilliSecond(Date date, int value) {
        return add(date, Calendar.MILLISECOND, value);
    }

    /**
     * 添加或减去指定的时间量
     * 
     * @param date
     * @param field
     * @param value
     * @return Date
     * @author zeng88
     */
    public static final Date add(Date date, int field, int value) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(field, value);
        return cal.getTime();
    }

    /**
     * 设置指定的时间量,按年
     * 
     * @param date
     * @param value
     * @return Date
     * @author zeng88
     */
    public static final Date setYear(Date date, int value) {
        return set(date, Calendar.YEAR, value);
    }

    /**
     * 设置指定的时间量,按月
     * 
     * @param date
     * @param value
     * @return Date
     * @author zeng88
     */
    public static final Date setMonth(Date date, int value) {
        return set(date, Calendar.MONTH, value);
    }

    private final static List<Integer> BIG_MONTH = Arrays.asList(1, 3, 5, 7, 8, 10, 12);
    private final static List<Integer> LITTER_MONTH = Arrays.asList(4, 6, 9, 11);

    /**
     * 设置指定的时间量,按日
     * 
     * @param date
     * @param value
     * @return Date
     * @author zeng88
     */
    public static final Date setDay(Date date, int value) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
            if (month == 2) {
                value = value > 29 ? 29 : value;
            } else if (LITTER_MONTH.contains(month)) {
                value = value > 30 ? 30 : value;
            }
        } else {
            if (month == 2) {
                value = value > 28 ? 28 : value;
            } else if (LITTER_MONTH.contains(month)) {
                value = value > 30 ? 30 : value;
            }
        }
        return set(date, Calendar.DAY_OF_MONTH, value);
    }

    /**
     * 设置指定的时间量,按小时
     * 
     * @param date
     * @param value
     * @return Date
     * @author zeng88
     */
    public static final Date setHour(Date date, int value) {
        return set(date, Calendar.HOUR_OF_DAY, value);
    }

    /**
     * 设置指定的时间量,按分钟
     * 
     * @param date
     * @param value
     * @return Date
     * @author zeng88
     */
    public static final Date setMinute(Date date, int value) {
        return set(date, Calendar.MINUTE, value);
    }

    /**
     * 设置指定的时间量,按秒
     * 
     * @param date
     * @param value
     * @return Date
     * @author zeng88
     */
    public static final Date setSecond(Date date, int value) {
        return set(date, Calendar.SECOND, value);
    }

    /**
     * 设置指定的时间量,按毫秒
     * 
     * @param date
     * @param value
     * @return Date
     * @author zeng88
     */
    public static final Date setMilliSecond(Date date, int value) {
        return set(date, Calendar.MILLISECOND, value);
    }

    /**
     * 设置指定的时间量
     * 
     * @param date
     * @param field
     * @param value
     * @return Date
     * @author zeng88
     */
    public static final Date set(Date date, int field, int value) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(field, value);
        return cal.getTime();
    }

    /**
     * 设置日期为当月的最后一天
     * 
     * @param date
     * @return Date
     * @author zeng88
     */
    public static final Date lastDate(Date date) {
        return addDay(addMonth(setDay(date, 1), 1), -1);
    }

    /**
     * 设置日期为当月的第一天
     * 
     * @param date
     * @return Date
     * @author zeng88
     */
    public static final Date firstDate(Date date) {
        return setDay(date, 1);
    }

    /**
     * 是否是当月最后一天
     * 
     * @param date
     * @return boolean
     * @author zeng88
     */
    public static final boolean isLastDate(Date date) {
        Date lastDate = lastDate(date);
        return compareByDate(date, lastDate) == 0;
    }

    /**
     * 是否是当月第一天
     * 
     * @param date
     * @return boolean
     * @author zeng88
     */
    public static final boolean isFirstDate(Date date) {
        return compareByDate(date, firstDate(date)) == 0;
    }

    /**
     * 是否是同一天
     * 
     * @param date1
     * @param date2
     * @return boolean
     * @author zeng88
     */
    public static final boolean isSameDate(Date date1, Date date2) {
        return compareByDate(getDateIgnoreTime(date1), getDateIgnoreTime(date2)) == 0;
    }

    /**
     * 是否是同一天月
     * 
     * @param date1
     * @param date2
     * @return boolean
     * @author zeng88
     */
    public static boolean isSameMonth(Date date1, Date date2) {
        return getMonth(date1) == getMonth(date2);
    }

    /**
     * 是否是同一天年
     * 
     * @param date1
     * @param date2
     * @return boolean
     * @author zeng88
     */
    public static boolean isSameYear(Date date1, Date date2) {
        return getYear(date1) == getYear(date2);
    }

    /**
     * 根据指定格式格式化时间
     * 
     * @param date
     * @param fmtString
     * @return String
     * @author zeng88
     */
    public static final String format(Date date, String fmtString) {
        return new SimpleDateFormat(fmtString).format(date);
    }

    /**
     * 格式格式化时间("yyyy-MM-dd")
     * 
     * @param date
     * @return String
     * @author zeng88
     */
    public static final String formatDate(Date date) {
        if (null == date)
            return null;
        return format(date, YYYY_MM_DD);
    }

    /**
     * 格式格式化时间("yyyy-MM-dd HH:mm:ss")
     * 
     * @param date
     * @return String
     * @author zeng88
     */
    public static final String formatTime(Date date) {
        if (null == date)
            return null;
        return format(date, YYYY_MM_DD_HH_MM_SS);
    }

    public static final String formatTimeMill(Date date) {
        if (null == date)
            return null;
        return format(date, YYYY_MM_DD_HH_MM_SS_SSS);
    }

    /**
     * 根据指定格式解析时间
     * 
     * @param dateString
     * @param fmtString
     * @return Date
     * @author zeng88
     */
    public static final Date parse(String dateString, String fmtString) {
        Date date = null;

        DateFormat format = new SimpleDateFormat(fmtString);
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            LOGGER.error("ParseException date ", e);
        }
        return date;
    }

    /**
     * 解析时间("yyyy-MM-dd")
     * 
     * @param dateString
     * @return Date
     * @author zeng88
     */
    public static final Date parseDate(String dateString) {
        return parse(dateString, YYYY_MM_DD);
    }

    /**
     * 解析时间("yyyy-MM-dd hh:mm:ss")
     * 
     * @param dateString
     * @return Date
     * @author zeng88
     */
    public static final Date parseTime(String dateString) {
        return parse(dateString, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 添加或减去指定的时间量,按实际月
     * 
     * @param date
     * @param value
     * @return
     * @author wangcj
     */
    public static final Date addActualMonth(Date date, int value) {
        if (isLastDate(date)) {
            if (value > 0) {
                return addDay(addMonth(firstDate(date), 2), -1);
            } else if (value < 0) {
                return addDay(firstDate(date), -1);
            } else {
                return addMonth(date, value);
            }
        } else {
            return addMonth(date, value);
        }
    }

    /**
     * 差1秒12点
     * 
     * @param date
     * @return
     */
    public static final Date endOfDay(Date date) {
        date = set(date, Calendar.HOUR_OF_DAY, 23);
        date = set(date, Calendar.MINUTE, 59);
        date = set(date, Calendar.SECOND, 59);
        return date;
    }

    /**
     * @Title: getRepayPlanConfirmTime @Description:
     *         获取还款计划时间,确认时间大于17:30:00分还款时间+1天 @param ConfirmTime 确认时间 @return
     *         Date @author zengsongbin @date 2016年1月27日下午8:24:55 @throws
     */
    public static Date getRepayPlanConfirmTime(Date confirmTime) {
        if (confirmTime == null) {
            return new Date();
        }
        Date base = confirmTime;
        base = DateUtil.setHour(base, 17);
        base = DateUtil.setMinute(base, 30);
        base = DateUtil.setSecond(base, 0);
        if (compareByDateTime(confirmTime, base) >= 0) {
            return addDay(confirmTime, 1);
        } else {
            return confirmTime;
        }
    }

    /**
     * 根据指定格式格式化时间
     * 
     * @param date
     * @param type
     * @return String
     * @author zeng88
     */
    public static String format(Date date, int type) {
        if(date==null){
            return null;
        }
        switch (type) {
            case 0:
                return formatDate(date);
            case 1:
                return format(date, "yyyy/MM");
            case 2:
                return format(date, "yyyyMMdd");
            case 3:
                return format(date, "yyyyMM");
            case 4:
                return format(date, "yyyy/MM/dd HH:mm:ss");
            case 5:
                return format(date, "yyyyMMddHHmmss");
            case 6:
                return format(date, "yyyy/MM/dd HH:mm");
            case 7:
                return format(date, "HH:mm:ss");
            case 8:
                return format(date, "HH:mm");
            case 9:
                return format(date, "HHmmss");
            case 10:
                return format(date, "HHmm");
            case 11:
                return format(date, YYYY_MM_DD);
            case 12:
                return format(date, YYYY_MM_DD_HH_MM_SS);
            case 13:
                return format(date, "yyyyMMddHHmmss");
            case 14:
                return format(date, "yyMMdd");
            case 15:
                return format(date, "yyyyMMdd'T'HHmmss");
            default:
                return format(date, YYYY_MM_DD);
        }
    }

    /**
     * @Title: getMillis @Description: 获得当前时间毫秒数 @param date @return long @author
     *         zengsongbin @date 2016年5月4日下午1:55:03 @throws
     */
    public static long getMillis(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }

    public static String diffDate(Date date, Date date1) {
        Long diffMillis = getMillis(date) - getMillis(date1);
        if (diffMillis < 0) {
            return "P";
        } else {
            DecimalFormat df = new DecimalFormat("#.##");
            Double diffHour = diffMillis / (3600D * 1000D);
            if (diffHour > 24) {
                return df.format(diffHour / 24D) + " Day";
            } else {
                return diffHour.intValue() + " hour";
            }
        }

    }

    public static String dateToStr(Date date, String pattern) {
        if (date == null)
            return null;
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    public static String dateToStr(Date date) {
        return dateToStr(date, "yyyy-MM-dd");
    }

    public static Date strToDate(String date) {
        try {
            String format = "yyyy-MM-dd";
            SimpleDateFormat fmt = new SimpleDateFormat(format);
            return fmt.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date strToDate2(String date) {
        try {
            String format = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat fmt = new SimpleDateFormat(format);
            return fmt.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 返回两个日期间的差异分钟数
     *
     * @param date1
     *            参照日期
     * @param date2
     *            比较日期
     * @return 参照日期与比较日期之间的天数差异，正数表示参照日期在比较日期之后，0表示两个日期同天，负数表示参照日期在比较日期之前
     */
    public static int minuteDiff(Date date1, Date date2) {
        long diff = date1.getTime() - date2.getTime();
        return (int) (diff / MINUTE);
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     * 
     * @param endDate
     * @param beginDate
     * @return
     */
    public static int differentDaysByMillisecond(Date endDate, Date beginDate) {
        int days = (int) ((endDate.getTime() - beginDate.getTime()) / (1000 * 3600 * 24));
        return days;
    }

    public static void main(String[] args) {
        String old = "2018-01-01 00:00:00";
        String s1 = "2018-4-03 13:12:14";
        System.out.println(DateUtil
                .differentDaysByMillisecond(DateUtil.parseTime(s1), DateUtil.parseTime(old)));
        String s = "1556294399000";
        Date datedue = new Date(Long.valueOf(s));
        System.out.println(DateUtil.formatTime(datedue));

        System.out.println(DateUtil.getCurrentTimeStr());
        System.out.println(DateUtil.getCurrentTimeMillStr());
    }
}
