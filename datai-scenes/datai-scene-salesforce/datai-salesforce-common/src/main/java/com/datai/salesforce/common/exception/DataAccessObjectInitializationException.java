
package com.datai.salesforce.common.exception;

/**
 * 数据访问对象初始化异常类
 * <p>在数据访问对象(DAO)初始化过程中发生错误时抛出此异常</p>
 */
@SuppressWarnings("serial")
public class DataAccessObjectInitializationException extends DataAccessObjectException {

    /**
     * 默认构造函数
     */
    public DataAccessObjectInitializationException() {
        super();
    }

    /**
     * 带错误消息的构造函数
     * 
     * @param message 错误消息
     */
    public DataAccessObjectInitializationException(String message) {
        super(message);
    }

    /**
     * 带错误消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public DataAccessObjectInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带原因的构造函数
     * 
     * @param cause 异常原因
     */
    public DataAccessObjectInitializationException(Throwable cause) {
        super(cause);
    }

}
