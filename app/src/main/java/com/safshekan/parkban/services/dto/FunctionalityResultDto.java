package com.safshekan.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FunctionalityResultDto extends ResultDto {

    @SerializedName("Values")
    private FunctionalityDto value;

    public FunctionalityDto getValue() {
        return value;
    }

    public void setValue(FunctionalityDto value) {
        this.value = value;
    }

    public static class FunctionalityDto {

        @SerializedName("TotalImageCount")
        private int totalImageCount;

        @SerializedName("TotalParkCount")
        private int totalParkCount;

        @SerializedName("Details")
        private List<FunctionalityDetailDto> details;

        public int getTotalImageCount() {
            return totalImageCount;
        }

        public void setTotalImageCount(int totalImageCount) {
            this.totalImageCount = totalImageCount;
        }

        public int getTotalParkCount() {
            return totalParkCount;
        }

        public void setTotalParkCount(int totalParkCount) {
            this.totalParkCount = totalParkCount;
        }

        public List<FunctionalityDetailDto> getDetails() {
            return details;
        }

        public void setDetails(List<FunctionalityDetailDto> details) {
            this.details = details;
        }
    }
}
