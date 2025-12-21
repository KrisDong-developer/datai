package org.dromara.salesforce.exception;

import com.sforce.ws.ConnectionException;

/**
 * 密码过期异常类
 * <p>当用户密码过期时抛出此异常</p>
 */
@SuppressWarnings("serial")
public class PasswordExpiredException extends ConnectionException {

    /**
     * 默认构造函数
     */
    public PasswordExpiredException() {
        super();
    }

    /**
     * 带错误消息的构造函数
     * 
     * @param message 错误消息
     */
    public PasswordExpiredException(String message) {
        super(message);
    }

    /**
     * 带错误消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public PasswordExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

}