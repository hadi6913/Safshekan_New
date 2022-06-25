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
import com.khodmohaseb.parkban.databinding.ActivityLoginBinding;
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
    private ActivityEnterMifareBinding binding;


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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enter_mifare);
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
        enterMifareViewModel.mCarTypeSpinner = binding.spinnerCarTypeEnterMifare;


        binding.spinnerCarTypeEnterMifare.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                enterMifareViewModel.getSelectedCarTypeName().setValue(String.valueOf(binding.spinnerCarTypeEnterMifare.getSelectedItem().toString().trim()));


                if (String.valueOf(binding.spinnerCarTypeEnterMifare.getSelectedItem().toString().trim()).equals(enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff1().getVehicleName().trim())) {
                    enterMifareViewModel.getSelectedTarrifId().setValue(1);
                    enterMifareViewModel.getSelectedTarrifEntranceFee().setValue(enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff1().getEntranceCost().toString());
                    if (enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff1().getIsReceiveUponEntrance()) {
                        enterMifareViewModel.getShouldPayFirst().setValue(true);
                    } else {
                        enterMifareViewModel.getShouldPayFirst().setValue(false);
                    }
                }

                if (enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff2() != null) {
                    if (String.valueOf(binding.spinnerCarTypeEnterMifare.getSelectedItem().toString().trim()).equals(enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff2().getVehicleName().trim())) {
                        enterMifareViewModel.getSelectedTarrifId().setValue(2);
                        enterMifareViewModel.getSelectedTarrifEntranceFee().setValue(enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff2().getEntranceCost().toString());


                        if (enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff2().getIsReceiveUponEntrance()) {
                            enterMifareViewModel.getShouldPayFirst().setValue(true);
                        } else {
                            enterMifareViewModel.getShouldPayFirst().setValue(false);
                        }
                    }
                }


                if (enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff3() != null) {
                    if (String.valueOf(binding.spinnerCarTypeEnterMifare.getSelectedItem().toString().trim()).equals(enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff3().getVehicleName().trim())) {
                        enterMifareViewModel.getSelectedTarrifId().setValue(3);
                        enterMifareViewModel.getSelectedTarrifEntranceFee().setValue(enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff3().getEntranceCost().toString());


                        if (enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff3().getIsReceiveUponEntrance()) {
                            enterMifareViewModel.getShouldPayFirst().setValue(true);
                        } else {
                            enterMifareViewModel.getShouldPayFirst().setValue(false);
                        }
                    }
                }

                if (enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff4() != null) {
                    if (String.valueOf(binding.spinnerCarTypeEnterMifare.getSelectedItem().toString().trim()).equals(enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff4().getVehicleName().trim())) {
                        enterMifareViewModel.getSelectedTarrifId().setValue(4);
                        enterMifareViewModel.getSelectedTarrifEntranceFee().setValue(enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff4().getEntranceCost().toString());


                        if (enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff4().getIsReceiveUponEntrance()) {
                            enterMifareViewModel.getShouldPayFirst().setValue(true);
                        } else {
                            enterMifareViewModel.getShouldPayFirst().setValue(false);
                        }
                    }
                }


                if (enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff5() != null) {
                    if (String.valueOf(binding.spinnerCarTypeEnterMifare.getSelectedItem().toString().trim()).equals(enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff5().getVehicleName().trim())) {
                        enterMifareViewModel.getSelectedTarrifId().setValue(5);
                        enterMifareViewModel.getSelectedTarrifEntranceFee().setValue(enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff5().getEntranceCost().toString());


                        if (enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff5().getIsReceiveUponEntrance()) {
                            enterMifareViewModel.getShouldPayFirst().setValue(true);
                        } else {
                            enterMifareViewModel.getShouldPayFirst().setValue(false);
                        }
                    }
                }


                if (enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff6() != null) {
                    if (String.valueOf(binding.spinnerCarTypeEnterMifare.getSelectedItem().toString().trim()).equals(enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff6().getVehicleName().trim())) {
                        enterMifareViewModel.getSelectedTarrifId().setValue(6);
                        enterMifareViewModel.getSelectedTarrifEntranceFee().setValue(enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff6().getEntranceCost().toString());


                        if (enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff6().getIsReceiveUponEntrance()) {
                            enterMifareViewModel.getShouldPayFirst().setValue(true);
                        } else {
                            enterMifareViewModel.getShouldPayFirst().setValue(false);
                        }
                    }
                }


                if (enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff7() != null) {
                    if (String.valueOf(binding.spinnerCarTypeEnterMifare.getSelectedItem().toString().trim()).equals(enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff7().getVehicleName().trim())) {
                        enterMifareViewModel.getSelectedTarrifId().setValue(7);
                        enterMifareViewModel.getSelectedTarrifEntranceFee().setValue(enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff7().getEntranceCost().toString());


                        if (enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff7().getIsReceiveUponEntrance()) {
                            enterMifareViewModel.getShouldPayFirst().setValue(true);
                        } else {
                            enterMifareViewModel.getShouldPayFirst().setValue(false);
                        }
                    }
                }

                if (enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff8() != null) {
                    if (String.valueOf(binding.spinnerCarTypeEnterMifare.getSelectedItem().toString().trim()).equals(enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff8().getVehicleName().trim())) {
                        enterMifareViewModel.getSelectedTarrifId().setValue(8);
                        enterMifareViewModel.getSelectedTarrifEntranceFee().setValue(enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff8().getEntranceCost().toString());


                        if (enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff8().getIsReceiveUponEntrance()) {
                            enterMifareViewModel.getShouldPayFirst().setValue(true);
                        } else {
                            enterMifareViewModel.getShouldPayFirst().setValue(false);
                        }
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.spinnerCarTypeEnterMifare.setSelection(0);
                enterMifareViewModel.getSelectedCarTypeName().setValue(String.valueOf(binding.spinnerCarTypeEnterMifare.getSelectedItem().toString().trim()));
                enterMifareViewModel.getSelectedTarrifId().setValue(1);
                enterMifareViewModel.getSelectedTarrifEntranceFee().setValue(enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff1().getEntranceCost().toString());


                if (enterMifareViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff1().getIsReceiveUponEntrance()) {
                    enterMifareViewModel.getShouldPayFirst().setValue(true);
                }
            }
        });


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

    public void startServices(byte[] block20, byte[] block21, byte[] block22) {
        try {
            readerHandlerIntent = new Intent(getApplicationContext(), EnterMifareReaderHandler.class);
            readerHandlerIntent.putExtra("bl20", block20);
            readerHandlerIntent.putExtra("bl21", block21);
            readerHandlerIntent.putExtra("bl22", block22);
            startService(readerHandlerIntent);
            getApplicationContext().bindService(readerHandlerIntent, readerHandlerConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception ex) {
            Log.d(TAG, "MainActivity > startServices():" + ex.getMessage());
        }
    }

    public void stopServices() {
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
    public void onBackPressed() {

        if (enterMifareViewModel.selectedDoor.getDoorType() != 2) {
            enterMifareViewModel.backPress(EnterMifareActivity.this);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        enterMifareViewModel.processActivityResult(this, requestCode, resultCode, data);
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
    public void enterMifareResult(boolean result) {


        if (result) {

            stopServicesForOperator();


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ShowToast.getInstance().showSuccess(EnterMifareActivity.this, R.string.submit_success);
                    enterMifareViewModel.mifareAlertDialog.dismiss();
                    enterMifareViewModel.cashTypeAlertDialog.dismiss();
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ShowToast.getInstance().showError(EnterMifareActivity.this, R.string.error_mifare);
                }
            });


        }
    }
}
