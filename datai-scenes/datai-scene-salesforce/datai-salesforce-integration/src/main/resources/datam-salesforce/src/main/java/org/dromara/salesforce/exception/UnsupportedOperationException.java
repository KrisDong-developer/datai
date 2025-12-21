package org.dromara.salesforce.exception;

/**
 * 不支持的操作异常类
 * <p>当执行了不支持的操作时抛出此异常</p>
 */
@SuppressWarnings("serial")
public class UnsupportedOperationException extends OperationInitializationException {

    /**
     * 默认构造函数
     */
    public UnsupportedOperationException() {
        super();
    }

    /**
     * 带错误消息的构造函数
     * 
     * @param message 错误消息
     */
    public UnsupportedOperationException(String message) {
        super(message);
    }

    /**
     * 带错误消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public UnsupportedOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带原因的构造函数
     * 
     * @param cause 异常原因
     */
    public UnsupportedOperationException(Throwable cause) {
        super(cause);
    }

}