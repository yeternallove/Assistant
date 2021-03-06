package com.yeternal.assistant.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.lang.Assert;
import com.yeternal.assistant.model.dto.DateDay;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * <p>
 * 时间相关工具类
 * </p>
 *
 * @author eternallove
 * @date Created in 2019/10/15 13:42
 */
public class TimeUtil {
    private final static int LAST_MONTH = 12;
    private final static int[] LEAP_DAY = {2, 29};
    /**
     * 设定 农历 基础时间点
     */
    private final static DateDay BASE_LUNAR_DATE = new DateDay(1901, 1, 1, false);
    /**
     * 设定 阳历 基础时间点
     */
    private final static Date BASE_DATE = new DateTime("1901-02-19", "yyyy-MM-dd");
    private final static FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyyMMdd");


    public static int getPackageTime(DateDay day) {
        return LunarUtil.getPackageTime(day.getYear(), day.getMonth(), day.getDay(), day.isLeap());
    }

    public static int getPackageTime(Date day) {
        return Integer.parseInt(DateUtil.format(day, DATE_FORMAT));
    }

    public static Date transToSolar(DateDay day) {
        int interval = LunarUtil.getInterval(BASE_LUNAR_DATE.getYear(), BASE_LUNAR_DATE.getMonth(), BASE_LUNAR_DATE.getDay(), BASE_LUNAR_DATE.isLeap(), day.getYear(), day.getMonth(), day.getDay(), day.isLeap());
        return DateUtil.offsetDay(BASE_DATE, interval);
    }

    public static DateDay transToLunar(Date date) {
        Assert.isTrue(BASE_DATE.before(date), "It has to be after 1901-02-19");
        long between = DateUtil.between(BASE_DATE, date, DateUnit.DAY);
        return getUnpackTime(LunarUtil.getLunarDate(BASE_LUNAR_DATE.getYear(), BASE_LUNAR_DATE.getMonth(), BASE_LUNAR_DATE.getDay(), BASE_LUNAR_DATE.isLeap(), (int) between));
    }

    /**
     * 获取在指定年的生日日期 若该年无生日则返回空
     *
     * @param birthday 生日
     * @param year     指定年
     * @param lunar    是否农历
     * @return 阳历时间
     */
    public static Date getBirthdayInYear(int birthday, int year, boolean lunar) {
        DateDay dateDay = getUnpackTime(birthday);
        if (lunar) {
            birthday = LunarUtil.getIntercalation(year, dateDay.getMonth(), dateDay.getDay(), dateDay.isLeap());
            return transToSolar(getUnpackTime(birthday));
        }
        if (dateDay.getMonth() == LEAP_DAY[0] && dateDay.getDay() == LEAP_DAY[1] && !DateUtil.isLeapYear(year)) {
            return null;
        }
        return Date.from(LocalDateTime.of(year, dateDay.getMonth(), dateDay.getDay(), 0, 0).atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取某个时间之后的生日日期
     *
     * @param birthday 生日
     * @param now      当前时间
     * @param lunar    是否农历
     * @return 生日日期
     */
    public static Date getNextBirthday(int birthday, Date now, boolean lunar) {
        if (null == now) {
            now = DateUtil.date();
        }
        Date date = null;
        int startYear = DateUtil.year(now);
        while (date == null || now.after(date)) {
            date = getBirthdayInYear(birthday, startYear++, lunar);
        }
        return date;
    }

    private static DateDay getUnpackTime(int date) {
        DateDay dateDay = new DateDay();
        dateDay.setDay(date % 100);
        int month = date / 100 % 100;
        if (month > LAST_MONTH) {
            dateDay.setLeap(true);
            month -= LAST_MONTH;
        }
        dateDay.setMonth(month);
        dateDay.setYear(date / 10000);
        return dateDay;
    }
}
