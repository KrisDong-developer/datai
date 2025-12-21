package org.dromara.salesforce.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TableHeader 类用于表示表格的列头信息。
 *
 * 此类负责管理表格列的名称和位置映射关系，提供列名到列位置的快速查找功能。
 * 在数据加载过程中，TableHeader 用于定义 TableRow 的结构，确保数据按正确的列位置存储和访问。
 *
 * 主要功能：
 * 1. 管理列名与列位置的映射关系
 * 2. 提供列位置的快速查找
 * 3. 维护列的顺序信息
 * 4. 支持动态添加和删除列
 *
 * 特点：
 * - 使用 HashMap 实现列名到位置的快速映射
 * - 列名比较不区分大小写
 * - 支持动态修改表结构
 * - 与 TableRow 配合使用，定义表格结构
 */
public class TableHeader {
    /**
     * 列名到列位置的映射表
     */
    private final Map<String, Integer> columnPositionMap = new HashMap<String, Integer>();

    /**
     * 最后一列的位置索引
     */
    private int lastColPosition = 0;

    /**
     * 列名列表，维护列的顺序
     */
    private List<String> columns;

    /**
     * 构造函数，根据给定的列名列表创建 TableHeader
     *
     * @param cols 列名列表
     */
    public TableHeader(List<String> cols) {
        this.columns = new ArrayList<String>(cols);
        for (String colName : cols) {
            if (colName == null) {
                continue;
            }
            columnPositionMap.put(colName.toLowerCase(), lastColPosition++);
        }
    }

    /**
     * 根据列名获取列位置
     *
     * @param columnName 列名
     * @return 列位置索引，如果列不存在则返回 null
     */
    public Integer getColumnPosition(String columnName) {
        return columnPositionMap.get(columnName.toLowerCase());
    }

    /**
     * 获取所有列名的副本
     *
     * @return 列名列表
     */
    public List<String> getColumns() {
        return new ArrayList<String>(columns);
    }

    /**
     * 添加新列
     *
     * @param colName 列名
     */
    public void addColumn(String colName) {
        columnPositionMap.put(colName.toLowerCase(), lastColPosition++);
        this.columns.add(colName);
    }

    /**
     * 删除列
     *
     * @param colName 列名
     */
    public void removeColumn(String colName) {
        this.columns.remove(colName);
    }
}
