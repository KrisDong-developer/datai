package com.datai.salesforce.common.exception;

import java.io.IOException;

/**
 * HTTP客户端传输异常类 - 处理HTTP传输过程中的异常情况
 *
 * HttpClientTransportException用于处理在HTTP客户端传输过程中发生的异常。
 * 它继承自IOException，提供了更具体的异常信息，便于识别和处理HTTP传输错误。
 *
 * 主要功能：
 * 1. 表示HTTP传输过程中的异常情况
 * 2. 提供详细的错误信息和状态码
 *
 * 设计特点：
 * - 继承自IOException，符合Java IO异常处理规范
 * - 包含HTTP状态码信息
 * - 提供多种构造方法满足不同使用场景
 *
 * 使用场景：
 * - HTTP请求失败
 * - 网络连接异常
 * - 服务器响应错误
 *
 * @author Salesforce
 * @since 64.0.0
 */
public class HttpClientTransportException extends IOException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * HTTP状态码
     */
    private int statusCode;
    
    /**
     * 构造函数 - 创建带有详细消息的异常
     *
     * @param message 详细错误消息
     */
    public HttpClientTransportException(String message) {
        super(message);
    }
    
    /**
     * 构造函数 - 创建带有详细消息和状态码的异常
     *
     * @param message 详细错误消息
     * @param statusCode HTTP状态码
     */
    public HttpClientTransportException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    /**
     * 构造函数 - 创建带有详细消息和原因的异常
     *
     * @param message 详细错误消息
     * @param cause 异常原因
     */
    public HttpClientTransportException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 构造函数 - 创建带有详细消息、原因和状态码的异常
     *
     * @param message 详细错误消息
     * @param statusCode HTTP状态码
     * @param cause 异常原因
     */
    public HttpClientTransportException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
    
    /**
     * 获取HTTP状态码
     *
     * @return int HTTP状态码
     */
    public int getStatusCode() {
        return statusCode;
    }
    
    /**
     * 设置HTTP状态码
     *
     * @param statusCode HTTP状态码
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
