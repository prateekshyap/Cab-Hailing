package com.cab.RideService;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "rideservice")
public class RideService {

    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO
    )
    @Column(name="ride_id")
    Integer rideId;

    @Column(nullable = false)
    private int custId;

    @ManyToOne
    @JoinColumn(name = "cabId")
    private CabStatus cab;

    @Column(nullable = false)
    private int sourceLoc;

    @Column(nullable = false)
    private int destLoc;

    @Column(nullable = false)
    private String rideStatus;


    public RideService() {}

    public RideService(CabStatus cab,Integer custId,Integer sourceLoc,Integer destLoc,String rideStatus){
        this.cab = cab;
        this.custId = custId;
        this.sourceLoc = sourceLoc;
        this.destLoc = destLoc;
        this.rideStatus = rideStatus;

    }

    @Override
    public String toString() {
        return "RideService{" +
                "rideId=" + rideId +
                ", custId=" + custId +
                ", cab=" + cab +
                ", sourceLoc=" + sourceLoc +
                ", destLoc=" + destLoc +
                ", rideStatus='" + rideStatus + '\'' +
                '}';
    }

    public String getRideStatus(){
        return rideStatus;
    }

    public int getDestLoc(){
        return destLoc;
    }

    public int getSourceLoc(){
        return sourceLoc;
    }

    public int getCustId(){
        return custId;
    }


    public CabStatus getCab(){
        return this.cab;
    }

    public void setRideStatus(String status) {
        this.rideStatus = status;
    }

    public void setCab(CabStatus cab) { this.cab = cab;
    }
}
