package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.AppoTreat;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AppoTreatMapper extends BaseMapper<AppoTreat> {
    @Select("select * from appo_treat where treat_id = #{treatId}")
    List<AppoTreat> getByTreatId(@Param("treatId") Integer treatId);
    @Select("select * from appo_treat where appoint_id = #{appointId}")
    List<AppoTreat> getByAppointId(@Param("appointId") Integer appointId);
}
