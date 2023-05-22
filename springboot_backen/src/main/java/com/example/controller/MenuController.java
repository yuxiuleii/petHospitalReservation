package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Constants;
import com.example.common.Result;
import com.example.entity.Dict;
import com.example.entity.Menu;
import com.example.mapper.DictMapper;
import com.example.service.MenuService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/menu")

public class MenuController {
    @Autowired
    private MenuService menuService;

    @Resource
    private DictMapper dictMapper;

    @GetMapping
    public Result findAll(@RequestParam(required = false) String name) {
        return Result.success(menuService.findMenus(name));
    }

    @GetMapping("/ids")
    //  获取所有menu菜单的id，返回一个list<Integer>存放在data中
    public Result findAllIds() {
        return Result.success(menuService.list().stream().map(Menu::getId));
    }
    @PostMapping
    public Result save(@RequestBody Menu menu) {
        return Result.success(menuService.saveMenu(menu));
    }

    @DeleteMapping("/{name}")
    public Result delete(@PathVariable String name){
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",name);
        return Result.success(menuService.remove(queryWrapper));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<String> names){
        int num=names.size();
        for (String name : names){
            QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name",name);
            menuService.removeById(menuService.getOne(queryWrapper).getId());
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
        IPage<Menu> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(Strings.isNotEmpty(name),"name",name);

        return Result.success(menuService.page(page, queryWrapper));
    }

    @GetMapping("/icons")
    public Result getIcons() {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", Constants.DICT_TYPE_ICON);
        return Result.success(dictMapper.selectList(queryWrapper));
    }
}
