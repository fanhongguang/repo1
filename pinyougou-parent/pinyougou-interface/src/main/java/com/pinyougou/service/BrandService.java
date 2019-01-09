package com.pinyougou.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    public List<Brand> findAll();

    PageResult findByPage(Integer page, Integer rows);

    void save(Brand brand);

    Brand findOne(Long id);

    void update(Brand brand);

    void dele(Long[] ids);

    List<Map> selectOptionList();
}
