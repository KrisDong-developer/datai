package com.datai.salesforce.common.exception;

/**
 * 批处理大小限制异常类
 * <p>当批处理大小超过限制时抛出此异常</p>
 */
@SuppressWarnings("serial")
public class BatchSizeLimitException extends Exception {

    /**
     * 默认构造函数
     */
    public BatchSizeLimitException() {
        super();
    }

    /**
     * 带错误消息的构造函数
     * 
     * @param message 错误消息
     */
    public BatchSizeLimitException(String message) {
        super(message);
    }

    /**
     * 带错误消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public BatchSizeLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带原因的构造函数
     * 
     * @param cause 异常原因
     */
    public BatchSizeLimitException(Throwable cause) {
        super(cause);
    }

}