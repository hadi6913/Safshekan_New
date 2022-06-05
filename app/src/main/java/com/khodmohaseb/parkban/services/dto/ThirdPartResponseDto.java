package com.khodmohaseb.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class ThirdPartResponseDto extends ResultDto {

    @SerializedName("Values")
    private ThirdPartResponse value;

    public ThirdPartResponse getValue() {
        return value;
    }

    public void setValue(ThirdPartResponse value) {
        this.value = value;
    }

    public static class ThirdPartResponse {

        @SerializedName("Result")
        private boolean resultAns;

        @SerializedName("SolarTransActionDateTime")
        private String dateTimeSolar;


        public boolean isResultAns() {
            return resultAns;
        }

        public void setResultAns(boolean resultAns) {
            this.resultAns = resultAns;
        }

        public String getDateTimeSolar() {
            return dateTimeSolar;
        }

        public void setDateTimeSolar(String dateTimeSolar) {
            this.dateTimeSolar = dateTimeSolar;
        }
    }
}
