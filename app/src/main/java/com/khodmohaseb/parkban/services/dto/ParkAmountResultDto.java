package com.khodmohaseb.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class ParkAmountResultDto extends ResultDto {

    @SerializedName("Values")
    private ParkAmountDto values;

    public ParkAmountDto getValues() {
        return values;
    }

    public void setValues(ParkAmountDto values) {
        this.values = values;
    }

    public static class ParkAmountDto {

        @SerializedName("Duration")
        private long duration;

        @SerializedName("ParkAmount")
        private long parkAmount;

        @SerializedName("AllowPayParkban")
        private boolean allowPay;

        @SerializedName("WalletCashAmount")
        private long walletCashAmount;

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public long getParkAmount() {
            return parkAmount;
        }

        public void setParkAmount(long parkAmount) {
            this.parkAmount = parkAmount;
        }

        public boolean isAllowPay() {
            return allowPay;
        }

        public void setAllowPay(boolean allowPay) {
            this.allowPay = allowPay;
        }

        public long getWalletCashAmount() {
            return walletCashAmount;
        }

        public void setWalletCashAmount(long walletCashAmount) {
            this.walletCashAmount = walletCashAmount;
        }
    }
}
