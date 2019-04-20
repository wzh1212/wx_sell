package com.example.service.impl;

import com.example.common.*;
import com.example.dto.OrderDetailDto;
import com.example.dto.OrderMasterAndDetailDto;
import com.example.dto.OrderMasterDto;
import com.example.entity.OrderDetail;
import com.example.entity.OrderMaster;
import com.example.entity.ProductInfo;
import com.example.exception.CustomException;
import com.example.repository.OrderDetailRepository;
import com.example.repository.OrderMasterRepository;
import com.example.service.OrderDetailService;
import com.example.service.OrderMasterService;
import com.example.service.ProductInfoService;
import com.example.util.BigDecimalUtil;
import com.example.util.IDUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderMasterServiceImpl implements OrderMasterService {

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

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
        /**
         * 1.根据购物车(订单项) 传来的商品id 查询对应的商品 取得价格等相关信息 如果没查到 订单生成失败
         * 2.比较库存 ，库存不足 订单生成失败
         * 3.生成订单项OrderDetail信息
         * 4.减少商品库存
         * 5.算出总价格 ，组装订单信息 插入数据库得到订单号
         * 6.批量插入订单项
         *
         * 注意:1.生成订单就会减少库存 加入购物车不会  所有的网站基本都是这么设计的
         *      2.商品价格以生成订单时候为准，后面商品价格改变不影响已经生成的订单
         */
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

    /**
     * 订单列表
     * @param openId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResultResponse queryList(String openId, Integer pageNum, Integer pageSize) {
        // 判断 订单是否有误
        if (StringUtils.isBlank(openId)){
            return ResultResponse.fail(OrderEnums.OPENID_ERROR.getMsg());
        }
        if (pageNum == null){
            pageNum = 0;
        }
        // 判断每页显示的条数是否为空，为空则设置默认 10条
        if (pageSize == null){
            pageSize = 10;
        }
        // 分页按照 修改订单时间进行降序排序
        PageRequest pageRequest = PageRequest.of(pageNum,pageSize, Sort.Direction.DESC,"updateTime");
        Page<OrderMaster> allByBuyerOpenid = orderMasterRepository.findAllByBuyerOpenid(openId, pageRequest);
        return ResultResponse.success(allByBuyerOpenid.getContent());
    }

    /**
     * 查询订单详情
     * @param openid
     * @param orderId
     * @return
     */
    @Override
    public ResultResponse<OrderMaster> findByOrderIdAndOpenId(String openid,String orderId) {
        if (StringUtils.isBlank(openid)){
            return ResultResponse.fail(OrderEnums.OPENID_ERROR.getMsg() + "：" + openid);
        }
        if (StringUtils.isBlank(orderId)){
            return ResultResponse.fail(ResultEnums.PARAM_ERROR.getMsg() + "：" + orderId);
        }

        // 根据 orderId 和 openId 查询出订单
        OrderMaster detail = orderMasterRepository.findByBuyerOpenidAndOrderId(openid,orderId);
        if (detail == null){
            throw new CustomException("参数错误");
        }

        OrderMasterAndDetailDto orderMasterAndDetailDto = OrderMasterAndDetailDto.build(detail);

        // 根据 orderId 查询出对应的商品
        List<OrderDetail> byOrderId = orderDetailRepository.findByOrderId(orderId);
        orderMasterAndDetailDto.setOrderDetailList(byOrderId);

        return ResultResponse.success(orderMasterAndDetailDto);
    }

    /**
     * 取消订单
     * @param openid
     * @param orderId
     * @return
     */
    @Override
    @Transactional
    public ResultResponse cancelOrder(String openid, String orderId) {
        // 获取订单详情
        OrderMaster detail = orderMasterRepository.findByBuyerOpenidAndOrderId(openid,orderId);
        // 判断数据是否为空
        if (detail == null){
            return ResultResponse.fail(OrderEnums.ORDER_NOT_EXITS.getMsg());
        }
        // 判断是否已经完成或取消
        if (detail.getOrderStatus() == OrderEnums.FINSH.getCode()
                || detail.getOrderStatus() == OrderEnums.CANCEL.getCode()){
            return ResultResponse.fail(OrderEnums.FINSH_CANCEL.getMsg());
        }
        // 修改订单状态
        detail.setOrderStatus(OrderEnums.CANCEL.getCode());
        // 取出订单
        List<OrderDetail> byOrderId = orderDetailRepository.findByOrderId(orderId);
        // 遍历订单
        for (OrderDetail orderDetail : byOrderId) {
            ResultResponse<ProductInfo> queryById = productInfoService.queryById(orderDetail.getProductId());
            // 获取商品数据
            ProductInfo productInfo = queryById.getData();
            // 还原库存
            productInfo.setProductStock(productInfo.getProductStock() + orderDetail.getProductQuantity());
            // 添加到数据库
            productInfoService.updateProduct(productInfo);
        }
        orderMasterRepository.save(detail);
        return ResultResponse.success(OrderEnums.CANCEL.getMsg());
    }



}
