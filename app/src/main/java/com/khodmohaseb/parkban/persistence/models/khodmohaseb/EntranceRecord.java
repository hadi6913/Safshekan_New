package com.khodmohaseb.parkban.persistence.models.khodmohaseb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

import com.khodmohaseb.parkban.persistence.models.Car;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.Door;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.Operator;

import java.sql.Date;

@Entity(tableName = "entrance_table")
public class EntranceRecord {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;


    @ColumnInfo(name = "plate")
    private String plate;

    @ColumnInfo(name = "entrance_date")
    private String entranceDate;

    @ColumnInfo(name = "tariffId")
    private int tariffId;

    @ColumnInfo(name = "entrance_door_id")
    private long entranceDoorId;

    @ColumnInfo(name = "entrance_operator_id")
    private long entranceOperatorId;

    @ColumnInfo(name = "paid_amount")
    private long paidAmount;

    @ColumnInfo(name = "pay_type")
    private int payType;

    @ColumnInfo(name = "electronic_payment_code")
    private String electronicPaymentCode;


    public EntranceRecord() {
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
}
