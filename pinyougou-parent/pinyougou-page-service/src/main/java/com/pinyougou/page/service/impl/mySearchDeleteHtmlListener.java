package com.pinyougou.page.service.impl;

import com.pinyougou.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class mySearchDeleteHtmlListener implements MessageListener {

    @Autowired
    ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage)message;
        try {
            Long[] goodsIds = (Long[]) objectMessage.getObject();
            boolean b = itemPageService.deleteHtml(goodsIds);
            System.out.println("删除成功商品详情页"+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
