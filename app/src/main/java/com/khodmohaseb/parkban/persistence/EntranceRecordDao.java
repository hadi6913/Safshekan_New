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

    @Query("SELECT * FROM entrance_table WHERE plate=:plate AND is_exit==0")
    EntranceRecord getEntranceRecordByPlate(String plate);



    @Query("UPDATE entrance_table SET is_exit=1  WHERE plate=:inputPlate")
    void setExitEntranceRecordByPlate(String inputPlate);
















//    @Query("DELETE FROM entrance_table WHERE plate in (:plateList)")
//    void deleteEntranceRecordByPlate(List<String> plateList);
//




    @Query("UPDATE entrance_table SET is_send=1  WHERE plate in (:plateList)")
    void deleteEntranceRecordByPlate(List<String> plateList);


    @Query("DELETE FROM entrance_table WHERE plate=:plate")
    void deleteOneEntranceRecordByPlate(String plate);


    @Query("SELECT * FROM entrance_table WHERE is_send==0 LIMIT 5")
    List<EntranceRecord> getFiveEntranceRecord();







}
