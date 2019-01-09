package com.pinyougou.seckill.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.OrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    WeixinPayService weixinPayService;
    @Reference
    OrderService orderService;

    @RequestMapping("/createNative")
    public Map createNative(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if (name.equals("anonymousUser")){
            throw new RuntimeException("请先登录");
        }
        SeckillOrder seckillOrder = orderService.searchSeckOrderFromRedis(name);
        if (seckillOrder.getId()!=null){
            Map map = weixinPayService.createNative(String.valueOf(seckillOrder.getId()), "1");
            return map;
        }else {
            return new HashMap<>();
        }

    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result = null;
        int i = 0;
        while (true){
            Map map = weixinPayService.queryPayStatus(out_trade_no);
            if (map==null){
                result = new Result(false,"支付出错");
                break;
            }
            if (map.get("trade_state").equals("SUCCESS")){
                result = new Result(false,"支付成功");
                break;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
            if (i>=5){
                result = new Result(false,"二维码超时");
                break;
            }
        }
        return result;

    }


}
