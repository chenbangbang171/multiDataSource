package com.example.multidatasource.service;


import com.example.multidatasource.pojo.User;

import java.util.List;

public interface UserService {

    void insert(User user);

    List<User> queryAll();
}
