package com.khodmohaseb.parkban;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.khodmohaseb.parkban.databinding.ActivityLoginBinding;
import com.khodmohaseb.parkban.viewmodels.LoginViewModel;

public class LoginActivity extends BaseActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        binding.setViewModel(loginViewModel);

        binding.setLifecycleOwner(this);

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
