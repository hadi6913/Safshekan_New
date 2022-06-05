package com.khodmohaseb.parkban;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.os.Bundle;

import com.khodmohaseb.parkban.databinding.ActivityReportBinding;
import com.khodmohaseb.parkban.helper.DateTimeHelper;
import com.khodmohaseb.parkban.viewmodels.ReportViewModel;

import java.util.Date;

public class ReportActivity extends BaseActivity {


    private ActivityReportBinding binding;
    private Date BeginDate, EndDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_report);
        final ReportViewModel viewModel = ViewModelProviders.of(this).get(ReportViewModel.class);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        BeginDate = DateTimeHelper.getBeginCurrentMonth();
        EndDate = DateTimeHelper.getEndCurrentMonth();
        binding.fromDatePickerNew.setDisplayDate(BeginDate);
        binding.toDatePickerNew.setDisplayDate(EndDate);

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
        return binding.fromDatePickerNew.getDate();
    }

    public String getEndDate() {
        return binding.toDatePickerNew.getDate();
    }


}
