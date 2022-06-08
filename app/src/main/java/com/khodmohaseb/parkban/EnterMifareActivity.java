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

import com.khodmohaseb.parkban.databinding.ActivityEnterMifareBinding;
import com.khodmohaseb.parkban.databinding.ActivityMainBinding;
import com.khodmohaseb.parkban.helper.ShowToast;
import com.khodmohaseb.parkban.services.EnterMifareReaderHandler;

import com.khodmohaseb.parkban.utils.MyBounceInterpolator;
import com.khodmohaseb.parkban.viewmodels.EnterMifareViewModel;
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


public class EnterMifareActivity extends BaseActivity implements EnterMifareReaderHandler.Callbacks {

    private static final String TAG = "EnterMifareActivity";
    private int failedLoadLib = 0;
    private EnterMifareViewModel enterMifareViewModel;
    private boolean doubleBackToExitPressedOnce = false;

    //**********************************************************************************************
    //**********************************************************************************************
    private Intent readerHandlerIntent;
    private EnterMifareReaderHandler readerHandler;

    private ServiceConnection readerHandlerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            EnterMifareReaderHandler.LocalBinder binder = (EnterMifareReaderHandler.LocalBinder) service;
            readerHandler = binder.getServiceInstance();
            readerHandler.registerClient(EnterMifareActivity.this);
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
        final ActivityEnterMifareBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_enter_mifare);
        copyAssets();
        CreateANPR();
        enterMifareViewModel = ViewModelProviders.of(this).get(EnterMifareViewModel.class);
        binding.setViewModel(enterMifareViewModel);
        EditText etxtCar = findViewById(R.id.etxt_car_plate_first_cell_enter_mifare);
        EditText etxtMotor = findViewById(R.id.etxt_motor_plate_first_cell_enter_mifare);
        enterMifareViewModel.init(this, etxtCar, etxtMotor);
        enterMifareViewModel.getHasPelak().setValue(true);
        binding.setLifecycleOwner(this);
        enterMifareViewModel.mSpinner = binding.spinnerEnterMifare;
        binding.spinnerEnterMifare.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                enterMifareViewModel.getPlate__1().setValue(String.valueOf(binding.spinnerEnterMifare.getSelectedItem()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.spinnerEnterMifare.setSelection(0);
                enterMifareViewModel.getPlate__1().setValue(String.valueOf(binding.spinnerEnterMifare.getSelectedItem()));
            }
        });
        binding.etxtCarPlateThirdCellEnterMifare.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 3) {
                    binding.etxtCarPlateThirdCellEnterMifare.clearFocus();
                    binding.etxtCarPlateForthCellEnterMifare.requestFocus();
                    binding.etxtCarPlateForthCellEnterMifare.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etxtMotorPlateFirstCellEnterMifare.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 3) {
                    binding.etxtMotorPlateFirstCellEnterMifare.clearFocus();
                    binding.etxtMotorPlateSecondCellEnterMifare.requestFocus();
                    binding.etxtMotorPlateSecondCellEnterMifare.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etxtCarPlateForthCellEnterMifare.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    binding.etxtCarPlateForthCellEnterMifare.clearFocus();
                    binding.etxtCarPlateThirdCellEnterMifare.requestFocus();
                    binding.etxtCarPlateThirdCellEnterMifare.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etxtMotorPlateSecondCellEnterMifare.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    binding.etxtMotorPlateSecondCellEnterMifare.clearFocus();
                    binding.etxtMotorPlateFirstCellEnterMifare.requestFocus();
                    binding.etxtMotorPlateFirstCellEnterMifare.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        enterMifareViewModel.getProgress().observe(this, new Observer<Integer>() {
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
            readerHandlerIntent = new Intent(getApplicationContext(), EnterMifareReaderHandler.class);
            startService(readerHandlerIntent);
            getApplicationContext().bindService(readerHandlerIntent, readerHandlerConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception ex) {
            Log.d(TAG, "MainActivity > startServices():" + ex.getMessage());
        }
    }

    private void stopServices() {
        try {
            //Try to stop Reader handler
            if (EnterMifareReaderHandler.getInstance() != null)
                EnterMifareReaderHandler.getInstance().stopSelf();
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

    }

    @Override
    public void onBackPressed() {
        enterMifareViewModel.backPress(EnterMifareActivity.this);
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
    public void showOnUi(String serialNumber) {

    }
}
