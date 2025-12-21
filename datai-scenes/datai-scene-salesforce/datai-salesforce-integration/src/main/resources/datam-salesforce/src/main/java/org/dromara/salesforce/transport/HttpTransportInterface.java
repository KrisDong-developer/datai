package org.dromara.salesforce.transport;

import com.sforce.ws.ConnectorConfig;

import java.io.IOException;
import java.io.InputStream;

/**
 * HTTP传输接口 - 定义HTTP传输操作的接口
 *
 * HttpTransportInterface定义了与Salesforce服务器进行HTTP通信所需的基本方法。
 * 它为不同的HTTP传输实现提供了一个统一的接口，使得应用程序可以在不同的HTTP客户端实现之间切换。
 *
 * 主要功能：
 * 1. 定义HTTP GET请求方法
 * 2. 定义HTTP POST请求方法
 * 3. 定义配置设置方法
 *
 * 设计特点：
 * - 遵循面向接口编程原则
 * - 提供统一的HTTP传输操作接口
 * - 支持ConnectorConfig配置
 *
 * 使用场景：
 * - 与Salesforce服务器进行HTTP通信
 * - 实现不同的HTTP客户端传输方式
 * - 统一管理HTTP请求和响应处理
 *
 * @author Salesforce
 * @since 64.0.0
 */
public interface HttpTransportInterface {

    /**
     * 执行HTTP GET请求
     *
     * @param url 请求URL
     * @return InputStream 响应输入流
     * @throws IOException IO异常
     */
    InputStream httpGet(String url) throws IOException;

    /**
     * 执行HTTP POST请求
     *
     * @param url 请求URL
     * @param data 请求数据
     * @param contentType 内容类型
     * @return InputStream 响应输入流
     * @throws IOException IO异常
     */
    InputStream httpPost(String url, byte[] data, String contentType) throws IOException;

    /**
     * 设置连接器配置
     *
     * @param config 连接器配置
     */
    void setConfig(ConnectorConfig config);
}
