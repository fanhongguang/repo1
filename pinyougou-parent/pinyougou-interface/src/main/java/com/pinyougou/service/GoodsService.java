package com.pinyougou.service;

import cn.itcast.core.pojo.entity.Goods_Desc;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.item.Item;

import java.util.List;

public interface GoodsService {

    void save(Goods_Desc goods_desc);

    PageResult search(Integer page, Integer rows, Goods goods);

    Goods_Desc findOne(Long id);

    void update(Goods_Desc goods_desc);

    void updateStatus(Long[] ids,String status);

    void delete(Long[] ids);

    /**
     * 根据商品ID和状态查询Item表信息
     * @param
     * @param status
     * @return
     */
    public List<Item> findItemListByGoodsIdAndStatus(Long[] goodsIds, String status );
}
