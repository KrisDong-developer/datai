package com.datai.salesforce.common.exception;

/**
 * Salesforce传统凭据登录异常类
 * 用于处理Salesforce传统凭据登录过程中发生的各种异常
 */
public class SalesforceLegacyCredentialLoginException extends SalesforceLoginException {
    private static final long serialVersionUID = 1L;

    public SalesforceLegacyCredentialLoginException(String message) {
        super(message);
    }

    public SalesforceLegacyCredentialLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public SalesforceLegacyCredentialLoginException(String errorCode, String message) {
        super(errorCode, message);
    }

    public SalesforceLegacyCredentialLoginException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}