package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Schedule;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ScheduleMapper extends BaseMapper<Schedule> {
    @Select("select * from schedule where id =#{id}")
    Schedule getById(@Param("id") Integer id);
}
