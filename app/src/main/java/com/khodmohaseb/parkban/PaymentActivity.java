package com.khodmohaseb.parkban;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;

import com.khodmohaseb.parkban.databinding.ActivityPaymentBinding;
import com.khodmohaseb.parkban.viewmodels.PaymentViewModel;

public class PaymentActivity extends BaseActivity {

    private PaymentViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityPaymentBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_payment);

        viewModel = ViewModelProviders.of(this).get(PaymentViewModel.class);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.init(this);

        viewModel.getProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer value) {
                if (value > 0 ) {
                    showProgress(true);
                }else {
                    showProgress(false);
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i("--------------->>>>","onStop ");
        viewModel.stopEvent(PaymentActivity.this);
    }
}
