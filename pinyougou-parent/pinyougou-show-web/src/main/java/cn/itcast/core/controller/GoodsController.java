package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.Goods_Desc;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.GoodsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    GoodsService goodsService;

    @RequestMapping("/add")
    public Result save(@RequestBody Goods_Desc goods_desc){
        System.out.println(goods_desc.toString());
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        goods_desc.getGoods().setSellerId(name);
        try {
            goodsService.save(goods_desc);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"失败");
        }

    }
    @RequestMapping("/upadte")
    public Result update(@RequestBody Goods_Desc goods_desc){
        Goods_Desc one = goodsService.findOne(goods_desc.getGoods().getId());

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!name.equals(one.getGoods().getSellerId())&&!name.equals(goods_desc.getGoods().getSellerId())){
            new Result(false,"非法操作");
        }
        try {
            goodsService.update(goods_desc);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody Goods goods,Integer page, Integer rows){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(goods);
        System.out.println(name);
        goods.setSellerId(name);
        PageResult pageResult = goodsService.search(page,rows,goods);
        return pageResult;
    }

    @RequestMapping("/findOne")
    public Goods_Desc findOne(Long id){
        Goods_Desc goods =  goodsService.findOne(id);
        return goods;
    }
}
