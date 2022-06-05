package com.khodmohaseb.parkban.services.dto;

import com.google.gson.annotations.SerializedName;

public class ReportDetailDto {

    @SerializedName("MemberName")
    private String memberName;

    @SerializedName("Plate")
    private String plate;

    @SerializedName("Title")
    private String title;

    @SerializedName("CashDateTime")
    private String cashDateTime;

    @SerializedName("SolarCashDateTime")
    private String solarCashDateTime;

    @SerializedName("Description")
    private String description;

    @SerializedName("Amount")
    private long amount;

    @SerializedName("CashType")
    private long cashType;

    @SerializedName("Id")
    private long id;

    @SerializedName("PersistOn")
    private String persistOn;


    @SerializedName("MemberCode")
    private String memberCode;


    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCashDateTime() {
        return cashDateTime;
    }

    public void setCashDateTime(String cashDateTime) {
        this.cashDateTime = cashDateTime;
    }

    public String getSolarCashDateTime() {
        return solarCashDateTime;
    }

    public void setSolarCashDateTime(String solarCashDateTime) {
        this.solarCashDateTime = solarCashDateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getCashType() {
        return cashType;
    }

    public void setCashType(long cashType) {
        this.cashType = cashType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPersistOn() {
        return persistOn;
    }

    public void setPersistOn(String persistOn) {
        this.persistOn = persistOn;
    }


    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }
}


