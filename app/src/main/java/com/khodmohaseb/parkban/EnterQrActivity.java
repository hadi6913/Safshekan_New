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

import com.khodmohaseb.parkban.databinding.ActivityEnterQrBinding;
import com.khodmohaseb.parkban.databinding.ActivityLoginBinding;
import com.khodmohaseb.parkban.databinding.ActivityMainBinding;
import com.khodmohaseb.parkban.helper.ShowToast;

import com.khodmohaseb.parkban.utils.MyBounceInterpolator;
import com.khodmohaseb.parkban.viewmodels.EnterQrViewModel;
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


public class EnterQrActivity extends BaseActivity {

    private static final String TAG = "EnterQrActivity";
    private int failedLoadLib = 0;
    private EnterQrViewModel enterQrViewModel;
    private ActivityEnterQrBinding binding;


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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enter_qr);
        copyAssets();
        CreateANPR();

        enterQrViewModel = ViewModelProviders.of(this).get(EnterQrViewModel.class);
        binding.setViewModel(enterQrViewModel);
        EditText etxtCar = findViewById(R.id.etxt_car_plate_first_cell_enter_qr);
        EditText etxtMotor = findViewById(R.id.etxt_motor_plate_first_cell_enter_qr);
        enterQrViewModel.init(this, etxtCar, etxtMotor);
        enterQrViewModel.getHasPelak().setValue(true);
        binding.setLifecycleOwner(this);
        enterQrViewModel.mSpinner = binding.spinnerEnterQr;
        enterQrViewModel.mCarTypeSpinner = binding.spinnerCarTypeEnterQr;


        binding.spinnerCarTypeEnterQr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                enterQrViewModel.getSelectedCarTypeName().setValue(String.valueOf(binding.spinnerCarTypeEnterQr.getSelectedItem().toString().trim()));


                if (String.valueOf(binding.spinnerCarTypeEnterQr.getSelectedItem().toString().trim()).equals(enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff1().getVehicleName().trim())) {
                    enterQrViewModel.getSelectedTarrifId().setValue(1);
                    enterQrViewModel.getSelectedTarrifEntranceFee().setValue(enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff1().getEntranceCost().toString());
                    if (enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff1().getIsReceiveUponEntrance()) {
                        enterQrViewModel.getShouldPayFirst().setValue(true);
                    }else {
                        enterQrViewModel.getShouldPayFirst().setValue(false);
                    }
                }

                if (enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff2() != null){
                    if (String.valueOf(binding.spinnerCarTypeEnterQr.getSelectedItem().toString().trim()).equals(enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff2().getVehicleName().trim())) {
                        enterQrViewModel.getSelectedTarrifId().setValue(2);
                        enterQrViewModel.getSelectedTarrifEntranceFee().setValue(enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff2().getEntranceCost().toString());



                        if (enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff2().getIsReceiveUponEntrance()) {
                            enterQrViewModel.getShouldPayFirst().setValue(true);
                        }else {
                            enterQrViewModel.getShouldPayFirst().setValue(false);
                        }
                    }
                }



                if (enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff3() != null){
                    if (String.valueOf(binding.spinnerCarTypeEnterQr.getSelectedItem().toString().trim()).equals(enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff3().getVehicleName().trim())) {
                        enterQrViewModel.getSelectedTarrifId().setValue(3);
                        enterQrViewModel.getSelectedTarrifEntranceFee().setValue(enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff3().getEntranceCost().toString());



                        if (enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff3().getIsReceiveUponEntrance()) {
                            enterQrViewModel.getShouldPayFirst().setValue(true);
                        }else {
                            enterQrViewModel.getShouldPayFirst().setValue(false);
                        }
                    }
                }

                if (enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff4() != null){
                    if (String.valueOf(binding.spinnerCarTypeEnterQr.getSelectedItem().toString().trim()).equals(enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff4().getVehicleName().trim())) {
                        enterQrViewModel.getSelectedTarrifId().setValue(4);
                        enterQrViewModel.getSelectedTarrifEntranceFee().setValue(enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff4().getEntranceCost().toString());




                        if (enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff4().getIsReceiveUponEntrance()) {
                            enterQrViewModel.getShouldPayFirst().setValue(true);
                        }else {
                            enterQrViewModel.getShouldPayFirst().setValue(false);
                        }
                    }
                }



                if (enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff5() != null){
                    if (String.valueOf(binding.spinnerCarTypeEnterQr.getSelectedItem().toString().trim()).equals(enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff5().getVehicleName().trim())) {
                        enterQrViewModel.getSelectedTarrifId().setValue(5);
                        enterQrViewModel.getSelectedTarrifEntranceFee().setValue(enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff5().getEntranceCost().toString());




                        if (enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff5().getIsReceiveUponEntrance()) {
                            enterQrViewModel.getShouldPayFirst().setValue(true);
                        }else {
                            enterQrViewModel.getShouldPayFirst().setValue(false);
                        }
                    }
                }


                if (enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff6() != null){
                    if (String.valueOf(binding.spinnerCarTypeEnterQr.getSelectedItem().toString().trim()).equals(enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff6().getVehicleName().trim())) {
                        enterQrViewModel.getSelectedTarrifId().setValue(6);
                        enterQrViewModel.getSelectedTarrifEntranceFee().setValue(enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff6().getEntranceCost().toString());


                        if (enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff6().getIsReceiveUponEntrance()) {
                            enterQrViewModel.getShouldPayFirst().setValue(true);
                        }else {
                            enterQrViewModel.getShouldPayFirst().setValue(false);
                        }
                    }
                }



                if (enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff7() != null){
                    if (String.valueOf(binding.spinnerCarTypeEnterQr.getSelectedItem().toString().trim()).equals(enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff7().getVehicleName().trim())) {
                        enterQrViewModel.getSelectedTarrifId().setValue(7);
                        enterQrViewModel.getSelectedTarrifEntranceFee().setValue(enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff7().getEntranceCost().toString());


                        if (enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff7().getIsReceiveUponEntrance()) {
                            enterQrViewModel.getShouldPayFirst().setValue(true);
                        }else {
                            enterQrViewModel.getShouldPayFirst().setValue(false);
                        }
                    }
                }

                if (enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff8() != null){
                    if (String.valueOf(binding.spinnerCarTypeEnterQr.getSelectedItem().toString().trim()).equals(enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff8().getVehicleName().trim())) {
                        enterQrViewModel.getSelectedTarrifId().setValue(8);
                        enterQrViewModel.getSelectedTarrifEntranceFee().setValue(enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff8().getEntranceCost().toString());


                        if (enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff8().getIsReceiveUponEntrance()) {
                            enterQrViewModel.getShouldPayFirst().setValue(true);
                        }else {
                            enterQrViewModel.getShouldPayFirst().setValue(false);
                        }
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.spinnerCarTypeEnterQr.setSelection(0);
                enterQrViewModel.getSelectedCarTypeName().setValue(String.valueOf(binding.spinnerCarTypeEnterQr.getSelectedItem().toString().trim()));
                enterQrViewModel.getSelectedTarrifId().setValue(1);
                enterQrViewModel.getSelectedTarrifEntranceFee().setValue(enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff1().getEntranceCost().toString());



                if (enterQrViewModel.getParkingInfoResponse.getTariffs().getVehicleTariff1().getIsReceiveUponEntrance()) {
                    enterQrViewModel.getShouldPayFirst().setValue(true);
                }
            }
        });


        binding.spinnerEnterQr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                enterQrViewModel.getPlate__1().setValue(String.valueOf(binding.spinnerEnterQr.getSelectedItem()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.spinnerEnterQr.setSelection(0);
                enterQrViewModel.getPlate__1().setValue(String.valueOf(binding.spinnerEnterQr.getSelectedItem()));
            }
        });
        binding.etxtCarPlateThirdCellEnterQr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 3) {
                    binding.etxtCarPlateThirdCellEnterQr.clearFocus();
                    binding.etxtCarPlateForthCellEnterQr.requestFocus();
                    binding.etxtCarPlateForthCellEnterQr.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etxtMotorPlateFirstCellEnterQr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 3) {
                    binding.etxtMotorPlateFirstCellEnterQr.clearFocus();
                    binding.etxtMotorPlateSecondCellEnterQr.requestFocus();
                    binding.etxtMotorPlateSecondCellEnterQr.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etxtCarPlateForthCellEnterQr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    binding.etxtCarPlateForthCellEnterQr.clearFocus();
                    binding.etxtCarPlateThirdCellEnterQr.requestFocus();
                    binding.etxtCarPlateThirdCellEnterQr.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etxtMotorPlateSecondCellEnterQr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    binding.etxtMotorPlateSecondCellEnterQr.clearFocus();
                    binding.etxtMotorPlateFirstCellEnterQr.requestFocus();
                    binding.etxtMotorPlateFirstCellEnterQr.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        enterQrViewModel.getProgress().observe(this, new Observer<Integer>() {
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
    public void onBackPressed() {


        if (enterQrViewModel.selectedDoor.getDoorType() != 2) {
            enterQrViewModel.backPress(EnterQrActivity.this);
        } else {
            super.onBackPressed();
        }


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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        enterQrViewModel.processActivityResult(this, requestCode, resultCode, data);
    }


}
