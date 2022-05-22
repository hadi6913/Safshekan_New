package com.safshekan.parkban;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.os.Bundle;

import com.safshekan.parkban.databinding.ActivityShiftBinding;
import com.safshekan.parkban.helper.DateTimeHelper;
import com.safshekan.parkban.viewmodels.ShiftViewModel;

import java.util.Date;

public class ShiftActivity extends BaseActivity {

    private ActivityShiftBinding binding;
    private Date BeginDate, EndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_shift);
        ShiftViewModel viewModel = ViewModelProviders.of(this).get(ShiftViewModel.class);
        binding.setViewModel(viewModel);

        binding.setLifecycleOwner(this);

        BeginDate = DateTimeHelper.getBeginCurrentMonth();
        EndDate = DateTimeHelper.getEndCurrentMonth();
        binding.fromDatePicker.setDisplayDate(BeginDate);
        binding.toDatePicker.setDisplayDate(EndDate);

        viewModel.init(this);

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

    public String getStartDate() {
        return binding.fromDatePicker.getDate();
    }

    public String getEndDate() {
        return binding.toDatePicker.getDate();
    }
}
