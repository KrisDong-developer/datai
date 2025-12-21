package org.dromara.salesforce.util;

import lombok.extern.slf4j.Slf4j;
import org.dromara.salesforce.domain.bo.MigrationFieldBo;

import java.math.BigDecimal;
import java.text.*;
import java.util.*;
import java.io.Reader;
import java.nio.CharBuffer;
import java.sql.Clob;

/**
 * 数据转换工具类
 * <p>
 * 提供Salesforce字段与数据库字段类型之间的转换功能，以及其他相关数据转换操作
 * </p>
 *
 * @author lingma
 * @since 1.0.0
 */
@Slf4j
public class ConvertUtil {

    /**
     * 根据MigrationField信息转换为数据库字段类型
     *
     * @param field MigrationFieldBo对象
     * @return 数据库字段类型
     */
    public static String convertTypeSFToLocal(MigrationFieldBo field) {
        String fieldType = field.getFieldDataType();
        Integer fieldLength = field.getFieldLength();
        Integer fieldScale = field.getFieldScale();

        if (fieldType == null) {
            return "text";
        }

        switch (fieldType.toLowerCase()) {
            case "id":
            case "reference":
                return "varchar(" + (fieldLength != null ? fieldLength : 18) + ")";
            case "string":
                // Name字段用varchar方便加索引
                if ("Name".equalsIgnoreCase(field.getField())) {
                    return "varchar(" + (fieldLength != null ? fieldLength : 255) + ")";
                } else {
                    return "text";
                }
            case "url":
            case "email":
            case "phone":
            case "picklist":
            case "textarea":
            case "multipicklist":
            case "address":
            case "combobox":
            case "location":
            case "encryptedstring":
            case "anytype":
                return "text";
            case "date":
                return "date";
            case "datetime":
                return "datetime";
            case "boolean":
                return "tinyint(1)";
            case "time":
                return "time(3)";
            case "long":
            case "int":
                return "int(" + (fieldLength != null ? Math.min(fieldLength, 11) : 11) + ")";
            case "double":
            case "currency":
            case "percent":
                int precision = 18;
                int scale = fieldScale != null ? fieldScale : 2;
                return "decimal(" + precision + "," + scale + ")";
            case "base64":
                return "longblob";
            default:
                return "text";
        }
    }

    /**
     * 将本地数据转换为Salesforce字段类型
     *
     * @param fieldType 字段类型
     * @param data      数据字符串
     * @return 转换后的对象
     * @throws ParseException 解析异常
     */
    public static Object convertTypeLocalToSF(String fieldType, String data) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date;
        //date转Calendar类型
        Calendar calendar = Calendar.getInstance();
        switch (fieldType) {
            case "int":
                return Integer.parseInt(data);
            case "double":
            case "currency":
            case "percent":
                return new BigDecimal(data);
            case "boolean":
                return Boolean.valueOf(data);
            case "date":
                try {
                    return sdf.parse(data+"T08:00:00Z");
                } catch (ParseException e) {
                    log.error("日期解析异常", e);
                }
            case "datetime":
                try {
                    date = sd.parse(data);
                }catch (ParseException e){
                    //解决当时间秒为0时，转换秒精度丢失问题
                    date = sd.parse(data+":00");
                }
                calendar.setTime(date);
                return calendar;
            case "time":
                return adjustHour(data);
            default:
                return data;
        }
    }

    /**
     * 调整小时时间（减去8小时时差）
     *
     * @param timeStr 时间字符串
     * @return 调整后的小时时间
     */
    public static String adjustHour(String timeStr) {
        // 提取小时部分并转换为整数
        int hour = Integer.parseInt(timeStr.substring(0, 2));

        // 减去8小时并处理跨天逻辑
        int adjustedHour = hour - 8;
        if (adjustedHour < 0) {
            adjustedHour += 24;  // 处理负数情况（跨天）
        }

        // 格式化为两位字符串（自动补零）
        String newHour = String.format("%02d", adjustedHour);

        // 拼接原始字符串的剩余部分（分钟+秒）
        String timeWithoutMilliseconds = newHour + timeStr.substring(2);

        // 添加毫秒部分和Z后缀
        // 如果原始字符串不包含毫秒，则添加.000
        if (timeStr.length() <= 8) { // HH:mm:ss 格式，长度为8
            return timeWithoutMilliseconds + ".000Z";
        } else if (timeStr.contains(".")) { // 已经包含毫秒
            return timeWithoutMilliseconds + "Z";
        } else {
            return timeWithoutMilliseconds + ".000Z";
        }
    }

    /**
     * 将对象转换为Boolean类型
     * 支持将"yes", "y", "true", "on", "1"转换为true
     * 支持将"no", "n", "false", "off", "0"转换为false
     *
     * @param value 待转换的对象
     * @return 转换后的Boolean值
     */
    public static Boolean convertBoolean(Object value) {
        if (value == null || value instanceof Boolean) {
            return (Boolean) value;
        }

        String stringValue = value.toString().trim();
        if (stringValue.length() == 0) {
            return null;
        }

        if (stringValue.equalsIgnoreCase("yes") ||
                stringValue.equalsIgnoreCase("y") ||
                stringValue.equalsIgnoreCase("true") ||
                stringValue.equalsIgnoreCase("on") ||
                stringValue.equalsIgnoreCase("1")) {
            return Boolean.TRUE;
        } else if (stringValue.equalsIgnoreCase("no") ||
                stringValue.equalsIgnoreCase("n") ||
                stringValue.equalsIgnoreCase("false") ||
                stringValue.equalsIgnoreCase("off") ||
                stringValue.equalsIgnoreCase("0")) {
            return Boolean.FALSE;
        } else {
            throw new IllegalArgumentException("无法将字符串转换为布尔值: " + stringValue);
        }
    }

    /**
     * 将对象转换为Integer类型
     *
     * @param value 待转换的对象
     * @return 转换后的Integer值
     */
    public static Integer convertInteger(Object value) {
        if (value == null || String.valueOf(value).length() == 0) {
            return null;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return Integer.valueOf(((Number) value).intValue());
        }

        try {
            NumberFormat numFormat = DecimalFormat.getIntegerInstance(Locale.getDefault());
            numFormat.setParseIntegerOnly(true);
            Number number = numFormat.parse(value.toString());
            return Integer.valueOf(number.intValue());
        } catch (ParseException e) {
            throw new IllegalArgumentException("无法转换为整数: " + value, e);
        }
    }

    /**
     * 将对象转换为Double类型
     *
     * @param value 待转换的对象
     * @return 转换后的Double值
     */
    public static Double convertDouble(Object value) {
        if (value == null || String.valueOf(value).length() == 0) {
            return null;
        }
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        try {
            return Double.valueOf(value.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("无法转换为双精度浮点数: " + value, e);
        }
    }

    /**
     * 将对象转换为String类型，并清理XML不支持的字符
     *
     * @param value 待转换的对象
     * @return 转换后的String值
     */
    public static String convertString(Object value) {
        if (value == null || String.valueOf(value).isEmpty()) {
            return null;
        }

        // 处理CLOB类型
        if (value instanceof Clob) {
            final StringBuilder sb = new StringBuilder(1024);
            final CharBuffer cbuf = CharBuffer.allocate(1024);
            try {
                final Reader rdr = ((Clob) value).getCharacterStream();
                while (rdr.read(cbuf) >= 0) {
                    cbuf.rewind();
                    sb.append(cbuf);
                    cbuf.rewind();
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("读取CLOB时出错", e);
            }
            value = sb.toString();
        }

        return cleanseString(value.toString());
    }

    /**
     * 清理字符串，移除XML不支持的字符
     *
     * @param value 输入字符串
     * @return 清理后的字符串
     */
    private static String cleanseString(String value) {
        if (value == null) return value;

        StringBuilder buff = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            // parallel code of XmlWriter.write()
            switch (c) {
                case '\n': // 换行符允许
                case '\r': // 回车符允许
                case '\t': // 制表符允许
                    // 这些字符是明确允许的，作为一般规则的例外:
                    buff.append(c);
                    break;
                default:
                    if (((c >= 0x20) && (c <= 0xD7FF)) || ((c >= 0xE000) && (c <= 0xFFFD))) {
                        buff.append(c);
                    } else if (Character.isHighSurrogate(c) && (i + 1) < value.length() && Character.isLowSurrogate(value.charAt(i + 1))) {
                        buff.append(c);
                        buff.append(value.charAt(i + 1));
                        i++;
                    }
                    // 对于这些范围之外的字符（如控制字符），
                    // 不做任何操作；在XML中打印这些字符是不合法的，
                    // 即使转义也不行
            }
        }
        return buff.toString();
    }

    // 从DateTimeConverter提取的方法
    private static final TimeZone GMT_TZ = TimeZone.getTimeZone("GMT");
    private static final List<String> SUPPORTED_PATTERNS = getSupportedPatterns(false);
    private static final List<String> SUPPORTED_EUROPEAN_PATTERNS = getSupportedPatterns(true);

    /**
     * 将对象转换为日期时间类型
     *
     * @param value         待转换的对象
     * @param timeZone      时区
     * @param useEuroDates  是否使用欧洲日期格式
     * @return 转换后的Calendar值
     */
    public static Calendar convertDateTime(Object value, TimeZone timeZone, boolean useEuroDates) {
        if (value == null) {
            return null;
        }

        if (value instanceof Calendar) {
            return (Calendar) value;
        }

        Calendar cal = Calendar.getInstance(timeZone);
        if (value instanceof Date) {
            cal.setTimeInMillis(((Date) value).getTime());
            return cal;
        }

        String dateString = value.toString().trim();
        int len = dateString.length();

        if (len == 0) return null;

        TimeZone timeZoneForValue = timeZone;
        if ("z".equalsIgnoreCase(dateString.substring(len - 1))) {
            dateString = dateString.substring(0, len - 1);
            timeZoneForValue = GMT_TZ;
        }

        List<String> patterns = useEuroDates ? SUPPORTED_EUROPEAN_PATTERNS : SUPPORTED_PATTERNS;
        for (String pattern : patterns) {
            final DateFormat df = new SimpleDateFormat(pattern);
            df.setTimeZone(timeZoneForValue);
            cal = parseDate(dateString, df, timeZone);
            if (cal != null) return cal;
        }

        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT);
        df.setTimeZone(timeZone);
        cal = parseDate(dateString, df, timeZone);
        if (cal != null) return cal;

        df = DateFormat.getDateInstance(DateFormat.SHORT);
        df.setTimeZone(timeZone);
        cal = parseDate(dateString, df, timeZone);
        if (cal != null) return cal;

        throw new IllegalArgumentException("日期解析失败: " + value);
    }

    /**
     * 解析日期字符串
     *
     * @param dateString 日期字符串
     * @param fmt        日期格式
     * @param timeZone   时区
     * @return 解析后的Calendar对象
     */
    private static Calendar parseDate(String dateString, DateFormat fmt, TimeZone timeZone) {
        final ParsePosition pos = new ParsePosition(0);
        fmt.setLenient(false);
        final Date date = fmt.parse(dateString, pos);
        // 只有在解析成功且使用了整个字符串时才使用该日期
        if (date != null && pos.getIndex() == dateString.length()) {
            Calendar cal = Calendar.getInstance(timeZone);
            cal.setTimeInMillis(date.getTime());
            return cal;
        }
        return null;
    }

    /**
     * 生成DataLoader支持的所有日期格式模式
     * 这些模式是Java text.SimpleDateFormat支持的模式的子集
     * 参考: https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
     */
    private static List<String> getSupportedPatterns(boolean europeanDates) {
        List<String> basePatterns = new ArrayList<String>();

        // 扩展模式意味着在日期中使用-分隔符
        List<String> extendedPatterns = new ArrayList<String>();
        extendedPatterns.add("yyyy-MM-dd'T'HH:mm:ss.SSS");
        extendedPatterns.add("yyyy-MM-dd'T'HH:mm:ss");
        extendedPatterns.add("yyyy-MM-dd'T'HH:mm");
        extendedPatterns.add("yyyy-MM-dd'T'HH");
        extendedPatterns.add("yyyy-MM-dd'T'"); //?
        extendedPatterns.add("yyyy-MM-dd");

        // 根据ISO 8601 5.2.1.1，当日部分被省略时，年和月之间需要一个-
        List<String> extendedPatternsDateOnly = new ArrayList<String>();
        extendedPatternsDateOnly.add("yyyy-MM");
        extendedPatternsDateOnly.add("yyyyMMdd");
        extendedPatternsDateOnly.add("yyyy");

        // 使用空格而不是'T'分隔日期和时间
        List<String> extendedPatternsWithoutT = new ArrayList<String>();
        extendedPatternsWithoutT.add("yyyy-MM-dd HH:mm:ss.SSS");
        extendedPatternsWithoutT.add("yyyy-MM-dd HH:mm:ss");
        extendedPatternsWithoutT.add("yyyy-MM-dd HH:mm");
        extendedPatternsWithoutT.add("yyyy-MM-dd HH");

        // 不使用任何东西来分隔日期元素。
        // 通过已知的组件长度进行匹配。
        List<String> basicPatterns = new ArrayList<String>();
        basicPatterns.add("yyyyMMdd'T'HH:mm:ss.SSS");
        basicPatterns.add("yyyyMMdd'T'HH:mm:ss");
        basicPatterns.add("yyyyMMdd'T'HH:mm");
        basicPatterns.add("yyyyMMdd'T'HH");
        basicPatterns.add("yyyyMMdd'T'"); //?

        // 使用空格而不是'T'分隔日期和时间
        List<String> basicPatternsWithoutT = new ArrayList<String>();
        basicPatternsWithoutT.add("yyyyMMdd HH:mm:ss.SSS");
        basicPatternsWithoutT.add("yyyyMMdd HH:mm:ss");
        basicPatternsWithoutT.add("yyyyMMdd HH:mm");
        basicPatternsWithoutT.add("yyyyMMdd HH");

        // 根据iso 8601规范
        List<String> fullBasicFormats = new ArrayList<String>();
        fullBasicFormats.add("yyyyMMdd'T'HHmmss");
        fullBasicFormats.add("yyyyMMdd'T'HHmm");
        fullBasicFormats.add("yyyyMMdd'T'HH");

        List<String> fullBasicFormatsWithoutT = new ArrayList<String>();
        fullBasicFormatsWithoutT.add("yyyyMMdd HHmmss");
        fullBasicFormatsWithoutT.add("yyyyMMdd HHmm");
        fullBasicFormatsWithoutT.add("yyyyMMdd HH");

        String baseDate = europeanDates ? "dd/MM/yyyy" : "MM/dd/yyyy";

        // 使用空格而不是'T'分隔日期和时间
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

        // 这里的顺序很重要，因为如果首先匹配到错误的格式，会误解时间
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
        // 大写Z => RFC822时区
        basePatterns.forEach(p -> timeZones.add(p + "Z"));
        basePatterns.forEach(p -> timeZones.add(p + " Z"));

        // 大写X => ISO8601时区
        basePatterns.forEach(p -> timeZones.add(p + "XXX"));
        basePatterns.forEach(p -> timeZones.add(p + " XXX"));

        basePatterns.forEach(p -> timeZones.add(p + "'Z'Z"));
        basePatterns.forEach(p -> timeZones.add(p + "'z'Z"));
        basePatterns.forEach(p -> timeZones.add(p + "z"));

        basePatterns.addAll(timeZones);

        return basePatterns;
    }

    /**
     * 将对象转换为日期类型（仅日期，不含时间）
     *
     * @param value         待转换的对象
     * @param timeZone      时区
     * @param useEuroDates  是否使用欧洲日期格式
     * @return 转换后的Calendar值
     */
    public static Calendar convertDateOnly(Object value, TimeZone timeZone, boolean useEuroDates) {
        return convertDateTime(value, timeZone, useEuroDates);
    }
}
