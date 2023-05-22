package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "visit")
public class Visit {
    @TableId
    private Integer id;
    private String doctorName;
    private String petName;
    private String realname;
    private String temperature;
    private Integer pressure;
    private Integer rate;
    private String symptom;
    private String happenTime;
    private String level;
    private String result;

}
