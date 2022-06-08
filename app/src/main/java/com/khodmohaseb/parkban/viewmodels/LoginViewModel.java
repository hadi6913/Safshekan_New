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
import com.khodmohaseb.parkban.EnterMifareActivity;
import com.khodmohaseb.parkban.EnterQrActivity;
import com.khodmohaseb.parkban.ExitMifareActivity;
import com.khodmohaseb.parkban.ExitQrActivity;
import com.khodmohaseb.parkban.LoginActivity;
import com.khodmohaseb.parkban.MainActivity;
import com.khodmohaseb.parkban.R;
import com.khodmohaseb.parkban.SelectModeActivity;
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

    private final static String TAG = "xeagle_LoginViewModel";

    private ParkbanRepository parkbanRepository;
    private GetParkingInfoResponse getParkingInfoResponse;


    private String userName;
    private String password, finalPassword;
    private Context context;
    private String version;
    private boolean loc = false;
    private MutableLiveData<Boolean> rememberPassword;


    private ArrayList<String> userListArray = new ArrayList<String>();
    private ArrayList<String> doorListArray = new ArrayList<String>();
    private String[] userList;
    private String[] doorList;

    public ArrayAdapter<CharSequence> langAdapter_user;
    public ArrayAdapter<CharSequence> langAdapter_door;

    public String enteredPassword;

    public String parkingName;


    public Spinner mUserSpinner;
    public Spinner mDoorSpinner;


    private MutableLiveData<String> selectedUserName;
    private MutableLiveData<String> selectedDoorName;


    public MutableLiveData<String> getSelectedUserName() {
        if (selectedUserName == null)
            selectedUserName = new MutableLiveData<>();
        return selectedUserName;
    }


    public MutableLiveData<String> getSelectedDoorName() {
        if (selectedDoorName == null)
            selectedDoorName = new MutableLiveData<>();
        return selectedDoorName;
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


                            parkbanRepository.getDeviceToken("\"868500040082190\"", new ParkbanRepository.ServiceResultCallBack<String>() {
                                @Override
                                public void onSuccess(String deviceToken) {


                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("devicetoken", deviceToken);
                                    editor.commit();


                                    ParkbanServiceProvider.setInstanceNull();






                                    parkbanRepository.getParkingInformation("\"868500040082190\"",
                                            new ParkbanRepository.ServiceResultCallBack<GetParkingInfoResponse>() {
                                                @Override
                                                public void onSuccess(GetParkingInfoResponse result) {
                                                    progress.setValue(0);


                                                    getParkingInfoResponse = result;

                                                    Gson gson = new GsonBuilder()
                                                            .serializeNulls()
                                                            .create();
                                                    String json = gson.toJson(result);
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.putString("parkinginfo", json);
                                                    editor.commit();


//                                            parkingName = "پارکینگ " + result.getParkingName();
                                                    parkingName = result.getParkingName();

                                                    userListArray.clear();
                                                    doorListArray.clear();


                                                    for (Operator item : result.getOperators()) {
                                                        if (item.getIsActive()) {
                                                            userListArray.add(item.getUserName());
                                                        }
                                                    }

                                                    for (Door item : result.getDoors()) {

                                                        userListArray.add(item.getDoorName());

                                                    }


                                                    userList = new String[userListArray.size()];
                                                    userList = userListArray.toArray(userList);

                                                    doorList = new String[doorListArray.size()];
                                                    doorList = doorListArray.toArray(doorList);


                                                    langAdapter_user = new ArrayAdapter<CharSequence>(context, R.layout.spinner_text, userList);
                                                    langAdapter_user.setDropDownViewResource(R.layout.simple_spinner_dropdown);


                                                    langAdapter_door = new ArrayAdapter<CharSequence>(context, R.layout.spinner_text, doorList);
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

//            parkingName = "پارکینگ " + getParkingInfoResponse.getParkingName();
            parkingName = getParkingInfoResponse.getParkingName();

            userListArray.clear();
            doorListArray.clear();


            for (Operator item : getParkingInfoResponse.getOperators()) {
                if (item.getIsActive()) {
                    userListArray.add(item.getUserName());
                }
            }

            for (Door item : getParkingInfoResponse.getDoors()) {

                doorListArray.add(item.getDoorName());

            }


            userList = new String[userListArray.size()];
            userList = userListArray.toArray(userList);

            doorList = new String[doorListArray.size()];
            doorList = doorListArray.toArray(doorList);


            langAdapter_user = new ArrayAdapter<CharSequence>(context, R.layout.spinner_text, userList);
            langAdapter_user.setDropDownViewResource(R.layout.simple_spinner_dropdown);


            langAdapter_door = new ArrayAdapter<CharSequence>(context, R.layout.spinner_text, doorList);
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

                Operator selectedUser = null;
                for (Operator item : getParkingInfoResponse.getOperators()) {
                    if (item.getUserName().trim().equals(getSelectedUserName().getValue().toString().trim())) {
                        selectedUser = item;
                    }
                }
                Door selectedDoor = null;
                for (Door item : getParkingInfoResponse.getDoors()) {
                    if (item.getDoorName().trim().equals(getSelectedDoorName().getValue().toString().trim())) {
                        selectedDoor = item;
                    }
                }


                //todo remove below line
                enteredPassword = "rewq1234@";


                if ((enteredPassword != null) && (!enteredPassword.trim().equals(""))) {

                    if (selectedUser.getUserPass().trim().equals(enteredPassword.trim())) {

                        Log.d(TAG, "should login now");


                        Gson gson = new GsonBuilder()
                                .serializeNulls()
                                .create();
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

                        String json_user = gson.toJson(selectedUser);
                        editor.putString("currentuser", json_user);
                        editor.commit();

                        String json_door = gson.toJson(selectedDoor);
                        editor.putString("currentdoor", json_door);
                        editor.commit();


                        switch (selectedDoor.getDoorType().intValue()) {

                            case 0:
                                //ورود


                                if (getParkingInfoResponse.getCardKind().intValue() == 0) {
                                    //Mifare
                                    ((BaseActivity) context).finish();
                                    Intent i = new Intent(context, EnterMifareActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(i);

                                } else {
                                    //QR
                                    ((BaseActivity) context).finish();
                                    Intent i = new Intent(context, EnterQrActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(i);

                                }


                                break;

                            case 1:
                                //خروج


                                if (getParkingInfoResponse.getCardKind().intValue() == 0) {
                                    //Mifare

                                    ((BaseActivity) context).finish();
                                    Intent i = new Intent(context, ExitMifareActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(i);

                                } else {
                                    //QR


                                    ((BaseActivity) context).finish();
                                    Intent i = new Intent(context, ExitQrActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(i);

                                }


                                break;

                            case 2:
                                //ورود-خروج


                                ((BaseActivity) context).finish();
                                Intent i = new Intent(context, SelectModeActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(i);


                                break;


                        }


                    } else {
                        ShowToast.getInstance().showError(context, R.string.wrong_password);
                    }


                } else {

                    ShowToast.getInstance().showError(context, R.string.enter_password);
                }


            }
        }, Animation_Constant.ANIMATION_VALUE);


    }


    public void enFaClick(View view) {

        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_bubble_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                String lan = PreferenceManager.getDefaultSharedPreferences(context).getString("language", "fa");

                if (lan.equals("fa")) {
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString("language", "en").commit();
                } else {
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString("language", "fa").commit();
                }


                ((Activity) context).recreate();


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




                                parkbanRepository.getDeviceToken("\"868500040082190\"", new ParkbanRepository.ServiceResultCallBack<String>() {
                                    @Override
                                    public void onSuccess(String deviceToken) {


                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("devicetoken", deviceToken);
                                        editor.commit();


                                        ParkbanServiceProvider.setInstanceNull();






                                        parkbanRepository.getParkingInformation("\"868500040082190\"",
                                                new ParkbanRepository.ServiceResultCallBack<GetParkingInfoResponse>() {
                                                    @Override
                                                    public void onSuccess(GetParkingInfoResponse result) {
                                                        progress.setValue(0);


                                                        getParkingInfoResponse = result;

                                                        Gson gson = new GsonBuilder()
                                                                .serializeNulls()
                                                                .create();
                                                        String json = gson.toJson(result);
                                                        SharedPreferences.Editor editor = preferences.edit();
                                                        editor.putString("parkinginfo", json);
                                                        editor.commit();


//                                            parkingName = "پارکینگ " + result.getParkingName();
                                                        parkingName = result.getParkingName();

                                                        userListArray.clear();
                                                        doorListArray.clear();


                                                        for (Operator item : result.getOperators()) {
                                                            if (item.getIsActive()) {
                                                                userListArray.add(item.getUserName());
                                                            }
                                                        }

                                                        for (Door item : result.getDoors()) {

                                                            userListArray.add(item.getDoorName());

                                                        }


                                                        userList = new String[userListArray.size()];
                                                        userList = userListArray.toArray(userList);

                                                        doorList = new String[doorListArray.size()];
                                                        doorList = doorListArray.toArray(doorList);


                                                        langAdapter_user = new ArrayAdapter<CharSequence>(context, R.layout.spinner_text, userList);
                                                        langAdapter_user.setDropDownViewResource(R.layout.simple_spinner_dropdown);


                                                        langAdapter_door = new ArrayAdapter<CharSequence>(context, R.layout.spinner_text, doorList);
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


}
