package com.khodmohaseb.parkban.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.khodmohaseb.parkban.persistence.models.khodmohaseb.ExitRecord;
import com.khodmohaseb.parkban.persistence.models.khodmohaseb.TraffikRecord;

import java.util.List;


@Dao
public interface TraffikRecordDao {

    @Insert
    long saveTraffikRecord(TraffikRecord traffikRecord);


//    @Query("DELETE FROM traffik_table WHERE plate in (:plateList)")
//    void deleteTraffikRecordByPlate(List<String> plateList);
//


    @Query("UPDATE traffik_table SET is_send=1  WHERE plate in (:plateList)")
    void deleteTraffikRecordByPlate(List<String> plateList);


    @Query("SELECT * FROM traffik_table WHERE is_send==0 LIMIT 5")
    List<TraffikRecord> getFiveTraffikRecord();


}
