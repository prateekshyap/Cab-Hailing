package com.cab.RideService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RideServiceFunctionality {

    @Autowired
    private RideServiceRepository rsrep;

    public List<RideService> getAllRideService(){
        List<RideService> rideServices = new ArrayList<>();
        rsrep.findAll().forEach(rideServices::add);
        return rideServices;
    }

    public Optional<RideService> findById(Integer rideId){
        Optional<RideService> ride =  rsrep.findById(rideId);
        return ride;
    }

    public void addRideService(RideService rideService){
        rsrep.save(rideService);
    }

    public void updateRideService(RideService rideService){
        rsrep.save(rideService);
    }
}
