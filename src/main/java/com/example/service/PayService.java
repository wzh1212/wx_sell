package com.example.service;

import com.example.entity.OrderMaster;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.model.RefundResponse;
import org.springframework.stereotype.Service;

@Service
public interface PayService {

    // 根据 订单id 查询订单
    OrderMaster findOrderById(String orderId);

    // 创建预支付订单
//    void create(OrderMaster orderMaster);
    PayResponse create(OrderMaster orderMaster);

    // 异步通知处理
    void weixin_notify(String notifyData);

    // 退款
    RefundResponse refund(OrderMaster orderMaster);


}
