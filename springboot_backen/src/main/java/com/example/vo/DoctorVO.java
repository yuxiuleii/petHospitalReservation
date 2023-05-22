package com.example.vo;

import com.example.entity.Activity;
import lombok.Data;

import java.util.List;

@Data
public class DoctorVO {
    private Integer id;
    private String username;
    private String name;
    private String position;
    private Integer appointNumber;
    private Integer isVisit;
    private String avatarUrl;

    private String department;
    private List<Activity> activities;
}
