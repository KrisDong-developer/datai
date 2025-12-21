package org.dromara.salesforce.exception;

/**
 * 未登录异常类
 * <p>当用户未登录或会话已过期时抛出此异常</p>
 */
@SuppressWarnings("serial")
public class NotLoggedInException extends RuntimeException {

    /**
     * 默认构造函数
     */
    public NotLoggedInException() {
        super();
    }

    /**
     * 带错误消息的构造函数
     *
     * @param message 错误消息
     */
    public NotLoggedInException(String message) {
        super(message);
    }

    /**
     * 带错误消息和原因的构造函数
     *
     * @param message 错误消息
     * @param cause 异常原因
     */
    public NotLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带原因的构造函数
     *
     * @param cause 异常原因
     */
    public NotLoggedInException(Throwable cause) {
        super(cause);
    }

}
