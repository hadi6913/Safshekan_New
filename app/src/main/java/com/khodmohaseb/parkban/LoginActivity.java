package com.khodmohaseb.parkban;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.khodmohaseb.parkban.databinding.ActivityLoginBinding;
import com.khodmohaseb.parkban.utils.Animation_Constant;
import com.khodmohaseb.parkban.utils.MyBounceInterpolator;
import com.khodmohaseb.parkban.viewmodels.LoginViewModel;

import java.util.Locale;

public class LoginActivity extends BaseActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String lan = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("language", "fa");

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(lan)); // API 17+ only.
        } else {
            conf.locale = new Locale(lan);
        }
        res.updateConfiguration(conf, dm);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        binding.setViewModel(loginViewModel);


        binding.setLifecycleOwner(this);


        loginViewModel.mUserSpinner = binding.spinnerSelectUser;
        binding.spinnerSelectUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loginViewModel.getSelectedUserName().setValue(String.valueOf(binding.spinnerSelectUser.getSelectedItem().toString().trim()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.spinnerSelectUser.setSelection(0);
                loginViewModel.getSelectedUserName().setValue(String.valueOf(binding.spinnerSelectUser.getSelectedItem().toString().trim()));
            }
        });


        loginViewModel.mDoorSpinner = binding.spinnerSelectDoor;
        binding.spinnerSelectDoor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loginViewModel.getSelectedDoorName().setValue(String.valueOf(binding.spinnerSelectDoor.getSelectedItem().toString().trim()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.spinnerSelectDoor.setSelection(0);
                loginViewModel.getSelectedDoorName().setValue(String.valueOf(binding.spinnerSelectDoor.getSelectedItem().toString().trim()));

            }
        });


        loginViewModel.init(this);


//        loginViewModel.fillDefaultUserAndPass(this);

        loginViewModel.getProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer value) {
                if (value > 0) {
                    showProgress(true);
                } else {
                    showProgress(false);
                }
            }
        });


        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)

                requestPermissions(new String[]{Manifest.permission.CAMERA}, 10001);

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
//        loginViewModel.fillDefaultUserAndPass(this);
    }
}
