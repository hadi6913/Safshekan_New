package com.safshekan.parkban;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.safshekan.parkban.databinding.ActivitySettingBinding;
import com.safshekan.parkban.viewmodels.SettingViewModel;

public class SettingActivity extends BaseActivity {

    private ActivitySettingBinding binding;
    private SettingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);

        viewModel = ViewModelProviders.of(this).get(SettingViewModel.class);
        binding.setViewModel(viewModel);

        binding.setLifecycleOwner(this);

        viewModel.init(this);

    }

    @Override
    public void onBackPressed() {
        viewModel.backSelect(this);

    }
}
