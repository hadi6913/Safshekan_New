package com.safshekan.parkban.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.safshekan.parkban.R;

public class EditParkSpaceDialog extends DialogFragment {

    LinearLayout confirmLayout, cancelLayout , newParkLayout;
    TextView messageTextView;

    private Activity context;
    private Dialog alertDialog;
    private DialogCallBack callBack;
    private String message;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_park_space, null);
        builder.setView(view);
        alertDialog = builder.create();

        messageTextView = view.findViewById(R.id.messageTextView);
        messageTextView.setText(message);

        confirmLayout = view.findViewById(R.id.confirm);
        cancelLayout = view.findViewById(R.id.cancel);
        newParkLayout = view.findViewById(R.id.newParkLayout);

        newParkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onCallBack( "newPark");
                dismiss();
            }
        });

        confirmLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onCallBack( "confirm");
                dismiss();
            }
        });

        cancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onCallBack("cancel");
                dismiss();
            }
        });

        return alertDialog;
    }

    public EditParkSpaceDialog setItem(String parkingSpaceName) {

        message = parkingSpaceName + " جایگاه پارک ";
        return this;
    }

    public void setCallBack(DialogCallBack callBack) {
        this.callBack = callBack;
    }

    public interface DialogCallBack {
        void onCallBack( String state);
    }
}
