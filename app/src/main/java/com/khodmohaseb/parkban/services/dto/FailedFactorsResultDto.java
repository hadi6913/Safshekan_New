package com.khodmohaseb.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FailedFactorsResultDto extends ResultDto {

    @SerializedName("Values")
    private List<FailedFactorsDto> value;

    public List<FailedFactorsDto> getValue() {
        return value;
    }

    public void setValue(List<FailedFactorsDto> value) {
        this.value = value;
    }



    public static class FailedFactorsDto {

        @SerializedName("FactorId")
        private String FactorId;

        @SerializedName("Value")
        private boolean Value;


        public String getFactorId() {
            return FactorId;
        }

        public void setFactorId(String factorId) {
            FactorId = factorId;
        }

        public boolean isValue() {
            return Value;
        }

        public void setValue(boolean value) {
            Value = value;
        }
    }
}
