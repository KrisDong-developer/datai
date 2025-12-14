package com.datai.salesforce.common.exception;

/**
 * OAuth浏览器登录运行异常类
 * <p>在OAuth浏览器登录运行过程中发生错误时抛出此异常</p>
 */
@SuppressWarnings("serial")
public class OAuthBrowserLoginRunnerException extends Exception{

    /**
     * 默认构造函数
     */
    public OAuthBrowserLoginRunnerException() {
        super();
    }

    /**
     * 带错误消息的构造函数
     * 
     * @param message 错误消息
     */
    public OAuthBrowserLoginRunnerException(String message) {
        super(message);
    }

    /**
     * 带错误消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public OAuthBrowserLoginRunnerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带原因的构造函数
     * 
     * @param cause 异常原因
     */
    public OAuthBrowserLoginRunnerException(Throwable cause) {
        super(cause);
    }

}