package com.example.dto;

import com.example.entity.Menu;
import lombok.Data;

import java.util.List;
@Data
public class UserDto {

    private String username;
    private String email;
    private String phone;
    private String address;
    private String identity;
    private String realname;
    private String password;
    private String avatarUrl;
    private String token;
    private String role;
    private List<Menu> menus;
}
