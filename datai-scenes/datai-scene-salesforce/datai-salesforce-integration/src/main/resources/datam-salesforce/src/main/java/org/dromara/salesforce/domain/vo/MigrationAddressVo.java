package org.dromara.salesforce.domain.vo;

import org.dromara.salesforce.domain.MigrationAddress;
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
 * 地址信息视图对象 migration_address
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MigrationAddress.class)
public class MigrationAddressVo implements Serializable {

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
     * 街道
     */
    @ExcelProperty(value = "街道")
    private String street;

    /**
     * 城市
     */
    @ExcelProperty(value = "城市")
    private String city;

    /**
     * 州/省
     */
    @ExcelProperty(value = "州/省")
    private String state;

    /**
     * 州代码
     */
    @ExcelProperty(value = "州代码")
    private String stateCode;

    /**
     * 邮政编码
     */
    @ExcelProperty(value = "邮政编码")
    private String postalCode;

    /**
     * 国家
     */
    @ExcelProperty(value = "国家")
    private String country;

    /**
     * 国家代码
     */
    @ExcelProperty(value = "国家代码")
    private String countryCode;

    /**
     * 地理编码精度
     */
    @ExcelProperty(value = "地理编码精度")
    private String geocodeAccuracy;

    /**
     * 纬度
     */
    @ExcelProperty(value = "纬度")
    private String latitude;

    /**
     * 经度
     */
    @ExcelProperty(value = "经度")
    private String longitude;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
