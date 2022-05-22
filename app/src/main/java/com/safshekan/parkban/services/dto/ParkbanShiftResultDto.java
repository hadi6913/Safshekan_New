package com.safshekan.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ParkbanShiftResultDto extends ResultDto {

    @SerializedName("Values")
    private List<ParkbanShiftDto> value;

    public List<ParkbanShiftDto> getValue() {
        return value;
    }

    public void setValue(List<ParkbanShiftDto> value) {
        this.value = value;
    }

    public static class ParkbanShiftDto {

        @SerializedName("ShiftDate")
        private String shiftDate;

        @SerializedName("SolarShiftDate")
        private String solarShiftDateAndTime;

        @SerializedName("AreaTitle")
        private String areaTitle;

        @SerializedName("WorkShiftType")
        private int workShiftType;

        private String workShiftName;

        @SerializedName("BeginTime")
        private String beginTime;

        @SerializedName("EndTime")
        private String endTime;

        @SerializedName("SolarShiftDayOnWeek")
        private String dayName;

        @SerializedName("StreetName")
        private String streetName;

        private String solarShiftDate;

        public String getShiftDate() {
            return shiftDate;
        }

        public void setShiftDate(String shiftDate) {
            this.shiftDate = shiftDate;
        }

        public String getSolarShiftDateAndTime() {
            return solarShiftDateAndTime;
        }

        public void setSolarShiftDateAndTime(String solarShiftDateAndTime) {
            this.solarShiftDateAndTime = solarShiftDateAndTime;
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

        public String getBeginTime() {
            if (beginTime != null){
                beginTime = beginTime.substring(0,5);
            }
            return beginTime;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }

        public String getEndTime() {
            if (endTime != null){
                endTime = endTime.substring(0,5);
            }
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getWorkShiftName() {
            if (workShiftType == 0)
                workShiftName = "شیفت 1";
            else if (workShiftType == 1)
                workShiftName = "شیفت 2";
            else
                workShiftName = "شیفت 3";
            return workShiftName;
        }

        public void setWorkShiftName(String workShiftName) {
            this.workShiftName = workShiftName;
        }

        public String getSolarShiftDate() {
            if (solarShiftDateAndTime != null)
                solarShiftDate = solarShiftDateAndTime.substring(0, 11);
            return solarShiftDate;
        }

        public void setSolarShiftDate(String solarShiftDate) {
            this.solarShiftDate = solarShiftDate;
        }

        public String getDayName() {
            return dayName;
        }

        public void setDayName(String dayName) {
            this.dayName = dayName;
        }

        public String getStreetName() {
            return streetName;
        }

        public void setStreetName(String streetName) {
            this.streetName = streetName;
        }
    }
}
