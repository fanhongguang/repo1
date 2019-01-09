package com.pinyougou.task;

import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class Task {

    @Autowired
    SeckillGoodsDao seckillGoodsDao;
    @Autowired
    RedisTemplate redisTemplate;

    @Scheduled(cron = "* * * * * ?")
    public void task(){
        System.out.println("执行了任务调度"+new Date());
        List keys = new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys());
        SeckillGoodsQuery query = new SeckillGoodsQuery();
        SeckillGoodsQuery.Criteria criteria = query.createCriteria();
        criteria.andStatusEqualTo("1");
        criteria.andStockCountGreaterThan(0);
        criteria.andStartTimeLessThanOrEqualTo(new Date());
        criteria.andEndTimeGreaterThanOrEqualTo(new Date());
        if (keys.size()>0){
            System.out.println(keys.toString());
            criteria.andIdNotIn(keys);
        }
        List<SeckillGoods> seckillGoodsList = seckillGoodsDao.selectByExample(query);
        System.out.println(seckillGoodsList.size());

        for (SeckillGoods seckillGoods : seckillGoodsList) {
            redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(),seckillGoods);
            System.out.println("更新的商品为id为"+seckillGoods.getId());
        }

    }

}
