package com.example.controller;

import com.example.entity.OrderMaster;
import com.example.service.PayService;
import com.lly835.bestpay.model.PayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("pay")
@Slf4j
public class PayController {

    @Autowired
    private PayService payService;

    @RequestMapping("create")
    public ModelAndView create(@RequestParam("orderId") String orderId,
                               @RequestParam("returnUrl") String returnUrl,
                               Map map){

        // 根据 订单id 查询订单
        OrderMaster orderMaster = payService.findOrderById(orderId);

        // 根据 订单创建支付
        PayResponse response = payService.create(orderMaster);
        // 将参数设置到map中
        map.put("payResponse",response);
        map.put("returnUrl",returnUrl);
        return new ModelAndView("weixin/pay",map);
    }

    /**
     * 微信异步通知
     * @return
     */
    @RequestMapping("notify")
    public ModelAndView weixin_notify(@RequestBody String notifyData){
        log.info("notifyData:->{}",notifyData);
        // 验证数据，修改订单
        payService.weixin_notify(notifyData);
        return new ModelAndView("weixin/success");
    }

    @RequestMapping("test")
    public void test(){
        log.info("异步回调 OK");
    }

}
