package com.example.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Appoint;
import com.example.mapper.AppointMapper;
import com.example.service.AppointService;
import org.springframework.stereotype.Service;

@Service
public class AppointServiceImpl extends ServiceImpl<AppointMapper, Appoint> implements AppointService {
}
