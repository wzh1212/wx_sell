package com.example.repository;

import com.example.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository包：持久层，装的是对类的操作
public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Integer> {

    //

}
