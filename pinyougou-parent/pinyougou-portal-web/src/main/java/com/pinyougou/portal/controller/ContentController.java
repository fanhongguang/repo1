package com.pinyougou.portal.controller;

import cn.itcast.core.pojo.ad.Content;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.ContentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    ContentService contentService;

    @RequestMapping("/findByCategoryId")
    public List<Content> findByCategoryId(Long categoryId){
        List<Content> list = contentService.findByCategoryId(categoryId);
        return list;
    }


}
