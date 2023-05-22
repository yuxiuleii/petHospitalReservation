package com.example.vo;

import lombok.Data;

@Data
public class TreatVO {
    private Integer id;
    private String departmentName;
    private String doctorName;
    private String activityName;
    private String result;
    private String avatarUrl;
    private Integer appointId;
    private String appointTime;
    private String realname;
    private String petName;
    private String visitDoctorName;

}
