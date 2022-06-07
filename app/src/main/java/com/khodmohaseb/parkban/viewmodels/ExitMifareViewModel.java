package com.khodmohaseb.parkban.viewmodels;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

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
import com.khodmohaseb.parkban.helper.ShowToast;
import com.khodmohaseb.parkban.persistence.ParkbanDatabase;
import com.khodmohaseb.parkban.persistence.models.CarPlate;
import com.khodmohaseb.parkban.persistence.models.ResponseResultType;
import com.khodmohaseb.parkban.repositories.ParkbanRepository;
import com.khodmohaseb.parkban.services.dto.ExitBillDto;
import com.khodmohaseb.parkban.utils.Animation_Constant;
import com.khodmohaseb.parkban.utils.MyBounceInterpolator;
import com.pax.dal.IDAL;
import com.pax.dal.IPrinter;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class ExitMifareViewModel extends ViewModel {

    public static final String TAG = "MainViewModel";
    private static final String MEDIA_FILE_TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";
    private MutableLiveData<Boolean> car;
    private MutableLiveData<Boolean> motor;
    private MutableLiveData<Boolean> edit_text_imageview_ozv_status;
    private MutableLiveData<Boolean> edit_text_imageview_kart_status;
    private MutableLiveData<Boolean> hasPelak;
    private MutableLiveData<Boolean> hasMemberCode;
    private MutableLiveData<Boolean> hasCardNumbeer;
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
    public Spinner mSpinner;
    private IPrinter iPrinter;
    private MutableLiveData<String> ozv_code_string;
    private MutableLiveData<String> card_code_string;
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

    public MutableLiveData<Boolean> getHasMemberCode() {
        if (hasMemberCode == null)
            hasMemberCode = new MutableLiveData<>();
        return hasMemberCode;
    }

    public MutableLiveData<Boolean> getHasCardNumbeer() {
        if (hasCardNumbeer == null)
            hasCardNumbeer = new MutableLiveData<>();
        return hasCardNumbeer;
    }

    public MutableLiveData<String> getOzv_code_string() {
        if (ozv_code_string == null)
            ozv_code_string = new MutableLiveData<>();
        return ozv_code_string;
    }

    public MutableLiveData<String> getCard_code_string() {
        if (card_code_string == null)
            card_code_string = new MutableLiveData<>();
        return card_code_string;
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

    public MutableLiveData<Boolean> getEdit_text_imageview_kart_status() {
        if (edit_text_imageview_kart_status == null)
            edit_text_imageview_kart_status = new MutableLiveData<>();
        return edit_text_imageview_kart_status;
    }

    public MutableLiveData<Boolean> getEdit_text_imageview_ozv_status() {
        if (edit_text_imageview_ozv_status == null)
            edit_text_imageview_ozv_status = new MutableLiveData<>();
        return edit_text_imageview_ozv_status;
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

    public void init(final Context context, EditText editTextCar, EditText editTextMotor) {
        etxt_first_cell_car_plate = editTextCar;
        etxt_first_cell_motor_plate = editTextMotor;
        myContext = context;
        if (car == null)
            car = new MutableLiveData<>();
        car.setValue(true);
        if (motor == null)
            motor = new MutableLiveData<>();
        motor.setValue(false);
        if (edit_text_imageview_ozv_status == null)
            edit_text_imageview_ozv_status = new MutableLiveData<>();
        edit_text_imageview_ozv_status.setValue(false);
        if (edit_text_imageview_kart_status == null)
            edit_text_imageview_kart_status = new MutableLiveData<>();
        edit_text_imageview_kart_status.setValue(false);
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
        if (ozv_code_string == null)
            ozv_code_string = new MutableLiveData<>();
        ozv_code_string.setValue("");
        if (card_code_string == null)
            card_code_string = new MutableLiveData<>();
        card_code_string.setValue("");
        try {
            idal = PrintParkbanApp.getInstance().getIdal();
            if (idal != null)
                iPrinter = idal.getPrinter();
        } catch (Exception e) {
        }
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
                            photoURI = FileProvider.getUriForFile(myContext, "com.safshekan.parkban", photoFile);
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

    public void processActivityResult(Context context, int requestCode, int resultCode, Intent
            data) {
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

    public void Card_Khan_Onclick(View view) {
        view.clearFocus();
        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_bubble_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(myContext, MifareCardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                myContext.startActivity(i);
            }
        }, Animation_Constant.ANIMATION_VALUE);
    }

    public void Gozaresh_Onclick(View view) {
        view.clearFocus();
        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_bubble_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(myContext, ReportActivity.class);
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
            getEdit_text_imageview_ozv_status().setValue(false);
            getEdit_text_imageview_kart_status().setValue(false);
            ImageView imageView = (ImageView) view;
            imageView.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.motor_256));
        } else {
            getCar().setValue(true);
            getMotor().setValue(false);
            etxt_first_cell_car_plate.requestFocus();
            getEdit_text_imageview_ozv_status().setValue(false);
            getEdit_text_imageview_kart_status().setValue(false);
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
                boolean is_pelak_empty = false;
                if (getCar().getValue()) {
                    //test car plate
                    if (getPlate__0().getValue().equals("") || getPlate__0().getValue().equals(null))
                        is_pelak_empty = true;
                    if (getPlate__2().getValue().equals("") || getPlate__2().getValue().equals(null))
                        is_pelak_empty = true;
                    if (getPlate__3().getValue().equals("") || getPlate__3().getValue().equals(null))
                        is_pelak_empty = true;
                } else {
                    //test motor plate
                    if (getMplate__0().getValue().equals("") || getMplate__0().getValue().equals(null))
                        is_pelak_empty = true;
                    if (getMplate__1().getValue().equals("") || getMplate__1().getValue().equals(null))
                        is_pelak_empty = true;
                }
                boolean is_membercode_empty = false;
                if (getOzv_code_string().getValue().isEmpty())
                    is_membercode_empty = true;
                boolean is_cardcode_empty = false;
                if ((getCard_code_string().getValue().equals("")) || (getCard_code_string().getValue().equals(null)))
                    is_cardcode_empty = true;
                Log.d(TAG, "pelak status >>> " + is_pelak_empty);
                Log.d(TAG, "member number status >>> " + is_membercode_empty);
                Log.d(TAG, "card number status >>> " + is_cardcode_empty);
                if (is_pelak_empty && is_membercode_empty && is_cardcode_empty) {
                    ShowToast.getInstance().showWarning(view.getContext(), R.string.at_least_one_parameter_requierd);
                    return;
                }
                //************************************************************************************************************************
                //************************************************************************************************************************
                String pelak = "";
                progress.setValue(10);
                if (getCar().getValue()) {
                    if (!is_pelak_empty) {
                        pelak = getPlate__0().getValue() + getPlate__1().getValue() + getPlate__2().getValue() + getPlate__3().getValue();
                    }
                } else {
                    if (!is_pelak_empty) {
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
                //************************************************************************************************************************
                String ozv_code = "";
                ozv_code = getOzv_code_string().getValue();
                Log.d(TAG, "UnChanged-Default ozv_code >>>  " + ozv_code);
                StringBuilder newOzv_Code = new StringBuilder(ozv_code);
                for (int i = 0; i < ozv_code.length(); i++) {
                    if (Character.isDigit(ozv_code.charAt(i))) {
                        switch (ozv_code.charAt(i)) {
                            case '۰':
                                newOzv_Code.setCharAt(i, '0');
                                break;
                            case '۱':
                                newOzv_Code.setCharAt(i, '1');
                                break;
                            case '۲':
                                newOzv_Code.setCharAt(i, '2');
                                break;
                            case '۳':
                                newOzv_Code.setCharAt(i, '3');
                                break;
                            case '۴':
                                newOzv_Code.setCharAt(i, '4');
                                break;
                            case '۵':
                                newOzv_Code.setCharAt(i, '5');
                                break;
                            case '۶':
                                newOzv_Code.setCharAt(i, '6');
                                break;
                            case '۷':
                                newOzv_Code.setCharAt(i, '7');
                                break;
                            case '۸':
                                newOzv_Code.setCharAt(i, '8');
                                break;
                            case '۹':
                                newOzv_Code.setCharAt(i, '9');
                                break;
                        }
                    }
                }
                ozv_code = newOzv_Code.toString();
                Log.d(TAG, "Changed-New ozv_code >>>  " + ozv_code);
                //************************************************************************************************************************
                //************************************************************************************************************************
                String card_code = "";
                card_code = getCard_code_string().getValue();
                Log.d(TAG, "UnChanged-Default card_code >>>  " + card_code);
                StringBuilder newCard_code = new StringBuilder(card_code);
                for (int i = 0; i < card_code.length(); i++) {
                    if (Character.isDigit(card_code.charAt(i))) {
                        switch (card_code.charAt(i)) {
                            case '۰':
                                newCard_code.setCharAt(i, '0');
                                break;
                            case '۱':
                                newCard_code.setCharAt(i, '1');
                                break;
                            case '۲':
                                newCard_code.setCharAt(i, '2');
                                break;
                            case '۳':
                                newCard_code.setCharAt(i, '3');
                                break;
                            case '۴':
                                newCard_code.setCharAt(i, '4');
                                break;
                            case '۵':
                                newCard_code.setCharAt(i, '5');
                                break;
                            case '۶':
                                newCard_code.setCharAt(i, '6');
                                break;
                            case '۷':
                                newCard_code.setCharAt(i, '7');
                                break;
                            case '۸':
                                newCard_code.setCharAt(i, '8');
                                break;
                            case '۹':
                                newCard_code.setCharAt(i, '9');
                                break;
                        }
                    }
                }
                card_code = newCard_code.toString();
                Log.d(TAG, "Changed-New card_code >>>  " + card_code);
                //************************************************************************************************************************
                //************************************************************************************************************************
                parkbanRepository.sendBasicInformation(pelak, ozv_code, card_code, new ParkbanRepository.ServiceResultCallBack<ExitBillDto.ExitBillDtoResponse>() {
                    @Override
                    public void onSuccess(ExitBillDto.ExitBillDtoResponse result) {
                        progress.setValue(0);
                        if (!result.equals(null)) {
                            Intent intent = new Intent(myContext, PaymentSafshekanActivity.class);
                            intent.putExtra("isfromtaeed", true);
                            intent.putExtra("CommonCost", result.getCommonCost()); // long
                            intent.putExtra("SolarEnterDateTime", result.getSolarEnterDateTime()); //string
                            intent.putExtra("FormatedDuration", result.getFormatedDuration()); // string
                            intent.putExtra("AbsolutCarPlate", result.getAbsolutCarPlate()); // string
                            intent.putExtra("DumpId", result.getDumpId());
                            if (result.getMemberCode() == null) {
                                intent.putExtra("MemberCode", ""); // string
                            } else {
                                intent.putExtra("MemberCode", result.getMemberCode()); // string
                            }
                            if (result.getCardNumber() == null) {
                                intent.putExtra("CardNumber", ""); // string
                            } else {
                                intent.putExtra("CardNumber", result.getCardNumber()); // string
                            }
                            myContext.startActivity(intent);
                        } else {
                            ShowToast.getInstance().showError(myContext, R.string.connection_failed);
                        }
                    }

                    @Override
                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                        progress.setValue(0);
                        switch (resultType) {
                            case NotFoundEntry:
                                ShowToast.getInstance().showError(myContext, R.string.not_entry);
                                break;
                            case RetrofitError:
                                ShowToast.getInstance().showError(myContext, R.string.exception_msg);
                                // Messenger.showErrorMessage(view.getContext(), R.string.exception_msg);
                                break;
                            case ServerError:
                                if (errorCode != 0)
                                    ShowToast.getInstance().showError(myContext, errorCode);
                                else {
                                    ShowToast.getInstance().showError(myContext, R.string.connection_failed);
                                    Log.d("xhxhxh", "resultType>>" + resultType.getValue() + resultType.getDescription());
                                    Log.d("xhxhxh", "message>>" + message);
                                    Log.d("xhxhxh", "errorcode>>" + errorCode);
                                }
                                break;
                            default:
                                ShowToast.getInstance().showError(myContext, resultType.ordinal());
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

    public View.OnFocusChangeListener getOnFocusChangeListener() {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocussed) {
                if (isFocussed) {
                    EditText editText = (EditText) view;
                    final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_bubble_animation);
                    MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                    myAnim.setInterpolator(interpolator);
                    switch (editText.getId()) {
                        case R.id.etxt_main_shomare_ozv:
                            getEdit_text_imageview_ozv_status().setValue(true);
                            getEdit_text_imageview_kart_status().setValue(false);
                            view.startAnimation(myAnim);
                            break;
                        case R.id.etxt_main_shomare_kart:
                            getEdit_text_imageview_ozv_status().setValue(false);
                            getEdit_text_imageview_kart_status().setValue(true);
                            view.startAnimation(myAnim);
                            break;
                        case R.id.etxt_car_plate_first_cell:
                            getEdit_text_imageview_ozv_status().setValue(false);
                            getEdit_text_imageview_kart_status().setValue(false);
                            break;
                        case R.id.etxt_car_plate_third_cell:
                            getEdit_text_imageview_ozv_status().setValue(false);
                            getEdit_text_imageview_kart_status().setValue(false);
                            break;
                        case R.id.etxt_car_plate_forth_cell:
                            getEdit_text_imageview_ozv_status().setValue(false);
                            getEdit_text_imageview_kart_status().setValue(false);
                            break;
                        case R.id.etxt_motor_plate_first_cell:
                            getEdit_text_imageview_ozv_status().setValue(false);
                            getEdit_text_imageview_kart_status().setValue(false);
                            break;
                        case R.id.etxt_motor_plate_second_cell:
                            getEdit_text_imageview_ozv_status().setValue(false);
                            getEdit_text_imageview_kart_status().setValue(false);
                            break;
                    }
                }
            }
        };
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
        ozv_code_string.setValue("");
        card_code_string.setValue("");
        car.setValue(true);
        motor.setValue(false);
        mSpinner.setSelection(0);
        edit_text_imageview_ozv_status.setValue(false);
        getEdit_text_imageview_kart_status().setValue(false);
    }
}
