package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Visit;
import org.apache.ibatis.annotations.Select;

public interface VisitMapper extends BaseMapper<Visit> {

    @Select("select * from visit order by id desc limit 1")
    Visit getLastOne();
}
