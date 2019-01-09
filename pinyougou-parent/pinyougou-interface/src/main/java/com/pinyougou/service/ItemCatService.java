package com.pinyougou.service;

import cn.itcast.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {
    List<ItemCat> findByParentId(Long parentId);

    void add(ItemCat itemCat);

    ItemCat findOne(Long id);

    List<ItemCat> findAll();
}
