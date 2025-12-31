package com.datai.integration.model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;
import com.datai.common.annotation.Excel;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.datai.integration.model.domain.DataiIntegrationField;
/**
 * 对象字段信息Vo对象 datai_integration_field
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiIntegrationFieldVo implements Serializable {
    private static final long serialVersionUID = 1L;

        /** 主键ID */
    private Integer id;

        /** 部门ID */
    private Long deptId;

        /** 所属对象API */
    private String api;

        /** 字段API */
    private String field;

        /** 字段标签 */
    private String label;

        /** 是否可创建 */
    private Boolean isCreateable;

        /** 是否为空 */
    private Boolean isNillable;

        /** 是否可更新 */
    private Boolean isUpdateable;

        /** 是否默认值 */
    private Boolean isDefaultedOnCreate;

        /** 是否唯一 */
    private Boolean isUnique;

        /** 是否可过滤 */
    private Boolean isFilterable;

        /** 是否可排序 */
    private Boolean isSortable;

        /** 是否可聚合 */
    private Boolean isAggregatable;

        /** 是否可分组 */
    private Boolean isGroupable;

        /** 是否多态外键 */
    private Boolean isPolymorphicForeignKey;

        /** 是否外部ID */
    private Boolean isExternalId;

        /** 是否自定义字段 */
    private Boolean isCustom;

        /** 是否计算字段 */
    private Boolean isCalculated;

        /** 是否自动编号字段 */
    private Boolean isAutoNumber;

        /** 是否区分大小写 */
    private Boolean isCaseSensitive;

        /** 是否加密字段 */
    private Boolean isEncrypted;

        /** 是否HTML格式字段 */
    private Boolean isHtmlFormatted;

        /** 是否可通过ID查找 */
    private Boolean isIdLookup;

        /** 是否可设置权限 */
    private Boolean isPermissionable;

        /** 是否限制选择列表 */
    private Boolean isRestrictedPicklist;

        /** 是否限制删除 */
    private Boolean isRestrictedDelete;

        /** 写入时是否需要主读 */
    private Boolean isWriteRequiresMasterRead;

        /** 是否可以通过距离查询 */
    private Boolean queryByDistance;

        /** 是否可以预过滤搜索 */
    private Boolean searchPrefilterable;

        /** 字段数据类型 */
    private String fieldDataType;

        /** 字段长度 */
    private Integer fieldLength;

        /** 数字字段的精度 */
    private Integer fieldPrecision;

        /** 数字字段的小数位数 */
    private Integer fieldScale;

        /** 字段的字节长度 */
    private Integer fieldByteLength;

        /** SOAP类型 */
    private String soapType;

        /** 关系名称 */
    private String relationshipName;

        /** 引用目标字段 */
    private String referenceTargetField;

        /** 引用目标对象的API名称列表（逗号分隔） */
    private String referenceTo;

        /** 多态映射字段 */
    private String polymorphicForeignField;

        /** 关系顺序 */
    private Integer relationshipOrder;

        /** 默认值 */
    private String defaultValue;

        /** 计算字段的公式 */
    private String calculatedFormula;

        /** 内联帮助文本 */
    private String inlineHelpText;

        /** 掩码 */
    private String mask;

        /** 掩码类型 */
    private String maskType;

        /** 选择列表值 (JSON或其他格式) */
    private String picklistValues;

        /** 备注 */
    private String remark;

        /** 创建者 */
    private String createBy;

        /** 创建时间 */
    private Date createTime;

        /** 更新者 */
    private String updateBy;

        /** 更新时间 */
    private Date updateTime;


    /**
     * 对象转封装类
     *
     * @param dataiIntegrationField DataiIntegrationField实体对象
     * @return DataiIntegrationFieldVo
     */
    public static DataiIntegrationFieldVo objToVo(DataiIntegrationField dataiIntegrationField) {
        if (dataiIntegrationField == null) {
            return null;
        }
            DataiIntegrationFieldVo dataiIntegrationFieldVo = new DataiIntegrationFieldVo();
        BeanUtils.copyProperties(dataiIntegrationField, dataiIntegrationFieldVo);
        return dataiIntegrationFieldVo;
    }
}
