package com.example.vo;

import com.example.entity.Treat;
import com.example.entity.Visit;
import lombok.Data;

import java.util.List;
@Data
public class RecordVO {
    private Integer id;
    private String username;
    private String realname;
    private String petName;
    private String doctorName;
    private String departmentName;
    private String appointTime;
    private String period;
    private Integer appointNumber;
    private Integer status;

    private Visit visit;
    private List<Treat> treats;

}
