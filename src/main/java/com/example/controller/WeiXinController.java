package com.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("weixin")
@Slf4j
public class WeiXinController {

    /**
     * 获取授权码
     */
    @RequestMapping("/getCode")
    public void getCode(@RequestParam("code") String code){
        log.info("成功回调 getCode() 方法");
        log.info("微信授权码：{}",code);
    }
}
