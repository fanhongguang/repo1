package com.pinyougou.portal.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.log.PayLog;
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

    /**
     * 生成二维码
     * @return
     */


    @RequestMapping("/del")
    public void del(){
        orderService.del();
    }


    @RequestMapping("/createNative")
    public Map createNative(){
        //获取当前用户
        String userId= SecurityContextHolder.getContext().getAuthentication().getName();
        //到redis查询支付日志
        PayLog payLog = orderService.searchPayLogFromRedis(userId);
        //判断支付日志存在
        if(payLog!=null){
            return weixinPayService.createNative(payLog.getOutTradeNo(),"1");
        }else{
            return new HashMap();
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
                result = new Result(true,"支付成功");
                orderService.updateOrderStatus(out_trade_no, String.valueOf(map.get("transaction_id")));
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
