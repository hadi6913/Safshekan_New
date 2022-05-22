package com.safshekan.parkban.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.safshekan.parkban.persistence.models.CarPlate;

import java.sql.Date;
import java.util.List;

@Dao
public interface CarPlateDao {

    @Insert
    long saveCarPlate(CarPlate carPlate);

    @Query("SELECT * FROM car_plates WHERE car_id=:carId")
    LiveData<List<CarPlate>> getCarPlates(int carId);

    @Query("SELECT * FROM car_plates WHERE car_id=:carId")
    List<CarPlate> getAllCarPlates(int carId);

    @Query("SELECT * FROM car_plates WHERE car_id=:carId ORDER BY record_date DESC LIMIT 1")
    CarPlate getLastCarPlates(int carId);

    @Query("SELECT * FROM car_plates WHERE id=:id")
    CarPlate getCarPlate(long id);

    @Query("UPDATE car_plates SET status=:status WHERE id = :car_id")
    void update(int status, int car_id);

    @Query("SELECT is_exit FROM car_plates WHERE car_id=:carId AND is_exit=1")
    boolean checkIsExitOfCar(long carId);

    @Query("UPDATE car_plates set status=:status WHERE id=:id")
    void updateCarPlateStatus(long id, int status);

    @Update
    void updateCarPlate(CarPlate carPlate);

    @Query("SELECT * FROM car_plates WHERE status=1 AND record_date <:date")
    List<CarPlate> getCarPlateSent(long date);

    @Query("DELETE FROM car_plates WHERE status=1 AND record_date <:date")
    void deleteCarPlateSent(long date);

    @Delete
    void deleteCarPlate(CarPlate plate);

    @Query("SELECT record_date FROM car_plates WHERE car_id =:carId ORDER BY record_date ASC LIMIT 1")
    Date getFirstCarPlateTime(long carId);

    @Query("UPDATE car_plates set status=0 WHERE status=3")
    void updateAllCarPlateStatus();

}
