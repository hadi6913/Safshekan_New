package com.safshekan.parkban;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.AssetManager;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.safshekan.parkban.databinding.ActivityDriverChargeBinding;
import com.safshekan.parkban.helper.EasyHelper;
import com.safshekan.parkban.helper.ShowToast;
import com.safshekan.parkban.viewmodels.DriverChargeViewModel;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static ir.shahaabco.ANPRNDK.anpr_create;
//hhhhhhhhhhhhhhh >>>>>  added for new icon tashkhis

public class DriverChargeActivity extends BaseActivity implements EasyHelper.Receiver {

    DriverChargeViewModel viewModel;


    //hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
    private int failedLoadLib = 0;
    private static final String TAG = "ANPR";

    //hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
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

        final ActivityDriverChargeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_charge);

        //hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
        copyAssets();
        CreateANPR();

        viewModel = ViewModelProviders.of(this).get(DriverChargeViewModel.class);

        binding.setViewModel(viewModel);
        viewModel.init(this);

        binding.setLifecycleOwner(this);


        viewModel.mSpinner = binding.spinner;

        //click listener for spinner list of alphabetic charecters of pelak
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                viewModel.setPlate1(String.valueOf(binding.spinner.getSelectedItem()));
                viewModel.getPlate__1().setValue(String.valueOf(binding.spinner.getSelectedItem()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.number.addTextChangedListener(viewModel.phoneTextWatcher);
        binding.confirmNumber.addTextChangedListener(viewModel.confirmTextWatcher);
        binding.cashPaymentRadioButton.setVisibility(viewModel.getShouldShowKifePoolDiloag().getValue());
        binding.onlinePaymentRadioButton.setVisibility(viewModel.getShouldShowElectronicPayDiloag().getValue());
        binding.paymentTypeLayout.setVisibility(viewModel.getShouldShowEntirePayDiloag().getValue());





    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("xhadi", "onResume: ");
        //should run only when cooming from pahpat
        if (PreferenceManager.getDefaultSharedPreferences(DriverChargeActivity.this).getBoolean("commingFromPahpat", false) && PreferenceManager.getDefaultSharedPreferences(DriverChargeActivity.this).getBoolean("FinalConfirm", false)) {
            viewModel.showDialogAndPrint();
            PreferenceManager.getDefaultSharedPreferences(DriverChargeActivity.this).edit().putBoolean("commingFromPahpat", false).apply();
            PreferenceManager.getDefaultSharedPreferences(DriverChargeActivity.this).edit().putBoolean("FinalConfirm", false).apply();
        }

        if (PreferenceManager.getDefaultSharedPreferences(DriverChargeActivity.this).getBoolean("commingFromPahpat", false)) {
            if (PreferenceManager.getDefaultSharedPreferences(DriverChargeActivity.this).getBoolean("ShowDialog", false)) {
                //calling method that shows dialog >>> retry or print
                //retry  ....
                //print custom dialog and add to list of failed requests.....
                viewModel.showDialogForRetryOrPrint(DriverChargeActivity.this);
            }
        }


    }

    @Override
    public void onReceiveResult(int serviceId, int resultCode, Bundle resultData) {
        String myFactorId = PreferenceManager.getDefaultSharedPreferences(DriverChargeActivity.this).getString("factorId", "0");
        //first confirm
        try {
            if (serviceId == EasyHelper.GOOD_PAYMENT) {
                if (resultCode == EasyHelper.TXN_SUCCESS) {
                    try {
                        if (resultData.getString("ResNum").equals(myFactorId)) {
                            //part 3  start >>>
                            viewModel.part3(DriverChargeActivity.this, resultData);
                        } else {
                            ShowToast.getInstance().showError(DriverChargeActivity.this, R.string.connection_failed);
                        }
                    } catch (Exception e) {
                        ShowToast.getInstance().showError(DriverChargeActivity.this, R.string.connection_failed);
                    }

                } else {
                    boolean MustFinish = resultData.getBoolean("MustFinish");
                    if (MustFinish) {
//                        finish();

                    }

                }
            }
        } catch (Exception e) {
            ShowToast.getInstance().showError(DriverChargeActivity.this, R.string.exception_msg);

        }


        //second confirm
        try {
            if (serviceId == EasyHelper.GOOD_CONFIRM) {

                boolean MustFinish = resultData.getBoolean("MustFinish");
                try {


                } catch (Exception e) {

                }
                if (MustFinish) {
                    viewModel.part4(DriverChargeActivity.this, myFactorId);
                    PreferenceManager.getDefaultSharedPreferences(DriverChargeActivity.this).edit().putBoolean("commingFromPahpat", true).apply();

                }

            }
        } catch (Exception e) {
            ShowToast.getInstance().showError(DriverChargeActivity.this, R.string.exception_msg);
        }


    }




    //hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.processActivityResult(this,requestCode,resultCode,data);
    }

    //hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
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

    //hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
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

    //hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
