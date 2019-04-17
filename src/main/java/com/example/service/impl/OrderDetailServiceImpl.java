package com.example.service.impl;

import com.example.dao.impl.BatchDaoImpl;
import com.example.entity.OrderDetail;
import com.example.service.OrderDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderDetailServiceImpl extends BatchDaoImpl<OrderDetail> implements OrderDetailService {

    /**
     * 批量插入
     * @param orderDetailList
     */
    @Override
    @Transactional //增删改触发事务
    public void batchInsert(List<OrderDetail> orderDetailList) {
        super.batchInsert(orderDetailList);
    }
}
