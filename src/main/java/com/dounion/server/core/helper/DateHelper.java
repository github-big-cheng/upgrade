package com.dounion.server.core.helper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 支持线程安全的 SimpleDateFormat.parse
 */
public class DateHelper {

    private static final Logger logger = LoggerFactory.getLogger(DateHelper.class);

    /** 锁对象 */
    private static final Object LOCK_OBJ = new Object();


    private final static String DEF_FORMAT = "yyyy-MM-dd HH:mm:ss";


    /** 存放不同的日期模板格式的sdf的Map */
    private static Map<String, ThreadLocal<SimpleDateFormat>> SDF_MAP = new HashMap<>();

    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getFormat(final String pattern) {

        ThreadLocal<SimpleDateFormat> tl = SDF_MAP.get(pattern);

        // do double-check
        if (tl == null) {
            synchronized (LOCK_OBJ) {
                tl = SDF_MAP.get(pattern);
                if (tl == null) {
                    // 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
                    logger.debug("put new format of pattern " + pattern + " to map");

                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            logger.debug("thread: " + Thread.currentThread() + " init pattern: " + pattern);
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    SDF_MAP.put(pattern, tl);
                }
            }
        }
        return tl.get();
    }

    /**
     * 日期格式化
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format(date, DEF_FORMAT);
    }

    /**
     * 日期格式化
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {

        if(date == null){
            return null;
        }

        return getFormat(pattern).format(date);
    }


    /**
     * 字符串转日期
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date parse(String dateStr) throws ParseException {
        return parse(dateStr, DEF_FORMAT);
    }

    /**
     * 字符串转日期
     * @param dateStr
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static Date parse(String dateStr, String pattern) throws ParseException {

        if(StringUtils.isBlank(dateStr)){
            return null;
        }

        return getFormat(pattern).parse(dateStr);
    }

    /**
     * 根据对象类型获取Date对象
     *      暂不支持Long类型字符串的转换
     * @param o
     * @return
     */
    public static Date getDate(Object o) {
        return getDate(o, null);
    }


    public static Integer getWeek(Date date){
        if(date == null){
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 根据对象类型获取Date对象
     *      暂不支持Long类型字符串的转换
     * @param o
     * @param pattern 指定日期格式
     * @return
     */
    public static Date getDate(Object o, String pattern) {

        if(o == null){
            return null;
        }

        if(o instanceof Date){
            return (Date) o;
        }

        if(o instanceof Long){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis((Long) o);
            return cal.getTime();
        }

        if(o instanceof String){
            String str = (String) o;
            try {
                String format =
                    StringUtils.isBlank(pattern) ? getDateFormat(str) : pattern;
                return getFormat(format).parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static Map<Integer, String> formatMap = new HashMap(){{
        put(0, "y");
        put(1, "M");
        put(2, "d");
        put(3, "H");
        put(4, "m");
        put(5, "s");
        put(6, "S");
    }};

    /**
     * 获取执行字符串的pattern
     *  目前测试过的格式如下：
     *         MM-dd
     *         yyyy-MM
     *         yyyy-MM-dd
     *         HH:mm
     *         yyyy-MM-dd HH:mm:ss
     *         yyyy-MM-dd HH:mm:ss:SSS
     *         HH时mm分ss秒SSS毫秒
     *         yyyy/MM/dd HH时mm分ss秒SSS毫秒
     *         当前yyyy年MM月dd日 HH时mm分ss秒SSS毫秒
     *   暂不支持连续数字的读取
     *   如：
     *      yyyyMMddHHmmssSSS
     *      yyyyMMdd
     *      yyyyMM
     *      MMdd
     *      等
     *
     * @param str
     * @return
     */
    public static String getDateFormat(String str) {
        boolean year = false;
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        if (pattern.matcher(str.substring(0, 4)).matches()) {
            year = true;
        }
        StringBuilder sb = new StringBuilder();
        int index = 0;
        if (!year) {
            if (str.contains("月") || str.contains("-") || str.contains("/")) {
                if (Character.isDigit(str.charAt(0))) {
                    index = 1;
                }
            } else {
                index = 3;
            }
        }
        for (int i = 0; i < str.length(); i++) {
            char chr = str.charAt(i);
            if (Character.isDigit(chr)) {
                sb.append(formatMap.get(index));
            } else {
                if (i > 0) {
                    char lastChar = str.charAt(i - 1);
                    if (Character.isDigit(lastChar)) {
                        index++;
                    }
                }
                sb.append(chr);
            }
        }
        return sb.toString();
    }

    /**
     * 返回2个日期之间相隔的 年/月/日/时/分/秒/毫秒
     * @param begin
     * @param end
     * @param pattern
     * @return
     */
    public static Long getBetween(Date begin, Date end, int pattern) {

        if(begin==null || end==null){
            return 0l;
        }

        Calendar beginCal = Calendar.getInstance();
        beginCal.setTime(begin);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);

        switch (pattern) {
            case Calendar.YEAR:
                return getByField(beginCal, endCal, Calendar.YEAR);
            case Calendar.MONTH:
                return getByField(beginCal, endCal, Calendar.YEAR)*12
                            + getByField(beginCal, endCal, Calendar.MONTH);
            case Calendar.DAY_OF_MONTH:
                return getTime(begin, end)/(24*60*60*1000);
            case Calendar.HOUR:
                return getTime(begin, end)/(60*60*1000);
            case Calendar.MINUTE:
                return getTime(begin, end)/(60*1000);
            case Calendar.SECOND:
                return getTime(begin, end)/1000;
            case Calendar.MILLISECOND:
                getByField(beginCal, endCal, pattern);
            default:
                return 0l;
        }
    }

    private static long getByField(Calendar begin, Calendar end, int field){
        return end.get(field) - begin.get(field);
    }

    private static long getTime(Date beginDate, Date endDate){
        return endDate.getTime() - beginDate.getTime();
    }


    public static int get(int field) {
        return get(new Date(), field);
    }

    public static int get(Date date, int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(field);
    }

    /**
     * 日期所在季度
     * @param date
     * @return
     */
    public static int getSeason(Date date) {
        int month = DateHelper.get(date, Calendar.MONTH);
        if (month <= 2) {
            return 1;
        } else if (month <= 5) {
            return 2;
        } else if (month <= 8) {
            return 3;
        } else /*if (month <= 11) */ {
            return 4;
        }
    }

    /**
     * 日期所在月最后一天
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    /**
     * 日期所在季度最后一天
     * @param date
     * @return
     */
    public static Date getLastDayOfSeason(Date date) {
        int season = getSeason(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (season == 1) {
            // 第一季度
            // 0,1,2
            calendar.set(Calendar.MONTH, 2);
        } else if (season == 2) {
            // 第二季度
            // 3,4,5
            calendar.set(Calendar.MONTH, 5);
        } else if (season == 3) {
            // 第三季度
            // 6,7,8
            calendar.set(Calendar.MONTH, 8);
        } else {
            // 第四季度
            // 9,10,11
            calendar.set(Calendar.MONTH, 11);
        }
        return getLastDayOfMonth(calendar.getTime());
    }

    /**
     * 获取某年最后一天日期
     * @param year
     * @return
     */
    public static Date getLastDayOfYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

}
