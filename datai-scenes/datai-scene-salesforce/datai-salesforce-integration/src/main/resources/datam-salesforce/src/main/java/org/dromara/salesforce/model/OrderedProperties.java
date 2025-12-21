package org.dromara.salesforce.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 有序属性类，用于保持属性键值对的插入顺序
 *
 * OrderedProperties是标准Properties类的扩展，通过使用LinkedHashMap来保持属性的插入顺序。
 * 在数据加载器中主要用于需要保持配置项顺序的场景，如映射文件的读取和保存。
 *
 * 主要功能：
 * 1. 保持属性键值对的插入顺序
 * 2. 提供有序的属性访问接口
 * 3. 支持属性的增删改查操作
 *
 * 设计特点：
 * - 继承自Properties类，保持兼容性
 * - 使用LinkedHashMap维护插入顺序
 * - 重写关键方法以确保顺序性
 *
 */
public class OrderedProperties extends Properties {

    private static final long serialVersionUID = 1L;

    /**
     * 用于维护属性顺序的LinkedHashMap
     */
    LinkedHashMap<Object, Object> props;

    /**
     * 构造函数，创建一个新的OrderedProperties实例
     */
    public OrderedProperties() {
        super ();

        props = new LinkedHashMap<Object, Object>();
    }

    /**
     * 添加属性键值对
     *
     * @param key 键
     * @param value 值
     * @return 之前的值，如果没有则返回null
     */
    public Object put(Object key, Object value) {
        if (props.containsKey(key)) {
            props.remove(key);
        }

        if (value == null) {
            value = "";
        }

        props.put(key, value);

        return super.put(key, value);
    }

    /**
     * 获取属性条目集合
     *
     * @return 属性条目集合，保持插入顺序
     */
    public Set<Map.Entry<Object, Object>> entrySet() {
        return props.entrySet();
    }

    /**
     * 移除指定键的属性
     *
     * @param key 要移除的键
     * @return 与键关联的值，如果没有则返回null
     */
    public Object remove(Object key) {
        props.remove(key);

        return super .remove(key);
    }
}
