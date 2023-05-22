package com.example.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.Constants;
import com.example.dto.UserDto;
import com.example.entity.Menu;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.exception.ServiceException;
import com.example.mapper.RoleMapper;
import com.example.mapper.RoleMenuMapper;
import com.example.mapper.UserMapper;
import com.example.service.MenuService;
import com.example.service.UserService;
import com.example.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RoleMenuMapper roleMenuMapper;
    @Resource
    private MenuService menuService;
    @Override
    public User getUserByName(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);

        return getOne(queryWrapper);
    }

    @Override
    public boolean saveUser(User user) {
        return saveOrUpdate(user);
    }

    @Override
    public UserDto login(UserDto userDto) {

        QueryWrapper<User> queryWrapperUsername = new QueryWrapper<>();
        QueryWrapper<User> queryWrapperEmail = new QueryWrapper<>();

        queryWrapperUsername.eq("username",userDto.getUsername());
        queryWrapperUsername.eq("password",userDto.getPassword());

        queryWrapperEmail.eq("email", userDto.getEmail());
        queryWrapperEmail.eq("password",userDto.getPassword());

        User userWithUsername;
        User userWithEmail;
        try {
            userWithUsername = getOne(queryWrapperUsername);
            userWithEmail = getOne(queryWrapperEmail);

        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        if (userWithUsername != null){
            BeanUtil.copyProperties(userWithUsername, userDto, true);
            //  生成token并存入Dto中返回给前端
            String token = TokenUtils.genToken(userWithUsername.getId().toString(), userWithUsername.getPassword());
            userDto.setToken(token);

            String role = userWithUsername.getRole();  //查询出唯一标识（等于role表里的flag字段）
            System.out.println(role);
            //  设置用户的菜单列表
            List<Menu> roleMenus = getRoleMenus(role);
            userDto.setMenus(roleMenus);
            return userDto;
        } else if (userWithEmail != null) {
            BeanUtil.copyProperties(userWithEmail, userDto, true);
            //  生成token并存入Dto中返回给前端
            String token = TokenUtils.genToken(userWithEmail.getId().toString(), userWithEmail.getPassword());
            userDto.setToken(token);

            String role = userWithEmail.getRole();  //查询出唯一标识（等于role表里的flag字段）
            //  设置用户的菜单列表
            List<Menu> roleMenus = getRoleMenus(role);
            userDto.setMenus(roleMenus);
            return userDto;
        } else {
            throw new ServiceException(Constants.CODE_600, "用户名或密码错误");
        }


    }

    @Override
    public User register(UserDto userDto) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",userDto.getUsername());
        User user;
        try {
            user = getOne(queryWrapper);

        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        if (user == null){
            user = new User();
            BeanUtil.copyProperties(userDto, user, true);
            user.setCreateTime(DateUtil.date());
            save(user);
        }else {
            throw new ServiceException(Constants.CODE_600, "用户已存在");
        }
        return user;
    }

    /*
     * 获取当前角色的菜单列表
     * */
    private List<Menu> getRoleMenus(String roleFlag) {
        QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
        roleQueryWrapper.eq("flag", roleFlag);
        Integer roleId = roleMapper.selectOne(roleQueryWrapper).getId();
        //  当前角色的所有菜单id集合
        List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);

        //  查出系统所有的菜单
        List<Menu> menus = menuService.findMenus("");

        //  new一个list存放筛选通过的菜单
        ArrayList<Menu> roleMenus = new ArrayList<>();
        //  筛选当前用户角色的菜单
        for (Menu menu : menus) {
            if (menuIds.contains(menu.getId())){
                roleMenus.add(menu);
            }
            List<Menu> children = menu.getChildren();
            children.removeIf(child -> !menuIds.contains(child.getId()));
        }
        return roleMenus;
    }
}
