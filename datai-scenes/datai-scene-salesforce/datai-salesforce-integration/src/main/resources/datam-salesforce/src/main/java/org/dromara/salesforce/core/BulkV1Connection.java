package org.dromara.salesforce.core;

import com.sforce.async.*;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.parser.PullParserException;
import com.sforce.ws.parser.XmlInputStream;
import org.dromara.salesforce.config.AppConfig;
import org.dromara.salesforce.config.SalesforceSingletonConfig;
import org.dromara.salesforce.constant.BulkConstant;
import org.dromara.salesforce.constant.SalesforceConstant;
import org.dromara.salesforce.transport.HttpTransportImpl;
import org.dromara.salesforce.transport.HttpTransportInterface;
import org.dromara.salesforce.util.AppUtil;
import org.apache.logging.log4j.Logger;
import org.dromara.salesforce.util.DLLogManager;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Bulk V1连接类 - 处理Salesforce Bulk API V1操作的具体逻辑
 *
 * BulkV1Connection继承自BulkConnection，提供了对Salesforce Bulk API V1的增强支持。
 * 它扩展了标准BulkConnection的功能，添加了自定义的HTTP GET请求处理和结果解析逻辑。
 *
 * 主要功能：
 * 1. 执行Bulk V1 API操作（作业状态查询、批处理信息获取等）
 * 2. 处理与Salesforce Bulk V1 API的通信
 * 3. 管理连接配置和请求头
 * 4. 解析API响应结果
 *
 * 设计特点：
 * - 继承自BulkConnection，保持与标准Bulk API的兼容性
 * - 支持传统HTTP GET和自定义HTTP GET两种模式
 * - 提供XML和JSON格式的结果解析
 * - 集成自定义HTTP传输实现
 *
 * 使用场景：
 * - 处理大量数据的批量导入/导出操作
 * - 需要异步处理的长时间运行操作
 * - 需要更灵活的HTTP请求处理场景
 *
 * @author Salesforce
 * @since 64.0.0
 */
public class BulkV1Connection extends BulkConnection {
    /**
     * 日志记录器实例
     */
    private static Logger logger = DLLogManager.getLogger(BulkV1Connection.class);

    /**
     * 构造函数 - 创建BulkV1Connection实例
     *
     * @param config 连接器配置
     * @throws AsyncApiException 异步API异常
     */
    public BulkV1Connection(ConnectorConfig config) throws AsyncApiException {
        super(config);

        // 这是设置Bulk V1调用中正确的客户端名称所必需的
        addHeader(BulkConstant.SFORCE_CALL_OPTIONS_HEADER, config.getRequestHeader(BulkConstant.SFORCE_CALL_OPTIONS_HEADER));
    }

    /**
     * 添加请求头
     *
     * @param headerName 请求头名称
     * @param headerValue 请求头值
     */
    public void addHeader(String headerName, String headerValue) {
        super.addHeader(headerName, headerValue);
        if (BulkConstant.SFORCE_CALL_OPTIONS_HEADER.equalsIgnoreCase(headerName)) {
            logger.debug(BulkConstant.SFORCE_CALL_OPTIONS_HEADER + " : " + headerValue);
        }
    }

    /**
     * 获取作业状态
     *
     * @param jobId 作业ID
     * @return JobInfo 作业信息
     * @throws AsyncApiException 异步API异常
     */
    public JobInfo getJobStatus(String jobId) throws AsyncApiException {
        return getJobStatus(jobId, ContentType.XML);
    }

    /**
     * 获取作业状态(指定内容类型)
     *
     * @param jobId 作业ID
     * @param contentType 内容类型
     * @return JobInfo 作业信息
     * @throws AsyncApiException 异步API异常
     */
    public JobInfo getJobStatus(String jobId, ContentType contentType) throws AsyncApiException {
        if (AppConfig.getCurrentConfig().getBoolean(AppConfig.PROP_USE_LEGACY_HTTP_GET)) {
            return super.getJobStatus(jobId, contentType);
        } else {
            String[] urlParts = {"job", jobId};
            InputStream in = invokeBulkV1GET(urlParts);
            return processBulkV1Get(in, contentType, JobInfo.class);
        }
    }

    /**
     * 获取批处理信息列表
     *
     * @param jobId 作业ID
     * @return BatchInfoList 批处理信息列表
     * @throws AsyncApiException 异步API异常
     */
    public BatchInfoList getBatchInfoList(String jobId) throws AsyncApiException {
        return getBatchInfoList(jobId, ContentType.XML);
    }

    /**
     * 获取批处理信息列表(指定内容类型)
     *
     * @param jobId 作业ID
     * @param contentType 内容类型
     * @return BatchInfoList 批处理信息列表
     * @throws AsyncApiException 异步API异常
     */
    public BatchInfoList getBatchInfoList(String jobId, ContentType contentType) throws AsyncApiException {
        if (AppConfig.getCurrentConfig().getBoolean(AppConfig.PROP_USE_LEGACY_HTTP_GET)) {
            return super.getBatchInfoList(jobId, contentType);
        } else {
            String[] urlParts = {"job", jobId, "batch"};
            InputStream in = invokeBulkV1GET(urlParts);
            return processBulkV1Get(in, contentType, BatchInfoList.class);
        }
    }

    /**
     * 获取批处理结果流
     *
     * @param jobId 作业ID
     * @param batchId 批处理ID
     * @return InputStream 结果流
     * @throws AsyncApiException 异步API异常
     */
    public InputStream getBatchResultStream(String jobId, String batchId) throws AsyncApiException {
        if (AppConfig.getCurrentConfig().getBoolean(AppConfig.PROP_USE_LEGACY_HTTP_GET)) {
            return super.getBatchResultStream(jobId, batchId);
        } else {
            String[] urlParts = {"job", jobId, "batch", batchId, "result"};
            return invokeBulkV1GET(urlParts);
        }
    }

    /**
     * 获取查询结果列表
     *
     * @param jobId 作业ID
     * @param batchId 批处理ID
     * @return QueryResultList 查询结果列表
     * @throws AsyncApiException 异步API异常
     */
    public QueryResultList getQueryResultList(String jobId, String batchId) throws AsyncApiException {
        return getQueryResultList(jobId, batchId, ContentType.XML);
    }

    /**
     * 获取查询结果列表(指定内容类型)
     *
     * @param jobId 作业ID
     * @param batchId 批处理ID
     * @param contentType 内容类型
     * @return QueryResultList 查询结果列表
     * @throws AsyncApiException 异步API异常
     */
    public QueryResultList getQueryResultList(String jobId, String batchId, ContentType contentType) throws AsyncApiException {
        if (AppConfig.getCurrentConfig().getBoolean(AppConfig.PROP_USE_LEGACY_HTTP_GET)) {
            return super.getQueryResultList(jobId, batchId, contentType);
        } else {
            InputStream in = getBatchResultStream(jobId, batchId);
            return processBulkV1Get(in, contentType, QueryResultList.class);
        }
    }

    /**
     * 获取查询结果流
     *
     * @param jobId 作业ID
     * @param batchId 批处理ID
     * @param resultId 结果ID
     * @return InputStream 结果流
     * @throws AsyncApiException 异步API异常
     */
    public InputStream getQueryResultStream(String jobId, String batchId, String resultId) throws AsyncApiException {
        if (AppConfig.getCurrentConfig().getBoolean(AppConfig.PROP_USE_LEGACY_HTTP_GET)) {
            return super.getQueryResultStream(jobId, batchId, resultId);
        } else {
            String[] urlParts = {"job", jobId, "batch", batchId, "result", resultId};
            return invokeBulkV1GET(urlParts);
        }
    }

    /**
     * 执行Bulk V1 GET请求
     *
     * @param urlParts URL路径部分数组
     * @return InputStream 响应输入流
     * @throws AsyncApiException 异步API异常
     */
    private InputStream invokeBulkV1GET(String[] urlParts) throws AsyncApiException {
        String endpoint = getConfig().getRestEndpoint();
        endpoint = endpoint.endsWith("/") ? endpoint : endpoint + "/";
        if (urlParts != null) {
            for (String urlPart : urlParts) {
                endpoint += urlPart + "/";
            }
        }
        try {
            HttpTransportInterface transport = HttpTransportImpl.getInstance();
            transport.setConfig(getConfig());
            return transport.httpGet(endpoint);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new AsyncApiException("Failed to get result ", AsyncExceptionCode.ClientInputError, e);
        }
    }

    /**
     * 处理Bulk V1 GET请求结果
     *
     * @param <T> 返回类型
     * @param is 输入流
     * @param contentType 内容类型
     * @param returnClass 返回类
     * @return T 解析后的结果对象
     * @throws AsyncApiException 异步API异常
     */
    private <T> T processBulkV1Get(InputStream is, ContentType contentType, Class<T> returnClass) throws AsyncApiException {
        try {
            if (contentType == ContentType.JSON || contentType == ContentType.ZIP_JSON) {
                return AppUtil.deserializeJsonToObject(is, returnClass);
            } else {
                XmlInputStream xin = new XmlInputStream();
                xin.setInput(is, "UTF-8");
                Constructor<?>[] ctors = returnClass.getDeclaredConstructors();
                Constructor<?> ctor = null;
                for (int i = 0; i < ctors.length; i++) {
                    ctor = ctors[i];
                    if (ctor.getGenericParameterTypes().length == 0)
                    break;
                }
                if (ctor == null) {
                    throw new AsyncApiException("Failed to get result: " + returnClass + " cannot be instantiated", AsyncExceptionCode.ClientInputError);
                }
                T result = (T)ctor.newInstance();
                Method loadMethod = returnClass.getMethod("load", xin.getClass(), typeMapper.getClass());
                loadMethod.invoke(result, xin, typeMapper);
                return result;
            }
        } catch (IOException | PullParserException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
            logger.error(e.getMessage());
            throw new AsyncApiException("Failed to get result ", AsyncExceptionCode.ClientInputError, e);
        }
    }
}
