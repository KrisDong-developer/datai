package org.dromara.salesforce.dyna;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.apache.logging.log4j.Logger;
import org.dromara.salesforce.model.NACalendarValue;
import org.dromara.salesforce.model.NATextValue;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * DateTimeConverter 类用于将字符串转换为包含日期和时间信息的 Calendar 对象。
 *
 * 此类实现了 Apache Commons Beanutils 的 Converter 接口，专门用于处理各种日期时间格式字符串的解析和转换。
 * 它支持多种日期时间格式，包括 ISO8601 标准格式、欧洲日期格式和美国日期格式。
 *
 * 主要功能：
 * 1. 解析多种日期时间格式的字符串
 * 2. 支持欧洲日期格式（dd/MM/yyyy）和美国日期格式（MM/dd/yyyy）
 * 3. 处理时区信息
 * 4. 支持 NA（Not Available）值处理
 * 5. 提供灵活的日期时间解析机制
 *
 * 支持的格式包括但不限于：
 * - ISO8601 格式：yyyy-MM-dd'T'HH:mm:ss.SSS, yyyy-MM-dd'T'HH:mm:ss 等
 * - 基本格式：yyyyMMdd'T'HHmmss, yyyyMMdd'T'HHmm 等
 * - 欧洲/美国日期格式：dd/MM/yyyy HH:mm, MM/dd/yyyy HH:mm 等
 */
@Slf4j
public class DateTimeConverter implements Converter {

    static final TimeZone GMT_TZ = TimeZone.getTimeZone("GMT");
    static final List<String> supportedEuropeanPatterns = getSupportedPatterns(true);
    static final List<String> supportedRegularPatterns = getSupportedPatterns(false);

    /**
     * 是否使用欧洲日期格式
     */
    final boolean useEuroDates;
    /**
     * 时区设置
     */
    final TimeZone timeZone;

    /**
     * 构造函数，创建一个新的 DateTimeConverter 实例
     *
     * @param tz 时区设置
     * @param useEuroDateFormat 是否使用欧洲日期格式（dd/MM/yyyy），false 表示使用美国日期格式（MM/dd/yyyy）
     */
    public DateTimeConverter(TimeZone tz, boolean useEuroDateFormat) {
        this.timeZone = tz;
        this.useEuroDates = useEuroDateFormat;
    }

    /**
     * 使用指定的日期格式解析日期字符串
     *
     * @param dateString 要解析的日期字符串
     * @param fmt 用于解析的日期格式
     * @return 解析成功的 Calendar 对象，如果解析失败则返回 null
     */
    private Calendar parseDate(String dateString, DateFormat fmt) {
        final ParsePosition pos = new ParsePosition(0);
        fmt.setLenient(false);
        final Date date = fmt.parse(dateString, pos);
        // we only want to use the date if parsing succeeded and used the entire string
        if (date != null && pos.getIndex() == dateString.length()) {
            Calendar cal = getCalendar(fmt.getTimeZone());
            cal.setTimeInMillis(date.getTime());
            return cal;
        }
        return null;
    }

    /**
     * 将指定的输入对象转换为 Calendar 对象。
     *
     * 转换过程：
     * 1. 如果输入值为 null，直接返回 null
     * 2. 如果输入值是 NATextValue 实例，返回 NA 值的 Calendar 实例
     * 3. 如果输入值已经是 Calendar 类型，直接返回
     * 4. 如果输入值是 Date 类型，将其转换为 Calendar 对象
     * 5. 将其他类型的值转换为字符串进行处理
     * 6. 尝试使用预定义的日期格式模式进行解析
     * 7. 如果预定义模式都无法解析，尝试使用系统默认的日期时间格式解析
     * 8. 如果仍然无法解析，抛出 ConversionException 异常
     *
     * @param type 数据类型，表示该值应该被转换成的目标类型
     * @param value 输入值，需要被转换的对象
     * @return 转换后的 Calendar 对象
     * @exception ConversionException 如果无法成功执行转换
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object convert(Class type, Object value) {
        if (value == null) {
            return null;
        }

        if(value instanceof NATextValue) {
            return getNAValueCalendar();
        }

        if (value instanceof Calendar) { return value; }

        Calendar cal = getCalendar(this.timeZone);
        if (value instanceof Date) {
            cal.setTimeInMillis(((Date)value).getTime());
            return cal;
        }

        String dateString = value.toString().trim();
        int len = dateString.length();

        if (len == 0) return null;

        TimeZone timeZoneForValue = this.timeZone;
        if ("z".equalsIgnoreCase(dateString.substring(len - 1))) {
            dateString = dateString.substring(0, len - 1);
            timeZoneForValue = GMT_TZ;
        }

        for (String pattern : useEuroDates ? supportedEuropeanPatterns : supportedRegularPatterns) {
            final DateFormat df = new SimpleDateFormat(pattern);
            df.setTimeZone(timeZoneForValue);
            cal = parseDate(dateString, df);
            if (cal != null) return cal;
        }

        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT);
        df.setTimeZone(this.timeZone);
        cal = parseDate(dateString, df);
        if (cal != null) return cal;

        df = DateFormat.getDateInstance(DateFormat.SHORT);
        df.setTimeZone(this.timeZone);
        cal = parseDate(dateString, df);
        if (cal != null) return cal;

        throw new ConversionException("Failed to parse date: " + value);
    }

    /**
     * 获取指定时区的 Calendar 实例
     *
     * @param timezone 时区设置
     * @return 指定时区的 Calendar 实例
     */
    // NOTE: Always use this method to get Calendar instance
    protected Calendar getCalendar(TimeZone timezone) {
        return Calendar.getInstance(timezone);
    }

    /**
     * 获取表示 NA（Not Available）值的 Calendar 实例
     *
     * @return NACalendarValue 实例，表示不可用的日期时间值
     */
    protected Calendar getNAValueCalendar() {
        return NACalendarValue.getInstance();
    }

    /**
     * 获取支持的日期时间格式模式列表
     *
     * 这些模式是 Java text.SimpleDateFormat 支持的模式的子集
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</a>
     *
     * @param europeanDates 是否使用欧洲日期格式
     * @return 支持的日期时间格式模式列表
     */
    private static List<String> getSupportedPatterns(boolean europeanDates) {

        List<String> basePatterns = new ArrayList<String>();

        // Extended patterns means using the - delimiter in the date

        List<String> extendedPatterns = new ArrayList<String>();
        extendedPatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSS");
        extendedPatterns.add("yyyy-MM-dd'T'HH:mm:ss");
        extendedPatterns.add("yyyy-MM-dd'T'HH:mm");
        extendedPatterns.add("yyyy-MM-dd'T'HH");
        extendedPatterns.add("yyyy-MM-dd'T'"); //?
        extendedPatterns.add("yyyy-MM-dd");

        //As per ISO 8601 5.2.1.1, when only the days are omitted, a - is necessary between year and month
        List<String> extendedPatternsDateOnly = new ArrayList<String>();
        extendedPatternsDateOnly.add("yyyy-MM");
        extendedPatternsDateOnly.add("yyyyMMdd");
        extendedPatternsDateOnly.add("yyyy");

        // Using a space instead of 'T' to separate date and time
        List<String> extendedPatternsWithoutT = new ArrayList<String>();
        extendedPatternsWithoutT.add("yyyy-MM-dd HH:mm:ss.SSS");
        extendedPatternsWithoutT.add("yyyy-MM-dd HH:mm:ss");
        extendedPatternsWithoutT.add("yyyy-MM-dd HH:mm");
        extendedPatternsWithoutT.add("yyyy-MM-dd HH");

        // Not using anything to deliminate the date elements from each
        // other. Matched through known lengths of components.
        List<String> basicPatterns = new ArrayList<String>();
        basicPatterns.add("yyyyMMdd'T'HH:mm:ss.SSS");
        basicPatterns.add("yyyyMMdd'T'HH:mm:ss");
        basicPatterns.add("yyyyMMdd'T'HH:mm");
        basicPatterns.add("yyyyMMdd'T'HH");
        basicPatterns.add("yyyyMMdd'T'"); //?

        // Using a space instead of 'T' to separate date and time
        List<String> basicPatternsWithoutT = new ArrayList<String>();
        basicPatternsWithoutT.add("yyyyMMdd HH:mm:ss.SSS");
        basicPatternsWithoutT.add("yyyyMMdd HH:mm:ss");
        basicPatternsWithoutT.add("yyyyMMdd HH:mm");
        basicPatternsWithoutT.add("yyyyMMdd HH");

        //as per the iso 8601 spec
        List<String> fullBasicFormats = new ArrayList<String>();
        fullBasicFormats.add("yyyyMMdd'T'HHmmss");
        fullBasicFormats.add("yyyyMMdd'T'HHmm");
        fullBasicFormats.add("yyyyMMdd'T'HH");


        List<String> fullBasicFormatsWithoutT = new ArrayList<String>();
        fullBasicFormatsWithoutT.add("yyyyMMdd HHmmss");
        fullBasicFormatsWithoutT.add("yyyyMMdd HHmm");
        fullBasicFormatsWithoutT.add("yyyyMMdd HH");


        String baseDate = europeanDates ? "dd/MM/yyyy" : "MM/dd/yyyy";

        // Using a space instead of 'T' to separate date and time
        List<String> slashPatternsWithoutT = new ArrayList<String>();
        slashPatternsWithoutT.add(baseDate +" HH:mm:ss.SSS");
        slashPatternsWithoutT.add(baseDate +" HH:mm:ss");
        slashPatternsWithoutT.add(baseDate +" HH:mm");
        slashPatternsWithoutT.add(baseDate +" HH");
        slashPatternsWithoutT.add(baseDate +" HHZ");
        slashPatternsWithoutT.add(baseDate);

        List<String> slashPatternsWithT = new ArrayList<String>();
        slashPatternsWithT.add(baseDate +  "'T'HH:mm:ss.SSS");
        slashPatternsWithT.add(baseDate +  "'T'HH:mm:ss");
        slashPatternsWithT.add(baseDate +  "'T'HH:mm");
        slashPatternsWithT.add(baseDate +  "'T'HH");

        //order is important here because if it matches against the wrong format first, it will
        //misinterpret the time

        basePatterns.addAll(fullBasicFormatsWithoutT);
        basePatterns.addAll(fullBasicFormats);
        basePatterns.addAll(basicPatterns);
        basePatterns.addAll(basicPatternsWithoutT);
        basePatterns.addAll(extendedPatternsDateOnly);
        basePatterns.addAll(extendedPatterns);
        basePatterns.addAll(extendedPatternsWithoutT);
        basePatterns.addAll(slashPatternsWithoutT);
        basePatterns.addAll(slashPatternsWithT);

        List<String> timeZones = new ArrayList<>();
        // uppercase Z => RFC822 TimeZone
        basePatterns.forEach(p -> timeZones.add(p + "Z"));
        basePatterns.forEach(p -> timeZones.add(p + " Z"));

        // uppercase X => ISO8601 TimeZone
        basePatterns.forEach(p -> timeZones.add(p + "XXX"));
        basePatterns.forEach(p -> timeZones.add(p + " XXX"));

        basePatterns.forEach(p -> timeZones.add(p + "'Z'Z"));
        basePatterns.forEach(p -> timeZones.add(p + "'z'Z"));
        basePatterns.forEach(p -> timeZones.add(p + "z"));

        basePatterns.addAll(timeZones);

        return basePatterns;
    }
}
