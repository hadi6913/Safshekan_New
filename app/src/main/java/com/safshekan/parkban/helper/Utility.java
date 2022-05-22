package com.safshekan.parkban.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;

import com.safshekan.parkban.services.ReaderHandler;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Utility {

    private static final String TAG =  "inside  Utility : ";

    private static Context context;
    private static Window window;

    public static Window getWindow() {
        return window;
    }

    public static void setWindow(Window window) {
        Utility.window = window;
    }

    public static Context getContext() {
        return context.getApplicationContext();
    }

    public static void setContext(Context context) {
        Utility.context = context;
    }

    public static String[] listFile(String folder, String ext) {
        GenericExtFilter filter = new GenericExtFilter(ext);
        File dir = new File(folder);
        // list out all the file name and filter by the extension
        return dir.list(filter);
    }

    public static void dirChecker(String path) {
        File f = new File(path);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }

    public static String getAppVersionName() {
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    getContext().getPackageName(), 0);
            return info.versionName;
        } catch (Exception ex) {

            Log.d(TAG, "getAppVersionName  exception: "+ex.toString());
            return "";
        }
    }

    @SuppressLint("LongLogTag")
    public static int getAppVersionCode() {
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    getContext().getPackageName(), 0);
            return info.versionCode;
        } catch (Exception ex) {
            Log.d("Utility --> getAppVersionCode():" , ex.getMessage());


            return 0;
        }
    }


    public static String imei = "";

    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String getIMEIFromOS() {
        try {
            if (!imei.equals(""))
                return imei;
            TelephonyManager manager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            imei = manager.getDeviceId(0);
            return imei;
        } catch (Exception ex) {
            Log.d("Utility --> getIMEIFromOS():" , ex.getMessage());

        }
        return "";
    }


}

class GenericExtFilter implements FilenameFilter {

    private String ext;

    public GenericExtFilter(String ext) {
        this.ext = ext;
    }

    public boolean accept(File dir, String name) {
        return (name.endsWith(ext));
    }
}
