package com.safshekan.parkban.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;

import com.safshekan.parkban.R;
import com.safshekan.parkban.controls.PersianTextView;

public class FailedConfirmDialog extends DialogFragment {




    public static String PRINT = "print";
    public static String RETRY = "retry";

    LinearLayout printLayout, retryLayout ;
    PersianTextView messageTextView;

    private Activity context;
    private Dialog alertDialog;
    private FailedConfirmDialog.DialogCallBack callBack;
    private String message;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_failed_confirm, null);
        builder.setView(view);
        alertDialog = builder.create();

        printLayout = view.findViewById(R.id.print_btn);
        retryLayout = view.findViewById(R.id.retry_btn);

        messageTextView = view.findViewById(R.id.txt_message);
        messageTextView.setText(message);

        printLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                callBack.onCallBack( PRINT);
            }
        });

        retryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                callBack.onCallBack(RETRY);
            }
        });

        alertDialog.setCanceledOnTouchOutside(false);

        return alertDialog;
    }

    public void setMessage(String msg){
        message = msg;
    }

    public void setCallBack(FailedConfirmDialog.DialogCallBack callBack) {
        this.callBack = callBack;
    }

    public interface DialogCallBack {
        void onCallBack( String state);
    }



}
