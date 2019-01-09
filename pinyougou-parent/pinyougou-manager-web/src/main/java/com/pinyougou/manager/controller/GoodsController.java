package com.pinyougou.manager.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.item.Item;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    GoodsService goodsService;
    /*@Reference
    ItemSearchService itemSearchService;*/
    //@Reference
    //ItemPageService itemPageService;
    @Autowired
    JmsTemplate jmsTemplate;
    @Autowired
    Destination queueTextDestination;
    @Autowired
    Destination topicPageDestination;
    @Autowired
    Destination topicPageDeleteDestination;

    @RequestMapping("/search")
    public PageResult search(@RequestBody Goods goods,Integer page,Integer rows){
        System.out.println(goods);
        PageResult pageResult = goodsService.search(page,rows,goods);
        return pageResult;
    }

    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status){
        try {
            goodsService.updateStatus(ids,status);
            if(status.equals("1")){//审核通过
                List<Item> itemList = goodsService.findItemListByGoodsIdAndStatus(ids, status);
                //调用搜索接口实现数据批量导入
                if(itemList.size()>0){
                    //itemSearchService.importList(itemList);
                    String itemListToJson = JSON.toJSONString(itemList);
                    jmsTemplate.send(queueTextDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(itemListToJson);
                        }
                    });
                    for (Long id : ids) {
                        //template(id);
                        jmsTemplate.send(topicPageDestination, new MessageCreator() {
                            @Override
                            public Message createMessage(Session session) throws JMSException {
                                return session.createTextMessage(id+"");
                            }
                        });
                    }
                }else{
                    System.out.println("没有明细数据");
                }
            }
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    @Autowired
    Destination queueTextDeleteDestination;

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            goodsService.delete(ids);
            //itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
            jmsTemplate.send(queueTextDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(ids);
                }
            });
            jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(ids);
                }
            });
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    //@RequestMapping("/template")
    /*public void template(Long goodsId){
        itemPageService.genItemHtml(goodsId);
    }*/

}
