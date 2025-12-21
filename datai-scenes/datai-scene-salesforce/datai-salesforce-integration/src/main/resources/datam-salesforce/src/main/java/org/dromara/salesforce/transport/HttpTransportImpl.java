package org.dromara.salesforce.transport;

import com.sforce.ws.ConnectorConfig;
import org.dromara.salesforce.exception.HttpClientTransportException;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * HTTP传输实现类 - 实现HTTP传输操作的具体逻辑
 *
 * HttpTransportImpl是HttpTransportInterface接口的具体实现类。
 * 它使用Java原生的HttpURLConnection来实现与Salesforce服务器的HTTP通信。
 *
 * 主要功能：
 * 1. 实现HTTP GET和POST请求
 * 2. 处理HTTP请求头和认证信息
 * 3. 管理连接器配置
 *
 * 设计特点：
 * - 使用Java原生HttpURLConnection实现HTTP通信
 * - 遵循单例模式确保全局唯一实例
 * - 支持Salesforce认证和请求头处理
 *
 * 使用场景：
 * - 与Salesforce服务器进行HTTP通信
 * - 处理Bulk API V1和V2的HTTP请求
 * - 管理HTTP连接和认证信息
 *
 * @author Salesforce
 * @since 64.0.0
 */
@Component
public class HttpTransportImpl implements HttpTransportInterface {

    private ConnectorConfig config;
    private static HttpTransportImpl instance;

    // HTTP请求头映射
    private Map<String, String> headers = new HashMap<>();

    /**
     * 私有构造函数，实现单例模式
     */
    private HttpTransportImpl() {
    }

    /**
     * 获取HttpTransportImpl单例实例
     *
     * @return HttpTransportImpl 单例实例
     */
    public static HttpTransportImpl getInstance() {
        if (instance == null) {
            synchronized (HttpTransportImpl.class) {
                if (instance == null) {
                    instance = new HttpTransportImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 执行HTTP GET请求
     *
     * @param url 请求URL
     * @return InputStream 响应输入流
     * @throws IOException IO异常
     */
    @Override
    public InputStream httpGet(String url) throws IOException {
        return executeHttpRequest(url, "GET", null, null);
    }

    /**
     * 执行HTTP POST请求
     *
     * @param url 请求URL
     * @param data 请求数据
     * @param contentType 内容类型
     * @return InputStream 响应输入流
     * @throws IOException IO异常
     */
    @Override
    public InputStream httpPost(String url, byte[] data, String contentType) throws IOException {
        return executeHttpRequest(url, "POST", data, contentType);
    }

    /**
     * 设置连接器配置
     *
     * @param config 连接器配置
     */
    @Override
    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }

    /**
     * 执行HTTP请求
     *
     * @param urlString 请求URL
     * @param method HTTP方法(GET/POST)
     * @param data 请求数据
     * @param contentType 内容类型
     * @return InputStream 响应输入流
     * @throws IOException IO异常
     */
    private InputStream executeHttpRequest(String urlString, String method, byte[] data, String contentType) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // 设置请求方法
        connection.setRequestMethod(method);

        // 设置通用请求头
        connection.setRequestProperty("User-Agent", "DataLoaderBulkUI/");

        // 设置认证头
        if (config != null && config.getSessionId() != null) {
            connection.setRequestProperty("Authorization", "OAuth " + config.getSessionId());
        }

        // 设置自定义请求头
        Set<Map.Entry<String, String>> entrySet = headers.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        // 如果是POST请求，设置内容类型和发送数据
        if ("POST".equals(method) && data != null) {
            connection.setDoOutput(true);
            if (contentType != null) {
                connection.setRequestProperty("Content-Type", contentType);
            }
            try (OutputStream os = connection.getOutputStream()) {
                os.write(data);
            }
        }

        // 检查响应码
        int responseCode = connection.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            return connection.getInputStream();
        } else {
            // 处理错误响应
            String errorMessage = "HTTP " + method + " request failed with response code: " + responseCode;
            throw new HttpClientTransportException(errorMessage, responseCode);
        }
    }

    /**
     * 添加请求头
     *
     * @param headerName 请求头名称
     * @param headerValue 请求头值
     */
    public void addHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
    }

    /**
     * 移除请求头
     *
     * @param headerName 请求头名称
     */
    public void removeHeader(String headerName) {
        headers.remove(headerName);
    }
}
