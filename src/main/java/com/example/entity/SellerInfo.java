package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity//表示该类为实体类
@Data  // 相当于 Get Set ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "seller_info")
public class SellerInfo implements Serializable {

    @Id  // 主键
    private String id;

    private String username;
    private String password;
    private String openid;
    private Date createTime;
    private Date updateTime;

}
