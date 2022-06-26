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
import com.khodmohaseb.parkban.R;
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
import com.khodmohaseb.parkban.persistence.models.khodmohaseb.EntranceRecord;
import com.khodmohaseb.parkban.repositories.ParkbanRepository;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.Door;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.GetParkingInfoResponse;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.Operator;
import com.khodmohaseb.parkban.utils.Animation_Constant;
import com.khodmohaseb.parkban.utils.MyBounceInterpolator;
import com.khodmohaseb.parkban.utils.PelakUtility;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.pax.dal.IDAL;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

import static com.google.android.gms.internal.zzid.runOnUiThread;

public class EnterManuallyViewModel extends ViewModel {

    public static final String TAG = "EnterManuallyViewModel";


    public GetParkingInfoResponse getParkingInfoResponse;
    public Operator selectedUser;
    public Door selectedDoor;

    private static final String MEDIA_FILE_TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";
    private MutableLiveData<Boolean> car;
    private MutableLiveData<Boolean> motor;
    private MutableLiveData<Boolean> hasPelak;
    private EditText etxt_first_cell_car_plate;
    private EditText etxt_first_cell_motor_plate;
    private Context myContext;
    private CarPlate carPlateActual;
    private String newPhotoFilePath;
    private Uri photoURI;
    private Bitmap plateBitmap;
    private IDAL idal;
    private BaseAnprProvider anprProvider;
    private MutableLiveData<String> plate__0;
    private MutableLiveData<String> plate__1;
    private MutableLiveData<String> plate__2;
    private MutableLiveData<String> plate__3;
    private MutableLiveData<String> mplate__0;
    private MutableLiveData<String> mplate__1;
    private MutableLiveData<String> enteredDate;

    private ArrayList<String> carTypeListArray = new ArrayList<String>();
    private String[] carTypeList;
    public ArrayAdapter<CharSequence> langAdapter_car_type;
    public Spinner mCarTypeSpinner;
    private MutableLiveData<String> selectedCarTypeName;
    private MutableLiveData<Integer> selectedTarrifId;
    private MutableLiveData<String> selectedTarrifEntranceFee;




    public MutableLiveData<String> getEnteredDate() {
        if (enteredDate == null)
            enteredDate = new MutableLiveData<>();
        return enteredDate;
    }



    public MutableLiveData<String> getSelectedTarrifEntranceFee() {
        if (selectedTarrifEntranceFee == null)
            selectedTarrifEntranceFee = new MutableLiveData<>();
        return selectedTarrifEntranceFee;
    }


    public MutableLiveData<String> getSelectedCarTypeName() {
        if (selectedCarTypeName == null)
            selectedCarTypeName = new MutableLiveData<>();
        return selectedCarTypeName;
    }


    public MutableLiveData<Integer> getSelectedTarrifId() {
        if (selectedTarrifId == null)
            selectedTarrifId = new MutableLiveData<>();
        return selectedTarrifId;
    }

    public Spinner mSpinner;
    public MutableLiveData<Integer> progress = new MutableLiveData<>();
    private ParkbanRepository parkbanRepository;

    public LiveData<Integer> getProgress() {
        return progress;
    }

    public MutableLiveData<Boolean> getHasPelak() {
        if (hasPelak == null)
            hasPelak = new MutableLiveData<>();
        return hasPelak;
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


    public MutableLiveData<String> getenteredDate() {
        if (enteredDate == null)
            enteredDate = new MutableLiveData<>();
        return enteredDate;
    }

    private boolean doubleBackToExitPressedOnce = false;
    private static final long EXIT_TIMEOUT = 3000;


    //print
    //*********************************************************************************************
    //*********************************************************************************************
    //*********************************************************************************************
    private Bitmap mBitmap;
    PrinterManager mPrinterManager;
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
    //*********************************************************************************************

    public Typeface mFont;


    public void init(final Context context, EditText editTextCar, EditText editTextMotor) {
        etxt_first_cell_car_plate = editTextCar;
        etxt_first_cell_motor_plate = editTextMotor;
        myContext = context;

        new CustomThread().start();

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json_parkingInfo = preferences.getString("parkinginfo", "");
        if (json_parkingInfo.equals("")) {
            getParkingInfoResponse = null;
        } else {
            getParkingInfoResponse = gson.fromJson(json_parkingInfo, GetParkingInfoResponse.class);
        }
        mFont = Typeface.createFromAsset(myContext.getAssets(), "irsans.ttf");

        String json_selectedUser = preferences.getString("currentuser", "");
        if (json_selectedUser.equals("")) {
            selectedUser = null;
        } else {
            selectedUser = gson.fromJson(json_selectedUser, Operator.class);
        }

        String json_selectedDoor = preferences.getString("currentdoor", "");
        if (json_selectedDoor.equals("")) {
            selectedDoor = null;
        } else {
            selectedDoor = gson.fromJson(json_selectedDoor, Door.class);
        }


        carTypeListArray.clear();

        if (getParkingInfoResponse.getTariffs().getVehicleTariff1() != null) {
            carTypeListArray.add(getParkingInfoResponse.getTariffs().getVehicleTariff1().getVehicleName());
        }

        if (getParkingInfoResponse.getTariffs().getVehicleTariff2() != null) {
            carTypeListArray.add(getParkingInfoResponse.getTariffs().getVehicleTariff2().getVehicleName());
        }

        if (getParkingInfoResponse.getTariffs().getVehicleTariff3() != null) {
            carTypeListArray.add(getParkingInfoResponse.getTariffs().getVehicleTariff3().getVehicleName());
        }

        if (getParkingInfoResponse.getTariffs().getVehicleTariff4() != null) {
            carTypeListArray.add(getParkingInfoResponse.getTariffs().getVehicleTariff4().getVehicleName());
        }

        if (getParkingInfoResponse.getTariffs().getVehicleTariff5() != null) {
            carTypeListArray.add(getParkingInfoResponse.getTariffs().getVehicleTariff5().getVehicleName());
        }

        if (getParkingInfoResponse.getTariffs().getVehicleTariff6() != null) {
            carTypeListArray.add(getParkingInfoResponse.getTariffs().getVehicleTariff6().getVehicleName());
        }

        if (getParkingInfoResponse.getTariffs().getVehicleTariff7() != null) {
            carTypeListArray.add(getParkingInfoResponse.getTariffs().getVehicleTariff7().getVehicleName());
        }

        if (getParkingInfoResponse.getTariffs().getVehicleTariff8() != null) {
            carTypeListArray.add(getParkingInfoResponse.getTariffs().getVehicleTariff8().getVehicleName());
        }


        carTypeList = new String[carTypeListArray.size()];
        carTypeList = carTypeListArray.toArray(carTypeList);


        langAdapter_car_type = new ArrayAdapter<CharSequence>(context, R.layout.spinner_text, carTypeList);
        langAdapter_car_type.setDropDownViewResource(R.layout.simple_spinner_dropdown);


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
        if (enteredDate == null)
            enteredDate = new MutableLiveData<>();
        enteredDate.setValue("");


        if (selectedTarrifId == null)
            selectedTarrifId = new MutableLiveData<>();
        selectedTarrifId.setValue(1);

        if (selectedTarrifEntranceFee == null)
            selectedTarrifEntranceFee = new MutableLiveData<>();
        selectedTarrifEntranceFee.setValue("");


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

    //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$



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





    }


    //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$


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


    public void Submit_Onclick(final View view) {
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





            }
        }, Animation_Constant.ANIMATION_VALUE);
    }


    public void Return_Onclick(final View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_bubble_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                ((EnterManullyActivity) (myContext)).finish();



            }
        }, Animation_Constant.ANIMATION_VALUE);
    }


    public void Select_DateTime_Picker(final View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_bubble_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                PersianCalendar persianCalendar = new PersianCalendar();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        (EnterManullyActivity)myContext,
                        persianCalendar.getPersianYear(),
                        persianCalendar.getPersianMonth(),
                        persianCalendar.getPersianDay()
                );
                datePickerDialog.show(myContext.getFragmentManager(), "Datepickerdialog");



            }
        }, Animation_Constant.ANIMATION_VALUE);
    }


    public String getEntranceFeeFromTariffId() {
        String result = "0";
        switch (selectedTarrifId.getValue()) {
            case 1:
                result = getParkingInfoResponse.getTariffs().getVehicleTariff1().getEntranceCost().toString();
                break;
            case 2:
                result = getParkingInfoResponse.getTariffs().getVehicleTariff2().getEntranceCost().toString();
                break;
            case 3:
                result = getParkingInfoResponse.getTariffs().getVehicleTariff3().getEntranceCost().toString();
                break;
            case 4:
                result = getParkingInfoResponse.getTariffs().getVehicleTariff4().getEntranceCost().toString();
                break;
            case 5:
                result = getParkingInfoResponse.getTariffs().getVehicleTariff5().getEntranceCost().toString();
                break;
            case 6:
                result = getParkingInfoResponse.getTariffs().getVehicleTariff6().getEntranceCost().toString();
                break;
            case 7:
                result = getParkingInfoResponse.getTariffs().getVehicleTariff7().getEntranceCost().toString();
                break;
            case 8:
                result = getParkingInfoResponse.getTariffs().getVehicleTariff8().getEntranceCost().toString();
                break;
        }

        return result;
    }


    //***********************************************************************************************
    //***********************************************************************************************
    //***********************************************************************************************

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
    private final int PRINT_FORWARD = 3;   //Forward (paper feed)

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


    //***********************************************************************************************
    //***********************************************************************************************
    //***********************************************************************************************


}
