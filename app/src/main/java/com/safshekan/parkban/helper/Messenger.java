package com.safshekan.parkban.helper;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.safshekan.parkban.R;
import com.safshekan.parkban.persistence.models.ResponseResultType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Messenger {
    public static final int Message_Info = 0;
    public static final int Message_Success = 1;
    public static final int Message_Warning = 2;
    public static final int Message_Error = 3;

    @IntDef({Message_Info, Message_Success, Message_Warning, Message_Error})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MessageType {
    }

    private static void makeText(Context context, String text, int length, @MessageType int messageType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.control_toast,null);

        TextView text_message = view.findViewById(R.id.toast_text);
        text_message.setText(text);
        if (text_message != null)
            text_message.setGravity(Gravity.CENTER);
        RelativeLayout toast_layout = view.findViewById(R.id.toast_layout);

        int backColor = 0;
        int icon = 0;
        switch (messageType) {
            case Message_Info:
                backColor = R.color.infoColor;
                break;
            case Message_Success:
                backColor = R.color.successColor;
                break;
            case Message_Error:
                backColor = R.color.errorColor;
                break;
            case Message_Warning:
                backColor = R.color.warningColor;
                break;
        }

        toast_layout.setBackgroundColor(context.getResources().getColor(backColor));

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, 130);
        toast.setDuration(length);
        toast.setView(view);
        toast.show();
    }

    public static void showMessage(Context context, String text) {
        makeText(context, text, Toast.LENGTH_LONG, Message_Info);
    }

    public static void showErrorMessageWithResponse(Context context, int responseCode) {
        if (responseCode == ResponseResultType.InvalidUserNameOrPassword.getValue()){
            showErrorMessage(context,ResponseResultType.InvalidUserNameOrPassword.getDescription());
        }
        else if (responseCode == ResponseResultType.CanNotModify.getValue()){
            showErrorMessage(context,ResponseResultType.CanNotModify.getDescription());
        }
        else if (responseCode == ResponseResultType.ErrorOnHttpGet.getValue()){
            showErrorMessage(context,ResponseResultType.ErrorOnHttpGet.getDescription());
        }
        else if (responseCode == ResponseResultType.ErrorOnHttpPost.getValue()){
            showErrorMessage(context,ResponseResultType.ErrorOnHttpPost.getDescription());
        }
        else if (responseCode == ResponseResultType.CanNotFindPermission.getValue()){
            showErrorMessage(context,ResponseResultType.CanNotFindPermission.getDescription());
        }
        else if (responseCode == ResponseResultType.CanNotFindUser.getValue()){
            showErrorMessage(context,ResponseResultType.CanNotFindUser.getDescription());
        }
        else if (responseCode == ResponseResultType.UserIsDeactive.getValue()){
            showErrorMessage(context,ResponseResultType.UserIsDeactive.getDescription());
        }
        else if (responseCode == ResponseResultType.CreditIsNotEnough.getValue()){
            showErrorMessage(context,ResponseResultType.CreditIsNotEnough.getDescription());
        }
        else if (responseCode == ResponseResultType.Fail.getValue()){
            showErrorMessage(context,R.string.unhandled_error_from_server);
        }
        else if (responseCode == ResponseResultType.CanNotLoad.getValue()){
            showErrorMessage(context,ResponseResultType.CanNotLoad.getDescription());
        }
        else if (responseCode == ResponseResultType.InvalidToken.getValue()){
//            showInfoMessage(context,R.string.un_authorized);
//            Intent i = new Intent(context, LoginActivity.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //context.startActivity(i);
        }else if (responseCode == ResponseResultType.ExpireToken.getValue()){
//            showInfoMessage(context,R.string.un_authorized);
//            Intent i = new Intent(context, LoginActivity.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
           // context.startActivity(i);
        }
    }

    public static void showErrorMessageFail(Context context,String msg, int id){
        if (msg.contains("Unauthorized") || id == 401){
//            showInfoMessage(context,R.string.un_authorized);
//            Intent i = new Intent(context, LoginActivity.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            context.startActivity(i);
        }
        //else
           // Messenger.showErrorMessage(context, R.string.unhandled_error_from_server);
    }

    public static void showErrorMessage(Context context, int resId) {
        showError(context, context.getString(resId));
    }

    public static void showErrorMessage(Context context, String text) {
        showError(context,text);
    }

    public static void shortErrorMessage(Context context, int resId) {
        makeText(context, context.getString(resId), Toast.LENGTH_SHORT, Message_Error);
    }

    public static void shortWarningMessage(Context context, String text) {
        makeText(context, text, Toast.LENGTH_LONG, Message_Warning);
    }

    public static void showInfoMessage(Context context, String text) {
        makeText(context, text, Toast.LENGTH_LONG, Message_Info);
    }

    public static void showInfoMessage(Context context, int text) {
        makeText(context, context.getString(text), Toast.LENGTH_LONG, Message_Info);
    }

    public static Boolean showCompleteSave(Context context, String text) {
        showMessageWithLength(context, text, Toast.LENGTH_SHORT, Message_Success);
        return true;
    }

    public static Boolean showCompleteError(Context context, String text) {
        showMessageWithLength(context, text, Toast.LENGTH_SHORT, Message_Error);
        return true;
    }

    public static Boolean showCompleteSaveLong(Context context, String text) {
        showMessageWithLength(context, text, Toast.LENGTH_LONG, Message_Success);
        return true;
    }

//    public static void confirmMessage(int title, int text, FragmentManager context, final ConfirmDialog.OnItemSelectedListener listener) {
//        ConfirmDialog newFragment = new ConfirmDialog();
//        newFragment.setItems(title, text, listener);
//        newFragment.show(context, "");
//    }

    private static void showError(Context context, String text) {
        makeText(context, text, Toast.LENGTH_LONG, Message_Error);
    }

    private static void showMessageWithLength(Context context, String text, int length, @MessageType int messageType) {
        Log.i("-----000-", "length " + length);
        makeText(context, text, length, messageType);
    }

}
