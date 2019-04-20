package com.example.service;

import com.example.common.ResultResponse;
import com.example.dto.OrderMasterDto;
import com.example.entity.OrderMaster;
import org.springframework.stereotype.Service;

@Service
public interface OrderMasterService {

    // 插入订单
    ResultResponse insertOrder(OrderMasterDto orderMasterDto);

    //订单列表
    ResultResponse queryList(String openId, Integer pageNum, Integer pageSize);

    // 查询订单详情
    ResultResponse<OrderMaster> findByOrderIdAndOpenId(String orderId,String openId);

    // 取消订单
    ResultResponse cancelOrder(String openid,String orderId);

}
