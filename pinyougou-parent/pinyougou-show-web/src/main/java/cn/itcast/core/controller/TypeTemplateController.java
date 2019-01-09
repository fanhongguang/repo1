package cn.itcast.core.controller;

import cn.itcast.core.pojo.template.TypeTemplate;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.TemplateService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference
    TemplateService templateService;

    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        TypeTemplate template = templateService.findOne(id);
        return template;
    }


    @RequestMapping("/findBySpecList")
    public List<Map> findBySpecList(Long id){
        List<Map> list = templateService.findBySpecList(id);
        return list;
    }


}
