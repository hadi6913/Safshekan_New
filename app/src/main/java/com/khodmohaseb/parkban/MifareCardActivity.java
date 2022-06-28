package com.khodmohaseb.parkban;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.pax.dal.entity.EDetectMode;
import com.pax.dal.entity.EM1KeyType;
import com.pax.dal.entity.PiccCardInfo;
import com.khodmohaseb.parkban.databinding.ActivityMifareCardBinding;
import com.khodmohaseb.parkban.utils.IPiccUtils;
import com.khodmohaseb.parkban.utils.Utils;
import com.khodmohaseb.parkban.viewmodels.MifareCardViewModel;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;


public class MifareCardActivity extends BaseActivity {

    //sector 0
//    private byte readBlockNo = (byte) 0;//0
//    private byte readBlockNo = (byte) 1;//1
    private byte readBlockNo = (byte) 2;//2
//    private byte readBlockNo = (byte) 3;//3

//sector 1
//    private byte readBlockNo = (byte) 4;//4
//    private byte readBlockNo = (byte) 5;//5
//    private byte readBlockNo = (byte) 6;//6
//    private byte readBlockNo = (byte) 7;//7

//sector 2
//    private byte readBlockNo = (byte) 8;//8
//    private byte readBlockNo = (byte) 9;//9
//    private byte readBlockNo = (byte) 10;//10
//    private byte readBlockNo = (byte) 11;//11

//sector 3
//    private byte readBlockNo = (byte) 12;//12
//    private byte readBlockNo = (byte) 13;//13
//    private byte readBlockNo = (byte) 14;//14
//    private byte readBlockNo = (byte) 15;//15

//sector 4
//    private byte readBlockNo = (byte) 16;//16
//    private byte readBlockNo = (byte) 17;//17
//    private byte readBlockNo = (byte) 18;//18
//    private byte readBlockNo = (byte) 19;//19

//sector 5
//    private byte readBlockNo = (byte) 20;//20
//    private byte readBlockNo = (byte) 21;//21
//    private byte readBlockNo = (byte) 22;//22
//    private byte readBlockNo = (byte) 23;//23

//sector 6
//    private byte readBlockNo = (byte) 24;//24
//    private byte readBlockNo = (byte) 25;//25
//    private byte readBlockNo = (byte) 26;//26
//    private byte readBlockNo = (byte) 27;//27

//sector 7
//    private byte readBlockNo = (byte) 28;//28
//    private byte readBlockNo = (byte) 29;//29
//    private byte readBlockNo = (byte) 30;//30
//    private byte readBlockNo = (byte) 31;//31

//sector 8
//    private byte readBlockNo = (byte) 32;//32
//    private byte readBlockNo = (byte) 33;//33
//    private byte readBlockNo = (byte) 34;//34
//    private byte readBlockNo = (byte) 35;//35

//sector 9
//    private byte readBlockNo = (byte) 36;//36
//    private byte readBlockNo = (byte) 37;//37
//    private byte readBlockNo = (byte) 38;//38
//    private byte readBlockNo = (byte) 39;//39

//sector 10
//    private byte readBlockNo = (byte) 40;//40
//    private byte readBlockNo = (byte) 41;//41
//    private byte readBlockNo = (byte) 42;//42
//    private byte readBlockNo = (byte) 43;//43

//sector 11
//    private byte readBlockNo = (byte) 44;//44
//    private byte readBlockNo = (byte) 45;//45
//    private byte readBlockNo = (byte) 46;//46
//    private byte readBlockNo = (byte) 47;//47

//sector 12
//    private byte readBlockNo = (byte) 48;//48
//    private byte readBlockNo = (byte) 49;//49
//    private byte readBlockNo = (byte) 50;//50
//    private byte readBlockNo = (byte) 51;//51

//sector 13
//    private byte readBlockNo = (byte) 52;//52
//    private byte readBlockNo = (byte) 53;//53
//    private byte readBlockNo = (byte) 54;//54
//    private byte readBlockNo = (byte) 55;//55

//sector 14
//    private byte readBlockNo = (byte) 56;//56
//    private byte readBlockNo = (byte) 57;//57
//    private byte readBlockNo = (byte) 58;//58
//    private byte readBlockNo = (byte) 59;//59

//sector 15
//    private byte readBlockNo = (byte) 60;//60
//    private byte readBlockNo = (byte) 61;//61
//    private byte readBlockNo = (byte) 62;//62
//    private byte readBlockNo = (byte) 63;//63


    private PiccCardInfo cardInfo;
    private byte[] serialNo;
    public static final int DETECT_SUCCESS = 0;


    public static final String TAG = "xeagle6913 MifareCardActivity";


    private MifareCardViewModel viewModel;
    private ImageView cardImage;
    private ImageView arrowLeft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMifareCardBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_mifare_card);
        viewModel = ViewModelProviders.of(this).get(MifareCardViewModel.class);
        cardImage = findViewById(R.id.img_mifare_card_activity_mifare);
        arrowLeft = findViewById(R.id.img_arrow_left);
        binding.setViewModel(viewModel);
        viewModel.init(this, cardImage, arrowLeft);
        binding.setLifecycleOwner(this);


    }


    @Override
    protected void onResume() {
        super.onResume();
        if (IPiccUtils.getInstance().open()) {
            Log.d(TAG, "start_detecting_hardware>>>ok...detecting");
            new DetectThread().start();
        } else {
            Log.d(TAG, "start_detecting_hardware>>>fail");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (IPiccUtils.getInstance().close()) {
            Log.d(TAG, "stop_detecting_hardware>>>ok...stoped");
        } else {
            Log.d(TAG, "stop_detecting_hardware>>>fail...still detecting");
        }
    }


    class DetectThread extends Thread {
        boolean flag = true;

        @Override
        public void run() {
            super.run();
            while (flag) {
                cardInfo = IPiccUtils.getInstance().detect(EDetectMode.ONLY_M);
                if (cardInfo != null) {
                    flag = false;
                    serialNo = cardInfo.getSerialInfo();
                    myHandler.sendEmptyMessage(DETECT_SUCCESS);
                }
            }
        }
    }

    private MyHandler myHandler = new MyHandler(this);

    private class MyHandler extends Handler {
        private final WeakReference<MifareCardActivity> mActivity;
        private boolean authSuccess = false;

        private MyHandler(MifareCardActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MifareCardActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case DETECT_SUCCESS:
                        Log.d(TAG, "detected_card_serial_number >>> " + Utils.getInstance().bcdToStr(activity.cardInfo.getSerialInfo()));
                        //auth_blok
                        authSuccess = IPiccUtils.getInstance().m1Auth(EM1KeyType.TYPE_A, (byte) 2, Utils.getInstance().strToBcd("FFFFFFFFFFFF", Utils.EPaddingPosition.PADDING_LEFT), activity.cardInfo.getSerialInfo());
                        if (authSuccess) {
                            Log.d(TAG, "auth SUCCESS");
                            byte[] result = IPiccUtils.getInstance().m1Read((byte) 2);
                            if (result == null) {
                                return;
                            }

                            Log.d(TAG, "value in block raw binary>>>> " + result);
                            Log.d(TAG, "value in block raw binary convertted to string>>>> " + Utils.getInstance().bcdToStr(result));
                            String outPut = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                outPut = new String(result, StandardCharsets.UTF_8);
                            }
                            Log.d(TAG, "actual conveted value in block , in INTEGER format >>> "+outPut );
                            if (outPut.equals(null)) {
                                outPut = "";
                            }
                            Intent intent = new Intent(MifareCardActivity.this, MainActivity.class);
                            intent.putExtra("isfrommifare", true);
                            intent.putExtra("scanned_string", outPut);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);


                        } else {
                            Log.d(TAG, "auth FAILED");
                            Intent intent = new Intent(MifareCardActivity.this, MainActivity.class);
                            intent.putExtra("isfrommifare", true);
                            intent.putExtra("scanned_string", "");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                        break;
                    default:
                        Intent intent = new Intent(MifareCardActivity.this, MainActivity.class);
                        intent.putExtra("isfrommifare", true);
                        intent.putExtra("scanned_string", "");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                }
            }
        }
    }

}
