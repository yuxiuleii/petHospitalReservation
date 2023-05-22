package com.example.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Menu;
import com.example.mapper.MenuMapper;
import com.example.service.MenuService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Override
    public boolean saveMenu(Menu menu) {
        return saveOrUpdate(menu);
    }

    @Override
    public List<Menu> findMenus(String name) {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(Strings.isNotEmpty(name),"name",name);
        //  查询所有数据
        List<Menu> list =list(queryWrapper);
        //  找出所有父节点为null的节点，形成一级菜单
        List<Menu> parentNodes = list.stream().filter(menu -> menu.getPid() == null).collect(Collectors.toList());
        // 找出一级菜单的所有子菜单
        for (Menu menuFirst : parentNodes) {
            //  筛选满足 一级菜单的id==二级菜单的pid ，则将二级菜单加入一级菜单的children中
            menuFirst.setChildren(list.stream().filter(menuSecond -> menuFirst.getId().equals(menuSecond.getPid())).collect(Collectors.toList()));
        }
        return parentNodes;
    }
}
