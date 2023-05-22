package com.example.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.entity.FileEntity;
import com.example.mapper.FileMapper;
import com.example.service.FileService;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileEntity> implements FileService {
}
