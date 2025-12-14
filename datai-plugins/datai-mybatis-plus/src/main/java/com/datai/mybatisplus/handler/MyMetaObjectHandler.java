package com.datai.mybatisplus.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.datai.common.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MyBatis Plus 元数据对象处理器
 * 用于自动填充创建人、创建时间、更新人、更新时间字段
 */
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 获取当前登录用户名
        String username = getUsername();
        
        // 填充创建时间和创建人
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "createBy", String.class, username);
        
        // 填充更新时间和更新人
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateBy", String.class, username);
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 获取当前登录用户名
        String username = getUsername();
        
        // 只更新更新时间和更新人
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
        this.strictUpdateFill(metaObject, "updateBy", String.class, username);
    }
    
    /**
     * 获取当前登录用户名
     * @return 用户名
     */
    private String getUsername() {
        try {
            return SecurityUtils.getUsername();
        } catch (Exception e) {
            // 如果获取不到用户信息，则返回系统标识
            return "system";
        }
    }
}