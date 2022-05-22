package com.safshekan.parkban.persistence.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Parking_space")
public class ParkingSpace {

    public ParkingSpace() {
    }

    @PrimaryKey
    private long id;

    private String name;

    public ParkingSpaceStatus status ;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ParkingSpaceStatus getStatus() {
        return status;
    }

    public void setStatus(ParkingSpaceStatus status) {
        this.status = status;
    }
}
