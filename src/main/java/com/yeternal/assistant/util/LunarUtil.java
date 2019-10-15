package com.yeternal.assistant.util;

/**
 * <p>
 * 农历工具类
 * </p>
 *
 * @author eternallove
 * @date Created in 2019/10/12 9:10
 */
public class LunarUtil {

    private final static int START_YEAR = 1900;
    private final static int FLAG_LEAP = 0x10000;
    private final static int FLAG_MOUTH = 0x8000;
    private final static int FLAG_LEAP_MOUTH = 0x8;
    private final static int LAST_MONTH = 12;

    private final static long[] LUNAR_INFO = new long[]{
            0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
            0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,
            0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
            0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,
            0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,
            0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0,
            0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,
            0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6,
            0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,
            0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0,
            0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5,
            0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,
            0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,
            0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,
            0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0};

    /**
     * 计算指定日期偏移相应天数对应农历日期
     *
     * @param y        年
     * @param m        月
     * @param d        日
     * @param leap     闰月
     * @param interval 间隔时间
     * @return 特定格式的年月日 闰月采用+12算法 出错返回-1
     */
    private static int getLunarDate(int y, int m, int d, boolean leap, long interval) {
        long info = getInfo(y);
        // 同月
        int days = (leap ? leapDays(info) : monthDays(info, m)) - d;
        if (days >= interval) {
            return getPackageTime(y, m, (int) (d + interval), leap);
        }
        interval -= days;
        // 特殊闰月处理
        int month = leapMonth(info);
        if (!leap && m == month) {
            days = leapDays(info);
            if (days >= interval) {
                return getPackageTime(y, m, (int) interval, true);
            }
            interval -= days;
        }
        // 同年
        long date = getLunarDate(y, m + 1, month, interval);
        if (date > 0) {
            return (int) date;
        }
        interval += date;
        // 跳过中间年
        days = 0;
        while (days < interval) {
            interval -= days;
            days = yearDays(getInfo(++y));
        }
        // 最后一年偏移
        month = leapMonth(getInfo(y));
        return (int) getLunarDate(y, 1, month, interval);
    }

    private static long getLunarDate(int y, int startMonth, int leapMonth, long interval) {
        final long old = interval;
        long info = getInfo(y);
        int days;
        for (int i = startMonth; i <= LAST_MONTH; i++) {
            days = monthDays(info, i);
            if (days >= interval) {
                return getPackageTime(y, i, (int) interval, false);
            }
            interval -= days;
            if (leapMonth == i) {
                days = leapDays(info);
                if (days >= interval) {
                    return getPackageTime(y, i, (int) interval, true);
                }
                interval -= days;
            }
        }
        return interval - old;
    }

    /**
     * 求指定日期之间的差值（天）
     *
     * @param y1    起始年
     * @param m1    起始月
     * @param d1    起始日
     * @param leap1 闰月
     * @param y2    目标年
     * @param m2    目标月
     * @param d2    目标日
     * @param leap2 闰月
     * @return 天数
     */
    private static int interval(int y1, int m1, int d1, boolean leap1, int y2, int m2, int d2, boolean leap2) {
        // 假设已经进行参数校验
        // 1.年份 月份范围校验
        // 2.日范围校验
        // 3.闰月真实性校验
        long info1 = getInfo(y1);
        long info2 = getInfo(y2);
        int border1 = leap1 ? m1 : m1 - 1;
        int border2 = leap2 ? m2 : m2 - 1;
        int sum = d2 - d1;
        for (int i = 1; i <= LAST_MONTH; i++) {
            if (i > border1) {
                sum += monthDays(info1, i);
            }
            if (i <= border2) {
                sum += monthDays(info2, i);
            }
        }
        // 闰月补偿
        int month = leapMonth(info1);
        if (leap1 || month >= m1) {
            sum += leapDays(info1);
        }
        month = leapMonth(info2);
        if (month < m2) {
            sum += leapDays(info2);
        }
        return sum + yearDays(y1 + 1, y2 - 1);
    }

    /**
     * 指定年份之间间隔几天
     *
     * @param y1 起始年
     * @param y2 目标年
     * @return 天数 y1>y2 返回0
     */
    private static int yearDays(int y1, int y2) {
        int sum = 0;
        for (int i = y1; i <= y2; i++) {
            sum += yearDays(getInfo(i));
        }
        return sum;
    }

    /**
     * 传回农历 y年的总天数
     *
     * @param info 闰年信息
     * @return 天数
     */
    private static int yearDays(long info) {
        int sum = 348;
        for (int i = FLAG_MOUTH; i > FLAG_LEAP_MOUTH; i >>= 1) {
            if ((info & i) != 0) {
                sum += 1;
            }
        }
        return sum + leapDays(info);
    }

    /**
     * 传回农历 y年m月的总天数
     *
     * @param info 闰年信息
     * @param m    月
     * @return 天数
     */
    private static int monthDays(long info, int m) {
        return (info & (FLAG_LEAP >> m)) == 0 ? 29 : 30;
    }

    /**
     * 传回农历 y年闰月的天数
     *
     * @param info 闰年信息
     * @return 闰月天数
     */
    private static int leapDays(long info) {
        if (leapMonth(info) == 0) {
            return 0;
        }
        return (info & FLAG_LEAP) == 0 ? 29 : 30;
    }

    /**
     * 传回农历 y年闰哪个月 1-12 , 没闰传回 0
     *
     * @param info 闰年信息
     * @return 几月
     */
    private static int leapMonth(long info) {
        return (int) (info & 0xf);
    }

    private static long getInfo(int y) {
        return LUNAR_INFO[y - START_YEAR];
    }

    /**
     * 生成特定格式封装时间
     *
     * @param y 年
     * @param m 月
     * @param d 日
     * @return 特定格式的年月日 闰月采用+12算法
     */
    private static int getPackageTime(int y, int m, int d, boolean leap) {
        return y * 10000 + (leap ? m + 12 : m) * 100 + d;
    }
}
