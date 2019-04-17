package com.example.service;

import com.example.common.ResultResponse;
import org.springframework.stereotype.Service;

@Service
public interface ProductInfoService {
    ResultResponse queryList();
}
