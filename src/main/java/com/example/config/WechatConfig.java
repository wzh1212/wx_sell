package com.example.config;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信配置类
 */
@Configuration
@Slf4j
public class WechatConfig {

    @Autowired
    private WeiXinProperties weiXinProperties;

    @Bean
    public WxMpService wxMpService(){
        WxMpService wxMpService = new WxMpServiceImpl();
        // 设置微信配置的存储
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage());
        return wxMpService;
    }

    @Bean
    public WxMpConfigStorage wxMpConfigStorage(){
        WxMpInMemoryConfigStorage wxMpInMemoryConfigStorage = new WxMpInMemoryConfigStorage();
        // 设置微信配置
        wxMpInMemoryConfigStorage.setAppId(weiXinProperties.getAppid());
        wxMpInMemoryConfigStorage.setSecret(weiXinProperties.getSecret());
        log.info("appid:{}",weiXinProperties.getAppid());
        log.info("secret:{}",weiXinProperties.getSecret());
        return wxMpInMemoryConfigStorage;
    }

}
