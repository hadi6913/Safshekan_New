package com.safshekan.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class DriverDebtResultDto extends ResultDto {

    @SerializedName("Values")
    private DriverDebtAndWalletDto value;

    public DriverDebtAndWalletDto getValue() {
        return value;
    }

    public void setValue(DriverDebtAndWalletDto value) {
        this.value = value;
    }

    public class DriverDebtAndWalletDto {

        @SerializedName("Wallet")
        private long wallet;

        @SerializedName("Debt")
        private long debt;

        public long getWallet() {
            return wallet;
        }

        public void setWallet(long wallet) {
            this.wallet = wallet;
        }

        public long getDebt() {
            return debt;
        }

        public void setDebt(long debt) {
            this.debt = debt;
        }
    }
}
