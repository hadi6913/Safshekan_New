package com.safshekan.parkban.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.safshekan.parkban.persistence.models.ParkingSpace;

@Dao
public interface ParkingSpaceDao {

    @Insert
    long saveParkingSpace(ParkingSpace parkingSpaceList);

    @Update
    void updateParkingSpace(ParkingSpace parkingSpace);

    @Query("SELECT * FROM parking_space WHERE id=:id")
    ParkingSpace getParkingSpace(long id);

}
