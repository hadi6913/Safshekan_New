package com.khodmohaseb.parkban.services;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.khodmohaseb.parkban.helper.Preferences;
import com.khodmohaseb.parkban.helper.Utility;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ParkbanServiceProvider {

    private static final String HEADER_DEVICE_TOKEN = "DeviceToken";
////    public static final String BASE_URL = "http://192.168.10.164:3001/api/";
//    public static String BASE_URL = "";

    private static ParkbanService instance;
    private static String deviceToken;


    private ParkbanServiceProvider() {
//        BASE_URL = Preferences.getServerAddress(Utility.getContext());
    }

    public static synchronized ParkbanService getInstance() {
        if (instance == null) {
            instance = createParkbanService();
        }
        return instance;
    }



    public static   void  setInstanceNull() {
       instance = null;
    }


    private static ParkbanService createParkbanService() {




        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Utility.getContext());

        String baseUrl = preferences.getString("serveraddress", "");















        StringBuilder newUserName = new StringBuilder(baseUrl);


        for (int i = 0; i < baseUrl.length(); i++){
            if(Character.isDigit(baseUrl.charAt(i))){
                switch (baseUrl.charAt(i)){
                    case '۰':
                        newUserName.setCharAt(i, '0');
                        break;
                    case '۱':
                        newUserName.setCharAt(i, '1');
                        break;
                    case '۲':
                        newUserName.setCharAt(i, '2');
                        break;
                    case '۳':
                        newUserName.setCharAt(i, '3');
                        break;
                    case '۴':
                        newUserName.setCharAt(i, '4');
                        break;
                    case '۵':
                        newUserName.setCharAt(i, '5');
                        break;
                    case '۶':
                        newUserName.setCharAt(i, '6');
                        break;
                    case '۷':
                        newUserName.setCharAt(i, '7');
                        break;
                    case '۸':
                        newUserName.setCharAt(i, '8');
                        break;
                    case '۹':
                        newUserName.setCharAt(i, '9');
                        break;

                }
            }
        }

        baseUrl  = newUserName.toString();

















        HttpLoggingInterceptor myInterceptor = new HttpLoggingInterceptor();
        myInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .readTimeout(40, TimeUnit.SECONDS)
                .connectTimeout(40, TimeUnit.SECONDS);

        httpClient.addInterceptor(myInterceptor);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .addHeader("Accept", "*/*")
                        .addHeader("Content-type", "application/json");


                deviceToken =   preferences.getString("devicetoken", "");



                if (!TextUtils.isEmpty(deviceToken)) {
                    requestBuilder.addHeader(HEADER_DEVICE_TOKEN, deviceToken);
                }

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient.build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ParkbanService.class);
    }

    public static void setDeviceToken(String deviceToken) {
        ParkbanServiceProvider.deviceToken = deviceToken;
    }



}
