package com.safshekan.parkban.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.safshekan.parkban.R;

public class ToastDialog extends DialogFragment {

    private Activity context;
    private AlertDialog alertDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_toast, null);
        builder.setView(view);
        alertDialog = builder.create();

        return alertDialog;
    }
}
