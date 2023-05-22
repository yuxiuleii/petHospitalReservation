package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.AppoVisit;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AppoVisitMapper extends BaseMapper<AppoVisit> {
    @Select("select * from appo_visit where appoint_id = #{appointId}")
    AppoVisit getByAppointId(@Param("appointId") Integer appointId);
}
