package com.khodmohaseb.parkban.persistence.models.khodmohaseb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "traffik_table")
public class TraffikRecord {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;



    @ColumnInfo(name = "is_send")
    private int isSend = 0;

    public int getIsSend() {
        return isSend;
    }

    public void setIsSend(int isSend) {
        this.isSend = isSend;
    }

    @ColumnInfo(name = "device_id")
    private long device_id;


    @ColumnInfo(name = "plate")
    private String plate;

    @ColumnInfo(name = "entrance_date")
    private String entranceDate;

    @ColumnInfo(name = "exit_date")
    private String exitDate;

    @ColumnInfo(name = "tariffId")
    private int tariffId;

    @ColumnInfo(name = "entrance_door_id")
    private long entranceDoorId;

    @ColumnInfo(name = "exit_door_id")
    private long exitDoorId;

    @ColumnInfo(name = "entrance_operator_id")
    private long entranceOperatorId;

    @ColumnInfo(name = "exit_operator_id")
    private long exitOperatorId;

    @ColumnInfo(name = "paid_amount")
    private long paidAmount;

    @ColumnInfo(name = "pay_type")
    private int payType;

    @ColumnInfo(name = "electronic_payment_code")
    private String electronicPaymentCode;

    @ColumnInfo(name = "vehicle_name")
    private String vehicleName;


    public TraffikRecord() {
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

    public String getEntranceDate() {
        return entranceDate;
    }

    public void setEntranceDate(String entranceDate) {
        this.entranceDate = entranceDate;
    }

    public int getTariffId() {
        return tariffId;
    }

    public void setTariffId(int tariffId) {
        this.tariffId = tariffId;
    }

    public long getEntranceDoorId() {
        return entranceDoorId;
    }

    public void setEntranceDoorId(long entranceDoorId) {
        this.entranceDoorId = entranceDoorId;
    }

    public long getEntranceOperatorId() {
        return entranceOperatorId;
    }

    public void setEntranceOperatorId(long entranceOperatorId) {
        this.entranceOperatorId = entranceOperatorId;
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


    public long getDevice_id() {
        return device_id;
    }

    public void setDevice_id(long device_id) {
        this.device_id = device_id;
    }

    public String getExitDate() {
        return exitDate;
    }

    public void setExitDate(String exitDate) {
        this.exitDate = exitDate;
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

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }
}
