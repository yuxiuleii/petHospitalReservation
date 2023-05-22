package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.entity.Role;
import com.example.service.RoleService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;
    @PostMapping
    public Result save(@RequestBody Role role) {
        return Result.success(roleService.saveRole(role));
    }

    @GetMapping
    public Result findAll() {
        return Result.success(roleService.list());
    }

    @DeleteMapping("/{name}")
    public Result delete(@PathVariable String name){
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",name);
        return Result.success(roleService.remove(queryWrapper));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<String> names){
        int num=names.size();
        for (String name : names){
            QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name",name);
            roleService.removeById(roleService.getOne(queryWrapper).getId());
            num--;
        }
        if (num==0){
            return Result.success(true);
        }
        return Result.success(false);
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(required = false) String name) {
        IPage<Role> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(Strings.isNotEmpty(name),"name",name);



        return Result.success(roleService.page(page, queryWrapper));
    }

    /*
    * 绑定角色和菜单的关系
    * */
    @PostMapping("/roleMenu/{roleId}")
    public Result setRoleMenu(@PathVariable Integer roleId, @RequestBody List<Integer> menuIds) {
        roleService.setRoleMenu(roleId, menuIds);
        return Result.success();
    }

    @GetMapping("/roleMenu/{roleId}")
    public Result getRoleMenu(@PathVariable Integer roleId) {
        return Result.success(roleService.getRoleMenu(roleId));
    }
}
