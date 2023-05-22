package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "department")
public class Department {
    @TableId
    private Integer id;
    private String name;
    private String path;
}
