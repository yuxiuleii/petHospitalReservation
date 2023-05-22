package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "appo_treat")
public class AppoTreat {
    private Integer appointId;
    private Integer treatId;
}
