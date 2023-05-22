package com.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
@Data
public class ScheduleDto {
    private Integer id;
    private Integer doctorId;
    private String departmentName;
    private String doctorName;
    private Integer period1;
    private Integer period2;
    private Integer period3;
    private Integer period4;
    private Integer period5;
    private Integer period6;
    private Integer period7;
    private Integer period8;
//    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd hh:mm:ss")
//    private Date scheTime;
    private String scheTime;
}
