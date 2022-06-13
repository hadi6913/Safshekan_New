package com.khodmohaseb.parkban.viewmodels;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.gson.Gson;
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
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.Door;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.GetParkingInfoResponse;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.Operator;
import com.khodmohaseb.parkban.utils.Animation_Constant;
import com.khodmohaseb.parkban.utils.MyBounceInterpolator;
import com.pax.dal.IDAL;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EnterQrViewModel extends ViewModel {

    public static final String TAG = "EnterQrViewModel";


    private GetParkingInfoResponse getParkingInfoResponse;
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
    public MutableLiveData<String> getSelectedCarTypeName() {
        if (selectedCarTypeName == null)
            selectedCarTypeName = new MutableLiveData<>();
        return selectedCarTypeName;
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

    public void init(final Context context, EditText editTextCar, EditText editTextMotor) {
        etxt_first_cell_car_plate = editTextCar;
        etxt_first_cell_motor_plate = editTextMotor;
        myContext = context;


        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json_parkingInfo = preferences.getString("parkinginfo", "");
        if (json_parkingInfo.equals("")) {
            getParkingInfoResponse = null;
        } else {
            getParkingInfoResponse = gson.fromJson(json_parkingInfo, GetParkingInfoResponse.class);
        }


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
}
