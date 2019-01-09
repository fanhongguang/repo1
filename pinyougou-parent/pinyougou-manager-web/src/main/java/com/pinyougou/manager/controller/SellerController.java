package com.pinyougou.manager.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.SellerService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("seller")
public class SellerController {

    @Reference
    SellerService sellerService;

    @RequestMapping("/search")
    public PageResult search(@RequestBody Seller seller,Integer page,Integer rows){
        PageResult pageResult = sellerService.search(seller,page,rows);
        return pageResult;
    }

    @RequestMapping("/findOne")
    public Seller findOne(String id) throws Exception {
        String ids = new String(id.getBytes("ISO8859-1"), "UTF-8");
        System.out.println(ids);
        Seller seller = sellerService.findOne(ids);
        return seller;
    }

    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId,String status) throws Exception {
        String ids = new String(sellerId.getBytes("ISO8859-1"), "UTF-8");
        System.out.println(ids);
        System.out.println(status);
        try {
            sellerService.updateStatus(ids,status);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }
}
