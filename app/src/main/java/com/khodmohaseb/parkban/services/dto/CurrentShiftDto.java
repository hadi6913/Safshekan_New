package com.khodmohaseb.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurrentShiftDto extends ResultDto {

    @SerializedName("Values")
    private CurrentShift value;

    public CurrentShift getValue() {
        return value;
    }

    public void setValue(CurrentShift value) {
        this.value = value;
    }

    public static class CurrentShift {

        @SerializedName("ExecuteDate")
        private String executeDate;

        @SerializedName("BeginTime")
        private int beginTime;

        @SerializedName("EndTime")
        private int endTime;

        @SerializedName("ParkSpaces")
        private List<ParkingSpaceDto> parkingSpaceDtoList;

        public String getExecuteDate() {
            return executeDate;
        }

        public void setExecuteDate(String executeDate) {
            this.executeDate = executeDate;
        }

        public int getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(int beginTime) {
            this.beginTime = beginTime;
        }

        public int getEndTime() {
            return endTime;
        }

        public void setEndTime(int endTime) {
            this.endTime = endTime;
        }

        public List<ParkingSpaceDto> getParkingSpaceDtoList() {
            return parkingSpaceDtoList;
        }

        public void setParkingSpaceDtoList(List<ParkingSpaceDto> parkingSpaceDtoList) {
            this.parkingSpaceDtoList = parkingSpaceDtoList;
        }
    }
}
