package com.khodmohaseb.parkban.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.khodmohaseb.parkban.persistence.models.khodmohaseb.EntranceRecord;
import com.khodmohaseb.parkban.persistence.models.khodmohaseb.PasswordOperatorRecord;

import java.util.List;

@Dao
public interface PasswordOperatorRecordDao {

    @Insert
    long savePasswordOperatorRecord(PasswordOperatorRecord passwordOperatorRecord);

    @Query("SELECT * FROM change_password ")
    List<PasswordOperatorRecord> getAllPasswordOperatorRecord();

    @Query("DELETE FROM change_password")
    int deleteAllRecords();


















}
