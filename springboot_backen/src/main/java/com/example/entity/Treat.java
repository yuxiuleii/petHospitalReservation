package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "treat")
public class Treat {
    @TableId
    private Integer id;
    private String departmentName;
    private String doctorName;
    private String activityName;
    private String result;
    private String avatarUrl;
    private Integer appointId;
    private String appointTime;
    private Integer status;
}
