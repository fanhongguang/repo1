package com.pinyougou.solrutil;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    ItemDao itemDao;
    @Autowired
    private SolrTemplate solrTemplate;

    public void importItemData(){
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andStatusEqualTo("1");
        List<Item> itemList = itemDao.selectByExample(query);
        System.out.println("商品列表");
        if (itemList!=null) {
            for (Item item : itemList) {
                String spec = item.getSpec();
                Map map = JSON.parseObject(spec,Map.class);
                item.setSpecMap(map);
            }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
        System.out.println("商品列表结束");
        }

    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtli = (SolrUtil) context.getBean("solrUtil");
        solrUtli.importItemData();
    }

}
