package com.example.repository;

import com.example.entity.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductInfoRepository extends JpaRepository<ProductInfo,String> {

    // 根据 类目的编号和状态 查询商品列表
    List<ProductInfo> findByProductStatusAndCategoryTypeIn(Integer status,List<Integer> categoryType);

}
