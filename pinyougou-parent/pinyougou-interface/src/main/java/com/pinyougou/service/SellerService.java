package com.pinyougou.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seller.Seller;

public interface SellerService {
    void add(Seller seller);

    PageResult search(Seller seller, Integer page, Integer rows);

    Seller findOne(String id);

    void updateStatus(String id, String status);


}
