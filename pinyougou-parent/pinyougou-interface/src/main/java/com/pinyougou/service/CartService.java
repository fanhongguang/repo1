package com.pinyougou.service;

import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.entity.Cart;

import java.util.List;

public interface CartService {
    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);
    /**
     * 从redis中查询购物车
     * @param username
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     * 将购物车保存到redis
     * @param username
     * @param cartList
     */
    public void saveCartListToRedis(String username,List<Cart> cartList);

    List<Address> findListByLoginUser(String username);
}
