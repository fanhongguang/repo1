package cn.itcast.core.service;

import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.util.IdWorker;
import com.pinyougou.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    SeckillGoodsDao seckillGoodsDao;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    IdWorker idWorker;

    @Override
    public void submitOrder(String username, Long seckillId) {
        //1.查询缓存中数据
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
        if(seckillGoods==null){
            throw new RuntimeException("商品不存在");
        }
        if (seckillGoods.getStockCount()<=0){
            throw new RuntimeException("商品已经被抢光");
        }

        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        redisTemplate.boundHashOps("seckillGoods").put(seckillGoods,seckillGoods);
        if (seckillGoods.getStockCount()==0){
            System.out.println("更新到数据库中");
            seckillGoodsDao.updateByPrimaryKey(seckillGoods);
            redisTemplate.boundHashOps("seckillGoods").delete(seckillId);
        }

        //将数据保存到订单上
        SeckillOrder order = new SeckillOrder();
        order.setId(idWorker.nextId());
        order.setSeckillId(seckillId);
        order.setMoney(seckillGoods.getCostPrice());
        order.setUserId(username);
        order.setSellerId(seckillGoods.getSellerId());
        order.setCreateTime(new Date());
        order.setStatus("0");
        System.out.println("将订单存储到redis中");
        redisTemplate.boundHashOps("seckillOrder").put(username,order);
    }
}
