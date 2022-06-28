package com.khodmohaseb.parkban.viewmodels;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.device.PrinterManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.khodmohaseb.parkban.BaseActivity;
import com.khodmohaseb.parkban.EnterManullyActivity;
import com.khodmohaseb.parkban.ExitQrActivity;
import com.khodmohaseb.parkban.LoginActivity;
import com.khodmohaseb.parkban.MifareCardActivity;
import com.khodmohaseb.parkban.PaymentSafshekanActivity;
import com.khodmohaseb.parkban.QRcodeReaderActivity;
import com.khodmohaseb.parkban.R;
import com.khodmohaseb.parkban.ReportActivity;
import com.khodmohaseb.parkban.anpr.farsi_ocr_anpr.FarsiOcrAnprProvider;
import com.khodmohaseb.parkban.core.anpr.BaseAnprProvider;
import com.khodmohaseb.parkban.core.anpr.helpers.PlateDetectionState;
import com.khodmohaseb.parkban.core.anpr.helpers.RidingType;
import com.khodmohaseb.parkban.core.anpr.onPlateDetectedCallback;
import com.khodmohaseb.parkban.helper.DateTimeHelper;
import com.khodmohaseb.parkban.helper.FontHelper;
import com.khodmohaseb.parkban.helper.ImageLoadHelper;
import com.khodmohaseb.parkban.helper.PrintParkbanApp;
import com.khodmohaseb.parkban.helper.PrinterUtils;
import com.khodmohaseb.parkban.helper.ShowToast;
import com.khodmohaseb.parkban.persistence.ParkbanDatabase;
import com.khodmohaseb.parkban.persistence.models.CarPlate;
import com.khodmohaseb.parkban.persistence.models.ResponseResultType;
import com.khodmohaseb.parkban.persistence.models.khodmohaseb.EntranceRecord;
import com.khodmohaseb.parkban.repositories.ParkbanRepository;
import com.khodmohaseb.parkban.services.dto.ExitBillDto;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.forgotrecord.ForgotEntranceRequest;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.forgotrecord.ForgotRecordResponse;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.Door;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.GetParkingInfoResponse;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.Operator;
import com.khodmohaseb.parkban.utils.Animation_Constant;
import com.khodmohaseb.parkban.utils.CalculateHelperUtility;
import com.khodmohaseb.parkban.utils.DailyFareCalculator;
import com.khodmohaseb.parkban.utils.DailyHourlyFareCalculator;
import com.khodmohaseb.parkban.utils.HourlyFareCalculator;
import com.khodmohaseb.parkban.utils.MyBounceInterpolator;
import com.khodmohaseb.parkban.utils.PelakUtility;
import com.pax.dal.IDAL;
import com.pax.dal.IPrinter;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

import static com.google.android.gms.internal.zzid.runOnUiThread;

public class   ExitQrViewModel extends ViewModel {

    public static final String TAG = "ExitQrViewModel";
    private MutableLiveData<Boolean> car;
    private MutableLiveData<Boolean> motor;
    private EditText etxt_first_cell_car_plate;
    private EditText etxt_first_cell_motor_plate;
    private Context myContext;
    private CarPlate carPlateActual;
    private MutableLiveData<String> plate__0;
    private MutableLiveData<String> plate__1;
    private MutableLiveData<String> plate__2;
    private MutableLiveData<String> plate__3;
    private MutableLiveData<String> mplate__0;
    private MutableLiveData<String> mplate__1;
    private MutableLiveData<Operator> selectedUser;
    private MutableLiveData<Door> selectedDoor;
    private MutableLiveData<GetParkingInfoResponse> getParkingInfoResponse;
    private String newPhotoFilePath;
    private Uri photoURI;
    private Bitmap plateBitmap;
    private static final String MEDIA_FILE_TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";
    private IDAL idal;
    private BaseAnprProvider anprProvider;
    private Bitmap mBitmap;
    PrinterManager mPrinterManager;
    String pelakString____;
    String tariffIdString____;
    Date enterDateTime;
    Date currentDateTime;
    String enterDoorIDString____;
    String exitDoorIDString____;
    String enterOperatorIDString____;
    String exitOperatorIDString____;
    String paidAmount____;


    public Spinner mSpinner;
    public MutableLiveData<Integer> progress = new MutableLiveData<>();
    private ParkbanRepository parkbanRepository;

    public LiveData<Integer> getProgress() {
        return progress;
    }


    public MutableLiveData<Boolean> getCar() {
        if (car == null)
            car = new MutableLiveData<>();
        return car;
    }

    public MutableLiveData<Boolean> getMotor() {
        if (motor == null)
            motor = new MutableLiveData<>();
        return motor;
    }


    public MutableLiveData<Operator> getSelectedUser() {
        if (selectedUser == null)
            selectedUser = new MutableLiveData<>();
        return selectedUser;
    }

    public MutableLiveData<Door> getSelectedDoor() {
        if (selectedDoor == null)
            selectedDoor = new MutableLiveData<>();
        return selectedDoor;
    }

    public MutableLiveData<GetParkingInfoResponse> getParkingInfoResponse() {
        if (getParkingInfoResponse == null)
            getParkingInfoResponse = new MutableLiveData<>();
        return getParkingInfoResponse;
    }


    public MutableLiveData<String> getPlate__0() {
        if (plate__0 == null)
            plate__0 = new MutableLiveData<>();
        return plate__0;
    }

    public MutableLiveData<String> getPlate__1() {
        if (plate__1 == null)
            plate__1 = new MutableLiveData<>();
        return plate__1;
    }

    public MutableLiveData<String> getPlate__2() {
        if (plate__2 == null)
            plate__2 = new MutableLiveData<>();
        return plate__2;
    }

    public MutableLiveData<String> getPlate__3() {
        if (plate__3 == null)
            plate__3 = new MutableLiveData<>();
        return plate__3;
    }

    public MutableLiveData<String> getMplate__0() {
        if (mplate__0 == null)
            mplate__0 = new MutableLiveData<>();
        return mplate__0;
    }

    public MutableLiveData<String> getMplate__1() {
        if (mplate__1 == null)
            mplate__1 = new MutableLiveData<>();
        return mplate__1;
    }


    public Typeface mFont;
    private boolean doubleBackToExitPressedOnce = false;
    private static final long EXIT_TIMEOUT = 3000;


    /**
     * Execution printing
     * To print data with this class, use the following steps:
     * Obtain an instance of Printer with PrinterManager printer = new PrinterManager().
     * Call setupPage(int, int) to initialize the page size.
     * If necessary, append a line in the current page with drawLine(int , int , int , int , int ).
     * If necessary, append text in the current page with drawTextEx(String , int, int , int , int , String ,int , int , int , int ).
     * If necessary, append barcode data in the current page with drawBarcode(String , int , int , int ,int , int , int ).
     * If necessary, append picture data in the current page with drawBitmap(Bitmap , int , int ).
     * To begin print the current page session, call printPage(int).
     *
     * @param printerManager printerManager
     * @param type           PRINT_TEXT PRINT_BITMAP PRINT_BARCOD PRINT_FORWARD
     * @param content        content
     */
    private void doPrint(PrinterManager printerManager, int type, Object content) {
        int ret = printerManager.getStatus();

        final int finalRet = ret;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updatePrintStatus(finalRet);
            }
        });


        Log.d("xeagle696969", "printer status : " + ret);


        //Get printer status
        if (ret == PRNSTS_OK) {
            printerManager.setupPage(384, -1);   //Set paper size
            switch (type) {
                case PRINT_BITMAP:
                    Bitmap bitmap = (Bitmap) content;
                    if (bitmap != null) {
                        printerManager.drawBitmap(bitmap, 30, 0);  //print pictures
                    } else {


                        Log.d(TAG, "doPrint: error >>> bitmap is null");


                    }
                    break;
            }
            ret = printerManager.printPage(0);  //Execution printing
            printerManager.paperFeed(16);  //paper feed
        }

    }

    private final int PRINT_TEXT = 0;   //Printed text
    private final int PRINT_BITMAP = 1;   //print pictures
    private final int PRINT_BARCOD = 2;   //Print bar code
    private final int PRINT_FORWARD = 3;

    //Printer gray value 0-4
    private final static int DEF_PRINTER_HUE_VALUE = 0;
    private final static int MIN_PRINTER_HUE_VALUE = 0;
    private final static int MAX_PRINTER_HUE_VALUE = 4;

    //Print speed value 0-9
    private final static int DEF_PRINTER_SPEED_VALUE = 9;
    private final static int MIN_PRINTER_SPEED_VALUE = 0;
    private final static int MAX_PRINTER_SPEED_VALUE = 9;

    // Printer status
    private final static int PRNSTS_OK = 0;                //OK
    private final static int PRNSTS_OUT_OF_PAPER = -1;    //Out of paper
    private final static int PRNSTS_OVER_HEAT = -2;        //Over heat
    private final static int PRNSTS_UNDER_VOLTAGE = -3;    //under voltage
    private final static int PRNSTS_BUSY = -4;            //Device is busy
    private final static int PRNSTS_ERR = -256;            //Common error
    private final static int PRNSTS_ERR_DRIVER = -257;    //Printer Driver error

    //*********************************************************************************************
    //*********************************************************************************************
    //*********************************************************************************************//Forward (paper feed)

    private Handler mPrintHandler;

    class CustomThread extends Thread {
        @Override
        public void run() {
            //To create a message loop
            Looper.prepare();   //1.Initialize looper
            mPrintHandler = new Handler() {   //2.Bind handler to looper object of customthread instance
                public void handleMessage(Message msg) {   //3.Define how messages are processed
                    switch (msg.what) {
                        case PRINT_TEXT:
                        case PRINT_BITMAP:
                        case PRINT_BARCOD:
                            doPrint(getPrinterManager(), msg.what, msg.obj);   //Print
                            break;
                        case PRINT_FORWARD:
//                            getPrinterManager().paperFeed(20);
                            getPrinterManager().paperFeed(800);
//                            getPrinterManager().paperFeed(1200);
                            updatePrintStatus(100);
                            break;
                    }
                }
            };
            Looper.loop();   //4.Start message loop
        }
    }

    //Update printer status, toast reminder in case of exception
    private void updatePrintStatus(final int status) {
        if (status == PRNSTS_OUT_OF_PAPER) {

            ShowToast.getInstance().showError(myContext, R.string.tst_info_paper);


        } else if (status == PRNSTS_OVER_HEAT) {

            ShowToast.getInstance().showError(myContext, R.string.tst_info_temperature);


        } else if (status == PRNSTS_UNDER_VOLTAGE) {

            ShowToast.getInstance().showError(myContext, R.string.tst_info_voltage);


        } else if (status == PRNSTS_BUSY) {

            ShowToast.getInstance().showError(myContext, R.string.tst_info_busy);


        } else if (status == PRNSTS_ERR) {

            ShowToast.getInstance().showError(myContext, R.string.tst_info_error);


        } else if (status == PRNSTS_ERR_DRIVER) {

            ShowToast.getInstance().showError(myContext, R.string.tst_info_driver_error);


        }
    }

    public static boolean isNumeric(String string) {
        if (string != null && !string.equals("") && string.matches("\\d*")) {
            if (String.valueOf(Integer.MAX_VALUE).length() < string.length()) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    //Instantiate printermanager
    private PrinterManager getPrinterManager() {
        if (mPrinterManager == null) {
            mPrinterManager = new PrinterManager();
            mPrinterManager.open();
        }
        return mPrinterManager;
    }

    public void init(final Context context, EditText editTextCar, EditText editTextMotor) {
        etxt_first_cell_car_plate = editTextCar;
        etxt_first_cell_motor_plate = editTextMotor;
        myContext = context;
        mFont = Typeface.createFromAsset(myContext.getAssets(), "irsans.ttf");


        new CustomThread().start();


        if (selectedDoor == null)
            selectedDoor = new MutableLiveData<>();
        if (selectedUser == null)
            selectedUser = new MutableLiveData<>();
        if (getParkingInfoResponse == null)
            getParkingInfoResponse = new MutableLiveData<>();

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();

        String json_parkingInfo = preferences.getString("parkinginfo", "");
        getParkingInfoResponse.setValue(gson.fromJson(json_parkingInfo, GetParkingInfoResponse.class));

        String json_selectedUser = preferences.getString("currentuser", "");
        selectedUser.setValue(gson.fromJson(json_selectedUser, Operator.class));

        String json_selectedDoor = preferences.getString("currentdoor", "");
        selectedDoor.setValue(gson.fromJson(json_selectedDoor, Door.class));


        if (car == null)
            car = new MutableLiveData<>();
        car.setValue(true);
        if (motor == null)
            motor = new MutableLiveData<>();
        motor.setValue(false);

        if (plate__0 == null)
            plate__0 = new MutableLiveData<>();
        plate__0.setValue("");
        if (plate__1 == null)
            plate__1 = new MutableLiveData<>();
        plate__1.setValue("");
        if (plate__2 == null)
            plate__2 = new MutableLiveData<>();
        plate__2.setValue("");
        if (plate__3 == null)
            plate__3 = new MutableLiveData<>();
        plate__3.setValue("");
        if (mplate__0 == null)
            mplate__0 = new MutableLiveData<>();
        mplate__0.setValue("");
        if (mplate__1 == null)
            mplate__1 = new MutableLiveData<>();
        mplate__1.setValue("");
        if (anprProvider == null) {
            anprProvider = new FarsiOcrAnprProvider(context);
        }
        carPlateActual = new CarPlate();
        if (parkbanRepository == null) {
            ParkbanDatabase.getInstance(context.getApplicationContext(), new ParkbanDatabase.DatabaseReadyCallback() {
                @Override
                public void onReady(ParkbanDatabase instance) {
                    parkbanRepository = new ParkbanRepository(instance);
                }
            });
        }
    }

    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    public void handelExit(
            final String pelakString,
            String enterDateTimeString,
            final String tariffIdString,
            final String paidAmountString,
            final String enterDoorIDString,
            final String enterOperatorIDString,
            final String paymentTypeString,
            final String electronicPaymentCodeString
    ) throws Exception {
        Log.d(TAG, "handelExit in QR Mode");
        Log.d(TAG, "pelak >" + pelakString);
        Log.d(TAG, "enterDateTime >" + enterDateTimeString);
        Log.d(TAG, "tariffId >" + tariffIdString);
        Log.d(TAG, "paidAmount >" + paidAmountString);
        Log.d(TAG, "enterDoorID >" + enterDoorIDString);
        Log.d(TAG, "enterOperatorID >" + enterOperatorIDString);
        Log.d(TAG, "paymentType >" + paymentTypeString);
        Log.d(TAG, "electronicPaymentCode >" + electronicPaymentCodeString);


        pelakString____ = pelakString;
        tariffIdString____ = tariffIdString;
        enterDoorIDString____ = enterDoorIDString;
        enterOperatorIDString____ = enterOperatorIDString;


        long price = 0;
        long roundedPrice = 0;

        if (Long.parseLong(paidAmountString) == 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
            enterDateTime = dateFormat.parse(enterDateTimeString);
            currentDateTime = new Date();
            long totalStayInMinutes;
            long diffInMillisec = currentDateTime.getTime() - enterDateTime.getTime();
            totalStayInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillisec);
            Log.d(TAG, "total stay in minutes : " + totalStayInMinutes);
            if (totalStayInMinutes <= getParkingInfoResponse.getValue().getTariffs().getFreeDuration()) {
                //  should only show dialog and no need to pay  it is free


                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(myContext);
                LayoutInflater inflater = ((Activity) myContext).getLayoutInflater();
                View alertView = inflater.inflate(R.layout.dialog_no_need_pay, null);
                alertDialog.setView(alertView);
                alertDialog.setCancelable(false);
                final AlertDialog show = alertDialog.show();
                TextView txtPlate = alertView.findViewById(R.id.dialog_no_need_pay_1);
                if (pelakString.length() == 8) {
                    txtPlate.setText(pelakString.substring(0, 3) + " - " + pelakString.substring(3, 8));
                } else {


                    txtPlate.setText(

                            pelakString.substring(4, 9)


                                    +
                                    " - " +
                                    PelakUtility.convertFromCode(pelakString.substring(2, 4)) +
                                    " - " +

                                    pelakString.substring(0, 2)


                    );
                }


                TextView txtEnterDateTime = alertView.findViewById(R.id.dialog_no_need_pay_2);
                TextView txtPaidCost = alertView.findViewById(R.id.dialog_no_need_pay_6);
                String lan = PreferenceManager.getDefaultSharedPreferences(myContext).getString("language", "fa");

                if (lan.equals("fa")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
                    Date dateNow = sdf.parse(enterDateTimeString);
                    PersianDate persianDate = new PersianDate(dateNow);
                    PersianDateFormat pdformater1 = new PersianDateFormat("Y/m/d");
                    SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String jalaliDate = pdformater1.format(persianDate) + "  " + sdf1.format(dateNow);
                    txtEnterDateTime.setText(jalaliDate);
                    txtPaidCost.setText("رایگان");


                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
                    Date dateNow = sdf.parse(enterDateTimeString);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.getDefault());
                    txtEnterDateTime.setText(sdf1.format(dateNow));
                    txtPaidCost.setText("Free");

                }

                TextView txtTariffId = alertView.findViewById(R.id.dialog_no_need_pay_3);
                txtTariffId.setText(getTariffNameById(tariffIdString));
                TextView txtEnterDoor = alertView.findViewById(R.id.dialog_no_need_pay_4);
                txtEnterDoor.setText(enterDoorIDString);
                TextView txtEnterOperator = alertView.findViewById(R.id.dialog_no_need_pay_5);
                txtEnterOperator.setText(enterOperatorIDString);
                LinearLayout cancelButton = alertView.findViewById(R.id.dialog_no_need_pay_7);
                LinearLayout exitButton = alertView.findViewById(R.id.dialog_no_need_pay_8);


                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show.dismiss();
                    }
                });


                exitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Log.d(TAG, "save in exit_table then  print recepit and paid by cash");

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault());
                        final String exitDateTimeStringForDataBase = sdf.format(currentDateTime);
                        final String enterDateTimeStringForDataBase = sdf.format(enterDateTime);
                        final int tarrifId = Integer.parseInt(tariffIdString);


                        parkbanRepository.saveExitRecord(
                                getParkingInfoResponse.getValue().getDeviceId(),
                                pelakString,
                                exitDateTimeStringForDataBase,
                                tarrifId,
                                0,
                                0,
                                "",
                                selectedDoor.getValue().getId().longValue(),
                                selectedUser.getValue().getId().longValue(),
                                0,
                                new ParkbanRepository.DataBaseResultCallBack() {
                                    @Override
                                    public void onSuccess(long id) {

                                        parkbanRepository.saveTraffikRecord(
                                                getParkingInfoResponse.getValue().getDeviceId(),
                                                pelakString,
                                                enterDateTimeStringForDataBase,
                                                exitDateTimeStringForDataBase,
                                                tarrifId,
                                                0,
                                                0,
                                                "",
                                                Long.parseLong(enterDoorIDString),
                                                selectedDoor.getValue().getId().longValue(),
                                                Long.parseLong(enterOperatorIDString),
                                                selectedUser.getValue().getId().longValue(),
                                                getTariffNameById(tariffIdString), new ParkbanRepository.DataBaseResultCallBack() {
                                                    @Override
                                                    public void onSuccess(long id) {


                                                        parkbanRepository.setExitEntranceRecord(pelakString, new ParkbanRepository.DataBaseResultCallBack() {
                                                            @Override
                                                            public void onSuccess(long id) {
                                                                ShowToast.getInstance().showSuccess(myContext, R.string.submit_success);
                                                                show.dismiss();
                                                            }

                                                            @Override
                                                            public void onFailed() {
                                                                ShowToast.getInstance().showError(myContext, R.string.error_in_save_data_base);
                                                                show.dismiss();
                                                            }
                                                        });


                                                    }

                                                    @Override
                                                    public void onFailed() {
                                                        ShowToast.getInstance().showError(myContext, R.string.error_in_save_data_base);
                                                        show.dismiss();
                                                    }
                                                }


                                        );


                                    }

                                    @Override
                                    public void onFailed() {
                                        ShowToast.getInstance().showError(myContext, R.string.error_in_save_data_base);
                                        show.dismiss();
                                    }
                                }


                        );


                    }
                });


            } else {
                //should call calculate fare
                long roundedTotalStayInMinutes = CalculateHelperUtility.roundStayLengthTime(totalStayInMinutes, getParkingInfoResponse.getValue().getTariffs());
                Log.d(TAG, "rounded stay in minutes : " + roundedTotalStayInMinutes);
                if (getParkingInfoResponse.getValue().getTariffs().getIsCircadian()) {
                    // should calculate daily or daily-hourly
                    if (getParkingInfoResponse.getValue().getTariffs().getCircadianCalcKind() == 0) {
                        //daily
                        switch (tariffIdString.trim()) {
                            case "1":
                                price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff1().getEntranceCost() +
                                        DailyFareCalculator.calculateDailyFareVehicle1(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getValue().getTariffs()
                                        );

                                break;
                            case "2":
                                price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff2().getEntranceCost() +
                                        DailyFareCalculator.calculateDailyFareVehicle2(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getValue().getTariffs()
                                        );
                                break;
                            case "3":
                                price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff3().getEntranceCost() +
                                        DailyFareCalculator.calculateDailyFareVehicle3(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getValue().getTariffs()
                                        );
                                break;
                            case "4":
                                price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff4().getEntranceCost() +
                                        DailyFareCalculator.calculateDailyFareVehicle4(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getValue().getTariffs()
                                        );
                                break;
                            case "5":
                                price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff5().getEntranceCost() +
                                        DailyFareCalculator.calculateDailyFareVehicle5(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getValue().getTariffs()
                                        );
                                break;
                            case "6":
                                price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff6().getEntranceCost() +
                                        DailyFareCalculator.calculateDailyFareVehicle6(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getValue().getTariffs()
                                        );
                                break;
                            case "7":
                                price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff7().getEntranceCost() +
                                        DailyFareCalculator.calculateDailyFareVehicle7(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getValue().getTariffs()
                                        );
                                break;
                            case "8":
                                price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff8().getEntranceCost() +
                                        DailyFareCalculator.calculateDailyFareVehicle8(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getValue().getTariffs()
                                        );
                                break;
                        }
                    } else {
                        //daily hourly
                        switch (tariffIdString.trim()) {
                            case "1":
                                price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff1().getEntranceCost() +
                                        DailyHourlyFareCalculator.calculateHourlyDailyFareVehicle1(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getValue().getTariffs()
                                        );
                                break;
                            case "2":
                                price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff2().getEntranceCost() +
                                        DailyHourlyFareCalculator.calculateHourlyDailyFareVehicle2(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getValue().getTariffs()
                                        );
                                break;
                            case "3":
                                price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff3().getEntranceCost() +
                                        DailyHourlyFareCalculator.calculateHourlyDailyFareVehicle3(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getValue().getTariffs()
                                        );
                                break;
                            case "4":
                                price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff4().getEntranceCost() +
                                        DailyHourlyFareCalculator.calculateHourlyDailyFareVehicle4(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getValue().getTariffs()
                                        );
                                break;
                            case "5":
                                price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff5().getEntranceCost() +
                                        DailyHourlyFareCalculator.calculateHourlyDailyFareVehicle5(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getValue().getTariffs()
                                        );
                                break;
                            case "6":
                                price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff6().getEntranceCost() +
                                        DailyHourlyFareCalculator.calculateHourlyDailyFareVehicle6(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getValue().getTariffs()
                                        );
                                break;
                            case "7":
                                price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff7().getEntranceCost() +
                                        DailyHourlyFareCalculator.calculateHourlyDailyFareVehicle7(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getValue().getTariffs()
                                        );
                                break;
                            case "8":
                                price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff8().getEntranceCost() +
                                        DailyHourlyFareCalculator.calculateHourlyDailyFareVehicle8(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getValue().getTariffs()
                                        );
                                break;
                        }
                    }


                } else {
                    // should calculate hourly
                    switch (tariffIdString.trim()) {
                        case "1":
                            price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff1().getEntranceCost() +
                                    HourlyFareCalculator.calculateStepFareVehicle1(
                                            roundedTotalStayInMinutes,
                                            getParkingInfoResponse.getValue().getTariffs()
                                    );
                            break;
                        case "2":
                            price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff2().getEntranceCost() +
                                    HourlyFareCalculator.calculateStepFareVehicle2(
                                            roundedTotalStayInMinutes,
                                            getParkingInfoResponse.getValue().getTariffs()
                                    );
                            break;
                        case "3":
                            price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff3().getEntranceCost() +
                                    HourlyFareCalculator.calculateStepFareVehicle3(
                                            roundedTotalStayInMinutes,
                                            getParkingInfoResponse.getValue().getTariffs()
                                    );
                            break;
                        case "4":
                            price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff4().getEntranceCost() +
                                    HourlyFareCalculator.calculateStepFareVehicle4(
                                            roundedTotalStayInMinutes,
                                            getParkingInfoResponse.getValue().getTariffs()
                                    );
                            break;
                        case "5":
                            price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff5().getEntranceCost() +
                                    HourlyFareCalculator.calculateStepFareVehicle5(
                                            roundedTotalStayInMinutes,
                                            getParkingInfoResponse.getValue().getTariffs()
                                    );
                            break;
                        case "6":
                            price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff6().getEntranceCost() +
                                    HourlyFareCalculator.calculateStepFareVehicle6(
                                            roundedTotalStayInMinutes,
                                            getParkingInfoResponse.getValue().getTariffs()
                                    );
                            break;
                        case "7":
                            price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff7().getEntranceCost() +
                                    HourlyFareCalculator.calculateStepFareVehicle7(
                                            roundedTotalStayInMinutes,
                                            getParkingInfoResponse.getValue().getTariffs()
                                    );
                            break;
                        case "8":
                            price = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff8().getEntranceCost() +
                                    HourlyFareCalculator.calculateStepFareVehicle8(
                                            roundedTotalStayInMinutes,
                                            getParkingInfoResponse.getValue().getTariffs()
                                    );
                            break;

                    }
                }
                roundedPrice = CalculateHelperUtility.roundFarePrice(price, getParkingInfoResponse.getValue().getTariffs());
                paidAmount____ = Long.toString(roundedPrice);


                //should show dialog and need to pay

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(myContext);
                LayoutInflater inflater = ((Activity) myContext).getLayoutInflater();
                View alertView = inflater.inflate(R.layout.dialog_need_pay, null);
                alertDialog.setView(alertView);
                alertDialog.setCancelable(false);
                final AlertDialog show = alertDialog.show();
                TextView txtPlate = alertView.findViewById(R.id.dialog_need_pay_1);
                if (pelakString.length() == 8) {
                    txtPlate.setText(pelakString.substring(0, 3) + " - " + pelakString.substring(3, 8));
                } else {
                    txtPlate.setText(
                            pelakString.substring(4, 9)


                                    + " - " +


                                    PelakUtility.convertFromCode(pelakString.substring(2, 4)) +

                                    " - " +


                                    pelakString.substring(0, 2)


                    );
                }


                TextView txtEnterDateTime = alertView.findViewById(R.id.dialog_need_pay_2);
                TextView txtShouldPayCost = alertView.findViewById(R.id.dialog_need_pay_6);
                String lan = PreferenceManager.getDefaultSharedPreferences(myContext).getString("language", "fa");

                if (lan.equals("fa")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
                    Date dateNow = sdf.parse(enterDateTimeString);
                    PersianDate persianDate = new PersianDate(dateNow);
                    PersianDateFormat pdformater1 = new PersianDateFormat("Y/m/d");
                    SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String jalaliDate = pdformater1.format(persianDate) + "  " + sdf1.format(dateNow);
                    txtEnterDateTime.setText(jalaliDate);
                    txtShouldPayCost.setText(roundedPrice + " ریال");


                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
                    Date dateNow = sdf.parse(enterDateTimeString);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.getDefault());
                    txtEnterDateTime.setText(sdf1.format(dateNow));
                    txtShouldPayCost.setText(roundedPrice + " Rials");

                }

                TextView txtTariffId = alertView.findViewById(R.id.dialog_need_pay_3);
                txtTariffId.setText(getTariffNameById(tariffIdString));
                TextView txtEnterDoor = alertView.findViewById(R.id.dialog_need_pay_4);
                txtEnterDoor.setText(enterDoorIDString);
                TextView txtEnterOperator = alertView.findViewById(R.id.dialog_need_pay_5);
                txtEnterOperator.setText(enterOperatorIDString);
                LinearLayout cancelButton = alertView.findViewById(R.id.dialog_need_pay_7);
                LinearLayout cashButton = alertView.findViewById(R.id.dialog_need_pay_8);
                LinearLayout electronicButton = alertView.findViewById(R.id.dialog_need_pay_9);

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show.dismiss();
                    }
                });

                final long finalRoundedPrice = roundedPrice;
                cashButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Log.d(TAG, "save in exit_table then  print recepit and paid by cash");

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault());
                        final String exitDateTimeStringForDataBase = sdf.format(currentDateTime);
                        final String enterDateTimeStringForDataBase = sdf.format(enterDateTime);

                        final int tarrifId = Integer.parseInt(tariffIdString);


                        parkbanRepository.saveExitRecord(
                                getParkingInfoResponse.getValue().getDeviceId(),
                                pelakString,
                                exitDateTimeStringForDataBase,
                                tarrifId,
                                finalRoundedPrice,
                                0,
                                "",
                                selectedDoor.getValue().getId().longValue(),
                                selectedUser.getValue().getId().longValue(),
                                0,
                                new ParkbanRepository.DataBaseResultCallBack() {
                                    @Override
                                    public void onSuccess(long id) {

                                        parkbanRepository.saveTraffikRecord(
                                                getParkingInfoResponse.getValue().getDeviceId(),
                                                pelakString,
                                                enterDateTimeStringForDataBase,
                                                exitDateTimeStringForDataBase,
                                                tarrifId,
                                                finalRoundedPrice,
                                                0,
                                                "",
                                                Long.parseLong(enterDoorIDString),
                                                selectedDoor.getValue().getId().longValue(),
                                                Long.parseLong(enterOperatorIDString),
                                                selectedUser.getValue().getId().longValue(),
                                                getTariffNameById(tariffIdString), new ParkbanRepository.DataBaseResultCallBack() {
                                                    @Override
                                                    public void onSuccess(long id) {


                                                        parkbanRepository.setExitEntranceRecord(pelakString, new ParkbanRepository.DataBaseResultCallBack() {
                                                            @Override
                                                            public void onSuccess(long id) {


                                                                Log.d(TAG, "onSuccess in save database , now print process begin");

                                                                try {
                                                                    mBitmap = generateBitmapByLayoutForPayment(myContext, enterDateTimeStringForDataBase, exitDateTimeStringForDataBase, getTariffNameById(tariffIdString), Long.toString(finalRoundedPrice), pelakString);
                                                                    Message msg = mPrintHandler.obtainMessage(PRINT_BITMAP);
                                                                    msg.obj = mBitmap;
                                                                    msg.sendToTarget();
                                                                    mPrintHandler.obtainMessage(PRINT_FORWARD).sendToTarget();
                                                                    show.dismiss();

                                                                } catch (Exception exception) {

                                                                }


                                                            }

                                                            @Override
                                                            public void onFailed() {
                                                                ShowToast.getInstance().showError(myContext, R.string.error_in_save_data_base);
                                                                show.dismiss();
                                                            }
                                                        });


                                                    }

                                                    @Override
                                                    public void onFailed() {
                                                        ShowToast.getInstance().showError(myContext, R.string.error_in_save_data_base);
                                                        show.dismiss();
                                                    }
                                                }


                                        );


                                    }

                                    @Override
                                    public void onFailed() {
                                        ShowToast.getInstance().showError(myContext, R.string.error_in_save_data_base);
                                        show.dismiss();
                                    }
                                }


                        );


                    }
                });

                electronicButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "do payment then save in database");
                        show.dismiss();
                        Intent fanavaPaymentIntent = new Intent("ir.totan.pos.view.cart.TXN");
                        fanavaPaymentIntent.putExtra("type", 3);
                        fanavaPaymentIntent.putExtra("amount", Long.toString(finalRoundedPrice));
                        fanavaPaymentIntent.putExtra("res_num", Long.parseLong(pelakString));
                        ((Activity) myContext).startActivityForResult(fanavaPaymentIntent, 103);


                    }
                });


            }


        } else {
            // should only show dialog and no need to pay he paid first at entrance
            currentDateTime = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
            enterDateTime= sdf.parse(enterDateTimeString);

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(myContext);
            LayoutInflater inflater = ((Activity) myContext).getLayoutInflater();
            View alertView = inflater.inflate(R.layout.dialog_no_need_pay, null);
            alertDialog.setView(alertView);
            alertDialog.setCancelable(false);
            final AlertDialog show = alertDialog.show();
            TextView txtPlate = alertView.findViewById(R.id.dialog_no_need_pay_1);
            if (pelakString.length() == 8) {
                txtPlate.setText(pelakString.substring(0, 3) + " - " + pelakString.substring(3, 8));
            } else {


                txtPlate.setText(

                        pelakString.substring(4, 9)


                                +
                                " - " +
                                PelakUtility.convertFromCode(pelakString.substring(2, 4)) +
                                " - " +

                                pelakString.substring(0, 2)


                );
            }


            TextView txtEnterDateTime = alertView.findViewById(R.id.dialog_no_need_pay_2);
            TextView txtPaidCost = alertView.findViewById(R.id.dialog_no_need_pay_6);
            String lan = PreferenceManager.getDefaultSharedPreferences(myContext).getString("language", "fa");

            if (lan.equals("fa")) {
                Date dateNow = sdf.parse(enterDateTimeString);
                PersianDate persianDate = new PersianDate(dateNow);
                PersianDateFormat pdformater1 = new PersianDateFormat("Y/m/d");
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String jalaliDate = pdformater1.format(persianDate) + "  " + sdf1.format(dateNow);
                txtEnterDateTime.setText(jalaliDate);
                txtPaidCost.setText(paidAmountString + "  ریال");


            } else {
                Date dateNow = sdf.parse(enterDateTimeString);
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.getDefault());
                txtEnterDateTime.setText(sdf1.format(dateNow));
                txtPaidCost.setText(paidAmountString + " Rials");

            }

            TextView txtTariffId = alertView.findViewById(R.id.dialog_no_need_pay_3);
            txtTariffId.setText(getTariffNameById(tariffIdString));
            TextView txtEnterDoor = alertView.findViewById(R.id.dialog_no_need_pay_4);
            txtEnterDoor.setText(enterDoorIDString);
            TextView txtEnterOperator = alertView.findViewById(R.id.dialog_no_need_pay_5);
            txtEnterOperator.setText(enterOperatorIDString);
            LinearLayout cancelButton = alertView.findViewById(R.id.dialog_no_need_pay_7);
            LinearLayout exitButton = alertView.findViewById(R.id.dialog_no_need_pay_8);


            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show.dismiss();
                }
            });


            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Log.d(TAG, "save in exit_table then  print recepit and paid by cash");

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault());
                    final String exitDateTimeStringForDataBase = sdf.format(currentDateTime);
                    final String enterDateTimeStringForDataBase = sdf.format(enterDateTime);
                    final int tarrifId = Integer.parseInt(tariffIdString);


                    parkbanRepository.saveExitRecord(
                            getParkingInfoResponse.getValue().getDeviceId(),
                            pelakString,
                            exitDateTimeStringForDataBase,
                            tarrifId,
                            Long.parseLong(paidAmountString.trim()),
                            Integer.parseInt(paymentTypeString.trim()),
                            electronicPaymentCodeString,
                            selectedDoor.getValue().getId().longValue(),
                            selectedUser.getValue().getId().longValue(),
                            0,
                            new ParkbanRepository.DataBaseResultCallBack() {
                                @Override
                                public void onSuccess(long id) {

                                    parkbanRepository.saveTraffikRecord(
                                            getParkingInfoResponse.getValue().getDeviceId(),
                                            pelakString,
                                            enterDateTimeStringForDataBase,
                                            exitDateTimeStringForDataBase,
                                            tarrifId,
                                            Long.parseLong(paidAmountString.trim()),
                                            Integer.parseInt(paymentTypeString.trim()),
                                            electronicPaymentCodeString,
                                            Long.parseLong(enterDoorIDString),
                                            selectedDoor.getValue().getId().longValue(),
                                            Long.parseLong(enterOperatorIDString),
                                            selectedUser.getValue().getId().longValue(),
                                            getTariffNameById(tariffIdString), new ParkbanRepository.DataBaseResultCallBack() {
                                                @Override
                                                public void onSuccess(long id) {

                                                    parkbanRepository.setExitEntranceRecord(pelakString, new ParkbanRepository.DataBaseResultCallBack() {
                                                        @Override
                                                        public void onSuccess(long id) {
                                                            ShowToast.getInstance().showSuccess(myContext, R.string.submit_success);
                                                            show.dismiss();
                                                        }

                                                        @Override
                                                        public void onFailed() {
                                                            ShowToast.getInstance().showError(myContext, R.string.error_in_save_data_base);
                                                            show.dismiss();
                                                        }
                                                    });


                                                }

                                                @Override
                                                public void onFailed() {
                                                    ShowToast.getInstance().showError(myContext, R.string.error_in_save_data_base);
                                                    show.dismiss();
                                                }
                                            }


                                    );


                                }

                                @Override
                                public void onFailed() {
                                    ShowToast.getInstance().showError(myContext, R.string.error_in_save_data_base);
                                    show.dismiss();
                                }
                            }


                    );


                }
            });


        }


    }


    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


    //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    public void Pelak_Khan_Onclick(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_bubble_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //*****************************************************************
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(myContext.getPackageManager()) != null) {
                    Log.d("hadiGhadiri", "1");
                    File photoFile = null;
                    try {
                        photoFile = createPrivateImageFile(myContext);
                        newPhotoFilePath = photoFile.getAbsolutePath();
                        //set file name in model for save to database
                        carPlateActual.setPlateFileName(photoFile.getName());
                        Log.d("hadiGhadiri", "2");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("xxxxhhhhhh", "error >>> " + e.getMessage());
                    }
                    if (photoFile != null) {
                        Log.d("hadiGhadiri", "3");
                        try {
                            //for Android 4.4-
//                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
//                        photoURI = Uri.fromFile(photoFile);
//                    } else
                            photoURI = FileProvider.getUriForFile(myContext, "com.khodmohaseb.parkban", photoFile);
                            List<ResolveInfo> resolvedIntentActivities = myContext.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                            for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                                String packageName = resolvedIntentInfo.activityInfo.packageName;
                                myContext.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }
                            //takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            Log.d("hadiGhadiri", "4");
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            ((Activity) myContext).startActivityForResult(takePictureIntent, 1369);
                        } catch (Exception e) {
                            Log.d("hadiGhadiri", "5");
                        }
                    }
                }
            }
        }, Animation_Constant.ANIMATION_VALUE);
    }

    public File createPrivateImageFile(Context context) throws IOException {
        String timeStamp = DateTimeHelper.DateToStr(new Date(), MEDIA_FILE_TIMESTAMP_FORMAT);
        String imageFileName = "PLATE_" + timeStamp;
        carPlateActual.setPlateFileName(imageFileName);
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private String getBundleString(Bundle b) {
        return
                "Status   :" + b.getString("result") + '\n' +
                        "rrn    :" + b.getString("rrn", null) + '\n' +
                        "date    :" + b.getLong("date", -1) + '\n' +
                        "trace    :" + b.getString("trace", null) + '\n' +
                        "pan    :" + b.getString("pan", null) + '\n' +
                        "amount   :" + b.getString("amount", null) + '\n' +
                        "res_num   :" + b.getLong("res_num", -1) + '\n' +
                        "charge_serial    :" + b.getString("charge_serial", null) + '\n' +
                        "charge_pin    :" + b.getString("charge_pin", null) + '\n' +
                        "message    :" + b.getString("message", null) + '\n';
    }

    public void processActivityResult(Context context, int requestCode, int resultCode, Intent
            data) {


        if (requestCode == 103) {


            final Bundle b = data.getBundleExtra("response");
            Log.d(TAG, "onActivityResult:  >>>>>>>>>>>>>>> " + getBundleString(b));
            if (b.getString("result").trim().equals("succeed")) {
                Log.d("e_pardakht >>>", "step 2 >>> GOOD_PAYMENT ");

                Log.d(TAG, "save in exit_table then delete from entrance print recepit and paid by cash");

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault());
                final String exitDateTimeStringForDataBase = sdf.format(currentDateTime);
                final String enterDateTimeStringForDataBase = sdf.format(enterDateTime);

                final int tarrifId = Integer.parseInt(tariffIdString____);


                parkbanRepository.saveExitRecord(
                        getParkingInfoResponse.getValue().getDeviceId(),
                        pelakString____,
                        exitDateTimeStringForDataBase,
                        tarrifId,
                        Long.parseLong(paidAmount____),
                        1,
                        b.getString("rrn"),
                        selectedDoor.getValue().getId().longValue(),
                        selectedUser.getValue().getId().longValue(),
                        0,
                        new ParkbanRepository.DataBaseResultCallBack() {
                            @Override
                            public void onSuccess(long id) {

                                parkbanRepository.saveTraffikRecord(
                                        getParkingInfoResponse.getValue().getDeviceId(),
                                        pelakString____,
                                        enterDateTimeStringForDataBase,
                                        exitDateTimeStringForDataBase,
                                        tarrifId,
                                        Long.parseLong(paidAmount____),
                                        1,
                                        b.getString("rrn"),
                                        Long.parseLong(enterDoorIDString____),
                                        selectedDoor.getValue().getId().longValue(),
                                        Long.parseLong(enterOperatorIDString____),
                                        selectedUser.getValue().getId().longValue(),
                                        getTariffNameById(tariffIdString____), new ParkbanRepository.DataBaseResultCallBack() {
                                            @Override
                                            public void onSuccess(long id) {


                                                parkbanRepository.setExitEntranceRecord(pelakString____, new ParkbanRepository.DataBaseResultCallBack() {
                                                    @Override
                                                    public void onSuccess(long id) {


                                                        Log.d(TAG, "onSuccess in save database , now print process begin");

                                                        try {
                                                            mBitmap = generateBitmapByLayoutForPayment(myContext, enterDateTimeStringForDataBase, exitDateTimeStringForDataBase, getTariffNameById(tariffIdString____), paidAmount____, pelakString____);
                                                            Message msg = mPrintHandler.obtainMessage(PRINT_BITMAP);
                                                            msg.obj = mBitmap;
                                                            msg.sendToTarget();
                                                            mPrintHandler.obtainMessage(PRINT_FORWARD).sendToTarget();


                                                        } catch (Exception exception) {

                                                        }


                                                    }

                                                    @Override
                                                    public void onFailed() {
                                                        ShowToast.getInstance().showError(myContext, R.string.error_in_save_data_base);

                                                    }
                                                });


                                            }

                                            @Override
                                            public void onFailed() {
                                                ShowToast.getInstance().showError(myContext, R.string.error_in_save_data_base);

                                            }
                                        }


                                );


                            }

                            @Override
                            public void onFailed() {
                                ShowToast.getInstance().showError(myContext, R.string.error_in_save_data_base);

                            }
                        }


                );

            } else {


                ShowToast.getInstance().showError(myContext, R.string.failed_payment);
            }


        } else {
            if (requestCode == 1369 && resultCode == Activity.RESULT_OK) {
                Log.d("xxxhhhhhhhhhhhh", "" + newPhotoFilePath);
                plateBitmap = ImageLoadHelper.getInstance().loadImage(context, newPhotoFilePath);
                //*****************************************************************************************************
                try {
                    idal = PrintParkbanApp.getInstance().getIdal();
                    if (idal == null) {
                        try {
                            ExifInterface exif = new ExifInterface(newPhotoFilePath);
                            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                            Log.d("xeagle69", "camera XXX>>> rotation in int >>> " + rotation);
                            if (rotation == 1) {
                            } else {
                                Matrix matrix = new Matrix();
                                matrix.setRotate(0, 0, 0);
                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(plateBitmap, plateBitmap.getWidth(), plateBitmap.getHeight(), true);
                                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                                plateBitmap = rotatedBitmap;
                            }
                        } catch (Exception e) {
                        }
                    } else {
                        try {
                            ExifInterface exif = new ExifInterface(newPhotoFilePath);
                            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                            Log.d("xeagle69", "camera XXX>>> rotation in int >>> " + rotation);
                            if (rotation == 0) {
                            } else {
                                Matrix matrix = new Matrix();
                                matrix.setRotate(-90, 0, 0);
                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(plateBitmap, plateBitmap.getWidth(), plateBitmap.getHeight(), true);
                                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                                plateBitmap = rotatedBitmap;
                            }
                        } catch (Exception e) {
//
                        }
                    }
                } catch (Exception e) {
                }
                //*****************************************************************************************************
                carPlateActual.setPlateImage(plateBitmap);
                doDetectPlate(context, plateBitmap);
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        }


    }

    private void doDetectPlate(final Context context, Bitmap plateImage) {
        anprProvider.getPlate(context, plateImage, new onPlateDetectedCallback() {
            @Override
            public void onPlateDetected(PlateDetectionState state, RidingType ridingType, String plateNo, Bitmap plateImage, String part0, String part1, String part2, String part3) {
                Log.d("xxxxhhhhhh", "part0 >>" + part0);
                Log.d("xxxxhhhhhh", "part1 >>" + part1);
                Log.d("xxxxhhhhhh", "part2 >>" + part2);
                Log.d("xxxxhhhhhh", "part3 >>" + part3);
                Log.d("xxxxhhhhhh", "state >>" + state.toString());
                Log.d("xxxxhhhhhh", "type >>" + ridingType.toString());
                if (state == PlateDetectionState.DETECTED) {
                    if (ridingType == RidingType.CAR) {
                        motor.setValue(false);
                        car.setValue(true);
                        plate__0.setValue(part0);
                        String[] alphabetArray = context.getResources().getStringArray(R.array.image_array);
                        for (int i = 1; i < alphabetArray.length; i++) {
                            if (alphabetArray[i].equals(part1))
                                mSpinner.setSelection(i);
                        }
                        plate__2.setValue(part2);
//                        plate__3.setValue(part3);
                        getPlate__3().setValue(part3);
                    } else {
                        motor.setValue(true);
                        car.setValue(false);
                        Log.d("xxxxhhhhhh", "Motor section");
                        if (plateNo != null) {
                            mplate__0.setValue(plateNo.substring(0, 3));
                            mplate__1.setValue(plateNo.substring(3, plateNo.length()));
                        }
                    }
                } else {
                    ShowToast.getInstance().showError(myContext, R.string.plate_not_detected);
                }
            }
        });
    }

    //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    public void Barcod_Khan_Onclick(View view) {
        view.clearFocus();
        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_bubble_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(myContext, QRcodeReaderActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                myContext.startActivity(i);
            }
        }, Animation_Constant.ANIMATION_VALUE);
    }









    public void Motor_Car_Onclick(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_rotate_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        myAnim.setRepeatMode(1);
        view.startAnimation(myAnim);
        if (car.getValue()) {
            getCar().setValue(false);
            getMotor().setValue(true);
            etxt_first_cell_motor_plate.requestFocus();
            ImageView imageView = (ImageView) view;
            imageView.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.motor_256));
        } else {
            getCar().setValue(true);
            getMotor().setValue(false);
            etxt_first_cell_car_plate.requestFocus();
            ImageView imageView = (ImageView) view;
            imageView.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.car_256));
        }
    }

    public void Enter_Manual_Onclick(final View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_bubble_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                //************************************************************************************************************************
                // ************************************************************************************************************************
                boolean is_pelak_valid = true;
                if (getCar().getValue()) {
                    //test car plate
                    if (getPlate__0().getValue().trim().equals("") || getPlate__0().getValue().equals(null))
                        is_pelak_valid = false;
                    if (getPlate__2().getValue().trim().equals("") || getPlate__2().getValue().equals(null))
                        is_pelak_valid = false;
                    if (getPlate__3().getValue().trim().equals("") || getPlate__3().getValue().equals(null))
                        is_pelak_valid = false;

                    if (getPlate__0().getValue().trim().length() != 2)
                        is_pelak_valid = false;
                    if (getPlate__2().getValue().trim().length() != 3)
                        is_pelak_valid = false;
                    if (getPlate__3().getValue().trim().length() != 2)
                        is_pelak_valid = false;


                } else {
                    //test motor plate
                    if (getMplate__0().getValue().trim().equals("") || getMplate__0().getValue().equals(null))
                        is_pelak_valid = false;
                    if (getMplate__1().getValue().trim().equals("") || getMplate__1().getValue().equals(null))
                        is_pelak_valid = false;
                    if (getMplate__0().getValue().trim().length() != 3)
                        is_pelak_valid = false;
                    if (getMplate__1().getValue().trim().length() != 5)
                        is_pelak_valid = false;
                }

                Log.d(TAG, "pelak status >>> " + is_pelak_valid);

                if (!is_pelak_valid) {
                    ShowToast.getInstance().showWarning(view.getContext(), R.string.enter_car_tag);
                    return;
                }
                String pelak = "";
                if (getCar().getValue()) {
                    if (is_pelak_valid) {
                        pelak = getPlate__0().getValue() + PelakUtility.convertToCode(getPlate__1().getValue()) + getPlate__2().getValue() + getPlate__3().getValue();
                    }
                } else {
                    if (is_pelak_valid) {
                        pelak = getMplate__0().getValue() + getMplate__1().getValue();
                    }
                }
                //converting persian numbers to english
                pelak = FontHelper.removeEnter(FontHelper.convertArabicToPersian(pelak));
                Log.d(TAG, "UnChanged-Default pelak >>>  " + pelak);
                StringBuilder newPelak = new StringBuilder(pelak);
                for (int i = 0; i < pelak.length(); i++) {
                    if (Character.isDigit(pelak.charAt(i))) {
                        switch (pelak.charAt(i)) {
                            case '۰':
                                newPelak.setCharAt(i, '0');
                                break;
                            case '۱':
                                newPelak.setCharAt(i, '1');
                                break;
                            case '۲':
                                newPelak.setCharAt(i, '2');
                                break;
                            case '۳':
                                newPelak.setCharAt(i, '3');
                                break;
                            case '۴':
                                newPelak.setCharAt(i, '4');
                                break;
                            case '۵':
                                newPelak.setCharAt(i, '5');
                                break;
                            case '۶':
                                newPelak.setCharAt(i, '6');
                                break;
                            case '۷':
                                newPelak.setCharAt(i, '7');
                                break;
                            case '۸':
                                newPelak.setCharAt(i, '8');
                                break;
                            case '۹':
                                newPelak.setCharAt(i, '9');
                                break;
                        }
                    }
                }
                pelak = newPelak.toString();
                Log.d(TAG, "Changed-New pelak >>>  " + pelak);
                //************************************************************************************************************************
                // ************************************************************************************************************************
                final String finalPelak = pelak;


                Intent intent = new Intent(myContext, EnterManullyActivity.class);
                intent.putExtra("sendedpelak", finalPelak);
                myContext.startActivity(intent);


            }
        }, Animation_Constant.ANIMATION_VALUE);
    }

    public void Search_Memory_Onclick(final View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_bubble_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                //************************************************************************************************************************
                // ************************************************************************************************************************
                boolean is_pelak_valid = true;
                if (getCar().getValue()) {
                    //test car plate
                    if (getPlate__0().getValue().trim().equals("") || getPlate__0().getValue().equals(null))
                        is_pelak_valid = false;
                    if (getPlate__2().getValue().trim().equals("") || getPlate__2().getValue().equals(null))
                        is_pelak_valid = false;
                    if (getPlate__3().getValue().trim().equals("") || getPlate__3().getValue().equals(null))
                        is_pelak_valid = false;

                    if (getPlate__0().getValue().trim().length() != 2)
                        is_pelak_valid = false;
                    if (getPlate__2().getValue().trim().length() != 3)
                        is_pelak_valid = false;
                    if (getPlate__3().getValue().trim().length() != 2)
                        is_pelak_valid = false;


                } else {
                    //test motor plate
                    if (getMplate__0().getValue().trim().equals("") || getMplate__0().getValue().equals(null))
                        is_pelak_valid = false;
                    if (getMplate__1().getValue().trim().equals("") || getMplate__1().getValue().equals(null))
                        is_pelak_valid = false;
                    if (getMplate__0().getValue().trim().length() != 3)
                        is_pelak_valid = false;
                    if (getMplate__1().getValue().trim().length() != 5)
                        is_pelak_valid = false;
                }

                Log.d(TAG, "pelak status >>> " + is_pelak_valid);

                if (!is_pelak_valid) {
                    ShowToast.getInstance().showWarning(view.getContext(), R.string.enter_car_tag);
                    return;
                }
                String pelak = "";
                if (getCar().getValue()) {
                    if (is_pelak_valid) {
                        pelak = getPlate__0().getValue() + PelakUtility.convertToCode(getPlate__1().getValue()) + getPlate__2().getValue() + getPlate__3().getValue();
                    }
                } else {
                    if (is_pelak_valid) {
                        pelak = getMplate__0().getValue() + getMplate__1().getValue();
                    }
                }
                //converting persian numbers to english
                pelak = FontHelper.removeEnter(FontHelper.convertArabicToPersian(pelak));
                Log.d(TAG, "UnChanged-Default pelak >>>  " + pelak);
                StringBuilder newPelak = new StringBuilder(pelak);
                for (int i = 0; i < pelak.length(); i++) {
                    if (Character.isDigit(pelak.charAt(i))) {
                        switch (pelak.charAt(i)) {
                            case '۰':
                                newPelak.setCharAt(i, '0');
                                break;
                            case '۱':
                                newPelak.setCharAt(i, '1');
                                break;
                            case '۲':
                                newPelak.setCharAt(i, '2');
                                break;
                            case '۳':
                                newPelak.setCharAt(i, '3');
                                break;
                            case '۴':
                                newPelak.setCharAt(i, '4');
                                break;
                            case '۵':
                                newPelak.setCharAt(i, '5');
                                break;
                            case '۶':
                                newPelak.setCharAt(i, '6');
                                break;
                            case '۷':
                                newPelak.setCharAt(i, '7');
                                break;
                            case '۸':
                                newPelak.setCharAt(i, '8');
                                break;
                            case '۹':
                                newPelak.setCharAt(i, '9');
                                break;
                        }
                    }
                }
                pelak = newPelak.toString();
                Log.d(TAG, "Changed-New pelak >>>  " + pelak);
                //************************************************************************************************************************
                // ************************************************************************************************************************
                final String finalPelak = pelak;

                parkbanRepository.isCarInserted(finalPelak, new ParkbanRepository.DataBaseInsertedCarPelakCallBack() {
                    @Override
                    public void onSuccess(EntranceRecord entranceRecord) {
                        Log.d(TAG, "onSuccess: yes it was inserted");

                        try {
                            handelExit(
                                    entranceRecord.getPlate(),
                                    entranceRecord.getEntranceDate().replace("_", ""),
                                    Integer.toString(entranceRecord.getTariffId()),
                                    Long.toString(entranceRecord.getPaidAmount()),
                                    Long.toString(entranceRecord.getEntranceDoorId()),
                                    Long.toString(entranceRecord.getEntranceOperatorId()),
                                    Integer.toString(entranceRecord.getPayType()),
                                    entranceRecord.getElectronicPaymentCode()
                            );
                        } catch (Exception exception) {
                            ShowToast.getInstance().showError(myContext, R.string.exception_msg_245);
                        }


                    }

                    @Override
                    public void onFailed() {
                        Log.d(TAG, "onFailed: the vehicle does not exits");
                        ShowToast.getInstance().showWarning(myContext, R.string.car_not_inserted);


                    }
                });


            }
        }, Animation_Constant.ANIMATION_VALUE);
    }

    public void Search_Server_Onclick(final View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_bubble_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                //************************************************************************************************************************
                // ************************************************************************************************************************
                boolean is_pelak_valid = true;
                if (getCar().getValue()) {
                    //test car plate
                    if (getPlate__0().getValue().trim().equals("") || getPlate__0().getValue().equals(null))
                        is_pelak_valid = false;
                    if (getPlate__2().getValue().trim().equals("") || getPlate__2().getValue().equals(null))
                        is_pelak_valid = false;
                    if (getPlate__3().getValue().trim().equals("") || getPlate__3().getValue().equals(null))
                        is_pelak_valid = false;

                    if (getPlate__0().getValue().trim().length() != 2)
                        is_pelak_valid = false;
                    if (getPlate__2().getValue().trim().length() != 3)
                        is_pelak_valid = false;
                    if (getPlate__3().getValue().trim().length() != 2)
                        is_pelak_valid = false;


                } else {
                    //test motor plate
                    if (getMplate__0().getValue().trim().equals("") || getMplate__0().getValue().equals(null))
                        is_pelak_valid = false;
                    if (getMplate__1().getValue().trim().equals("") || getMplate__1().getValue().equals(null))
                        is_pelak_valid = false;
                    if (getMplate__0().getValue().trim().length() != 3)
                        is_pelak_valid = false;
                    if (getMplate__1().getValue().trim().length() != 5)
                        is_pelak_valid = false;
                }

                Log.d(TAG, "pelak status >>> " + is_pelak_valid);

                if (!is_pelak_valid) {
                    ShowToast.getInstance().showWarning(view.getContext(), R.string.enter_car_tag);
                    return;
                }
                String pelak = "";
                if (getCar().getValue()) {
                    if (is_pelak_valid) {
                        pelak = getPlate__0().getValue() + PelakUtility.convertToCode(getPlate__1().getValue()) + getPlate__2().getValue() + getPlate__3().getValue();
                    }
                } else {
                    if (is_pelak_valid) {
                        pelak = getMplate__0().getValue() + getMplate__1().getValue();
                    }
                }
                //converting persian numbers to english
                pelak = FontHelper.removeEnter(FontHelper.convertArabicToPersian(pelak));
                Log.d(TAG, "UnChanged-Default pelak >>>  " + pelak);
                StringBuilder newPelak = new StringBuilder(pelak);
                for (int i = 0; i < pelak.length(); i++) {
                    if (Character.isDigit(pelak.charAt(i))) {
                        switch (pelak.charAt(i)) {
                            case '۰':
                                newPelak.setCharAt(i, '0');
                                break;
                            case '۱':
                                newPelak.setCharAt(i, '1');
                                break;
                            case '۲':
                                newPelak.setCharAt(i, '2');
                                break;
                            case '۳':
                                newPelak.setCharAt(i, '3');
                                break;
                            case '۴':
                                newPelak.setCharAt(i, '4');
                                break;
                            case '۵':
                                newPelak.setCharAt(i, '5');
                                break;
                            case '۶':
                                newPelak.setCharAt(i, '6');
                                break;
                            case '۷':
                                newPelak.setCharAt(i, '7');
                                break;
                            case '۸':
                                newPelak.setCharAt(i, '8');
                                break;
                            case '۹':
                                newPelak.setCharAt(i, '9');
                                break;
                        }
                    }
                }
                pelak = newPelak.toString();
                Log.d(TAG, "Changed-New pelak >>>  " + pelak);
                //************************************************************************************************************************
                // ************************************************************************************************************************
                final String finalPelak = pelak;
                final TelephonyManager telephonyManager = (TelephonyManager) (((ExitQrActivity) myContext).getSystemService(Context.TELEPHONY_SERVICE));
                ;

                String pelakForAskFromServer;

                if (finalPelak.length() == 8) {
                    //motor
                    pelakForAskFromServer = finalPelak;
                } else {
                    //car
                    pelakForAskFromServer = finalPelak.substring(0, 2) + PelakUtility.convertFromCode(finalPelak.substring(2, 4)) + finalPelak.substring(4, 9);
                }


                ForgotEntranceRequest forgotEntranceRequest = new ForgotEntranceRequest();
                forgotEntranceRequest.setImei("968500040082191".trim());
                forgotEntranceRequest.setPlate(pelakForAskFromServer);

                parkbanRepository.forgotEntrance(forgotEntranceRequest, new ParkbanRepository.ServiceResultCallBack<ForgotRecordResponse>() {
                    @Override
                    public void onSuccess(ForgotRecordResponse result) {

                        String temp1 = result.getTrafficDateTime().replace("-", "");
                        String temp2 = temp1.replace(":", "");
                        String temp3 = temp2.replace("T", "");
                        String validDatabaseEntranceDate = temp3.substring(0, 12);


                        try {
                            handelExit(
                                    finalPelak,
                                    validDatabaseEntranceDate,
                                    Integer.toString(1),
                                    Long.toString(result.getCost()),
                                    Long.toString(result.getDoorId()),
                                    Long.toString(result.getOperatorId()),
                                    Long.toString(result.getPaymentKind()),
                                    result.getElectronicPaymentTracingCode()
                            );
                        } catch (Exception exception) {
                            ShowToast.getInstance().showError(myContext, R.string.exception_msg_245);
                        }


                    }

                    @Override
                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                        progress.setValue(0);
                        switch (resultType) {
                            case RetrofitError:
                                ShowToast.getInstance().showError(myContext, R.string.entrance_not_found);
                                break;
                            case ServerError:
                                if (errorCode != 0)
                                    ShowToast.getInstance().showError(myContext, errorCode);
                                else
                                    ShowToast.getInstance().showError(myContext, R.string.connection_failed);
                                break;
                            default:
                                ShowToast.getInstance().showError(myContext, resultType.ordinal());
                        }
                    }
                });


//                parkbanRepository.forgotEntrance();


            }
        }, Animation_Constant.ANIMATION_VALUE);
    }

    public void Refresh_Page(final View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_rotate_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        myAnim.setRepeatMode(1);
        view.startAnimation(myAnim);
        doRefresh();
    }

    public void Setting_Password_Page(final View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_rotate_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        myAnim.setRepeatMode(1);
        view.startAnimation(myAnim);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


            }
        }, Animation_Constant.ANIMATION_VALUE);


    }


    public void backPress(Context context) {
        if (doubleBackToExitPressedOnce) {
            ((BaseActivity) (context)).finish();
        }
        this.doubleBackToExitPressedOnce = true;
        ShowToast.getInstance().showExit(context, R.string.click_back_again);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, EXIT_TIMEOUT);
    }

    public void doRefresh() {
        mplate__0.setValue("");
        mplate__1.setValue("");
        plate__0.setValue("");
        plate__1.setValue("الف");
        plate__2.setValue("");
        plate__3.setValue("");

        car.setValue(true);
        motor.setValue(false);
        mSpinner.setSelection(0);

    }

    private String getTariffNameById(String tarrifId) {
        String result = "";
        switch (tarrifId) {
            case "1":
                result = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff1().getVehicleName();
                break;
            case "2":
                result = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff2().getVehicleName();
                break;
            case "3":
                result = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff3().getVehicleName();
                break;
            case "4":
                result = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff4().getVehicleName();
                break;
            case "5":
                result = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff5().getVehicleName();
                break;
            case "6":
                result = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff6().getVehicleName();
                break;
            case "7":
                result = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff7().getVehicleName();
                break;
            case "8":
                result = getParkingInfoResponse.getValue().getTariffs().getVehicleTariff8().getVehicleName();
                break;

        }
        return result;
    }

    public Bitmap generateBitmapByLayoutForPayment(Context context, String enterTime, String exitTime, String vehicelName, String paidAmount, String pelak) throws ParseException {

        Log.d(TAG, "generateBitmapByLayoutForPayment:  pelak that recived for print : " + pelak);


        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.receipt_exit, null);

        TextView entry_date_txt = view.findViewById(R.id.exit_resid_date_time_value);
        TextView exit_date_txt = view.findViewById(R.id.exit_resid_date_time_value_exit);
        TextView parking_name_txt = view.findViewById(R.id.exit_resid_parking_name);
        TextView paid_amount_txt = view.findViewById(R.id.exit_resid_paid_amount_value);
        TextView tariff_txt = view.findViewById(R.id.exit_resid_tariff_type_value);
        TextView plate_txt_0 = view.findViewById(R.id.p0_exit_resid);
        TextView plate_txt_1 = view.findViewById(R.id.p1_exit_resid);
        TextView plate_txt_2 = view.findViewById(R.id.p2_exit_resid);
        TextView plate_txt_3 = view.findViewById(R.id.p3_exit_resid);
        TextView m_plate_txt_0 = view.findViewById(R.id.m0_exit_resid);
        TextView m_plate_txt_1 = view.findViewById(R.id.m1_exit_resid);
        RelativeLayout pelak_main_layoout = view.findViewById(R.id.plate_main_layout_exit_resid);
        LinearLayout motor_main_layout = view.findViewById(R.id.motor_layout_exit_resid);
        LinearLayout car_main_layout = view.findViewById(R.id.plate_layout_exit_resid);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault());
        Date enter__ = sdf.parse(enterTime);
        Date exit__ = sdf.parse(exitTime);


        String lan = PreferenceManager.getDefaultSharedPreferences(context).getString("language", "fa");

        if (lan.equals("fa")) {
            paid_amount_txt.setText(paidAmount + " ریال");


            PersianDate persianDate = new PersianDate(enter__);
            PersianDateFormat pdformater1 = new PersianDateFormat("Y/m/d");
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String jalaliDateEnter = pdformater1.format(persianDate) + "  " + sdf1.format(enter__);


            PersianDate persianDate___ = new PersianDate(exit__);
            String jalaliDateExit = pdformater1.format(persianDate___) + "  " + sdf1.format(exit__);


            entry_date_txt.setText(jalaliDateEnter);
            exit_date_txt.setText(jalaliDateExit);
        } else {
            paid_amount_txt.setText(paidAmount + " Rials");


            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.getDefault());


            entry_date_txt.setText(sdf1.format(enter__));
            exit_date_txt.setText(sdf1.format(exit__));
        }


        parking_name_txt.setText(getParkingInfoResponse.getValue().getParkingName().trim());

        pelak_main_layoout.setVisibility(View.VISIBLE);
        if (pelak.length() > 8) {
            car_main_layout.setVisibility(View.VISIBLE);
            motor_main_layout.setVisibility(View.GONE);
            plate_txt_0.setText(pelak.substring(0, 2));
            plate_txt_1.setText(PelakUtility.convertFromCode(pelak.substring(2, 4)));
            plate_txt_2.setText(pelak.substring(4, 7));
            plate_txt_3.setText(pelak.substring(7, 9));


        } else {
            car_main_layout.setVisibility(View.GONE);
            motor_main_layout.setVisibility(View.VISIBLE);
            m_plate_txt_0.setText(pelak.substring(0, 3));
            m_plate_txt_1.setText(pelak.substring(3, 8));
        }

        tariff_txt.setText(vehicelName);


        return PrinterUtils.convertViewToBitmap(view);
    }


}
