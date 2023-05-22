package com.example.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Constants;
import com.example.common.Result;
import com.example.dto.UserDto;
import com.example.entity.Pet;
import com.example.entity.User;
import com.example.entity.UserPet;
import com.example.mapper.UserPetMapper;
import com.example.service.PetService;
import com.example.service.UserService;
import com.example.utils.TokenUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Resource
    private UserPetMapper userPetMapper;
    @Autowired
    private PetService petService;


    @PostMapping
    public Result save(@RequestBody User user){
        System.out.println(user + "-----------------!!");
        return Result.success(userService.saveUser(user));
    }

    @DeleteMapping("/{username}")
    public Result delete(@PathVariable String username){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        return Result.success(userService.remove(queryWrapper));
    }
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<String> usernames){
        int num=usernames.size();
        for (String username : usernames){
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username",username);
            userService.removeById(userService.getOne(queryWrapper).getId());
            num--;
        }
        if (num==0){
            return Result.success(true);
        }
        return Result.success(false);
    }

    @GetMapping
    public Result findAll() {
        return Result.success(userService.list());
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(required = false) String username,
                           @RequestParam(required = false) String realname) {
        IPage<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(Strings.isNotEmpty(username),"username",username);
        queryWrapper.like(Strings.isNotEmpty(realname),"realname",realname);

        return Result.success(userService.page(page, queryWrapper));
    }

    @PostMapping("/login")
    public Result login(@RequestBody UserDto userDto){
        String username=userDto.getUsername();
        String password=userDto.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)){
            return Result.error(Constants.CODE_400, "参数错误");
        }
        return Result.success(userService.login(userDto));
    }

    @PostMapping("/register")
    public Result register(@RequestBody UserDto userDto) {
        String username=userDto.getUsername();
        String password=userDto.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)){
            return Result.error(Constants.CODE_400, "参数错误");
        }
        return Result.success(userService.register(userDto));
    }

    @GetMapping("/username/{username}")
    public Result findOne(@PathVariable String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return Result.success(userService.getOne(queryWrapper));
    }
    @GetMapping("/mypets/{username}")
    public Result getMyPets(@PathVariable String username) {
        User user = userService.getUserByName(username);
        Integer userId = user.getId();

        QueryWrapper<UserPet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<UserPet> userPetList = userPetMapper.selectList(queryWrapper);

        List<Pet> pets = new ArrayList<>();
        for (UserPet userPet : userPetList) {
            Pet pet = petService.getById(userPet.getPetId());
            pets.add(pet);
        }

        return Result.success(pets);
    }

    //    表格导出
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        List<User> list = userService.list();
//        浏览器导出
        ExcelWriter writer = ExcelUtil.getWriter(true);
//        设置中文表头
        writer.addHeaderAlias("username","用户名");
        writer.addHeaderAlias("role","角色");
        writer.addHeaderAlias("realname","姓名");
        writer.addHeaderAlias("phone","电话");
        writer.addHeaderAlias("email","邮箱");
        writer.addHeaderAlias("address","地址");
        writer.addHeaderAlias("identity","身份证");
        writer.setOnlyAlias(true);
        writer.write(list,true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息","UTF-8");
        response.setHeader("Content-Disposition","attachment;filename=" + fileName +".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }


    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader excelReader = ExcelUtil.getReader(inputStream);
        excelReader.addHeaderAlias("用户名","username");
        excelReader.addHeaderAlias("角色","role");
        excelReader.addHeaderAlias("姓名","realname");
        excelReader.addHeaderAlias("电话","phone");
        excelReader.addHeaderAlias("邮箱","email");
        excelReader.addHeaderAlias("地址","address");
        excelReader.addHeaderAlias("身份证","identity");
        List<User> list = excelReader.readAll(User.class);
        System.out.println(list);
        return Result.success(userService.saveBatch(list));
    }
}
