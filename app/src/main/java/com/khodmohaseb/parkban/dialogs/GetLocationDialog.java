package com.khodmohaseb.parkban.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;

import com.khodmohaseb.parkban.BaseActivity;
import com.khodmohaseb.parkban.R;
import com.khodmohaseb.parkban.helper.LocationTracker;

public class GetLocationDialog extends DialogFragment {

    private Activity context;

    LinearLayout retry;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_location, null);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();

        retry = view.findViewById(R.id.retry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GPSTracker gpsTracker = new GPSTracker((Activity) v.getContext());
                //gpsTracker.getLocation("");
                BaseActivity.locationTracker = new LocationTracker(context);

                dismiss();
            }
        });

        alertDialog.setCanceledOnTouchOutside(false);

        return alertDialog;
    }

}
