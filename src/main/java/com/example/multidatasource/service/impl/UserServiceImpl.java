package com.example.multidatasource.service.impl;

import com.example.multidatasource.mapper.UserMapper;
import com.example.multidatasource.pojo.User;
import com.example.multidatasource.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public void insert(User user) {
        userMapper.insert(user);
    }

    @Override
    public List<User> queryAll() {
        return userMapper.queryAllUsers();
    }
}
