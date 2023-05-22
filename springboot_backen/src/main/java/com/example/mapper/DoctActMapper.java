package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.DoctAct;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DoctActMapper extends BaseMapper<DoctAct> {
    @Select("select doctor_id from doct_act where activity_id = #{activityId}")
    Integer getAllDoctorsId(@Param("activityId") Integer activityId);
    @Select("select activity_id from doct_act where doctor_id = #{doctorId}")
    List<Integer> getActivitiesIdsByDoctorId(@Param("doctorId") Integer doctorId);
    @Select("select * from doct_act where activity_id = #{activityId}")
    DoctAct getByActivityId(@Param("activityId") Integer activityId);
}
