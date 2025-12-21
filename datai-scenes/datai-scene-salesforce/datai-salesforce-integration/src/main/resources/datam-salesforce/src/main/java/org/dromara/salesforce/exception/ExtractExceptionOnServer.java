package org.dromara.salesforce.exception;

/**
 * 服务器端数据提取异常类
 * <p>在服务器端数据提取过程中发生错误时抛出此异常</p>
 */
@SuppressWarnings("serial")
public class ExtractExceptionOnServer extends ExtractException {

    /**
     * 默认构造函数
     */
    public ExtractExceptionOnServer() {
        super();
    }

    /**
     * 带错误消息的构造函数
     *
     * @param message 错误消息
     */
    public ExtractExceptionOnServer(String message) {
        super(message);
    }

    /**
     * 带原因的构造函数
     *
     * @param cause 异常原因
     */
    public ExtractExceptionOnServer(Throwable cause) {
        super(cause);
    }

    /**
     * 带错误消息和原因的构造函数
     *
     * @param message 错误消息
     * @param cause 异常原因
     */
    public ExtractExceptionOnServer(String message, Throwable cause) {
        super(message, cause);
    }

}
