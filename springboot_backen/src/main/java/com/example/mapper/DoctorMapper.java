package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Doctor;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface DoctorMapper extends BaseMapper<Doctor> {
    @Select("select * from doctor where name = #{name}")
    Doctor getByName(@Param("name") String name);
    @Select("select * from doctor order by id desc limit 1")
    Doctor getLastOne();
}
