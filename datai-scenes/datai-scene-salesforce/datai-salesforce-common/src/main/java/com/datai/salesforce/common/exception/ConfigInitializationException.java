package com.datai.salesforce.common.exception;

/**
 * 配置初始化异常类
 * <p>在配置初始化过程中发生错误时抛出此异常</p>
 */
@SuppressWarnings("serial")
public class ConfigInitializationException extends ProcessInitializationException {

    /**
     * 默认构造函数
     */
    public ConfigInitializationException() {
        super();
    }

    /**
     * 带错误消息的构造函数
     * 
     * @param message 错误消息
     */
    public ConfigInitializationException(String message) {
        super(message);
    }

    /**
     * 带错误消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public ConfigInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带原因的构造函数
     * 
     * @param cause 异常原因
     */
    public ConfigInitializationException(Throwable cause) {
        super(cause);
    }

}