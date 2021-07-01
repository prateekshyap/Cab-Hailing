package com.pp.Project2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CabEntity
{
    @Id
    private int id;

    @Column
    private int currentPos;

    @Column(nullable = false)
    private String currentState;

    @Column
    private String interestStatus;

    @Column
    private int rideId;

    @Column
    private int totalRides;

    public CabEntity()
    {}

    public CabEntity(int id, String currentState, String interestStatus)
    {
        this.id = id;
        this.currentState = currentState;
        this.interestStatus = interestStatus;

    }

    public String getCurrentState()
    {
        return this.currentState;
    }
    public String getInterestStatus() {
        return this.interestStatus;
    }
    public int getRideId() {
        return this.rideId;
    }
    public int getTotalRides() {
        return this.totalRides;
    }
    public int getCurrentPos() { return this.currentPos; }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }
    public void setInterestStatus(String interestStatus) {
        this.interestStatus = interestStatus;
    }
    public void setRideId(int rideId) {
        this.rideId = rideId;
    }
    public void setTotalRides(int totalRides) {
        this.totalRides = totalRides;
    }
    public void setCurrentPos(int currentPos) { this.currentPos = currentPos; }
}