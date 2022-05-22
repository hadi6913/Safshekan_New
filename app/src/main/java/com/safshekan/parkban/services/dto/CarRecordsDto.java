package com.safshekan.parkban.services.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class CarRecordsDto {

    @SerializedName("FirstParkDateTime")
    @Expose
    private Date firstParkDate;

    @SerializedName("Longitude")
    @Expose
    private double longitude;

    @SerializedName("Latitude")
    @Expose
    private double latitude ;

    @SerializedName("parkId")
    private long parkId ;

    @SerializedName("CarPlate")
    @Expose
    private String plateNo;

    @SerializedName("UserId")
    @Expose
    private long userId;

    @SerializedName("DataRow")
    @Expose
    private int[] imageIntArray;

    @SerializedName("ParkDateTime")
    @Expose
    private Date dateTime;

    @SerializedName("IsExit")
    @Expose
    private boolean IsExit;

    @SerializedName("ParkingSpaceId")
    @Expose
    private long parkingSpaceId;

    @SerializedName("DriverPhoneNumber")
    @Expose
    private long driverPhoneNumber ;

    @SerializedName("VerificationStatus")
    @Expose
    private int verificationStatus ;

    public long getDriverPhoneNumber() {
        return driverPhoneNumber;
    }

    public void setDriverPhoneNumber(long driverPhoneNumber) {
        this.driverPhoneNumber = driverPhoneNumber;
    }

    //    @SerializedName("CarParkDetails")
//    @Expose
//    private ArrayList<CarDetailDto> carDetailList;

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

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

//    public ArrayList<CarDetailDto> getCarDetailList() {
//        if (carDetailList == null)
//            carDetailList = new ArrayList<>();
//        return carDetailList;
//    }
//
//    public void setCarDetailList(ArrayList<CarDetailDto> carDetailList) {
//        this.carDetailList = carDetailList;
//    }

    public long getParkId() {
        return parkId;
    }

    public void setParkId(long parkId) {
        this.parkId = parkId;
    }

    public int[] getImageIntArray() {
        return imageIntArray;
    }

    public void setImageIntArray(int[] imageIntArray) {
        this.imageIntArray = imageIntArray;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isExit() {
        return IsExit;
    }

    public void setExit(boolean exit) {
        IsExit = exit;
    }

    public Date getFirstParkDate() {
        return firstParkDate;
    }

    public void setFirstParkDate(Date firstParkDate) {
        this.firstParkDate = firstParkDate;
    }

    public long getParkingSpaceId() {
        return parkingSpaceId;
    }

    public void setParkingSpaceId(long parkingSpaceId) {
        this.parkingSpaceId = parkingSpaceId;
    }

    public int getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(int verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
}
