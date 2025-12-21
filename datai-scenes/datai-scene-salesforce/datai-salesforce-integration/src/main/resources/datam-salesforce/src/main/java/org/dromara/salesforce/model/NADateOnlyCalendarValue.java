
package org.dromara.salesforce.model;


/**
 * NADateOnlyCalendarValue 类表示日期字段（不含时间）的空值（Not Available）。
 *
 * 此类继承自 DateOnlyCalendar，用于表示 Salesforce 中日期字段（不包含时间部分）的特殊空值状态。
 * 它使用单例模式确保在整个应用程序中只有一个实例，并以 "#N/A" 字符串表示空值状态。
 *
 * 主要功能：
 * 1. 作为日期字段（不含时间）的特殊空值表示
 * 2. 提供单例实例访问
 * 3. 重写 equals、hashCode 和 toString 方法以正确处理空值比较和显示
 *
 * 与 NACalendarValue 的区别：
 * - NACalendarValue 用于日期时间字段（包含日期和时间）
 * - NADateOnlyCalendarValue 用于仅日期字段（不包含时间部分）
 *
 * 使用场景：
 * - 当日期字段没有有效值时使用此类表示
 * - 在数据转换和处理过程中标识无效或缺失的日期数据
 * - 与 DataLoader 的日期处理组件配合使用，确保数据一致性
 */
@SuppressWarnings("serial")
public class NADateOnlyCalendarValue  extends DateOnlyCalendar {

    /**
     * 单例实例
     */
    private static final NADateOnlyCalendarValue INSTANCE = new NADateOnlyCalendarValue();

    /**
     * 空值的字符串表示
     */
    private static final String NA_VALUE = "#N/A";

    /**
     * 构造函数，调用父类构造函数
     */
    private NADateOnlyCalendarValue() {
        super();
    }

    /**
     * 获取单例实例
     *
     * @return NADateOnlyCalendarValue 的单例实例
     */
    public static NADateOnlyCalendarValue getInstance() {
        return INSTANCE;
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
