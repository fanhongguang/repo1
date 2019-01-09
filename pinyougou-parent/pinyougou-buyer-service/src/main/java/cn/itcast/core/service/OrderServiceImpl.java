package cn.itcast.core.service;

import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.entity.Cart;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.util.IdWorker;
import com.pinyougou.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderDao orderDao;
    @Autowired
    OrderItemDao orderItemDao;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    IdWorker idWorker;
    @Autowired
    PayLogDao payLogDao;

    @Override
    public void add(Order order) {
        //获取购物车列表
        List<Cart> list = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
        List<String> orderList = new ArrayList<>();
        double totalMany = 0;
        for (Cart cart : list) {
            Order order1 = new Order();
            long orderId = idWorker.nextId();
            order1.setOrderId(orderId);
            order1.setPaymentType(order.getPaymentType());
            order1.setStatus("1");
            order1.setCreateTime(new Date());
            order1.setUpdateTime(new Date());
            order1.setUserId(order.getUserId());
            order1.setReceiverAreaName(order.getReceiverAreaName());
            order1.setReceiverMobile(order.getReceiverMobile());
            order1.setReceiver(order.getReceiver());
            order1.setSourceType(order.getSourceType());
            order1.setSellerId(cart.getSellerId());
            order1.setPayment(new BigDecimal(0.1));

            double many = 0;
            for (OrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setSellerId(cart.getSellerId());
                orderItem.setId(idWorker.nextId());
                orderItem.setOrderId(orderId);
                many+=orderItem.getTotalFee().doubleValue();
                orderItem.setTotalFee(new BigDecimal(many));
                orderItemDao.insert(orderItem);
            }
            orderDao.insert(order1);
            orderList.add(orderId+"");
            totalMany+=many;
        }
        if("1".equals(order.getPaymentType())){//如果是微信支付
            PayLog payLog=new PayLog();
            String outTradeNo=  idWorker.nextId()+"";//支付订单号
            payLog.setOutTradeNo(outTradeNo);//支付订单号
            payLog.setCreateTime(new Date());//创建时间
            //订单号列表，逗号分隔
            String ids=orderList.toString().replace("[", "").replace("]", "").replace(" ", "");
            payLog.setOrderList(ids);//订单号列表，逗号分隔
            payLog.setPayType("1");//支付类型
            payLog.setTotalFee( (long)(totalMany*100 ) );//总金额(分)
            payLog.setTradeState("0");//支付状态
            payLog.setUserId(order.getUserId());//用户ID
            payLogDao.insertSelective(payLog);//插入到支付日志表
            redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);//放入缓存
        }
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());
    }

    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        //1.修改支付日志状态
        PayLog payLog = payLogDao.selectByPrimaryKey(out_trade_no);
        payLog.setPayTime(new Date());
        payLog.setTradeState("1");//已支付
        payLog.setTransactionId(transaction_id);//交易号
        payLogDao.updateByPrimaryKey(payLog);
        //2.修改订单状态
        String orderList = payLog.getOrderList();//获取订单号列表
        String[] orderIds = orderList.split(",");//获取订单号数组

        for(String orderId:orderIds){
            Order order = orderDao.selectByPrimaryKey( Long.parseLong(orderId) );
            if(order!=null){
                order.setStatus("2");//已付款
                orderDao.updateByPrimaryKey(order);
            }
        }
        //清除redis缓存数据
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }

    @Override
    public PayLog searchPayLogFromRedis(String userId) {
        return (PayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Override
    public void del() {
        redisTemplate.boundHashOps("payLog").delete("admin");
    }

    @Override
    public SeckillOrder searchSeckOrderFromRedis(String name) {
        return (SeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(name);
    }

}
