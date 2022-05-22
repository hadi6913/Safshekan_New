package com.safshekan.parkban.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Toast;

import com.safshekan.parkban.BaseActivity;
import com.safshekan.parkban.LoginActivity;
import com.safshekan.parkban.R;
import com.safshekan.parkban.persistence.models.ResponseResultType;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import es.dmoral.toasty.Toasty;

public class ShowToast {

    private static ShowToast instance;

    private ShowToast() {
    }

    public static ShowToast getInstance() {
        if (instance == null)
            instance = new ShowToast();
        return instance;
    }

    public void showError(Context context, int msg) {
        Crouton.cancelAllCroutons();
        Style style = new Style.Builder()
                .setBackgroundColorValue(Color.parseColor("#D50000"))
                .setGravity(Gravity.CENTER_HORIZONTAL)
                .setHeight(150)
                .setTextColorValue(Color.parseColor("#ffffff")).build();
        Toast t = null;
        if (msg == ResponseResultType.InvalidUserNameOrPassword.getValue()) {
            if (BaseActivity.DeviceModel.contains("SM-A5"))
                Crouton.showText(((Activity) context), ResponseResultType.InvalidUserNameOrPassword.getDescription(), style);
            else
                t = Toasty.custom(context, ResponseResultType.InvalidUserNameOrPassword.getDescription(), context.getResources().getDrawable(R.mipmap.sign), context.getResources().getColor(R.color.errorColor), Toast.LENGTH_LONG, true, true);
        } else if (msg == ResponseResultType.CanNotModify.getValue()) {
            if (BaseActivity.DeviceModel.contains("SM-A5"))
                Crouton.showText(((Activity) context), ResponseResultType.CanNotModify.getDescription(), style);
            else
                t = Toasty.custom(context, ResponseResultType.CanNotModify.getDescription(), context.getResources().getDrawable(R.mipmap.sign), context.getResources().getColor(R.color.errorColor), Toast.LENGTH_LONG, true, true);
        } else if (msg == ResponseResultType.ErrorOnHttpGet.getValue()) {
            if (BaseActivity.DeviceModel.contains("SM-A5"))
                Crouton.showText(((Activity) context), ResponseResultType.ErrorOnHttpGet.getDescription(), style);
            else
                t = Toasty.custom(context, ResponseResultType.ErrorOnHttpGet.getDescription(), context.getResources().getDrawable(R.mipmap.sign), context.getResources().getColor(R.color.errorColor), Toast.LENGTH_LONG, true, true);
        } else if (msg == ResponseResultType.ErrorOnHttpPost.getValue()) {
            if (BaseActivity.DeviceModel.contains("SM-A5"))
                Crouton.showText(((Activity) context), ResponseResultType.ErrorOnHttpPost.getDescription(), style);
            else
                t = Toasty.custom(context, ResponseResultType.ErrorOnHttpPost.getDescription(), context.getResources().getDrawable(R.mipmap.sign), context.getResources().getColor(R.color.errorColor), Toast.LENGTH_LONG, true, true);
        } else if (msg == ResponseResultType.CanNotFindPermission.getValue()) {
            if (BaseActivity.DeviceModel.contains("SM-A5"))
                Crouton.showText(((Activity) context), ResponseResultType.CanNotFindPermission.getDescription(), style);
            else
                t = Toasty.custom(context, ResponseResultType.CanNotFindPermission.getDescription(), context.getResources().getDrawable(R.mipmap.sign), context.getResources().getColor(R.color.errorColor), Toast.LENGTH_LONG, true, true);
        } else if (msg == ResponseResultType.CanNotFindUser.getValue()) {
            if (BaseActivity.DeviceModel.contains("SM-A5"))
                Crouton.showText(((Activity) context), ResponseResultType.CanNotFindUser.getDescription(), style);
            else
                t = Toasty.custom(context, ResponseResultType.CanNotFindUser.getDescription(), context.getResources().getDrawable(R.mipmap.sign), context.getResources().getColor(R.color.errorColor), Toast.LENGTH_LONG, true, true);
        } else if (msg == ResponseResultType.UserIsDeactive.getValue()) {
            if (BaseActivity.DeviceModel.contains("SM-A5"))
                Crouton.showText(((Activity) context), ResponseResultType.UserIsDeactive.getDescription(), style);
            else
                t = Toasty.custom(context, ResponseResultType.UserIsDeactive.getDescription(), context.getResources().getDrawable(R.mipmap.sign), context.getResources().getColor(R.color.errorColor), Toast.LENGTH_LONG, true, true);
        } else if (msg == ResponseResultType.CreditIsNotEnough.getValue()) {
            if (BaseActivity.DeviceModel.contains("SM-A5"))
                Crouton.showText(((Activity) context), ResponseResultType.CreditIsNotEnough.getDescription(), style);
            else
                t = Toasty.custom(context, ResponseResultType.CreditIsNotEnough.getDescription(), context.getResources().getDrawable(R.mipmap.sign), context.getResources().getColor(R.color.errorColor), Toast.LENGTH_LONG, true, true);
        } else if (msg == ResponseResultType.Fail.getValue()) {
            if (BaseActivity.DeviceModel.contains("SM-A5"))
                Crouton.showText(((Activity) context), context.getResources().getString(R.string.unhandled_error_from_server), style);
            else
                t = Toasty.custom(context, context.getResources().getString(R.string.unhandled_error_from_server), context.getResources().getDrawable(R.mipmap.sign), context.getResources().getColor(R.color.errorColor), Toast.LENGTH_LONG, true, true);
        } else if (msg == ResponseResultType.CanNotLoad.getValue()) {
            if (BaseActivity.DeviceModel.contains("SM-A5"))
                Crouton.showText(((Activity) context), ResponseResultType.CanNotLoad.getDescription(), style);
            else
                t = Toasty.custom(context, ResponseResultType.CanNotLoad.getDescription(), context.getResources().getDrawable(R.mipmap.sign), context.getResources().getColor(R.color.errorColor), Toast.LENGTH_LONG, true, true);
        } else if (msg == ResponseResultType.InvalidToken.getValue()) {
            t = Toasty.custom(context, context.getResources().getString(R.string.un_authorized), context.getResources().getDrawable(R.mipmap.sign), context.getResources().getColor(R.color.infoColor), Toast.LENGTH_LONG, true, true);
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        } else if (msg == ResponseResultType.ExpireToken.getValue()) {
            t = Toasty.custom(context, context.getResources().getString(R.string.un_authorized), context.getResources().getDrawable(R.mipmap.sign), context.getResources().getColor(R.color.infoColor), Toast.LENGTH_LONG, true, true);
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        } else {
            if (BaseActivity.DeviceModel.contains("SM-A5"))
                Crouton.showText(((Activity) context), context.getResources().getString(msg), style);
            else
                t = Toasty.custom(context, context.getResources().getString(msg), context.getResources().getDrawable(R.mipmap.sign), context.getResources().getColor(R.color.errorColor), Toast.LENGTH_LONG, true, true);
        }

        if (t != null) {
            t.setGravity(Gravity.TOP, 0, 130);
            t.show();
        }
    }

    public void showErrorStringMsg(Context context, String msg) {
        Crouton.cancelAllCroutons();
        if (BaseActivity.DeviceModel.contains("SM-A5")) {

            Style style = new Style.Builder()
                    .setBackgroundColorValue(Color.parseColor("#f509a146"))
                    .setGravity(Gravity.CENTER_HORIZONTAL)
                    .setHeight(150)
                    .setTextColorValue(Color.parseColor("#ffffff")).build();
            Crouton.showText(((Activity) context), msg, style);

        } else {
            Toast t = Toasty.error(context, msg, Toast.LENGTH_LONG, true);
            t.setGravity(Gravity.TOP, 0, 130);
            t.show();
        }
    }

    public void showSuccess(Context context, int msg) {
        Crouton.cancelAllCroutons();
        if (BaseActivity.DeviceModel.contains("SM-A5")) {

            Style style = new Style.Builder()
                    .setBackgroundColorValue(Color.parseColor("#f509a146"))
                    .setGravity(Gravity.CENTER_HORIZONTAL)
                    .setHeight(150)
                    .setTextColorValue(Color.parseColor("#ffffff")).build();
            Crouton.showText(((Activity) context), context.getResources().getString(msg), style);

        } else {
            Toast t = Toasty.success(context, context.getResources().getString(msg), Toast.LENGTH_SHORT, true);
            t.setGravity(Gravity.TOP, 0, 130);
            t.show();
        }
    }

    public void showWarning(Context context, int msg) {
        Crouton.cancelAllCroutons();
        if (BaseActivity.DeviceModel.contains("SM-A5")) {
            Style style = new Style.Builder()
                    .setBackgroundColorValue(Color.parseColor("#FFA900"))
                    .setGravity(Gravity.CENTER_HORIZONTAL)
                    .setHeight(150)
                    .setTextColorValue(Color.parseColor("#ffffff")).build();
            Crouton.showText(((Activity) context), context.getResources().getString(msg), style);
        } else {
            Toast t = Toasty.custom(context, context.getResources().getString(msg), context.getResources().getDrawable(R.mipmap.warning), context.getResources().getColor(R.color.warningColor), Toast.LENGTH_LONG, true, true);
            t.setGravity(Gravity.TOP, 0, 130);
            t.show();
        }
    }

    public void showExit(Context context, int msg) {
        Crouton.cancelAllCroutons();
        if (BaseActivity.DeviceModel.contains("SM-A5")) {
            Style style = new Style.Builder()
                    .setBackgroundColorValue(Color.parseColor("#616161"))
                    .setGravity(Gravity.CENTER_HORIZONTAL)
                    .setHeight(150)
                    .setTextColorValue(Color.parseColor("#ffffff")).build();
            Crouton.showText(((Activity) context), context.getResources().getString(msg), style);
        } else {
            Toast t = Toasty.custom(context, context.getResources().getString(msg), context.getResources().getDrawable(R.mipmap.sign), context.getResources().getColor(R.color.dark_grey), Toast.LENGTH_SHORT, false, true);
            t.setGravity(Gravity.TOP, 0, 130);
            t.show();
        }
    }


}
