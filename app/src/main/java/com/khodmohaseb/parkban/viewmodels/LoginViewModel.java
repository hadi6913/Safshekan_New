package com.khodmohaseb.parkban.viewmodels;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.khodmohaseb.parkban.BaseActivity;
import com.khodmohaseb.parkban.LoginActivity;
import com.khodmohaseb.parkban.MainActivity;
import com.khodmohaseb.parkban.R;
import com.khodmohaseb.parkban.helper.EncryptionPassword;
import com.khodmohaseb.parkban.helper.FontHelper;
import com.khodmohaseb.parkban.helper.Preferences;
import com.khodmohaseb.parkban.helper.ShowToast;
import com.khodmohaseb.parkban.helper.Utility;
import com.khodmohaseb.parkban.persistence.ParkbanDatabase;
import com.khodmohaseb.parkban.persistence.models.ResponseResultType;
import com.khodmohaseb.parkban.persistence.models.User;
import com.khodmohaseb.parkban.repositories.ParkbanRepository;
import com.khodmohaseb.parkban.services.ParkbanServiceProvider;
import com.khodmohaseb.parkban.services.dto.LoginResultDto;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.Door;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.GetParkingInfoResponse;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.Operator;
import com.khodmohaseb.parkban.utils.Animation_Constant;
import com.khodmohaseb.parkban.utils.MyBounceInterpolator;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class LoginViewModel extends ViewModel {

    private ParkbanRepository parkbanRepository;
    private GetParkingInfoResponse getParkingInfoResponse;


    private String userName;
    private String password, finalPassword;
    private Context context;
    private String version;
    private boolean loc = false;
    private MutableLiveData<Boolean> rememberPassword;
    private User user;



    private  ArrayList<String> userListArray = new ArrayList<String>();
    private ArrayList<String> doorListArray = new ArrayList<String>();
    private String[] userList;
    private String[] doorList;

    public ArrayAdapter<CharSequence> langAdapter_user;
    public ArrayAdapter<CharSequence> langAdapter_door;

    public String parkingName;



    public User getUser() {
        return user;
    }

    public LiveData<Boolean> getRememberPassword() {
        if (rememberPassword == null)
            rememberPassword = new MutableLiveData<>();
        return rememberPassword;
    }

    public String getVersion() {
        if (version == null)
            // version = "test3";
            version = BaseActivity.Version;
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public MutableLiveData<Integer> progress = new MutableLiveData<>();

    public LiveData<Integer> getProgress() {
        return progress;
    }

    public void init(final Context context) {
        this.context = context;
        if (parkbanRepository == null) {
            ParkbanDatabase.getInstance(context.getApplicationContext(), new ParkbanDatabase.DatabaseReadyCallback() {
                @Override
                public void onReady(ParkbanDatabase instance) {
                    parkbanRepository = new ParkbanRepository(instance);
                }
            });
        }
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = preferences.getString("parkinginfo", "");
        if (json.equals("")) {
            getParkingInfoResponse = null;
        } else {
            getParkingInfoResponse = gson.fromJson(json, GetParkingInfoResponse.class);
        }


        if (getParkingInfoResponse == null) {


            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            View alertView = inflater.inflate(R.layout.dialog_settings_no_parameter, null);
            alertDialog.setView(alertView);
            alertDialog.setCancelable(false);
            final AlertDialog show = alertDialog.show();
            LinearLayout button = alertView.findViewById(R.id.confirm_layout_settings_noparameter);
            final EditText serverEditText = (EditText) alertView.findViewById(R.id.server_address_edittext_noparameter);


            String serverAddress = preferences.getString("serveraddress", "");

            if (serverAddress.equals("")) {
                serverAddress = "http://93.118.101.126:8046/DeviceApi/";
            }

            serverEditText.setText(serverAddress);


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ((serverEditText.getText().toString().trim() != null) && (!serverEditText.getText().toString().trim().equals(""))) {

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("serveraddress", serverEditText.getText().toString().trim());
                        editor.commit();


                        ParkbanServiceProvider.setInstanceNull();

                        progress.setValue(10);
                        //todo implement getting imei from device


                        try {

                            parkbanRepository.getParkingInformation("\"358351080272763\"",
                                    new ParkbanRepository.ServiceResultCallBack<GetParkingInfoResponse>() {
                                        @Override
                                        public void onSuccess(GetParkingInfoResponse result) {
                                            progress.setValue(0);

                                            Gson gson = new GsonBuilder()
                                                    .serializeNulls()
                                                    .create();
                                            String json = gson.toJson(result);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("parkinginfo", json);
                                            editor.commit();


                                            //todo init login view


                                            parkingName = "پارکینگ "+result.getParkingName();

                                            userListArray.clear();
                                            doorListArray.clear();



                                            for (Operator item:result.getOperators()) {
                                                if(item.getIsActive()){
                                                    userListArray.add(item.getUserName());
                                                }
                                            }

                                            for (Door item:result.getDoors()) {

                                                    userListArray.add(item.getDoorName());

                                            }


                                            userList = new String[userListArray.size()];
                                            userList = userListArray.toArray(userList);

                                            doorList = new String[doorListArray.size()];
                                            doorList = doorListArray.toArray(doorList);



                                            langAdapter_user = new ArrayAdapter<CharSequence>(context, R.layout.spinner_text,userList);
                                            langAdapter_user.setDropDownViewResource(R.layout.simple_spinner_dropdown);



                                            langAdapter_door = new ArrayAdapter<CharSequence>(context, R.layout.spinner_text,doorList);
                                            langAdapter_door.setDropDownViewResource(R.layout.simple_spinner_dropdown);


                                            show.dismiss();

                                             ((Activity) context).recreate();



                                        }

                                        @Override
                                        public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                            progress.setValue(0);


                                            switch (resultType) {
                                                case RetrofitError:
                                                    ShowToast.getInstance().showError(context, R.string.exception_msg);
                                                    break;
                                                case ServerError:
                                                    if (errorCode != 0)
                                                        ShowToast.getInstance().showError(context, errorCode);
                                                    else {
                                                        ShowToast.getInstance().showError(context, R.string.connection_failed);

                                                    }
                                                    break;
                                                default:
                                                    ShowToast.getInstance().showError(context, resultType.ordinal());
                                            }
                                        }
                                    });

                        } catch (Exception e) {
                            progress.setValue(0);
                            ShowToast.getInstance().showError(context, R.string.error_server_address);
                        }


                    } else {


                        ShowToast.getInstance().showError(context, R.string.error_server_address);
                    }


                }
            });


        } else {
            //todo init login view
            parkingName = "پارکینگ "+getParkingInfoResponse.getParkingName();

            userListArray.clear();
            doorListArray.clear();



            for (Operator item:getParkingInfoResponse.getOperators()) {
                if(item.getIsActive()){
                    userListArray.add(item.getUserName());
                }
            }

            for (Door item:getParkingInfoResponse.getDoors()) {

                doorListArray.add(item.getDoorName());

            }


            userList = new String[userListArray.size()];
            userList = userListArray.toArray(userList);

            doorList = new String[doorListArray.size()];
            doorList = doorListArray.toArray(doorList);






            langAdapter_user = new ArrayAdapter<CharSequence>(context, R.layout.spinner_text,userList);
            langAdapter_user.setDropDownViewResource(R.layout.simple_spinner_dropdown);



            langAdapter_door = new ArrayAdapter<CharSequence>(context, R.layout.spinner_text,doorList);
            langAdapter_door.setDropDownViewResource(R.layout.simple_spinner_dropdown);







        }


    }

    public void loginClick(View view) {

        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_bubble_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //your code

                EncryptionPassword pass = new EncryptionPassword();

                if (!loc) {
                    loc = ((LoginActivity) context).checkLocationPermission1(BaseActivity.LoginActivity);
                    return;
                }

                if (user.getUserName().isEmpty()) {
                    ShowToast.getInstance().showError(context, R.string.username_required);
                    return;
                }
                if (user.getPassword().isEmpty()) {
                    ShowToast.getInstance().showError(context, R.string.password_required);
                    return;
                }


                userName = FontHelper.removeEnter(FontHelper.convertArabicToPersian(user.getUserName()));


                StringBuilder newUserName = new StringBuilder(userName);


                for (int i = 0; i < userName.length(); i++) {
                    if (Character.isDigit(userName.charAt(i))) {
                        switch (userName.charAt(i)) {
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

                userName = newUserName.toString();
                password = FontHelper.toEnglishNumber(FontHelper.removeEnter(user.getPassword()));


                try {
                    byte[] bytes = password.getBytes("ASCII");
                    finalPassword = FontHelper.removeEnter(pass.encryptAsBase64(bytes));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                progress.setValue(10);
                parkbanRepository.login(userName, finalPassword,
                        new ParkbanRepository.ServiceResultCallBack<LoginResultDto.Parkban>() {
                            @Override
                            public void onSuccess(LoginResultDto.Parkban result) {
                                progress.setValue(0);

//
//                        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//                        //here in below lines we will modify the app menus via access detail code
//                        Long accessDetailCode = result.getAccessDetails().get(0);
//                        if ((4&accessDetailCode)==4){
//                            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("enablekifepool",true).apply();
//                            Log.d("xeagle69", "login>>onSuccess>>enable_kife_pool.......>>>>true");
//                        }else {
//                            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("enablekifepool",false).apply();
//                            Log.d("xeagle69", "login>>onSuccess>>enable_kife_pool.......>>>>false");
//                        }
//                        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


                                if (userName != null)
                                    Preferences.setUserName(userName, context);
//                        if (password != null)
//                            Preferences.setPassword(password, context);
                                if (rememberPassword.getValue()) {
                                    Preferences.setPassword(password, context);
                                    Preferences.setRememberCheck(true, context);
                                } else {
                                    Preferences.setPassword("", context);
                                    Preferences.setRememberCheck(false, context);
                                }

//                        ((BaseActivity) context).getPrakbanCurrentShift(result.getParkbanId());

                                ParkbanServiceProvider.setUserToken(result.getUserToken());

//                        BaseActivity.PhoneComplaints = result.getPhoneComplaints();
//                        BaseActivity.ParkbanPhoneNumber = result.getParkbanPhoneNumber();
//                        BaseActivity.CurrentUserId = result.getParkbanId();
//                        BaseActivity.parkingSpaceList = result.getParkingSpaces();
//                        Log.i("========------==>", "result.getParkingSpaces() " + result.getParkingSpaces().size());

                                ((BaseActivity) context).finish();
                                Intent i = new Intent(context, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(i);
                            }

                            @Override
                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                progress.setValue(0);
                                switch (resultType) {
                                    case RetrofitError:
                                        ShowToast.getInstance().showError(context, R.string.exception_msg);
                                        // Messenger.showErrorMessage(view.getContext(), R.string.exception_msg);
                                        break;
                                    case ServerError:
                                        if (errorCode != 0)
                                            ShowToast.getInstance().showError(context, errorCode);
                                        else {
                                            ShowToast.getInstance().showError(context, R.string.connection_failed);
                                            Log.d("xhxhxh", "resultType>>" + resultType.getValue() + resultType.getDescription());
                                            Log.d("xhxhxh", "message>>" + message);
                                            Log.d("xhxhxh", "errorcode>>" + errorCode);
                                        }


                                        break;
                                    default:
                                        ShowToast.getInstance().showError(context, resultType.ordinal());
                                }
                            }
                        });

            }
        }, Animation_Constant.ANIMATION_VALUE);


    }


    public void settingsClick(View view) {

        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_bubble_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                View alertView = inflater.inflate(R.layout.dialog_settings, null);
                alertDialog.setView(alertView);
                alertDialog.setCancelable(true);
                final AlertDialog show = alertDialog.show();
                LinearLayout button = alertView.findViewById(R.id.confirm_layout_settings);
                final EditText serverEditText = (EditText) alertView.findViewById(R.id.server_address_edittext);


                String serverAddress = preferences.getString("serveraddress", "");

                if (serverAddress.equals("")) {
                    serverAddress = "http://93.118.101.126:8046/DeviceApi/";
                }

                serverEditText.setText(serverAddress);


                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if ((serverEditText.getText().toString().trim() != null) && (!serverEditText.getText().toString().trim().equals(""))) {

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("serveraddress", serverEditText.getText().toString().trim());
                            editor.commit();


                            ParkbanServiceProvider.setInstanceNull();

                            progress.setValue(10);
                            //todo implement getting imei from device


                            try {

                                parkbanRepository.getParkingInformation("\"358351080272763\"",
                                        new ParkbanRepository.ServiceResultCallBack<GetParkingInfoResponse>() {
                                            @Override
                                            public void onSuccess(GetParkingInfoResponse result) {
                                                progress.setValue(0);

                                                Gson gson = new GsonBuilder()
                                                        .serializeNulls()
                                                        .create();
                                                String json = gson.toJson(result);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("parkinginfo", json);
                                                editor.commit();


                                                //todo init login view

                                                parkingName = "پارکینگ "+result.getParkingName();


                                                userListArray.clear();
                                                doorListArray.clear();



                                                for (Operator item:result.getOperators()) {
                                                    if(item.getIsActive()){
                                                        userListArray.add(item.getUserName());
                                                    }
                                                }

                                                for (Door item:result.getDoors()) {

                                                    userListArray.add(item.getDoorName());

                                                }


                                                userList = new String[userListArray.size()];
                                                userList = userListArray.toArray(userList);

                                                doorList = new String[doorListArray.size()];
                                                doorList = doorListArray.toArray(doorList);


                                                langAdapter_user = new ArrayAdapter<CharSequence>(context, R.layout.spinner_text,userList);
                                                langAdapter_user.setDropDownViewResource(R.layout.simple_spinner_dropdown);



                                                langAdapter_door = new ArrayAdapter<CharSequence>(context, R.layout.spinner_text,doorList);
                                                langAdapter_door.setDropDownViewResource(R.layout.simple_spinner_dropdown);

                                                show.dismiss();

                                                ((Activity) context).recreate();


                                            }

                                            @Override
                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                progress.setValue(0);


                                                switch (resultType) {
                                                    case RetrofitError:
                                                        ShowToast.getInstance().showError(context, R.string.exception_msg);
                                                        break;
                                                    case ServerError:
                                                        if (errorCode != 0)
                                                            ShowToast.getInstance().showError(context, errorCode);
                                                        else {
                                                            ShowToast.getInstance().showError(context, R.string.connection_failed);

                                                        }
                                                        break;
                                                    default:
                                                        ShowToast.getInstance().showError(context, resultType.ordinal());
                                                }
                                            }
                                        });

                            } catch (Exception e) {
                                progress.setValue(0);
                                ShowToast.getInstance().showError(context, R.string.error_server_address);
                            }


                        } else {


                            ShowToast.getInstance().showError(context, R.string.error_server_address);
                        }


                    }
                });


            }
        }, Animation_Constant.ANIMATION_VALUE);


    }

    public void fillDefaultUserAndPass(Context context) {
        String userName = Preferences.getUserName(context);
        Log.d("xeagle69", "fillDefaultUserAndPass>> username >>>  " + userName);
        String password = Preferences.getPassword(context);
        Log.d("xeagle69", "fillDefaultUserAndPass>> password >>>  " + userName);
        boolean rememberPass = Preferences.getRememberCheck(context);

        if (userName != null) {
            user.setUserName(userName);
        }
//        if (password != null)
//            setPassword(password);
        if (rememberPass) {
            user.setPassword(password);
            rememberPassword.setValue(true);
        } else {
            user.setPassword("");
            rememberPassword.setValue(false);
        }
    }

    public void getRememberPassStatus(View view) {
        if (rememberPassword.getValue())
            rememberPassword.setValue(false);
        else
            rememberPassword.setValue(true);
    }

}
