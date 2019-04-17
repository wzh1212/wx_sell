package com.example;

import com.example.entity.ProductCategory;
import com.example.entity.SellerInfo;
import com.example.repository.ProductCategoryRepository;
import com.example.repository.SellerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

// spring 整合 JUnit4
@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Autowired
	private SellerRepository sellerRepository;

	@Autowired
	private ProductCategoryRepository productCategoryRepository;

	@Test
	public void contextLoads() {
		List<SellerInfo> all = sellerRepository.findAll();
		// 获取流遍历
		all.stream().forEach(System.out::println);

//		List<ProductCategory> all = productCategoryRepository.findAll();
//		all.stream().forEach(System.out::println);
	}


}
