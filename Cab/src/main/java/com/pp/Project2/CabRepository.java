package com.pp.Project2;

import org.springframework.data.repository.Repository;

public interface CabRepository extends Repository <CabEntity,Integer>{
    void save(CabEntity cab);
    CabEntity findById(int id);
}
