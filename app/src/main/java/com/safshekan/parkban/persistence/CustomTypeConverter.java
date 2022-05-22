package com.safshekan.parkban.persistence;

import android.arch.persistence.room.TypeConverter;

import com.safshekan.parkban.persistence.models.ParkingSpaceStatus;

import java.sql.Date;


public class CustomTypeConverter {

    @TypeConverter
    public static Date dateFromTimeStamp(Long timestamp) {
//        Time timeFormat = new Time();
//        timeFormat.set(timestamp - TimeZone.getDefault().getOffset(timestamp));
//        timestamp = timeFormat.toMillis(true);
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long dateToTimeStamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Integer toInteger(ParkingSpaceStatus status){
        return status.ordinal();
    }

    @TypeConverter
    public static ParkingSpaceStatus toStatus(int status){
        if (status == ParkingSpaceStatus.FULL.ordinal()) {
            return ParkingSpaceStatus.FULL;
        }else if (status == ParkingSpaceStatus.EMPTY.ordinal()) {
            return ParkingSpaceStatus.EMPTY;
        }
        else {
            throw new IllegalArgumentException("Could not recognize status");
        }
    }

}
