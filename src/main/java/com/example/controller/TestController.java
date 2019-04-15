package com.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

//    Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/hello")  // 请求方式
//    @PostMapping
//    @RequestMapping(method = get)
    public String hello(){
 //       logger.info("杨幂");
        log.info("info -> {}","杨幂");
        return "你好，赵丽颖";
    }

}
