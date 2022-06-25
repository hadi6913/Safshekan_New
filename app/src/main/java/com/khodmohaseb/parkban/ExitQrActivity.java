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

import com.khodmohaseb.parkban.databinding.ActivityExitQrBinding;
import com.khodmohaseb.parkban.databinding.ActivityMainBinding;
import com.khodmohaseb.parkban.helper.ShowToast;
import com.khodmohaseb.parkban.utils.MyBounceInterpolator;
import com.khodmohaseb.parkban.viewmodels.ExitQrViewModel;
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


public class ExitQrActivity extends BaseActivity {

    private static final String TAG = "ExitQrActivity";
    private int failedLoadLib = 0;
    private ExitQrViewModel exitQrViewModel;
    private boolean doubleBackToExitPressedOnce = false;


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
        final ActivityExitQrBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_exit_qr);
        copyAssets();
        CreateANPR();
        exitQrViewModel = ViewModelProviders.of(this).get(ExitQrViewModel.class);
        binding.setViewModel(exitQrViewModel);
        EditText etxtCar = findViewById(R.id.etxt_car_plate_first_cell_exit_qr);
        EditText etxtMotor = findViewById(R.id.etxt_motor_plate_first_cell_exit_qr);
        exitQrViewModel.init(this, etxtCar, etxtMotor);
        binding.setLifecycleOwner(this);
        exitQrViewModel.mSpinner = binding.spinnerExitQr;
        binding.spinnerExitQr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                exitQrViewModel.getPlate__1().setValue(String.valueOf(binding.spinnerExitQr.getSelectedItem()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.spinnerExitQr.setSelection(0);
                exitQrViewModel.getPlate__1().setValue(String.valueOf(binding.spinnerExitQr.getSelectedItem()));
            }
        });
        binding.etxtCarPlateThirdCellExitQr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 3) {
                    binding.etxtCarPlateThirdCellExitQr.clearFocus();
                    binding.etxtCarPlateForthCellExitQr.requestFocus();
                    binding.etxtCarPlateForthCellExitQr.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etxtMotorPlateFirstCellExitQr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 3) {
                    binding.etxtMotorPlateFirstCellExitQr.clearFocus();
                    binding.etxtMotorPlateSecondCellExitQr.requestFocus();
                    binding.etxtMotorPlateSecondCellExitQr.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etxtCarPlateForthCellExitQr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    binding.etxtCarPlateForthCellExitQr.clearFocus();
                    binding.etxtCarPlateThirdCellExitQr.requestFocus();
                    binding.etxtCarPlateThirdCellExitQr.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etxtMotorPlateSecondCellExitQr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    binding.etxtMotorPlateSecondCellExitQr.clearFocus();
                    binding.etxtMotorPlateFirstCellExitQr.requestFocus();
                    binding.etxtMotorPlateFirstCellExitQr.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if (getIntent().getBooleanExtra("isfromqr", false)) {
            String General_Qr_Code_String = getIntent().getStringExtra("scanned_string");
            Log.d(TAG, "General_Qr_Code_String_scanned >>>>>>>>>>>>>>>> " + General_Qr_Code_String);
            if (General_Qr_Code_String.equals("") || General_Qr_Code_String.equals(null)) {
                exitQrViewModel.getCar().setValue(true);
                exitQrViewModel.getMotor().setValue(false);
                exitQrViewModel.getPlate__0().setValue("");
                exitQrViewModel.getPlate__2().setValue("");
                exitQrViewModel.getPlate__3().setValue("");
                ShowToast.getInstance().showWarning(this, R.string.qr_null);
            } else {
                if (General_Qr_Code_String.trim().equals("backback")) {
                } else {
                    try {


                        String[] qrItems = General_Qr_Code_String.split("#");
                        String crc = qrItems[0];
                        String pelak = qrItems[1];
                        String enterDateTime = qrItems[2];
                        String tariffId = qrItems[3];
                        String paidAmount = qrItems[4];
                        String enterDoorId = qrItems[5];
                        String enterOperatorId = qrItems[6];
                        String paymentType = qrItems[7];
                        String paymentCode = qrItems[8];

                        String calculatedCrc = enterDateTime.substring(9, 12) + "6913" + pelak.substring(0, 2);
                        if (crc.equals(calculatedCrc)) {

                            exitQrViewModel.handelExit(pelak,enterDateTime,tariffId,paidAmount,enterDoorId,enterOperatorId,paymentType,paymentCode);


                        } else {

                            ShowToast.getInstance().showWarning(this, R.string.invalid_qr);
                            return;

                        }


                    } catch (Exception e) {
                        ShowToast.getInstance().showWarning(this, R.string.invalid_qr);
                        return;
                    }

                }
            }
        }
        exitQrViewModel.getProgress().observe(this, new Observer<Integer>() {
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

    @Override
    protected void onResume() {
        super.onResume();
        if (PreferenceManager.getDefaultSharedPreferences(ExitQrActivity.this).getBoolean("comFmPardakhtDialog", false)) {
            PreferenceManager.getDefaultSharedPreferences(ExitQrActivity.this).edit().putBoolean("comFmPardakhtDialog", false).apply();
            //do refresh
            exitQrViewModel.doRefresh();
        }
    }

    @Override
    public void onBackPressed() {
        exitQrViewModel.backPress(ExitQrActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        exitQrViewModel.processActivityResult(this, requestCode, resultCode, data);
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


}
