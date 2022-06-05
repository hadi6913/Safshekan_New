package com.khodmohaseb.parkban.utils;

import android.databinding.BindingAdapter;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;

public class BindingUtils {

    @BindingAdapter("animatedVisibility")
    public static void setVisible(final View view, long duration) {

        AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
        animation1.setDuration(duration);
//        animation1.setStartOffset(5000);
        animation1.setFillAfter(true);
        view.startAnimation(animation1);

    }


    @BindingAdapter("animatedVisibilityWink")
    public static void setVisibleWink(final View view, long duration) {

        AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
        animation1.setDuration(duration);
//        animation1.setStartOffset(5000);
        animation1.setFillAfter(true);
        animation1.setRepeatCount(Animation.INFINITE);
        animation1.setRepeatMode(Animation.REVERSE);
        view.startAnimation(animation1);

    }


    @BindingAdapter("animatedVisibilityScale")
    public static void setVisibleScale(final View view, long duration) {


        AnimationSet as = new AnimationSet(true);
        as.setFillEnabled(true);
        as.setRepeatMode(Animation.REVERSE);
        as.setRepeatCount(Animation.INFINITE);

        ScaleAnimation scaleAnimation = new ScaleAnimation(0.3f,1f,0.3f,1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(2000);
        as.addAnimation(scaleAnimation);

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,300,0.5f,300,0.5f);
        rotateAnimation.setDuration(10000);
        rotateAnimation.setStartOffset(2000);// allowing 2000 milliseconds for ta to finish
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setRepeatMode(Animation.REVERSE);
        as.addAnimation(rotateAnimation);


        view.startAnimation(as);



    }


    @BindingAdapter("animatedInVisibility")
    public static void setInVisible(final View view, long duration) {

        AlphaAnimation animation1 = new AlphaAnimation(1.0f, 1.0f);
        animation1.setDuration(duration);
//        animation1.setStartOffset(5000);
        animation1.setFillAfter(true);
        view.startAnimation(animation1);

    }


    @BindingAdapter("app:onFocusChange")
    public static void onFocusChange(EditText text, final View.OnFocusChangeListener listener) {
        text.setOnFocusChangeListener(listener);
    }


}
