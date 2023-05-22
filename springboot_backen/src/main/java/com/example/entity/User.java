package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "user")
public class User {
    @TableId
    private Integer id;
    private String username;
    private String password;
    private String realname;
    private String phone;
    private String email;
    private String address;
    private String identity;
    private String avatarUrl;
    private String role;
    private Date createTime;
    @TableLogic
    private Integer deleted;

}
