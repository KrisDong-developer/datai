package com.datai.salesforce.common.exception;

/**
 * 不支持的数据访问对象异常类
 * <p>当使用了不支持的数据访问对象时抛出此异常</p>
 */
@SuppressWarnings("serial")
public class UnsupportedDataAccessObjectException extends DataAccessObjectInitializationException {

    /**
     * 默认构造函数
     */
    public UnsupportedDataAccessObjectException() {
        super();
    }

    /**
     * 带错误消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public UnsupportedDataAccessObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带错误消息的构造函数
     * 
     * @param message 错误消息
     */
    public UnsupportedDataAccessObjectException(String message) {
        super(message);
    }

    /**
     * 带原因的构造函数
     * 
     * @param cause 异常原因
     */
    public UnsupportedDataAccessObjectException(Throwable cause) {
        super(cause);
    }

}