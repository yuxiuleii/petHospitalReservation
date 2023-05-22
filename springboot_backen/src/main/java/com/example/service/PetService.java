package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Pet;

public interface PetService extends IService<Pet> {
    boolean savePet(Pet pet);
}
