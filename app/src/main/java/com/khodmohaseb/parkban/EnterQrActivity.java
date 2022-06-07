package com.khodmohaseb.parkban;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;

import com.khodmohaseb.parkban.databinding.ActivityMainBinding;
import com.khodmohaseb.parkban.helper.ShowToast;
import com.khodmohaseb.parkban.services.ReaderHandler;
import com.khodmohaseb.parkban.utils.MyBounceInterpolator;
import com.khodmohaseb.parkban.viewmodels.MainViewModel;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ir.shahaabco.ANPRNDK.anpr_create;

//public class MainActivity extends BaseActivity implements ReaderHandler.Callbacks {
public class EnterQrActivity extends BaseActivity implements ReaderHandler.Callbacks {

    private static final String TAG = "MainActivity";
    private int failedLoadLib = 0;
    private MainViewModel viewModel;
    private boolean doubleBackToExitPressedOnce = false;

    //**********************************************************************************************
    //**********************************************************************************************
    private Intent readerHandlerIntent;
    private ReaderHandler readerHandler;

    private ServiceConnection readerHandlerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            ReaderHandler.LocalBinder binder = (ReaderHandler.LocalBinder) service;
            readerHandler = binder.getServiceInstance();
            readerHandler.registerClient(EnterQrActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
    //**********************************************************************************************
    //**********************************************************************************************

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
        }
        System.loadLibrary("anpr_ndk");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        copyAssets();
        CreateANPR();
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        binding.setViewModel(viewModel);
        EditText etxtCar = findViewById(R.id.etxt_car_plate_first_cell);
        EditText etxtMotor = findViewById(R.id.etxt_motor_plate_first_cell);
        viewModel.init(this, etxtCar, etxtMotor);
        viewModel.getHasPelak().setValue(true);
        viewModel.getHasMemberCode().setValue(true);
        viewModel.getHasCardNumbeer().setValue(true);
        binding.setLifecycleOwner(this);
        viewModel.mSpinner = binding.spinner;
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.getPlate__1().setValue(String.valueOf(binding.spinner.getSelectedItem()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.spinner.setSelection(0);
                viewModel.getPlate__1().setValue(String.valueOf(binding.spinner.getSelectedItem()));
            }
        });
        binding.etxtCarPlateThirdCell.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 3) {
                    binding.etxtCarPlateThirdCell.clearFocus();
                    binding.etxtCarPlateForthCell.requestFocus();
                    binding.etxtCarPlateForthCell.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etxtMotorPlateFirstCell.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 3) {
                    binding.etxtMotorPlateFirstCell.clearFocus();
                    binding.etxtMotorPlateSecondCell.requestFocus();
                    binding.etxtMotorPlateSecondCell.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etxtCarPlateForthCell.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    binding.etxtCarPlateForthCell.clearFocus();
                    binding.etxtCarPlateThirdCell.requestFocus();
                    binding.etxtCarPlateThirdCell.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etxtMotorPlateSecondCell.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    binding.etxtMotorPlateSecondCell.clearFocus();
                    binding.etxtMotorPlateFirstCell.requestFocus();
                    binding.etxtMotorPlateFirstCell.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if (getIntent().getBooleanExtra("isfromqr", false)) {
            viewModel.getHasCardNumbeer().setValue(true);
            viewModel.getHasMemberCode().setValue(true);
            viewModel.getHasPelak().setValue(true);
            String General_Qr_Code_String = getIntent().getStringExtra("scanned_string");
            Log.d(TAG, "General_Qr_Code_String_scanned >>>>>>>>>>>>>>>> " + General_Qr_Code_String);
            if (General_Qr_Code_String.equals("") || General_Qr_Code_String.equals(null)) {
                viewModel.getOzv_code_string().setValue("");
                viewModel.getCard_code_string().setValue("");
                viewModel.getCar().setValue(true);
                viewModel.getMotor().setValue(false);
                viewModel.getPlate__0().setValue("");
                viewModel.getPlate__2().setValue("");
                viewModel.getPlate__3().setValue("");
                viewModel.getEdit_text_imageview_ozv_status().setValue(false);
                viewModel.getEdit_text_imageview_kart_status().setValue(false);
                ShowToast.getInstance().showWarning(this, R.string.qr_null);
            } else {
                if (General_Qr_Code_String.trim().equals("backback")) {
                } else {
                    viewModel.getEdit_text_imageview_ozv_status().setValue(true);
                    viewModel.getEdit_text_imageview_kart_status().setValue(true);
                    try {
//                        !@^#1380130265321###042911022#62339!@^
//                        !@^###4834388266#64909!@^
                        String actualStringFromQrcode = General_Qr_Code_String.substring(3, General_Qr_Code_String.length() - 3);
                        Log.d(TAG, "actualStringFromQrcode >>> " + actualStringFromQrcode);
                        int crc = Integer.parseInt(actualStringFromQrcode.charAt(3) + "");
                        int calculatedCrc = 0;
                        int sumForCalculatedCrc = 0;
                        String actualStringFromQrcodeWithOutSharp = actualStringFromQrcode.replace("#", "").trim();
                        Log.d(TAG, "actualStringFromQrcodeWithOutSharp >>> " + actualStringFromQrcodeWithOutSharp);
                        for (int i = 0; i < actualStringFromQrcodeWithOutSharp.length(); i++) {
                            Log.d(TAG, "" + Integer.parseInt(actualStringFromQrcodeWithOutSharp.charAt(i) + ""));
                            sumForCalculatedCrc = sumForCalculatedCrc + Integer.parseInt(actualStringFromQrcodeWithOutSharp.charAt(i) + "");
                        }
                        Log.d(TAG, "sumForCalculatedCrc befor minus crc >>> " + sumForCalculatedCrc);
                        sumForCalculatedCrc = sumForCalculatedCrc - Integer.parseInt(actualStringFromQrcode.charAt(3) + "");
                        Log.d(TAG, "sumForCalculatedCrc >>> " + sumForCalculatedCrc);
                        calculatedCrc = (sumForCalculatedCrc % 9);
                        Log.d(TAG, "calculatedCrc >>> " + calculatedCrc);
                        Log.d(TAG, "Crc >>> " + crc);
                        if (calculatedCrc == crc) {
                            Log.d(TAG, "crc is correct");
                        } else {
                            Log.d(TAG, "crc is in-correct");
                            throw new Exception();
                        }
//
                        StringBuffer stringBuffer1 = new StringBuffer(actualStringFromQrcode);
                        String acutalStringFromQrCodeWithOutCrc = stringBuffer1.replace(3, 4, "").toString().trim();
//                          #130130265321
//                          #
//                          #
//                          #042911022
//                          #62339
                        Log.d(TAG, "acutalStringFromQrCodeWithOutCrc >>>" + acutalStringFromQrCodeWithOutCrc);
                        List<String> listString = new ArrayList<String>(Arrays.asList(acutalStringFromQrCodeWithOutCrc.split("#")));



                        if(listString.size()==6){

                            String reverse_id = listString.get(5);
                            Log.d(TAG, "reverse_id >>> " + reverse_id);
                            String reverse_pelak_pure = listString.get(4);
                            Log.d(TAG, "reverse_pelak_pure >>> " + reverse_pelak_pure);
                            String reverse_ozv = listString.get(3);
                            Log.d(TAG, "reverse_ozv >>> " + reverse_ozv);
                            String cardCrc = listString.get(2);
                            Log.d(TAG, "cardCrc >>> " + cardCrc);
                            String reverse_card = null;
                            if (cardCrc.equals("")) {
                                reverse_card = "";
                            } else {
//                            reverse_card = cardCrc.substring(0, 3) + cardCrc.substring(4, cardCrc.length());
                                reverse_card = cardCrc;
                            }
                            Log.d(TAG, "reverse_card >>> " + reverse_card);
                            String id = new StringBuilder(reverse_id).reverse().toString();
                            String pelak_pure = new StringBuilder(reverse_pelak_pure).reverse().toString();
                            String ozv = new StringBuilder(reverse_ozv).reverse().toString();
                            String card = new StringBuilder(reverse_card).reverse().toString();
                            viewModel.getOzv_code_string().setValue(ozv);
                            viewModel.getCard_code_string().setValue(card);
                            boolean isCar;
                            if (pelak_pure.length() == 8) {
                                isCar = false;
                            } else {
                                isCar = true;
                            }
                            Log.d(TAG, "pure_pelak with original enum >>> " + pelak_pure);
                            String motor_pelak_pure = pelak_pure;
                            Log.d(TAG, "original enum" + pelak_pure.substring(2, 4));
                            String actualPurePelak = null;
                            StringBuffer stringBuffer;
                            switch (pelak_pure.substring(2, 4)) {
                                case "00":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "الف").toString();
                                    break;
                                case "01":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ب").toString();
                                    break;
                                case "02":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "پ").toString();
                                    break;
                                case "03":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ت").toString();
                                    break;
                                case "04":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ث").toString();
                                    break;
                                case "05":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ج").toString();
                                    break;
                                case "06":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "چ").toString();
                                    break;
                                case "07":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ح").toString();
                                    break;
                                case "08":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "خ").toString();
                                    break;
                                case "09":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "د").toString();
                                    break;
                                case "10":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ذ").toString();
                                    break;
                                case "11":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ر").toString();
                                    break;
                                case "12":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ز").toString();
                                    break;
                                case "13":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ژ").toString();
                                    break;
                                case "14":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "س").toString();
                                    break;
                                case "15":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ش").toString();
                                    break;
                                case "16":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ص").toString();
                                    break;
                                case "17":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ض").toString();
                                    break;
                                case "18":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ط").toString();
                                    break;
                                case "19":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ظ").toString();
                                    break;
                                case "20":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ع").toString();
                                    break;
                                case "21":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "غ").toString();
                                    break;
                                case "22":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ف").toString();
                                    break;
                                case "23":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ق").toString();
                                    break;
                                case "24":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ک").toString();
                                    break;
                                case "25":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "گ").toString();
                                    break;
                                case "26":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ل").toString();
                                    break;
                                case "27":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "م").toString();
                                    break;
                                case "28":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ن").toString();
                                    break;
                                case "29":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "و").toString();
                                    break;
                                case "30":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ﻫ").toString();
                                    break;
                                case "31":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ی").toString();
                                    break;
                                case "32":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "♿").toString();
                                    break;
                                case "33":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "D").toString();
                                    break;
                                case "34":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "S").toString();
                                    break;
                            }
                            Log.d(TAG, "actual plate >>> " + actualPurePelak);
                            if (isCar) {
                                //car
                                viewModel.getCar().setValue(true);
                                viewModel.getMotor().setValue(false);
                                String plate0 = actualPurePelak.substring(0, 2);
                                viewModel.getPlate__0().setValue(plate0);
                                String plate2 =actualPurePelak.substring(actualPurePelak.length() - 5, actualPurePelak.length() - 2);
                                viewModel.getPlate__2().setValue(plate2);
                                String plate3=actualPurePelak.substring(actualPurePelak.length() - 2, actualPurePelak.length());
                                viewModel.getPlate__3().setValue(plate3);





                                if (actualPurePelak.length() > 8) {
                                    //الف pelak
                                    String plate1 =actualPurePelak.substring(2, 5);
                                            viewModel.getPlate__1().setValue(plate1);
                                } else {
                                    //بدون الف
                                    String plate1 =actualPurePelak.substring(2, 3);
                                            viewModel.getPlate__1().setValue(plate1);
                                }





                                String[] alphabetArray = getResources().getStringArray(R.array.image_array);
                                for (int i = 1; i < alphabetArray.length; i++) {
                                    if (alphabetArray[i].equals(viewModel.getPlate__1().getValue()))
                                        binding.spinner.setSelection(i);
                                }
                                Log.d(TAG, "car?????????????????????????????????????????????????????");
                            } else {
                                //motor
                                viewModel.getCar().setValue(false);
                                viewModel.getMotor().setValue(true);
                                viewModel.getMplate__0().setValue(motor_pelak_pure.substring(0, 3));
                                viewModel.getMplate__1().setValue(motor_pelak_pure.substring(3));
                                Log.d(TAG, "motor?????????????????????????????????????????????????????");
                            }












                        }else{
                            String reverse_id = listString.get(4);
                            Log.d(TAG, "reverse_id >>> " + reverse_id);
                            String reverse_pelak_pure = listString.get(3);
                            Log.d(TAG, "reverse_pelak_pure >>> " + reverse_pelak_pure);
                            String reverse_ozv = listString.get(2);
                            Log.d(TAG, "reverse_ozv >>> " + reverse_ozv);
                            String cardCrc = listString.get(1);
                            Log.d(TAG, "cardCrc >>> " + cardCrc);
                            String reverse_card = null;
                            if (cardCrc.equals("")) {
                                reverse_card = "";
                            } else {
//                            reverse_card = cardCrc.substring(0, 3) + cardCrc.substring(4, cardCrc.length());
                                reverse_card = cardCrc;
                            }
                            Log.d(TAG, "reverse_card >>> " + reverse_card);
                            String id = new StringBuilder(reverse_id).reverse().toString();
                            String pelak_pure = new StringBuilder(reverse_pelak_pure).reverse().toString();
                            String ozv = new StringBuilder(reverse_ozv).reverse().toString();
                            String card = new StringBuilder(reverse_card).reverse().toString();
                            viewModel.getOzv_code_string().setValue(ozv);
                            viewModel.getCard_code_string().setValue(card);
                            boolean isCar;
                            if (pelak_pure.length() == 8) {
                                isCar = false;
                            } else {
                                isCar = true;
                            }
                            Log.d(TAG, "pure_pelak with original enum >>> " + pelak_pure);
                            String motor_pelak_pure = pelak_pure;
                            Log.d(TAG, "original enum" + pelak_pure.substring(2, 4));
                            String actualPurePelak = null;
                            StringBuffer stringBuffer;
                            switch (pelak_pure.substring(2, 4)) {
                                case "00":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "الف").toString();
                                    break;
                                case "01":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ب").toString();
                                    break;
                                case "02":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "پ").toString();
                                    break;
                                case "03":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ت").toString();
                                    break;
                                case "04":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ث").toString();
                                    break;
                                case "05":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ج").toString();
                                    break;
                                case "06":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "چ").toString();
                                    break;
                                case "07":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ح").toString();
                                    break;
                                case "08":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "خ").toString();
                                    break;
                                case "09":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "د").toString();
                                    break;
                                case "10":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ذ").toString();
                                    break;
                                case "11":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ر").toString();
                                    break;
                                case "12":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ز").toString();
                                    break;
                                case "13":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ژ").toString();
                                    break;
                                case "14":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "س").toString();
                                    break;
                                case "15":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ش").toString();
                                    break;
                                case "16":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ص").toString();
                                    break;
                                case "17":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ض").toString();
                                    break;
                                case "18":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ط").toString();
                                    break;
                                case "19":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ظ").toString();
                                    break;
                                case "20":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ع").toString();
                                    break;
                                case "21":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "غ").toString();
                                    break;
                                case "22":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ف").toString();
                                    break;
                                case "23":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ق").toString();
                                    break;
                                case "24":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ک").toString();
                                    break;
                                case "25":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "گ").toString();
                                    break;
                                case "26":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ل").toString();
                                    break;
                                case "27":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "م").toString();
                                    break;
                                case "28":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ن").toString();
                                    break;
                                case "29":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "و").toString();
                                    break;
                                case "30":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ه").toString();
                                    break;
                                case "31":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "ی").toString();
                                    break;
                                case "32":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "♿").toString();
                                    break;
                                case "33":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "D").toString();
                                    break;
                                case "34":
                                    stringBuffer = new StringBuffer(pelak_pure);
                                    actualPurePelak = stringBuffer.replace(2, 4, "S").toString();
                                    break;
                            }
                            Log.d(TAG, "actual plate >>> " + actualPurePelak);
                            if (isCar) {
                                //car
                                viewModel.getCar().setValue(true);
                                viewModel.getMotor().setValue(false);
                                viewModel.getPlate__0().setValue(actualPurePelak.substring(0, 2));
                                viewModel.getPlate__2().setValue(actualPurePelak.substring(actualPurePelak.length() - 5, actualPurePelak.length() - 2));
                                viewModel.getPlate__3().setValue(actualPurePelak.substring(actualPurePelak.length() - 2, actualPurePelak.length()));
                                if (actualPurePelak.length() > 8) {
                                    //الف pelak
                                    viewModel.getPlate__1().setValue(actualPurePelak.substring(2, 5));
                                } else {
                                    //بدون الف
                                    viewModel.getPlate__1().setValue(actualPurePelak.substring(2, 3));
                                }
                                String[] alphabetArray = getResources().getStringArray(R.array.image_array);
                                for (int i = 1; i < alphabetArray.length; i++) {
                                    if (alphabetArray[i].equals(viewModel.getPlate__1().getValue()))
                                        binding.spinner.setSelection(i);
                                }
                                Log.d(TAG, "car?????????????????????????????????????????????????????");
                            } else {
                                //motor
                                viewModel.getCar().setValue(false);
                                viewModel.getMotor().setValue(true);
                                viewModel.getMplate__0().setValue(motor_pelak_pure.substring(0, 3));
                                viewModel.getMplate__1().setValue(motor_pelak_pure.substring(3));
                                Log.d(TAG, "motor?????????????????????????????????????????????????????");
                            }
                        }




















                    } catch (Exception e) {
                        ShowToast.getInstance().showWarning(this, R.string.error_mifare);
                        return;
                    }
                    ShowToast.getInstance().showSuccess(this, R.string.ok_mifare);
                    final Animation myAnim = AnimationUtils.loadAnimation(EnterQrActivity.this, R.anim.btn_bubble_animation);
                    MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                    myAnim.setInterpolator(interpolator);
                    binding.etxtMainShomareKart.startAnimation(myAnim);
                    binding.etxtMainShomareOzv.startAnimation(myAnim);
                }
            }
        }
//        if (getIntent().getBooleanExtra("isfrommifare", false)) {
//            viewModel.getHasCardNumbeer().setValue(true);
//            viewModel.getHasMemberCode().setValue(true);
//            viewModel.getHasPelak().setValue(true);
//            String General_Mifare_Code_String = getIntent().getStringExtra("scanned_string");
//            Log.d(TAG, "General_Mifare_Code_String_scanned >>>>>>>>>>>>>>>> " + General_Mifare_Code_String);
//            if (General_Mifare_Code_String.equals("") || General_Mifare_Code_String.equals(null)) {
//                ShowToast.getInstance().showWarning(this, R.string.error_mifare);
//                viewModel.getEdit_text_imageview_ozv_status().setValue(false);
//                viewModel.getEdit_text_imageview_kart_status().setValue(false);
//                viewModel.getOzv_code_string().setValue("");
//                viewModel.getCard_code_string().setValue("");
//                viewModel.getCar().setValue(true);
//                viewModel.getMotor().setValue(false);
//                viewModel.getPlate__0().setValue("");
//                viewModel.getPlate__2().setValue("");
//                viewModel.getPlate__3().setValue("");
//                binding.spinner.setSelection(0);
//            } else {
//                if (General_Mifare_Code_String.equals("backback")) {
//                } else {
//                    ShowToast.getInstance().showSuccess(this, R.string.ok_mifare);
//                    viewModel.getEdit_text_imageview_ozv_status().setValue(false);
//                    viewModel.getEdit_text_imageview_kart_status().setValue(false);
//                    binding.etxtMainShomareKart.requestFocus();
//                    String card = General_Mifare_Code_String;
//                    viewModel.getPlate__0().setValue("");
//                    viewModel.getPlate__2().setValue("");
//                    viewModel.getPlate__3().setValue("");
//                    binding.spinner.setSelection(0);
//                    viewModel.getMplate__1().setValue("");
//                    viewModel.getMplate__0().setValue("");
//                    viewModel.getCard_code_string().setValue(card);
//                    viewModel.getOzv_code_string().setValue("");
//                    final Animation myAnim = AnimationUtils.loadAnimation(com.safshekan.parkban.MainActivity.this, R.anim.btn_bubble_animation);
//                    MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
//                    myAnim.setInterpolator(interpolator);
//                    binding.etxtMainShomareKart.startAnimation(myAnim);
//                }
//            }
//        }
        viewModel.getProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer value) {
                if (value > 0) {
                    showProgress(true);
                } else {
                    showProgress(false);
                }
            }
        });
    }
    //**********************************************************************************************
    //**********************************************************************************************

    private void startServices() {
        try {
            readerHandlerIntent = new Intent(getApplicationContext(), ReaderHandler.class);
            startService(readerHandlerIntent);
            getApplicationContext().bindService(readerHandlerIntent, readerHandlerConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception ex) {
            Log.d(TAG, "MainActivity > startServices():" + ex.getMessage());
        }
    }

    private void stopServices() {
        try {
            //Try to stop Reader handler
            if (ReaderHandler.getInstance() != null)
                ReaderHandler.getInstance().stopSelf();
        } catch (Exception ex) {
            Log.d(TAG, "MainActivity > stopServices():" + ex.getMessage());
        }
    }

    public void stopServicesForOperator() {
        try {
            getApplicationContext().unbindService(readerHandlerConnection);
            stopService(readerHandlerIntent);
        } catch (Exception ex) {
            Log.d(TAG, "MainActivity > stopServicesForOperator():" + ex.getMessage());
        }
    }
    //**********************************************************************************************
    //**********************************************************************************************

    @Override
    protected void onResume() {
        super.onResume();
        //******************************************************************************************
        //******************************************************************************************
        startServices();
        //******************************************************************************************
        //******************************************************************************************
        if (PreferenceManager.getDefaultSharedPreferences(EnterQrActivity.this).getBoolean("comFmPardakhtDialog", false)) {
            PreferenceManager.getDefaultSharedPreferences(EnterQrActivity.this).edit().putBoolean("comFmPardakhtDialog", false).apply();
            //do refresh
            viewModel.doRefresh();
        }
    }

    @Override
    public void onBackPressed() {
        viewModel.backPress(EnterQrActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.processActivityResult(this, requestCode, resultCode, data);
    }

    private void CreateANPR() {
        try {
            File dir = getApplicationContext().getExternalFilesDir(null);
            String mcs_path = dir.getPath();// + "/IRP.mcs";
            short result = anpr_create((byte) 0, mcs_path, "09361392929", (byte) 0);
            if (result >= 0)
                Log.i(TAG, "ANPR Lib Initialized Correctly");
            else {
                failedLoadLib += 1;
                System.loadLibrary("anpr_ndk");
                CreateANPR();
                if (failedLoadLib > 3) {
                    ShowToast.getInstance().showError(this, R.string.anpr_not_load);
                }
            }
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File dir = getExternalFilesDir(null);
                File outFile = new File(dir, filename);
                if (!outFile.exists()) {
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                }
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //******************************************************************************************
        //******************************************************************************************
        stopServices();
        //******************************************************************************************
        //******************************************************************************************
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopServicesForOperator();
    }

    @Override
    public void showOnUi(final String serialNumber) {
        Log.d(TAG, "showOnUi has been called  , actual serial number>> " + serialNumber);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewModel.getCard_code_string().setValue(serialNumber);
                viewModel.getEdit_text_imageview_kart_status().setValue(true);
            }
        });
    }
}
