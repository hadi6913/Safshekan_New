package com.khodmohaseb.parkban.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.khodmohaseb.parkban.R;
import com.khodmohaseb.parkban.adapters.DialogAdapter;
import com.khodmohaseb.parkban.helper.ShowToast;
import com.khodmohaseb.parkban.persistence.models.ChargeAmount;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ChargeValueDialog extends DialogFragment {

    ListView listView;
    TextView titleDialog;
    EditText chargeValue;
    LinearLayout confirmLayout, cancelLayout;

    private AlertDialog alertDialog;
    private Activity context;
    private DialogAdapter dialogAdapter;
    private ArrayList<ChargeAmount> uiEntities;
    private ArrayList<ChargeAmount> arrayList;
    private int title;
    private List<String> list;
    private DialogCallBack callBack;
    private DialogOnItemSelectedListener dialogOnItemSelectedListener;

    public static NumberFormat formatter = new DecimalFormat("#,###");
    public static String CONFIRM = "confirm";
    public static String CANCEL = "cancel";

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_charge_value, null);
        builder.setView(view);
        alertDialog = builder.create();

        listView = view.findViewById(R.id.list_view);

        titleDialog = view.findViewById(R.id.dialog_title);
        titleDialog.setText(title);

        chargeValue = view.findViewById(R.id.charge_value);
        chargeValue.setFocusableInTouchMode(true);
        chargeValue.requestFocus();

        dialogAdapter = new DialogAdapter(uiEntities, getActivity());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (dialogOnItemSelectedListener != null)
                    dialogOnItemSelectedListener.OnItemSelected(uiEntities.get(position));
                chargeValue.setText(String.valueOf(uiEntities.get(position).getValue()));
                // dismiss();
            }
        });

        listView.setAdapter(dialogAdapter);
        alertDialog.setCanceledOnTouchOutside(false);

        confirmLayout = view.findViewById(R.id.confirm);
        cancelLayout = view.findViewById(R.id.cancel);


        confirmLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chargeValue.getText().toString().equals("")) {
                    ShowToast.getInstance().showWarning(context, R.string.charge_no_value);
                    return;
                } else {
                    alertDialog.dismiss();
                    callBack.onCallBack(CONFIRM, Long.parseLong(chargeValue.getText().toString()));
                }
            }
        });

        cancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                callBack.onCallBack(CANCEL, 0L);
            }
        });

        return alertDialog;
    }

    public ChargeValueDialog setItems(final ArrayList<ChargeAmount> uiEntities, int title) {
        this.uiEntities = (ArrayList<ChargeAmount>) uiEntities.clone();
        this.title = title;

        arrayList = new ArrayList<>();
        arrayList.addAll(uiEntities);

        list = new ArrayList<>();
        for (ChargeAmount u : uiEntities) {
            if (u instanceof ChargeAmount) {
                list.add(formatter.format(u.getValue()));
            }
        }
        return this;
    }

    public interface DialogOnItemSelectedListener {
        void OnItemSelected(Object selectedItem);
    }

    public void setCallBack(DialogCallBack callBack) {
        this.callBack = callBack;
    }

    public interface DialogCallBack {
        void onCallBack(String state, long chargeValue);
    }
}
