package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "doctor")
public class Doctor {
    @TableId
    private Integer id;
    private String username;
    private String name;
    private String position;
    private Integer appointNumber;
    private Integer isVisit;
    private String avatarUrl;
}
