package com.khodmohaseb.parkban.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.khodmohaseb.parkban.R;

public class ProgressDialog {

    private Dialog progressDialog;

    public void showProgress(Context context, final boolean show) {
        if (progressDialog == null) {
            progressDialog = new Dialog(context, android.R.style.Theme_Black);
            View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.control_progress_dialog, null);

            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
            progressDialog.setContentView(view);
        }
        if (show)
            progressDialog.show();
        else
            progressDialog.cancel();
    }
}
