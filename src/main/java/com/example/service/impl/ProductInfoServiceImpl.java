package com.example.service.impl;

import com.example.common.ProductEnums;
import com.example.common.ResultEnums;
import com.example.common.ResultResponse;
import com.example.dto.ProductCategoryDto;
import com.example.dto.ProductInfoDto;
import com.example.entity.ProductCategory;
import com.example.entity.ProductInfo;
import com.example.repository.ProductCategoryRepository;
import com.example.repository.ProductInfoRepository;
import com.example.service.ProductInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductInfoServiceImpl implements ProductInfoService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductInfoRepository productInfoRepository;

    /**
     * 商品分类展示
     * @return
     */
    @Override
    public ResultResponse queryList() {
        // 查询所有分类
        List<ProductCategory> all = productCategoryRepository.findAll();
        // 将 all 转换成 dto
        List<ProductCategoryDto> productCategoryDtoList
                = all.stream().map(productCategory -> ProductCategoryDto.build(productCategory)).collect(Collectors.toList());

        // 判断是否为空
        if (CollectionUtils.isEmpty(all)){
            return ResultResponse.fail();
        }
        // 获取 类目的编号集合
        List<Integer> typeList
                = productCategoryDtoList.stream().map(productCategoryDto -> productCategoryDto.getCategoryType()).collect(Collectors.toList());

        // 根据 typeList 查询商品列表
        List<ProductInfo> productInfoList
                = productInfoRepository.findByProductStatusAndCategoryTypeIn(ResultEnums.PRODUCT_UP.getCode(), typeList);

        // 对目标集合（productCategoryDtoList）进行多线程遍历，取出每个商品的类目编号，设置到对应的目录中
        // parallelStream：并发流，提高效率
        // 将 productInfo 设置到 foods 中
        // 过滤：将不同的 type，进行不同的封装
        // 将 productInfo 转成 dto
        List<ProductCategoryDto> productCategoryDtos = productCategoryDtoList.parallelStream().map(
                productCategoryDto -> {
                    productCategoryDto.setProductInfoDtoList(
                            productInfoList.stream()
                                    .filter(productInfo -> productInfo.getCategoryType() == productCategoryDto.getCategoryType())
                                    .map(productInfo -> ProductInfoDto.build(productInfo))
                                    .collect(Collectors.toList())
                    );
                    return productCategoryDto;
                }
        ).collect(Collectors.toList());

        return ResultResponse.success(productCategoryDtos);
    }

    /**
     * 根据 id 查询商品
     * @param productId
     * @return
     */
    @Override
    public ResultResponse<ProductInfo> queryById(String productId) {
        if (StringUtils.isBlank(productId)){
            return ResultResponse.fail(ResultEnums.PARAM_ERROR.getMsg() + ":" + productId);
        }

        Optional<ProductInfo> byId = productInfoRepository.findById(productId);
        // 判断 商品是否存在
        if (!byId.isPresent()){
            return ResultResponse.fail(ResultEnums.NOT_EXITS.getMsg() + ":" + productId);
        }

        ProductInfo productInfo = byId.get();
        // 判断商品是否下架
        if (productInfo.getProductStatus() == ResultEnums.PRODUCT_DOWN.getCode()){
            return ResultResponse.fail(ResultEnums.PRODUCT_DOWN.getMsg());
        }

        return ResultResponse.success(productInfo);
    }

    /**
     * 修改商品到的库存
     * @param productInfo
     */
    @Override
    public void updateProduct(ProductInfo productInfo) {
        productInfoRepository.save(productInfo);
    }
}
