package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Activity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ActivityMapper extends BaseMapper<Activity> {
    @Select("select * from activity where name = #{name}")
    List<Activity> getByName(@Param("name") String name);
    @Select("select * from activity order by id desc limit 1")
    Activity getLastOne();
}
