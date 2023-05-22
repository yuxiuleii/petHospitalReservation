package com.example.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Pet;
import com.example.mapper.PetMapper;
import com.example.service.PetService;
import org.springframework.stereotype.Service;

@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet> implements PetService {
    @Override
    public boolean savePet(Pet pet) {
        return saveOrUpdate(pet);
    }
}
