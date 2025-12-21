package com.datai.salesforce.common.exception;

/**
 * Salesforce CLI登录异常类
 * 用于处理Salesforce CLI登录过程中发生的各种异常
 */
public class SalesforceCliLoginException extends SalesforceLoginException {
    private static final long serialVersionUID = 1L;

    public SalesforceCliLoginException(String message) {
        super(message);
    }

    public SalesforceCliLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public SalesforceCliLoginException(String errorCode, String message) {
        super(errorCode, message);
    }

    public SalesforceCliLoginException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}