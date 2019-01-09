package com.pinyougou.page.service.impl;

import com.pinyougou.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class MyMessageListener implements MessageListener {

    @Autowired
    ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage)message;
        try {
            String text = textMessage.getText();
            boolean b = itemPageService.genItemHtml(Long.parseLong(text));
            System.out.println("使用activemq生成静态页面"+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
