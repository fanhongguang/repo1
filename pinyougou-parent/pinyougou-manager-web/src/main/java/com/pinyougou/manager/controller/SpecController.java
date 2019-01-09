package com.pinyougou.manager.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.entity.SpecificationEntity;
import cn.itcast.core.pojo.specification.Specification;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.SpecService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecController {

    @Reference
    SpecService specService;

    @RequestMapping("/search")
    public PageResult search(Integer page,Integer rows,@RequestBody Specification specName){
        PageResult pageResult = specService.search(page,rows,specName);
        return pageResult;
    }

    @RequestMapping("/add")
    public Result save(@RequestBody SpecificationEntity specificationEntity){
        try {
            specService.save(specificationEntity);
            return new Result(true,"成");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"shiba");
        }

    }
    @RequestMapping("/findOne")
    public SpecificationEntity findOne(Long id){
        SpecificationEntity specificationEntity = specService.findOne(id);
        return specificationEntity;
    }

    @RequestMapping("/delete")
    public Result dele(Long[] ids){
        try {
            specService.dele(ids);
            return new Result(true,"success");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"error");
        }
    }
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        List<Map> mapList = specService.selectOptionList();
        return mapList;
    }

}
