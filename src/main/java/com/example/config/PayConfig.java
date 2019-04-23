package com.example.config;

import com.lly835.bestpay.config.WxPayH5Config;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付配置类
 */
@Configuration
@Slf4j
public class PayConfig {

    @Autowired
    private WeiXinProperties weiXinProperties;

    /**
     * BestPayService ：调用下单业务的 API 类
     * WxPayH5Config ：配置文件
     * @return ：bestPayService
     */
    @Bean
    public BestPayService bestPayService(){
        WxPayH5Config wxPayH5Config = new WxPayH5Config();
        // 设置 公众号 appId
        wxPayH5Config.setAppId(weiXinProperties.getAppid());
        // 设置 公众号密钥
        wxPayH5Config.setAppSecret(weiXinProperties.getSecret());
        // 设置 商户号
        wxPayH5Config.setMchId(weiXinProperties.getMchId());
        // 设置 商户密钥
        wxPayH5Config.setMchKey(weiXinProperties.getMchKey());
        // 设置 商户证书路径
        wxPayH5Config.setKeyPath(weiXinProperties.getKeyPath());
        // 设置 异步通知路径
        wxPayH5Config.setNotifyUrl(weiXinProperties.getNotifyUrl());

        // 支付类，所有方法都在这个类里
        BestPayServiceImpl bestPayService = new BestPayServiceImpl();
        bestPayService.setWxPayH5Config(wxPayH5Config);

        return bestPayService;
    }

}
