package com.khodmohaseb.parkban.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.khodmohaseb.parkban.persistence.models.khodmohaseb.EntranceRecord;
import com.khodmohaseb.parkban.persistence.models.khodmohaseb.ExitRecord;

import java.util.List;

@Dao
public interface ExitRecordDao {

    @Insert
    long saveExitRecord(ExitRecord exitRecord);


//    @Query("DELETE FROM exit_table WHERE plate in (:plateList)")
//    void deleteExitRecordByPlate(List<String> plateList);


    @Query("DELETE FROM exit_table WHERE plate=:plate")
    void deleteOneExitRecordByPlate(String plate);


    @Query("UPDATE exit_table SET is_send=1  WHERE plate in (:plateList)")
    void deleteExitRecordByPlate(List<String> plateList);

    @Query("SELECT * FROM exit_table  WHERE is_send==0 LIMIT 5")
    List<ExitRecord> getFiveExitRecord();


}
