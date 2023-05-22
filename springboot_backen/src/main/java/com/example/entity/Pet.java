package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pet")
public class Pet {
    @TableId
    private Integer id;
    private String name;
    private String age;
    private String category;
    private String breed;
    private String description;
    private String avatarUrl;
    @TableLogic
    private Integer deleted;
}
