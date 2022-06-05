package com.khodmohaseb.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class IncreaseDriverWalletCardResultDto extends ResultDto {

    @SerializedName("Values")
    private IncreaseDriverWalletDto value;

    public IncreaseDriverWalletDto getValue() {
        return value;
    }

    public void setValue(IncreaseDriverWalletDto value) {
        this.value = value;
    }

    public static class IncreaseDriverWalletDto {

        @SerializedName("ErrorMessage")
        private String errorMessage;

        @SerializedName("PhoneNumber")
        private long phoneNumber;

        @SerializedName("RealPhoneNumber")
        private long realPhoneNumber;

        @SerializedName("Amount")
        private long amount;

        @SerializedName("FactorId")
        private long factorId;

        @SerializedName("Plate")
        private String plate;

        @SerializedName("ReceiptCode")
        private String receiptCode;

        @SerializedName("DriverWalletCashAmount")
        private long driverWalletCashAmount;

        @SerializedName("QRCodeBase64")
        private String QRCodeBase64;

        @SerializedName("SolarDateTime")
        private String solarDateTime;

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public long getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(long phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public long getRealPhoneNumber() {
            return realPhoneNumber;
        }

        public void setRealPhoneNumber(long realPhoneNumber) {
            this.realPhoneNumber = realPhoneNumber;
        }

        public long getAmount() {
            return amount;
        }

        public void setAmount(long amount) {
            this.amount = amount;
        }

        public long getFactorId() {
            return factorId;
        }

        public void setFactorId(long factorId) {
            this.factorId = factorId;
        }

        public String getPlate() {
            return plate;
        }

        public void setPlate(String plate) {
            this.plate = plate;
        }

        public String getReceiptCode() {
            return receiptCode;
        }

        public void setReceiptCode(String receiptCode) {
            this.receiptCode = receiptCode;
        }

        public long getDriverWalletCashAmount() {
            return driverWalletCashAmount;
        }

        public void setDriverWalletCashAmount(long driverWalletCashAmount) {
            this.driverWalletCashAmount = driverWalletCashAmount;
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
