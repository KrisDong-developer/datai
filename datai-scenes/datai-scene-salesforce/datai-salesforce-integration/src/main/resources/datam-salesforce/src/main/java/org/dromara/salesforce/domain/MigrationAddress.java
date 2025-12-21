package org.dromara.salesforce.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 地址信息对象 migration_address
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("migration_address")
public class MigrationAddress extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Integer id;

    /**
     * 对象API
     */
    private String api;

    /**
     * 字段API
     */
    private String field;

    /**
     * 街道
     */
    private String street;

    /**
     * 城市
     */
    private String city;

    /**
     * 州/省
     */
    private String state;

    /**
     * 州代码
     */
    private String stateCode;

    /**
     * 邮政编码
     */
    private String postalCode;

    /**
     * 国家
     */
    private String country;

    /**
     * 国家代码
     */
    private String countryCode;

    /**
     * 地理编码精度
     */
    private String geocodeAccuracy;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 备注
     */
    private String remark;


}
