package com.pinyougou.user.service.impl;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Override
    public void add(User user) {
        user.setCreated(new Date());
        user.setUpdated(new Date());
        user.setSourceType("1");
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        userDao.insert(user);
    }
}
