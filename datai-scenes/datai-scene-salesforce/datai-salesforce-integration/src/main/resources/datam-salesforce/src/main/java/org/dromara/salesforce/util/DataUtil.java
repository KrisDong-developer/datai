package org.dromara.salesforce.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 数据处理工具类
 * <p>
 * 提供各种数据处理相关的工具方法，包括字符串处理、集合操作等
 * </p>
 *
 * @author lingma
 * @since 1.0.0
 */
@Slf4j
public class DataUtil {

    /**
     * 将逗号分隔的API名称字符串转换为List
     *
     * @param apis 逗号分隔的API名称字符串
     * @return API名称列表
     */
    public static List<String> toApiList(String apis) {
        if (StringUtils.isBlank(apis)) {
            return Lists.newArrayList();
        }
        return Arrays.asList(apis.replaceAll(StringUtils.SPACE, StringUtils.EMPTY).split(","));
    }

    /**
     * 将逗号分隔的API名称字符串转换为Set
     *
     * @param apis 逗号分隔的API名称字符串
     * @return API名称集合
     */
    public static Set<String> toApiSet(String apis) {
        if (StringUtils.isBlank(apis)) {
            return Collections.emptySet();
        }
        Set<String> set = new HashSet<>();
        String[] split = apis.replaceAll(StringUtils.SPACE, StringUtils.EMPTY).split(",");
        for (String id : split) {
            if (StringUtils.isNotBlank(id)) {
                set.add(id);
            }
        }
        return set;
    }

    /**
     * 将API列表转换为逗号分隔的字符串
     *
     * @param apiList API列表
     * @return 逗号分隔的API字符串
     */
    public static String toApiString(List<String> apiList) {
        if (apiList == null || apiList.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return String.join(",", apiList);
    }

    /**
     * 将API集合转换为逗号分隔的字符串
     *
     * @param apiSet API集合
     * @return 逗号分隔的API字符串
     */
    public static String toApiString(Set<String> apiSet) {
        if (apiSet == null || apiSet.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return String.join(",", apiSet);
    }

    /**
     * 清理API名称列表，去除空白字符和重复项
     *
     * @param apis 逗号分隔的API名称字符串
     * @return 清理后的API名称集合
     */
    public static Set<String> cleanApiSet(String apis) {
        if (StringUtils.isBlank(apis)) {
            return Sets.newHashSet();
        }

        Set<String> result = Sets.newHashSet();
        String[] split = apis.replaceAll(StringUtils.SPACE, StringUtils.EMPTY).split(",");
        for (String api : split) {
            if (StringUtils.isNotBlank(api)) {
                result.add(api.trim());
            }
        }
        return result;
    }

    /**
     * 判断API字符串是否包含指定的API
     *
     * @param apis 逗号分隔的API名称字符串
     * @param targetApi 目标API名称
     * @return 是否包含
     */
    public static boolean containsApi(String apis, String targetApi) {
        if (StringUtils.isBlank(apis) || StringUtils.isBlank(targetApi)) {
            return false;
        }

        Set<String> apiSet = toApiSet(apis);
        return apiSet.contains(targetApi.trim());
    }

    /**
     * 从API字符串中移除指定的API
     *
     * @param apis 逗号分隔的API名称字符串
     * @param targetApi 要移除的API名称
     * @return 移除指定API后的字符串
     */
    public static String removeApi(String apis, String targetApi) {
        if (StringUtils.isBlank(apis) || StringUtils.isBlank(targetApi)) {
            return StringUtils.defaultString(apis);
        }

        Set<String> apiSet = cleanApiSet(apis);
        apiSet.remove(targetApi.trim());
        return toApiString(apiSet);
    }

    /**
     * 向API字符串中添加新的API
     *
     * @param apis 逗号分隔的API名称字符串
     * @param newApi 要添加的API名称
     * @return 添加新API后的字符串
     */
    public static String addApi(String apis, String newApi) {
        if (StringUtils.isBlank(newApi)) {
            return StringUtils.defaultString(apis);
        }

        Set<String> apiSet = cleanApiSet(apis);
        apiSet.add(newApi.trim());
        return toApiString(apiSet);
    }
}
