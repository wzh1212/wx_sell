package com.example.service;

import com.example.common.ResultResponse;
import com.example.dto.OrderMasterDto;
import org.springframework.stereotype.Service;

@Service
public interface OrderMasterService {

    // 插入订单
    ResultResponse insertOrder(OrderMasterDto orderMasterDto);

}
