package com.datai.salesforce.common.exception;

/**
 * 操作异常类
 * <p>在执行操作过程中发生错误时抛出此异常</p>
 */
@SuppressWarnings("serial")
public class OperationException extends Exception {

    /**
     * 默认构造函数
     */
    public OperationException() {
        super();
    }

    /**
     * 带错误消息的构造函数
     * 
     * @param message 错误消息
     */
    public OperationException(String message) {
        super(message);
    }

    /**
     * 带错误消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public OperationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带原因的构造函数
     * 
     * @param cause 异常原因
     */
    public OperationException(Throwable cause) {
        super(cause);
    }

}