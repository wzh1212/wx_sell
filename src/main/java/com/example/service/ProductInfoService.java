package com.example.service;

import com.example.common.ResultResponse;
import com.example.entity.ProductInfo;
import org.springframework.stereotype.Service;

@Service
public interface ProductInfoService {
    ResultResponse queryList();

    // 根据 id 查询商品
    ResultResponse<ProductInfo> queryById(String productId);

    // 修改商品到的库存
    void updateProduct(ProductInfo productInfo);
}
