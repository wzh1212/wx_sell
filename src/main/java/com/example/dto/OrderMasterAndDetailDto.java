package com.example.dto;

import com.example.entity.OrderDetail;
import com.example.entity.OrderMaster;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderMasterAndDetailDto extends OrderMaster implements Serializable {

    private List<OrderDetail> orderDetailList;

    public static OrderMasterAndDetailDto build(OrderMaster orderMaster){
        OrderMasterAndDetailDto orderMasterAndDetailDto = new OrderMasterAndDetailDto();
        BeanUtils.copyProperties(orderMaster,orderMasterAndDetailDto);
        return orderMasterAndDetailDto;
    }
}
