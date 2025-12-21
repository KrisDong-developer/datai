package org.dromara.salesforce.model;

import java.util.*;

/**
 * 表格行类，表示表格中的一行数据
 *
 * TableRow是Row的另一种实现，与Row不同的是，TableRow使用TableHeader来定义列的结构，
 * 并使用数组来存储数据值，这种方式在某些场景下可以提供更好的性能。
 * TableRow与TableHeader配合使用，TableHeader定义了列的结构，TableRow存储具体的数据。
 *
 * 主要特点：
 * 1. 使用数组存储数据值，提高访问效率
 * 2. 通过TableHeader管理列结构和列位置映射
 * 3. 实现了Map<String, Object>和RowInterface接口
 * 4. 提供了行数据的基本操作方法
 */
public class TableRow implements Map<String, Object>, RowInterface {
    // 表头信息，定义了列的结构
    private TableHeader header;
    // 数据值数组，按列位置存储数据
    private Object[] cellValues;

    /**
     * 构造函数，根据给定的表头创建TableRow
     *
     * @param header 表头信息
     */
    public TableRow(TableHeader header) {
        this.header = header;
        cellValues = new Object[header.getColumns().size()];
    }

    /**
     * 复制构造函数，根据给定的TableRow创建新的TableRow
     *
     * @param rowToCopy 要复制的TableRow
     */
    public TableRow(TableRow rowToCopy) {
        this.header = rowToCopy.getHeader();
        cellValues = Arrays.copyOf(rowToCopy.cellValues, rowToCopy.cellValues.length);
    }

    /**
     * 根据键（列名）获取值
     *
     * @param key 键（列名）
     * @return 与该键关联的值，如果没有则返回null
     */
    public Object get(Object key) {
        Integer colPos = this.header.getColumnPosition((String)key);
        if (colPos == null) {
            return null;
        }
        return cellValues[colPos];
    }

    /**
     * 在指定的键（列名）下添加值
     *
     * @param key 键（列名）
     * @param value 要存储的值
     * @return 之前与该键关联的值，如果没有则返回null
     */
    public Object put(String key, Object value) {
        Integer colPos = this.header.getColumnPosition(key);
        if (colPos == null) {
            return null;
        }
        return this.cellValues[colPos] = value;
    }

    /**
     * 获取表头信息
     *
     * @return 表头信息
     */
    public TableHeader getHeader() {
        return this.header;
    }

    /**
     * 添加新的列头
     *
     * @param columnName 列名
     */
    public void addHeaderColumn(String columnName) {
        this.header.addColumn(columnName);
        this.cellValues = Arrays.copyOf(this.cellValues, this.header.getColumns().size());
    }

    /**
     * 创建一个空的TableRow
     *
     * @return 空的TableRow
     */
    public static TableRow emptyRow() {
        return new TableRow(new TableHeader(new ArrayList<String>()));
    }

    /**
     * 创建一个只有一个键值对的TableRow
     *
     * @param key 键（列名）
     * @param value 值
     * @return 包含指定键值对的TableRow
     */
    public static TableRow singleEntryImmutableRow(String key, Object value) {
        ArrayList<String> headers = new ArrayList<String>();
        headers.add(key);
        TableHeader tableHeader = new TableHeader(headers);
        TableRow row = new TableRow(tableHeader);
        row.put(key, value);
        return row;
    }

    /**
     * 获取非空单元格的数量
     *
     * @return 非空单元格的数量
     */
    public int getNonEmptyCellsCount() {
        int numNonEmptyCells = 0;
        for (int i = 0; i < this.cellValues.length; i++) {
            if (cellValues[i] != null) {
                numNonEmptyCells++;
            }
        }
        return numNonEmptyCells;
    }

    @Override
    public List<String> getColumnNames() {
        return new ArrayList<String>(this.header.getColumns());
    }

    @Override
    public int size() {
        return this.header.getColumns().size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.header.getColumns().contains(key);
    }

    @Override
    public boolean containsValue(Object value) {
        for (Object val : this.cellValues) {
            if (val != null && val.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object remove(Object key) {
        Integer colPos = this.header.getColumnPosition((String)key);
        if (colPos == null) {
            return null;
        }
        Object value = this.get(key);
        this.header.removeColumn((String)key);
        return value;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        // TODO Auto-generated method stub
    }

    @Override
    public void clear() {
        ArrayList<String> headerNames = new ArrayList<String>();
        this.header = new TableHeader(headerNames);
    }

    @Override
    public Set<String> keySet() {
        return new HashSet<String>(this.header.getColumns());
    }

    @Override
    public Collection<Object> values() {
        return Arrays.asList(this.cellValues);
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        HashSet<Entry<String, Object>> rowEntrySet = new HashSet<Entry<String, Object>>();
        for (String colName : this.header.getColumns()) {
            Entry<String, Object> colEntry = new AbstractMap.SimpleEntry<String, Object>(colName, get(colName));
            rowEntrySet.add(colEntry);
        }
        return rowEntrySet;
    }
}
