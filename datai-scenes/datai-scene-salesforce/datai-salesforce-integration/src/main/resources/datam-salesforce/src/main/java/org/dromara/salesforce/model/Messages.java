/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.dromara.salesforce.model;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 消息处理类
 *
 * Messages是数据加载器中用于处理国际化消息的工具类。它从资源文件中加载消息，
 *
 * 主要功能：
 * 1. 从资源文件加载消息文本
 * 2. 支持消息的参数化格式化
 * 3. 提供基于类名或模块名的消息查找
 * 4. 处理缺失资源的情况
 *
 * 设计特点：
 * - 使用ResourceBundle管理国际化资源
 * - 支持MessageFormat进行消息格式化
 * - 提供多种消息获取方式
 * - 处理空参数和缺失资源的异常情况
 */
public class Messages {
    /**
     * 资源包名称
     */
    private static final String BUNDLE_NAME = "messages";

    /**
     * 资源包实例
     */
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    /**
     * 私有构造函数，防止实例化
     */
    private Messages() { }

    /**
     * 获取指定键的消息字符串
     *
     * @param key 消息键
     * @param args 消息参数
     * @return 格式化后的消息字符串
     */
    public static String getString(String key, Object... args) {
        return getString(key, false, args);
    }

    /**
     * 获取指定键的消息字符串
     *
     * @param key 消息键
     * @param nullOk 是否允许返回null
     * @param args 消息参数
     * @return 格式化后的消息字符串，如果nullOk为true且资源不存在则返回null，否则返回带感叹号的键名
     */
    private static String getString(String key, boolean nullOk, Object... args) {
        assert key.contains(".");
        try {
            if (args == null) return RESOURCE_BUNDLE.getString(key);
            for (int i = 0; i < args.length; i++)
                if (args[i] == null) args[i] = "";
            return MessageFormat.format(RESOURCE_BUNDLE.getString(key), args);
        } catch (MissingResourceException e) {
            return nullOk ? null : '!' + key + '!';
        }
    }

    /**
     * 获取指定模块和键的消息
     *
     * @param section 模块名
     * @param key 消息键
     * @param nullOk 是否允许返回null
     * @param args 消息参数
     * @return 格式化后的消息字符串
     */
    public static String getMessage(String section, String key, boolean nullOk, Object... args) {
        return getString(section + "." + key, nullOk, args);
    }

    /**
     * 获取指定模块和键的消息
     *
     * @param section 模块名
     * @param key 消息键
     * @param args 消息参数
     * @return 格式化后的消息字符串
     */
    public static String getMessage(String section, String key, Object... args) {
        return getMessage(section, key, false, args);
    }

    /**
     * 获取指定类和键的消息
     *
     * @param cls 类对象
     * @param key 消息键
     * @param args 消息参数
     * @return 格式化后的消息字符串
     */
    public static String getMessage(Class<?> cls, String key, Object... args) {
        return getMessage(cls, key, false, args);
    }

    /**
     * 获取指定类和键的消息
     *
     * @param cls 类对象
     * @param key 消息键
     * @param nullOk 是否允许返回null
     * @param args 消息参数
     * @return 格式化后的消息字符串
     */
    public static String getMessage(Class<?> cls, String key, boolean nullOk, Object... args) {
        for (Class<?> currentClass = cls; currentClass != null; currentClass = currentClass.getSuperclass()) {
            final String msg = getMessage(currentClass.getSimpleName(), key, true, args);
            if (msg != null) return msg;
        }
        return getMessage(cls.getSimpleName(), key, nullOk, args);
    }

    /**
     * 获取格式化后的消息字符串
     *
     * @param key 消息键
     * @param arg 消息参数
     * @return 格式化后的消息字符串
     */
    public static String getFormattedString(String key, Object arg) {
        return getString(key, arg);
    }

    /**
     * 获取格式化后的消息字符串
     *
     * @param key 消息键
     * @param args 消息参数数组
     * @return 格式化后的消息字符串
     */
    public static String getFormattedString(String key, Object[] args) {
        return getString(key, args);
    }

}
