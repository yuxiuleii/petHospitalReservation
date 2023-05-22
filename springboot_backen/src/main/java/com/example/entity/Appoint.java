package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
@Data
@TableName(value = "appoint")
public class Appoint {
    @TableId
    private Integer id;
    private String username;
    private String realname;
    private String petName;
    private String doctorName;
    private String departmentName;
    private String appointTime;
    private String period;
    private Integer appointNumber;
    private Integer status;
}
