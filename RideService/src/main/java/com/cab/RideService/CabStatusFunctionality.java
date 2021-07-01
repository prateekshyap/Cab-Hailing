package com.cab.RideService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CabStatusFunctionality {

    @Autowired
    private CabStatusRepository cabrep;

    public List<CabStatus> findAllCabs(){

        List<CabStatus> cabs = new ArrayList<>();
        cabrep.findAll().forEach(cabs::add);
        return cabs;
    }



    public Optional<CabStatus> findById(Integer cabId){
        Optional<CabStatus> cab = cabrep.findById(cabId);
        return cab;
    }

    public void addCab (CabStatus cab){
        cabrep.save(cab);
    }

    public void updateCab (CabStatus cab){
        cabrep.save(cab);
    }


}
