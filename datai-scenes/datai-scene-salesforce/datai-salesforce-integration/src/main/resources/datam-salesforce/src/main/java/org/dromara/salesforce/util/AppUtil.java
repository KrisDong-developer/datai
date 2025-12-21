package org.dromara.salesforce.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * 应用工具类 - 提供应用程序通用的工具方法
 * 
 * AppUtil提供了一系列常用的工具方法，包括JSON序列化/反序列化、字符串处理等。
 * 它是应用程序中各种工具方法的集合，便于在不同部分复用。
 * 
 * 主要功能：
 * 1. JSON序列化和反序列化
 * 2. 字符串处理
 * 3. 通用工具方法
 * 
 * 设计特点：
 * - 静态方法集合，便于直接调用
 * - 使用Jackson库处理JSON
 * - 提供常用的字符串分隔符常量
 * 
 * 使用场景：
 * 1. 处理JSON数据格式转换
 * 2. 字符串操作和处理
 * 3. 各种通用工具方法
 * 
 * @author Salesforce
 * @since 64.0.0
 */
public class AppUtil {
    
    /**
     * 逗号分隔符常量
     */
    public static final String COMMA = ",";
    
    /**
     * JSON对象映射器
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 将JSON输入流反序列化为对象
     * 
     * @param <T> 对象类型
     * @param is JSON输入流
     * @param clazz 对象类
     * @return T 反序列化后的对象
     * @throws IOException IO异常
     */
    public static <T> T deserializeJsonToObject(InputStream is, Class<T> clazz) throws IOException {
        return objectMapper.readValue(is, clazz);
    }
    
    /**
     * 将对象序列化为JSON字符串
     * 
     * @param object 对象实例
     * @return String JSON字符串
     * @throws JsonProcessingException JSON处理异常
     */
    public static String serializeObjectToJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
    
    /**
     * 将JSON字符串反序列化为对象
     * 
     * @param <T> 对象类型
     * @param json JSON字符串
     * @param clazz 对象类
     * @return T 反序列化后的对象
     * @throws JsonProcessingException JSON处理异常
     */
    public static <T> T deserializeJsonToObject(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, clazz);
    }
}