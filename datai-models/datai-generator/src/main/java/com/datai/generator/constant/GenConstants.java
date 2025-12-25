package com.datai.generator.constant;

/**
 * 代码生成通用常量
 * 
 * @author datai
 */
public class GenConstants
{
    /** 单表（增删改查） */
    public static final String TPL_CRUD = "crud";

    /** 树表（增删改查） */
    public static final String TPL_TREE = "tree";

    /** 主子表（增删改查） */
    public static final String TPL_SUB = "sub";

    /** 树编码字段 */
    public static final String TREE_CODE = "treeCode";

    /** 树父编码字段 */
    public static final String TREE_PARENT_CODE = "treeParentCode";

    /** 树名称字段 */
    public static final String TREE_NAME = "treeName";

    /** 上级菜单ID字段 */
    public static final String PARENT_MENU_ID = "parentMenuId";

    /** 上级菜单名称字段 */
    public static final String PARENT_MENU_NAME = "parentMenuName";

    /** 数据库字符串类型 */
    public static final String[] COLUMNTYPE_STR = { "char", "varchar", "nvarchar", "varchar2" };

    /** 数据库文本类型 */
    public static final String[] COLUMNTYPE_TEXT = { "tinytext", "text", "mediumtext", "longtext" };

    /** 数据库日期时间类型 */
    public static final String[] COLUMNTYPE_DATETIME = { "datetime", "timestamp" };

    /** 数据库时间类型 */
    public static final String[] COLUMNTYPE_TIME = { "time" };

    /** 数据库日期类型 */
    public static final String[] COLUMNTYPE_DATE = { "date" };

    /**
     * 映射为 Java Integer 的数据库类型
     * (通常为 32 位及以下的整数)
     */
    public static final String[] COLUMNTYPE_INT = {
            "int", "integer", "mediumint", "smallint", "int2", "int4"
    };

    /**
     * 映射为 Java Long 的数据库类型
     * (通常为 64 位整数)
     */
    public static final String[] COLUMNTYPE_LONG = {
            "bigint", "int8", "bigserial"
    };

    /** * 数据库浮点数类型 (Approximate Numeric Types)
     * 包含：单精度、双精度浮点数
     */
    public static final String[] COLUMNTYPE_FLOAT = {
            "float", "float4", "float8", "double", "real",
            "double precision", "binary_float", "binary_double"
    };

    /** * 数据库定点数/金额类型 (Exact Numeric Types)
     * 包含：高精度数值、金额，适用于财务计算
     */
    public static final String[] COLUMNTYPE_NUMBER = {
            "number", "decimal", "numeric", "money", "smallmoney"
    };

    /** * 数据库逻辑/位类型 (Boolean/Bit Types)
     * 包含：布尔值、位图类型
     */
    public static final String[] COLUMNTYPE_BOOLEAN = {
            "tinyint", "bit", "boolean", "bool"
    };

    /** 页面不需要插入字段 */
    public static final String[] COLUMNNAME_NOT_INSERT = { "id", "create_by", "create_time", "dept_id", "update_by", "update_time" };

    /** 页面不需要编辑字段 */
    public static final String[] COLUMNNAME_NOT_EDIT = { "id", "create_by", "create_time", "dept_id", "update_by", "update_time" };

    /** 页面不需要显示的列表字段 */
    public static final String[] COLUMNNAME_NOT_LIST = { "id", "create_by", "create_time", "dept_id", "update_by", "update_time" };

    /** 页面不需要显示的详情字段 */
    public static final String[] COLUMNNAME_NOT_DETAIL = { };

    /** 页面不需要查询字段 */
    public static final String[] COLUMNNAME_NOT_QUERY = { "id", "create_by", "create_time", "del_flag", "update_by",
            "update_time", "remark" };

    /** Entity基类字段 */
    public static final String[] BASE_ENTITY = { "createBy", "createTime", "updateBy", "updateTime", "remark" };

    /** Tree基类字段 */
    public static final String[] TREE_ENTITY = { "parentName", "parentId", "orderNum", "ancestors", "children" };

    /** 文本框 */
    public static final String HTML_INPUT = "input";

    /** 文本域 */
    public static final String HTML_TEXTAREA = "textarea";

    /** 下拉框 */
    public static final String HTML_SELECT = "select";

    /** 单选框 */
    public static final String HTML_RADIO = "radio";

    /** 复选框 */
    public static final String HTML_CHECKBOX = "checkbox";

    /** 日期控件 */
    public static final String HTML_DATE = "date";

    /** 时间控件 */
    public static final String HTML_TIME = "time";

    /** 日期时间控件 */
    public static final String HTML_DATETIME = "datetime";

    /** 图片上传控件 */
    public static final String HTML_IMAGE_UPLOAD = "imageUpload";

    /** 文件上传控件 */
    public static final String HTML_FILE_UPLOAD = "fileUpload";

    /** 富文本控件 */
    public static final String HTML_EDITOR = "editor";

    /** 字符串类型 */
    public static final String TYPE_STRING = "String";

    /** 整型 */
    public static final String TYPE_INTEGER = "Integer";

    /** 长整型 */
    public static final String TYPE_LONG = "Long";

    /** 浮点型 */
    public static final String TYPE_DOUBLE = "Double";

    /**
     * 布尔
     */
    public static final String TYPE_BOOLEAN= "Boolean";


    /** 高精度计算类型 */
    public static final String TYPE_BIGDECIMAL = "BigDecimal";

    /** 日期类型 */
    public static final String TYPE_DATE = "LocalDate";

    /** 时间类型 */
    public static final String TYPE_TIME = "LocalTime";

    /** 日期时间类型 */
    public static final String TYPE_DATETIME = "LocalDateTime";

    /** 模糊查询 */
    public static final String QUERY_LIKE = "LIKE";

    /** 相等查询 */
    public static final String QUERY_EQ = "EQ";

    /** 需要 */
    public static final String REQUIRE = "1";
}
