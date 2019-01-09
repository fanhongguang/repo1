package com.pinyougou.service;

import cn.itcast.core.pojo.seckill.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {
    List<SeckillGoods> findList();

    SeckillGoods findOneFromRedis(Long id);

}
