package com.datai.salesforce.common.exception;

/**
 * Salesforce Session ID登录异常类
 * 用于处理Salesforce Session ID登录过程中发生的各种异常
 */
public class SalesforceSessionIdLoginException extends SalesforceLoginException {
    private static final long serialVersionUID = 1L;

    public SalesforceSessionIdLoginException(String message) {
        super(message);
    }

    public SalesforceSessionIdLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public SalesforceSessionIdLoginException(String errorCode, String message) {
        super(errorCode, message);
    }

    public SalesforceSessionIdLoginException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}