package com.safshekan.parkban.persistence.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import java.sql.Date;

@Entity(tableName = "Car_plates")
public class CarPlate {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ForeignKey(entity = Car.class, parentColumns = "id", childColumns = "car_id")
    @ColumnInfo(name = "car_id")
    private int carId;

    @ColumnInfo(name = "plate_file_name")
    private String plateFileName;

    @Ignore
    private String seenDate;

    @ColumnInfo(name = "record_date")
    private Date recordDate;

    @ColumnInfo(name = "status")
    private int status;

    @ColumnInfo(name = "is_exit")
    private boolean isExit;

    @ColumnInfo(name="exit_by_system")
    private boolean exitBySystem;

    @ColumnInfo(name= "edited_plate")
    private int editedPlate;

    @Ignore
    private String plateNumber;

    @Ignore
    private Bitmap plateImage;

    @Ignore
    private String part0;

    @Ignore
    private String part1;

    @Ignore
    private String part2;

    @Ignore
    private String part3;

    @Ignore
    private double latitude;

    @Ignore
    private double longitude;

    @Ignore
    private long parkingSpaceId;

    @Ignore
    private long firstDate;


    public CarPlate() {
    }

    public CarPlate(Bitmap plateImage, String part0, String part1, String part2, String part3) {
        this.plateImage = plateImage;
        //this.parkingSpaceId = ParkingSpaceId;
        this.part0 = part0;
        this.part1 = part1;
        this.part2 = part2;
        this.part3 = part3;
    }

    public Bitmap getPlateImage() {
        return plateImage;
    }

    public void setPlateImage(Bitmap plateImage) {
        this.plateImage = plateImage;
    }

    public String getSeenDate() {
        return seenDate;
    }

    public void setSeenDate(String seenDate) {
        this.seenDate = seenDate;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getPart0() {
        return part0;
    }

    public void setPart0(String part0) {
        this.part0 = part0;
    }

    public String getPart1() {
        return part1;
    }

    public void setPart1(String part1) {
        this.part1 = part1;
    }

    public String getPart2() {
        return part2;
    }

    public void setPart2(String part2) {
        this.part2 = part2;
    }

    public String getPart3() {
        return part3;
    }

    public void setPart3(String part3) {
        this.part3 = part3;
    }

    public String getPlateFileName() {
        return plateFileName;
    }

    public void setPlateFileName(String plateFileName) {
        this.plateFileName = plateFileName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isExit() {
        return isExit;
    }

    public void setExit(boolean exit) {
        isExit = exit;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getParkingSpaceId() {
        return parkingSpaceId;
    }

    public void setParkingSpaceId(long parkingSpaceId) {
        this.parkingSpaceId = parkingSpaceId;
    }

    public boolean isExitBySystem() {
        return exitBySystem;
    }

    public void setExitBySystem(boolean exitBySystem) {
        this.exitBySystem = exitBySystem;
    }

    public long getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(long firstDate) {
        this.firstDate = firstDate;
    }

    public int getEditedPlate() {
        return editedPlate;
    }

    public void setEditedPlate(int editedPlate) {
        this.editedPlate = editedPlate;
    }


}
