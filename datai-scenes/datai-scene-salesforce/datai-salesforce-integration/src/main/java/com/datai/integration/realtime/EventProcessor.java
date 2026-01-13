package com.datai.integration.realtime;

import com.salesforce.multicloudj.pubsub.driver.Message;

public interface EventProcessor {
    
    void processMessage(Message message);

    void processMessageBatch(Message[] messages);
}
