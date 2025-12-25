package com.datai.auth.service;

/**
 * 令牌管理器接口，负责令牌的验证、吊销、绑定等操作
 * 支持令牌绑定设备/IP，提高安全性
 * 
 * @author datai
 * @date 2025-12-14
 */
public interface ITokenManager {
    /**
     * 验证令牌有效性
     * 
     * @param sessionId 会话ID
     * @return 是否有效
     */
    boolean validateToken(String sessionId);

    /**
     * 吊销令牌
     * 
     * @param sessionId 会话ID
     * @return 是否吊销成功
     */
    boolean revokeToken(String sessionId);

    /**
     * 绑定令牌到设备/IP
     * 
     * @param sessionId 会话ID
     * @param deviceId 设备ID
     * @param ip IP地址
     */
    void bindToken(String sessionId, String deviceId, String ip);

    /**
     * 检查令牌绑定
     * 
     * @param sessionId 会话ID
     * @param deviceId 设备ID
     * @param ip IP地址
     * @return 是否绑定匹配
     */
    boolean checkTokenBinding(String sessionId, String deviceId, String ip);
}