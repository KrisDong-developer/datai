package com.datai.salesforce.common.exception;

/**
 * Salesforce认证异常类
 * 用于处理Salesforce登录认证过程中发生的各种异常
 */
public class SalesforceAuthException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private String errorCode;
    
    public SalesforceAuthException(String message) {
        super(message);
    }
    
    public SalesforceAuthException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public SalesforceAuthException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public SalesforceAuthException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}