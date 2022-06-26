package com.khodmohaseb.parkban;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.khodmohaseb.parkban.helper.ImageLoadHelper;
import com.khodmohaseb.parkban.helper.LocationTracker;
import com.khodmohaseb.parkban.helper.Preferences;
import com.khodmohaseb.parkban.helper.ShowToast;
import com.khodmohaseb.parkban.persistence.ParkbanDatabase;
import com.khodmohaseb.parkban.persistence.models.Car;
import com.khodmohaseb.parkban.persistence.models.CarPlate;
import com.khodmohaseb.parkban.persistence.models.ResponseResultType;
import com.khodmohaseb.parkban.persistence.models.SendStatus;
import com.khodmohaseb.parkban.repositories.ParkbanRepository;
import com.khodmohaseb.parkban.services.SendRecordHandler;
import com.khodmohaseb.parkban.services.dto.CarRecordsDto;
import com.khodmohaseb.parkban.services.dto.CurrentShiftDto;
import com.khodmohaseb.parkban.services.dto.ParkingSpaceDto;
import com.khodmohaseb.parkban.services.dto.SendRecordResultDto;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaseActivity extends AppCompatActivity implements SendRecordHandler.Callbacks {

    public static long CurrentUserId;
    public static long ParkbanPhoneNumber;
    public static String PhoneComplaints;
    public static List<ParkingSpaceDto> parkingSpaceList;
    public static List<ParkingSpaceDto> parkingSpaceListShift;
    public static int BeginTimeShift, EndTimeShift;
    public static String LoginActivity = "Login";
    public static String RecordActivity = "Record";
    public static String ListActivity = "List";
    public static String MainActivity = "Main";
    public static String Version;
    public static boolean _isActive = false;
    public static boolean SendAutomatic = true;
    public static int marginLimit;
    public static LocationTracker locationTracker;
    public static String DeviceModel = android.os.Build.MODEL;

    private String source;
    private Dialog progressDialog;
    private ParkbanRepository parkbanRepository;
    private Handler handler = new Handler();
    private final int interval = 100000;  // 1 minutes

    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;



    //**********************************************************************************************
    //**********************************************************************************************
    //**********************************************************************************************
    Intent sendRecordHandlerIntent;
    SendRecordHandler sendRecordHandler;
    private ServiceConnection sendRecordHandlerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            SendRecordHandler.LocalBinder binder = (SendRecordHandler.LocalBinder) service;
            sendRecordHandler = binder.getServiceInstance(); //Get instance of your service!
            sendRecordHandler.registerClient(BaseActivity.this); //Activity register in the service as client for callabcks!
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
    //**********************************************************************************************
    //**********************************************************************************************
    //**********************************************************************************************

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Version = getPackageManager().getPackageInfo(getBaseContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (parkbanRepository == null) {
            ParkbanDatabase.getInstance(getApplicationContext(), new ParkbanDatabase.DatabaseReadyCallback() {
                @Override
                public void onReady(ParkbanDatabase instance) {
                    parkbanRepository = new ParkbanRepository(instance);
                }
            });
        }

        if (locationTracker == null)
            locationTracker = new LocationTracker(BaseActivity.this);




        sendRecordHandlerIntent = new Intent(getApplicationContext(), SendRecordHandler.class);
        Log.d("BaseActivity", "onCreate: MainActivity >>>> Service has been started");

        startService(sendRecordHandlerIntent);
        getApplicationContext().bindService(sendRecordHandlerIntent, sendRecordHandlerConnection, Context.BIND_AUTO_CREATE);




    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("BaseActivity", "onCreate: MainActivity >>>> Service has been stoped");
        stopService(sendRecordHandlerIntent);
        getApplicationContext().unbindService( sendRecordHandlerConnection);
    }

    public void showProgress(final boolean show) {
        try {
            if (progressDialog == null) {
                progressDialog = new Dialog(this, android.R.style.Theme_Black);
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.control_progress_dialog, null);

                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                progressDialog.setContentView(view);
            }
            if (show)
                progressDialog.show();
            else

                progressDialog.dismiss();

        } catch (Exception e) {
            Log.i("progress ex", e.getMessage());
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission(String source) {
        this.source = source;
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    public boolean checkLocationPermission1(String source) {
        this.source = source;
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                return false;
            } else
                return true;
        }
        return true;
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {

                            ShowToast.getInstance().showError(this, R.string.location_permission_deny);

                            if (source == RecordActivity || source == ListActivity) {
                                Intent i = new Intent(this, LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }

                            return;
                        }
                    }
                }

                break;
        }
    }

    public boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(BaseActivity.this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(BaseActivity.this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    public void setHandler() {

        if (hasNetConnection())
            if (!Preferences.getManualSending(BaseActivity.this)) {
                //ShowToast.getInstance().showErrorStringMsg(BaseActivity.this, "Automatic");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendRecord();
                        Log.i("----------------->", "base handler ");

                        handler.postDelayed(this, interval);
                    }
                }, interval);
            } else {
                //ShowToast.getInstance().showErrorStringMsg(BaseActivity.this, "manual");
                handler.removeCallbacksAndMessages(null);
                Log.i("----------------->", "base else a ");
            }

    }

    public void sendRecord() {
        if (Preferences.getManualSending(BaseActivity.this))
            return;

        parkbanRepository.getAllCars(new ParkbanRepository.DataBaseCarsResultCallBack() {
            @Override
            public void onSuccess(List<Car> cars) {

                for (final Car car : cars) {
                    parkbanRepository.getAllCarPlates(car.getId(), new ParkbanRepository.DataBaseCarPlatesResultCallBack() {
                        @Override
                        public void onSuccess(List<CarPlate> carPlates) {
                            car.getCarPlates().addAll(carPlates);
                            makeCarRecord(car);
                        }

                        @Override
                        public void onFailed() {
                        }
                    });
                }
            }

            @Override
            public void onFailed() {
            }
        });
    }

    private void makeCarRecord(Car car) {

        CarRecordsDto carRecords = new CarRecordsDto();

        for (int j = 0; j < car.getCarPlates().size(); j++) {

            if (car.getCarPlates().get(j).getStatus() != SendStatus.SENT.ordinal()) {

                carRecords.setLatitude(car.getLatitude());
                carRecords.setLongitude(car.getLongitude());
                carRecords.setPlateNo(car.getPlateNo());
                carRecords.setUserId(BaseActivity.CurrentUserId);
                carRecords.setFirstParkDate(new Date(car.getCarPlates().get(0).getRecordDate().getTime()));
                carRecords.setImageIntArray(ImageLoadHelper.getInstance().convertImageFileToIntArray(this, car.getCarPlates().get(j).getPlateFileName()));

                carRecords.setDateTime(new Date(car.getCarPlates().get(j).getRecordDate().getTime()));
                carRecords.setExit(car.getCarPlates().get(j).isExit());
                carRecords.setParkId(car.getCarPlates().get(j).getId());
                carRecords.setParkingSpaceId(car.getParkingSpaceId());
                carRecords.setVerificationStatus(car.getCarPlates().get(j).getEditedPlate());

                sendToServer(carRecords);
            }
        }
    }

    private void sendToServer(CarRecordsDto record) {
        parkbanRepository.sendRecord(record,
                new ParkbanRepository.ServiceResultCallBack<SendRecordResultDto.SendRecordStatus>() {
                    @Override
                    public void onSuccess(final SendRecordResultDto.SendRecordStatus result) {
                        parkbanRepository.getAndUpdateCarPlate(result.getParkId(), result.isStatus(), new ParkbanRepository.DataBaseCarPlateUpdateCallBack() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onFailed() {

                            }
                        });
                    }

                    @Override
                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {

                    }

                });
    }

    public void getPrakbanCurrentShift(long parkbanId) {
        parkbanRepository.getCurrentShift(parkbanId, new ParkbanRepository.ServiceResultCallBack<CurrentShiftDto>() {
            @Override
            public void onSuccess(CurrentShiftDto result) {
                if (result.getValue() != null) {
                    BeginTimeShift = result.getValue().getBeginTime();
                    EndTimeShift = result.getValue().getEndTime();
                    Log.i("========------==>", "BeginTimeShift " + BeginTimeShift);
                    Log.i("========------==>", "EndTimeShift " + EndTimeShift);
                    parkingSpaceListShift = result.getValue().getParkingSpaceDtoList();
                } else {
                    BeginTimeShift = -1;
                    EndTimeShift = -1;
                    ShowToast.getInstance().showWarning(BaseActivity.this, R.string.no_current_shift);
                }
            }

            @Override
            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                switch (resultType) {
                    case RetrofitError:
                        ShowToast.getInstance().showError(BaseActivity.this, R.string.exception_msg);
                        break;
                    case ServerError:
                        if (errorCode != 0)
                            ShowToast.getInstance().showError(BaseActivity.this, errorCode);
                        else
                            ShowToast.getInstance().showError(BaseActivity.this, R.string.connection_failed);
                        break;
                    default:
                        ShowToast.getInstance().showError(BaseActivity.this, resultType.ordinal());
                }
            }
        });
    }

    private boolean hasNetConnection() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean mobileConnected = mobileInfo.getState() == NetworkInfo.State.CONNECTED;

        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean wifiConnected = wifiInfo.getState() == NetworkInfo.State.CONNECTED;

        if (mobileConnected || wifiConnected) {
            return true;
        } else
            return false;
    }

    @Override
    public void sendRecordServiceResult(String message) {

    }
}
