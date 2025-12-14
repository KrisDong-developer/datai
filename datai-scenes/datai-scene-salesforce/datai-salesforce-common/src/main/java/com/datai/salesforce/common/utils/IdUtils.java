package com.datai.salesforce.common.utils;

import com.datai.salesforce.common.converter.SalesforceDataTypeConverter;

/**
 * Salesforce ID工具类
 */
public class IdUtils {
    private static final SalesforceDataTypeConverter dataTypeConverter = new SalesforceDataTypeConverter();
    private static final int ID_LENGTH_15 = 15;
    private static final int ID_LENGTH_18 = 18;

    /**
     * 验证Salesforce ID是否有效
     *
     * @param salesforceId Salesforce ID
     * @return 是否有效
     */
    public static boolean isValid(String salesforceId) {
        if (salesforceId == null) {
            return false;
        }
        int length = salesforceId.length();
        if (length != ID_LENGTH_15 && length != ID_LENGTH_18) {
            return false;
        }

        // 检查格式是否为字母数字
        return salesforceId.matches("^[a-zA-Z0-9]+$");
    }

    /**
     * 获取Salesforce ID的对象类型
     *
     * @param salesforceId Salesforce ID
     * @return 对象类型
     */
    public static String getObjectType(String salesforceId) {
        if (!isValid(salesforceId)) {
            throw new IllegalArgumentException("无效的Salesforce ID: " + salesforceId);
        }

        // 获取ID的前3位，代表对象类型
        return salesforceId.substring(0, 3);
    }

    /**
     * 将15位Salesforce ID转换为18位
     *
     * @param id15 15位Salesforce ID
     * @return 18位Salesforce ID
     */
    public static String to18CharId(String id15) {
        return dataTypeConverter.to18CharId(id15);
    }

    /**
     * 将18位Salesforce ID转换为15位
     *
     * @param id18 18位Salesforce ID
     * @return 15位Salesforce ID
     */
    public static String to15CharId(String id18) {
        return dataTypeConverter.to15CharId(id18);
    }

    /**
     * 比较两个Salesforce ID是否相等，忽略大小写和长度差异
     *
     * @param id1 第一个Salesforce ID
     * @param id2 第二个Salesforce ID
     * @return 是否相等
     */
    public static boolean equals(String id1, String id2) {
        if (id1 == null && id2 == null) {
            return true;
        }
        if (id1 == null || id2 == null) {
            return false;
        }

        // 转换为18位ID后比较
        String normalizedId1 = to18CharId(id1);
        String normalizedId2 = to18CharId(id2);
        return normalizedId1.equals(normalizedId2);
    }
}