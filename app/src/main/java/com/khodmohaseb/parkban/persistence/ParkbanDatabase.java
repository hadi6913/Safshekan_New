package com.khodmohaseb.parkban.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;

import com.khodmohaseb.parkban.persistence.models.Car;
import com.khodmohaseb.parkban.persistence.models.CarPlate;
import com.khodmohaseb.parkban.persistence.models.ParkingSpace;
import com.khodmohaseb.parkban.persistence.models.khodmohaseb.EntranceRecord;
import com.khodmohaseb.parkban.persistence.models.khodmohaseb.ExitRecord;
import com.khodmohaseb.parkban.persistence.models.khodmohaseb.PasswordOperatorRecord;
import com.khodmohaseb.parkban.persistence.models.khodmohaseb.TraffikRecord;

@Database(entities = {CarPlate.class, Car.class , PasswordOperatorRecord.class, ParkingSpace.class, EntranceRecord.class, ExitRecord.class, TraffikRecord.class}, version = 1, exportSchema = false)
@TypeConverters({CustomTypeConverter.class})
public abstract class ParkbanDatabase extends RoomDatabase {

    public abstract CarDao getCarDao();

    public abstract CarPlateDao getCarPlateDao();

    public abstract ParkingSpaceDao getParkingSpaceDao();


    public abstract PasswordOperatorRecordDao getPasswordOperatorRecordDao();



    public abstract EntranceRecordDao getEntranceRecordDao();
    public abstract ExitRecordDao getExitRecordDao();
    public abstract TraffikRecordDao getTraffikRecordDao();




    private static ParkbanDatabase instance;

    public interface DatabaseReadyCallback{
        void onReady(ParkbanDatabase instance);
    }

    public synchronized static ParkbanDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), ParkbanDatabase.class, "parkban_db").build();
        }
        return instance;
    }

    public static void getInstance(final Context context, DatabaseReadyCallback callback) {
        if (instance != null) {
            callback.onReady(instance);
            return;
        }

        new GetInstanceAsyncTask(callback)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
    }

    private static class GetInstanceAsyncTask extends AsyncTask<Context, Void, ParkbanDatabase> {

        private final DatabaseReadyCallback callback;

        public GetInstanceAsyncTask(DatabaseReadyCallback callback) {
            this.callback = callback;
        }

        @Override
        protected ParkbanDatabase doInBackground(Context... contexts) {
            return getInstance(contexts[0]);
        }

        @Override
        protected void onPostExecute(ParkbanDatabase database) {
            super.onPostExecute(database);
            callback.onReady(database);
        }
    }

}
