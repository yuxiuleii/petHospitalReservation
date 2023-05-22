package com.example.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.entity.Activity;
import com.example.entity.Department;
import com.example.entity.Doctor;

import com.example.entity.Schedule;
import com.example.mapper.*;
import com.example.service.DoctorService;
import com.example.vo.ScheduleVO;
import com.sun.javaws.IconUtil;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private DoctorService doctorService;
    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private DepDoctMapper depDoctMapper;
    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private DoctActMapper doctActMapper;
    @Resource
    private ScheduleMapper scheduleMapper;

    @GetMapping("/eye")
    public Result getEyeDoctors(@RequestParam Integer pageNum,
                                @RequestParam Integer pageSize) {
        IPage<Doctor> page = new Page<>(pageNum, pageSize);

        List<Activity> activities = activityMapper.getByName("眼科门诊");

        List<Integer> list = new ArrayList<>();
        for (Activity activity : activities){
            Integer id = doctActMapper.getAllDoctorsId(activity.getId());
            list.add(id);
        }


        QueryWrapper<Doctor> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", list);
        return Result.success(doctorService.page(page, queryWrapper));
    }
    @GetMapping("/weichuang")
    public Result getWeichuangDoctors(@RequestParam Integer pageNum,
                                @RequestParam Integer pageSize) {
        IPage<Doctor> page = new Page<>(pageNum, pageSize);

        List<Activity> activities = activityMapper.getByName("微创门诊");

        List<Integer> list = new ArrayList<>();
        for (Activity activity : activities){
            Integer id = doctActMapper.getAllDoctorsId(activity.getId());
            list.add(id);
        }


        QueryWrapper<Doctor> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", list);
        return Result.success(doctorService.page(page, queryWrapper));
    }

    @GetMapping("/schedule/{doctorId}")
    public Result getSchedule(@PathVariable Integer doctorId) {
        QueryWrapper<Schedule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("doctor_id", doctorId);
        List<Schedule> list = scheduleMapper.selectList(queryWrapper);
        List<ScheduleVO> listVO = new ArrayList<>();
        List<String> times = new ArrayList<>();
        ScheduleVO scheduleVO = new ScheduleVO();
        for (Schedule schedule : list) {
            scheduleVO = new ScheduleVO();
            BeanUtil.copyProperties(schedule, scheduleVO, true);

            times = new ArrayList<>();
            if (schedule.getPeriod1()>0) {
                times.add("9:00-10:00");
            }
            if (schedule.getPeriod2()>0) {
                times.add("10:00-11:00");
            }
            if (schedule.getPeriod3()>0) {
                times.add("11:00-12:00");
            }
            if (schedule.getPeriod4()>0) {
                times.add("13:00-14:00");
            }
            if (schedule.getPeriod5()>0) {
                times.add("14:00-15:00");
            }
            if (schedule.getPeriod6()>0) {
                times.add("15:00-16:00");
            }
            if (schedule.getPeriod7()>0) {
                times.add("16:00-17:00");
            }
            if (schedule.getPeriod8()>0) {
                times.add("17:00-18:00");
            }
            scheduleVO.setTimes(times);

            listVO.add(scheduleVO);
        }
        System.out.println(listVO);

        return Result.success(listVO);
    }

}
