package com.safshekan.parkban.viewmodels;

import android.animation.ValueAnimator;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.safshekan.parkban.MainActivity;
import com.safshekan.parkban.R;
import com.safshekan.parkban.utils.Animation_Constant;
import com.safshekan.parkban.utils.MyBounceInterpolator;

import java.util.List;

//import com.noob.noobcameraflash.managers.NoobCameraManager;

public class QRcodeReaderViewModel extends ViewModel {

    public static final String TAG = "QRcodeReaderViewModel";

    private String lastScannedText = "";
    private DecoratedBarcodeView mBarcodeView;
    private Context mContext;


    float[] hsv;
    int hue = 0;


    private MutableLiveData<Integer> backgroundAnimationColor;
    private MutableLiveData<Boolean> flash_light_status;

    public MutableLiveData<Integer> getBackgroundAnimationColor() {
        if (backgroundAnimationColor == null)
            backgroundAnimationColor = new MutableLiveData<>();
        return backgroundAnimationColor;
    }


    public MutableLiveData<Boolean> getFlash_light_status() {
        if (flash_light_status == null)
            flash_light_status = new MutableLiveData<>();
        return flash_light_status;
    }


    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null || result.getText().equals(lastScannedText)) {
                // Prevent duplicate scans
                return;
            }


            lastScannedText = result.getText();
            Log.d(TAG, "lastScannedText >>> "+lastScannedText);
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("isfromqr", true);
            intent.putExtra("scanned_string", lastScannedText);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);



        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };


    public void init(Context context, DecoratedBarcodeView barcodeView) {

        mContext = context;
        mBarcodeView = barcodeView;
        mBarcodeView.decodeContinuous(callback);
        mBarcodeView.setStatusText(" ");
        getFlash_light_status().setValue(false);



        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.setDuration(2000);
        hsv = new float[3]; // Transition color
        hsv[1] = 1;
        hsv[2] = 1;
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                hsv[0] = 360 * animation.getAnimatedFraction();
                getBackgroundAnimationColor().setValue(Color.HSVToColor(hsv));
            }
        });
        anim.setRepeatCount(Animation.INFINITE);
        anim.start();





    }


    public void Back_Onclick(View view) {

        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_bubble_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //your code
                if (lastScannedText.equals(null)) {
                    lastScannedText = "";
                }
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("isfromqr", true);
                intent.putExtra("scanned_string", "backback");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);

            }
        }, Animation_Constant.ANIMATION_VALUE);





    }


    public void Flash_Light_Onclick(View view) {


        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_rotate_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        myAnim.setRepeatMode(1);
        view.startAnimation(myAnim);

        if (flash_light_status.getValue()) {
            getFlash_light_status().setValue(false);
            Log.d("QRcodeReaderViewModel", "Flash_Light_Onclick:  off");
            boolean isFlashAvailable = mContext.getApplicationContext().getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
            if (isFlashAvailable){
                mBarcodeView.setTorchOff();
            }



        } else {
            getFlash_light_status().setValue(true);
            Log.d("QRcodeReaderViewModel", "Flash_Light_Onclick:  on");
            boolean isFlashAvailable = mContext.getApplicationContext().getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
            if (isFlashAvailable){
                mBarcodeView.setTorchOn();
            }

        }

    }


}
