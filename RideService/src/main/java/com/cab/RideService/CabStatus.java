package com.cab.RideService;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name="cab")
public class CabStatus {

    @Id
    @Column(name = "cabId")
    Integer cabId;

    /*
      signed-out
      available
      committed
      giving-ride
     */
    String cabStatus;

    Integer cabCurrentLocation;

    Integer rideId;




    @OneToMany(mappedBy = "cab", cascade = CascadeType.ALL)
    Collection<RideService> rideServiceCollection;

    public CabStatus() {
        this.cabStatus = "Signed Out";
        this.cabCurrentLocation = 0;
        this.rideServiceCollection = new ArrayList<>();
    }


    public CabStatus(Integer cabId,String cabStatus, Integer cabCurrentLocation) {

        this.cabId = cabId;
        this.cabStatus = cabStatus;
        this.cabCurrentLocation = cabCurrentLocation;
        this.rideServiceCollection = new ArrayList<>();

    }


    public void setCabStatus(String cabStatus){
        this.cabStatus = cabStatus;
    }

    public void setCabCurrentLocation(Integer cabCurrentLocation){
        this.cabCurrentLocation = cabCurrentLocation;
    }

    public void setRideId(Integer rideId){
        this.rideId = rideId;
    }

}
