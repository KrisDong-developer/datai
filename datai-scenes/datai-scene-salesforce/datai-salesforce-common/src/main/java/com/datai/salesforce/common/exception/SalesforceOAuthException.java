package com.datai.salesforce.common.exception;

/**
 * Salesforce OAuth异常类
 * 用于处理Salesforce OAuth认证过程中发生的各种异常
 */
public class SalesforceOAuthException extends SalesforceLoginException {
    private static final long serialVersionUID = 1L;

    public SalesforceOAuthException(String message) {
        super(message);
    }

    public SalesforceOAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public SalesforceOAuthException(String errorCode, String message) {
        super(errorCode, message);
    }

    public SalesforceOAuthException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}