package com.example.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.entity.*;
import com.example.mapper.*;
import com.example.service.AppointService;
import com.example.vo.RecordVO;
import com.example.vo.TreatVO;
import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/doctor")
public class DoctorController {
    @Autowired
    private AppointService appointService;
    @Resource
    private VisitMapper visitMapper;
    @Resource
    private AppoVisitMapper appoVisitMapper;
    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private DepDoctMapper depDoctMapper;
    @Resource
    private DoctorMapper doctorMapper;
    @Resource
    private DoctActMapper doctActMapper;
    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private TreatMapper treatMapper;
    @Resource
    private AppoTreatMapper appoTreatMapper;

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id){
        Doctor doctor = doctorMapper.selectById(id);
        return Result.success(doctor);
    }
    @GetMapping("/visits")
    public Result getTodayVisits(@RequestParam Integer pageNum,
                                 @RequestParam Integer pageSize,
                                 @RequestParam String doctorName){
        IPage<Appoint> page = new Page<>(pageNum, pageSize);

        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        Date todayDate =new Date();
        String DBDate = ft.format(todayDate)+" 12:00:00";

        QueryWrapper<Appoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("doctor_name", doctorName);
        queryWrapper.eq("appoint_time", DBDate);

        return Result.success(appointService.page(page, queryWrapper));
    }
    @GetMapping("/treats")
    public Result getTodayTreats(@RequestParam Integer pageNum,
                                 @RequestParam Integer pageSize,
                                 @RequestParam String doctorName){
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        Date todayDate =new Date();
        String DBDate = ft.format(todayDate)+" 12:00:00";

        IPage<Treat> page = new Page<>(pageNum, pageSize);

        QueryWrapper<Treat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("doctor_name", doctorName);
        queryWrapper.eq("appoint_time", DBDate);

        Page<Treat> treats = (Page<Treat>)treatMapper.selectPage(page,queryWrapper);

//        List<TreatVO> treatVOS = treats.getRecords().stream().map(res->{
//            TreatVO treatVO = Convert.convert(TreatVO.class,res);
//            Appoint appoint = appointService.getById(res.getAppointId());
//            treatVO.setRealname(appoint.getRealname());
//            treatVO.setPetName(appoint.getPetName());
//            treatVO.setVisitDoctorName(appoint.getDoctorName());
//            return treatVO;
//        }).collect(Collectors.toList());
        Page<TreatVO> treatVOPage = (Page<TreatVO>) treats.convert(res->{
            TreatVO treatVO = Convert.convert(TreatVO.class,res);
            Appoint appoint = appointService.getById(res.getAppointId());
            treatVO.setRealname(appoint.getRealname());
            treatVO.setPetName(appoint.getPetName());
            treatVO.setVisitDoctorName(appoint.getDoctorName());
            return treatVO;
        });
        return Result.success(treatVOPage);
    }

    @PostMapping("/saveVisit/{appointId}")
    @Transactional
    public Result saveVisit(@PathVariable Integer appointId, @RequestBody Visit visit){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Appoint appoint = appointService.getById(appointId);
            appoint.setStatus(1);
            appointService.updateById(appoint);

            // 修改格式为"yyyy-MM-dd HH:mm:ss"形式
            Date date = inputFormat.parse(visit.getHappenTime());
            // 增加8小时时差
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, 8);
            Date resultDate = calendar.getTime();
            String happenTime = outputFormat.format(resultDate);

            visit.setHappenTime(happenTime);
        }catch (ParseException e){
            e.printStackTrace();
        }
        visitMapper.insert(visit);

        //  获取最新记录
        Visit lastOne = visitMapper.getLastOne();

        //  关系表添加记录
        AppoVisit appoVisit = new AppoVisit();
        appoVisit.setAppointId(appointId);
        appoVisit.setVisitId(lastOne.getId());
        appoVisitMapper.insert(appoVisit);

        return Result.success();
    }

    @PostMapping("/saveTreat/{appointId}/{departmentId}/{doctorId}/{activityId}")
    @Transactional
    public Result saveTreat(@PathVariable Integer appointId,@PathVariable Integer departmentId,@PathVariable Integer doctorId,@PathVariable Integer activityId){
        Treat treat = new Treat();

        treat.setDepartmentName(departmentMapper.selectById(departmentId).getName());
        treat.setDoctorName(doctorMapper.selectById(doctorId).getName());
        treat.setActivityName(activityMapper.selectById(activityId).getName());
        treat.setAppointId(appointId);
        treat.setAppointTime(appointService.getById(appointId).getAppointTime());
        treatMapper.insert(treat);

        return Result.success();
    }

    @GetMapping("/allDepartments")
    public Result getAllDepartments(){
        List<Department> departments = departmentMapper.selectList(null);

        return Result.success(departments);
    }
    @GetMapping("/allDoctors/{departmentId}")
    public Result getDoctorsByDep(@PathVariable Integer departmentId){
        QueryWrapper<DepDoct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id", departmentId);
        List<DepDoct> depDocts = depDoctMapper.selectList(queryWrapper);

        List<Doctor> list = new ArrayList<>();

        for (DepDoct depDoct : depDocts){
            Doctor doctor = doctorMapper.selectById(depDoct.getDoctorId());
            if (doctor.getIsVisit() < 1){
                list.add(doctor);
            }
        }

        return Result.success(list);
    }

    @GetMapping("/allActivities/{doctorId}")
    public Result getActivitiesByDoc(@PathVariable Integer doctorId){
        QueryWrapper<DoctAct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("doctor_id", doctorId);
        List<DoctAct> doctActs = doctActMapper.selectList(queryWrapper);

        List<Activity> list = new ArrayList<>();

        for (DoctAct doctAct : doctActs) {
            list.add(activityMapper.selectById(doctAct.getActivityId()));
        }

        return Result.success(list);
    }
    @PostMapping("/uploadResult/{treatId}")
    public Result uploadResult(@PathVariable Integer treatId,@RequestBody Treat treat){

        Treat one = treatMapper.selectById(treatId);
        one.setResult(treat.getResult());
        one.setAvatarUrl(treat.getAvatarUrl());
        one.setStatus(1);
        treatMapper.updateById(one);
        return Result.success();
    }
    @GetMapping("/record/{realname}/{petName}")
    public Result getRecord(@PathVariable String realname,@PathVariable String petName){
        List<RecordVO> recordVOS = new ArrayList<>();

        QueryWrapper<Appoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("realname", realname);
        queryWrapper.eq("pet_name", petName);
        queryWrapper.eq("status", 1);
        List<Appoint> appoints = appointService.list(queryWrapper);
        for (Appoint a : appoints){
            RecordVO recordVO = new RecordVO();
            BeanUtil.copyProperties(a, recordVO);

            Visit visit = visitMapper.selectById(appoVisitMapper.getByAppointId(a.getId()).getVisitId());
            recordVO.setVisit(visit);

            List<Treat> treats = treatMapper.getByAppointId(a.getId());

            recordVO.setTreats(treats);

            recordVOS.add(recordVO);
        }

        return Result.success(recordVOS);
    }
}
