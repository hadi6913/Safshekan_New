package com.safshekan.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FailedFactorsRequestDto extends ResultDto {

    @SerializedName("Values")
    private RequestFailedFactor value;

    public RequestFailedFactor getValue() {
        return value;
    }

    public void setValue(RequestFailedFactor value) {
        this.value = value;
    }

    public static class RequestFailedFactor {
        @SerializedName("factorIds")
        private List<String> failedList;

        public List<String> getFailedList() {
            return failedList;
        }

        public void setFailedList(List<String> failedList) {
            this.failedList = failedList;
        }
    }
}
