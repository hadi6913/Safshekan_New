package com.khodmohaseb.parkban.viewmodels;

import android.annotation.SuppressLint;
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
import android.text.format.DateUtils;
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
import com.google.gson.GsonBuilder;
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
import com.khodmohaseb.parkban.utils.CalculateHelperUtility;
import com.khodmohaseb.parkban.utils.DailyFareCalculator;
import com.khodmohaseb.parkban.utils.DailyHourlyFareCalculator;
import com.khodmohaseb.parkban.utils.HourlyFareCalculator;
import com.khodmohaseb.parkban.utils.MyBounceInterpolator;
import com.khodmohaseb.parkban.utils.PelakUtility;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.pax.dal.IDAL;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

import static com.google.android.gms.internal.zzid.runOnUiThread;

public class EnterManuallyViewModel extends ViewModel {

    public static final String TAG = "xeagle6913 EnterManuallyViewModel";


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


    String pelakString____;
    String tariffIdString____;
    Date enterDateTime;
    Date currentDateTime;
    String enterDoorIDString____;
    String exitDoorIDString____;
    String enterOperatorIDString____;
    String exitOperatorIDString____;
    String paidAmount____;


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
        enteredDate.setValue("****");


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
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(myContext);
                LayoutInflater inflater = ((Activity) myContext).getLayoutInflater();
                View alertView = inflater.inflate(R.layout.dialog_change_password, null);
                alertDialog.setView(alertView);
                alertDialog.setCancelable(false);
                final AlertDialog show = alertDialog.show();
                final EditText exTxtCurrentPassword = alertView.findViewById(R.id.dialog_change_password_current_pass_etxt);
                final EditText exTxtNewPassword = alertView.findViewById(R.id.dialog_change_password_new_pass_etxt);
                final EditText exTxtConfirmPassword = alertView.findViewById(R.id.dialog_change_password_repeat_new_pass_etxt);
                LinearLayout cancel = alertView.findViewById(R.id.dialog_change_password_cancel_linear);
                LinearLayout confirm = alertView.findViewById(R.id.dialog_change_password_confirm_linear);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show.dismiss();
                    }
                });

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((exTxtCurrentPassword.getText().toString().trim() != null) && (!exTxtCurrentPassword.getText().toString().trim().equals(""))) {
                            if (selectedUser.getUserPass().trim().equals(exTxtCurrentPassword.getText().toString().trim())) {

                                if ((exTxtNewPassword.getText().toString().trim() != null) && (!exTxtNewPassword.getText().toString().trim().equals(""))) {

                                    if ((exTxtConfirmPassword.getText().toString().trim() != null) && (!exTxtConfirmPassword.getText().toString().trim().equals(""))) {

                                        if (exTxtNewPassword.getText().toString().trim().equals(exTxtConfirmPassword.getText().toString().trim())) {
                                            //save shared pref
                                            Gson gson = new GsonBuilder()
                                                    .serializeNulls()
                                                    .create();
                                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(myContext).edit();


                                            Operator operator = new Operator();
                                            operator.setId(selectedUser.getId());
                                            operator.setFirstName(selectedUser.getFirstName());
                                            operator.setLastName(selectedUser.getLastName());
                                            operator.setUserName(selectedUser.getUserName());
                                            operator.setUserPass(exTxtConfirmPassword.getText().toString().trim());
                                            operator.setParkingId(selectedUser.getParkingId());
                                            selectedUser = operator;
                                            String json_user = gson.toJson(operator);
                                            editor.putString("currentuser", json_user);
                                            editor.commit();

                                            GetParkingInfoResponse getParkingInfoResponse1 = getParkingInfoResponse;


                                            for (Operator item : getParkingInfoResponse1.getOperators()) {
                                                if (item.getUserName().equals(operator.getUserName())) {
                                                    item.setUserPass(operator.getUserPass());
                                                }
                                            }


                                            String json = gson.toJson(getParkingInfoResponse1);
                                            editor.putString("parkinginfo", json);
                                            editor.commit();


                                            //save in table database
                                            parkbanRepository.saveChangePassword(
                                                    operator.getParkingId(),
                                                    operator.getUserName(),
                                                    operator.getUserPass(), new ParkbanRepository.DataBaseResultCallBack() {
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
                                                    }

                                            );


                                        } else {
                                            ShowToast.getInstance().showError(myContext, R.string.repeat_password_not);
                                            return;
                                        }

                                    } else {
                                        ShowToast.getInstance().showError(myContext, R.string.enter_all_items);
                                        return;
                                    }


                                } else {
                                    ShowToast.getInstance().showError(myContext, R.string.enter_all_items);
                                    return;
                                }


                            } else {
                                ShowToast.getInstance().showError(myContext, R.string.current_password_not);
                                return;
                            }
                        } else {

                            ShowToast.getInstance().showError(myContext, R.string.enter_all_items);
                            return;
                        }

                    }
                });


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
            @SuppressLint("LongLogTag")
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
                Log.d(TAG, "final pelak >>>  " + pelak);
                //************************************************************************************************************************
                // ************************************************************************************************************************
                final String finalPelak = pelak;
                if (enteredDate.getValue().length() < 6) {
                    ShowToast.getInstance().showWarning(view.getContext(), R.string.enter_entrance_date);
                    return;
                }
                String temp = enteredDate.getValue();
                String temp1 = temp.replace("/", "");
                String temp2 = temp1.replace(":", "");
                String finalEntranceDate = temp2.replace(" ", "").trim();
                Log.d(TAG, "final entranceDate >>>  " + finalEntranceDate);
                PersianDate persianDate = new PersianDate();


                int[] dateGeorg = persianDate.jalali_to_gregorian(
                        Integer.parseInt(finalEntranceDate.substring(0, 4)),
                        Integer.parseInt(finalEntranceDate.substring(4, 6)),
                        Integer.parseInt(finalEntranceDate.substring(6, 8))
                );

                String year = Integer.toString(dateGeorg[0]);
                String month = String.format("%02d", dateGeorg[1]);
                String day = String.format("%02d", dateGeorg[2]);
                String hour = finalEntranceDate.substring(8, 10);
                String min = finalEntranceDate.substring(10, 12);

                String actualFinalDate = year + month + day + hour + min;


                try {


                    handelExit(
                            finalPelak,
                            actualFinalDate,
                            Integer.toString(selectedTarrifId.getValue()),
                            "0",
                            Long.toString(selectedDoor.getId()),
                            Long.toString(selectedUser.getId()),
                            "0",
                            "");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }


            }
        }, Animation_Constant.ANIMATION_VALUE);
    }

    private String getTariffNameById(String tarrifId) {
        String result = "";
        switch (tarrifId) {
            case "1":
                result = getParkingInfoResponse.getTariffs().getVehicleTariff1().getVehicleName();
                break;
            case "2":
                result = getParkingInfoResponse.getTariffs().getVehicleTariff2().getVehicleName();
                break;
            case "3":
                result = getParkingInfoResponse.getTariffs().getVehicleTariff3().getVehicleName();
                break;
            case "4":
                result = getParkingInfoResponse.getTariffs().getVehicleTariff4().getVehicleName();
                break;
            case "5":
                result = getParkingInfoResponse.getTariffs().getVehicleTariff5().getVehicleName();
                break;
            case "6":
                result = getParkingInfoResponse.getTariffs().getVehicleTariff6().getVehicleName();
                break;
            case "7":
                result = getParkingInfoResponse.getTariffs().getVehicleTariff7().getVehicleName();
                break;
            case "8":
                result = getParkingInfoResponse.getTariffs().getVehicleTariff8().getVehicleName();
                break;

        }
        return result;
    }


    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    @SuppressLint("LongLogTag")
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
            if (totalStayInMinutes <= getParkingInfoResponse.getTariffs().getFreeDuration()) {
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


                        parkbanRepository.saveEntranceRecord(
                                getParkingInfoResponse.getDeviceId(),
                                pelakString,
                                enterDateTimeStringForDataBase,
                                tarrifId,
                                0,
                                0,
                                "",
                                selectedDoor.getId().longValue(),
                                selectedUser.getId().longValue(),
                                1,
                                new ParkbanRepository.DataBaseResultCallBack() {
                                    @Override
                                    public void onSuccess(long id) {
                                        parkbanRepository.saveExitRecord(
                                                getParkingInfoResponse.getDeviceId(),
                                                pelakString,
                                                exitDateTimeStringForDataBase,
                                                tarrifId,
                                                0,
                                                0,
                                                "",
                                                selectedDoor.getId().longValue(),
                                                selectedUser.getId().longValue(),
                                                1,
                                                new ParkbanRepository.DataBaseResultCallBack() {
                                                    @Override
                                                    public void onSuccess(long id) {

                                                        parkbanRepository.saveTraffikRecord(
                                                                getParkingInfoResponse.getDeviceId(),
                                                                pelakString,
                                                                enterDateTimeStringForDataBase,
                                                                exitDateTimeStringForDataBase,
                                                                tarrifId,
                                                                0,
                                                                0,
                                                                "",
                                                                Long.parseLong(enterDoorIDString),
                                                                selectedDoor.getId().longValue(),
                                                                Long.parseLong(enterOperatorIDString),
                                                                selectedUser.getId().longValue(),
                                                                getTariffNameById(tariffIdString), new ParkbanRepository.DataBaseResultCallBack() {
                                                                    @Override
                                                                    public void onSuccess(long id) {


                                                                        parkbanRepository.setExitEntranceRecord(pelakString, new ParkbanRepository.DataBaseResultCallBack() {
                                                                            @Override
                                                                            public void onSuccess(long id) {
                                                                                ShowToast.getInstance().showSuccess(myContext, R.string.submit_success);
                                                                                doRefresh();
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

                                    @Override
                                    public void onFailed() {

                                    }
                                }


                        );


                    }
                });


            } else {
                //should call calculate fare
                long roundedTotalStayInMinutes = CalculateHelperUtility.roundStayLengthTime(totalStayInMinutes, getParkingInfoResponse.getTariffs());
                Log.d(TAG, "rounded stay in minutes : " + roundedTotalStayInMinutes);
                if (getParkingInfoResponse.getTariffs().getIsCircadian()) {

                    if (roundedTotalStayInMinutes < getParkingInfoResponse.getTariffs().getCircadianCalcBase()) {
                        //should calculate hourly
                        switch (tariffIdString.trim()) {
                            case "1":
                                price = getParkingInfoResponse.getTariffs().getVehicleTariff1().getEntranceCost() +
                                        HourlyFareCalculator.calculateStepFareVehicle1(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getTariffs()
                                        );
                                break;
                            case "2":
                                price = getParkingInfoResponse.getTariffs().getVehicleTariff2().getEntranceCost() +
                                        HourlyFareCalculator.calculateStepFareVehicle2(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getTariffs()
                                        );
                                break;
                            case "3":
                                price = getParkingInfoResponse.getTariffs().getVehicleTariff3().getEntranceCost() +
                                        HourlyFareCalculator.calculateStepFareVehicle3(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getTariffs()
                                        );
                                break;
                            case "4":
                                price = getParkingInfoResponse.getTariffs().getVehicleTariff4().getEntranceCost() +
                                        HourlyFareCalculator.calculateStepFareVehicle4(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getTariffs()
                                        );
                                break;
                            case "5":
                                price = getParkingInfoResponse.getTariffs().getVehicleTariff5().getEntranceCost() +
                                        HourlyFareCalculator.calculateStepFareVehicle5(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getTariffs()
                                        );
                                break;
                            case "6":
                                price = getParkingInfoResponse.getTariffs().getVehicleTariff6().getEntranceCost() +
                                        HourlyFareCalculator.calculateStepFareVehicle6(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getTariffs()
                                        );
                                break;
                            case "7":
                                price = getParkingInfoResponse.getTariffs().getVehicleTariff7().getEntranceCost() +
                                        HourlyFareCalculator.calculateStepFareVehicle7(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getTariffs()
                                        );
                                break;
                            case "8":
                                price = getParkingInfoResponse.getTariffs().getVehicleTariff8().getEntranceCost() +
                                        HourlyFareCalculator.calculateStepFareVehicle8(
                                                roundedTotalStayInMinutes,
                                                getParkingInfoResponse.getTariffs()
                                        );
                                break;

                        }
                    } else {
                        // should calculate daily or daily-hourly
                        if (getParkingInfoResponse.getTariffs().getCircadianCalcKind() == 0) {
                            //daily
                            switch (tariffIdString.trim()) {
                                case "1":
                                    price = getParkingInfoResponse.getTariffs().getVehicleTariff1().getEntranceCost() +
                                            DailyFareCalculator.calculateDailyFareVehicle1(
                                                    roundedTotalStayInMinutes,
                                                    getParkingInfoResponse.getTariffs()
                                            );

                                    break;
                                case "2":
                                    price = getParkingInfoResponse.getTariffs().getVehicleTariff2().getEntranceCost() +
                                            DailyFareCalculator.calculateDailyFareVehicle2(
                                                    roundedTotalStayInMinutes,
                                                    getParkingInfoResponse.getTariffs()
                                            );
                                    break;
                                case "3":
                                    price = getParkingInfoResponse.getTariffs().getVehicleTariff3().getEntranceCost() +
                                            DailyFareCalculator.calculateDailyFareVehicle3(
                                                    roundedTotalStayInMinutes,
                                                    getParkingInfoResponse.getTariffs()
                                            );
                                    break;
                                case "4":
                                    price = getParkingInfoResponse.getTariffs().getVehicleTariff4().getEntranceCost() +
                                            DailyFareCalculator.calculateDailyFareVehicle4(
                                                    roundedTotalStayInMinutes,
                                                    getParkingInfoResponse.getTariffs()
                                            );
                                    break;
                                case "5":
                                    price = getParkingInfoResponse.getTariffs().getVehicleTariff5().getEntranceCost() +
                                            DailyFareCalculator.calculateDailyFareVehicle5(
                                                    roundedTotalStayInMinutes,
                                                    getParkingInfoResponse.getTariffs()
                                            );
                                    break;
                                case "6":
                                    price = getParkingInfoResponse.getTariffs().getVehicleTariff6().getEntranceCost() +
                                            DailyFareCalculator.calculateDailyFareVehicle6(
                                                    roundedTotalStayInMinutes,
                                                    getParkingInfoResponse.getTariffs()
                                            );
                                    break;
                                case "7":
                                    price = getParkingInfoResponse.getTariffs().getVehicleTariff7().getEntranceCost() +
                                            DailyFareCalculator.calculateDailyFareVehicle7(
                                                    roundedTotalStayInMinutes,
                                                    getParkingInfoResponse.getTariffs()
                                            );
                                    break;
                                case "8":
                                    price = getParkingInfoResponse.getTariffs().getVehicleTariff8().getEntranceCost() +
                                            DailyFareCalculator.calculateDailyFareVehicle8(
                                                    roundedTotalStayInMinutes,
                                                    getParkingInfoResponse.getTariffs()
                                            );
                                    break;
                            }
                        } else {
                            //daily hourly
                            switch (tariffIdString.trim()) {
                                case "1":
                                    price = getParkingInfoResponse.getTariffs().getVehicleTariff1().getEntranceCost() +
                                            DailyHourlyFareCalculator.calculateHourlyDailyFareVehicle1(
                                                    roundedTotalStayInMinutes,
                                                    getParkingInfoResponse.getTariffs()
                                            );
                                    break;
                                case "2":
                                    price = getParkingInfoResponse.getTariffs().getVehicleTariff2().getEntranceCost() +
                                            DailyHourlyFareCalculator.calculateHourlyDailyFareVehicle2(
                                                    roundedTotalStayInMinutes,
                                                    getParkingInfoResponse.getTariffs()
                                            );
                                    break;
                                case "3":
                                    price = getParkingInfoResponse.getTariffs().getVehicleTariff3().getEntranceCost() +
                                            DailyHourlyFareCalculator.calculateHourlyDailyFareVehicle3(
                                                    roundedTotalStayInMinutes,
                                                    getParkingInfoResponse.getTariffs()
                                            );
                                    break;
                                case "4":
                                    price = getParkingInfoResponse.getTariffs().getVehicleTariff4().getEntranceCost() +
                                            DailyHourlyFareCalculator.calculateHourlyDailyFareVehicle4(
                                                    roundedTotalStayInMinutes,
                                                    getParkingInfoResponse.getTariffs()
                                            );
                                    break;
                                case "5":
                                    price = getParkingInfoResponse.getTariffs().getVehicleTariff5().getEntranceCost() +
                                            DailyHourlyFareCalculator.calculateHourlyDailyFareVehicle5(
                                                    roundedTotalStayInMinutes,
                                                    getParkingInfoResponse.getTariffs()
                                            );
                                    break;
                                case "6":
                                    price = getParkingInfoResponse.getTariffs().getVehicleTariff6().getEntranceCost() +
                                            DailyHourlyFareCalculator.calculateHourlyDailyFareVehicle6(
                                                    roundedTotalStayInMinutes,
                                                    getParkingInfoResponse.getTariffs()
                                            );
                                    break;
                                case "7":
                                    price = getParkingInfoResponse.getTariffs().getVehicleTariff7().getEntranceCost() +
                                            DailyHourlyFareCalculator.calculateHourlyDailyFareVehicle7(
                                                    roundedTotalStayInMinutes,
                                                    getParkingInfoResponse.getTariffs()
                                            );
                                    break;
                                case "8":
                                    price = getParkingInfoResponse.getTariffs().getVehicleTariff8().getEntranceCost() +
                                            DailyHourlyFareCalculator.calculateHourlyDailyFareVehicle8(
                                                    roundedTotalStayInMinutes,
                                                    getParkingInfoResponse.getTariffs()
                                            );
                                    break;
                            }
                        }
                    }


                } else {
                    // should calculate hourly
                    switch (tariffIdString.trim()) {
                        case "1":
                            price = getParkingInfoResponse.getTariffs().getVehicleTariff1().getEntranceCost() +
                                    HourlyFareCalculator.calculateStepFareVehicle1(
                                            roundedTotalStayInMinutes,
                                            getParkingInfoResponse.getTariffs()
                                    );
                            break;
                        case "2":
                            price = getParkingInfoResponse.getTariffs().getVehicleTariff2().getEntranceCost() +
                                    HourlyFareCalculator.calculateStepFareVehicle2(
                                            roundedTotalStayInMinutes,
                                            getParkingInfoResponse.getTariffs()
                                    );
                            break;
                        case "3":
                            price = getParkingInfoResponse.getTariffs().getVehicleTariff3().getEntranceCost() +
                                    HourlyFareCalculator.calculateStepFareVehicle3(
                                            roundedTotalStayInMinutes,
                                            getParkingInfoResponse.getTariffs()
                                    );
                            break;
                        case "4":
                            price = getParkingInfoResponse.getTariffs().getVehicleTariff4().getEntranceCost() +
                                    HourlyFareCalculator.calculateStepFareVehicle4(
                                            roundedTotalStayInMinutes,
                                            getParkingInfoResponse.getTariffs()
                                    );
                            break;
                        case "5":
                            price = getParkingInfoResponse.getTariffs().getVehicleTariff5().getEntranceCost() +
                                    HourlyFareCalculator.calculateStepFareVehicle5(
                                            roundedTotalStayInMinutes,
                                            getParkingInfoResponse.getTariffs()
                                    );
                            break;
                        case "6":
                            price = getParkingInfoResponse.getTariffs().getVehicleTariff6().getEntranceCost() +
                                    HourlyFareCalculator.calculateStepFareVehicle6(
                                            roundedTotalStayInMinutes,
                                            getParkingInfoResponse.getTariffs()
                                    );
                            break;
                        case "7":
                            price = getParkingInfoResponse.getTariffs().getVehicleTariff7().getEntranceCost() +
                                    HourlyFareCalculator.calculateStepFareVehicle7(
                                            roundedTotalStayInMinutes,
                                            getParkingInfoResponse.getTariffs()
                                    );
                            break;
                        case "8":
                            price = getParkingInfoResponse.getTariffs().getVehicleTariff8().getEntranceCost() +
                                    HourlyFareCalculator.calculateStepFareVehicle8(
                                            roundedTotalStayInMinutes,
                                            getParkingInfoResponse.getTariffs()
                                    );
                            break;

                    }
                }
                roundedPrice = CalculateHelperUtility.roundFarePrice(price, getParkingInfoResponse.getTariffs());
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


                        parkbanRepository.saveEntranceRecord(
                                getParkingInfoResponse.getDeviceId(),
                                pelakString,
                                enterDateTimeStringForDataBase,
                                tarrifId,
                                0,
                                0,
                                "",
                                selectedDoor.getId().longValue(),
                                selectedUser.getId().longValue(),
                                1,
                                new ParkbanRepository.DataBaseResultCallBack() {
                                    @Override
                                    public void onSuccess(long id) {
                                        parkbanRepository.saveExitRecord(
                                                getParkingInfoResponse.getDeviceId(),
                                                pelakString,
                                                exitDateTimeStringForDataBase,
                                                tarrifId,
                                                finalRoundedPrice,
                                                0,
                                                "",
                                                selectedDoor.getId().longValue(),
                                                selectedUser.getId().longValue(),
                                                1,
                                                new ParkbanRepository.DataBaseResultCallBack() {
                                                    @Override
                                                    public void onSuccess(long id) {

                                                        parkbanRepository.saveTraffikRecord(
                                                                getParkingInfoResponse.getDeviceId(),
                                                                pelakString,
                                                                enterDateTimeStringForDataBase,
                                                                exitDateTimeStringForDataBase,
                                                                tarrifId,
                                                                finalRoundedPrice,
                                                                0,
                                                                "",
                                                                Long.parseLong(enterDoorIDString),
                                                                selectedDoor.getId().longValue(),
                                                                Long.parseLong(enterOperatorIDString),
                                                                selectedUser.getId().longValue(),
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
                                                                                    doRefresh();
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

                                    @Override
                                    public void onFailed() {

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
                txtPaidCost.setText(paidAmountString + "  ریال");


            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
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


                    parkbanRepository.saveEntranceRecord(
                            getParkingInfoResponse.getDeviceId(),
                            pelakString,
                            enterDateTimeStringForDataBase,
                            tarrifId,
                            0,
                            0,
                            "",
                            selectedDoor.getId().longValue(),
                            selectedUser.getId().longValue(),
                            1, new ParkbanRepository.DataBaseResultCallBack() {
                                @Override
                                public void onSuccess(long id) {

                                    parkbanRepository.saveExitRecord(
                                            getParkingInfoResponse.getDeviceId(),
                                            pelakString,
                                            exitDateTimeStringForDataBase,
                                            tarrifId,
                                            Long.parseLong(paidAmountString.trim()),
                                            Integer.parseInt(paymentTypeString.trim()),
                                            electronicPaymentCodeString,
                                            selectedDoor.getId().longValue(),
                                            selectedUser.getId().longValue(),
                                            1,
                                            new ParkbanRepository.DataBaseResultCallBack() {
                                                @Override
                                                public void onSuccess(long id) {

                                                    parkbanRepository.saveTraffikRecord(
                                                            getParkingInfoResponse.getDeviceId(),
                                                            pelakString,
                                                            enterDateTimeStringForDataBase,
                                                            exitDateTimeStringForDataBase,
                                                            tarrifId,
                                                            Long.parseLong(paidAmountString.trim()),
                                                            Integer.parseInt(paymentTypeString.trim()),
                                                            electronicPaymentCodeString,
                                                            Long.parseLong(enterDoorIDString),
                                                            selectedDoor.getId().longValue(),
                                                            Long.parseLong(enterOperatorIDString),
                                                            selectedUser.getId().longValue(),
                                                            getTariffNameById(tariffIdString), new ParkbanRepository.DataBaseResultCallBack() {
                                                                @Override
                                                                public void onSuccess(long id) {

                                                                    parkbanRepository.setExitEntranceRecord(pelakString, new ParkbanRepository.DataBaseResultCallBack() {
                                                                        @Override
                                                                        public void onSuccess(long id) {
                                                                            ShowToast.getInstance().showSuccess(myContext, R.string.submit_success);
                                                                            doRefresh();
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

                                @Override
                                public void onFailed() {

                                }
                            }


                    );


                }
            });


        }


    }


    @SuppressLint("LongLogTag")
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


                parkbanRepository.saveEntranceRecord(


                        getParkingInfoResponse.getDeviceId(),
                        pelakString____,
                        enterDateTimeStringForDataBase,
                        tarrifId,
                        0,
                        0,
                        "",
                        selectedDoor.getId().longValue(),
                        selectedUser.getId().longValue(),
                        1, new ParkbanRepository.DataBaseResultCallBack() {
                            @Override
                            public void onSuccess(long id) {
                                parkbanRepository.saveExitRecord(
                                        getParkingInfoResponse.getDeviceId(),
                                        pelakString____,
                                        exitDateTimeStringForDataBase,
                                        tarrifId,
                                        Long.parseLong(paidAmount____),
                                        1,
                                        b.getString("rrn"),
                                        selectedDoor.getId().longValue(),
                                        selectedUser.getId().longValue(),
                                        1,
                                        new ParkbanRepository.DataBaseResultCallBack() {
                                            @Override
                                            public void onSuccess(long id) {

                                                parkbanRepository.saveTraffikRecord(
                                                        getParkingInfoResponse.getDeviceId(),
                                                        pelakString____,
                                                        enterDateTimeStringForDataBase,
                                                        exitDateTimeStringForDataBase,
                                                        tarrifId,
                                                        Long.parseLong(paidAmount____),
                                                        1,
                                                        b.getString("rrn"),
                                                        Long.parseLong(enterDoorIDString____),
                                                        selectedDoor.getId().longValue(),
                                                        Long.parseLong(enterOperatorIDString____),
                                                        selectedUser.getId().longValue(),
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
                                                                            doRefresh();


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
                            }

                            @Override
                            public void onFailed() {

                            }
                        }


                );


            } else {


                ShowToast.getInstance().showError(myContext, R.string.failed_payment);
            }


        }


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


        parking_name_txt.setText(getParkingInfoResponse.getParkingName().trim());

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


    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


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
        enteredDate.setValue("****");

    }


    //***********************************************************************************************
    //***********************************************************************************************
    //***********************************************************************************************


}
