package com.example.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("订单项参数实体类") // swagger 参数的描述信息
public class OrderDetailDto implements Serializable {

    @NotBlank(message = "商品 id 不能为空")
    @ApiModelProperty(value = "商品 id",dataType = "String")
    private String productId;

    @NotNull(message = "商品数量不能为空")
    @Min(value = 1,message = "数量不能少于一件")
    @ApiModelProperty(value = "商品数量",dataType = "Integer")
    private Integer productQuantity;


}
