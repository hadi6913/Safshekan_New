package com.khodmohaseb.parkban.services.dto;

import android.support.annotation.NonNull;

import com.khodmohaseb.parkban.persistence.models.ParkingSpaceStatus;
import com.google.gson.annotations.SerializedName;

public class ParkingSpaceDto implements Comparable<ParkingSpaceDto> {

    @SerializedName("Id")
    private long id;

    @SerializedName("Name")
    private String Name;

    @SerializedName("Latitude")
    private double latitude;

    @SerializedName("Longitude")
    private double longitude;

    private int distance;

    private ParkingSpaceStatus status;

    public ParkingSpaceDto(long id, String name, double latitude, double longitude) {
        this.id = id;
        Name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public ParkingSpaceStatus getSpaceStatus() {
        return status;
    }

    public void setSpaceStatus(ParkingSpaceStatus spaceStatus) {
        this.status = spaceStatus;
    }

    @Override
    public int compareTo(@NonNull ParkingSpaceDto o) {
        int space = o.getDistance();
        return this.distance - space;
    }
}
