package com.khodmohaseb.parkban;

import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.khodmohaseb.parkban.databinding.ActivitySplashBinding;
import com.khodmohaseb.parkban.helper.Utility;
import com.khodmohaseb.parkban.viewmodels.SplashViewModel;

import static com.khodmohaseb.parkban.viewmodels.SplashViewModel.STORAGE_PERMISSION;

public class SplashActivity extends BaseActivity {

    private SplashViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setContext(getApplicationContext());
        ActivitySplashBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        viewModel = ViewModelProviders.of(this).get(SplashViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        viewModel.init(this);

//        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
//        boolean firstStart = prefs.getBoolean("firstStart", true);
//
//        if (firstStart) {
//            showStartDialog();
//        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    viewModel.getLastVersion();
                } else {
                    System.exit(0);
                }
                return;
            }
        }
    }


}
