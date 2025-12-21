package org.dromara.salesforce.domain.vo;

import org.dromara.salesforce.domain.MigrationField;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 对象字段信息视图对象 migration_field
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MigrationField.class)
public class MigrationFieldVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ExcelProperty(value = "主键ID")
    private Integer id;

    /**
     * 对象API
     */
    @ExcelProperty(value = "对象API")
    private String api;

    /**
     * 字段API
     */
    @ExcelProperty(value = "字段API")
    private String field;

    /**
     * 字段标签
     */
    @ExcelProperty(value = "字段标签")
    private String label;

    /**
     * 是否可编辑（目标库字段）
     */
    @ExcelProperty(value = "是否可编辑", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "目=标库字段")
    private Boolean isCreateable;

    /**
     * 是否为空（目标库字段）
     */
    @ExcelProperty(value = "是否为空", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "目=标库字段")
    private Boolean isNillable;

    /**
     * 是否可更新
     */
    @ExcelProperty(value = "是否可更新")
    private Boolean isUpdateable;

    /**
     * 是否默认值
     */
    @ExcelProperty(value = "是否默认值")
    private Boolean isDefaultedOnCreate;

    /**
     * 是否唯一
     */
    @ExcelProperty(value = "是否唯一")
    private Boolean isUnique;

    /**
     * 是否可过滤
     */
    @ExcelProperty(value = "是否可过滤")
    private Boolean isFilterable;

    /**
     * 是否可排序
     */
    @ExcelProperty(value = "是否可排序")
    private Boolean isSortable;

    /**
     * 是否可聚合
     */
    @ExcelProperty(value = "是否可聚合")
    private Boolean isAggregatable;

    /**
     * 是否可分组
     */
    @ExcelProperty(value = "是否可分组")
    private Boolean isGroupable;

    /**
     * 是否多态外键
     */
    @ExcelProperty(value = "是否多态外键")
    private Boolean isPolymorphicForeignKey;

    /**
     * 多态映射字段
     */
    @ExcelProperty(value = "多态映射字段")
    private String polymorphicForeignField;

    /**
     * 是否外部ID
     */
    @ExcelProperty(value = "是否外部ID")
    private Boolean isExternalId;

    /**
     * 是否自定义字段
     */
    @ExcelProperty(value = "是否自定义字段")
    private Boolean isCustom;

    /**
     * 是否计算字段
     */
    @ExcelProperty(value = "是否计算字段")
    private Boolean isCalculated;

    /**
     * 是否自动编号字段
     */
    @ExcelProperty(value = "是否自动编号字段")
    private Boolean isAutoNumber;

    /**
     * 是否区分大小写
     */
    @ExcelProperty(value = "是否区分大小写")
    private Boolean isCaseSensitive;

    /**
     * 是否加密字段
     */
    @ExcelProperty(value = "是否加密字段")
    private Boolean isEncrypted;

    /**
     * 是否HTML格式字段
     */
    @ExcelProperty(value = "是否HTML格式字段")
    private Boolean isHtmlFormatted;

    /**
     * 是否可通过ID查找
     */
    @ExcelProperty(value = "是否可通过ID查找")
    private Boolean isIdLookup;

    /**
     * 是否可设置权限
     */
    @ExcelProperty(value = "是否可设置权限")
    private Boolean isPermissionable;

    /**
     * 是否限制选择列表
     */
    @ExcelProperty(value = "是否限制选择列表")
    private Boolean isRestrictedPicklist;

    /**
     * 是否限制删除
     */
    @ExcelProperty(value = "是否限制删除")
    private Boolean isRestrictedDelete;

    /**
     * 写入时是否需要主读
     */
    @ExcelProperty(value = "写入时是否需要主读")
    private Boolean isWriteRequiresMasterRead;

    /**
     * 字段数据类型
     */
    @ExcelProperty(value = "字段数据类型")
    private String fieldDataType;

    /**
     * 字段长度
     */
    @ExcelProperty(value = "字段长度")
    private Integer fieldLength;

    /**
     * 数字字段的精度
     */
    @ExcelProperty(value = "数字字段的精度")
    private Integer fieldPrecision;

    /**
     * 数字字段的小数位数
     */
    @ExcelProperty(value = "数字字段的小数位数")
    private Integer fieldScale;

    /**
     * 字段的字节长度
     */
    @ExcelProperty(value = "字段的字节长度")
    private Integer fieldByteLength;

    /**
     * 默认值
     */
    @ExcelProperty(value = "默认值")
    private String defaultValue;

    /**
     * 计算字段的公式
     */
    @ExcelProperty(value = "计算字段的公式")
    private String calculatedFormula;

    /**
     * 内联帮助文本
     */
    @ExcelProperty(value = "内联帮助文本")
    private String inlineHelpText;

    /**
     * 掩码
     */
    @ExcelProperty(value = "掩码")
    private String mask;

    /**
     * 掩码类型
     */
    @ExcelProperty(value = "掩码类型")
    private String maskType;

    /**
     * 选择列表值
     */
    @ExcelProperty(value = "选择列表值")
    private String picklistValues;

    /**
     * 关系名称
     */
    @ExcelProperty(value = "关系名称")
    private String relationshipName;

    /**
     * 关系顺序
     */
    @ExcelProperty(value = "关系顺序")
    private Integer relationshipOrder;

    /**
     * 引用目标字段
     */
    @ExcelProperty(value = "引用目标字段")
    private String referenceTargetField;

    /**
     * 是否可以通过距离查询
     */
    @ExcelProperty(value = "是否可以通过距离查询")
    private Boolean queryByDistance;

    /**
     * 是否可以预过滤搜索
     */
    @ExcelProperty(value = "是否可以预过滤搜索")
    private Boolean searchPrefilterable;

    /**
     * SOAP类型
     */
    @ExcelProperty(value = "SOAP类型")
    private String soapType;

    /**
     * 是否可更新
     */
    @ExcelProperty(value = "是否可更新")
    private Boolean updateable;

    /**
     * 写入时是否需要主读
     */
    @ExcelProperty(value = "写入时是否需要主读")
    private Boolean writeRequiresMasterRead;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
