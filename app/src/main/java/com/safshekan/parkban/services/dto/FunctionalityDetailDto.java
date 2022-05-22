package com.safshekan.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class FunctionalityDetailDto {

    @SerializedName("SolarDate")
    private String solarDate;

    @SerializedName("SolarDayName")
    private String solarDayName;

    @SerializedName("AreaTitle")
    private String areaTitle;

    @SerializedName("WorkShiftType")
    private int workShiftType;

    @SerializedName("ParkAmount")
    private int parkAmount;

    @SerializedName("ParkCount")
    private int parkCount;

    @SerializedName("IsExitParkCount")
    private int isExitParkCount;

    @SerializedName("NormalParkCount")
    private int normalParkCount;

    @SerializedName("TotalImageCount")
    private int totalImageCount;

    @SerializedName("FirstPhotoDateTime")
    private String firstPhotoDateTime;

    @SerializedName("LastPhotoDateTime")
    private String lastPhotoDateTime;

    public String getSolarDate() {
        return solarDate;
    }

    public void setSolarDate(String solarDate) {
        this.solarDate = solarDate;
    }

    public String getSolarDayName() {
        return solarDayName;
    }

    public void setSolarDayName(String solarDayName) {
        this.solarDayName = solarDayName;
    }

    public String getAreaTitle() {
        return areaTitle;
    }

    public void setAreaTitle(String areaTitle) {
        this.areaTitle = areaTitle;
    }

    public int getWorkShiftType() {
        return workShiftType;
    }

    public void setWorkShiftType(int workShiftType) {
        this.workShiftType = workShiftType;
    }

    public int getParkAmount() {
        return parkAmount;
    }

    public void setParkAmount(int parkAmount) {
        this.parkAmount = parkAmount;
    }

    public int getParkCount() {
        return parkCount;
    }

    public void setParkCount(int parkCount) {
        this.parkCount = parkCount;
    }

    public int getIsExitParkCount() {
        return isExitParkCount;
    }

    public void setIsExitParkCount(int isExitParkCount) {
        this.isExitParkCount = isExitParkCount;
    }

    public int getNormalParkCount() {
        return normalParkCount;
    }

    public void setNormalParkCount(int normalParkCount) {
        this.normalParkCount = normalParkCount;
    }

    public int getTotalImageCount() {
        return totalImageCount;
    }

    public void setTotalImageCount(int totalImageCount) {
        this.totalImageCount = totalImageCount;
    }

    public String getFirstPhotoDateTime() {
        return firstPhotoDateTime;
    }

    public void setFirstPhotoDateTime(String firstPhotoDateTime) {
        this.firstPhotoDateTime = firstPhotoDateTime;
    }

    public String getLastPhotoDateTime() {
        return lastPhotoDateTime;
    }

    public void setLastPhotoDateTime(String lastPhotoDateTime) {
        this.lastPhotoDateTime = lastPhotoDateTime;
    }
}


