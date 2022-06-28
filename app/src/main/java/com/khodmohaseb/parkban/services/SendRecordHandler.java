package com.khodmohaseb.parkban.services;

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

    private final static String TAG = "popxeagle6913 SendRecordHandler";
    private Thread runningThread;
    private boolean stopConds = false;
    private ParkbanRepository parkbanRepository;
    private int sleep = 5 * 60 * 1000;
    //    private String imei = "562837562483719";
    private String imei;


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

    @Override
    public void onDestroy() {
        stopConds = true;
        Log.d(TAG, "onDestroy > SendRecordHandler has been stopped");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void registerClient(BaseActivity baseActivity) {
        this.activity = (Callbacks) baseActivity;
        this.baseActivity = baseActivity;


        final TelephonyManager telephonyManager = (TelephonyManager) (baseActivity.getSystemService(Context.TELEPHONY_SERVICE));
        Log.d(TAG, "IMEI : " + "968500040082191");
        imei = "968500040082191";

    }

    public class LocalBinder extends Binder {
        public SendRecordHandler getServiceInstance() {
            return SendRecordHandler.this;
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setPriority(9);
        while (!stopConds) {
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
                                            ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDto();
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
                                            parkbanRepository.changePassword(changePasswordRequestDto, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                @Override
                                                public void onSuccess(String result) {


                                                    parkbanRepository.deleteAllFromChangePassword(new ParkbanRepository.DataBaseResultCallBack() {
                                                        @Override
                                                        public void onSuccess(long id) {
                                                            parkbanRepository.getFromEntranceTable(new ParkbanRepository.DataBaseEntranceRecordResultCallBack() {
                                                                @Override
                                                                public void onSuccess(final List<EntranceRecord> entranceRecordList) {
                                                                    if (entranceRecordList.size() != 0) {
                                                                        Log.d(TAG, "some entrance records");
                                                                        SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                                        sendIoRecordRequest.setImei(imei);
                                                                        List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                                        final List<String> entrancePlatesForDelete = new ArrayList<>();
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
                                                                        sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                                        Log.d(TAG, "send entrance records");
                                                                        parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                            @Override
                                                                            public void onSuccess(String result) {
                                                                                Log.d(TAG, "delete entrance records");
                                                                                parkbanRepository.deleteFromEntranceRecord(entrancePlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                    @Override
                                                                                    public void onSuccess(final boolean entranceDeleteResult) {
                                                                                        if (entranceDeleteResult) {
                                                                                            Log.d(TAG, "get from exit table");
                                                                                            parkbanRepository.getFromExitTable(new ParkbanRepository.DataBaseExitRecordResultCallBack() {
                                                                                                @Override
                                                                                                public void onSuccess(List<ExitRecord> exitRecordList) {
                                                                                                    if (exitRecordList.size() != 0) {
                                                                                                        Log.d(TAG, "some exit records");
                                                                                                        SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                                                                        sendIoRecordRequest.setImei(imei);
                                                                                                        List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                                                                        final List<String> exitPlatesForDelete = new ArrayList<>();
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
                                                                                                        sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                                                                        Log.d(TAG, "send exit records");
                                                                                                        parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(final String result) {
                                                                                                                Log.d(TAG, "delete exit records");
                                                                                                                parkbanRepository.deleteFromExitRecord(exitPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                    @Override
                                                                                                                    public void onSuccess(boolean exitDeleteResult) {
                                                                                                                        if (exitDeleteResult) {
                                                                                                                            Log.d(TAG, "get from traffik table");
                                                                                                                            parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                                                                @Override
                                                                                                                                public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                                                    if (traffikRecordList.size() != 0) {
                                                                                                                                        Log.d(TAG, "some traffik records gdfds");
                                                                                                                                        SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                                                        sendTraffikRecordRequest.setImei(imei);
                                                                                                                                        List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                                                        final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                                                        Log.d(TAG, "send exit records dgdss");
                                                                                                                                        parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                                                            @Override
                                                                                                                                            public void onSuccess(final String result) {
                                                                                                                                                Log.d(TAG, "delete traffik reords  efgfds");
                                                                                                                                                parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                                                    @Override
                                                                                                                                                    public void onSuccess(boolean exit) {
                                                                                                                                                        Log.d(TAG, "success delete trafiick records mhdsbjfdbs");
                                                                                                                                                        return;
                                                                                                                                                    }

                                                                                                                                                    @Override
                                                                                                                                                    public void onFailed() {
                                                                                                                                                        Log.d(TAG, "failed delete trafiick records mhdsbjfdbs");
                                                                                                                                                        return;
                                                                                                                                                    }
                                                                                                                                                });

                                                                                                                                            }

                                                                                                                                            @Override
                                                                                                                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                                                Log.d(TAG, "failed send traffik hdskjfhk");
                                                                                                                                                return;
                                                                                                                                            }
                                                                                                                                        });


                                                                                                                                    } else {
                                                                                                                                        Log.d(TAG, "no traffik record gsdgdfs");
                                                                                                                                        return;
                                                                                                                                    }
                                                                                                                                }

                                                                                                                                @Override
                                                                                                                                public void onFailed() {
                                                                                                                                    Log.d(TAG, "no traffik record dfgsd");
                                                                                                                                    return;
                                                                                                                                }
                                                                                                                            });
                                                                                                                        } else {
                                                                                                                            Log.d(TAG, "failed delete exit records 5664");
                                                                                                                            return;
                                                                                                                        }
                                                                                                                    }

                                                                                                                    @Override
                                                                                                                    public void onFailed() {
                                                                                                                        Log.d(TAG, "failed delete exit records 2332");
                                                                                                                        return;
                                                                                                                    }
                                                                                                                });
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                Log.d(TAG, "Failed send exit records");
                                                                                                                return;
                                                                                                            }
                                                                                                        });


                                                                                                    } else {
                                                                                                        Log.d(TAG, "no exit record");
                                                                                                        Log.d(TAG, "get from traffik table");
                                                                                                        parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                                            @Override
                                                                                                            public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                                if (traffikRecordList.size() != 0) {
                                                                                                                    Log.d(TAG, "some traffik records ghjgjh");
                                                                                                                    SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                                    sendTraffikRecordRequest.setImei(imei);
                                                                                                                    List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                                    final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                                    Log.d(TAG, "send exit records jkjhjkh");
                                                                                                                    parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                                        @Override
                                                                                                                        public void onSuccess(final String result) {
                                                                                                                            Log.d(TAG, "delete traffik reords kjhjhkjhkj");
                                                                                                                            parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                                @Override
                                                                                                                                public void onSuccess(boolean exit) {
                                                                                                                                    Log.d(TAG, "Done kjfjhdskjhfsdkjh");
                                                                                                                                    return;
                                                                                                                                }

                                                                                                                                @Override
                                                                                                                                public void onFailed() {
                                                                                                                                    Log.d(TAG, "failed delete traffik records kjfjhdskjhfsdkjh");
                                                                                                                                    return;
                                                                                                                                }
                                                                                                                            });

                                                                                                                        }

                                                                                                                        @Override
                                                                                                                        public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                            Log.d(TAG, "failed send traffik jhkj kjkjhkjhkjhkhjhghgj");
                                                                                                                            return;
                                                                                                                        }
                                                                                                                    });


                                                                                                                } else {
                                                                                                                    Log.d(TAG, "no traffik record kjkjhkjhkjhkhjhghgj");
                                                                                                                    return;
                                                                                                                }
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailed() {
                                                                                                                Log.d(TAG, "no traffik record kjjljjj");
                                                                                                                return;
                                                                                                            }
                                                                                                        });
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onFailed() {
                                                                                                    Log.d(TAG, "no exit record");
                                                                                                    Log.d(TAG, "get from traffik table");
                                                                                                    parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                                        @Override
                                                                                                        public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                            if (traffikRecordList.size() != 0) {
                                                                                                                Log.d(TAG, "some traffik records dfsdfsd");
                                                                                                                SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                                sendTraffikRecordRequest.setImei(imei);
                                                                                                                List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                                final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                                Log.d(TAG, "send exit records dsafsafassa");
                                                                                                                parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                                    @Override
                                                                                                                    public void onSuccess(final String result) {
                                                                                                                        Log.d(TAG, "delete traffik reords");
                                                                                                                        parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(boolean exit) {
                                                                                                                                return;
                                                                                                                            }

                                                                                                                            @Override
                                                                                                                            public void onFailed() {
                                                                                                                                return;
                                                                                                                            }
                                                                                                                        });

                                                                                                                    }

                                                                                                                    @Override
                                                                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                        return;
                                                                                                                    }
                                                                                                                });


                                                                                                            } else {
                                                                                                                Log.d(TAG, "no traffik record");
                                                                                                                return;
                                                                                                            }
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onFailed() {
                                                                                                            Log.d(TAG, "no traffik record");
                                                                                                            return;
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            });


                                                                                        } else {
                                                                                            Log.d(TAG, "Failed delete entrance records-1");
                                                                                            return;
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onFailed() {
                                                                                        Log.d(TAG, "Failed delete entrance records-2");
                                                                                        return;
                                                                                    }
                                                                                });
                                                                            }

                                                                            @Override
                                                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                Log.d(TAG, "Failed send entrance records");
                                                                                return;
                                                                            }
                                                                        });


                                                                    } else {
                                                                        Log.d(TAG, "no entrance record-1");
                                                                        Log.d(TAG, "get from exit table");
                                                                        parkbanRepository.getFromExitTable(new ParkbanRepository.DataBaseExitRecordResultCallBack() {
                                                                            @Override
                                                                            public void onSuccess(List<ExitRecord> exitRecordList) {
                                                                                if (exitRecordList.size() != 0) {
                                                                                    Log.d(TAG, "some exit records");
                                                                                    SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                                                    sendIoRecordRequest.setImei(imei);
                                                                                    List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                                                    final List<String> exitPlatesForDelete = new ArrayList<>();
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
                                                                                    sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                                                    Log.d(TAG, "send exit records");
                                                                                    parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                        @Override
                                                                                        public void onSuccess(final String result) {
                                                                                            Log.d(TAG, "delete exit records");
                                                                                            parkbanRepository.deleteFromExitRecord(exitPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                @Override
                                                                                                public void onSuccess(boolean exitDeleteResult) {
                                                                                                    if (exitDeleteResult) {
                                                                                                        Log.d(TAG, "get from traffik table");
                                                                                                        parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                                            @Override
                                                                                                            public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                                if (traffikRecordList.size() != 0) {
                                                                                                                    Log.d(TAG, "some traffik records");
                                                                                                                    SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                                    sendTraffikRecordRequest.setImei(imei);
                                                                                                                    List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                                    final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                                    Log.d(TAG, "send exit records");
                                                                                                                    parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                                        @Override
                                                                                                                        public void onSuccess(final String result) {
                                                                                                                            Log.d(TAG, "delete traffik reords");
                                                                                                                            parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                                @Override
                                                                                                                                public void onSuccess(boolean exit) {
                                                                                                                                    return;
                                                                                                                                }

                                                                                                                                @Override
                                                                                                                                public void onFailed() {
                                                                                                                                    return;
                                                                                                                                }
                                                                                                                            });

                                                                                                                        }

                                                                                                                        @Override
                                                                                                                        public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                            return;
                                                                                                                        }
                                                                                                                    });


                                                                                                                } else {
                                                                                                                    Log.d(TAG, "no traffik record");
                                                                                                                    return;
                                                                                                                }
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailed() {
                                                                                                                Log.d(TAG, "no traffik record");
                                                                                                                return;
                                                                                                            }
                                                                                                        });
                                                                                                    } else {
                                                                                                        return;
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onFailed() {
                                                                                                    return;
                                                                                                }
                                                                                            });
                                                                                        }

                                                                                        @Override
                                                                                        public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                            return;
                                                                                        }
                                                                                    });


                                                                                } else {
                                                                                    Log.d(TAG, "no exit record");
                                                                                    Log.d(TAG, "get from traffik table");
                                                                                    parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                        @Override
                                                                                        public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                            if (traffikRecordList.size() != 0) {
                                                                                                Log.d(TAG, "some traffik records dfsdfsd");
                                                                                                SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                sendTraffikRecordRequest.setImei(imei);
                                                                                                List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                Log.d(TAG, "send exit records dsafsafassa");
                                                                                                parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(final String result) {
                                                                                                        Log.d(TAG, "delete traffik reords");
                                                                                                        parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                            @Override
                                                                                                            public void onSuccess(boolean exit) {
                                                                                                                return;
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailed() {
                                                                                                                return;
                                                                                                            }
                                                                                                        });

                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                        return;
                                                                                                    }
                                                                                                });


                                                                                            } else {
                                                                                                Log.d(TAG, "no traffik record");
                                                                                                return;
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onFailed() {
                                                                                            Log.d(TAG, "no traffik record");
                                                                                            return;
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onFailed() {
                                                                                Log.d(TAG, "no exit record");
                                                                                Log.d(TAG, "get from traffik table");
                                                                                parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                    @Override
                                                                                    public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                        if (traffikRecordList.size() != 0) {
                                                                                            Log.d(TAG, "some traffik records dfsdfsd");
                                                                                            SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                            sendTraffikRecordRequest.setImei(imei);
                                                                                            List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                            final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                            Log.d(TAG, "send exit records dsafsafassa");
                                                                                            parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                @Override
                                                                                                public void onSuccess(final String result) {
                                                                                                    Log.d(TAG, "delete traffik reords");
                                                                                                    parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                        @Override
                                                                                                        public void onSuccess(boolean exit) {
                                                                                                            return;
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onFailed() {
                                                                                                            return;
                                                                                                        }
                                                                                                    });

                                                                                                }

                                                                                                @Override
                                                                                                public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                    return;
                                                                                                }
                                                                                            });


                                                                                        } else {
                                                                                            Log.d(TAG, "no traffik record");
                                                                                            return;
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onFailed() {
                                                                                        Log.d(TAG, "no traffik record");
                                                                                        return;
                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFailed() {
                                                                    Log.d(TAG, "no entrance record-2");
                                                                    Log.d(TAG, "get from exit table");
                                                                    parkbanRepository.getFromExitTable(new ParkbanRepository.DataBaseExitRecordResultCallBack() {
                                                                        @Override
                                                                        public void onSuccess(List<ExitRecord> exitRecordList) {
                                                                            if (exitRecordList.size() != 0) {
                                                                                Log.d(TAG, "some exit records");
                                                                                SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                                                sendIoRecordRequest.setImei(imei);
                                                                                List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                                                final List<String> exitPlatesForDelete = new ArrayList<>();
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
                                                                                sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                                                Log.d(TAG, "send exit records");
                                                                                parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                    @Override
                                                                                    public void onSuccess(final String result) {
                                                                                        Log.d(TAG, "delete exit records");
                                                                                        parkbanRepository.deleteFromExitRecord(exitPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                            @Override
                                                                                            public void onSuccess(boolean exitDeleteResult) {
                                                                                                if (exitDeleteResult) {
                                                                                                    Log.d(TAG, "get from traffik table");
                                                                                                    parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                                        @Override
                                                                                                        public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                            if (traffikRecordList.size() != 0) {
                                                                                                                Log.d(TAG, "some traffik records");
                                                                                                                SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                                sendTraffikRecordRequest.setImei(imei);
                                                                                                                List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                                final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                                Log.d(TAG, "send exit records");
                                                                                                                parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                                    @Override
                                                                                                                    public void onSuccess(final String result) {
                                                                                                                        Log.d(TAG, "delete traffik reords");
                                                                                                                        parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(boolean exit) {
                                                                                                                                return;
                                                                                                                            }

                                                                                                                            @Override
                                                                                                                            public void onFailed() {
                                                                                                                                return;
                                                                                                                            }
                                                                                                                        });

                                                                                                                    }

                                                                                                                    @Override
                                                                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                        return;
                                                                                                                    }
                                                                                                                });


                                                                                                            } else {
                                                                                                                Log.d(TAG, "no traffik record");
                                                                                                                return;
                                                                                                            }
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onFailed() {
                                                                                                            Log.d(TAG, "no traffik record");
                                                                                                            return;
                                                                                                        }
                                                                                                    });
                                                                                                } else {
                                                                                                    return;
                                                                                                }
                                                                                            }

                                                                                            @Override
                                                                                            public void onFailed() {
                                                                                                return;
                                                                                            }
                                                                                        });
                                                                                    }

                                                                                    @Override
                                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                        return;
                                                                                    }
                                                                                });


                                                                            } else {
                                                                                Log.d(TAG, "no exit record");
                                                                                Log.d(TAG, "get from traffik table");
                                                                                parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                    @Override
                                                                                    public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                        if (traffikRecordList.size() != 0) {
                                                                                            Log.d(TAG, "some traffik records dfsdfsd");
                                                                                            SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                            sendTraffikRecordRequest.setImei(imei);
                                                                                            List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                            final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                            Log.d(TAG, "send exit records dsafsafassa");
                                                                                            parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                @Override
                                                                                                public void onSuccess(final String result) {
                                                                                                    Log.d(TAG, "delete traffik reords");
                                                                                                    parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                        @Override
                                                                                                        public void onSuccess(boolean exit) {
                                                                                                            return;
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onFailed() {
                                                                                                            return;
                                                                                                        }
                                                                                                    });

                                                                                                }

                                                                                                @Override
                                                                                                public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                    return;
                                                                                                }
                                                                                            });


                                                                                        } else {
                                                                                            Log.d(TAG, "no traffik record");
                                                                                            return;
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onFailed() {
                                                                                        Log.d(TAG, "no traffik record");
                                                                                        return;
                                                                                    }
                                                                                });
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onFailed() {
                                                                            Log.d(TAG, "no exit record");
                                                                            Log.d(TAG, "get from traffik table");
                                                                            parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                @Override
                                                                                public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                    if (traffikRecordList.size() != 0) {
                                                                                        Log.d(TAG, "some traffik records dfsdfsd");
                                                                                        SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                        sendTraffikRecordRequest.setImei(imei);
                                                                                        List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                        final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                        Log.d(TAG, "send exit records dsafsafassa");
                                                                                        parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                            @Override
                                                                                            public void onSuccess(final String result) {
                                                                                                Log.d(TAG, "delete traffik reords");
                                                                                                parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                    @Override
                                                                                                    public void onSuccess(boolean exit) {
                                                                                                        return;
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onFailed() {
                                                                                                        return;
                                                                                                    }
                                                                                                });

                                                                                            }

                                                                                            @Override
                                                                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                return;
                                                                                            }
                                                                                        });


                                                                                    } else {
                                                                                        Log.d(TAG, "no traffik record");
                                                                                        return;
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onFailed() {
                                                                                    Log.d(TAG, "no traffik record");
                                                                                    return;
                                                                                }
                                                                            });
                                                                        }
                                                                    });


                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onFailed() {
                                                            parkbanRepository.getFromEntranceTable(new ParkbanRepository.DataBaseEntranceRecordResultCallBack() {
                                                                @Override
                                                                public void onSuccess(final List<EntranceRecord> entranceRecordList) {
                                                                    if (entranceRecordList.size() != 0) {
                                                                        Log.d(TAG, "some entrance records");
                                                                        SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                                        sendIoRecordRequest.setImei(imei);
                                                                        List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                                        final List<String> entrancePlatesForDelete = new ArrayList<>();
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
                                                                        sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                                        Log.d(TAG, "send entrance records");
                                                                        parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                            @Override
                                                                            public void onSuccess(String result) {
                                                                                Log.d(TAG, "delete entrance records");
                                                                                parkbanRepository.deleteFromEntranceRecord(entrancePlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                    @Override
                                                                                    public void onSuccess(final boolean entranceDeleteResult) {
                                                                                        if (entranceDeleteResult) {
                                                                                            Log.d(TAG, "get from exit table");
                                                                                            parkbanRepository.getFromExitTable(new ParkbanRepository.DataBaseExitRecordResultCallBack() {
                                                                                                @Override
                                                                                                public void onSuccess(List<ExitRecord> exitRecordList) {
                                                                                                    if (exitRecordList.size() != 0) {
                                                                                                        Log.d(TAG, "some exit records");
                                                                                                        SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                                                                        sendIoRecordRequest.setImei(imei);
                                                                                                        List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                                                                        final List<String> exitPlatesForDelete = new ArrayList<>();
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
                                                                                                        sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                                                                        Log.d(TAG, "send exit records");
                                                                                                        parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(final String result) {
                                                                                                                Log.d(TAG, "delete exit records");
                                                                                                                parkbanRepository.deleteFromExitRecord(exitPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                    @Override
                                                                                                                    public void onSuccess(boolean exitDeleteResult) {
                                                                                                                        if (exitDeleteResult) {
                                                                                                                            Log.d(TAG, "get from traffik table");
                                                                                                                            parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                                                                @Override
                                                                                                                                public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                                                    if (traffikRecordList.size() != 0) {
                                                                                                                                        Log.d(TAG, "some traffik records gdfds");
                                                                                                                                        SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                                                        sendTraffikRecordRequest.setImei(imei);
                                                                                                                                        List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                                                        final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                                                        Log.d(TAG, "send exit records dgdss");
                                                                                                                                        parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                                                            @Override
                                                                                                                                            public void onSuccess(final String result) {
                                                                                                                                                Log.d(TAG, "delete traffik reords  efgfds");
                                                                                                                                                parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                                                    @Override
                                                                                                                                                    public void onSuccess(boolean exit) {
                                                                                                                                                        Log.d(TAG, "success delete trafiick records mhdsbjfdbs");
                                                                                                                                                        return;
                                                                                                                                                    }

                                                                                                                                                    @Override
                                                                                                                                                    public void onFailed() {
                                                                                                                                                        Log.d(TAG, "failed delete trafiick records mhdsbjfdbs");
                                                                                                                                                        return;
                                                                                                                                                    }
                                                                                                                                                });

                                                                                                                                            }

                                                                                                                                            @Override
                                                                                                                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                                                Log.d(TAG, "failed send traffik hdskjfhk");
                                                                                                                                                return;
                                                                                                                                            }
                                                                                                                                        });


                                                                                                                                    } else {
                                                                                                                                        Log.d(TAG, "no traffik record gsdgdfs");
                                                                                                                                        return;
                                                                                                                                    }
                                                                                                                                }

                                                                                                                                @Override
                                                                                                                                public void onFailed() {
                                                                                                                                    Log.d(TAG, "no traffik record dfgsd");
                                                                                                                                    return;
                                                                                                                                }
                                                                                                                            });
                                                                                                                        } else {
                                                                                                                            Log.d(TAG, "failed delete exit records 5664");
                                                                                                                            return;
                                                                                                                        }
                                                                                                                    }

                                                                                                                    @Override
                                                                                                                    public void onFailed() {
                                                                                                                        Log.d(TAG, "failed delete exit records 2332");
                                                                                                                        return;
                                                                                                                    }
                                                                                                                });
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                Log.d(TAG, "Failed send exit records");
                                                                                                                return;
                                                                                                            }
                                                                                                        });


                                                                                                    } else {
                                                                                                        Log.d(TAG, "no exit record");
                                                                                                        Log.d(TAG, "get from traffik table");
                                                                                                        parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                                            @Override
                                                                                                            public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                                if (traffikRecordList.size() != 0) {
                                                                                                                    Log.d(TAG, "some traffik records ghjgjh");
                                                                                                                    SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                                    sendTraffikRecordRequest.setImei(imei);
                                                                                                                    List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                                    final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                                    Log.d(TAG, "send exit records jkjhjkh");
                                                                                                                    parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                                        @Override
                                                                                                                        public void onSuccess(final String result) {
                                                                                                                            Log.d(TAG, "delete traffik reords kjhjhkjhkj");
                                                                                                                            parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                                @Override
                                                                                                                                public void onSuccess(boolean exit) {
                                                                                                                                    Log.d(TAG, "Done kjfjhdskjhfsdkjh");
                                                                                                                                    return;
                                                                                                                                }

                                                                                                                                @Override
                                                                                                                                public void onFailed() {
                                                                                                                                    Log.d(TAG, "failed delete traffik records kjfjhdskjhfsdkjh");
                                                                                                                                    return;
                                                                                                                                }
                                                                                                                            });

                                                                                                                        }

                                                                                                                        @Override
                                                                                                                        public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                            Log.d(TAG, "failed send traffik jhkj kjkjhkjhkjhkhjhghgj");
                                                                                                                            return;
                                                                                                                        }
                                                                                                                    });


                                                                                                                } else {
                                                                                                                    Log.d(TAG, "no traffik record kjkjhkjhkjhkhjhghgj");
                                                                                                                    return;
                                                                                                                }
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailed() {
                                                                                                                Log.d(TAG, "no traffik record kjjljjj");
                                                                                                                return;
                                                                                                            }
                                                                                                        });
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onFailed() {
                                                                                                    Log.d(TAG, "no exit record");
                                                                                                    Log.d(TAG, "get from traffik table");
                                                                                                    parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                                        @Override
                                                                                                        public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                            if (traffikRecordList.size() != 0) {
                                                                                                                Log.d(TAG, "some traffik records dfsdfsd");
                                                                                                                SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                                sendTraffikRecordRequest.setImei(imei);
                                                                                                                List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                                final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                                Log.d(TAG, "send exit records dsafsafassa");
                                                                                                                parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                                    @Override
                                                                                                                    public void onSuccess(final String result) {
                                                                                                                        Log.d(TAG, "delete traffik reords");
                                                                                                                        parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(boolean exit) {
                                                                                                                                return;
                                                                                                                            }

                                                                                                                            @Override
                                                                                                                            public void onFailed() {
                                                                                                                                return;
                                                                                                                            }
                                                                                                                        });

                                                                                                                    }

                                                                                                                    @Override
                                                                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                        return;
                                                                                                                    }
                                                                                                                });


                                                                                                            } else {
                                                                                                                Log.d(TAG, "no traffik record");
                                                                                                                return;
                                                                                                            }
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onFailed() {
                                                                                                            Log.d(TAG, "no traffik record");
                                                                                                            return;
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            });


                                                                                        } else {
                                                                                            Log.d(TAG, "Failed delete entrance records-1");
                                                                                            return;
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onFailed() {
                                                                                        Log.d(TAG, "Failed delete entrance records-2");
                                                                                        return;
                                                                                    }
                                                                                });
                                                                            }

                                                                            @Override
                                                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                Log.d(TAG, "Failed send entrance records");
                                                                                return;
                                                                            }
                                                                        });


                                                                    } else {
                                                                        Log.d(TAG, "no entrance record-1");
                                                                        Log.d(TAG, "get from exit table");
                                                                        parkbanRepository.getFromExitTable(new ParkbanRepository.DataBaseExitRecordResultCallBack() {
                                                                            @Override
                                                                            public void onSuccess(List<ExitRecord> exitRecordList) {
                                                                                if (exitRecordList.size() != 0) {
                                                                                    Log.d(TAG, "some exit records");
                                                                                    SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                                                    sendIoRecordRequest.setImei(imei);
                                                                                    List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                                                    final List<String> exitPlatesForDelete = new ArrayList<>();
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
                                                                                    sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                                                    Log.d(TAG, "send exit records");
                                                                                    parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                        @Override
                                                                                        public void onSuccess(final String result) {
                                                                                            Log.d(TAG, "delete exit records");
                                                                                            parkbanRepository.deleteFromExitRecord(exitPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                @Override
                                                                                                public void onSuccess(boolean exitDeleteResult) {
                                                                                                    if (exitDeleteResult) {
                                                                                                        Log.d(TAG, "get from traffik table");
                                                                                                        parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                                            @Override
                                                                                                            public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                                if (traffikRecordList.size() != 0) {
                                                                                                                    Log.d(TAG, "some traffik records");
                                                                                                                    SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                                    sendTraffikRecordRequest.setImei(imei);
                                                                                                                    List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                                    final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                                    Log.d(TAG, "send exit records");
                                                                                                                    parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                                        @Override
                                                                                                                        public void onSuccess(final String result) {
                                                                                                                            Log.d(TAG, "delete traffik reords");
                                                                                                                            parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                                @Override
                                                                                                                                public void onSuccess(boolean exit) {
                                                                                                                                    return;
                                                                                                                                }

                                                                                                                                @Override
                                                                                                                                public void onFailed() {
                                                                                                                                    return;
                                                                                                                                }
                                                                                                                            });

                                                                                                                        }

                                                                                                                        @Override
                                                                                                                        public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                            return;
                                                                                                                        }
                                                                                                                    });


                                                                                                                } else {
                                                                                                                    Log.d(TAG, "no traffik record");
                                                                                                                    return;
                                                                                                                }
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailed() {
                                                                                                                Log.d(TAG, "no traffik record");
                                                                                                                return;
                                                                                                            }
                                                                                                        });
                                                                                                    } else {
                                                                                                        return;
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onFailed() {
                                                                                                    return;
                                                                                                }
                                                                                            });
                                                                                        }

                                                                                        @Override
                                                                                        public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                            return;
                                                                                        }
                                                                                    });


                                                                                } else {
                                                                                    Log.d(TAG, "no exit record");
                                                                                    Log.d(TAG, "get from traffik table");
                                                                                    parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                        @Override
                                                                                        public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                            if (traffikRecordList.size() != 0) {
                                                                                                Log.d(TAG, "some traffik records dfsdfsd");
                                                                                                SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                sendTraffikRecordRequest.setImei(imei);
                                                                                                List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                Log.d(TAG, "send exit records dsafsafassa");
                                                                                                parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(final String result) {
                                                                                                        Log.d(TAG, "delete traffik reords");
                                                                                                        parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                            @Override
                                                                                                            public void onSuccess(boolean exit) {
                                                                                                                return;
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailed() {
                                                                                                                return;
                                                                                                            }
                                                                                                        });

                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                        return;
                                                                                                    }
                                                                                                });


                                                                                            } else {
                                                                                                Log.d(TAG, "no traffik record");
                                                                                                return;
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onFailed() {
                                                                                            Log.d(TAG, "no traffik record");
                                                                                            return;
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onFailed() {
                                                                                Log.d(TAG, "no exit record");
                                                                                Log.d(TAG, "get from traffik table");
                                                                                parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                    @Override
                                                                                    public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                        if (traffikRecordList.size() != 0) {
                                                                                            Log.d(TAG, "some traffik records dfsdfsd");
                                                                                            SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                            sendTraffikRecordRequest.setImei(imei);
                                                                                            List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                            final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                            Log.d(TAG, "send exit records dsafsafassa");
                                                                                            parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                @Override
                                                                                                public void onSuccess(final String result) {
                                                                                                    Log.d(TAG, "delete traffik reords");
                                                                                                    parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                        @Override
                                                                                                        public void onSuccess(boolean exit) {
                                                                                                            return;
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onFailed() {
                                                                                                            return;
                                                                                                        }
                                                                                                    });

                                                                                                }

                                                                                                @Override
                                                                                                public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                    return;
                                                                                                }
                                                                                            });


                                                                                        } else {
                                                                                            Log.d(TAG, "no traffik record");
                                                                                            return;
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onFailed() {
                                                                                        Log.d(TAG, "no traffik record");
                                                                                        return;
                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFailed() {
                                                                    Log.d(TAG, "no entrance record-2");
                                                                    Log.d(TAG, "get from exit table");
                                                                    parkbanRepository.getFromExitTable(new ParkbanRepository.DataBaseExitRecordResultCallBack() {
                                                                        @Override
                                                                        public void onSuccess(List<ExitRecord> exitRecordList) {
                                                                            if (exitRecordList.size() != 0) {
                                                                                Log.d(TAG, "some exit records");
                                                                                SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                                                sendIoRecordRequest.setImei(imei);
                                                                                List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                                                final List<String> exitPlatesForDelete = new ArrayList<>();
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
                                                                                sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                                                Log.d(TAG, "send exit records");
                                                                                parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                    @Override
                                                                                    public void onSuccess(final String result) {
                                                                                        Log.d(TAG, "delete exit records");
                                                                                        parkbanRepository.deleteFromExitRecord(exitPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                            @Override
                                                                                            public void onSuccess(boolean exitDeleteResult) {
                                                                                                if (exitDeleteResult) {
                                                                                                    Log.d(TAG, "get from traffik table");
                                                                                                    parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                                        @Override
                                                                                                        public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                            if (traffikRecordList.size() != 0) {
                                                                                                                Log.d(TAG, "some traffik records");
                                                                                                                SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                                sendTraffikRecordRequest.setImei(imei);
                                                                                                                List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                                final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                                Log.d(TAG, "send exit records");
                                                                                                                parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                                    @Override
                                                                                                                    public void onSuccess(final String result) {
                                                                                                                        Log.d(TAG, "delete traffik reords");
                                                                                                                        parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(boolean exit) {
                                                                                                                                return;
                                                                                                                            }

                                                                                                                            @Override
                                                                                                                            public void onFailed() {
                                                                                                                                return;
                                                                                                                            }
                                                                                                                        });

                                                                                                                    }

                                                                                                                    @Override
                                                                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                        return;
                                                                                                                    }
                                                                                                                });


                                                                                                            } else {
                                                                                                                Log.d(TAG, "no traffik record");
                                                                                                                return;
                                                                                                            }
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onFailed() {
                                                                                                            Log.d(TAG, "no traffik record");
                                                                                                            return;
                                                                                                        }
                                                                                                    });
                                                                                                } else {
                                                                                                    return;
                                                                                                }
                                                                                            }

                                                                                            @Override
                                                                                            public void onFailed() {
                                                                                                return;
                                                                                            }
                                                                                        });
                                                                                    }

                                                                                    @Override
                                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                        return;
                                                                                    }
                                                                                });


                                                                            } else {
                                                                                Log.d(TAG, "no exit record");
                                                                                Log.d(TAG, "get from traffik table");
                                                                                parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                    @Override
                                                                                    public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                        if (traffikRecordList.size() != 0) {
                                                                                            Log.d(TAG, "some traffik records dfsdfsd");
                                                                                            SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                            sendTraffikRecordRequest.setImei(imei);
                                                                                            List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                            final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                            Log.d(TAG, "send exit records dsafsafassa");
                                                                                            parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                @Override
                                                                                                public void onSuccess(final String result) {
                                                                                                    Log.d(TAG, "delete traffik reords");
                                                                                                    parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                        @Override
                                                                                                        public void onSuccess(boolean exit) {
                                                                                                            return;
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onFailed() {
                                                                                                            return;
                                                                                                        }
                                                                                                    });

                                                                                                }

                                                                                                @Override
                                                                                                public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                    return;
                                                                                                }
                                                                                            });


                                                                                        } else {
                                                                                            Log.d(TAG, "no traffik record");
                                                                                            return;
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onFailed() {
                                                                                        Log.d(TAG, "no traffik record");
                                                                                        return;
                                                                                    }
                                                                                });
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onFailed() {
                                                                            Log.d(TAG, "no exit record");
                                                                            Log.d(TAG, "get from traffik table");
                                                                            parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                @Override
                                                                                public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                    if (traffikRecordList.size() != 0) {
                                                                                        Log.d(TAG, "some traffik records dfsdfsd");
                                                                                        SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                        sendTraffikRecordRequest.setImei(imei);
                                                                                        List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                        final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                        Log.d(TAG, "send exit records dsafsafassa");
                                                                                        parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                            @Override
                                                                                            public void onSuccess(final String result) {
                                                                                                Log.d(TAG, "delete traffik reords");
                                                                                                parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                    @Override
                                                                                                    public void onSuccess(boolean exit) {
                                                                                                        return;
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onFailed() {
                                                                                                        return;
                                                                                                    }
                                                                                                });

                                                                                            }

                                                                                            @Override
                                                                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                return;
                                                                                            }
                                                                                        });


                                                                                    } else {
                                                                                        Log.d(TAG, "no traffik record");
                                                                                        return;
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onFailed() {
                                                                                    Log.d(TAG, "no traffik record");
                                                                                    return;
                                                                                }
                                                                            });
                                                                        }
                                                                    });


                                                                }
                                                            });
                                                            return;
                                                        }
                                                    });















                                                }

                                                @Override
                                                public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                    parkbanRepository.getFromEntranceTable(new ParkbanRepository.DataBaseEntranceRecordResultCallBack() {
                                                        @Override
                                                        public void onSuccess(final List<EntranceRecord> entranceRecordList) {
                                                            if (entranceRecordList.size() != 0) {
                                                                Log.d(TAG, "some entrance records");
                                                                SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                                sendIoRecordRequest.setImei(imei);
                                                                List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                                final List<String> entrancePlatesForDelete = new ArrayList<>();
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
                                                                sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                                Log.d(TAG, "send entrance records");
                                                                parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                    @Override
                                                                    public void onSuccess(String result) {
                                                                        Log.d(TAG, "delete entrance records");
                                                                        parkbanRepository.deleteFromEntranceRecord(entrancePlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                            @Override
                                                                            public void onSuccess(final boolean entranceDeleteResult) {
                                                                                if (entranceDeleteResult) {
                                                                                    Log.d(TAG, "get from exit table");
                                                                                    parkbanRepository.getFromExitTable(new ParkbanRepository.DataBaseExitRecordResultCallBack() {
                                                                                        @Override
                                                                                        public void onSuccess(List<ExitRecord> exitRecordList) {
                                                                                            if (exitRecordList.size() != 0) {
                                                                                                Log.d(TAG, "some exit records");
                                                                                                SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                                                                sendIoRecordRequest.setImei(imei);
                                                                                                List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                                                                final List<String> exitPlatesForDelete = new ArrayList<>();
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
                                                                                                sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                                                                Log.d(TAG, "send exit records");
                                                                                                parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(final String result) {
                                                                                                        Log.d(TAG, "delete exit records");
                                                                                                        parkbanRepository.deleteFromExitRecord(exitPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                            @Override
                                                                                                            public void onSuccess(boolean exitDeleteResult) {
                                                                                                                if (exitDeleteResult) {
                                                                                                                    Log.d(TAG, "get from traffik table");
                                                                                                                    parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                                                        @Override
                                                                                                                        public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                                            if (traffikRecordList.size() != 0) {
                                                                                                                                Log.d(TAG, "some traffik records gdfds");
                                                                                                                                SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                                                sendTraffikRecordRequest.setImei(imei);
                                                                                                                                List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                                                final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                                                Log.d(TAG, "send exit records dgdss");
                                                                                                                                parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                                                    @Override
                                                                                                                                    public void onSuccess(final String result) {
                                                                                                                                        Log.d(TAG, "delete traffik reords  efgfds");
                                                                                                                                        parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                                            @Override
                                                                                                                                            public void onSuccess(boolean exit) {
                                                                                                                                                Log.d(TAG, "success delete trafiick records mhdsbjfdbs");
                                                                                                                                                return;
                                                                                                                                            }

                                                                                                                                            @Override
                                                                                                                                            public void onFailed() {
                                                                                                                                                Log.d(TAG, "failed delete trafiick records mhdsbjfdbs");
                                                                                                                                                return;
                                                                                                                                            }
                                                                                                                                        });

                                                                                                                                    }

                                                                                                                                    @Override
                                                                                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                                        Log.d(TAG, "failed send traffik hdskjfhk");
                                                                                                                                        return;
                                                                                                                                    }
                                                                                                                                });


                                                                                                                            } else {
                                                                                                                                Log.d(TAG, "no traffik record gsdgdfs");
                                                                                                                                return;
                                                                                                                            }
                                                                                                                        }

                                                                                                                        @Override
                                                                                                                        public void onFailed() {
                                                                                                                            Log.d(TAG, "no traffik record dfgsd");
                                                                                                                            return;
                                                                                                                        }
                                                                                                                    });
                                                                                                                } else {
                                                                                                                    Log.d(TAG, "failed delete exit records 5664");
                                                                                                                    return;
                                                                                                                }
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailed() {
                                                                                                                Log.d(TAG, "failed delete exit records 2332");
                                                                                                                return;
                                                                                                            }
                                                                                                        });
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                        Log.d(TAG, "Failed send exit records");
                                                                                                        return;
                                                                                                    }
                                                                                                });


                                                                                            } else {
                                                                                                Log.d(TAG, "no exit record");
                                                                                                Log.d(TAG, "get from traffik table");
                                                                                                parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                                    @Override
                                                                                                    public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                        if (traffikRecordList.size() != 0) {
                                                                                                            Log.d(TAG, "some traffik records ghjgjh");
                                                                                                            SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                            sendTraffikRecordRequest.setImei(imei);
                                                                                                            List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                            final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                            Log.d(TAG, "send exit records jkjhjkh");
                                                                                                            parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                                @Override
                                                                                                                public void onSuccess(final String result) {
                                                                                                                    Log.d(TAG, "delete traffik reords kjhjhkjhkj");
                                                                                                                    parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                        @Override
                                                                                                                        public void onSuccess(boolean exit) {
                                                                                                                            Log.d(TAG, "Done kjfjhdskjhfsdkjh");
                                                                                                                            return;
                                                                                                                        }

                                                                                                                        @Override
                                                                                                                        public void onFailed() {
                                                                                                                            Log.d(TAG, "failed delete traffik records kjfjhdskjhfsdkjh");
                                                                                                                            return;
                                                                                                                        }
                                                                                                                    });

                                                                                                                }

                                                                                                                @Override
                                                                                                                public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                    Log.d(TAG, "failed send traffik jhkj kjkjhkjhkjhkhjhghgj");
                                                                                                                    return;
                                                                                                                }
                                                                                                            });


                                                                                                        } else {
                                                                                                            Log.d(TAG, "no traffik record kjkjhkjhkjhkhjhghgj");
                                                                                                            return;
                                                                                                        }
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onFailed() {
                                                                                                        Log.d(TAG, "no traffik record kjjljjj");
                                                                                                        return;
                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onFailed() {
                                                                                            Log.d(TAG, "no exit record");
                                                                                            Log.d(TAG, "get from traffik table");
                                                                                            parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                                @Override
                                                                                                public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                    if (traffikRecordList.size() != 0) {
                                                                                                        Log.d(TAG, "some traffik records dfsdfsd");
                                                                                                        SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                        sendTraffikRecordRequest.setImei(imei);
                                                                                                        List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                        final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                        Log.d(TAG, "send exit records dsafsafassa");
                                                                                                        parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(final String result) {
                                                                                                                Log.d(TAG, "delete traffik reords");
                                                                                                                parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                    @Override
                                                                                                                    public void onSuccess(boolean exit) {
                                                                                                                        return;
                                                                                                                    }

                                                                                                                    @Override
                                                                                                                    public void onFailed() {
                                                                                                                        return;
                                                                                                                    }
                                                                                                                });

                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                return;
                                                                                                            }
                                                                                                        });


                                                                                                    } else {
                                                                                                        Log.d(TAG, "no traffik record");
                                                                                                        return;
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onFailed() {
                                                                                                    Log.d(TAG, "no traffik record");
                                                                                                    return;
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    });


                                                                                } else {
                                                                                    Log.d(TAG, "Failed delete entrance records-1");
                                                                                    return;
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onFailed() {
                                                                                Log.d(TAG, "Failed delete entrance records-2");
                                                                                return;
                                                                            }
                                                                        });
                                                                    }

                                                                    @Override
                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                        Log.d(TAG, "Failed send entrance records");
                                                                        return;
                                                                    }
                                                                });


                                                            } else {
                                                                Log.d(TAG, "no entrance record-1");
                                                                Log.d(TAG, "get from exit table");
                                                                parkbanRepository.getFromExitTable(new ParkbanRepository.DataBaseExitRecordResultCallBack() {
                                                                    @Override
                                                                    public void onSuccess(List<ExitRecord> exitRecordList) {
                                                                        if (exitRecordList.size() != 0) {
                                                                            Log.d(TAG, "some exit records");
                                                                            SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                                            sendIoRecordRequest.setImei(imei);
                                                                            List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                                            final List<String> exitPlatesForDelete = new ArrayList<>();
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
                                                                            sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                                            Log.d(TAG, "send exit records");
                                                                            parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                @Override
                                                                                public void onSuccess(final String result) {
                                                                                    Log.d(TAG, "delete exit records");
                                                                                    parkbanRepository.deleteFromExitRecord(exitPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                        @Override
                                                                                        public void onSuccess(boolean exitDeleteResult) {
                                                                                            if (exitDeleteResult) {
                                                                                                Log.d(TAG, "get from traffik table");
                                                                                                parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                                    @Override
                                                                                                    public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                        if (traffikRecordList.size() != 0) {
                                                                                                            Log.d(TAG, "some traffik records");
                                                                                                            SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                            sendTraffikRecordRequest.setImei(imei);
                                                                                                            List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                            final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                            Log.d(TAG, "send exit records");
                                                                                                            parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                                @Override
                                                                                                                public void onSuccess(final String result) {
                                                                                                                    Log.d(TAG, "delete traffik reords");
                                                                                                                    parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                        @Override
                                                                                                                        public void onSuccess(boolean exit) {
                                                                                                                            return;
                                                                                                                        }

                                                                                                                        @Override
                                                                                                                        public void onFailed() {
                                                                                                                            return;
                                                                                                                        }
                                                                                                                    });

                                                                                                                }

                                                                                                                @Override
                                                                                                                public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                    return;
                                                                                                                }
                                                                                                            });


                                                                                                        } else {
                                                                                                            Log.d(TAG, "no traffik record");
                                                                                                            return;
                                                                                                        }
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onFailed() {
                                                                                                        Log.d(TAG, "no traffik record");
                                                                                                        return;
                                                                                                    }
                                                                                                });
                                                                                            } else {
                                                                                                return;
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onFailed() {
                                                                                            return;
                                                                                        }
                                                                                    });
                                                                                }

                                                                                @Override
                                                                                public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                    return;
                                                                                }
                                                                            });


                                                                        } else {
                                                                            Log.d(TAG, "no exit record");
                                                                            Log.d(TAG, "get from traffik table");
                                                                            parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                @Override
                                                                                public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                    if (traffikRecordList.size() != 0) {
                                                                                        Log.d(TAG, "some traffik records dfsdfsd");
                                                                                        SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                        sendTraffikRecordRequest.setImei(imei);
                                                                                        List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                        final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                        Log.d(TAG, "send exit records dsafsafassa");
                                                                                        parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                            @Override
                                                                                            public void onSuccess(final String result) {
                                                                                                Log.d(TAG, "delete traffik reords");
                                                                                                parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                    @Override
                                                                                                    public void onSuccess(boolean exit) {
                                                                                                        return;
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onFailed() {
                                                                                                        return;
                                                                                                    }
                                                                                                });

                                                                                            }

                                                                                            @Override
                                                                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                return;
                                                                                            }
                                                                                        });


                                                                                    } else {
                                                                                        Log.d(TAG, "no traffik record");
                                                                                        return;
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onFailed() {
                                                                                    Log.d(TAG, "no traffik record");
                                                                                    return;
                                                                                }
                                                                            });
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onFailed() {
                                                                        Log.d(TAG, "no exit record");
                                                                        Log.d(TAG, "get from traffik table");
                                                                        parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                            @Override
                                                                            public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                if (traffikRecordList.size() != 0) {
                                                                                    Log.d(TAG, "some traffik records dfsdfsd");
                                                                                    SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                    sendTraffikRecordRequest.setImei(imei);
                                                                                    List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                    final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                    Log.d(TAG, "send exit records dsafsafassa");
                                                                                    parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                        @Override
                                                                                        public void onSuccess(final String result) {
                                                                                            Log.d(TAG, "delete traffik reords");
                                                                                            parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                @Override
                                                                                                public void onSuccess(boolean exit) {
                                                                                                    return;
                                                                                                }

                                                                                                @Override
                                                                                                public void onFailed() {
                                                                                                    return;
                                                                                                }
                                                                                            });

                                                                                        }

                                                                                        @Override
                                                                                        public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                            return;
                                                                                        }
                                                                                    });


                                                                                } else {
                                                                                    Log.d(TAG, "no traffik record");
                                                                                    return;
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onFailed() {
                                                                                Log.d(TAG, "no traffik record");
                                                                                return;
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailed() {
                                                            Log.d(TAG, "no entrance record-2");
                                                            Log.d(TAG, "get from exit table");
                                                            parkbanRepository.getFromExitTable(new ParkbanRepository.DataBaseExitRecordResultCallBack() {
                                                                @Override
                                                                public void onSuccess(List<ExitRecord> exitRecordList) {
                                                                    if (exitRecordList.size() != 0) {
                                                                        Log.d(TAG, "some exit records");
                                                                        SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                                        sendIoRecordRequest.setImei(imei);
                                                                        List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                                        final List<String> exitPlatesForDelete = new ArrayList<>();
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
                                                                        sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                                        Log.d(TAG, "send exit records");
                                                                        parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                            @Override
                                                                            public void onSuccess(final String result) {
                                                                                Log.d(TAG, "delete exit records");
                                                                                parkbanRepository.deleteFromExitRecord(exitPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                    @Override
                                                                                    public void onSuccess(boolean exitDeleteResult) {
                                                                                        if (exitDeleteResult) {
                                                                                            Log.d(TAG, "get from traffik table");
                                                                                            parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                                @Override
                                                                                                public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                    if (traffikRecordList.size() != 0) {
                                                                                                        Log.d(TAG, "some traffik records");
                                                                                                        SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                        sendTraffikRecordRequest.setImei(imei);
                                                                                                        List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                        final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                        Log.d(TAG, "send exit records");
                                                                                                        parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(final String result) {
                                                                                                                Log.d(TAG, "delete traffik reords");
                                                                                                                parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                    @Override
                                                                                                                    public void onSuccess(boolean exit) {
                                                                                                                        return;
                                                                                                                    }

                                                                                                                    @Override
                                                                                                                    public void onFailed() {
                                                                                                                        return;
                                                                                                                    }
                                                                                                                });

                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                return;
                                                                                                            }
                                                                                                        });


                                                                                                    } else {
                                                                                                        Log.d(TAG, "no traffik record");
                                                                                                        return;
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onFailed() {
                                                                                                    Log.d(TAG, "no traffik record");
                                                                                                    return;
                                                                                                }
                                                                                            });
                                                                                        } else {
                                                                                            return;
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onFailed() {
                                                                                        return;
                                                                                    }
                                                                                });
                                                                            }

                                                                            @Override
                                                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                return;
                                                                            }
                                                                        });


                                                                    } else {
                                                                        Log.d(TAG, "no exit record");
                                                                        Log.d(TAG, "get from traffik table");
                                                                        parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                            @Override
                                                                            public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                if (traffikRecordList.size() != 0) {
                                                                                    Log.d(TAG, "some traffik records dfsdfsd");
                                                                                    SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                    sendTraffikRecordRequest.setImei(imei);
                                                                                    List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                    final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                    Log.d(TAG, "send exit records dsafsafassa");
                                                                                    parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                        @Override
                                                                                        public void onSuccess(final String result) {
                                                                                            Log.d(TAG, "delete traffik reords");
                                                                                            parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                @Override
                                                                                                public void onSuccess(boolean exit) {
                                                                                                    return;
                                                                                                }

                                                                                                @Override
                                                                                                public void onFailed() {
                                                                                                    return;
                                                                                                }
                                                                                            });

                                                                                        }

                                                                                        @Override
                                                                                        public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                            return;
                                                                                        }
                                                                                    });


                                                                                } else {
                                                                                    Log.d(TAG, "no traffik record");
                                                                                    return;
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onFailed() {
                                                                                Log.d(TAG, "no traffik record");
                                                                                return;
                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFailed() {
                                                                    Log.d(TAG, "no exit record");
                                                                    Log.d(TAG, "get from traffik table");
                                                                    parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                        @Override
                                                                        public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                            if (traffikRecordList.size() != 0) {
                                                                                Log.d(TAG, "some traffik records dfsdfsd");
                                                                                SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                sendTraffikRecordRequest.setImei(imei);
                                                                                List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                Log.d(TAG, "send exit records dsafsafassa");
                                                                                parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                    @Override
                                                                                    public void onSuccess(final String result) {
                                                                                        Log.d(TAG, "delete traffik reords");
                                                                                        parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                            @Override
                                                                                            public void onSuccess(boolean exit) {
                                                                                                return;
                                                                                            }

                                                                                            @Override
                                                                                            public void onFailed() {
                                                                                                return;
                                                                                            }
                                                                                        });

                                                                                    }

                                                                                    @Override
                                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                        return;
                                                                                    }
                                                                                });


                                                                            } else {
                                                                                Log.d(TAG, "no traffik record");
                                                                                return;
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onFailed() {
                                                                            Log.d(TAG, "no traffik record");
                                                                            return;
                                                                        }
                                                                    });
                                                                }
                                                            });


                                                        }
                                                    });
                                                    return;

                                                }
                                            });
                                        } else {
                                            parkbanRepository.getFromEntranceTable(new ParkbanRepository.DataBaseEntranceRecordResultCallBack() {
                                                @Override
                                                public void onSuccess(final List<EntranceRecord> entranceRecordList) {
                                                    if (entranceRecordList.size() != 0) {
                                                        Log.d(TAG, "some entrance records");
                                                        SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                        sendIoRecordRequest.setImei(imei);
                                                        List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                        final List<String> entrancePlatesForDelete = new ArrayList<>();
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
                                                        sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                        Log.d(TAG, "send entrance records");
                                                        parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                            @Override
                                                            public void onSuccess(String result) {
                                                                Log.d(TAG, "delete entrance records");
                                                                parkbanRepository.deleteFromEntranceRecord(entrancePlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                    @Override
                                                                    public void onSuccess(final boolean entranceDeleteResult) {
                                                                        if (entranceDeleteResult) {
                                                                            Log.d(TAG, "get from exit table");
                                                                            parkbanRepository.getFromExitTable(new ParkbanRepository.DataBaseExitRecordResultCallBack() {
                                                                                @Override
                                                                                public void onSuccess(List<ExitRecord> exitRecordList) {
                                                                                    if (exitRecordList.size() != 0) {
                                                                                        Log.d(TAG, "some exit records");
                                                                                        SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                                                        sendIoRecordRequest.setImei(imei);
                                                                                        List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                                                        final List<String> exitPlatesForDelete = new ArrayList<>();
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
                                                                                        sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                                                        Log.d(TAG, "send exit records");
                                                                                        parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                            @Override
                                                                                            public void onSuccess(final String result) {
                                                                                                Log.d(TAG, "delete exit records");
                                                                                                parkbanRepository.deleteFromExitRecord(exitPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                    @Override
                                                                                                    public void onSuccess(boolean exitDeleteResult) {
                                                                                                        if (exitDeleteResult) {
                                                                                                            Log.d(TAG, "get from traffik table");
                                                                                                            parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                                                @Override
                                                                                                                public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                                    if (traffikRecordList.size() != 0) {
                                                                                                                        Log.d(TAG, "some traffik records gdfds");
                                                                                                                        SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                                        sendTraffikRecordRequest.setImei(imei);
                                                                                                                        List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                                        final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                                        Log.d(TAG, "send exit records dgdss");
                                                                                                                        parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(final String result) {
                                                                                                                                Log.d(TAG, "delete traffik reords  efgfds");
                                                                                                                                parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                                    @Override
                                                                                                                                    public void onSuccess(boolean exit) {
                                                                                                                                        Log.d(TAG, "success delete trafiick records mhdsbjfdbs");
                                                                                                                                        return;
                                                                                                                                    }

                                                                                                                                    @Override
                                                                                                                                    public void onFailed() {
                                                                                                                                        Log.d(TAG, "failed delete trafiick records mhdsbjfdbs");
                                                                                                                                        return;
                                                                                                                                    }
                                                                                                                                });

                                                                                                                            }

                                                                                                                            @Override
                                                                                                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                                Log.d(TAG, "failed send traffik hdskjfhk");
                                                                                                                                return;
                                                                                                                            }
                                                                                                                        });


                                                                                                                    } else {
                                                                                                                        Log.d(TAG, "no traffik record gsdgdfs");
                                                                                                                        return;
                                                                                                                    }
                                                                                                                }

                                                                                                                @Override
                                                                                                                public void onFailed() {
                                                                                                                    Log.d(TAG, "no traffik record dfgsd");
                                                                                                                    return;
                                                                                                                }
                                                                                                            });
                                                                                                        } else {
                                                                                                            Log.d(TAG, "failed delete exit records 5664");
                                                                                                            return;
                                                                                                        }
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onFailed() {
                                                                                                        Log.d(TAG, "failed delete exit records 2332");
                                                                                                        return;
                                                                                                    }
                                                                                                });
                                                                                            }

                                                                                            @Override
                                                                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                Log.d(TAG, "Failed send exit records");
                                                                                                return;
                                                                                            }
                                                                                        });


                                                                                    } else {
                                                                                        Log.d(TAG, "no exit record");
                                                                                        Log.d(TAG, "get from traffik table");
                                                                                        parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                            @Override
                                                                                            public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                if (traffikRecordList.size() != 0) {
                                                                                                    Log.d(TAG, "some traffik records ghjgjh");
                                                                                                    SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                    sendTraffikRecordRequest.setImei(imei);
                                                                                                    List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                    final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                    Log.d(TAG, "send exit records jkjhjkh");
                                                                                                    parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(final String result) {
                                                                                                            Log.d(TAG, "delete traffik reords kjhjhkjhkj");
                                                                                                            parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                @Override
                                                                                                                public void onSuccess(boolean exit) {
                                                                                                                    Log.d(TAG, "Done kjfjhdskjhfsdkjh");
                                                                                                                    return;
                                                                                                                }

                                                                                                                @Override
                                                                                                                public void onFailed() {
                                                                                                                    Log.d(TAG, "failed delete traffik records kjfjhdskjhfsdkjh");
                                                                                                                    return;
                                                                                                                }
                                                                                                            });

                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                            Log.d(TAG, "failed send traffik jhkj kjkjhkjhkjhkhjhghgj");
                                                                                                            return;
                                                                                                        }
                                                                                                    });


                                                                                                } else {
                                                                                                    Log.d(TAG, "no traffik record kjkjhkjhkjhkhjhghgj");
                                                                                                    return;
                                                                                                }
                                                                                            }

                                                                                            @Override
                                                                                            public void onFailed() {
                                                                                                Log.d(TAG, "no traffik record kjjljjj");
                                                                                                return;
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onFailed() {
                                                                                    Log.d(TAG, "no exit record");
                                                                                    Log.d(TAG, "get from traffik table");
                                                                                    parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                        @Override
                                                                                        public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                            if (traffikRecordList.size() != 0) {
                                                                                                Log.d(TAG, "some traffik records dfsdfsd");
                                                                                                SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                sendTraffikRecordRequest.setImei(imei);
                                                                                                List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                Log.d(TAG, "send exit records dsafsafassa");
                                                                                                parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(final String result) {
                                                                                                        Log.d(TAG, "delete traffik reords");
                                                                                                        parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                            @Override
                                                                                                            public void onSuccess(boolean exit) {
                                                                                                                return;
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailed() {
                                                                                                                return;
                                                                                                            }
                                                                                                        });

                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                        return;
                                                                                                    }
                                                                                                });


                                                                                            } else {
                                                                                                Log.d(TAG, "no traffik record");
                                                                                                return;
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onFailed() {
                                                                                            Log.d(TAG, "no traffik record");
                                                                                            return;
                                                                                        }
                                                                                    });
                                                                                }
                                                                            });


                                                                        } else {
                                                                            Log.d(TAG, "Failed delete entrance records-1");
                                                                            return;
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onFailed() {
                                                                        Log.d(TAG, "Failed delete entrance records-2");
                                                                        return;
                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                Log.d(TAG, "Failed send entrance records");
                                                                return;
                                                            }
                                                        });


                                                    } else {
                                                        Log.d(TAG, "no entrance record-1");
                                                        Log.d(TAG, "get from exit table");
                                                        parkbanRepository.getFromExitTable(new ParkbanRepository.DataBaseExitRecordResultCallBack() {
                                                            @Override
                                                            public void onSuccess(List<ExitRecord> exitRecordList) {
                                                                if (exitRecordList.size() != 0) {
                                                                    Log.d(TAG, "some exit records");
                                                                    SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                                    sendIoRecordRequest.setImei(imei);
                                                                    List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                                    final List<String> exitPlatesForDelete = new ArrayList<>();
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
                                                                    sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                                    Log.d(TAG, "send exit records");
                                                                    parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                        @Override
                                                                        public void onSuccess(final String result) {
                                                                            Log.d(TAG, "delete exit records");
                                                                            parkbanRepository.deleteFromExitRecord(exitPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                @Override
                                                                                public void onSuccess(boolean exitDeleteResult) {
                                                                                    if (exitDeleteResult) {
                                                                                        Log.d(TAG, "get from traffik table");
                                                                                        parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                            @Override
                                                                                            public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                if (traffikRecordList.size() != 0) {
                                                                                                    Log.d(TAG, "some traffik records");
                                                                                                    SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                    sendTraffikRecordRequest.setImei(imei);
                                                                                                    List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                    final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                    Log.d(TAG, "send exit records");
                                                                                                    parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(final String result) {
                                                                                                            Log.d(TAG, "delete traffik reords");
                                                                                                            parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                @Override
                                                                                                                public void onSuccess(boolean exit) {
                                                                                                                    return;
                                                                                                                }

                                                                                                                @Override
                                                                                                                public void onFailed() {
                                                                                                                    return;
                                                                                                                }
                                                                                                            });

                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                            return;
                                                                                                        }
                                                                                                    });


                                                                                                } else {
                                                                                                    Log.d(TAG, "no traffik record");
                                                                                                    return;
                                                                                                }
                                                                                            }

                                                                                            @Override
                                                                                            public void onFailed() {
                                                                                                Log.d(TAG, "no traffik record");
                                                                                                return;
                                                                                            }
                                                                                        });
                                                                                    } else {
                                                                                        return;
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onFailed() {
                                                                                    return;
                                                                                }
                                                                            });
                                                                        }

                                                                        @Override
                                                                        public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                            return;
                                                                        }
                                                                    });


                                                                } else {
                                                                    Log.d(TAG, "no exit record");
                                                                    Log.d(TAG, "get from traffik table");
                                                                    parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                        @Override
                                                                        public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                            if (traffikRecordList.size() != 0) {
                                                                                Log.d(TAG, "some traffik records dfsdfsd");
                                                                                SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                sendTraffikRecordRequest.setImei(imei);
                                                                                List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                Log.d(TAG, "send exit records dsafsafassa");
                                                                                parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                    @Override
                                                                                    public void onSuccess(final String result) {
                                                                                        Log.d(TAG, "delete traffik reords");
                                                                                        parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                            @Override
                                                                                            public void onSuccess(boolean exit) {
                                                                                                return;
                                                                                            }

                                                                                            @Override
                                                                                            public void onFailed() {
                                                                                                return;
                                                                                            }
                                                                                        });

                                                                                    }

                                                                                    @Override
                                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                        return;
                                                                                    }
                                                                                });


                                                                            } else {
                                                                                Log.d(TAG, "no traffik record");
                                                                                return;
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onFailed() {
                                                                            Log.d(TAG, "no traffik record");
                                                                            return;
                                                                        }
                                                                    });
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailed() {
                                                                Log.d(TAG, "no exit record");
                                                                Log.d(TAG, "get from traffik table");
                                                                parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                    @Override
                                                                    public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                        if (traffikRecordList.size() != 0) {
                                                                            Log.d(TAG, "some traffik records dfsdfsd");
                                                                            SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                            sendTraffikRecordRequest.setImei(imei);
                                                                            List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                            final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                            Log.d(TAG, "send exit records dsafsafassa");
                                                                            parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                @Override
                                                                                public void onSuccess(final String result) {
                                                                                    Log.d(TAG, "delete traffik reords");
                                                                                    parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                        @Override
                                                                                        public void onSuccess(boolean exit) {
                                                                                            return;
                                                                                        }

                                                                                        @Override
                                                                                        public void onFailed() {
                                                                                            return;
                                                                                        }
                                                                                    });

                                                                                }

                                                                                @Override
                                                                                public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                    return;
                                                                                }
                                                                            });


                                                                        } else {
                                                                            Log.d(TAG, "no traffik record");
                                                                            return;
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onFailed() {
                                                                        Log.d(TAG, "no traffik record");
                                                                        return;
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onFailed() {
                                                    Log.d(TAG, "no entrance record-2");
                                                    Log.d(TAG, "get from exit table");
                                                    parkbanRepository.getFromExitTable(new ParkbanRepository.DataBaseExitRecordResultCallBack() {
                                                        @Override
                                                        public void onSuccess(List<ExitRecord> exitRecordList) {
                                                            if (exitRecordList.size() != 0) {
                                                                Log.d(TAG, "some exit records");
                                                                SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                                sendIoRecordRequest.setImei(imei);
                                                                List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                                final List<String> exitPlatesForDelete = new ArrayList<>();
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
                                                                sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                                Log.d(TAG, "send exit records");
                                                                parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                    @Override
                                                                    public void onSuccess(final String result) {
                                                                        Log.d(TAG, "delete exit records");
                                                                        parkbanRepository.deleteFromExitRecord(exitPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                            @Override
                                                                            public void onSuccess(boolean exitDeleteResult) {
                                                                                if (exitDeleteResult) {
                                                                                    Log.d(TAG, "get from traffik table");
                                                                                    parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                        @Override
                                                                                        public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                            if (traffikRecordList.size() != 0) {
                                                                                                Log.d(TAG, "some traffik records");
                                                                                                SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                sendTraffikRecordRequest.setImei(imei);
                                                                                                List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                Log.d(TAG, "send exit records");
                                                                                                parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(final String result) {
                                                                                                        Log.d(TAG, "delete traffik reords");
                                                                                                        parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                            @Override
                                                                                                            public void onSuccess(boolean exit) {
                                                                                                                return;
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailed() {
                                                                                                                return;
                                                                                                            }
                                                                                                        });

                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                        return;
                                                                                                    }
                                                                                                });


                                                                                            } else {
                                                                                                Log.d(TAG, "no traffik record");
                                                                                                return;
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onFailed() {
                                                                                            Log.d(TAG, "no traffik record");
                                                                                            return;
                                                                                        }
                                                                                    });
                                                                                } else {
                                                                                    return;
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onFailed() {
                                                                                return;
                                                                            }
                                                                        });
                                                                    }

                                                                    @Override
                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                        return;
                                                                    }
                                                                });


                                                            } else {
                                                                Log.d(TAG, "no exit record");
                                                                Log.d(TAG, "get from traffik table");
                                                                parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                    @Override
                                                                    public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                        if (traffikRecordList.size() != 0) {
                                                                            Log.d(TAG, "some traffik records dfsdfsd");
                                                                            SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                            sendTraffikRecordRequest.setImei(imei);
                                                                            List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                            final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                            Log.d(TAG, "send exit records dsafsafassa");
                                                                            parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                @Override
                                                                                public void onSuccess(final String result) {
                                                                                    Log.d(TAG, "delete traffik reords");
                                                                                    parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                        @Override
                                                                                        public void onSuccess(boolean exit) {
                                                                                            return;
                                                                                        }

                                                                                        @Override
                                                                                        public void onFailed() {
                                                                                            return;
                                                                                        }
                                                                                    });

                                                                                }

                                                                                @Override
                                                                                public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                    return;
                                                                                }
                                                                            });


                                                                        } else {
                                                                            Log.d(TAG, "no traffik record");
                                                                            return;
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onFailed() {
                                                                        Log.d(TAG, "no traffik record");
                                                                        return;
                                                                    }
                                                                });
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailed() {
                                                            Log.d(TAG, "no exit record");
                                                            Log.d(TAG, "get from traffik table");
                                                            parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                @Override
                                                                public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                    if (traffikRecordList.size() != 0) {
                                                                        Log.d(TAG, "some traffik records dfsdfsd");
                                                                        SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                        sendTraffikRecordRequest.setImei(imei);
                                                                        List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                        final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                        Log.d(TAG, "send exit records dsafsafassa");
                                                                        parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                            @Override
                                                                            public void onSuccess(final String result) {
                                                                                Log.d(TAG, "delete traffik reords");
                                                                                parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                    @Override
                                                                                    public void onSuccess(boolean exit) {
                                                                                        return;
                                                                                    }

                                                                                    @Override
                                                                                    public void onFailed() {
                                                                                        return;
                                                                                    }
                                                                                });

                                                                            }

                                                                            @Override
                                                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                return;
                                                                            }
                                                                        });


                                                                    } else {
                                                                        Log.d(TAG, "no traffik record");
                                                                        return;
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFailed() {
                                                                    Log.d(TAG, "no traffik record");
                                                                    return;
                                                                }
                                                            });
                                                        }
                                                    });


                                                }
                                            });
                                            return;
                                        }


                                    }

                                    @Override
                                    public void onFailed() {
                                        parkbanRepository.getFromEntranceTable(new ParkbanRepository.DataBaseEntranceRecordResultCallBack() {
                                            @Override
                                            public void onSuccess(final List<EntranceRecord> entranceRecordList) {
                                                if (entranceRecordList.size() != 0) {
                                                    Log.d(TAG, "some entrance records");
                                                    SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                    sendIoRecordRequest.setImei(imei);
                                                    List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                    final List<String> entrancePlatesForDelete = new ArrayList<>();
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
                                                    sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                    Log.d(TAG, "send entrance records");
                                                    parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                        @Override
                                                        public void onSuccess(String result) {
                                                            Log.d(TAG, "delete entrance records");
                                                            parkbanRepository.deleteFromEntranceRecord(entrancePlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                @Override
                                                                public void onSuccess(final boolean entranceDeleteResult) {
                                                                    if (entranceDeleteResult) {
                                                                        Log.d(TAG, "get from exit table");
                                                                        parkbanRepository.getFromExitTable(new ParkbanRepository.DataBaseExitRecordResultCallBack() {
                                                                            @Override
                                                                            public void onSuccess(List<ExitRecord> exitRecordList) {
                                                                                if (exitRecordList.size() != 0) {
                                                                                    Log.d(TAG, "some exit records");
                                                                                    SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                                                    sendIoRecordRequest.setImei(imei);
                                                                                    List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                                                    final List<String> exitPlatesForDelete = new ArrayList<>();
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
                                                                                    sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                                                    Log.d(TAG, "send exit records");
                                                                                    parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                        @Override
                                                                                        public void onSuccess(final String result) {
                                                                                            Log.d(TAG, "delete exit records");
                                                                                            parkbanRepository.deleteFromExitRecord(exitPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                @Override
                                                                                                public void onSuccess(boolean exitDeleteResult) {
                                                                                                    if (exitDeleteResult) {
                                                                                                        Log.d(TAG, "get from traffik table");
                                                                                                        parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                                            @Override
                                                                                                            public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                                                if (traffikRecordList.size() != 0) {
                                                                                                                    Log.d(TAG, "some traffik records gdfds");
                                                                                                                    SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                                    sendTraffikRecordRequest.setImei(imei);
                                                                                                                    List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                                    final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                                    Log.d(TAG, "send exit records dgdss");
                                                                                                                    parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                                        @Override
                                                                                                                        public void onSuccess(final String result) {
                                                                                                                            Log.d(TAG, "delete traffik reords  efgfds");
                                                                                                                            parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                                                @Override
                                                                                                                                public void onSuccess(boolean exit) {
                                                                                                                                    Log.d(TAG, "success delete trafiick records mhdsbjfdbs");
                                                                                                                                    return;
                                                                                                                                }

                                                                                                                                @Override
                                                                                                                                public void onFailed() {
                                                                                                                                    Log.d(TAG, "failed delete trafiick records mhdsbjfdbs");
                                                                                                                                    return;
                                                                                                                                }
                                                                                                                            });

                                                                                                                        }

                                                                                                                        @Override
                                                                                                                        public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                                            Log.d(TAG, "failed send traffik hdskjfhk");
                                                                                                                            return;
                                                                                                                        }
                                                                                                                    });


                                                                                                                } else {
                                                                                                                    Log.d(TAG, "no traffik record gsdgdfs");
                                                                                                                    return;
                                                                                                                }
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailed() {
                                                                                                                Log.d(TAG, "no traffik record dfgsd");
                                                                                                                return;
                                                                                                            }
                                                                                                        });
                                                                                                    } else {
                                                                                                        Log.d(TAG, "failed delete exit records 5664");
                                                                                                        return;
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onFailed() {
                                                                                                    Log.d(TAG, "failed delete exit records 2332");
                                                                                                    return;
                                                                                                }
                                                                                            });
                                                                                        }

                                                                                        @Override
                                                                                        public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                            Log.d(TAG, "Failed send exit records");
                                                                                            return;
                                                                                        }
                                                                                    });


                                                                                } else {
                                                                                    Log.d(TAG, "no exit record");
                                                                                    Log.d(TAG, "get from traffik table");
                                                                                    parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                        @Override
                                                                                        public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                            if (traffikRecordList.size() != 0) {
                                                                                                Log.d(TAG, "some traffik records ghjgjh");
                                                                                                SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                sendTraffikRecordRequest.setImei(imei);
                                                                                                List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                Log.d(TAG, "send exit records jkjhjkh");
                                                                                                parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(final String result) {
                                                                                                        Log.d(TAG, "delete traffik reords kjhjhkjhkj");
                                                                                                        parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                            @Override
                                                                                                            public void onSuccess(boolean exit) {
                                                                                                                Log.d(TAG, "Done kjfjhdskjhfsdkjh");
                                                                                                                return;
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailed() {
                                                                                                                Log.d(TAG, "failed delete traffik records kjfjhdskjhfsdkjh");
                                                                                                                return;
                                                                                                            }
                                                                                                        });

                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                        Log.d(TAG, "failed send traffik jhkj kjkjhkjhkjhkhjhghgj");
                                                                                                        return;
                                                                                                    }
                                                                                                });


                                                                                            } else {
                                                                                                Log.d(TAG, "no traffik record kjkjhkjhkjhkhjhghgj");
                                                                                                return;
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onFailed() {
                                                                                            Log.d(TAG, "no traffik record kjjljjj");
                                                                                            return;
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onFailed() {
                                                                                Log.d(TAG, "no exit record");
                                                                                Log.d(TAG, "get from traffik table");
                                                                                parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                    @Override
                                                                                    public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                        if (traffikRecordList.size() != 0) {
                                                                                            Log.d(TAG, "some traffik records dfsdfsd");
                                                                                            SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                            sendTraffikRecordRequest.setImei(imei);
                                                                                            List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                            final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                            Log.d(TAG, "send exit records dsafsafassa");
                                                                                            parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                @Override
                                                                                                public void onSuccess(final String result) {
                                                                                                    Log.d(TAG, "delete traffik reords");
                                                                                                    parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                        @Override
                                                                                                        public void onSuccess(boolean exit) {
                                                                                                            return;
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onFailed() {
                                                                                                            return;
                                                                                                        }
                                                                                                    });

                                                                                                }

                                                                                                @Override
                                                                                                public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                    return;
                                                                                                }
                                                                                            });


                                                                                        } else {
                                                                                            Log.d(TAG, "no traffik record");
                                                                                            return;
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onFailed() {
                                                                                        Log.d(TAG, "no traffik record");
                                                                                        return;
                                                                                    }
                                                                                });
                                                                            }
                                                                        });


                                                                    } else {
                                                                        Log.d(TAG, "Failed delete entrance records-1");
                                                                        return;
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFailed() {
                                                                    Log.d(TAG, "Failed delete entrance records-2");
                                                                    return;
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                            Log.d(TAG, "Failed send entrance records");
                                                            return;
                                                        }
                                                    });


                                                } else {
                                                    Log.d(TAG, "no entrance record-1");
                                                    Log.d(TAG, "get from exit table");
                                                    parkbanRepository.getFromExitTable(new ParkbanRepository.DataBaseExitRecordResultCallBack() {
                                                        @Override
                                                        public void onSuccess(List<ExitRecord> exitRecordList) {
                                                            if (exitRecordList.size() != 0) {
                                                                Log.d(TAG, "some exit records");
                                                                SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                                sendIoRecordRequest.setImei(imei);
                                                                List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                                final List<String> exitPlatesForDelete = new ArrayList<>();
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
                                                                sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                                Log.d(TAG, "send exit records");
                                                                parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                    @Override
                                                                    public void onSuccess(final String result) {
                                                                        Log.d(TAG, "delete exit records");
                                                                        parkbanRepository.deleteFromExitRecord(exitPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                            @Override
                                                                            public void onSuccess(boolean exitDeleteResult) {
                                                                                if (exitDeleteResult) {
                                                                                    Log.d(TAG, "get from traffik table");
                                                                                    parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                        @Override
                                                                                        public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                            if (traffikRecordList.size() != 0) {
                                                                                                Log.d(TAG, "some traffik records");
                                                                                                SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                                sendTraffikRecordRequest.setImei(imei);
                                                                                                List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                                final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                                Log.d(TAG, "send exit records");
                                                                                                parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(final String result) {
                                                                                                        Log.d(TAG, "delete traffik reords");
                                                                                                        parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                            @Override
                                                                                                            public void onSuccess(boolean exit) {
                                                                                                                return;
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailed() {
                                                                                                                return;
                                                                                                            }
                                                                                                        });

                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                        return;
                                                                                                    }
                                                                                                });


                                                                                            } else {
                                                                                                Log.d(TAG, "no traffik record");
                                                                                                return;
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onFailed() {
                                                                                            Log.d(TAG, "no traffik record");
                                                                                            return;
                                                                                        }
                                                                                    });
                                                                                } else {
                                                                                    return;
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onFailed() {
                                                                                return;
                                                                            }
                                                                        });
                                                                    }

                                                                    @Override
                                                                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                        return;
                                                                    }
                                                                });


                                                            } else {
                                                                Log.d(TAG, "no exit record");
                                                                Log.d(TAG, "get from traffik table");
                                                                parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                    @Override
                                                                    public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                        if (traffikRecordList.size() != 0) {
                                                                            Log.d(TAG, "some traffik records dfsdfsd");
                                                                            SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                            sendTraffikRecordRequest.setImei(imei);
                                                                            List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                            final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                            Log.d(TAG, "send exit records dsafsafassa");
                                                                            parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                @Override
                                                                                public void onSuccess(final String result) {
                                                                                    Log.d(TAG, "delete traffik reords");
                                                                                    parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                        @Override
                                                                                        public void onSuccess(boolean exit) {
                                                                                            return;
                                                                                        }

                                                                                        @Override
                                                                                        public void onFailed() {
                                                                                            return;
                                                                                        }
                                                                                    });

                                                                                }

                                                                                @Override
                                                                                public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                    return;
                                                                                }
                                                                            });


                                                                        } else {
                                                                            Log.d(TAG, "no traffik record");
                                                                            return;
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onFailed() {
                                                                        Log.d(TAG, "no traffik record");
                                                                        return;
                                                                    }
                                                                });
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailed() {
                                                            Log.d(TAG, "no exit record");
                                                            Log.d(TAG, "get from traffik table");
                                                            parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                @Override
                                                                public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                    if (traffikRecordList.size() != 0) {
                                                                        Log.d(TAG, "some traffik records dfsdfsd");
                                                                        SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                        sendTraffikRecordRequest.setImei(imei);
                                                                        List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                        final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                        Log.d(TAG, "send exit records dsafsafassa");
                                                                        parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                            @Override
                                                                            public void onSuccess(final String result) {
                                                                                Log.d(TAG, "delete traffik reords");
                                                                                parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                    @Override
                                                                                    public void onSuccess(boolean exit) {
                                                                                        return;
                                                                                    }

                                                                                    @Override
                                                                                    public void onFailed() {
                                                                                        return;
                                                                                    }
                                                                                });

                                                                            }

                                                                            @Override
                                                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                return;
                                                                            }
                                                                        });


                                                                    } else {
                                                                        Log.d(TAG, "no traffik record");
                                                                        return;
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFailed() {
                                                                    Log.d(TAG, "no traffik record");
                                                                    return;
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onFailed() {
                                                Log.d(TAG, "no entrance record-2");
                                                Log.d(TAG, "get from exit table");
                                                parkbanRepository.getFromExitTable(new ParkbanRepository.DataBaseExitRecordResultCallBack() {
                                                    @Override
                                                    public void onSuccess(List<ExitRecord> exitRecordList) {
                                                        if (exitRecordList.size() != 0) {
                                                            Log.d(TAG, "some exit records");
                                                            SendIoRecordRequest sendIoRecordRequest = new SendIoRecordRequest();
                                                            sendIoRecordRequest.setImei(imei);
                                                            List<DeviceIORecord> deviceIORecordList = new ArrayList<>();
                                                            final List<String> exitPlatesForDelete = new ArrayList<>();
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
                                                            sendIoRecordRequest.setDeviceIORecords(deviceIORecordList);
                                                            Log.d(TAG, "send exit records");
                                                            parkbanRepository.sendIo(sendIoRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                @Override
                                                                public void onSuccess(final String result) {
                                                                    Log.d(TAG, "delete exit records");
                                                                    parkbanRepository.deleteFromExitRecord(exitPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                        @Override
                                                                        public void onSuccess(boolean exitDeleteResult) {
                                                                            if (exitDeleteResult) {
                                                                                Log.d(TAG, "get from traffik table");
                                                                                parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                                    @Override
                                                                                    public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                                        if (traffikRecordList.size() != 0) {
                                                                                            Log.d(TAG, "some traffik records");
                                                                                            SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                                            sendTraffikRecordRequest.setImei(imei);
                                                                                            List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                                            final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                                            Log.d(TAG, "send exit records");
                                                                                            parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                                                @Override
                                                                                                public void onSuccess(final String result) {
                                                                                                    Log.d(TAG, "delete traffik reords");
                                                                                                    parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                                        @Override
                                                                                                        public void onSuccess(boolean exit) {
                                                                                                            return;
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onFailed() {
                                                                                                            return;
                                                                                                        }
                                                                                                    });

                                                                                                }

                                                                                                @Override
                                                                                                public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                                    return;
                                                                                                }
                                                                                            });


                                                                                        } else {
                                                                                            Log.d(TAG, "no traffik record");
                                                                                            return;
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onFailed() {
                                                                                        Log.d(TAG, "no traffik record");
                                                                                        return;
                                                                                    }
                                                                                });
                                                                            } else {
                                                                                return;
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onFailed() {
                                                                            return;
                                                                        }
                                                                    });
                                                                }

                                                                @Override
                                                                public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                    return;
                                                                }
                                                            });


                                                        } else {
                                                            Log.d(TAG, "no exit record");
                                                            Log.d(TAG, "get from traffik table");
                                                            parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                                @Override
                                                                public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                    if (traffikRecordList.size() != 0) {
                                                                        Log.d(TAG, "some traffik records dfsdfsd");
                                                                        SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                        sendTraffikRecordRequest.setImei(imei);
                                                                        List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                        final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                        Log.d(TAG, "send exit records dsafsafassa");
                                                                        parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                            @Override
                                                                            public void onSuccess(final String result) {
                                                                                Log.d(TAG, "delete traffik reords");
                                                                                parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                    @Override
                                                                                    public void onSuccess(boolean exit) {
                                                                                        return;
                                                                                    }

                                                                                    @Override
                                                                                    public void onFailed() {
                                                                                        return;
                                                                                    }
                                                                                });

                                                                            }

                                                                            @Override
                                                                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                                return;
                                                                            }
                                                                        });


                                                                    } else {
                                                                        Log.d(TAG, "no traffik record");
                                                                        return;
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFailed() {
                                                                    Log.d(TAG, "no traffik record");
                                                                    return;
                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailed() {
                                                        Log.d(TAG, "no exit record");
                                                        Log.d(TAG, "get from traffik table");
                                                        parkbanRepository.getFromTraffikTable(new ParkbanRepository.DataBaseTraffikRecordResultCallBack() {
                                                            @Override
                                                            public void onSuccess(List<TraffikRecord> traffikRecordList) {
                                                                if (traffikRecordList.size() != 0) {
                                                                    Log.d(TAG, "some traffik records dfsdfsd");
                                                                    SendTraffikRecordRequest sendTraffikRecordRequest = new SendTraffikRecordRequest();
                                                                    sendTraffikRecordRequest.setImei(imei);
                                                                    List<TrafficRecord> deviceTraffikRecordList = new ArrayList<>();
                                                                    final List<String> traffikPlatesForDelete = new ArrayList<>();
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
                                                                    Log.d(TAG, "send exit records dsafsafassa");
                                                                    parkbanRepository.sendTraffik(sendTraffikRecordRequest, new ParkbanRepository.ServiceResultCallBack<String>() {
                                                                        @Override
                                                                        public void onSuccess(final String result) {
                                                                            Log.d(TAG, "delete traffik reords");
                                                                            parkbanRepository.deleteFromTraffikRecord(traffikPlatesForDelete, new ParkbanRepository.DataBaseBooleanCallBack() {
                                                                                @Override
                                                                                public void onSuccess(boolean exit) {
                                                                                    return;
                                                                                }

                                                                                @Override
                                                                                public void onFailed() {
                                                                                    return;
                                                                                }
                                                                            });

                                                                        }

                                                                        @Override
                                                                        public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                                                            return;
                                                                        }
                                                                    });


                                                                } else {
                                                                    Log.d(TAG, "no traffik record");
                                                                    return;
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailed() {
                                                                Log.d(TAG, "no traffik record");
                                                                return;
                                                            }
                                                        });
                                                    }
                                                });


                                            }
                                        });
                                        return;
                                    }
                                });


//                                activity.sendRecordServiceResult("Done");
                            }
                        } else {
                            // Internet Not Available...
                            Log.d(TAG, "run > Internet Not Available...");
                        }
                    }
                });


                Thread.sleep(sleep);

            } catch (Exception ex) {


                Log.d(TAG, "run > SendRecordHandler exception :" + ex.getMessage());


            }
        }
    }


    public interface Callbacks {
        void sendRecordServiceResult(String message);
    }


}


