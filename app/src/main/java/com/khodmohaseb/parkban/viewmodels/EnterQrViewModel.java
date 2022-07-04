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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.khodmohaseb.parkban.BaseActivity;
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
import com.khodmohaseb.parkban.services.ParkbanServiceProvider;
import com.khodmohaseb.parkban.services.dto.ExitBillDto;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.Door;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.GetParkingInfoResponse;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.Operator;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.Tariff;
import com.khodmohaseb.parkban.utils.Animation_Constant;
import com.khodmohaseb.parkban.utils.MyBounceInterpolator;
import com.khodmohaseb.parkban.utils.PelakUtility;
import com.pax.dal.IDAL;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

import static com.google.android.gms.internal.zzid.runOnUiThread;

public class EnterQrViewModel extends ViewModel {

    public static final String TAG = "xeagle6913 EnterQrViewModel";


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

    private ArrayList<String> carTypeListArray = new ArrayList<String>();
    private String[] carTypeList;
    public ArrayAdapter<CharSequence> langAdapter_car_type;
    public Spinner mCarTypeSpinner;
    private MutableLiveData<String> selectedCarTypeName;
    private MutableLiveData<Boolean> shouldPayFirst;
    private MutableLiveData<Integer> selectedTarrifId;
    private MutableLiveData<String> selectedTarrifEntranceFee;


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

    public MutableLiveData<Boolean> getShouldPayFirst() {
        if (shouldPayFirst == null)
            shouldPayFirst = new MutableLiveData<>();
        return shouldPayFirst;
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

        if (shouldPayFirst == null)
            shouldPayFirst = new MutableLiveData<>();
        shouldPayFirst.setValue(false);

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


            Bundle b = data.getBundleExtra("response");
            Log.d(TAG, "onActivityResult:  >>>>>>>>>>>>>>> " + getBundleString(b));
            if (b.getString("result").trim().equals("succeed")) {
                Log.d("e_pardakht >>>", "step 2 >>> GOOD_PAYMENT ");
                Log.d(TAG, "now we should save in database and print recepit");
                final String rrn = b.getString("rrn").trim();

                String pelak = "";
                if (getCar().getValue()) {

                    pelak = getPlate__0().getValue() + PelakUtility.convertToCode(getPlate__1().getValue()) + getPlate__2().getValue() + getPlate__3().getValue();

                } else {

                    pelak = getMplate__0().getValue() + getMplate__1().getValue();

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


                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                Log.d(TAG, "current-date time >>>  " + currentDateandTime);
                //************************************************************************************************************************
                // ************************************************************************************************************************
                int tarrifId = 0;
                if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff1().getVehicleName().trim())) {
                    tarrifId = 1;
                } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff2().getVehicleName().trim())) {
                    tarrifId = 2;
                } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff3().getVehicleName().trim())) {
                    tarrifId = 3;
                } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff4().getVehicleName().trim())) {
                    tarrifId = 4;
                } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff5().getVehicleName().trim())) {
                    tarrifId = 5;
                } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff6().getVehicleName().trim())) {
                    tarrifId = 6;
                } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff7().getVehicleName().trim())) {
                    tarrifId = 7;
                } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff8().getVehicleName().trim())) {
                    tarrifId = 8;
                }
                Log.d(TAG, "current-tarrif id >>>  " + tarrifId);
                //************************************************************************************************************************
                // ************************************************************************************************************************
                Log.d(TAG, "paid-entrance >>>  " + getEntranceFeeFromTariffId());
                //************************************************************************************************************************
                // ************************************************************************************************************************
                Log.d(TAG, "operator id >>>  " + selectedUser.getId());
                //************************************************************************************************************************
                // ************************************************************************************************************************
                Log.d(TAG, "door id >>>  " + selectedDoor.getId());
                //************************************************************************************************************************
                // ************************************************************************************************************************
                String crc = currentDateandTime.substring(9, 12) + "6913" + finalPelak.substring(0, 2);
                Log.d(TAG, "crc >> " + crc);
                //************************************************************************************************************************
                // ************************************************************************************************************************


                Log.d(TAG, "pelak for save in data base : " + finalPelak);


                parkbanRepository.saveEntranceRecord(
                        getParkingInfoResponse.getDeviceId(),
                        finalPelak,
                        currentDateandTime,
                        tarrifId,
                        Long.parseLong(getEntranceFeeFromTariffId()),
                        1,
                        rrn,
                        selectedDoor.getId().longValue(),
                        selectedUser.getId().longValue(),
                        0,
                        new ParkbanRepository.DataBaseResultCallBack() {
                            @Override
                            public void onSuccess(long id) {
                                Log.d(TAG, "onSuccess in save database , now print process begin");
                                mBitmap = generateBitmapByLayoutForPayment(myContext, finalPelak, false, getEntranceFeeFromTariffId().trim(), rrn);
                                Message msg = mPrintHandler.obtainMessage(PRINT_BITMAP);
                                msg.obj = mBitmap;
                                msg.sendToTarget();
                                mPrintHandler.obtainMessage(PRINT_FORWARD).sendToTarget();

                                doRefresh();

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

    public void Taeed_Onclick(final View view) {
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
                        ShowToast.getInstance().showWarning(myContext, R.string.car_inserted);
                    }

                    @Override
                    public void onFailed() {
                        Log.d(TAG, "onFailed: no it was not inserted");


                        if (shouldPayFirst.getValue()) {
                            Log.d(TAG, " should do payment then save in database");
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(myContext);
                            LayoutInflater inflater = ((Activity) myContext).getLayoutInflater();
                            View alertView = inflater.inflate(R.layout.dialog_select_payment_type, null);
                            alertDialog.setView(alertView);
                            alertDialog.setCancelable(false);
                            final AlertDialog show = alertDialog.show();
                            LinearLayout cancelButton = alertView.findViewById(R.id.cancel_layout_select_payment_type);
                            LinearLayout cashButton = alertView.findViewById(R.id.cash_layout_select_payment_type);
                            LinearLayout electronicButton = alertView.findViewById(R.id.electronic_layout_select_payment_type);

                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    show.dismiss();
                                }
                            });

                            cashButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    Log.d(TAG, "save in database then print entrance qr and paid by cash");


                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault());
                                    String currentDateandTime = sdf.format(new Date());
                                    Log.d(TAG, "current-date time >>>  " + currentDateandTime);
                                    //************************************************************************************************************************
                                    // ************************************************************************************************************************
                                    int tarrifId = 0;
                                    if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff1().getVehicleName().trim())) {
                                        tarrifId = 1;
                                    } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff2().getVehicleName().trim())) {
                                        tarrifId = 2;
                                    } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff3().getVehicleName().trim())) {
                                        tarrifId = 3;
                                    } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff4().getVehicleName().trim())) {
                                        tarrifId = 4;
                                    } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff5().getVehicleName().trim())) {
                                        tarrifId = 5;
                                    } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff6().getVehicleName().trim())) {
                                        tarrifId = 6;
                                    } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff7().getVehicleName().trim())) {
                                        tarrifId = 7;
                                    } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff8().getVehicleName().trim())) {
                                        tarrifId = 8;
                                    }
                                    Log.d(TAG, "current-tarrif id >>>  " + tarrifId);
                                    //************************************************************************************************************************
                                    // ************************************************************************************************************************
                                    Log.d(TAG, "paid-entrance >>>  " + getEntranceFeeFromTariffId());
                                    //************************************************************************************************************************
                                    // ************************************************************************************************************************
                                    Log.d(TAG, "operator id >>>  " + selectedUser.getId());
                                    //************************************************************************************************************************
                                    // ************************************************************************************************************************
                                    Log.d(TAG, "door id >>>  " + selectedDoor.getId());
                                    //************************************************************************************************************************
                                    // ************************************************************************************************************************
                                    String crc = currentDateandTime.substring(9, 12) + "6913" + finalPelak.substring(0, 2);
                                    Log.d(TAG, "crc >> " + crc);
                                    //************************************************************************************************************************
                                    // ************************************************************************************************************************


                                    Log.d(TAG, "pelak for save in data base : " + finalPelak);


                                    parkbanRepository.saveEntranceRecord(
                                            getParkingInfoResponse.getDeviceId(),
                                            finalPelak,
                                            currentDateandTime,
                                            tarrifId,
                                            Long.parseLong(getEntranceFeeFromTariffId()),
                                            0,
                                            "",
                                            selectedDoor.getId().longValue(),
                                            selectedUser.getId().longValue(),
                                            0,
                                            new ParkbanRepository.DataBaseResultCallBack() {
                                                @Override
                                                public void onSuccess(long id) {

                                                    Log.d(TAG, "onSuccess in save database , now print process begin");
                                                    mBitmap = generateBitmapByLayoutForPayment(myContext, finalPelak, true, getEntranceFeeFromTariffId(), " ");
                                                    Message msg = mPrintHandler.obtainMessage(PRINT_BITMAP);
                                                    msg.obj = mBitmap;
                                                    msg.sendToTarget();
                                                    mPrintHandler.obtainMessage(PRINT_FORWARD).sendToTarget();
                                                    doRefresh();
                                                    show.dismiss();

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
                                    fanavaPaymentIntent.putExtra("amount", getEntranceFeeFromTariffId());
                                    fanavaPaymentIntent.putExtra("res_num", Long.parseLong(finalPelak));
                                    ((Activity) myContext).startActivityForResult(fanavaPaymentIntent, 103);


                                }
                            });


                        } else {
                            Log.d(TAG, "save in database then print entrance qr and no need to pay now");


                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault());
                            String currentDateandTime = sdf.format(new Date());
                            Log.d(TAG, "current-date time >>>  " + currentDateandTime);
                            //************************************************************************************************************************
                            // ************************************************************************************************************************
                            int tarrifId = 0;
                            if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff1().getVehicleName().trim())) {
                                tarrifId = 1;
                            } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff2().getVehicleName().trim())) {
                                tarrifId = 2;
                            } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff3().getVehicleName().trim())) {
                                tarrifId = 3;
                            } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff4().getVehicleName().trim())) {
                                tarrifId = 4;
                            } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff5().getVehicleName().trim())) {
                                tarrifId = 5;
                            } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff6().getVehicleName().trim())) {
                                tarrifId = 6;
                            } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff7().getVehicleName().trim())) {
                                tarrifId = 7;
                            } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff8().getVehicleName().trim())) {
                                tarrifId = 8;
                            }
                            Log.d(TAG, "current-tarrif id >>>  " + tarrifId);
                            //************************************************************************************************************************
                            // ************************************************************************************************************************
                            Log.d(TAG, "paid-entrance >>>  " + 0);
                            //************************************************************************************************************************
                            // ************************************************************************************************************************
                            Log.d(TAG, "operator id >>>  " + selectedUser.getId());
                            //************************************************************************************************************************
                            // ************************************************************************************************************************
                            Log.d(TAG, "door id >>>  " + selectedDoor.getId());
                            //************************************************************************************************************************
                            // ************************************************************************************************************************
                            String crc = currentDateandTime.substring(9, 12) + "6913" + finalPelak.substring(0, 2);
                            Log.d(TAG, "crc >> " + crc);
                            //************************************************************************************************************************
                            // ************************************************************************************************************************


                            Log.d(TAG, "pelak for save in data base : " + finalPelak);


                            parkbanRepository.saveEntranceRecord(
                                    getParkingInfoResponse.getDeviceId(),
                                    finalPelak,
                                    currentDateandTime,
                                    tarrifId,
                                    0,
                                    2,
                                    "",
                                    selectedDoor.getId().longValue(),
                                    selectedUser.getId().longValue(),
                                    0,
                                    new ParkbanRepository.DataBaseResultCallBack() {
                                        @Override
                                        public void onSuccess(long id) {
                                            Log.d(TAG, "onSuccess in save database , now print process begin");

                                            mBitmap = generateBitmapByLayoutForNonePayment(myContext, finalPelak);
                                            Message msg = mPrintHandler.obtainMessage(PRINT_BITMAP);
                                            msg.obj = mBitmap;
                                            msg.sendToTarget();
                                            mPrintHandler.obtainMessage(PRINT_FORWARD).sendToTarget();


                                            doRefresh();
                                        }

                                        @Override
                                        public void onFailed() {
                                            ShowToast.getInstance().showError(myContext, R.string.error_in_save_data_base);
                                        }
                                    }


                            );
                        }


                    }
                });


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


    public Bitmap generateBitmapByLayoutForNonePayment(Context context, String pelak) {

        Log.d(TAG, "generateBitmapByLayoutForNonePayment:  pelak that recived for print : " + pelak);


        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.receipt_entrance, null);
        ImageView qrImageView = view.findViewById(R.id.enter_resid_qr_image_view);
        TextView entry_date_txt = view.findViewById(R.id.enter_resid_date_time_value);
        TextView parking_name_txt = view.findViewById(R.id.enter_resid_parking_name);
        TextView paid_amount_txt = view.findViewById(R.id.enter_resid_paid_amount_value);
        TextView tariff_txt = view.findViewById(R.id.enter_resid_tariff_type_value);
        TextView plate_txt_0 = view.findViewById(R.id.p0_enter_resid);
        TextView plate_txt_1 = view.findViewById(R.id.p1_enter_resid);
        TextView plate_txt_2 = view.findViewById(R.id.p2_enter_resid);
        TextView plate_txt_3 = view.findViewById(R.id.p3_enter_resid);
        TextView m_plate_txt_0 = view.findViewById(R.id.m0_enter_resid);
        TextView m_plate_txt_1 = view.findViewById(R.id.m1_enter_resid);
        RelativeLayout pelak_main_layoout = view.findViewById(R.id.plate_main_layout_entrance_resid);
        LinearLayout motor_main_layout = view.findViewById(R.id.motor_layout_enterance_resid);
        LinearLayout car_main_layout = view.findViewById(R.id.plate_layout_entrance_resid);

        //************************************************************************************************************************
        // ************************************************************************************************************************
        Log.d(TAG, " pelak >>>  " + pelak);
        //************************************************************************************************************************
        // ************************************************************************************************************************
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
        Date dateNow = new Date();
        String currentDateandTime = sdf.format(dateNow);
        Log.d(TAG, "current-date time >>>  " + currentDateandTime);
        //************************************************************************************************************************
        // ************************************************************************************************************************
        int tarrifId = 0;
        if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff1().getVehicleName().trim())) {
            tarrifId = 1;
        } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff2().getVehicleName().trim())) {
            tarrifId = 2;
        } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff3().getVehicleName().trim())) {
            tarrifId = 3;
        } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff4().getVehicleName().trim())) {
            tarrifId = 4;
        } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff5().getVehicleName().trim())) {
            tarrifId = 5;
        } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff6().getVehicleName().trim())) {
            tarrifId = 6;
        } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff7().getVehicleName().trim())) {
            tarrifId = 7;
        } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff8().getVehicleName().trim())) {
            tarrifId = 8;
        }
        Log.d(TAG, "current-tarrif id >>>  " + tarrifId);
        //************************************************************************************************************************
        // ************************************************************************************************************************
        Log.d(TAG, "paid-entrance >>>  " + 0);
        //************************************************************************************************************************
        // ************************************************************************************************************************
        Log.d(TAG, "operator id >>>  " + selectedUser.getId());
        //************************************************************************************************************************
        // ************************************************************************************************************************
        Log.d(TAG, "door id >>>  " + selectedDoor.getId());
        //************************************************************************************************************************
        // ************************************************************************************************************************
        String crc = currentDateandTime.substring(9, 12) + "6913" + pelak.substring(0, 2);
        Log.d(TAG, "crc >> " + crc);
        //************************************************************************************************************************
        // ************************************************************************************************************************


        String content = crc + "#" + pelak.trim() + "#" + currentDateandTime + "#" + tarrifId + "#" + "0" + "#" + selectedUser.getId() + "#" + selectedDoor.getId() + "#" + "2" + "#" + " ";


        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            qrImageView.setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        String lan = PreferenceManager.getDefaultSharedPreferences(context).getString("language", "fa");

        if (lan.equals("fa")) {
            paid_amount_txt.setText("0 ریال");
            PersianDate persianDate = new PersianDate(dateNow);
            PersianDateFormat pdformater1 = new PersianDateFormat("Y/m/d");

            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.getDefault());


            String jalaliDate = pdformater1.format(persianDate) + "  " + sdf1.format(dateNow);


            entry_date_txt.setText(jalaliDate);
        } else {
            paid_amount_txt.setText("0 Rials");


            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.getDefault());


            entry_date_txt.setText(sdf1.format(dateNow));
        }


        tariff_txt.setText(getSelectedCarTypeName().getValue().trim());

        parking_name_txt.setText(getParkingInfoResponse.getParkingName().trim());

        pelak_main_layoout.setVisibility(View.VISIBLE);
        if (getCar().getValue()) {
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


        return PrinterUtils.convertViewToBitmap(view);
    }

    public Bitmap generateBitmapByLayoutForPayment(Context context, String pelak, boolean cash, String paidAmount, String rrn) {

        Log.d(TAG, "generateBitmapByLayoutForPayment:  pelak that recived for print : " + pelak);


        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.receipt_entrance, null);
        ImageView qrImageView = view.findViewById(R.id.enter_resid_qr_image_view);
        TextView entry_date_txt = view.findViewById(R.id.enter_resid_date_time_value);
        TextView parking_name_txt = view.findViewById(R.id.enter_resid_parking_name);
        TextView paid_amount_txt = view.findViewById(R.id.enter_resid_paid_amount_value);
        TextView tariff_txt = view.findViewById(R.id.enter_resid_tariff_type_value);
        TextView plate_txt_0 = view.findViewById(R.id.p0_enter_resid);
        TextView plate_txt_1 = view.findViewById(R.id.p1_enter_resid);
        TextView plate_txt_2 = view.findViewById(R.id.p2_enter_resid);
        TextView plate_txt_3 = view.findViewById(R.id.p3_enter_resid);
        TextView m_plate_txt_0 = view.findViewById(R.id.m0_enter_resid);
        TextView m_plate_txt_1 = view.findViewById(R.id.m1_enter_resid);
        RelativeLayout pelak_main_layoout = view.findViewById(R.id.plate_main_layout_entrance_resid);
        LinearLayout motor_main_layout = view.findViewById(R.id.motor_layout_enterance_resid);
        LinearLayout car_main_layout = view.findViewById(R.id.plate_layout_entrance_resid);

        //************************************************************************************************************************
        // ************************************************************************************************************************
        Log.d(TAG, " pelak >>>  " + pelak);
        //************************************************************************************************************************
        // ************************************************************************************************************************
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
        Date dateNow = new Date();
        String currentDateandTime = sdf.format(dateNow);
        Log.d(TAG, "current-date time >>>  " + currentDateandTime);
        //************************************************************************************************************************
        // ************************************************************************************************************************
        int tarrifId = 0;
        if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff1().getVehicleName().trim())) {
            tarrifId = 1;
        } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff2().getVehicleName().trim())) {
            tarrifId = 2;
        } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff3().getVehicleName().trim())) {
            tarrifId = 3;
        } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff4().getVehicleName().trim())) {
            tarrifId = 4;
        } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff5().getVehicleName().trim())) {
            tarrifId = 5;
        } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff6().getVehicleName().trim())) {
            tarrifId = 6;
        } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff7().getVehicleName().trim())) {
            tarrifId = 7;
        } else if (getSelectedCarTypeName().getValue().trim().equals(getParkingInfoResponse.getTariffs().getVehicleTariff8().getVehicleName().trim())) {
            tarrifId = 8;
        }
        Log.d(TAG, "current-tarrif id >>>  " + tarrifId);
        //************************************************************************************************************************
        // ************************************************************************************************************************
        Log.d(TAG, "paid-entrance >>>  " + getEntranceFeeFromTariffId());
        //************************************************************************************************************************
        // ************************************************************************************************************************
        Log.d(TAG, "operator id >>>  " + selectedUser.getId());
        //************************************************************************************************************************
        // ************************************************************************************************************************
        Log.d(TAG, "door id >>>  " + selectedDoor.getId());
        //************************************************************************************************************************
        // ************************************************************************************************************************
        String crc = currentDateandTime.substring(9, 12) + "6913" + pelak.substring(0, 2);
        Log.d(TAG, "crc >> " + crc);
        //************************************************************************************************************************
        // ************************************************************************************************************************


        String content = crc + "#" + pelak.trim() + "#" + currentDateandTime + "#" + tarrifId + "#" + paidAmount + "#" + selectedUser.getId() + "#" + selectedDoor.getId() + "#" + ((cash) ? "0" : "1") + "#" + rrn;


        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            qrImageView.setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        String lan = PreferenceManager.getDefaultSharedPreferences(context).getString("language", "fa");

        if (lan.equals("fa")) {
            paid_amount_txt.setText(getEntranceFeeFromTariffId() + " ریال");
            PersianDate persianDate = new PersianDate(dateNow);
            PersianDateFormat pdformater1 = new PersianDateFormat("Y/m/d");

            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.getDefault());


            String jalaliDate = pdformater1.format(persianDate) + "  " + sdf1.format(dateNow);


            entry_date_txt.setText(jalaliDate);
        } else {
            paid_amount_txt.setText(getEntranceFeeFromTariffId() + " Rials");


            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.getDefault());


            entry_date_txt.setText(sdf1.format(dateNow));
        }


        tariff_txt.setText(getSelectedCarTypeName().getValue().trim());

        parking_name_txt.setText(getParkingInfoResponse.getParkingName().trim());

        pelak_main_layoout.setVisibility(View.VISIBLE);
        if (getCar().getValue()) {
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


        return PrinterUtils.convertViewToBitmap(view);
    }
    //***********************************************************************************************
    //***********************************************************************************************
    //***********************************************************************************************


}
