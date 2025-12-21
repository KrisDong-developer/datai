package org.dromara.salesforce.model;

import java.util.*;

/**
 * 数据行类，表示来自CSV文件、数据库或其他数据源的一行数据
 *
 * Row本质上是一组列名和列值的集合，可以来自CSV文件、数据库或其他任何数据源。
 * Row是一个抽象类，用于替代使用Map的方式，提供更面向对象的方法。
 * 目前它实现了Map接口以简化初始重构工作，但未来应该朝向更具体的方法发展，
 * 并可能停止实现Map接口。所有Row的行为都应该移到这个类中，而不是分散在多个类中。
 */
public class Row implements Map<String, Object>, RowInterface {
    // 内部存储数据的Map，使用TreeMap并按列名不区分大小写排序
    private final Map<String, Object> internalMap;
    // 键映射，用于处理列名的大小写不敏感匹配
    private final Map<String, String> keyMap = new HashMap<String, String>();

    /**
     * 构造函数，创建一个新的Row实例
     * 使用TreeMap并设置不区分大小写的排序规则
     */
    public Row() {
        this.internalMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    @Override
    public int size() {
        return internalMap.size();
    }

    @Override
    public boolean isEmpty() {
        return internalMap.isEmpty();
    }

    /**
     * 检查是否包含指定的键（列名）
     * @param key 要检查的键（列名）
     * @return 如果包含该键返回true，否则返回false
     */
    @Override
    public boolean containsKey(Object key) {
        String realKey = this.keyMap.get(((String)key).toLowerCase());
        if (realKey == null) {
            return false;
        }
        return internalMap.containsKey(realKey);
    }

    @Override
    public boolean containsValue(Object value) {
        return internalMap.containsValue(value);
    }

    /**
     * 根据键（列名）获取值
     * @param key 键（列名）
     * @return 对应的值，如果不存在返回null
     */
    @Override
    public Object get(Object key) {
        String realKey = this.keyMap.get(((String)key).toLowerCase());
        if (realKey == null) {
            return null;
        }
        return internalMap.get(realKey);
    }

    /**
     * 添加键值对（列名和值）
     * @param key 键（列名）
     * @param value 值
     * @return 之前的值，如果没有则返回null
     */
    @Override
    public Object put(String key, Object value) {
        this.keyMap.put(key.toLowerCase(), key);
        return internalMap.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        this.keyMap.remove(((String)key).toLowerCase());
        return internalMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        for (String key : m.keySet()) {
            this.keyMap.put(key.toLowerCase(), key);
        }
        internalMap.putAll(m);
    }

    @Override
    public void clear() {
        this.keyMap.clear();
        internalMap.clear();
    }

    @Override
    public Set<String> keySet() {
        return internalMap.keySet();
    }

    @Override
    public Collection<Object> values() {
        return internalMap.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return internalMap.entrySet();
    }

    @Override
    public String toString() {
        return "Row{" +
                " size=" + internalMap.size() +
                " columns=" + internalMap +
                '}';
    }

    /**
     * 将当前Row转换为TableRow对象
     * @param header 表头信息
     * @return 转换后的TableRow对象
     */
    public TableRow convertToTableRow(TableHeader header) {
        TableRow trow = new TableRow(header);
        for (String headerColName : header.getColumns()) {
            trow.put(headerColName, this.get(headerColName));
        }
        return trow;
    }

    @Override
    public List<String> getColumnNames() {
        Set<String> fieldNameSet = this.keySet();
        return new ArrayList<String>(fieldNameSet);
    }
}
