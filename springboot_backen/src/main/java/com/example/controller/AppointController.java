package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.Result;
import com.example.entity.*;
import com.example.mapper.*;
import com.example.service.AppointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RestController
@RequestMapping("/appoint")
public class AppointController {

    @Resource
    private DepartmentMapper departmentMapper;
    @Autowired
    private AppointService appointService;
    @Resource
    private ScheduleMapper scheduleMapper;
    @Resource
    private DoctorMapper doctorMapper;
    @Resource
    private DepDoctMapper depDoctMapper;
    @Resource
    private UserMapper userMapper;
    @GetMapping("/allDepartments")
    public Result getAllDepartments() {
        return Result.success(departmentMapper.selectList(null));
    }
    @PostMapping("/{id}/{username}/{scheTime}/{doctorName}/{times}/{petName}")
    @Transactional
    public Result appoint(@PathVariable Integer id,
                           @PathVariable String username,
                           @PathVariable String scheTime,
                           @PathVariable String doctorName,
                           @PathVariable String times,
                           @PathVariable String petName) {

        System.out.println(scheTime);
        System.out.println(id);
        Schedule schedule = scheduleMapper.getById(id);

        System.out.println(schedule);
        System.out.println(schedule.getScheTime());

        Doctor doctor = doctorMapper.getByName(doctorName);
        Integer departmentId = depDoctMapper.getDepartmentIdByDoctorId(doctor.getId());
        Department department = departmentMapper.selectById(departmentId);
        User user = userMapper.getByUsername(username);

        Appoint appoint = new Appoint();
        appoint.setUsername(username);
        appoint.setRealname(user.getRealname());
        appoint.setPetName(petName);
        appoint.setDoctorName(doctorName);
        appoint.setDepartmentName(department.getName());
        appoint.setAppointTime(scheTime);
        appoint.setPeriod(times);

        Integer appointNumber = 0;
        switch (times) {
            case "9:00-10:00" :
                Integer period1 = schedule.getPeriod1();
                appointNumber = 6-period1+1;
                schedule.setPeriod1(period1-1);
                scheduleMapper.updateById(schedule);
                break;
            case "10:00-11:00" :
                Integer period2 = schedule.getPeriod2();
                appointNumber = 12-period2+1;
                schedule.setPeriod2(period2-1);
                scheduleMapper.updateById(schedule);
                break;
            case "11:00-12:00" :
                Integer period3 = schedule.getPeriod3();
                appointNumber = 18-period3+1;
                schedule.setPeriod3(period3-1);
                scheduleMapper.updateById(schedule);
                break;
            case "13:00-14:00" :
                Integer period4 = schedule.getPeriod4();
                appointNumber = 6-period4+1;
                schedule.setPeriod4(period4-1);
                scheduleMapper.updateById(schedule);
                break;
            case "14:00-15:00" :
                Integer period5 = schedule.getPeriod5();
                appointNumber = 12-period5+1;
                schedule.setPeriod5(period5-1);
                scheduleMapper.updateById(schedule);
                break;
            case "15:00-16:00" :
                Integer period6 = schedule.getPeriod6();
                appointNumber = 18-period6+1;
                schedule.setPeriod6(period6-1);
                scheduleMapper.updateById(schedule);
                break;
            case "16:00-17:00" :
                Integer period7 = schedule.getPeriod7();
                appointNumber = 24-period7+1;
                schedule.setPeriod7(period7-1);
                scheduleMapper.updateById(schedule);
                break;
            case "17:00-18:00" :
                Integer period8 = schedule.getPeriod8();
                appointNumber = 30-period8+1;
                schedule.setPeriod8(period8-1);
                scheduleMapper.updateById(schedule);
                break;
        }
        appoint.setAppointNumber(appointNumber);

        doctor.setAppointNumber(doctor.getAppointNumber()+1);
        doctorMapper.updateById(doctor);

        return Result.success(appointService.save(appoint));
    }

    @GetMapping("/myAppointments/{username}")
    public Result getMyAppointments(@PathVariable String username) {
        QueryWrapper<Appoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);

         return Result.success(appointService.list(queryWrapper));
    }
    @PostMapping("/cancel")
    @Transactional
    public Result cancelAppoint(@RequestBody Appoint appoint) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        System.out.println(appoint.getAppointTime());
        String scheTime = appoint.getAppointTime();
        try {
            Date date = outputFormat.parse(scheTime);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, -12);
            Date resultDate = calendar.getTime();
            String lastTime = outputFormat.format(resultDate);

            QueryWrapper<Schedule> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("doctor_name", appoint.getDoctorName());
            queryWrapper.eq("sche_time", lastTime);
            Schedule schedule = scheduleMapper.selectOne(queryWrapper);

            switch (appoint.getPeriod()) {
                case "9:00-10:00" :
                    schedule.setPeriod1(schedule.getPeriod1()+1);
                    scheduleMapper.update(schedule, queryWrapper);
                    appointService.removeById(appoint);
                    return Result.success();
                case "10:00-11:00" :
                    schedule.setPeriod2(schedule.getPeriod2()+1);
                    scheduleMapper.update(schedule, queryWrapper);
                    appointService.removeById(appoint);
                    return Result.success();
                case "11:00-12:00" :
                    schedule.setPeriod3(schedule.getPeriod3()+1);
                    scheduleMapper.update(schedule, queryWrapper);
                    appointService.removeById(appoint);
                    return Result.success();
                case "13:00-14:00" :
                    schedule.setPeriod4(schedule.getPeriod4()+1);
                    scheduleMapper.update(schedule, queryWrapper);
                    appointService.removeById(appoint);
                    return Result.success();
                case "14:00-15:00" :
                    schedule.setPeriod5(schedule.getPeriod5()+1);
                    scheduleMapper.update(schedule, queryWrapper);
                    appointService.removeById(appoint);
                    return Result.success();
                case "15:00-16:00" :
                    schedule.setPeriod6(schedule.getPeriod6()+1);
                    scheduleMapper.update(schedule, queryWrapper);
                    appointService.removeById(appoint);
                    return Result.success();
                case "16:00-17:00" :
                    schedule.setPeriod7(schedule.getPeriod7()+1);
                    scheduleMapper.update(schedule, queryWrapper);
                    appointService.removeById(appoint);
                    return Result.success();
                case "17:00-18:00" :
                    schedule.setPeriod8(schedule.getPeriod8()+1);
                    scheduleMapper.update(schedule, queryWrapper);
                    appointService.removeById(appoint);
                    return Result.success();
            }
        }catch (ParseException e){
            e.printStackTrace();
        }



//        QueryWrapper<Schedule> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("doctor_name", appoint.getDoctorName());
//        queryWrapper.eq("sche_time", appoint.getAppointTime());
//        Schedule schedule = scheduleMapper.selectOne(queryWrapper);

//        switch (appoint.getPeriod()) {
//            case "9:00-10:00" :
//                schedule.setPeriod1(schedule.getPeriod1()+1);
//                scheduleMapper.update(schedule, queryWrapper);
//                appointService.removeById(appoint);
//                return Result.success();
//            case "10:00-11:00" :
//                schedule.setPeriod2(schedule.getPeriod2()+1);
//                scheduleMapper.update(schedule, queryWrapper);
//                appointService.removeById(appoint);
//                return Result.success();
//            case "11:00-12:00" :
//                schedule.setPeriod3(schedule.getPeriod3()+1);
//                scheduleMapper.update(schedule, queryWrapper);
//                appointService.removeById(appoint);
//                return Result.success();
//            case "13:00-14:00" :
//                schedule.setPeriod4(schedule.getPeriod4()+1);
//                scheduleMapper.update(schedule, queryWrapper);
//                appointService.removeById(appoint);
//                return Result.success();
//            case "14:00-15:00" :
//                schedule.setPeriod5(schedule.getPeriod5()+1);
//                scheduleMapper.update(schedule, queryWrapper);
//                appointService.removeById(appoint);
//                return Result.success();
//            case "15:00-16:00" :
//                schedule.setPeriod6(schedule.getPeriod6()+1);
//                scheduleMapper.update(schedule, queryWrapper);
//                appointService.removeById(appoint);
//                return Result.success();
//            case "16:00-17:00" :
//                schedule.setPeriod7(schedule.getPeriod7()+1);
//                scheduleMapper.update(schedule, queryWrapper);
//                appointService.removeById(appoint);
//                return Result.success();
//            case "17:00-18:00" :
//                schedule.setPeriod8(schedule.getPeriod8()+1);
//                scheduleMapper.update(schedule, queryWrapper);
//                appointService.removeById(appoint);
//                return Result.success();
//        }
        return Result.error();
    }
}
