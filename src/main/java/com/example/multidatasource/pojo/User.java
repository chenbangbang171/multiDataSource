package com.example.multidatasource.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value ="tb_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("userId")
    private String userId;
    @TableField("userName")
    private  String userName;

}

