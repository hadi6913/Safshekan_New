package com.safshekan.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class ForthElectronicPaymentDto extends ResultDto {
    @SerializedName("Values")
    private ForthElectronicPaymentDto.ForthElectronicPaymentResponse value;


    public ForthElectronicPaymentDto.ForthElectronicPaymentResponse getValue() {
        return value;
    }

    public void setValue(ForthElectronicPaymentDto.ForthElectronicPaymentResponse value) {
        this.value = value;
    }

    public static class ForthElectronicPaymentResponse {

        //recipe resid
        @SerializedName("Result")
        private String result;



        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

}
