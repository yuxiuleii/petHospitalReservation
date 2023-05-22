package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Role;

import java.util.List;

public interface RoleService extends IService<Role> {

    boolean saveRole(Role role);

    void setRoleMenu(Integer roleId, List<Integer> menuIds);

    List<Integer> getRoleMenu(Integer roleId);
}
