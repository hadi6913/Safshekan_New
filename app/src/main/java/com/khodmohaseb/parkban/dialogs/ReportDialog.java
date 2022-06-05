package com.khodmohaseb.parkban.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.khodmohaseb.parkban.R;
import com.khodmohaseb.parkban.controls.HomeCardView;

public class ReportDialog extends DialogFragment {

    public static String Func = "Func";
    public static String Charge = "Charge";

    HomeCardView functionalityLayout, chargeLayout ;

    private Activity context;
    private Dialog alertDialog;
    private DialogCallBack callBack;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_reports, null);
        builder.setView(view);
        alertDialog = builder.create();

        functionalityLayout = view.findViewById(R.id.functionality);
        chargeLayout = view.findViewById(R.id.charge);

        functionalityLayout.setOnClickListener(new HomeCardView.OnCardMenuListener() {
            @Override
            public void onClick(HomeCardView v) {
                alertDialog.dismiss();
                callBack.onCallBack(Func);
            }
        });

        chargeLayout.setOnClickListener(new HomeCardView.OnCardMenuListener() {
            @Override
            public void onClick(HomeCardView v) {
                alertDialog.dismiss();
                callBack.onCallBack(Charge);
            }
        });

        return alertDialog;
    }

    public void setCallBack(DialogCallBack callBack) {
        this.callBack = callBack;
    }

    public interface DialogCallBack {
        void onCallBack(String state);
    }
}
