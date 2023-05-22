package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "appo_visit")
public class AppoVisit {
    private Integer appointId;
    private Integer visitId;
}
