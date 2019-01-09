package com.pinyougou.sellergoods.service.impl;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.SpecificationEntity;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecServiceImpl implements SpecService {

    @Autowired
    SpecificationDao specificationDao;
    @Autowired
    SpecificationOptionDao specificationOptionDao;
    @Override
    public PageResult search(Integer page, Integer rows, Specification specName) {
        PageHelper.startPage(page,rows);
        SpecificationQuery specificationQuery = new SpecificationQuery();
        SpecificationQuery.Criteria criteria = specificationQuery.createCriteria();

        if (specName.getSpecName()!=null) {
            criteria.andSpecNameLike("%" + specName.getSpecName() + "%");
        }
        Page<Specification> list = (Page<Specification>)specificationDao.selectByExample(specificationQuery);
        return new PageResult(list.getTotal(),list.getResult());
    }

    @Override
    public void save(SpecificationEntity specificationEntity) {
        Specification specification = specificationEntity.getSpecification();
        specificationDao.insert(specification);
        List<SpecificationOption> list = specificationEntity.getSpecificationOptionList();
        for (SpecificationOption option : list) {
            option.setSpecId(specification.getId());
            specificationOptionDao.insert(option);
        }
    }

    @Override
    public SpecificationEntity findOne(Long id) {
        Specification specification = specificationDao.selectByPrimaryKey(id);
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(id);

        List<SpecificationOption> specificationOptionList = specificationOptionDao.selectByExample(query);
        SpecificationEntity specificationEntity = new SpecificationEntity();
        specificationEntity.setSpecification(specification);
        specificationEntity.setSpecificationOptionList(specificationOptionList);

        return specificationEntity;
    }

    @Override
    public void dele(Long[] ids) {
        for (Long id : ids) {
            specificationDao.deleteByPrimaryKey(id);
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            SpecificationOptionQuery.Criteria criteria = query.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionDao.deleteByExample(query);
        }
    }

    @Override
    public List<Map> selectOptionList() {
        List<Map> mapList = specificationDao.selectOptionList();
        return mapList;
    }
}
