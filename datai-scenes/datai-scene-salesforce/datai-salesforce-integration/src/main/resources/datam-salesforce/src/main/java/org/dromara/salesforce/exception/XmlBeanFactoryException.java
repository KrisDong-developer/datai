package org.dromara.salesforce.exception;

/**
 * XML Bean工厂异常类
 * <p>在XML Bean工厂初始化或处理过程中发生错误时抛出此异常</p>
 */
@SuppressWarnings("serial")
public class XmlBeanFactoryException extends ControllerInitializationException {

    /**
     * 带错误消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public XmlBeanFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带错误消息的构造函数
     * 
     * @param message 错误消息
     */
    public XmlBeanFactoryException(String message) {
        super(message);
    }

    /**
     * 带原因的构造函数
     * 
     * @param cause 异常原因
     */
    public XmlBeanFactoryException(Throwable cause) {
        super(cause);
    }

}