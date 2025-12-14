package com.datai.salesforce.common.exception;

/**
 * 服务器端数据加载异常类
 * <p>在服务器端数据加载过程中发生错误时抛出此异常</p>
 */
@SuppressWarnings("serial")
public class LoadExceptionOnServer extends LoadException {

    /**
     * 默认构造函数
     */
    public LoadExceptionOnServer() {
        super();
    }

    /**
     * 带错误消息的构造函数
     * 
     * @param message 错误消息
     */
    public LoadExceptionOnServer(String message) {
        super(message);
    }

    /**
     * 带原因的构造函数
     * 
     * @param cause 异常原因
     */
    public LoadExceptionOnServer(Throwable cause) {
        super(cause);
    }

    /**
     * 带错误消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public LoadExceptionOnServer(String message, Throwable cause) {
        super(message, cause);
    }

}