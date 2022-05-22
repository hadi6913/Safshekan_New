package com.safshekan.parkban;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.safshekan.parkban.databinding.ActivityReportBinding;
import com.safshekan.parkban.helper.DateTimeHelper;
import com.safshekan.parkban.viewmodels.ReportViewModel;

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
