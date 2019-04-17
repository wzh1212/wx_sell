package com.example.dao.impl;

import com.example.dao.BatchDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

public class BatchDaoImpl<T> implements BatchDao<T> {

    @PersistenceContext
    protected EntityManager em;

    @Override
    @Transactional
    public void batchInsert(List<T> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            em.persist(list.get(i));
            //每100条执行一次写入数据库操作
            if (i % 100 == 0 || i == size -1){
                em.flush();
                em.clear();
            }
        }
    }

}
