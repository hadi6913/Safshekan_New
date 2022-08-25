package com.khodmohaseb.parkban.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;
import com.khodmohaseb.parkban.BaseActivity;
import com.khodmohaseb.parkban.LoginActivity;
import com.khodmohaseb.parkban.persistence.ParkbanDatabase;
import com.khodmohaseb.parkban.persistence.models.ResponseResultType;
import com.khodmohaseb.parkban.persistence.models.khodmohaseb.EntranceRecord;
import com.khodmohaseb.parkban.persistence.models.khodmohaseb.ExitRecord;
import com.khodmohaseb.parkban.persistence.models.khodmohaseb.PasswordOperatorRecord;
import com.khodmohaseb.parkban.persistence.models.khodmohaseb.TraffikRecord;
import com.khodmohaseb.parkban.repositories.ParkbanRepository;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.changepass.ChangePasswordRequestDto;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.changepass.OperatorUser;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.GetParkingInfoResponse;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.sendiorecord.DeviceIORecord;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.sendiorecord.SendIoRecordRequest;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.sendtraffikrecord.SendTraffikRecordRequest;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.sendtraffikrecord.TrafficRecord;
import com.khodmohaseb.parkban.utils.PelakUtility;

import java.util.ArrayList;
import java.util.List;


public class SendRecordHandler extends Service implements Runnable {

    private final static String TAG = "hhhhhhadi";
    private Thread runningThread;
    private boolean stopConds = false;
    private ParkbanRepository parkbanRepository;
    private int sleep = 5 * 60 * 1000;
    //    private String imei = "562837562483719";
    private String imei;


    private boolean shouldSendChangePassword = false;
    ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDto();


    private boolean shouldSendEntrance = false;
    SendIoRecordRequest sendEntranceRecordRequest = new SendIoRecordRequest();
    List<String> entrancePlatesForDelete = new ArrayList<>();


    private boolean shouldSendExit = false;
    SendIoRecordRequest sendExitRecordRequest = new SendIoRecordRequest();
    List<String> exitPlatesForDelete = new ArrayList<>();


    private boolean shouldSendTrafik = false;
    SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
    List<String> traffikPlatesForDelete = new ArrayList<>();


    Callbacks activity;
    BaseActivity baseActivity;
    private final IBinder mBinder = new LocalBinder();
    private static SendRecordHandler instance = null;


    public static SendRecordHandler getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;

        if (parkbanRepository == null) {
            ParkbanDatabase.getInstance(getApplicationContext(), new ParkbanDatabase.DatabaseReadyCallback() {
                @Override
                public void onReady(ParkbanDatabase instance) {
                    parkbanRepository = new ParkbanRepository(instance);
                }
            });
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        runningThread = new Thread(this);
        runningThread.start();
        return START_NOT_STICKY;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onDestroy() {
        stopConds = true;
        instance = null;
        Log.d(TAG, "onDestroy > SendRecordHandler has been stopped");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @SuppressLint("LongLogTag")
    public void registerClient(BaseActivity baseActivity) {
        this.activity = (Callbacks) baseActivity;
        this.baseActivity = baseActivity;


        final TelephonyManager telephonyManager = (TelephonyManager) (baseActivity.getSystemService(Context.TELEPHONY_SERVICE));
        Log.d(TAG, "IMEI : " + telephonyManager.getDeviceId());
        imei = telephonyManager.getDeviceId();

    }

    public class LocalBinder extends Binder {
        public SendRecordHandler getServiceInstance() {
            return SendRecordHandler.this;
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void run() {
        Thread.currentThread().setPriority(9);
        while (!stopConds) {

            Log.d(TAG, "while loop has been called");


            try {

                Log.d(TAG, "run > SendRecordHandler background work called");











                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (InternetConnection.checkConnection(getApplicationContext())) {
                            // Internet Available...
                            Log.d(TAG, "run > Internet Available...");
                            if (activity != null) {


                                parkbanRepository.getAllFromChangePassword(new ParkbanRepository.DataBaseChangePasswordRecordResultCallBack() {
                                    @Override
                                    public void onSuccess(List<PasswordOperatorRecord> passwordOperatorRecordList) {
                                        if (passwordOperatorRecordList.size() != 0) {
                                            shouldSendChangePassword = true;
                                            changePasswordRequestDto = new ChangePasswordRequestDto();
                                            changePasswordRequestDto.setImei(imei);
                                            List<OperatorUser> operatorUserList = new ArrayList<>();
                                            for (PasswordOperatorRecord item : passwordOperatorRecordList) {
                                                OperatorUser operatorUser = new OperatorUser();
                                                operatorUser.setParkingId(item.getParkingId());
                                                operatorUser.setUserName(item.getUserName());
                                                operatorUser.setPassword(item.getPassword());
                                                operatorUserList.add(operatorUser);
                                            }
                                            changePasswordRequestDto.setOperatorUsers(operatorUserList);

                                        } else {
                                            shouldSendChangePassword = false;
                                        }

                                        parkbanRepository.getFromEntranceTable(new ParkbanRepository.DataBaseEntranceRecordResultCallBack() {
                                            @Override
                                            public void onSuccess(final List<EntranceRecord> entranceRecordList) {
                                                if (entranceRecordList.size() != 0) {
                                                    shouldSendEntrance = true;
                                                    sendEntranceRecordRequest = new SendIoRecordRequest();
                                                    sendEntranceRecordRequest.setImei(imei);
                                                    List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                    entrancePlatesForDelete = new ArrayList<>();
                                                    for (EntranceRecord element : entranceRecordList) {
                                                        DeviceIORecord ioRecord = new DeviceIORecord();
                                                        ioRecord.setDeviceId(element.getDeviceID());
                                                        if (element.getPlate().length() == 8) {
                                                            ioRecord.setPlate(element.getPlate());
                                                        } else {
                                                            String carPlate = element.getPlate().substring(0, 2) +
                                                                    PelakUtility.convertFromCode(element.getPlate().substring(2, 4)) +
                                                                    element.getPlate().substring(4, 9);
                                                            ioRecord.setPlate(carPlate);
                                                        }
                                                        ioRecord.setDoorId(element.getEntranceDoorId());
                                                        ioRecord.setOperatorId(element.getEntranceOperatorId());
                                                        ioRecord.setCost(element.getPaidAmount());
                                                        ioRecord.setPaymentKind((element.getPayType() == 2) ? 0L : element.getPayType());
                                                        ioRecord.setElectronicPaymentTracingCode(element.getElectronicPaymentCode());
                                                        ioRecord.setSourceKindTraffic(element.getSourceKind());
                                                        ioRecord.setIoKind(0L);
                                                        String date = element.getEntranceDate().substring(0, 4)
                                                                + "-" + element.getEntranceDate().substring(4, 6)
                                                                + "-" + element.getEntranceDate().substring(6, 8)
                                                                + "T" + element.getEntranceDate().substring(9, 11)
                                                                + ":" + element.getEntranceDate().substring(11, 13);
                                                        ioRecord.setTrafficDateTime(date);
                                                        deviceIORecordList.add(ioRecord);
                                                        entrancePlatesForDelete.add(element.getPlate());
                                                    }
                                                    sendEntranceRecordRequest.setDeviceIORecords(deviceIORecordList);


                                                } else {
                                                    shouldSendEntrance = false;
                                                }


                                                parkbanRepository.getFromExitTable(new ParkbanRepository.DataBaseExitRecordResultCallBack() {
                                                    @Override
                                                    public void onSuccess(List<ExitRecord> exitRecordList) {
                                                        if (exitRecordList.size() != 0) {
                                                            shouldSendExit = true;
                                                            sendExitRecordRequest = new SendIoRecordRequest();
                                                            sendExitRecordRequest.setImei(imei);
                                                            List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                            exitPlatesForDelete = new ArrayList<>();
                                                            for (ExitRecord element : exitRecordList) {
                                                                DeviceIORecord ioRecord = new DeviceIORecord();
                                                                ioRecord.setDeviceId(element.getDeviceID());
                                                                if (element.getPlate().length() == 8) {
                                                                    ioRecord.setPlate(element.getPlate());
                                                                } else {
                                                                    String carPlate = element.getPlate().substring(0, 2) +
                                                                            PelakUtility.convertFromCode(element.getPlate().substring(2, 4)) +
                                                                            element.getPlate().substring(4, 9);
                                                                    ioRecord.setPlate(carPlate);
                                                                }
                                                                ioRecord.setDoorId(element.getExitDoorId());
                                                                ioRecord.setOperatorId(element.getExitOperatorId());
                                                                ioRecord.setCost(element.getPaidAmount());
                                                                ioRecord.setPaymentKind((element.getPayType() == 2) ? 0L : element.getPayType());
                                                                ioRecord.setElectronicPaymentTracingCode(element.getElectronicPaymentCode());
                                                                ioRecord.setSourceKindTraffic(element.getSourceKind());
                                                                ioRecord.setIoKind(1L);
                                                                String date = element.getExitDate().substring(0, 4)
                                                                        + "-" + element.getExitDate().substring(4, 6)
                                                                        + "-" + element.getExitDate().substring(6, 8)
                                                                        + "T" + element.getExitDate().substring(9, 11)
                                                                        + ":" + element.getExitDate().substring(11, 13);
                                                                ioRecord.setTrafficDateTime(date);
                                                                deviceIORecordList.add(ioRecord);
                                                                exitPlatesForDelete.add(element.getPlate());
                                                            }
                                                            sendExitRecordRequest.setDeviceIORecords(deviceIORecordList);


                                                        } else {
                                                            shouldSendExit = false;
                                                        }


                                                        parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                            @Override
                                                            public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                if (traffikRecordList.size() != 0) {
                                                                    shouldSendTrafik = true;
                                                                    sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                    sendTraffikRecordRequest.setImei(imei);
                                                                    List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                    traffikPlatesForDelete = new ArrayList<>();
                                                                    for (TraffikRecord element : traffikRecordList) {
                                                                        TrafficRecord trafficRecordDto = new TrafficRecord();
                                                                        trafficRecordDto.setDeviceId(element.getDevice_id());
                                                                        if (element.getPlate().length() == 8) {
                                                                            trafficRecordDto.setPlate(element.getPlate());
                                                                        } else {
                                                                            String carPlate = element.getPlate().substring(0, 2) +
                                                                                    PelakUtility.convertFromCode(element.getPlate().substring(2, 4)) +
                                                                                    element.getPlate().substring(4, 9);
                                                                            trafficRecordDto.setPlate(carPlate);
                                                                        }
                                                                        trafficRecordDto.setEnterDoorId(element.getEntranceDoorId());
                                                                        trafficRecordDto.setExitDoorId(element.getExitDoorId());
                                                                        trafficRecordDto.setEnterOperatorId(element.getEntranceOperatorId());
                                                                        trafficRecordDto.setExitOperatorId(element.getExitOperatorId());
                                                                        trafficRecordDto.setCost(element.getPaidAmount());
                                                                        trafficRecordDto.setPaymentKind((element.getPayType() == 2) ? 0L : element.getPayType());
                                                                        trafficRecordDto.setElectronicPaymentTracingCode(element.getElectronicPaymentCode());
                                                                        String enterDate = element.getEntranceDate().substring(0, 4)
                                                                                + "-" + element.getEntranceDate().substring(4, 6)
                                                                                + "-" + element.getEntranceDate().substring(6, 8)
                                                                                + "T" + element.getEntranceDate().substring(9, 11)
                                                                                + ":" + element.getEntranceDate().substring(11, 13);
                                                                        trafficRecordDto.setEnterDateTime(enterDate);
                                                                        String exitDate = element.getExitDate().substring(0, 4)
                                                                                + "-" + element.getExitDate().substring(4, 6)
                                                                                + "-" + element.getExitDate().substring(6, 8)
                                                                                + "T" + element.getExitDate().substring(9, 11)
                                                                                + ":" + element.getExitDate().substring(11, 13);
                                                                        trafficRecordDto.setExitDateTime(exitDate);
                                                                        trafficRecordDto.setVehicleName(element.getVehicleName());

                                                                        deviceTraffikRecordList.add(trafficRecordDto);
                                                                        traffikPlatesForDelete.add(element.getPlate());
                                                                    }
                                                                    sendTraffikRecordRequest.setTrafficRecords(deviceTraffikRecordList);


                                                                } else {
                                                                    shouldSendTrafik = false;
                                                                }


                                                                sendAllRecordsAndOtherThings();


                                                            }

                                                            @Override
                                                            public void onFailed() {
                                                                Log.d(TAG, "getFromTraffikTable failed");
                                                                return;
                                                            }
                                                        });


                                                    }

                                                    @Override
                                                    public void onFailed() {
                                                        Log.d(TAG, "getFromExitTable failed");
                                                        return;
                                                    }
                                                });


                                            }

                                            @Override
                                            public void onFailed() {

                                                Log.d(TAG, "getFromEntranceTable failed");
                                                return;

                                            }
                                        });


                                    }

                                    @Override
                                    public void onFailed() {
                                        Log.d(TAG, "getAllFromChangePassword failed");
                                        return;
                                    }
                                });


                            }
                        } else {
                            // Internet Not Available...
                            Log.d(TAG, "run > Internet Not Available...");
                        }
                    }
                });

                Log.d(TAG, "should do sleep");
                Thread.sleep(sleep);

            } catch (Exception ex) {


                Log.d(TAG, "run > SendRecordHandler exception :" + ex.getMessage());


            }
        }
    }

    //*********************************************************************************************************************************
    @SuppressLint("LongLogTag")
    public void sendAllRecordsAndOtherThings() {
        if (shouldSendChangePassword) {
            parkbanRepository.changePassword(changePasswordRequestDto, new ParkbanRepository.ServiceResultCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, "changePassword   success");

                    parkbanRepository.deleteAllFromChangePassword(new ParkbanRepository.DataBaseResultCallBack() {

                        @Override
                        public void onSuccess(long id) {
                            Log.d(TAG, "deleteAllFromChangePassword   success");
                        }

                        @Override
                        public void onFailed() {
                            Log.d(TAG, "deleteAllFromChangePassword   failed");
                        }
                    });


                }

                @Override
                public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                    Log.d(TAG, "changePassword   failed");

                }
            });
        }


        if (shouldSendEntrance) {


            parkbanRepository.sendIo(sendEntranceRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, "sendIo entrance   success");
                    parkbanRepository.deleteFromEntranceRecord(entrancePlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                        @Override
                        public void onSuccess(final boolean entranceDeleteResult) {
                            Log.d(TAG, "deleteFromEntranceRecord   success");
                        }

                        @Override
                        public void onFailed() {
                            Log.d(TAG, "deleteFromEntranceRecord   failed");
                        }
                    });
                }

                @Override
                public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                    Log.d(TAG, "sendIo entrance   failed");
                }
            });


        }


        if (shouldSendExit) {


            parkbanRepository.sendIo(sendExitRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, "sendIo exit   success");
                    parkbanRepository.deleteFromExitRecord(exitPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                        @Override
                        public void onSuccess(final boolean entranceDeleteResult) {
                            Log.d(TAG, "deleteFromExitRecord   success");
                        }

                        @Override
                        public void onFailed() {
                            Log.d(TAG, "deleteFromExitRecord   failed");
                        }
                    });
                }

                @Override
                public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                    Log.d(TAG, "sendIo exit   failed");
                }
            });


        }


        if (shouldSendTrafik) {


            parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                @Override
                public void onSuccess(final String result) {
                    Log.d(TAG, "sendTraffik   success");
                    parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                        @Override
                        public void onSuccess(boolean exit) {
                            Log.d(TAG, "deleteFromTrafikRecord   success");
                        }

                        @Override
                        public void onFailed() {
                            Log.d(TAG, "deleteFromTrafikRecord   failed");
                        }
                    });

                }

                @Override
                public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                    Log.d(TAG, "sendTraffik   failed");
                }
            });


        }


    }

    public interface Callbacks {
        void sendRecordServiceResult(String message);
    }


}


