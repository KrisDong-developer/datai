
package org.dromara.salesforce.dyna;

import org.apache.commons.beanutils.Converter;
import org.dromara.salesforce.model.DateOnlyCalendar;
import org.dromara.salesforce.model.NADateOnlyCalendarValue;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * DateOnlyConverter 类用于将字符串转换为仅包含日期部分的 Calendar 对象。
 *
 * 此类继承自 DateTimeConverter，专门处理只包含日期部分（不包含时间）的数据转换。
 * 主要用于处理 Salesforce 中的 Date 类型字段，这些字段只需要日期信息而不需要时间信息。
 *
 * 主要功能：
 * 1. 继承 DateTimeConverter 的所有日期解析功能
 * 2. 使用 DateOnlyCalendar 实例来确保只处理日期部分
 * 3. 支持欧洲日期格式和普通日期格式
 * 4. 支持处理 NA（Not Available）值
 *
 * 特点：
 * - 使用 DateOnlyCalendar 来表示只包含日期的数据
 * - 可以处理多种日期格式
 * - 支持时区设置
 * - 支持欧洲日期格式（dd/MM/yyyy）和美国日期格式（MM/dd/yyyy）
 */
public class DateOnlyConverter extends DateTimeConverter implements Converter {

    /**
     * 构造函数，创建一个新的 DateOnlyConverter 实例
     *
     * @param tz 时区设置
     * @param useEuroDateFormat 是否使用欧洲日期格式（dd/MM/yyyy），false 表示使用美国日期格式（MM/dd/yyyy）
     */
    public DateOnlyConverter(TimeZone tz, boolean useEuroDateFormat) {
        super(tz, useEuroDateFormat);
    }

    /**
     * 获取一个 DateOnlyCalendar 实例
     *
     * @param tz 时区设置
     * @return DateOnlyCalendar 实例
     */
    protected Calendar getCalendar(TimeZone tz) {
        return DateOnlyCalendar.getInstance(tz);
    }

    /**
     * 获取表示 NA（Not Available）值的日期实例
     *
     * @return NADateOnlyCalendarValue 实例，表示不可用的日期值
     */
    protected Calendar getNAValueCalendar() {
        return NADateOnlyCalendarValue.getInstance();
    }
}
