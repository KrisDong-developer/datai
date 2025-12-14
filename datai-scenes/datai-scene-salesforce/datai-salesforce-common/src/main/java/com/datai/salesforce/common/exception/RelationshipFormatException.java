
package com.datai.salesforce.common.exception;

/**
 * 关系字段格式异常类
 *
 * <p>自定义异常类，用于处理关系字段格式错误的情况。
 * 当在解析父对象关系字段格式时出现错误，会抛出此异常。</p>
 *
 * <p>此异常类继承自Exception，提供了多种构造方法：
 * <ol>
 *   <li>无参构造方法</li>
 *   <li>带消息参数的构造方法</li>
 *   <li>带消息和原因参数的构造方法</li>
 *   <li>带原因参数的构造方法</li>
 * </ol>
 * </p>
 */
@SuppressWarnings("serial")
public class RelationshipFormatException extends Exception {

    /**
     * 默认构造方法
     */
    public RelationshipFormatException() {
        super();
    }

    /**
     * 带错误消息的构造方法
     * @param message 错误消息
     */
    public RelationshipFormatException(String message) {
        super(message);
    }

    /**
     * 带错误消息和原因的构造方法
     * @param message 错误消息
     * @param cause 异常原因
     */
    public RelationshipFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带原因的构造方法
     * @param cause 异常原因
     */
    public RelationshipFormatException(Throwable cause) {
        super(cause);
    }
}
