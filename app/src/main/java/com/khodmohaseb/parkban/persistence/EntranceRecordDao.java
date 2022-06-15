package com.khodmohaseb.parkban.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.khodmohaseb.parkban.persistence.models.khodmohaseb.EntranceRecord;

import java.sql.Date;
import java.util.List;

@Dao
public interface EntranceRecordDao {

    @Insert
    long saveEntranceRecord(EntranceRecord entranceRecord);

    @Query("SELECT * FROM entrance_table WHERE plate=:plate")
    EntranceRecord getEntranceRecordByPlate(String plate);

    @Query("DELETE FROM entrance_table WHERE plate=:plate")
    void deleteEntranceRecordByPlate(String plate);




}
