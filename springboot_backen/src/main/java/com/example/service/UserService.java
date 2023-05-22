package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.UserDto;
import com.example.entity.User;

public interface UserService extends IService<User> {
    User getUserByName(String username);
    boolean saveUser(User user);
    UserDto login(UserDto userDto);

    User register(UserDto userDto);
}
