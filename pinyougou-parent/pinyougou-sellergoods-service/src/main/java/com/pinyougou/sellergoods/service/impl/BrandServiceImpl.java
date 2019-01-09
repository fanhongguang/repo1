package com.pinyougou.sellergoods.service.impl;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    BrandDao brandDao;


    @Override
    public List<Brand> findAll() {
        List<Brand> list = brandDao.selectByExample(null);
        return list;
    }

    @Override
    public PageResult findByPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        Page<Brand> list = (Page<Brand>) brandDao.selectByExample(null);
        return new PageResult(list.getTotal(),list.getResult());
    }

    @Override
    public void save(Brand brand) {
        brandDao.insert(brand);
    }

    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKey(brand);
    }

    @Override
    public void dele(Long[] ids) {
        for (Long id : ids) {
            brandDao.deleteByPrimaryKey(id);
        }
    }

    @Override
    public List<Map> selectOptionList() {
        List<Map> mapList = brandDao.selectOptionList();
        return mapList;
    }

}
