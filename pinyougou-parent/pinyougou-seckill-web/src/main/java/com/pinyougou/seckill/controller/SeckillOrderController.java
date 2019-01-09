package com.pinyougou.seckill.controller;

import cn.itcast.core.pojo.entity.Result;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {


    @Reference
    SeckillOrderService seckillOrderService;

    @RequestMapping("/submitOrder")
    public Result submitOrder(Long seckillId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(username.equals("anonymousUser")){
            return new Result(false,"用户未登录请去登录");
        }
        try {
            seckillOrderService.submitOrder(username,seckillId);
            return new Result(true,"提交成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(true,"提交失败");

        }

    }
}
