package com.safshekan.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class SendRecordAndPayResultDto extends ResultDto {

    @SerializedName("Values")
    private SendAndPayDto values;

    public SendAndPayDto getValues() {
        return values;
    }

    public void setValues(SendAndPayDto values) {
        this.values = values;
    }

    public static class SendAndPayDto {

        @SerializedName("CarParkResult")
        private SendRecordResultDto.SendRecordStatus carParkResult;

        @SerializedName("ReceiptCode")
        private String receiptCode;

        @SerializedName("QRCodeBase64")
        private String QRCodeBase64;

        @SerializedName("SolarDateTime")
        private String solarDateTime;

        public SendRecordResultDto.SendRecordStatus getCarParkResult() {
            return carParkResult;
        }

        public void setCarParkResult(SendRecordResultDto.SendRecordStatus carParkResult) {
            this.carParkResult = carParkResult;
        }

        public String getReceiptCode() {
            return receiptCode;
        }

        public void setReceiptCode(String receiptCode) {
            this.receiptCode = receiptCode;
        }

        public String getQRCodeBase64() {
            return QRCodeBase64;
        }

        public void setQRCodeBase64(String QRCodeBase64) {
            this.QRCodeBase64 = QRCodeBase64;
        }

        public String getSolarDateTime() {
            return solarDateTime;
        }

        public void setSolarDateTime(String solarDateTime) {
            this.solarDateTime = solarDateTime;
        }
    }
}
