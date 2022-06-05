package com.khodmohaseb.parkban.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.khodmohaseb.parkban.persistence.models.Car;

import java.util.List;

@Dao
public interface CarDao {

    @Insert
    long saveCar(Car car);

    @Query("SELECT * FROM CARS WHERE id in (SELECT car_id FROM Car_plates WHERE record_date>=:date)")
    LiveData<List<Car>> getCurrentCarsOld(long date);

    @Query("SELECT * FROM CARS WHERE id in (SELECT car_id FROM Car_plates WHERE record_date>=:date)")
    List<Car> getCurrentCars(long date);

    @Query("SELECT * FROM CARS WHERE id in (SELECT car_id FROM Car_plates WHERE record_date<:date AND Car_plates.status!=1)")
    LiveData<List<Car>> getPreviousCarsOld(long date);

    @Query("SELECT * FROM CARS WHERE id in (SELECT car_id FROM Car_plates WHERE record_date<:date AND Car_plates.status!=1)")
    List<Car> getPreviousCars(long date);

    @Query("SELECT * FROM CARS WHERE id in (SELECT car_id FROM Car_plates WHERE Car_plates.status!=1)")
    List<Car> getAllCars();

//    @Query("SELECT A.* , B.* FROM CARS A INNER JOIN car_plates B ON B.car_id=A.id WHERE A.id in (SELECT B.car_id FROM Car_plates WHERE B.record_date>=:date)")
//    LiveData<List<Car>> getCars1(long date);

    @Query("SELECT A.* FROM CARS A INNER JOIN car_plates B ON B.car_id=A.id WHERE A.palet_no=:plateNo AND B.record_date>=:date ORDER BY B.record_date DESC LIMIT 1")
    Car getCarByPlateNoAndDate(String plateNo, long date);

    @Query("SELECT distinct A.parking_space_id FROM CARS A INNER JOIN car_plates B ON B.car_id=A.id WHERE B.is_exit =0 and B.exit_by_system = 0 and B.record_date >= :diff " +
            "AND B.car_id not in (SELECT" +
            " B2.car_id FROM car_plates B2 WHERE (B2.is_exit = 1 or B2.exit_by_system = 1 ) AND B2.car_id = B.car_id   )")
    List<Long> getParkSpaceFull(Long diff);

    @Query("SELECT * FROM CARS WHERE palet_no=:plateNo")
    LiveData<Car> getCarByPlateNo(String plateNo);

    @Query("SELECT * FROM CARS WHERE parking_space_id=:id")
    boolean getCarByPlaceId(long id);

    @Query("SELECT * FROM CARS WHERE id=:id")
    Car getCarById(int id);

    @Update
    void updateCar(Car car);

    @Query("SELECT * FROM CARS WHERE id NOT IN (SELECT car_id  FROM car_plates WHERE CARS.id = car_id )")
    List<Car> getCarsNotHaveCarPlate();

    @Query("DELETE FROM CARS WHERE id NOT IN (SELECT car_id  FROM car_plates WHERE CARS.id = car_id)")
    void deleteCarsNotHaveCarPlate();
}
