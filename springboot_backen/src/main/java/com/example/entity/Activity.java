package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Activity {
    @TableId
    private Integer id;
    private String name;
}
