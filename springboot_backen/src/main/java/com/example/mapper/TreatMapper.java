package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Treat;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TreatMapper extends BaseMapper<Treat> {
    @Select("select * from treat order by id desc limit 1")
    Treat getLastOne();
    @Select("select * from treat where appoint_id = #{appointId} and status = 1")
    List<Treat> getByAppointId(@Param("appointId") Integer appointId);
}
