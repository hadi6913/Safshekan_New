package com.khodmohaseb.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class SendRecordResultDto extends ResultDto {

    @SerializedName("Values")
    private SendRecordStatus value;

    public SendRecordStatus getValue() {
        return value;
    }

    public void setValue(SendRecordStatus value) {
        this.value = value;
    }

    public static class SendRecordStatus {

        @SerializedName("Value")
        private boolean status;

        @SerializedName("Key")
        private long parkId;

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public long getParkId() {
            return parkId;
        }

        public void setParkId(long parkId) {
            this.parkId = parkId;
        }
    }

}
