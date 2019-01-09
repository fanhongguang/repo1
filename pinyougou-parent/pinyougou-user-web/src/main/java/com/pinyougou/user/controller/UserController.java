package com.pinyougou.user.controller;

import cn.itcast.core.pojo.user.User;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {


    @Reference
    UserService userService;

    @RequestMapping("/add")
        public void add(@RequestBody User user){
            userService.add(user);
        }


    @RequestMapping("/showname")
        public Map<Object, String> showName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<Object, String> map = new HashMap<>();
        map.put("loginName",name);
        return map;
    }

}
