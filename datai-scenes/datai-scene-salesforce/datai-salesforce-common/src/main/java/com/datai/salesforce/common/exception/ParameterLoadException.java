package com.datai.salesforce.common.exception;

/**
 * 参数加载异常类
 * <p>在参数加载过程中发生错误时抛出此异常</p>
 */
@SuppressWarnings("serial")
public class ParameterLoadException extends ConfigInitializationException {

    /**
     * 默认构造函数
     */
    public ParameterLoadException() {
        super();
    }

    /**
     * 带错误消息的构造函数
     * 
     * @param message 错误消息
     */
    public ParameterLoadException(String message) {
        super(message);
    }

    /**
     * 带错误消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public ParameterLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带原因的构造函数
     * 
     * @param cause 异常原因
     */
    public ParameterLoadException(Throwable cause) {
        super(cause);
    }

}