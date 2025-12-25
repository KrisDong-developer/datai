package com.datai.integration.mapper;


import com.datai.salesforce.common.param.SalesforceParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface SalesforceMapper {


    public void stockList(@Param("param") SalesforceParam param);

    public void incrementList(@Param("param") SalesforceParam param);

    /**
     * sf sql 统计数量
     * @param param 参数
     */
    public void stockCount(@Param("param") SalesforceParam param);

    public void incrementCount(@Param("param") SalesforceParam param);
    /**
     * sf sql
     * @param param 参数
     */
    public void listOrderById(@Param("param") SalesforceParam param);

    public void listOrderByIdNew(@Param("param") SalesforceParam param);


}
