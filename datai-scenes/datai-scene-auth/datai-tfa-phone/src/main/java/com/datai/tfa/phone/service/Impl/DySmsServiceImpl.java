package com.datai.tfa.phone.service.Impl;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.datai.auth.common.enums.OauthVerificationUse;
import com.datai.auth.common.utils.RandomCodeUtil;
import com.datai.common.constant.CacheConstants;
import com.datai.common.constant.Constants;
import com.datai.common.core.domain.entity.SysUser;
import com.datai.common.core.domain.model.LoginBody;
import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.core.domain.model.RegisterBody;
import com.datai.common.exception.ServiceException;
import com.datai.common.utils.CacheUtils;
import com.datai.common.utils.MessageUtils;
import com.datai.common.utils.SecurityUtils;
import com.datai.common.utils.StringUtils;
import com.datai.framework.manager.AsyncManager;
import com.datai.framework.manager.factory.AsyncFactory;
import com.datai.framework.web.service.SysLoginService;
import com.datai.framework.web.service.TokenService;
import com.datai.framework.web.service.UserDetailsServiceImpl;
import com.datai.system.service.ISysUserService;
import com.datai.tfa.phone.config.DySmsConfig;
import com.datai.tfa.phone.service.DySmsService;
import com.datai.tfa.phone.utils.DySmsUtil;

/**
 * 手机号认证Servcie
 * 
 * @author zlh
 * @date 2024-04-16
 */
@Service("auth:service:dySms")
public class DySmsServiceImpl implements DySmsService {

    @Autowired
    private ISysUserService userService;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private SysLoginService sysLoginService;
    @Autowired
    private DySmsConfig dySmsConfig;

    private static final Logger log = LoggerFactory.getLogger(DySmsServiceImpl.class);

    @Override
    public boolean sendCode(String phone, String code, OauthVerificationUse use) {
        if (CacheUtils.hasKey(CacheConstants.PHONE_CODES, use.getValue() + phone)) {
            throw new ServiceException("当前验证码未失效，请在1分钟后再发送");
        }

        try {
            JSONObject templateParams = new JSONObject();
            templateParams.put("code", code);
            DySmsUtil.sendSms(phone, dySmsConfig.getTemplate().get("VerificationCode"), templateParams);
            CacheUtils.put(CacheConstants.PHONE_CODES, use.getValue() + phone, code, 1, TimeUnit.MINUTES);
            log.info("发送手机验证码成功:{ phone: " + phone + " code:" + code + "}");
            return true;
        } catch (Exception e) {
            throw new ServiceException("发送手机验证码异常：" + phone);
        }
    }

    @Override
    public boolean checkCode(String phone, String code, OauthVerificationUse use) {
        if (StringUtils.isEmpty(code)) {
            return false;
        }
        String cachedCode = CacheUtils.get(CacheConstants.PHONE_CODES, use.getValue() + phone, String.class); // 从缓存中获取验证码
        boolean isValid = code.equals(cachedCode);
        if (isValid) {
            CacheUtils.remove(CacheConstants.PHONE_CODES, use.getValue() + phone);
        }
        return isValid;
    }

    @Override
    public void doRegister(RegisterBody registerBody) {
        SysUser sysUser = userService.selectUserByPhone(registerBody.getPhonenumber());
        if (sysUser != null) {
            throw new ServiceException("该手机号已绑定用户");
        }
        sendCode(registerBody.getPhonenumber(), RandomCodeUtil.numberCode(6), OauthVerificationUse.REGISTER);
    }

    @Override
    public void doRegisterVerify(RegisterBody registerBody) {
        if (!checkCode(registerBody.getPhonenumber(), registerBody.getCode(), OauthVerificationUse.REGISTER)) {
            throw new ServiceException("验证码错误");
        }
        SysUser sysUser = new SysUser();
        sysUser.setUserName(registerBody.getPhonenumber());
        sysUser.setNickName(registerBody.getUsername());
        sysUser.setPassword(SecurityUtils.encryptPassword(registerBody.getPassword()));
        sysUser.setPhonenumber(registerBody.getPhonenumber());
        userService.registerUser(sysUser);
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(sysUser.getUserName(), Constants.REGISTER,
                MessageUtils.message("user.register.success")));
    }

    @Override
    public void doLogin(LoginBody loginBody, boolean autoRegister) {
        SysUser sysUser = userService.selectUserByPhone(loginBody.getPhonenumber());
        if (sysUser == null && !autoRegister) {
            throw new ServiceException("该手机号未绑定用户");
        }
        sendCode(loginBody.getPhonenumber(), RandomCodeUtil.numberCode(6), OauthVerificationUse.LOGIN);
    }

    @Override
    public String doLoginVerify(LoginBody loginBody, boolean autoRegister) {
        if (checkCode(loginBody.getPhonenumber(), loginBody.getCode(), OauthVerificationUse.LOGIN)) {
            SysUser sysUser = userService.selectUserByPhone(loginBody.getPhonenumber());
            if (sysUser == null) {
                if (!autoRegister) {
                    throw new ServiceException("该手机号未绑定用户");
                }
                sysUser = new SysUser();
                sysUser.setUserName(loginBody.getPhonenumber());
                sysUser.setNickName(loginBody.getPhonenumber());
                sysUser.setPassword(SecurityUtils.encryptPassword(loginBody.getCode()));
                sysUser.setPhonenumber(loginBody.getPhonenumber());
                userService.registerUser(sysUser);
            }
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(sysUser.getUserName(), Constants.LOGIN_SUCCESS,
                    MessageUtils.message("user.login.success")));
            LoginUser loginUser = (LoginUser) userDetailsServiceImpl.createLoginUser(sysUser);
            sysLoginService.recordLoginInfo(loginUser.getUserId());
            return tokenService.createToken(loginUser);
        } else {
            throw new ServiceException("验证码错误");
        }
    }

    @Override
    public void doBind(LoginBody loginBody) {
        SysUser sysUser = userService.selectUserByPhone(loginBody.getPhonenumber());
        if (sysUser != null) {
            throw new ServiceException("该手机号已绑定用户");
        }
        sysUser = userService.selectUserById(SecurityUtils.getUserId());
        sendCode(loginBody.getPhonenumber(), RandomCodeUtil.numberCode(6), OauthVerificationUse.BIND);
    }

    @Override
    public void doBindVerify(LoginBody loginBody) {
        if (checkCode(loginBody.getPhonenumber(), loginBody.getCode(), OauthVerificationUse.BIND)) {
            SysUser sysUser = userService.selectUserById(SecurityUtils.getUserId());
            sysUser.setPhonenumber(loginBody.getPhonenumber());
            userService.updateUser(sysUser);
            LoginUser loginUser = SecurityUtils.getLoginUser();
            loginUser.setUser(sysUser);
            tokenService.refreshToken(loginUser);
        } else {
            throw new ServiceException("验证码错误");
        }
    }

    public void doReset(RegisterBody registerBody) {
        SysUser sysUser = userService.selectUserByPhone(registerBody.getPhonenumber());
        if (sysUser == null) {
            throw new ServiceException("该手机号未绑定用户");
        }
        if (!sysUser.getUserName().equals(SecurityUtils.getUsername())) {
            throw new ServiceException("只能解绑自己的手机号");
        }
        sendCode(registerBody.getPhonenumber(), RandomCodeUtil.numberCode(6), OauthVerificationUse.RESET);
    }

    public void doResetVerify(RegisterBody registerBody) {
        if (checkCode(registerBody.getPhonenumber(), registerBody.getCode(), OauthVerificationUse.RESET)) {
            SysUser sysUser = userService.selectUserByUserName(SecurityUtils.getUsername());
            sysUser.setPhonenumber("");
            userService.updateUser(sysUser);
            LoginUser loginUser = SecurityUtils.getLoginUser();
            loginUser.setUser(sysUser);
            tokenService.refreshToken(loginUser);
        } else {
            throw new ServiceException("验证码错误");
        }
    }

}
