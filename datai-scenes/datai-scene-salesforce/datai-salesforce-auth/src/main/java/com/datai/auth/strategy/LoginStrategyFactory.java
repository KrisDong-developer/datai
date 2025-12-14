package com.datai.auth.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 登录策略工厂，负责根据登录类型创建对应的登录策略
 * 
 * @author datai
 * @date 2025-12-14
 */
@Component
public class LoginStrategyFactory {
    
    private final Map<String, LoginStrategy> loginStrategyMap;
    
    @Autowired
    public LoginStrategyFactory(List<LoginStrategy> loginStrategies) {
        // 将登录策略列表转换为Map，key为登录类型
        this.loginStrategyMap = loginStrategies.stream()
                .collect(Collectors.toMap(LoginStrategy::getLoginType, strategy -> strategy));
    }
    
    /**
     * 根据登录类型获取对应的登录策略
     * 
     * @param loginType 登录类型
     * @return 登录策略实现
     */
    public LoginStrategy getLoginStrategy(String loginType) {
        LoginStrategy strategy = loginStrategyMap.get(loginType);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported login type: " + loginType);
        }
        return strategy;
    }
    
    /**
     * 获取所有支持的登录类型
     * 
     * @return 支持的登录类型列表
     */
    public List<String> getSupportedLoginTypes() {
        return loginStrategyMap.keySet().stream().collect(Collectors.toList());
    }
}