package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.DepDoct;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DepDoctMapper extends BaseMapper<DepDoct> {
    @Select("select doctor_id from dep_doct where department_id = #{departmentId}")
    List<Integer> getAllDoctorsId(@Param("departmentId") Integer departmentId);
    @Select("select department_id from dep_doct where doctor_id = #{doctorId}")
    Integer getDepartmentIdByDoctorId(@Param("doctorId") Integer doctorId);

    @Select("select * from dep_doct where doctor_id = #{doctorId}")
    DepDoct getByDoctorId(@Param("doctorId") Integer doctorId);

}
