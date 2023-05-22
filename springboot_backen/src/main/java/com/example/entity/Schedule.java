package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "schedule")
public class Schedule {
    @TableId
    private Integer id;
    private Integer doctorId;
    private String doctorName;
    private Integer period1;
    private Integer period2;
    private Integer period3;
    private Integer period4;
    private Integer period5;
    private Integer period6;
    private Integer period7;
    private Integer period8;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd hh:mm:ss")
    private Date scheTime;

}
