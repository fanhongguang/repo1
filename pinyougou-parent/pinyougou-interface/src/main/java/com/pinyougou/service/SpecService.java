package com.pinyougou.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.SpecificationEntity;
import cn.itcast.core.pojo.specification.Specification;

import java.util.List;
import java.util.Map;

public interface SpecService {
    PageResult search(Integer page, Integer rows, Specification specName);

    void save(SpecificationEntity specificationEntity);

    SpecificationEntity findOne(Long id);

    void dele(Long[] ids);

    List<Map> selectOptionList();
}
