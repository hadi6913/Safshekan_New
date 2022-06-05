package com.khodmohaseb.parkban;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.khodmohaseb.parkban.databinding.ActivityPaymentSafshekanBinding;
import com.khodmohaseb.parkban.helper.ShowToast;
import com.khodmohaseb.parkban.viewmodels.PaymentSafshekanViewModel;

public class PaymentSafshekanActivity extends BaseActivity  {

    public static final String TAG = "PaymentSafsActivity";

    private PaymentSafshekanViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_safshekan);
        final ActivityPaymentSafshekanBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_safshekan);
        viewModel = ViewModelProviders.of(this).get(PaymentSafshekanViewModel.class);
        binding.setViewModel(viewModel);
        viewModel.init(this);
        binding.setLifecycleOwner(this);
        if (getIntent().getBooleanExtra("isfromtaeed", false)) {
            //first step that is from taeed
            viewModel.getCommonCost().setValue(String.valueOf(getIntent().getLongExtra("CommonCost", 0)));
            if (String.valueOf(getIntent().getLongExtra("CommonCost", 0)).equals("0")) {
                binding.sendClick.setText("تایید");
                binding.radioGroupLayoutPayment.setVisibility(View.GONE);
            }
            viewModel.getSolarEnterDateTime().setValue(getIntent().getStringExtra("SolarEnterDateTime"));
            viewModel.getFormatedDuration().setValue(getIntent().getStringExtra("FormatedDuration"));
            viewModel.getCardNumber().setValue(getIntent().getStringExtra("CardNumber"));
            viewModel.getMemberCode().setValue(getIntent().getStringExtra("MemberCode"));
            viewModel.getDumpIdId().setValue(getIntent().getLongExtra("DumpId", 0));
            if (getIntent().getStringExtra("MemberCode").equals("") || getIntent().getStringExtra("MemberCode").equals(null)) {
                viewModel.getMember().setValue(false);
            } else {
                viewModel.getMember().setValue(true);
            }
            if (getIntent().getStringExtra("CardNumber").equals("") || getIntent().getStringExtra("CardNumber").equals(null)) {
                viewModel.getKart().setValue(false);
            } else {
                viewModel.getKart().setValue(true);
            }
            String pure_pelak = getIntent().getStringExtra("AbsolutCarPlate");
            if (pure_pelak.equals("") || pure_pelak.equals(null)) {
                viewModel.getPelak().setValue(false);
            } else {
                viewModel.getPelak().setValue(true);
                boolean isCar;
                try {
                    long temp = Long.parseLong(pure_pelak);
                    isCar = false;
                } catch (Exception e) {
                    isCar = true;
                }
                if (isCar) {
                    //car
                    viewModel.getCar().setValue(true);
                    viewModel.getMotor().setValue(false);
                    viewModel.getPlate__0().setValue(pure_pelak.substring(0, 2));
                    viewModel.getPlate__2().setValue(pure_pelak.substring(pure_pelak.length() - 5, pure_pelak.length() - 2));
                    viewModel.getPlate__3().setValue(pure_pelak.substring(pure_pelak.length() - 2, pure_pelak.length()));
                    if (pure_pelak.length() > 8) {
                        //الف pelak
                        viewModel.getPlate__1().setValue(pure_pelak.substring(2, 5));
                    } else {
                        //بدون الف
                        viewModel.getPlate__1().setValue(pure_pelak.substring(2, 3));
                    }
                    Log.d(TAG, "car?????????????????????????????????????????????????????");
                } else {
                    //motor
                    viewModel.getCar().setValue(false);
                    viewModel.getMotor().setValue(true);
                    viewModel.getMplate__0().setValue(pure_pelak.substring(0, 3));
                    viewModel.getMplate__1().setValue(pure_pelak.substring(3));
                    Log.d(TAG, "motor?????????????????????????????????????????????????????");
                }
            }
        }
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

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (PreferenceManager.getDefaultSharedPreferences(PaymentSafshekanActivity.this).getBoolean("commingFromPahpat", false)) {
//            PreferenceManager.getDefaultSharedPreferences(PaymentSafshekanActivity.this).edit().putBoolean("commingFromPahpat", false).apply();
//            String resid = PreferenceManager.getDefaultSharedPreferences(PaymentSafshekanActivity.this).getString("ResidNahaee", "ناموفق");
//            viewModel.showDilaogAndPrint(PaymentSafshekanActivity.this, resid);
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 103) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle b = data.getBundleExtra("response");
                Log.d(TAG, "onActivityResult:  >>>>>>>>>>>>>>> " + getBundleString(b));
                if (b.getString("result").trim().equals("succeed")) {
                    String myFactorId = String.valueOf(PreferenceManager.getDefaultSharedPreferences(PaymentSafshekanActivity.this).getLong("factorId", 0));
                    Log.d("e_pardakht >>>", "step 2 >>> GOOD_PAYMENT ");
                    viewModel.part3(PaymentSafshekanActivity.this, b);
                } else {
                    ShowToast.getInstance().showError(PaymentSafshekanActivity.this, R.string.failed_payment);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

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

//    @Override
//    public void onReceiveResult(int serviceId, int resultCode, Bundle resultData) {
//
//        String myFactorId = String.valueOf(PreferenceManager.getDefaultSharedPreferences(PaymentSafshekanActivity.this).getLong("factorId", 0));
//        Log.d("e_pardakht", "onReceiveResult >>>>  myFactorId >>>>> " + myFactorId);
////        //first confirm
////        try {
////            if (serviceId == EasyHelper.GOOD_PAYMENT) {
////                Log.d("elecronic_pardakht >>>", "step 2 >>> GOOD_PAYMENT ");
////                if (resultCode == EasyHelper.TXN_SUCCESS) {
////                    Log.d("elecronic_pardakht >>>", "step 2 >>> TXN_SUCCESS >>>  ApprovalCode : " + resultData.getString("ApprovalCode"));
////                    Log.d("elecronic_pardakht >>>", "step 2 >>> TXN_SUCCESS >>>  ResNum : " + resultData.getString("ResNum"));
////                    Log.d("elecronic_pardakht >>>", "step 2 >>> TXN_SUCCESS >>>  factorId from Shared Pref : " + myFactorId);
////                    try {
////                        if (resultData.getString("ResNum").equals(myFactorId)) {
////                            //part 3  start >>>
////                            viewModel.part3(PaymentSafshekanActivity.this, resultData);
////                        } else {
////                            ShowToast.getInstance().showError(PaymentSafshekanActivity.this, R.string.connection_failed);
////                        }
////                    } catch (Exception e) {
////                        ShowToast.getInstance().showError(PaymentSafshekanActivity.this, R.string.connection_failed);
////                    }
////                } else {
////                    boolean MustFinish = resultData.getBoolean("MustFinish");
////                    if (MustFinish) {
//////                        finish();
////                        Log.d("elecronic_pardakht >>>", "step 2 >>> TXN_UN_SUCCESS");
////                    }
////                }
////            }
////        } catch (Exception e) {
////            ShowToast.getInstance().showError(PaymentSafshekanActivity.this, R.string.exception_msg);
////            Log.d("elecronic_pardakht >>>", "step 2 >>> TXN_UN_SUCCESS >>> ERROR : " + e.getMessage());
////        }
//        //second confirm
//        try {
//            if (serviceId == EasyHelper.GOOD_CONFIRM) {
//                Log.d("elecronic_pardakht >>>", "onReceiveResult >>> Good_Confirm ");
//                boolean MustFinish = resultData.getBoolean("MustFinish");
//                try {
//                } catch (Exception e) {
//                }
//                if (MustFinish) {
//                    viewModel.part4(PaymentSafshekanActivity.this, myFactorId);
//                    PreferenceManager.getDefaultSharedPreferences(PaymentSafshekanActivity.this).edit().putBoolean("commingFromPahpat", true).apply();
//                }
//            }
//        } catch (Exception e) {
//            ShowToast.getInstance().showError(PaymentSafshekanActivity.this, R.string.exception_msg);
//        }
//    }
}
