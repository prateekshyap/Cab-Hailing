package com.pp.Project2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CabManager {

    @Autowired
    CabRepository cabRepository;

    public CabManager (CabRepository cabRepository)
    {
        this.cabRepository = cabRepository;
    }

    public CabEntity getCabById(int cabId)
    {
        return cabRepository.findById(cabId);
    }

    public void store(CabEntity cab)
    {
        cabRepository.save(cab);
    }
}
