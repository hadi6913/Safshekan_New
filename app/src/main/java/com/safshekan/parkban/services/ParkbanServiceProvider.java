package com.safshekan.parkban.services;

import android.content.Context;
import android.text.TextUtils;

import com.safshekan.parkban.helper.Preferences;
import com.safshekan.parkban.helper.Utility;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ParkbanServiceProvider {

    private static final String HEADER_TOKEN = "UserToken";
////    public static final String BASE_URL = "http://192.168.10.164:3001/api/";
//    public static String BASE_URL = "";

    private static ParkbanService instance;
    private static String userToken;


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


        String baseUrl = Preferences.getServerAddress(Utility.getContext());










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
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-type", "application/json");

                if (!TextUtils.isEmpty(userToken)) {
                    requestBuilder.addHeader(HEADER_TOKEN, userToken);
                }

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient.build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ParkbanService.class);
    }

    public static void setUserToken(String userToken) {
        ParkbanServiceProvider.userToken = userToken;
    }



}
