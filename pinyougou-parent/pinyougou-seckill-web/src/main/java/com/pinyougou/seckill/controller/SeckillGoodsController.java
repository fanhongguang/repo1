package com.pinyougou.seckill.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.SeckillGoodsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {


    @Reference
    SeckillGoodsService seckillGoodsService;

    @RequestMapping("/findList")
    public List<SeckillGoods> findList(){
        List<SeckillGoods> list = seckillGoodsService.findList();
        return list;
    }

    @RequestMapping("/findOneFromRedis")
    public SeckillGoods findOne(Long id){
        SeckillGoods seckillGoods = seckillGoodsService.findOneFromRedis(id);
        return seckillGoods;
    }



}
