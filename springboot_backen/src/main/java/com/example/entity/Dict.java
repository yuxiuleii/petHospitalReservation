package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "dict")
public class Dict {
    private String name;
    private String value;
    private String type;
}
