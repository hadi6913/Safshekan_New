package com.safshekan.parkban.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.safshekan.parkban.R;
import com.safshekan.parkban.helper.FontHelper;


public class PaymentResultDialog extends DialogFragment {

    LinearLayout confirmLayout;
    LinearLayout walletLayout;
    TextView receiptCode , walletDriver;
    ImageView QRImage;

    private Activity context;
    private Dialog alertDialog;
    private DialogCallBack callBack;
    private String code;
    private long cashAmount ;
    private boolean walletStatus;
    private String QRCodeBase64 ;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_payment_result, null);
        builder.setView(view);
        alertDialog = builder.create();

        receiptCode = view.findViewById(R.id.receipt_code);
        receiptCode.setText(code);

        confirmLayout = view.findViewById(R.id.confirm_layout);

        walletLayout = view.findViewById(R.id.wallet_driver_layout);
        walletDriver = view.findViewById(R.id.wallet_driver);
        walletDriver.setText(FontHelper.RialFormatter(cashAmount) + " ریال");

        QRImage = view.findViewById(R.id.QR_image);

        //convert Base64 to bitmap
        byte[] decodedString = Base64.decode(QRCodeBase64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        QRImage.setImageBitmap(decodedByte);

        if (walletStatus)
            walletLayout.setVisibility(View.GONE);
        else
            walletLayout.setVisibility(View.VISIBLE);

        confirmLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onCallBack("confirm");
                dismiss();
            }
        });

        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }

    public PaymentResultDialog setItem(String receiptCode , long wallet , String QRCode) {

        code = receiptCode;
        cashAmount = wallet;
        QRCodeBase64 = QRCode;

        return this;
    }

    public void setCallBack(DialogCallBack callBack) {
        this.callBack = callBack;
    }

    public interface DialogCallBack {
        void onCallBack(String state);
    }

    public void walletDisable(boolean status){
        walletStatus = status;

    }



}
