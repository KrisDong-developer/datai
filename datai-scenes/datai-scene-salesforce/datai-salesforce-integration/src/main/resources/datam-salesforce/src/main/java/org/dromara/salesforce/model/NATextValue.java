
package org.dromara.salesforce.model;

/**
 * NATextValue 类表示文本字段的空值（Not Available）。
 *
 * 此类用于表示 Salesforce 中文本字段的特殊空值状态。
 * 它使用单例模式确保在整个应用程序中只有一个实例，并以 "#N/A" 字符串表示空值状态。
 *
 * 主要功能：
 * 1. 作为文本字段的特殊空值表示
 * 2. 提供单例实例访问
 * 3. 提供检查对象是否为 NA 值的静态方法
 * 4. 重写 equals、hashCode 和 toString 方法以正确处理空值比较和显示
 *
 * 使用场景：
 * - 当文本字段没有有效值时使用此类表示
 * - 在数据转换和处理过程中标识无效或缺失的文本数据
 * - 与其他 NA 值类一起使用，提供统一的空值处理机制
 */
public class NATextValue {

    /**
     * 单例实例
     */
    private static final NATextValue INSTANCE = new NATextValue();

    /**
     * 空值的字符串表示
     */
    private static final String NA_VALUE = "#N/A";

    /**
     * 私有构造函数，防止外部实例化
     */
    private NATextValue() {
    }

    /**
     * 获取单例实例
     *
     * @return NATextValue 的单例实例
     */
    public static NATextValue getInstance() {
        return INSTANCE;
    }

    /**
     * 检查对象是否为 NA 值
     *
     * @param obj 要检查的对象
     * @return 如果对象是 NA 值返回 true，否则返回 false
     */
    public static boolean isNA(Object obj) {
        return INSTANCE.equals(obj);
    }

    /**
     * 比较对象是否相等
     *
     * @param obj 要比较的对象
     * @return 如果对象相等返回 true，否则返回 false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        return NA_VALUE.equals(obj.toString());
    }

    /**
     * 获取对象的哈希码
     *
     * @return NA_VALUE 字符串的哈希码
     */
    @Override
    public int hashCode() {
        return NA_VALUE.hashCode();
    }

    /**
     * 返回对象的字符串表示
     *
     * @return "#N/A" 字符串
     */
    @Override
    public String toString() {
        return NA_VALUE;
    }
}
