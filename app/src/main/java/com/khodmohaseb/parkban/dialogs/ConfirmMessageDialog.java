package com.khodmohaseb.parkban.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;

import com.khodmohaseb.parkban.R;
import com.khodmohaseb.parkban.controls.PersianTextView;

public class ConfirmMessageDialog extends DialogFragment {

    public static String CONFIRM = "confirm";
    public static String CANCEL = "cancel";

    LinearLayout confirmLayout, cancelLayout;
    PersianTextView messageTextView;

    private Activity context;
    private Dialog alertDialog;
    private DialogCallBack callBack;
    private String message;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_confirm_message, null);
        builder.setView(view);
        alertDialog = builder.create();

        confirmLayout = view.findViewById(R.id.confirm);
        cancelLayout = view.findViewById(R.id.cancel);

        messageTextView = view.findViewById(R.id.message);
        messageTextView.setText(message);

        confirmLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                callBack.onCallBack(CONFIRM);
            }
        });

        cancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                callBack.onCallBack(CANCEL);
            }
        });

        alertDialog.setCanceledOnTouchOutside(false);

        return alertDialog;
    }

    public void setMessage(String msg) {
        message = msg;
    }

    public void setCallBack(DialogCallBack callBack) {
        this.callBack = callBack;
    }

    public interface DialogCallBack {
        void onCallBack(String state);
    }
}
