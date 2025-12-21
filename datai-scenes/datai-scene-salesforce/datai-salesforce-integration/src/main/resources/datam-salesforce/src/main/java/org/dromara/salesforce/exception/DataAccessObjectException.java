package org.dromara.salesforce.exception;

/**
 * 数据访问对象异常类
 * <p>在数据访问对象(DAO)操作过程中发生错误时抛出此异常</p>
 */
@SuppressWarnings("serial")
public class DataAccessObjectException extends Exception {

    /**
     * 默认构造函数
     */
    public DataAccessObjectException() {
        super();
    }

    /**
     * 带错误消息的构造函数
     * 
     * @param message 错误消息
     */
    public DataAccessObjectException(String message) {
        super(message);
    }

    /**
     * 带错误消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public DataAccessObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带原因的构造函数
     * 
     * @param cause 异常原因
     */
    public DataAccessObjectException(Throwable cause) {
        super(cause);
    }

}