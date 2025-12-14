package com.datai.pay.alipay.service;

import java.util.Map;

import com.datai.pay.service.PayService;

public interface IAliPayService extends PayService {
    public void callback(Map<String, String> params);
}
