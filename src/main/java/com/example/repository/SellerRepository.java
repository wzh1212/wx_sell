package com.example.repository;

import com.example.entity.SellerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// 泛型1：实体类类型  泛型2：主键类型
public interface SellerRepository extends JpaRepository<SellerInfo,String> {

    // 关键字定义
    List<SellerInfo> findAllByIdIn(List<String> ids);

    // 自定义 sql
    // jpa 底层实现 hibernate，hibernate：hql（基于实体类进行查询）,jpa：jpql（基于实体类）
    @Query(value = "select * from sell_info where id = ?1 ",nativeQuery = true)
    SellerInfo querySellerInfoBySellerId(String id);

    // jpa：jpql（基于实体类）
    @Query(value = "select s from SellerInfo s where id = ?1 ")
    SellerInfo getSellerInfoBySellerId(String id);
}
