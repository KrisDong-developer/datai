package org.dromara.salesforce.exception;

/**
 * 数据加载异常类
 * <p>在数据加载过程中发生错误时抛出此异常</p>
 */
@SuppressWarnings("serial")
public class LoadException extends OperationException {

    /**
     * 默认构造函数
     */
    public LoadException() {
        super();
    }

    /**
     * 带错误消息的构造函数
     *
     * @param message 错误消息
     */
    public LoadException(String message) {
        super(message);
    }

    /**
     * 带原因的构造函数
     *
     * @param cause 异常原因
     */
    public LoadException(Throwable cause) {
        super(cause);
    }

    /**
     * 带错误消息和原因的构造函数
     *
     * @param message 错误消息
     * @param cause 异常原因
     */
    public LoadException(String message, Throwable cause) {
        super(message, cause);
    }

}
