
package org.dromara.salesforce.exception;

/**
 * 控制器初始化异常类
 * <p>在控制器初始化过程中发生错误时抛出此异常</p>
 */
@SuppressWarnings("serial")
public class ControllerInitializationException extends ProcessInitializationException {

    /**
     * 默认构造函数
     */
    public ControllerInitializationException() {
        super();
    }

    /**
     * 带错误消息的构造函数
     * 
     * @param message 错误消息
     */
    public ControllerInitializationException(String message) {
        super(message);
    }

    /**
     * 带错误消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public ControllerInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带原因的构造函数
     * 
     * @param cause 异常原因
     */
    public ControllerInitializationException(Throwable cause) {
        super(cause);
    }

}
