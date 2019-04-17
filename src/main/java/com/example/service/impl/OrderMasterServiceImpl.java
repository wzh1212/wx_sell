package com.example.service.impl;

import com.example.common.*;
import com.example.dto.OrderDetailDto;
import com.example.dto.OrderMasterDto;
import com.example.entity.OrderDetail;
import com.example.entity.OrderMaster;
import com.example.entity.ProductInfo;
import com.example.exception.CustomException;
import com.example.repository.OrderMasterRepository;
import com.example.service.OrderDetailService;
import com.example.service.OrderMasterService;
import com.example.service.ProductInfoService;
import com.example.util.BigDecimalUtil;
import com.example.util.IDUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderMasterServiceImpl implements OrderMasterService {

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private ProductInfoService productInfoService;

    @Autowired
    private OrderDetailService orderDetailService;


    /**
     * 新增订单
     * @param orderMasterDto
     * @return
     */
    @Override
    @Transactional  //增删改触发事务
    public ResultResponse insertOrder(OrderMasterDto orderMasterDto) {
        // 参数校验，取出订单
        List<OrderDetailDto> items = orderMasterDto.getItems();
        // 创建订单 detail 集合，将符合的放入其中，待会进行批量插入
        List<OrderDetail> orderDetailList = Lists.newArrayList();
        // 创建订单 总金额为 0，涉及到钱的都用 高精度计算
        BigDecimal totalPrice = new BigDecimal("0");

        for (OrderDetailDto item : items) {
            ResultResponse<ProductInfo> resultResponse = productInfoService.queryById(item.getProductId());
            //说明该商品未查询到，生成订单失败，因为这儿涉及到事务 需要抛出异常 事务机制是遇到异常才会回滚
            if (resultResponse.getCode() == ResultEnums.FAIL.getCode()) {
                throw new CustomException(resultResponse.getMsg());
            }

            //获得查询的商品
            ProductInfo productInfo = resultResponse.getData();
            //判断 该商品的库存是否足够
            if (productInfo.getProductStock() < item.getProductQuantity()) {
                // 订单生成失败，直接抛出异常 事务才会回滚
                throw new CustomException(ProductEnums.PRODUCT_NOT_ENOUGH.getMsg());
            }

            //将前台传入的订单项DTO与数据库查询到的 商品数据组装成OrderDetail 放入集合中  @builder
            OrderDetail orderDetail = OrderDetail.builder()
                    .productIcon(productInfo.getProductIcon())
                    .detailId(IDUtils.createIdByUUID())
                    .productId(item.getProductId())
                    .productName(productInfo.getProductName())
                    .productPrice(productInfo.getProductPrice())
                    .productQuantity(item.getProductQuantity())
                    .build();

            orderDetailList.add(orderDetail);

            //减少商品库存
            productInfo.setProductStock(productInfo.getProductStock() - item.getProductQuantity());
            productInfoService.updateProduct(productInfo);

            //计算价格
            totalPrice = BigDecimalUtil.add(totalPrice,
                    BigDecimalUtil.multi(productInfo.getProductPrice(), item.getProductQuantity()));
        }
        //生成订单id
        String orderId = IDUtils.createIdByUUID();

        //构建订单信息  日期等都用默认的即可
        OrderMaster orderMaster = OrderMaster.builder()
                .buyerAddress(orderMasterDto.getAddress())
                .buyerName(orderMasterDto.getName())
                .buyerOpenid(orderMasterDto.getOpenid())
                .orderStatus(OrderEnums.NEW.getCode())
                .payStatus(PayEnums.WAIT.getCode())
                .buyerPhone(orderMasterDto.getPhone())
                .orderId(orderId)
                .orderAmount(totalPrice)
                .build();

        //将生成的订单id，设置到订单项中
        List<OrderDetail> detailList = orderDetailList.stream().map(
                orderDetail -> {
                    orderDetail.setOrderId(orderId);
                    return orderDetail;
                }).collect(Collectors.toList());

        //插入订单项
        orderDetailService.batchInsert(detailList);
        //插入订单
        orderMasterRepository.save(orderMaster);
        HashMap<String,String> map = Maps.newHashMap();
        //按照前台要求的数据结构传入
        map.put("orderId",orderId);
        return ResultResponse.success(map);
    }
}
