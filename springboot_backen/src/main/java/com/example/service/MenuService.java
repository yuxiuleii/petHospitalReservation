package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {

    boolean saveMenu(Menu menu);

    List<Menu> findMenus(String name);
}
