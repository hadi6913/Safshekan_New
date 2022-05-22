package com.safshekan.parkban.viewmodels;

import android.animation.ValueAnimator;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.pax.dal.entity.EDetectMode;
import com.pax.dal.entity.EM1KeyType;
import com.pax.dal.entity.PiccCardInfo;
import com.safshekan.parkban.MainActivity;
import com.safshekan.parkban.MifareCardActivity;
import com.safshekan.parkban.R;
import com.safshekan.parkban.utils.Animation_Constant;
import com.safshekan.parkban.utils.IPiccUtils;
import com.safshekan.parkban.utils.MyBounceInterpolator;
import com.safshekan.parkban.utils.Utils;

import java.lang.ref.WeakReference;

public class MifareCardViewModel extends ViewModel {

    public static final String TAG = "MifareCardViewModel";

    //OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
    //sector 0
//    private byte readBlockNo = (byte) 0;//0
//    private byte readBlockNo = (byte) 1;//1
//    public byte readBlockNo = (byte) 2;//2
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


    private boolean authSuccess = false;
    private PiccCardInfo cardInfo;
    private byte[] mifareCardSerialNo;
    public static final int DETECT_SUCCESS = 0;

    final String PASSWORD_F = "FFFFFFFFFFFF";
    //OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO


    private String lastScannedText = "";
    private ImageView mImageView;
    private ImageView mImageViewLeft;
    private Context mContext;

    float[] hsv;
    int hue = 0;

    private MutableLiveData<Integer> backgroundAnimationColor;

    public MutableLiveData<Integer> getBackgroundAnimationColor() {
        if (backgroundAnimationColor == null)
            backgroundAnimationColor = new MutableLiveData<>();
        return backgroundAnimationColor;
    }


    public void init(Context context, ImageView imageView, ImageView left) {

        mContext = context;

        mImageView = imageView;

        mImageViewLeft = left;

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

        Animation animation = new TranslateAnimation(0, 0, 0, 200);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        mImageView.startAnimation(animation);


        Animation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);
        scaleAnimation.setInterpolator(new BounceInterpolator());
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        mImageViewLeft.startAnimation(scaleAnimation);





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
                intent.putExtra("isfrommifare", true);
                intent.putExtra("scanned_string", "backback");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);

            }
        }, Animation_Constant.ANIMATION_VALUE);


    }




}
