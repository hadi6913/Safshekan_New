package com.khodmohaseb.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CashDetailsResultDto extends ResultDto {

    @SerializedName("Values")
    private List<CashDetailsDto> value;

    public List<CashDetailsDto> getValue() {
        return value;
    }

    public class CashDetailsDto {

        @SerializedName("AmountIncreas")
        private long amountIncreas;

        @SerializedName("AmountDecreas")
        private long amountDecreas;

        @SerializedName("SolarPersistOn")
        private String jalaliPersistOn;

        @SerializedName("SolarPersistOnWeekDay")
        private String dayName;

        private String amountIncreasValue;

        private String amountDecreasValue;

        public long getAmountIncreas() {
            return amountIncreas;
        }

        public void setAmountIncreas(long amountIncreas) {
            this.amountIncreas = amountIncreas;
        }

        public long getAmountDecreas() {
            return amountDecreas;
        }

        public void setAmountDecreas(long amountDecreas) {
            this.amountDecreas = amountDecreas;
        }

        public String getJalaliPersistOn() {
            return jalaliPersistOn;
        }

        public void setJalaliPersistOn(String jalaliPersistOn) {
            this.jalaliPersistOn = jalaliPersistOn;
        }

        public String getDayName() {
            return dayName;
        }

        public void setDayName(String dayName) {
            this.dayName = dayName;
        }

        public String getAmountIncreasValue() {
            amountIncreasValue = String.valueOf(amountIncreas);
            return amountIncreasValue;
        }

        public void setAmountIncreasValue(String amountIncreasValue) {
            this.amountIncreasValue = amountIncreasValue;
        }

        public String getAmountDecreasValue() {
            return amountDecreasValue;
        }

        public void setAmountDecreasValue(String amountDecreasValue) {
            this.amountDecreasValue = amountDecreasValue;
        }
    }
}
