package com.khodmohaseb.parkban.persistence.models.khodmohaseb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "exit_table")
public class ExitRecord {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "device_id")
    private long deviceID;



    @ColumnInfo(name = "is_send")
    private int isSend = 0;

    public int getIsSend() {
        return isSend;
    }

    public void setIsSend(int isSend) {
        this.isSend = isSend;
    }

    public long getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(long deviceID) {
        this.deviceID = deviceID;
    }

    @ColumnInfo(name = "plate")
    private String plate;

    @ColumnInfo(name = "exit_date")
    private String exitDate;

    @ColumnInfo(name = "tariffId")
    private int tariffId;

    @ColumnInfo(name = "exit_door_id")
    private long exitDoorId;

    @ColumnInfo(name = "exit_operator_id")
    private long exitOperatorId;

    @ColumnInfo(name = "paid_amount")
    private long paidAmount;

    @ColumnInfo(name = "pay_type")
    private int payType;

    @ColumnInfo(name = "electronic_payment_code")
    private String electronicPaymentCode;


    public ExitRecord() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getExitDate() {
        return exitDate;
    }

    public void setExitDate(String exitDate) {
        this.exitDate = exitDate;
    }

    public int getTariffId() {
        return tariffId;
    }

    public void setTariffId(int tariffId) {
        this.tariffId = tariffId;
    }

    public long getExitDoorId() {
        return exitDoorId;
    }

    public void setExitDoorId(long exitDoorId) {
        this.exitDoorId = exitDoorId;
    }

    public long getExitOperatorId() {
        return exitOperatorId;
    }

    public void setExitOperatorId(long exitOperatorId) {
        this.exitOperatorId = exitOperatorId;
    }

    public long getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(long paidAmount) {
        this.paidAmount = paidAmount;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getElectronicPaymentCode() {
        return electronicPaymentCode;
    }

    public void setElectronicPaymentCode(String electronicPaymentCode) {
        this.electronicPaymentCode = electronicPaymentCode;
    }
}
