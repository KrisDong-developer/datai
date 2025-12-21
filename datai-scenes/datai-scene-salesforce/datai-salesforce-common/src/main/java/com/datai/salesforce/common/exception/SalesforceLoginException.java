package com.datai.salesforce.common.exception;

/**
 * Salesforce登录异常类
 * 用于处理Salesforce登录过程中发生的各种异常
 */
public class SalesforceLoginException extends SalesforceAuthException {
    private static final long serialVersionUID = 1L;

    public SalesforceLoginException(String message) {
        super(message);
    }

    public SalesforceLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public SalesforceLoginException(String errorCode, String message) {
        super(errorCode, message);
    }

    public SalesforceLoginException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}