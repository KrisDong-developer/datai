package com.datai.salesforce.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API列表工具类
 * 用于处理API列表字符串的分割和转换
 * 
 * @author datai
 */
public class ApiListUtils {

    private static final String CHINESE_COMMA = "，";
    private static final String ENGLISH_COMMA = ",";
    private static final String COMMA_REGEX = "[,，]";

    /**
     * 将API列表字符串分割为List<String>
     * 支持中文逗号和英文逗号分隔
     * 自动去除每个元素的首尾空格，过滤空字符串
     * 
     * @param apiList API列表字符串，例如："api1,api2,api3" 或 "api1，api2，api3"
     * @return API列表，如果输入为null或空字符串，返回空列表
     */
    public static List<String> splitApiList(String apiList) {
        if (apiList == null || apiList.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(apiList.split(COMMA_REGEX))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 将API列表字符串分割为List<String>（不区分大小写）
     * 支持中文逗号和英文逗号分隔
     * 自动去除每个元素的首尾空格，过滤空字符串
     * 
     * @param apiList API列表字符串
     * @return API列表（小写），如果输入为null或空字符串，返回空列表
     */
    public static List<String> splitApiListToLower(String apiList) {
        if (apiList == null || apiList.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(apiList.split(COMMA_REGEX))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 将API列表字符串分割为List<String>（不区分大小写）
     * 支持中文逗号和英文逗号分隔
     * 自动去除每个元素的首尾空格，过滤空字符串
     * 
     * @param apiList API列表字符串
     * @return API列表（大写），如果输入为null或空字符串，返回空列表
     */
    public static List<String> splitApiListToUpper(String apiList) {
        if (apiList == null || apiList.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(apiList.split(COMMA_REGEX))
                .map(String::trim)
                .map(String::toUpperCase)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 将API列表字符串分割为List<String>，并去除重复项
     * 支持中文逗号和英文逗号分隔
     * 自动去除每个元素的首尾空格，过滤空字符串
     * 
     * @param apiList API列表字符串
     * @return 去重后的API列表，如果输入为null或空字符串，返回空列表
     */
    public static List<String> splitApiListDistinct(String apiList) {
        if (apiList == null || apiList.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(apiList.split(COMMA_REGEX))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 将API列表字符串分割为List<String>，并去除重复项（不区分大小写）
     * 支持中文逗号和英文逗号分隔
     * 自动去除每个元素的首尾空格，过滤空字符串
     * 
     * @param apiList API列表字符串
     * @return 去重后的API列表（小写），如果输入为null或空字符串，返回空列表
     */
    public static List<String> splitApiListDistinctIgnoreCase(String apiList) {
        if (apiList == null || apiList.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(apiList.split(COMMA_REGEX))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 将List<String>拼接为API列表字符串
     * 使用英文逗号分隔
     * 
     * @param apiList API列表
     * @return API列表字符串，如果列表为null或空，返回空字符串
     */
    public static String joinApiList(List<String> apiList) {
        if (apiList == null || apiList.isEmpty()) {
            return "";
        }
        return String.join(ENGLISH_COMMA, apiList);
    }

    /**
     * 将List<String>拼接为API列表字符串（使用中文逗号分隔）
     * 
     * @param apiList API列表
     * @return API列表字符串，如果列表为null或空，返回空字符串
     */
    public static String joinApiListWithChineseComma(List<String> apiList) {
        if (apiList == null || apiList.isEmpty()) {
            return "";
        }
        return String.join(CHINESE_COMMA, apiList);
    }

    /**
     * 检查API列表字符串是否包含指定的API
     * 支持中文逗号和英文逗号分隔
     * 不区分大小写
     * 
     * @param apiList API列表字符串
     * @param api 要检查的API
     * @return 如果包含返回true，否则返回false
     */
    public static boolean containsApi(String apiList, String api) {
        if (apiList == null || api == null) {
            return false;
        }
        List<String> apis = splitApiListToLower(apiList);
        return apis.contains(api.toLowerCase());
    }

    /**
     * 从API列表字符串中移除指定的API
     * 支持中文逗号和英文逗号分隔
     * 不区分大小写
     * 
     * @param apiList API列表字符串
     * @param apiToRemove 要移除的API
     * @return 移除后的API列表字符串
     */
    public static String removeApi(String apiList, String apiToRemove) {
        if (apiList == null || apiToRemove == null) {
            return apiList;
        }
        List<String> apis = splitApiList(apiList);
        List<String> filtered = apis.stream()
                .filter(api -> !api.equalsIgnoreCase(apiToRemove))
                .collect(Collectors.toList());
        return joinApiList(filtered);
    }

    /**
     * 向API列表字符串中添加新的API
     * 如果已存在则不添加
     * 支持中文逗号和英文逗号分隔
     * 不区分大小写
     * 
     * @param apiList API列表字符串
     * @param apiToAdd 要添加的API
     * @return 添加后的API列表字符串
     */
    public static String addApi(String apiList, String apiToAdd) {
        if (apiToAdd == null || apiToAdd.trim().isEmpty()) {
            return apiList;
        }
        
        List<String> apis = splitApiList(apiList);
        
        if (apis.stream().anyMatch(api -> api.equalsIgnoreCase(apiToAdd))) {
            return apiList;
        }
        
        if (apis.isEmpty()) {
            return apiToAdd.trim();
        }
        
        apis.add(apiToAdd.trim());
        return joinApiList(apis);
    }

    /**
     * 验证API列表字符串格式是否正确
     * 
     * @param apiList API列表字符串
     * @return 如果格式正确返回true，否则返回false
     */
    public static boolean isValidApiList(String apiList) {
        if (apiList == null || apiList.trim().isEmpty()) {
            return true;
        }
        
        List<String> apis = splitApiList(apiList);
        return !apis.isEmpty();
    }

    /**
     * 获取API列表字符串中的API数量
     * 
     * @param apiList API列表字符串
     * @return API数量
     */
    public static int getApiCount(String apiList) {
        if (apiList == null || apiList.trim().isEmpty()) {
            return 0;
        }
        return splitApiList(apiList).size();
    }
}
