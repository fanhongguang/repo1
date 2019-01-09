package com.pinyougou.search.service.impl;

import cn.itcast.core.pojo.item.Item;
import com.alibaba.fastjson.JSON;
import com.pinyougou.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class mySearchListener implements MessageListener{

    @Autowired
    ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage)message;
        try {
            String text = textMessage.getText();
            List<Item> itemList = JSON.parseArray(text, Item.class);
            itemSearchService.importList(itemList);
            System.out.println("使用activemq向索引库中存储");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
