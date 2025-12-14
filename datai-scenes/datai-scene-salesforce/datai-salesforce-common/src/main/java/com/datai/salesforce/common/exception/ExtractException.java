package com.datai.salesforce.common.exception;

/**
 * 数据提取异常类
 * <p>在数据提取过程中发生错误时抛出此异常</p>
 */
@SuppressWarnings("serial")
public class ExtractException extends OperationException {

    /**
     * 默认构造函数
     */
    public ExtractException() {
        super();
    }

    /**
     * 带错误消息的构造函数
     *
     * @param message 错误消息
     */
    public ExtractException(String message) {
        super(message);
    }

    /**
     * 带原因的构造函数
     *
     * @param cause 异常原因
     */
    public ExtractException(Throwable cause) {
        super(cause);
    }

    /**
     * 带错误消息和原因的构造函数
     *
     * @param message 错误消息
     * @param cause 异常原因
     */
    public ExtractException(String message, Throwable cause) {
        super(message, cause);
    }

}
