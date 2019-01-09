package com.pinyougou.sellergoods.service.impl;

import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;
import cn.itcast.core.pojo.seller.SellerQuery.Criteria;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SellerServiceImpl implements SellerService {

    @Autowired
    SellerDao sellerDao;


    @Override
    public void add(Seller seller) {
        seller.setStatus("0");

        sellerDao.insert(seller);
    }

    @Override
    public PageResult search(Seller seller, Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        SellerQuery query = new SellerQuery();
        Criteria createCriteria = query.createCriteria();
        if(seller != null){
            if(seller.getName() != null && !"".equals(seller.getName())){
                createCriteria.andNameLike("%"+seller.getName()+"%");
            }
            if(seller.getNickName() != null && !"".equals(seller.getNickName())){
                createCriteria.andNickNameLike("%"+seller.getNickName()+"%");
            }
            if(seller.getStatus() != null && !"".equals(seller.getStatus())){
                createCriteria.andStatusEqualTo(seller.getStatus());
            }
        }
        Page<Seller> sellerPage = (Page<Seller>)sellerDao.selectByExample(query);
        return new PageResult(sellerPage.getTotal(), sellerPage.getResult());
    }

    @Override
    public Seller findOne(String id) {
        Seller seller = sellerDao.selectByPrimaryKey(id);
        return seller;
    }

    @Override
    public void updateStatus(String id, String status) {
        Seller seller = new Seller();
        seller.setSellerId(id);
        seller.setStatus(status);
        sellerDao.updateByPrimaryKeySelective(seller);
    }


}
