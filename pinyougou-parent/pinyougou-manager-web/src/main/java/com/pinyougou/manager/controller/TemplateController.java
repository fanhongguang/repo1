package com.pinyougou.manager.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.template.TypeTemplate;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.TemplateService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/typeTemplate")
public class TemplateController {

    @Reference
    TemplateService templateService;

    @RequestMapping("/search")
    public PageResult search(@RequestBody TypeTemplate typeTemplate, Integer page, Integer rows){
        PageResult search = templateService.search(typeTemplate, page, rows);
        return search;
    }

    @RequestMapping("/add")
    public Result save(@RequestBody TypeTemplate typeTemplate){
        try {
            templateService.save(typeTemplate);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        TypeTemplate typeTemplate = templateService.findOne(id);
        return typeTemplate;
    }
    @RequestMapping("/update")
    public Result update(@RequestBody TypeTemplate typeTemplate){
        try {
            templateService.update(typeTemplate);
            return new Result(true,"添加陈宫");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            templateService.delete(ids);
            return  new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return  new Result(false,"删除失败");

        }
    }
}
