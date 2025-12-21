package org.dromara.salesforce.model;

import java.util.List;

/**
 * 数据行接口，定义了数据行的基本操作
 *
 * RowInterface是所有数据行类需要实现的接口，提供了对数据行的基本操作方法，
 * 包括添加、获取数据以及获取列名列表等操作。该接口为不同的数据行实现提供了
 * 统一的操作规范，确保各种数据行实现具有一致的访问接口。
 */
public interface RowInterface {
    /**
     * 在指定的键（列名）下添加值
     *
     * @param key 键（列名）
     * @param value 要存储的值
     * @return 之前与该键关联的值，如果没有则返回null
     */
    public Object put(String key, Object value);

    /**
     * 根据键（列名）获取值
     *
     * @param key 键（列名）
     * @return 与该键关联的值，如果没有则返回null
     */
    public Object get(Object key);

    /**
     * 获取所有列名的列表
     *
     * @return 包含所有列名的列表
     */
    public List<String> getColumnNames();
}
