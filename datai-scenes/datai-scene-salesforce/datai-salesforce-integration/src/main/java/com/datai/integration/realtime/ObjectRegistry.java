package com.datai.integration.realtime;

import com.datai.integration.model.domain.DataiIntegrationObject;
import java.util.List;
import java.util.Set;

public interface ObjectRegistry {
    
    void registerObject(DataiIntegrationObject object);
    
    void unregisterObject(String objectApi);
    
    List<DataiIntegrationObject> getRealtimeSyncObjects();
    
    boolean isObjectRegistered(String objectApi);
    
    void refreshRegistry();

    DataiIntegrationObject getObject(String objectApi);

    Set<String> getRegisteredObjects();

    int getRegisteredObjectCount();
}
