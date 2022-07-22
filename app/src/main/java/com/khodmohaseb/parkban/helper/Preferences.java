package com.khodmohaseb.parkban.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static String User_Name = "username";
    private static String Password = "password";
    private static String First = "First";
    private static String StatusSend = "StatusSend";
    private static String LastTime = "LastTime";
    private static String Remember_Password = "rememberPassword";
    private static String Server_Address = "serverAddress";

    private static SharedPreferences preferences;

    private static SharedPreferences getSharedPreferences(Context context) {
        String PREFS_NAME = "Setting";
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static void setPassword(String pass, Context context) {

        getSharedPreferences(context).edit()
                .putString(Password, pass)
                .apply();
    }

    public static String getPassword(Context context) {

        SharedPreferences pref = getSharedPreferences(context);

        String pass = pref.getString(Password, null);

        return (pass != null && !pass.isEmpty())
                ? pass
                : "";
    }

    public static void setUserName(String text, Context context) {

        getSharedPreferences(context).edit()
                .putString(User_Name, text)
                .apply();
    }

    public static String getUserName(Context context) {
        SharedPreferences pref = getSharedPreferences(context);

        String userName = pref.getString(User_Name, null);

        return (userName != null && !userName.isEmpty())
                ? userName
                : "";
    }














    public static void setServerAddress(String text, Context context) {

        getSharedPreferences(context).edit()
                .putString(Server_Address, text)
                .apply();
    }

    public static String getServerAddress(Context context) {
        SharedPreferences pref = getSharedPreferences(context);

        String serverAddress = pref.getString(Server_Address, null);

        return (serverAddress != null && !serverAddress.isEmpty())
                ? serverAddress
                : "https://apicloudparking.elmosanat.com/DeviceApi/";

    }












    public static void setFirstLogin(Boolean first, Context context){
        getSharedPreferences(context).edit()
                .putBoolean(First, first)
                .apply();
    }

    public static Boolean getFirstLogin( Context context){
        SharedPreferences pref = getSharedPreferences(context);

        Boolean first = pref.getBoolean(First,false);

        return first;
    }

    public static void setManualSending(boolean status, Context context) {
        getSharedPreferences(context).edit()
                .putBoolean(StatusSend, status)
                .apply();
    }

    public static boolean getManualSending(Context context) {

        SharedPreferences pref = getSharedPreferences(context);
        return pref.getBoolean(StatusSend,false);
    }

    public static boolean getRememberCheck(Context context){

        SharedPreferences pref = getSharedPreferences(context);
        Boolean check = pref.getBoolean(Remember_Password,false);

        return check;
    }

    public static void setRememberCheck(boolean check , Context context){
        getSharedPreferences(context).edit()
                .putBoolean(Remember_Password, check)
                .apply();
    }

    public static void setLastTime(long time, Context context) {
        getSharedPreferences(context).edit()
                .putLong(LastTime, time)
                .apply();
    }

    public static long getLastTime(Context context) {
        SharedPreferences pref = getSharedPreferences(context);
        return pref.getLong(LastTime,0);
    }
}
