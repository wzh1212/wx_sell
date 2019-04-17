package com.example.service;

import com.example.entity.OrderDetail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderDetailService {

    // 批量插入
    void batchInsert(List<OrderDetail> orderDetailList);

}
