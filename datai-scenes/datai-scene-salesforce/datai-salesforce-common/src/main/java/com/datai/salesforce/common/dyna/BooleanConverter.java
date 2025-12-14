package com.datai.salesforce.common.dyna;


import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

/**
 * BooleanConverter 类用于将字符串转换为布尔值。
 *
 * 此类实现了 Apache Commons Beanutils 的 Converter 接口，专门用于处理字符串到布尔值的转换。
 * 支持多种表示布尔值的字符串格式，包括：
 * - true 值： "yes", "y", "true", "on", "1"
 * - false 值： "no", "n", "false", "off", "0"
 *
 * 主要功能：
 * 1. 将字符串转换为对应的布尔值
 * 2. 处理大小写不敏感的布尔值表示
 * 3. 处理空值和空白字符
 * 4. 对于无法识别的字符串，抛出 ConversionException 异常

 */

public final class BooleanConverter implements Converter {


    // ----------------------------------------------------------- Constructors

    /**
     * 构造函数，创建一个新的 BooleanConverter 实例
     */
    public BooleanConverter() {

    }

    // --------------------------------------------------------- Public Methods


    /**
     * 将指定的输入对象转换为指定类型的输出对象（布尔值）。
     *
     * 支持的转换规则：
     * 1. 如果输入值为 null 或已经是 Boolean 类型，则直接返回
     * 2. 将输入值转换为字符串并去除首尾空白字符
     * 3. 如果字符串为空，则返回 null
     * 4. 根据预定义的布尔值映射规则进行转换：
     *    - true 值包括："yes", "y", "true", "on", "1"（不区分大小写）
     *    - false 值包括："no", "n", "false", "off", "0"（不区分大小写）
     * 5. 如果字符串不匹配任何已知的布尔值表示，抛出 ConversionException 异常
     *
     * @param type 数据类型，表示该值应该被转换成的目标类型
     * @param value 输入值，需要被转换的对象
     * @return 转换后的布尔值，如果输入为空则返回 null
     * @exception ConversionException 如果无法成功执行转换
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object convert(Class type, Object value) {

        if (value == null || value instanceof Boolean) {
            return value;
        }

        String stringValue = value.toString().trim();
        if (stringValue.length()==0) {
            return null;
        }

        try {
            if (stringValue.equalsIgnoreCase("yes") ||
                    stringValue.equalsIgnoreCase("y") ||
                    stringValue.equalsIgnoreCase("true") ||
                    stringValue.equalsIgnoreCase("on") ||
                    stringValue.equalsIgnoreCase("1")) {
                return (Boolean.TRUE);
            } else if (stringValue.equalsIgnoreCase("no") ||
                    stringValue.equalsIgnoreCase("n") ||
                    stringValue.equalsIgnoreCase("false") ||
                    stringValue.equalsIgnoreCase("off") ||
                    stringValue.equalsIgnoreCase("0")) {
                return (Boolean.FALSE);
            } else {
                throw new ConversionException(stringValue);
            }
        } catch (ClassCastException e) {
            throw new ConversionException(e);
        }

    }


}
