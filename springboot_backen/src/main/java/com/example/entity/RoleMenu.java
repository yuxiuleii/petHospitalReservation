package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "role_menu")
public class RoleMenu {

    private Integer roleId;
    private Integer menuId;
}
