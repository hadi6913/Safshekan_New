package com.khodmohaseb.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class FirstElectronicPaymentDto extends ResultDto {


    @SerializedName("Values")
    private FirstElectronicPaymentResponse value;


    public FirstElectronicPaymentResponse getValue() {
        return value;
    }

    public void setValue(FirstElectronicPaymentResponse value) {
        this.value = value;
    }

    public static class FirstElectronicPaymentResponse {

        @SerializedName("FactorId")
        private long factorId;


        public long getFactorId() {
            return factorId;
        }

        public void setFactorId(long factorId) {
            this.factorId = factorId;
        }
    }
}
