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


import com.khodmohaseb.parkban.databinding.ActivitySelectModeBinding;
import com.khodmohaseb.parkban.helper.ShowToast;
import com.khodmohaseb.parkban.services.ReaderHandler;
import com.khodmohaseb.parkban.utils.MyBounceInterpolator;
import com.khodmohaseb.parkban.viewmodels.MainViewModel;
import com.khodmohaseb.parkban.viewmodels.SelectModeViewModel;

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


public class SelectModeActivity extends BaseActivity {

    private static final String TAG = "SelectModeActivity";

    private SelectModeViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivitySelectModeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_select_mode);
        viewModel = ViewModelProviders.of(this).get(SelectModeViewModel.class);
        binding.setViewModel(viewModel);
        viewModel.init(this);
        binding.setLifecycleOwner(this);
    }


    @Override
    public void onBackPressed() {
        viewModel.backPress(SelectModeActivity.this);
    }


}
