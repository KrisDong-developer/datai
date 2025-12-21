package org.dromara.salesforce.domain.bo;

import org.dromara.salesforce.domain.MigrationAddress;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 地址信息业务对象 migration_address
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MigrationAddress.class, reverseConvertGenerate = false)
public class MigrationAddressBo extends BaseEntity {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = { EditGroup.class })
    private Integer id;

    /**
     * 对象API
     */
    @NotBlank(message = "对象API不能为空", groups = { AddGroup.class, EditGroup.class })
    private String api;

    /**
     * 字段API
     */
    @NotBlank(message = "字段API不能为空", groups = { AddGroup.class, EditGroup.class })
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
