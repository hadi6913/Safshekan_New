package com.khodmohaseb.parkban.persistence.models.khodmohaseb;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "change_password")
public class PasswordOperatorRecord {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;


    @ColumnInfo(name = "device_id")
    private long parkingId;


    @ColumnInfo(name = "userName")
    private String userName;


    @ColumnInfo(name = "password")
    private String password;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParkingId() {
        return parkingId;
    }

    public void setParkingId(long parkingId) {
        this.parkingId = parkingId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
