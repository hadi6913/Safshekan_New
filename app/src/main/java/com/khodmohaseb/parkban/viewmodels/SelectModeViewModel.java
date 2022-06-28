package com.khodmohaseb.parkban.viewmodels;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.khodmohaseb.parkban.BaseActivity;
import com.khodmohaseb.parkban.EnterMifareActivity;
import com.khodmohaseb.parkban.EnterQrActivity;
import com.khodmohaseb.parkban.ExitMifareActivity;
import com.khodmohaseb.parkban.ExitQrActivity;
import com.khodmohaseb.parkban.MifareCardActivity;
import com.khodmohaseb.parkban.PaymentSafshekanActivity;
import com.khodmohaseb.parkban.QRcodeReaderActivity;
import com.khodmohaseb.parkban.R;
import com.khodmohaseb.parkban.ReportActivity;
import com.khodmohaseb.parkban.anpr.farsi_ocr_anpr.FarsiOcrAnprProvider;
import com.khodmohaseb.parkban.core.anpr.BaseAnprProvider;
import com.khodmohaseb.parkban.core.anpr.helpers.PlateDetectionState;
import com.khodmohaseb.parkban.core.anpr.helpers.RidingType;
import com.khodmohaseb.parkban.core.anpr.onPlateDetectedCallback;
import com.khodmohaseb.parkban.helper.DateTimeHelper;
import com.khodmohaseb.parkban.helper.FontHelper;
import com.khodmohaseb.parkban.helper.ImageLoadHelper;
import com.khodmohaseb.parkban.helper.PrintParkbanApp;
import com.khodmohaseb.parkban.helper.ShowToast;
import com.khodmohaseb.parkban.persistence.ParkbanDatabase;
import com.khodmohaseb.parkban.persistence.models.CarPlate;
import com.khodmohaseb.parkban.persistence.models.ResponseResultType;
import com.khodmohaseb.parkban.repositories.ParkbanRepository;
import com.khodmohaseb.parkban.services.dto.ExitBillDto;
import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.GetParkingInfoResponse;
import com.khodmohaseb.parkban.utils.Animation_Constant;
import com.khodmohaseb.parkban.utils.MyBounceInterpolator;
import com.pax.dal.IDAL;
import com.pax.dal.IPrinter;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class SelectModeViewModel extends ViewModel {

    public static final String TAG = "xeagle6913 SelectModeViewModel";
    private Context context;
    private boolean doubleBackToExitPressedOnce = false;
    private static final long EXIT_TIMEOUT = 3000;

    public Typeface mFont;

    private GetParkingInfoResponse getParkingInfoResponse;


    public void init(final Context context,GetParkingInfoResponse getParkingInfoResponse) {
        this.context = context;
        this.getParkingInfoResponse = getParkingInfoResponse;
        mFont = Typeface.createFromAsset(context.getAssets(), "irsans.ttf");

    }



    public void enter_Onclick(final View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_bubble_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {




                if(getParkingInfoResponse.getCardKind().intValue() == 0){
                    //Mifare
//                    ((BaseActivity) context).finish();
                    Intent i = new Intent(context, EnterMifareActivity.class);
                    context.startActivity(i);


                }else{
                    //qr
//                    ((BaseActivity) context).finish();
                    Intent i = new Intent(context, EnterQrActivity.class);
                    context.startActivity(i);
                }











            }
        }, Animation_Constant.ANIMATION_VALUE);
    }


    public void exit_Onclick(final View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_bubble_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {






                if(getParkingInfoResponse.getCardKind().intValue() == 0){
                    //Mifare
//                    ((BaseActivity) context).finish();
                    Intent i = new Intent(context, ExitMifareActivity.class);
                    context.startActivity(i);



                }else{
                    //qr
//                    ((BaseActivity) context).finish();
                    Intent i = new Intent(context, ExitQrActivity.class);
                    context.startActivity(i);
                }











            }
        }, Animation_Constant.ANIMATION_VALUE);
    }



    public void backPress(Context context) {
        if (doubleBackToExitPressedOnce) {
            ((BaseActivity) (context)).finish();
        }
        this.doubleBackToExitPressedOnce = true;
        ShowToast.getInstance().showExit(context, R.string.click_back_again);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, EXIT_TIMEOUT);
    }

}
