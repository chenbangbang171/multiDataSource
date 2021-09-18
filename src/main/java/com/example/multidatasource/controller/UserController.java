package com.example.multidatasource.controller;

import com.example.multidatasource.annotation.DSKey;
import com.example.multidatasource.annotation.DataSourceType;
import com.example.multidatasource.annotation.UseDataSource;
import com.example.multidatasource.pojo.User;
import com.example.multidatasource.service.UserService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PutMapping("insert")
    @UseDataSource(DataSourceType.SOURCE_1)
    public String insert( String userId, String userName){
        User user = new User();
        user.setUserId(userId);
        user.setUserName(userName);
        userService.insert(user);
        return "success";
    }

    @PutMapping("insertHash")
    @UseDataSource(useHashKey = true)
    public String insertHash(@RequestParam @DSKey String userId, String userName){
        User user = new User();
        user.setUserId(userId);
        user.setUserName(userName);
        userService.insert(user);
        return "success";
    }

    @RequestMapping("queryAll")
    public List<User> queryAllUsers(){
        return userService.queryAll();
    }
}
