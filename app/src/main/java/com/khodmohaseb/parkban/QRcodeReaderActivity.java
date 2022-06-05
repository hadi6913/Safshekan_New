package com.khodmohaseb.parkban;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.KeyEvent;

import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.khodmohaseb.parkban.databinding.ActivityQrcodeReaderBinding;
import com.khodmohaseb.parkban.viewmodels.QRcodeReaderViewModel;

public class QRcodeReaderActivity extends BaseActivity {


    private DecoratedBarcodeView barcodeView;
    private QRcodeReaderViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityQrcodeReaderBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_qrcode_reader);
        barcodeView = findViewById(R.id.barcode_scanner);
        viewModel = ViewModelProviders.of(this).get(QRcodeReaderViewModel.class);
        binding.setViewModel(viewModel);
        viewModel.init(this, barcodeView);
        binding.setLifecycleOwner(this);




    }




    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

}
