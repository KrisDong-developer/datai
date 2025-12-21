
package org.dromara.salesforce.model;


import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * DateOnlyCalendar 类是一个专门处理日期（不含时间）的 Calendar 实现。
 *
 * 此类继承自 GregorianCalendar，专门用于处理 Salesforce 中的 Date 类型字段，
 * 这些字段只需要日期信息而不需要时间信息。该类确保在处理日期时正确处理时区问题，
 * 并将时间部分设置为零（午夜）以避免时间相关的问题。
 *
 * 主要功能：
 * 1. 继承 GregorianCalendar 的所有功能
 * 2. 重写 setTimeInMillis 方法以确保只处理日期部分
 * 3. 处理时区转换，确保日期值在不同时区下保持一致
 * 4. 提供 getInstance 静态方法创建 DateOnlyCalendar 实例
 *
 * 特点：
 * - 当 useGMTForDateField 配置为 true 时，使用 GMT 时区处理日期
 * - 自动将时间部分设置为 0（午夜），确保只保留日期信息
 * - 处理时区偏移，确保日期在不同时区下保持一致性
 * - 与 Salesforce Date 字段的语义保持一致
 */
@Slf4j
public class DateOnlyCalendar extends GregorianCalendar {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    static final TimeZone GMT_TZ = TimeZone.getTimeZone("GMT");

    /**
     * 默认构造函数，创建一个 DateOnlyCalendar 实例
     */
    public DateOnlyCalendar() {
        super();
    }

    /**
     * 私有构造函数，使用指定的时区创建 DateOnlyCalendar 实例
     *
     * @param tz 时区设置
     */
    private DateOnlyCalendar(TimeZone tz) {
        // Use the timezone param to update the date by 1 in setDate()
        super(tz);
    }

    /**
     * 设置以毫秒为单位的时间值，并确保只保留日期部分
     *
     * 此方法重写了父类的 setTimeInMillis 方法，增加了以下处理：
     * 1. 检查并设置默认时区为 GMT（如果未设置时区）
     * 2. 根据配置决定是否使用 GMT 处理日期字段
     * 3. 将时间部分（小时、分钟、秒、毫秒）设置为 0
     * 4. 处理时区偏移，确保日期值正确
     *
     * @param specifiedTimeInMilliSeconds 以毫秒为单位的时间值
     */
    public void setTimeInMillis(long specifiedTimeInMilliSeconds) {
        TimeZone myTimeZone = super.getTimeZone();

        if (myTimeZone == null) {
            log.info("timezone is null. Settting it to GMT");
            myTimeZone = GMT_TZ;
            super.setTimeZone(myTimeZone);
        } else {
            log.info("Timezone is " + myTimeZone.getDisplayName());
        }
        Calendar cal = Calendar.getInstance(myTimeZone);
        cal.setTimeInMillis(specifiedTimeInMilliSeconds);


        if (myTimeZone != null) {
            // Set hour, minute, second, and millisec to 0 (12:00AM) as it is date-only value
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.AM_PM, Calendar.AM);

            int timeZoneDifference = myTimeZone.getOffset(cal.getTimeInMillis());
            if (timeZoneDifference > 0) {
                // timezone is ahead of GMT, compensate for it as server-side thinks it is in GMT.
                cal.setTimeInMillis(cal.getTimeInMillis() + timeZoneDifference);
            }
        }
        super.setTimeInMillis(cal.getTimeInMillis());
    }

    /**
     * 获取 DateOnlyCalendar 实例的静态工厂方法
     *
     * 根据配置和时区参数创建 DateOnlyCalendar 实例：
     * 1. 如果配置 useGMTForDateField 为 true 或时区为 null，则使用 GMT 时区
     * 2. 否则使用指定的时区创建实例
     *
     * @param timeZone 时区设置，如果为 null 则使用 GMT 时区
     * @return DateOnlyCalendar 实例
     */
    public static DateOnlyCalendar getInstance(TimeZone timeZone) {
        if (timeZone == null) {
            timeZone = GMT_TZ;
        }
        return new DateOnlyCalendar(timeZone);
    }
}
