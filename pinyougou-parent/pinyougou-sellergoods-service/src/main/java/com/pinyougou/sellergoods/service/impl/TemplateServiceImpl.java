package com.pinyougou.sellergoods.service.impl;

import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TemplateServiceImpl implements TemplateService {


    @Autowired
    TypeTemplateDao typeTemplateDao;
    @Autowired
    SpecificationOptionDao specificationOptionDao;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public PageResult search(TypeTemplate typeTemplate, Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
            TypeTemplateQuery query = new TypeTemplateQuery();
            TypeTemplateQuery.Criteria criteria = query.createCriteria();
        if (typeTemplate.getName()!=null){
            criteria.andNameLike("%"+typeTemplate.getName()+"%");
        }
            Page<TypeTemplate> list = (Page<TypeTemplate>) typeTemplateDao.selectByExample(query);
            saveToRedis();
        return new PageResult(list.getTotal(),list.getResult());
    }

    @Override
    public void save(TypeTemplate typeTemplate) {
        typeTemplateDao.insert(typeTemplate);
    }

    @Override
    public TypeTemplate findOne(Long id) {
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        return typeTemplate;
    }

    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateDao.updateByPrimaryKey(typeTemplate);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateDao.deleteByPrimaryKey(id);
        }
    }

    @Override
    public List<Map> findBySpecList(Long id) {
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        List<Map> mapList = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
        for (Map map : mapList) {
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            SpecificationOptionQuery.Criteria criteria = query.createCriteria();
            criteria.andSpecIdEqualTo(new Long((Integer)map.get("id")));
            List<SpecificationOption> optionList = specificationOptionDao.selectByExample(query);
            map.put("options",optionList);
        }
        return mapList;
    }

    @Override
    public List<TypeTemplate> findAll() {
        return typeTemplateDao.selectByExample(null);
    }

    private void saveToRedis(){
        List<TypeTemplate> typeTemplateList = findAll();
        for (TypeTemplate typeTemplate : typeTemplateList) {
            List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(),brandList);

            List<Map> specList = findBySpecList(typeTemplate.getId());
            redisTemplate.boundHashOps("specList").put(typeTemplate.getId(),specList);
        }
        System.out.println("将品牌数据和规格数据存储到缓存库");
    }
}
