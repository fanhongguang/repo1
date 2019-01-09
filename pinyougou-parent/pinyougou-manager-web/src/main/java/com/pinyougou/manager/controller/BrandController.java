package com.pinyougou.manager.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Brand;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    BrandService brandService;

    @RequestMapping("/findAll")
    public List<Brand> findAll(){
        List<Brand> list = brandService.findAll();
        return list;
    }

    @RequestMapping("/search")
    public PageResult findByPage(Integer page,Integer rows){
        PageResult pageResult = brandService.findByPage(page, rows);
        return pageResult;
    }

    @RequestMapping("/save")
    public Result save(@RequestBody Brand brand){
        try {
            brandService.save(brand);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(true,"添加失败");
        }
    }
    @RequestMapping("/findById")
    public Brand findById(Long id){
        Brand brand = brandService.findOne(id);
        return brand;
    }

    @RequestMapping("/update")
    public Result update(@RequestBody Brand brand){
        try {
            brandService.update(brand);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }

    }
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.dele(ids);
            return new Result(true,"添加成功");
        }catch (Exception e){
            return new Result(false,"失败");
        }

    }
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        List<Map> mapList = brandService.selectOptionList();
        return mapList;
    }

}
