package com.datai.integration.task;

import com.alibaba.fastjson2.JSON;
import com.datai.integration.model.param.DataiSyncParam;
import com.datai.salesforce.common.utils.ApiListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.datai.integration.service.ISalesforceDataPullService;

import java.util.List;

@Component("sfSyncTask")
public class SalesforceSyncTask {
    
    @Autowired
    private ISalesforceDataPullService salesforceDataPullService;
    
    public void syncAllData(String paramStr) {
        DataiSyncParam dataiSyncParam = new DataiSyncParam();

        if (StringUtils.isNotBlank(paramStr)) {
            dataiSyncParam = JSON.parseObject(paramStr, DataiSyncParam.class);
        }

        List<String> strings = ApiListUtils.splitApiList(dataiSyncParam.getApi());

        salesforceDataPullService.syncObjectsData(strings);
    }


    public void syncByObjectStructures(String paramStr) {

        DataiSyncParam dataiSyncParam = new DataiSyncParam();

        if (StringUtils.isNotBlank(paramStr)) {
            dataiSyncParam = JSON.parseObject(paramStr, DataiSyncParam.class);
        }

        List<String> strings = ApiListUtils.splitApiList(dataiSyncParam.getApi());

        salesforceDataPullService.syncObjectStructures(strings);
    }
}