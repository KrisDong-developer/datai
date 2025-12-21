package org.dromara.salesforce.core;

import java.io.Serializable;

/**
 * Bulk V2 API错误信息类
 *
 * 用于封装Salesforce Bulk V2 API返回的错误信息
 */
public class BulkV2Error implements Serializable {
    private static final long serialVersionUID = 3L;

    private String errorCode = "";
    private String message = "";

    public BulkV2Error() {
    }

    public BulkV2Error(String errorCode, String message) {
        this.errorCode = errorCode != null ? errorCode : "";
        this.message = message != null ? message : "";
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode != null ? errorCode : "";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message != null ? message : "";
    }
}
