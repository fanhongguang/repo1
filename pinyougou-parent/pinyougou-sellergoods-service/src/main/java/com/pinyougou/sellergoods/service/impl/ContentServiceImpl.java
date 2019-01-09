package com.pinyougou.sellergoods.service.impl;

import cn.itcast.core.dao.ad.ContentDao;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentQuery;
import cn.itcast.core.pojo.entity.PageResult;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
@Service
public class ContentServiceImpl implements ContentService {
    @Autowired
    private ContentDao contentDao;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Content> findAll() {
        List<Content> list = contentDao.selectByExample(null);
        return list;
    }

    @Override
    public PageResult findPage(Content content, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<Content> page = (Page<Content>)contentDao.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void add(Content content) {
        contentDao.insertSelective(content);
        redisTemplate.boundHashOps("Constants.REDIS_CONTENT").delete(content.getCategoryId());
        System.out.println("添加了删除缓存");
    }

    @Override
    public void edit(Content content) {
        Long categoryId = contentDao.selectByPrimaryKey(content.getId()).getCategoryId();
        redisTemplate.boundHashOps("Constants.REDIS_CONTENT").delete(categoryId);
        contentDao.updateByPrimaryKeySelective(content);
        if (categoryId.longValue()!=content.getCategoryId().longValue()){
            redisTemplate.boundHashOps("Constants.REDIS_CONTENT").delete(content.getCategoryId());
        }
        System.out.println("修改完了删除前后缓存");
    }

    @Override
    public Content findOne(Long id) {
        Content content = contentDao.selectByPrimaryKey(id);
        return content;
    }

    @Override
    public void delAll(Long[] ids) {
        if(ids != null){
            for(Long id : ids){
                Long categoryId = contentDao.selectByPrimaryKey(id).getCategoryId();
                redisTemplate.boundHashOps("Constants.REDIS_CONTENT").delete(categoryId);
                System.out.println("删除操作删除之前缓存");
                contentDao.deleteByPrimaryKey(id);
            }
        }
    }

    @Override
    public List<Content> findByCategoryId(Long id) {
        List<Content> contentList = (List<Content>) redisTemplate.boundHashOps("Constants.REDIS_CONTENT").get(id);
        if (contentList==null){
        System.out.println("缓存中没有从数据库中查出插入到缓存中");
        ContentQuery contentQuery = new ContentQuery();
        ContentQuery.Criteria criteria = contentQuery.createCriteria();
        criteria.andCategoryIdEqualTo(id);
        criteria.andStatusEqualTo("1");
        contentList = contentDao.selectByExample(contentQuery);
        redisTemplate.boundHashOps("Constants.REDIS_CONTENT").put(id,contentList);
        }else {
            System.out.println("从redis缓存中取");
        }
        return contentList;
    }

}