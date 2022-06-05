package com.khodmohaseb.parkban.persistence.models;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "cars")
public class Car {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "palet_no")
    private String plateNo;

    private double latitude;

    private double longitude;

    private int status;

    @ColumnInfo(name = "parking_space_id")
    private long parkingSpaceId;

    @Ignore
    private List<CarPlate> carPlates;

    public Car(String plateNo, double latitude, double longitude , long parkingSpaceId) {
        this.plateNo = plateNo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.parkingSpaceId = parkingSpaceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<CarPlate> getCarPlates() {
        if (carPlates == null) {
            carPlates = new ArrayList<CarPlate>();
        }
        return carPlates;
    }

    public void setCarPlates(List<CarPlate> carPlates) {
        this.carPlates = carPlates;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getParkingSpaceId() {
        return parkingSpaceId;
    }

    public void setParkingSpaceId(long parkingSpace) {
        this.parkingSpaceId = parkingSpace;
    }
}
