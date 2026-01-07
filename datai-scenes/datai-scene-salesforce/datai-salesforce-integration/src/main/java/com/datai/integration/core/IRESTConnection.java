package com.datai.integration.core;

import com.sforce.soap.partner.SaveResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import org.apache.commons.beanutils.DynaBean;

import java.util.List;

public interface IRESTConnection {

    SaveResult[] loadAction(String sessionId, ACTION_ENUM action, List<DynaBean> dynabeans) throws ConnectionException;

    void setApiVersion(String version);

    enum ACTION_ENUM {
        INSERT, UPDATE, DELETE, UPSERT
    }
}