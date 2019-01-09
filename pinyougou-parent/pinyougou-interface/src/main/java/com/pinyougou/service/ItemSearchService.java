package com.pinyougou.service;

import cn.itcast.core.pojo.item.Item;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    public Map<String,Object> search(Map searchMap);

    void importList(List<Item> itemList);

    void deleteByGoodsIds(List<Long> longs);
}
