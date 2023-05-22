package com.example.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.Result;
import com.example.entity.*;
import com.example.mapper.*;
import com.example.service.AppointService;
import com.example.service.PetService;
import com.example.service.UserService;
import com.example.vo.RecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    private PetService petService;
    @Autowired
    private UserService userService;
    @Resource
    private UserPetMapper userPetMapper;
    @Autowired
    private AppointService appointService;
    @Resource
    private AppoVisitMapper appoVisitMapper;
    @Resource
    private AppoTreatMapper appoTreatMapper;
    @Resource
    private VisitMapper visitMapper;
    @Resource
    private TreatMapper treatMapper;

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id){
        return Result.success(petService.getById(id));
    }
    @GetMapping("/getByName/{petName}")
    public Result getByName(@PathVariable String petName){
        QueryWrapper<Pet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", petName);
        Pet pet = petService.getOne(queryWrapper);
        return Result.success(pet);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Pet pet){
        return Result.success(petService.updateById(pet));
    }

    @PostMapping("/{username}")
    @Transactional
    public Result save(@PathVariable String username, @RequestBody Pet pet){
        //  存储宠物信息到pet表
        petService.savePet(pet);


           //获取用户
        User user = userService.getUserByName(username);

           //获取新增宠物信息
        QueryWrapper<Pet> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.last("limit 1");
        Pet newPet = petService.getOne(queryWrapper);

        // 用户和宠物的关系表userPet也要添加记录
        UserPet userPet = new UserPet();
        userPet.setUserId(user.getId());
        userPet.setPetId(newPet.getId());
        userPetMapper.insert(userPet);

        System.out.println(username);
        return Result.success();
    }
    @GetMapping("/mypets/{username}")
    public Result getPetsByUsername(@PathVariable String username) {
        User user = userService.getUserByName(username);
        QueryWrapper<UserPet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        List<UserPet> userPets = userPetMapper.selectList(queryWrapper);

        List<Pet> list =new ArrayList<>();
        for (UserPet userPet : userPets) {
            Pet pet = petService.getById(userPet.getPetId());
            list.add(pet);
        }

        return Result.success(list);
    }

    @GetMapping("/record/{realname}/{petId}")
    public Result getRecord(@PathVariable String realname,@PathVariable Integer petId){
        List<RecordVO> recordVOS = new ArrayList<>();

        String petName = petService.getById(petId).getName();

        QueryWrapper<Appoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("realname", realname);
        queryWrapper.eq("pet_name", petName);
        queryWrapper.eq("status", 1);
        List<Appoint> appoints = appointService.list(queryWrapper);
        for (Appoint appoint : appoints){
            RecordVO recordVO = new RecordVO();
            BeanUtil.copyProperties(appoint, recordVO);

            Visit visit = visitMapper.selectById(appoVisitMapper.getByAppointId(appoint.getId()).getVisitId());
            recordVO.setVisit(visit);

            List<Treat> treats = treatMapper.getByAppointId(appoint.getId());

            recordVO.setTreats(treats);

            recordVOS.add(recordVO);
        }

        return Result.success(recordVOS);
    }
}
