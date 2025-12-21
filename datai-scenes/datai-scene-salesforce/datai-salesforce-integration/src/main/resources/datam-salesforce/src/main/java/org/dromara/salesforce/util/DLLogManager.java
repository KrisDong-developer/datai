package org.dromara.salesforce.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DLLogManager {
    public static <T> Logger getLogger(Class<T> clazz) {
        return LogManager.getLogger(clazz);
    }
}
