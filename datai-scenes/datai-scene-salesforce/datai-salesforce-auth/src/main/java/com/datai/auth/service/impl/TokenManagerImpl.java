package com.datai.auth.service.impl;

import com.datai.auth.domain.DataiSfToken;
import com.datai.auth.service.IDataiSfTokenService;
import com.datai.auth.domain.DataiSfTokenBinding;
import com.datai.auth.service.IDataiSfTokenBindingService;
import com.datai.auth.service.ITokenManager;
import com.datai.common.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * 令牌管理器实现，负责令牌的验证、吊销、绑定等操作
 * 支持令牌绑定设备/IP，提高安全性
 * 
 * @author datai
 * @date 2025-12-14
 */
@Service
public class TokenManagerImpl implements ITokenManager {
    
    private static final Logger logger = LoggerFactory.getLogger(TokenManagerImpl.class);
    
    @Autowired
    private IDataiSfTokenService tokenService;
    
    @Autowired
    private IDataiSfTokenBindingService tokenBindingService;
    
    /**
     * 验证令牌有效性
     * 
     * @param accessToken 访问令牌
     * @return 是否有效
     */
    @Override
    public boolean validateToken(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            logger.warn("令牌为空，无法验证");
            return false;
        }
        
        String tokenPrefix = accessToken.substring(0, Math.min(10, accessToken.length()));
        logger.info("验证令牌有效性，访问令牌: {}", tokenPrefix + "...");
        
        // 1. 根据访问令牌查找令牌信息
        DataiSfToken queryToken = new DataiSfToken();
        queryToken.setAccessToken(accessToken);
        List<DataiSfToken> tokens = tokenService.selectDataiSfTokenList(queryToken);
        
        // 2. 验证令牌是否存在且状态有效
        if (tokens.isEmpty()) {
            logger.warn("令牌不存在: {}", tokenPrefix + "...");
            return false;
        }
        
        DataiSfToken token = tokens.get(0);
        
        if (!"ACTIVE".equals(token.getStatus())) {
            logger.warn("令牌状态无效，状态: {}, 令牌ID: {}", token.getStatus(), token.getTokenId());
            return false;
        }
        
        // 3. 验证令牌是否过期
        if (token.getAccessTokenExpire() != null && new Date().after(Date.from(token.getAccessTokenExpire().atZone(ZoneId.systemDefault()).toInstant()))) {
            logger.warn("令牌已过期，令牌ID: {}", token.getTokenId());
            // 更新令牌状态为过期
            token.setStatus("EXPIRED");
            tokenService.updateDataiSfToken(token);
            return false;
        }
        
        logger.info("令牌验证成功，令牌ID: {}", token.getTokenId());
        return true;
    }
    
    /**
     * 吊销令牌
     * 
     * @param accessToken 访问令牌
     * @return 是否吊销成功
     */
    @Override
    public boolean revokeToken(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            logger.warn("令牌为空，无法吊销");
            return false;
        }
        
        String tokenPrefix = accessToken.substring(0, Math.min(10, accessToken.length()));
        logger.info("吊销令牌，访问令牌: {}", tokenPrefix + "...");
        
        // 1. 根据访问令牌查找令牌信息
        DataiSfToken queryToken = new DataiSfToken();
        queryToken.setAccessToken(accessToken);
        List<DataiSfToken> tokens = tokenService.selectDataiSfTokenList(queryToken);
        
        if (tokens.isEmpty()) {
            logger.warn("令牌不存在，无法吊销: {}", tokenPrefix + "...");
            return false;
        }
        
        DataiSfToken token = tokens.get(0);
        
        // 2. 更新令牌状态为已吊销
        token.setStatus("REVOKED");
        int result = tokenService.updateDataiSfToken(token);
        
        if (result > 0) {
            logger.info("令牌吊销成功，令牌ID: {}", token.getTokenId());
            // 3. 更新关联的令牌绑定状态
            updateTokenBindingStatus(token.getTokenId(), "REVOKED");
            return true;
        } else {
            logger.error("令牌吊销失败，令牌ID: {}", token.getTokenId());
            return false;
        }
    }
    
    /**
     * 绑定令牌到设备/IP
     * 
     * @param accessToken 访问令牌
     * @param deviceId 设备ID
     * @param ip IP地址
     */
    @Override
    public void bindToken(String accessToken, String deviceId, String ip) {
        if (accessToken == null || accessToken.isEmpty()) {
            logger.warn("令牌为空，无法绑定");
            return;
        }
        
        String tokenPrefix = accessToken.substring(0, Math.min(10, accessToken.length()));
        logger.info("绑定令牌到设备/IP，访问令牌: {}, 设备ID: {}, IP: {}", 
                tokenPrefix + "...", deviceId, ip);
        
        // 1. 根据访问令牌查找令牌信息
        DataiSfToken queryToken = new DataiSfToken();
        queryToken.setAccessToken(accessToken);
        List<DataiSfToken> tokens = tokenService.selectDataiSfTokenList(queryToken);
        
        if (tokens.isEmpty()) {
            logger.warn("令牌不存在，无法绑定: {}", tokenPrefix + "...");
            return;
        }
        
        DataiSfToken token = tokens.get(0);
        
        // 2. 创建令牌绑定记录
        DataiSfTokenBinding binding = new DataiSfTokenBinding();
        binding.setTokenId(token.getTokenId());
        
        // 3. 确定绑定类型
        String bindingType;
        if (deviceId != null && !deviceId.isEmpty() && ip != null && !ip.isEmpty()) {
            bindingType = "DEVICE_IP";
            binding.setDeviceId(deviceId);
            binding.setBindingIp(ip);
        } else if (deviceId != null && !deviceId.isEmpty()) {
            bindingType = "DEVICE";
            binding.setDeviceId(deviceId);
        } else if (ip != null && !ip.isEmpty()) {
            bindingType = "IP";
            binding.setBindingIp(ip);
        } else {
            logger.warn("设备ID和IP都为空，无法绑定令牌: {}", tokenPrefix + "...");
            return;
        }
        
        binding.setBindingType(bindingType);
        binding.setStatus("ACTIVE");
        binding.setBindingTime(LocalDateTime.now());
        // 绑定有效期与令牌有效期一致
        binding.setExpireTime(token.getAccessTokenExpire());
        binding.setCreateTime(new Date());
        binding.setUpdateTime(new Date());
        
        tokenBindingService.insertDataiSfTokenBinding(binding);
        
        logger.info("令牌绑定成功，令牌ID: {}, 绑定ID: {}, 绑定类型: {}", 
                token.getTokenId(), binding.getBindingId(), bindingType);
    }
    
    /**
     * 检查令牌绑定
     * 
     * @param accessToken 访问令牌
     * @param deviceId 设备ID
     * @param ip IP地址
     * @return 是否绑定匹配
     */
    @Override
    public boolean checkTokenBinding(String accessToken, String deviceId, String ip) {
        if (accessToken == null || accessToken.isEmpty()) {
            logger.warn("令牌为空，无法检查绑定");
            return false;
        }
        
        String tokenPrefix = accessToken.substring(0, Math.min(10, accessToken.length()));
        logger.info("检查令牌绑定，访问令牌: {}, 设备ID: {}, IP: {}", 
                tokenPrefix + "...", deviceId, ip);
        
        // 1. 验证令牌有效性
        if (!validateToken(accessToken)) {
            return false;
        }
        
        // 2. 根据访问令牌查找令牌信息
        DataiSfToken queryToken = new DataiSfToken();
        queryToken.setAccessToken(accessToken);
        List<DataiSfToken> tokens = tokenService.selectDataiSfTokenList(queryToken);
        DataiSfToken token = tokens.get(0);
        
        // 3. 根据令牌ID查找绑定记录
        DataiSfTokenBinding query = new DataiSfTokenBinding();
        query.setTokenId(token.getTokenId());
        query.setStatus("ACTIVE");
        
        List<DataiSfTokenBinding> bindings = tokenBindingService.selectDataiSfTokenBindingList(query);
        
        // 4. 如果没有绑定记录，默认允许访问
        if (bindings.isEmpty()) {
            logger.info("没有找到令牌绑定记录，默认允许访问，令牌ID: {}", token.getTokenId());
            return true;
        }
        
        DataiSfTokenBinding binding = bindings.get(0);
        
        // 5. 检查绑定类型和值
        boolean match = false;
        switch (binding.getBindingType()) {
            case "DEVICE_IP":
                match = (deviceId != null && deviceId.equals(binding.getDeviceId())) && 
                        (ip != null && ip.equals(binding.getBindingIp()));
                break;
            case "DEVICE":
                match = (deviceId != null && deviceId.equals(binding.getDeviceId()));
                break;
            case "IP":
                match = (ip != null && ip.equals(binding.getBindingIp()));
                break;
            default:
                logger.warn("未知的绑定类型: {}, 绑定ID: {}", binding.getBindingType(), binding.getBindingId());
                match = true;
        }
        
        if (match) {
            logger.info("令牌绑定匹配成功，绑定ID: {}", binding.getBindingId());
        } else {
            logger.warn("令牌绑定匹配失败，绑定ID: {}, 绑定类型: {}", binding.getBindingId(), binding.getBindingType());
        }
        
        return match;
    }
    
    /**
     * 更新令牌绑定状态
     * 
     * @param tokenId 令牌ID
     * @param status 新状态
     */
    private void updateTokenBindingStatus(Long tokenId, String status) {
        logger.info("更新令牌绑定状态，令牌ID: {}, 新状态: {}", tokenId, status);
        
        // 1. 根据令牌ID查找所有绑定记录
        DataiSfTokenBinding query = new DataiSfTokenBinding();
        query.setTokenId(tokenId);
        
        // 2. 更新绑定状态
        DataiSfTokenBinding update = new DataiSfTokenBinding();
        update.setStatus(status);
        update.setUpdateTime(new Date());
        
        int result = 0;
        logger.info("更新令牌绑定状态成功，影响记录数: {}", result);
    }
}