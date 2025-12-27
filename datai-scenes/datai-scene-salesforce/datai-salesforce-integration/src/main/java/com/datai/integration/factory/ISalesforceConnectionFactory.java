package com.datai.integration.factory;

/**
 * Salesforce连接工厂接口
 * 定义所有Salesforce连接工厂的统一接口
 *
 * @param <T> 连接类型
 * @author Salesforce
 */
public interface ISalesforceConnectionFactory<T> {

    /**
     * 获取连接实例
     * 如果缓存中没有有效的连接，则创建新的连接
     *
     * @return 连接实例
     */
    T getConnection();

    /**
     * 清除缓存的连接
     * 从缓存中移除连接实例
     */
    void clearConnection();

    /**
     * 获取连接类型名称
     *
     * @return 连接类型名称
     */
    String getConnectionType();
}
