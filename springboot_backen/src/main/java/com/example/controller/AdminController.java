package com.example.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.dto.ScheduleDto;
import com.example.entity.*;
import com.example.mapper.*;
import com.example.service.UserService;
import com.example.vo.ActivityVO;
import com.example.vo.DoctorVO;
import com.example.vo.TreatVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.print.Doc;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private DoctorMapper doctorMapper;
    @Resource
    private DepDoctMapper depDoctMapper;
    @Resource
    private DoctActMapper doctActMapper;
    @Resource
    private ActivityMapper activityMapper;
    @Autowired
    private UserService userService;
    @Resource
    private ScheduleMapper scheduleMapper;
    @GetMapping("/departments")
    public Result getDepartments(){
        return Result.success(departmentMapper.selectList(null));
    }
    @GetMapping("/doctors")
    public Result getDoctors(@RequestParam Integer pageNum,
                             @RequestParam Integer pageSize){
        IPage<Doctor> page = new Page<>(pageNum, pageSize);
        Page<Doctor> doctors = (Page<Doctor>)doctorMapper.selectPage(page,null);

        Page<DoctorVO> doctorVOPage = (Page<DoctorVO>) doctors.convert(res->{
            DoctorVO doctorVO = Convert.convert(DoctorVO.class,res);

            String department = departmentMapper.selectById(depDoctMapper.getDepartmentIdByDoctorId(doctorVO.getId())).getName();
            doctorVO.setDepartment(department);

            ArrayList<Activity> activities = new ArrayList<>();
            List<Integer> activitiesIds = doctActMapper.getActivitiesIdsByDoctorId(doctorVO.getId());
            for (Integer activityId : activitiesIds){
                Activity activity = activityMapper.selectById(activityId);
                activities.add(activity);
            }
            doctorVO.setActivities(activities);
            return doctorVO;
        });

        return Result.success(doctorVOPage);
    }
    @GetMapping("/activities")
    public Result getActivities(){
        IPage<Activity> page = new Page<>();
        Page<Activity> activities = (Page<Activity>)activityMapper.selectPage(page,null);
        Page<ActivityVO> activityVOPage = (Page<ActivityVO>) activities.convert(res->{
            ActivityVO activityVO = Convert.convert(ActivityVO.class,res);

            DoctAct doctAct = doctActMapper.getByActivityId(activityVO.getId());
            activityVO.setDoctor_id(doctAct.getDoctorId());
            activityVO.setDoctorName(doctorMapper.selectById(doctAct.getDoctorId()).getName());
            return activityVO;
        });
        return Result.success(activityVOPage);
    }

    @PostMapping("/saveDep")
    public Result saveDep(@RequestBody Department department){
        return Result.success(departmentMapper.updateById(department));
    }
    @PostMapping("/updateDoctor")
    @Transactional
    public Result saveDoctor(@RequestBody DoctorVO doctorVO){
        Doctor doctor = new Doctor();
        BeanUtil.copyProperties(doctorVO, doctor);
        doctorMapper.updateById(doctor);

        DepDoct depDoct = depDoctMapper.getByDoctorId(doctorVO.getId());
        depDoct.setDepartmentId(departmentMapper.getByName(doctorVO.getDepartment()).getId());
        QueryWrapper<DepDoct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("doctor_id", doctorVO.getId());
        depDoctMapper.update(depDoct, queryWrapper);

        return Result.success();
    }
    @PostMapping("/updateActivity/{id}/{name}/{doctorName}")
    @Transactional
    public Result updateActivity(@PathVariable Integer id, @PathVariable String name,@PathVariable String doctorName){
        Activity activity = new Activity();
        activity.setId(id);
        activity.setName(name);
        activityMapper.updateById(activity);

        DoctAct doctAct = doctActMapper.getByActivityId(id);
        doctAct.setDoctorId(doctorMapper.getByName(doctorName).getId());
        QueryWrapper<DoctAct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("activity_id", id);
        doctActMapper.update(doctAct, queryWrapper);

        return Result.success();
    }

    @PostMapping("/addDep/{name}/{path}")
    public Result addDep(@PathVariable String name,@PathVariable String path){
        System.out.println(name);
        System.out.println(path);
        Department department = new Department();
        department.setName(name);
        department.setPath(path);
        return Result.success(departmentMapper.insert(department));
    }

    @PostMapping("/addActivity/{name}/{doctorName}")
    @Transactional
    public Result addActivity(@PathVariable String name,@PathVariable String doctorName){
        Activity activity = new Activity();
        activity.setName(name);
        activityMapper.insert(activity);

        Activity lastOne = activityMapper.getLastOne();
        DoctAct doctAct = new DoctAct();
        doctAct.setActivityId(lastOne.getId());
        doctAct.setDoctorId(doctorMapper.getByName(doctorName).getId());
        doctActMapper.insert(doctAct);

        return Result.success();
    }
    @DeleteMapping("/delDep/{id}")
    public Result delDep(@PathVariable Integer id){
        return Result.success(departmentMapper.deleteById(id));
    }

    @DeleteMapping("/delActivity/{id}")
    @Transactional
    public Result delActivity(@PathVariable Integer id){
        activityMapper.deleteById(id);

        QueryWrapper<DoctAct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("activity_id", id);
        doctActMapper.delete(queryWrapper);

        return Result.success();
    }

    @PostMapping("/addDoctor")
    @Transactional
    public Result addDoctor(@RequestBody DoctorVO doctorVO){
        Doctor doctor = new Doctor();
        BeanUtil.copyProperties(doctorVO, doctor);
        doctorMapper.insert(doctor);

        User user = new User();
        user.setUsername(doctorVO.getUsername());
        user.setRealname(doctorVO.getName());
        user.setPassword("123456");
        user.setAvatarUrl(doctorVO.getAvatarUrl());
        if (doctorVO.getIsVisit() == 1){
            user.setRole("ROLE_DOCTOR1");
        } else {
            user.setRole("ROLE_DOCTOR2");
        }
        userService.save(user);

        DepDoct depDoct = new DepDoct();
        Doctor lastOne = doctorMapper.getLastOne();
        depDoct.setDoctorId(lastOne.getId());
        depDoct.setDepartmentId(departmentMapper.getByName(doctorVO.getDepartment()).getId());
        depDoctMapper.insert(depDoct);

        return Result.success();

    }
    @GetMapping("/getDoctors/{departmentName}")
    public Result getDoctors(@PathVariable String departmentName){
        Department department = departmentMapper.getByName(departmentName);
        List<Integer> allDoctorsId = depDoctMapper.getAllDoctorsId(department.getId());
        List<Doctor> list = new ArrayList<>();
        for (Integer doctorId : allDoctorsId){
            Doctor doctor = doctorMapper.selectById(doctorId);
            list.add(doctor);
        }

        return Result.success(list);
    }
    @GetMapping("/getSchedule/{id}")
    public Result getSchedule(@PathVariable Integer id){
        QueryWrapper<Schedule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("doctor_id", id);

        return Result.success(scheduleMapper.selectList(queryWrapper));
    }

    @PostMapping("/addSchedule")
    public Result addSchedule(@RequestBody ScheduleDto scheduleDto){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = inputFormat.parse(scheduleDto.getScheTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, 8);
            Date resultDate = calendar.getTime();
            String scheTimeString = outputFormat.format(resultDate);
            Date scheTime = outputFormat.parse(scheTimeString);

            Schedule schedule = new Schedule();

            Doctor doctor = doctorMapper.getByName(scheduleDto.getDoctorName());
            schedule.setDoctorId(doctor.getId());
            schedule.setDoctorName(scheduleDto.getDoctorName());
            schedule.setScheTime(scheTime);
            schedule.setPeriod1(scheduleDto.getPeriod1());
            schedule.setPeriod2(scheduleDto.getPeriod2());
            schedule.setPeriod3(scheduleDto.getPeriod3());
            schedule.setPeriod4(scheduleDto.getPeriod4());
            schedule.setPeriod5(scheduleDto.getPeriod5());
            schedule.setPeriod6(scheduleDto.getPeriod6());
            schedule.setPeriod7(scheduleDto.getPeriod7());
            schedule.setPeriod8(scheduleDto.getPeriod8());

            scheduleMapper.insert(schedule);
        }catch (ParseException e){
            e.printStackTrace();
        }

        return Result.success();
    }
    @DeleteMapping("/delSchedule/{id}")
    public Result delSchedule(@PathVariable Integer id){
        return Result.success(scheduleMapper.deleteById(id));
    }
    @PostMapping("/updateSchedule")
    public Result updateSchedule(@RequestBody ScheduleDto scheduleDto){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = inputFormat.parse(scheduleDto.getScheTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, 8);
            Date resultDate = calendar.getTime();
            String scheTimeString = outputFormat.format(resultDate);
            Date scheTime = outputFormat.parse(scheTimeString);

            Schedule schedule = new Schedule();

            schedule.setId(scheduleDto.getId());
            schedule.setDoctorId(scheduleDto.getDoctorId());
            schedule.setDoctorName(scheduleDto.getDoctorName());
            schedule.setScheTime(scheTime);
            schedule.setPeriod1(scheduleDto.getPeriod1());
            schedule.setPeriod2(scheduleDto.getPeriod2());
            schedule.setPeriod3(scheduleDto.getPeriod3());
            schedule.setPeriod4(scheduleDto.getPeriod4());
            schedule.setPeriod5(scheduleDto.getPeriod5());
            schedule.setPeriod6(scheduleDto.getPeriod6());
            schedule.setPeriod7(scheduleDto.getPeriod7());
            schedule.setPeriod8(scheduleDto.getPeriod8());

            scheduleMapper.updateById(schedule);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return Result.success();
    }
}
