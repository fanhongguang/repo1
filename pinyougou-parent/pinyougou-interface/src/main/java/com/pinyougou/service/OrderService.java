package com.pinyougou.service;

import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.seckill.SeckillOrder;

public interface OrderService {
    void add(Order order);
    //out_trade_no订单号
    //transaction_id微信返回的交易流水号
    void updateOrderStatus(String out_trade_no, String transaction_id);

    PayLog searchPayLogFromRedis(String userId);

    void del();

    SeckillOrder searchSeckOrderFromRedis(String name);
}
