package com.safshekan.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class ExitBillDto extends ResultDto {


    @SerializedName("Values")
    private ExitBillDtoResponse value;


    public ExitBillDtoResponse getValue() {
        return value;
    }

    public void setValue(ExitBillDtoResponse value) {
        this.value = value;
    }

    public static class ExitBillDtoResponse {

        @SerializedName("Id")
        private long id;

        @SerializedName("CarPlate")
        private String carPlate;


        @SerializedName("AbsolutCarPlate")
        private String absolutCarPlate;


        @SerializedName("MemberCode")
        private String memberCode;

        @SerializedName("CardNumber")
        private String CardNumber;

        @SerializedName("SolarEnterDateTime")
        private String solarEnterDateTime;

        @SerializedName("SolarExitDateTime")
        private String solarExitDateTime;

        @SerializedName("EnterDateTime")
        private String enterDateTime;

        @SerializedName("ExitDateTime")
        private String exitDateTime;

        @SerializedName("FormatedDuration")
        private String formatedDuration;

        @SerializedName("DumpId")
        private long dumpId;

        @SerializedName("ParkingId")
        private long parkingId;

        @SerializedName("MemberCost")
        private long memberCost;

        @SerializedName("CommonCost")
        private long commonCost;

        @SerializedName("TotalCost")
        private long totalCost;

        @SerializedName("TotalDuration")
        private int totalDuration;

        @SerializedName("MemberDuration")
        private int memberDuration;

        @SerializedName("CommonDuration")
        private int commonDuration;

        @SerializedName("CarType")
        private int carType;


        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }


        public String getFormatedDuration() {
            return formatedDuration;
        }

        public void setFormatedDuration(String formatedDuration) {
            this.formatedDuration = formatedDuration;
        }

        public String getCarPlate() {
            return carPlate;
        }

        public void setCarPlate(String carPlate) {
            this.carPlate = carPlate;
        }

        public String getMemberCode() {
            return memberCode;
        }

        public void setMemberCode(String memberCode) {
            this.memberCode = memberCode;
        }

        public String getCardNumber() {
            return CardNumber;
        }

        public void setCardNumber(String cardNumber) {
            CardNumber = cardNumber;
        }

        public String getSolarEnterDateTime() {
            return solarEnterDateTime;
        }

        public void setSolarEnterDateTime(String solarEnterDateTime) {
            this.solarEnterDateTime = solarEnterDateTime;
        }

        public String getSolarExitDateTime() {
            return solarExitDateTime;
        }

        public void setSolarExitDateTime(String solarExitDateTime) {
            this.solarExitDateTime = solarExitDateTime;
        }

        public String getEnterDateTime() {
            return enterDateTime;
        }

        public void setEnterDateTime(String enterDateTime) {
            this.enterDateTime = enterDateTime;
        }

        public String getExitDateTime() {
            return exitDateTime;
        }

        public void setExitDateTime(String exitDateTime) {
            this.exitDateTime = exitDateTime;
        }

        public long getDumpId() {
            return dumpId;
        }

        public void setDumpId(long dumpId) {
            this.dumpId = dumpId;
        }

        public long getParkingId() {
            return parkingId;
        }

        public void setParkingId(long parkingId) {
            this.parkingId = parkingId;
        }

        public long getMemberCost() {
            return memberCost;
        }

        public void setMemberCost(long memberCost) {
            this.memberCost = memberCost;
        }

        public long getCommonCost() {
            return commonCost;
        }

        public void setCommonCost(long commonCost) {
            this.commonCost = commonCost;
        }

        public long getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(long totalCost) {
            this.totalCost = totalCost;
        }

        public int getTotalDuration() {
            return totalDuration;
        }

        public void setTotalDuration(int totalDuration) {
            this.totalDuration = totalDuration;
        }

        public int getMemberDuration() {
            return memberDuration;
        }

        public void setMemberDuration(int memberDuration) {
            this.memberDuration = memberDuration;
        }

        public int getCommonDuration() {
            return commonDuration;
        }

        public void setCommonDuration(int commonDuration) {
            this.commonDuration = commonDuration;
        }

        public int getCarType() {
            return carType;
        }

        public void setCarType(int carType) {
            this.carType = carType;
        }

        public String getAbsolutCarPlate() {
            return absolutCarPlate;
        }

        public void setAbsolutCarPlate(String absolutCarPlate) {
            this.absolutCarPlate = absolutCarPlate;
        }
    }
}
