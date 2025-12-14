package com.datai.salesforce.common.exception;

/**
 * 进程初始化异常类
 * <p>在进程初始化过程中发生错误时抛出此异常</p>
 */
@SuppressWarnings("serial")
public class ProcessInitializationException extends Exception {

    /**
     * 默认构造函数
     */
    public ProcessInitializationException() {
        super();
    }

    /**
     * 带错误消息的构造函数
     * 
     * @param message 错误消息
     */
    public ProcessInitializationException(String message) {
        super(message);
    }

    /**
     * 带错误消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public ProcessInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带原因的构造函数
     * 
     * @param cause 异常原因
     */
    public ProcessInitializationException(Throwable cause) {
        super(cause);
    }

}