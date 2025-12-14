package com.datai.oauth.wx.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.datai.auth.common.domain.OauthUser;
import com.datai.auth.common.service.IOauthUserService;
import com.datai.common.constant.Constants;
import com.datai.common.core.domain.entity.SysUser;
import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.exception.ServiceException;
import com.datai.common.utils.MessageUtils;
import com.datai.common.utils.SecurityUtils;
import com.datai.common.utils.StringUtils;
import com.datai.framework.manager.AsyncManager;
import com.datai.framework.manager.factory.AsyncFactory;
import com.datai.framework.web.service.SysLoginService;
import com.datai.framework.web.service.TokenService;
import com.datai.framework.web.service.UserDetailsServiceImpl;
import com.datai.oauth.wx.constant.WxPubConstant;
import com.datai.oauth.wx.service.WxLoginService;
import com.datai.system.service.ISysUserService;

@Service
public class WxPubLoginServiceImpl implements WxLoginService {

    @Autowired
    private WxPubConstant wxH5Constant;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IOauthUserService oauthUserService;

    @Autowired
    private SysLoginService sysLoginService;

    @Override
    public String doLogin(String code, boolean autoRegister) {
        JSONObject doAuth = doAuth(
                wxH5Constant.getUrl(),
                wxH5Constant.getAppId(),
                wxH5Constant.getAppSecret(),
                code);
        String openid = doAuth.getString("openid");
        OauthUser selectOauthUser = oauthUserService.selectOauthUserByUUID(openid);
        SysUser sysUser = null;
        if (selectOauthUser == null) {
            if (autoRegister) {
                sysUser = new SysUser();
                sysUser.setUserName(openid);
                sysUser.setNickName(openid);
                sysUser.setPassword(SecurityUtils.encryptPassword(code));
                userService.registerUser(sysUser);
                OauthUser oauthUser = new OauthUser();
                oauthUser.setUserId(sysUser.getUserId());
                oauthUser.setOpenId(doAuth.getString("openid"));
                oauthUser.setUuid(doAuth.getString("openid"));
                oauthUser.setSource("WXMiniApp");
                oauthUser.setAccessToken(doAuth.getString("session_key"));
                oauthUserService.insertOauthUser(oauthUser);
            }
        } else {
            sysUser = userService.selectUserById(selectOauthUser.getUserId());
        }
        if (sysUser == null) {
            throw new ServiceException("该微信未绑定用户");
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(sysUser.getUserName(), Constants.LOGIN_SUCCESS,
                MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) userDetailsServiceImpl.createLoginUser(sysUser);
        sysLoginService.recordLoginInfo(loginUser.getUserId());
        return tokenService.createToken(loginUser);
    }

    @Override
    public String doRegister(OauthUser oauthUser) {
        if (StringUtils.isEmpty(oauthUser.getCode())) {
            return "没有凭证";
        }
        if (oauthUser.getUserId() == null) {
            return "请先注册账号";
        }
        JSONObject doAuth = doAuth(
                wxH5Constant.getUrl(),
                wxH5Constant.getAppId(),
                wxH5Constant.getAppSecret(),
                oauthUser.getCode());
        oauthUser.setOpenId(doAuth.getString("openid"));
        oauthUser.setUuid(doAuth.getString("openid"));
        oauthUser.setSource("WXPub");
        oauthUser.setAccessToken(doAuth.getString("session_key"));
        oauthUserService.insertOauthUser(oauthUser);
        return "";
    }

}
