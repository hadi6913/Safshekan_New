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

import com.khodmohaseb.parkban.databinding.ActivityExitMifareBinding;
import com.khodmohaseb.parkban.databinding.ActivityMainBinding;
import com.khodmohaseb.parkban.helper.ShowToast;

import com.khodmohaseb.parkban.services.ExitMifareReaderHandler;
import com.khodmohaseb.parkban.utils.ByteUtils;
import com.khodmohaseb.parkban.utils.MyBounceInterpolator;
import com.khodmohaseb.parkban.viewmodels.ExitMifareViewModel;
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


public class ExitMifareActivity extends BaseActivity implements ExitMifareReaderHandler.Callbacks {

    private static final String TAG = "ExitMifareActivity";
    private int failedLoadLib = 0;
    private ExitMifareViewModel exitMifareViewModel;
    private boolean doubleBackToExitPressedOnce = false;

    //**********************************************************************************************
    //**********************************************************************************************
    private Intent readerHandlerIntent;
    private ExitMifareReaderHandler readerHandler;

    private ServiceConnection readerHandlerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            ExitMifareReaderHandler.LocalBinder binder = (ExitMifareReaderHandler.LocalBinder) service;
            readerHandler = binder.getServiceInstance();
            readerHandler.registerClient(ExitMifareActivity.this);
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
        final ActivityExitMifareBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_exit_mifare);
        copyAssets();
        CreateANPR();
        exitMifareViewModel = ViewModelProviders.of(this).get(ExitMifareViewModel.class);
        binding.setViewModel(exitMifareViewModel);
        EditText etxtCar = findViewById(R.id.etxt_car_plate_first_cell_exit_mifare);
        EditText etxtMotor = findViewById(R.id.etxt_motor_plate_first_cell_exit_mifare);
        exitMifareViewModel.init(this, etxtCar, etxtMotor);







        binding.setLifecycleOwner(this);
        exitMifareViewModel.mSpinner = binding.spinnerExitMifare;
        binding.spinnerExitMifare.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                exitMifareViewModel.getPlate__1().setValue(String.valueOf(binding.spinnerExitMifare.getSelectedItem()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.spinnerExitMifare.setSelection(0);
                exitMifareViewModel.getPlate__1().setValue(String.valueOf(binding.spinnerExitMifare.getSelectedItem()));
            }
        });
        binding.etxtCarPlateThirdCellExitMifare.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 3) {
                    binding.etxtCarPlateThirdCellExitMifare.clearFocus();
                    binding.etxtCarPlateForthCellExitMifare.requestFocus();
                    binding.etxtCarPlateForthCellExitMifare.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etxtMotorPlateFirstCellExitMifare.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 3) {
                    binding.etxtMotorPlateFirstCellExitMifare.clearFocus();
                    binding.etxtMotorPlateSecondCellExitMifare.requestFocus();
                    binding.etxtMotorPlateSecondCellExitMifare.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etxtCarPlateForthCellExitMifare.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    binding.etxtCarPlateForthCellExitMifare.clearFocus();
                    binding.etxtCarPlateThirdCellExitMifare.requestFocus();
                    binding.etxtCarPlateThirdCellExitMifare.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etxtMotorPlateSecondCellExitMifare.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    binding.etxtMotorPlateSecondCellExitMifare.clearFocus();
                    binding.etxtMotorPlateFirstCellExitMifare.requestFocus();
                    binding.etxtMotorPlateFirstCellExitMifare.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        exitMifareViewModel.getProgress().observe(this, new Observer<Integer>() {
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

    public void startServices() {
        try {
            readerHandlerIntent = new Intent(getApplicationContext(), ExitMifareReaderHandler.class);
            startService(readerHandlerIntent);
            getApplicationContext().bindService(readerHandlerIntent, readerHandlerConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception ex) {
            Log.d(TAG, "MainActivity > startServices():" + ex.getMessage());
        }
    }

    private void stopServices() {
        try {
            //Try to stop Reader handler
            if (ExitMifareReaderHandler.getInstance() != null)
                ExitMifareReaderHandler.getInstance().stopSelf();
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
    public void onBackPressed() {
        exitMifareViewModel.backPress(ExitMifareActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        exitMifareViewModel.processActivityResult(this, requestCode, resultCode, data);
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
    public void exitMifareResult(boolean result, final byte[] bl16, final byte[] bl17, final byte[] bl18) {




        if (result) {

            stopServicesForOperator();


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {

                        String pelak = Long.toString(ByteUtils.getValue(bl16,0,4));
                        String electronicPaymentCode = Long.toString(ByteUtils.getValue(bl16,4,12)) ;
                        String yyyyMMddHHmmEnterDateTime = Long.toString(ByteUtils.getValue(bl17,0,7)) ;
                        String tarrifId = Integer.toString(ByteUtils.getInt(bl17[7]));
                        String paymentType = Integer.toString(ByteUtils.getInt(bl17[8])) ;
                        String paidAmount =  Long.toString(ByteUtils.getValue(bl17,9,7));
                        String enterDoorId = Long.toString( ByteUtils.getValue(bl18,0,8));
                        String enterOperatorId = Long.toString( ByteUtils.getValue(bl18,7,8));
                        exitMifareViewModel.handelExit(
                           pelak,
                           yyyyMMddHHmmEnterDateTime,
                           tarrifId,
                           paidAmount,
                           enterDoorId,
                           enterOperatorId,
                           paymentType,
                           electronicPaymentCode
                        );

                    }catch (Exception e){
                        ShowToast.getInstance().showError(ExitMifareActivity.this, R.string.error_mifare);
                    }
                    exitMifareViewModel.mifareAlertDialog.dismiss();

                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ShowToast.getInstance().showError(ExitMifareActivity.this, R.string.error_mifare);
                }
            });


        }










    }
}
