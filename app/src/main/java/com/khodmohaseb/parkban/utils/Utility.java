package com.khodmohaseb.parkban.utils;

import android.app.AlarmManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.icu.util.TimeZone;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {

    private final static String TAG = "xeagle6913";

    private Context context;
    private static final Utility instance = new Utility();
    private Date lastServerConnectionTime = new Date();


    private Utility() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2000);
        lastServerConnectionTime = calendar.getTime();

    }



    public static Utility getInstance() {
        return instance;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getAppVersionCode() {
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    getContext().getPackageName(), 0);
            return info.versionCode;
        } catch (Exception ex) {
            Log.d(TAG, "getAppVersionCode exception : "+ex.getMessage());
            return 0;
        }
    }

    public static boolean isMidnightForIDLE() {
        try {
            Calendar start = Calendar.getInstance();
            start.set(Calendar.HOUR_OF_DAY, 22);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            Calendar end = Calendar.getInstance();
            end.set(Calendar.HOUR_OF_DAY, 5);
            end.set(Calendar.MINUTE, 0);
            end.set(Calendar.SECOND, 0);
            end.add(Calendar.DATE, 1);
            Calendar now = Calendar.getInstance();
            if ((start.getTimeInMillis() <= now.getTimeInMillis()) && (now.getTimeInMillis() <= end.getTimeInMillis()))
                return true;
        } catch (Exception ex) {
            Log.d(TAG, "isMidnightForIDLE exception : "+ex.getMessage());
        }
        return false;
    }


    public void setSystemDateAndTimeDirectly(Date d){
        try{
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            AlarmManager am = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            am.setTime(c.getTimeInMillis());
        }catch (Exception ex){
            Log.d(TAG, "setSystemDateAndTimeDirectly exception : "+ex.getMessage());
        }
    }

    public String getAppVersionName() {
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    getContext().getPackageName(), 0);
            return info.versionName;
        } catch (Exception ex) {
            Log.d(TAG, "getAppVersionName exception : "+ex.getMessage());
            return "";
        }
    }



    public boolean isMidnightForReboot(){
        try{
            Calendar start = Calendar.getInstance();
            start.set(Calendar.HOUR_OF_DAY, 2);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);

            Calendar end = Calendar.getInstance();
            end.set(Calendar.HOUR_OF_DAY, 2);
            end.set(Calendar.MINUTE, 1);
            end.set(Calendar.SECOND, 00);

            Calendar now = Calendar.getInstance();

            if ((start.getTimeInMillis() <= now.getTimeInMillis()) && (now.getTimeInMillis() <= end.getTimeInMillis()))
                return true;
        }catch (Exception ex){
            Log.d(TAG, "isMidnightForReboot exception : "+ex.getMessage());
        }
        return false;
    }

    public void rebootOS(){
        try{
            PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
            pm.reboot(null);
        }catch (Exception ex){
            Log.d(TAG, "rebootOS exception : "+ex.getMessage());
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getDateFromEpoch(long ephoch) {
        String result = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStartStr = "20000101000000";
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = simpleDateFormat.parse(dateStartStr);
            TimeZone timeZone = TimeZone.getDefault();
            int dts = 0;
            if (timeZone.inDaylightTime(new Date())) {
                dts = timeZone.getDSTSavings();
            }
            long milisc = date1.getTime() + (ephoch * 1000) - dts;
            date2 = new Date(milisc);
            result = simpleDateFormat.format(date2);
        } catch (ParseException ex) {
            Log.d(TAG, "getDateFromEpoch exception : "+ex.getMessage());
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public long getDateFrom2000() {
        String dateStartStr = "01/01/2000 00:00:00";
        Date date1 = null;
        Date date2 = null;
        long diff = 0;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        TimeZone timeZone = TimeZone.getDefault();
        long dst = 0;
        if (timeZone.inDaylightTime(new Date())) {
            dst = timeZone.getDSTSavings();
        }
        long currentTimeInMillis = System.currentTimeMillis();
        try {
            date1 = format.parse(dateStartStr);
            date2 = new Date(currentTimeInMillis);
            diff = (date2.getTime() - date1.getTime() + dst) / 1000;//epoch in seconds
        } catch (ParseException ex) {
            Log.d(TAG, "getDateFrom2000 exception : "+ex.getMessage());
        }
        return diff;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public long getDateFrom2000(Date date) {
        String dateStartStr = "01/01/2000 00:00:00";
        Date date1 = null;
        long diff = 0;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        TimeZone timeZone = TimeZone.getDefault();
        long dst = 0;
        if (timeZone.inDaylightTime(date)) {
            dst = timeZone.getDSTSavings();
        }
        try {
            date1 = format.parse(dateStartStr);
            diff = (date.getTime() - date1.getTime() + dst) / 1000;//epoch in seconds
        } catch (ParseException ex) {
            Log.d(TAG, "getDateFrom2000 exception : "+ex.getMessage());
        }
        return diff;
    }

    public long getCurrentDateInMilliSeconds() {
        long result = 0;
        try {
            Date currentDate = new Date();
            result = currentDate.getTime();
        } catch (Exception ex) {
            Log.d(TAG, "getCurrentDateInMilliSeconds exception : "+ex.getMessage());
        }
        return result;
    }


}
