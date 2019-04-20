package com.example.repository;

import com.example.entity.OrderMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderMasterRepository extends JpaRepository<OrderMaster,String> {

    // 查找订单列表
    Page<OrderMaster> findAllByBuyerOpenid(String openId, Pageable pageable);

    // 查询订单详情
    OrderMaster findByBuyerOpenidAndOrderId(String openid,String orderId);
}
