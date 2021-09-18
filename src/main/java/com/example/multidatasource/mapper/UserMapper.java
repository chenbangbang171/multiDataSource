package com.example.multidatasource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.multidatasource.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper extends BaseMapper<User> {

    int insert(User user);

    List<User> queryAllUsers();
}
