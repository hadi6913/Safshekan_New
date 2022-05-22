package com.safshekan.parkban.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.safshekan.parkban.R;
import com.safshekan.parkban.core.anpr.helpers.RidingType;
import com.safshekan.parkban.helper.ShowToast;

import java.util.ArrayList;
import java.util.List;

public class EditPlateDialog extends DialogFragment {

    public static String Confirm = "confirm";

    private Activity context;
    private Dialog alertDialog;
    private DialogCallBack callBack;
    private String plate;
    private RidingType ridingType;
    private String spinnerSelected , plate0 , plate1 , plate2 , plate3;
    private boolean detected = false ;
    private RidingType type;

    RadioButton carRadioButton, motorRadioButton;
    LinearLayout carPlateLayout, motorPlateLayout, confirmClickLayout;
    EditText part0, part1, part2, mPart0, mPart1;
    String p0 , p1 , p2 , p3;
    Spinner spinner;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_plate, null);
        builder.setView(view);
        alertDialog = builder.create();

        carRadioButton = view.findViewById(R.id.car_plate_radio_button);
        motorRadioButton = view.findViewById(R.id.motorcycle_plate_radio_button);

        carPlateLayout = view.findViewById(R.id.car_layout);
        motorPlateLayout = view.findViewById(R.id.motor_layout);

        carRadioButton.setChecked(true);
        ridingType = RidingType.CAR;
        carPlateLayout.setVisibility(View.VISIBLE);
        motorPlateLayout.setVisibility(View.GONE);

        carRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carPlateLayout.setVisibility(View.VISIBLE);
                motorPlateLayout.setVisibility(View.GONE);
                ridingType = RidingType.CAR;
            }
        });

        motorRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motorPlateLayout.setVisibility(View.VISIBLE);
                carPlateLayout.setVisibility(View.GONE);
                ridingType = RidingType.MOTORCYCLE;
            }
        });

        part0 = view.findViewById(R.id.part0);
        part1 = view.findViewById(R.id.part1);
        part2 = view.findViewById(R.id.part2);
        mPart0 = view.findViewById(R.id.mpart0);
        mPart1 = view.findViewById(R.id.mpart1);

        spinner = view.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelected = String.valueOf(spinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("الف");
        spinnerArray.add("ب");
        spinnerArray.add("پ");
        spinnerArray.add("ت");
        spinnerArray.add("ب");
        spinnerArray.add("ث");
        spinnerArray.add("ج");
        spinnerArray.add("د");
        spinnerArray.add("ز");
        spinnerArray.add("ژ");
        spinnerArray.add("س");
        spinnerArray.add("ش");
        spinnerArray.add("ص");
        spinnerArray.add("ط");
        spinnerArray.add("ع");
        spinnerArray.add("ف");
        spinnerArray.add("ق");
        spinnerArray.add("ک");
        spinnerArray.add("گ");
        spinnerArray.add("ل");
        spinnerArray.add("م");
        spinnerArray.add("ن");
        spinnerArray.add("و");
        spinnerArray.add("ه");
        spinnerArray.add("ی");
        spinnerArray.add("S");
        spinnerArray.add("D");
        spinnerArray.add("♿");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (detected){
            if (type == RidingType.CAR){
                carRadioButton.setChecked(true);
                motorRadioButton.setChecked(false);
                carPlateLayout.setVisibility(View.VISIBLE);
                motorPlateLayout.setVisibility(View.GONE);
                ridingType = RidingType.CAR;

                part0.setText(p0);
                part1.setText(p2);
                part2.setText(p3);
                spinner.setSelection(adapter.getPosition(p1));
            }
            else{
                carRadioButton.setChecked(false);
                motorRadioButton.setChecked(true);
                motorPlateLayout.setVisibility(View.VISIBLE);
                carPlateLayout.setVisibility(View.GONE);
                ridingType = RidingType.MOTORCYCLE;

                mPart0.setText(p0);
                mPart1.setText(p1);
            }
        }


        confirmClickLayout = view.findViewById(R.id.confirmClick);
        confirmClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (carRadioButton.isChecked()) {
                    if (part0.getText().toString().equals("")  || part1.getText().toString().equals("") || part2.getText().toString().equals("")) {
                        ShowToast.getInstance().showWarning(context, R.string.plate_not_complete);
                        return;
                    }
                    if (part0.getText().length() < 2 || part1.getText().length() < 3 || part2.getText().length() < 2) {
                        ShowToast.getInstance().showWarning(context, R.string.plate_not_complete);
                        return;
                    } else {
                        plate = part0.getText() + spinnerSelected + part1.getText() + part2.getText();
                        plate0 = String.valueOf(part0.getText());
                        plate1 = spinnerSelected;
                        plate2 = String.valueOf(part1.getText());
                        plate3 = String.valueOf(part2.getText());

                    }
                } else {
                    if (mPart0.getText().toString().equals("")  || mPart1.getText().toString().equals("")) {
                        ShowToast.getInstance().showWarning(context, R.string.plate_not_complete);
                        return;
                    }
                    if (mPart0.getText().length() < 3 || mPart1.getText().length() < 5) {
                        ShowToast.getInstance().showWarning(context, R.string.plate_not_complete);
                        return;
                    } else {
                        plate = mPart0.getText() + "" + mPart1.getText();
                        plate0 = String.valueOf(mPart0.getText());
                        plate1 = String.valueOf(mPart1.getText());
                    }
                }

                callBack.onCallBack(plate ,plate0 , plate1 , plate2 , plate3, ridingType, Confirm);
                dismiss();
            }
        });

        alertDialog.setCanceledOnTouchOutside(false);

        return alertDialog;
    }

    public void setPlate(String part0 , String part1 , String part2 , String part3 , boolean detected , RidingType ridingType){
        p0 = part0;
        p1 = part1;
        p2 = part2;
        p3 = part3;
        this.detected = detected;
        type = ridingType;
    }

    public void setCallBack(DialogCallBack callBack) {
        this.callBack = callBack;
    }

    public interface DialogCallBack {
        void onCallBack(String plate ,String part0 , String part1 , String part2 , String part3, RidingType ridingType, String state);
    }
}
