package com.safshekan.parkban.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.safshekan.parkban.R;
import com.safshekan.parkban.controls.PersianTextView;
import com.safshekan.parkban.helper.FontHelper;

public class PaymentDialog extends DialogFragment {


    private Activity context;
    private Dialog alertDialog;

    public Button printBtn;

    public PersianTextView entry_date_txt;
    public PersianTextView duration_date_txt;
    public PersianTextView cost_txt;
    public PersianTextView ozv_txt;
    public PersianTextView card_txt;


    public PersianTextView plate_txt_0;
    public PersianTextView plate_txt_1;
    public PersianTextView plate_txt_2;
    public PersianTextView plate_txt_3;
    public PersianTextView m_plate_txt_0;
    public PersianTextView m_plate_txt_1;


    public PersianTextView peygiri_txt;


    public RelativeLayout recipit_relativie_main;
    public RelativeLayout pelak_main_layoout;
    public LinearLayout cart_main_layout;
    public LinearLayout ozv_main_layout;
    public LinearLayout motor_main_layout;
    public LinearLayout car_main_layout;


    private boolean ozv_status = false;
    private boolean cart_status = false;
    private boolean pelak_status = false;
    private boolean car_status = false;
    private boolean motor_status = false;


    private String entryDate;
    private String duration;
    private String cost;
    private String ozv_number;
    private String cart_number;
    private String p0;
    private String p1;
    private String p2;
    private String p3;
    private String m0;
    private String m1;
    private String peygiri;


    private DialogCallBackNew callBack;


    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
        alertDialog.getWindow().setLayout(width, height);
        alertDialog.getWindow().setGravity(Gravity.TOP);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.payment_dialog_layout, null);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        printBtn = view.findViewById(R.id.print_recipit_btn);

        entry_date_txt = view.findViewById(R.id.entry_date);
        duration_date_txt = view.findViewById(R.id.duration_date);
        cost_txt = view.findViewById(R.id.cost_txt);
        ozv_txt = view.findViewById(R.id.ozv_txt);
        card_txt = view.findViewById(R.id.cart_txt);
        plate_txt_0 = view.findViewById(R.id.p0_layout);
        plate_txt_1 = view.findViewById(R.id.p1_layout);
        plate_txt_2 = view.findViewById(R.id.p2_layout);
        plate_txt_3 = view.findViewById(R.id.p3_layout);
        m_plate_txt_0 = view.findViewById(R.id.m0_layout);
        m_plate_txt_1 = view.findViewById(R.id.m1_layout);
        peygiri_txt = view.findViewById(R.id.txt_peygiri_layout);
        pelak_main_layoout = view.findViewById(R.id.plate_main_layout_layout);
        motor_main_layout = view.findViewById(R.id.motor_layout_layout);
        car_main_layout = view.findViewById(R.id.plate_layout_layout);
        ozv_main_layout = view.findViewById(R.id.ozv_recipit_layout_layout);
        cart_main_layout = view.findViewById(R.id.cart_layout_layout);
        recipit_relativie_main = view.findViewById(R.id.recipit_relativie_main);


        entry_date_txt.setText(entryDate);
        duration_date_txt.setText(duration);
        cost_txt.setText(FontHelper.RialFormatter(Long.parseLong(cost)) + " ریال");
        peygiri_txt.setText(peygiri);
        if (pelak_status == true) {
            pelak_main_layoout.setVisibility(View.VISIBLE);
            if (motor_status == true) {
                motor_main_layout.setVisibility(View.VISIBLE);
                car_main_layout.setVisibility(View.GONE);
                m_plate_txt_0.setText(m0);
                m_plate_txt_1.setText(m1);
            } else {
                car_main_layout.setVisibility(View.VISIBLE);
                motor_main_layout.setVisibility(View.GONE);
                plate_txt_0.setText(p0);
                plate_txt_1.setText(p1);
                plate_txt_2.setText(p2);
                plate_txt_3.setText(p3);
            }
        } else {
            pelak_main_layoout.setVisibility(View.GONE);
        }
        if (ozv_status == true) {
            ozv_main_layout.setVisibility(View.VISIBLE);
            ozv_txt.setText(ozv_number);
        } else {
            ozv_main_layout.setVisibility(View.GONE);
        }

        if (cart_status == true) {
            cart_main_layout.setVisibility(View.VISIBLE);
            card_txt.setText(cart_number);
        } else {
            cart_main_layout.setVisibility(View.GONE);
        }

        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Animation animation = new TranslateAnimation(0, 0, 0, -500);
                animation.setDuration(2000);
                animation.setFillAfter(true);
                recipit_relativie_main.startAnimation(animation);
                callBack.onCallBack("confirm");
            }
        });

        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;

    }


    public PaymentDialog setItem(String entryDate,
                                 String duration,
                                 String cost,
                                 String ozv_number,
                                 String cart_number,
                                 String p0,
                                 String p1,
                                 String p2,
                                 String p3,
                                 String m0,
                                 String m1,
                                 String peygiri,
                                 boolean ozv_status,
                                 boolean cart_status,
                                 boolean pelak_status,
                                 boolean motor_status,
                                 boolean car_status) {


        this.entryDate = entryDate;
        this.duration = duration;
        this.cost = cost;
        this.ozv_number = ozv_number;
        this.cart_number = cart_number;
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.m0 = m0;
        this.m1 = m1;
        this.peygiri = peygiri;
        this.ozv_status = ozv_status;
        this.cart_status = cart_status;
        this.pelak_status = pelak_status;
        this.motor_status = motor_status;
        this.car_status = car_status;
        return this;
    }

    public void setCallBack(DialogCallBackNew callBack) {
        this.callBack = callBack;
    }

    public interface DialogCallBackNew {
        void onCallBack(String state);
    }
}