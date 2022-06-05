package com.khodmohaseb.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class ThirdElectronicPaymentResponseDto extends ResultDto {


    @SerializedName("Values")
    private ThirdElectronicPaymentResponseDto.InnerResponse value;


    public ThirdElectronicPaymentResponseDto.InnerResponse getValue() {
        return value;
    }

    public void setValue(ThirdElectronicPaymentResponseDto.InnerResponse value) {
        this.value = value;
    }

    public static class InnerResponse {

        @SerializedName("Result")
        private boolean factorId;


        public boolean isFactorId() {
            return factorId;
        }

        public void setFactorId(boolean factorId) {
            this.factorId = factorId;
        }
    }
}
