package com.khodmohaseb.parkban.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.khodmohaseb.parkban.R;
import com.khodmohaseb.parkban.adapters.DialogAdapterTimes;
import com.khodmohaseb.parkban.persistence.models.CarPlateHistory;

import java.util.ArrayList;

public class CarTimesDialog<T> extends DialogFragment {

    private ArrayList<T> uiEntities;
    private ArrayList<T> arrayList;
    private DialogAdapterTimes dialogAdapter;
    private ArrayList<Object> list;
    private Bitmap carImageBitmap;

    ListView listView;
    ImageView carImage ;

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_car_items_times, null);
        builder.setView(view);

        listView = view.findViewById(R.id.list_item);
        dialogAdapter = new DialogAdapterTimes(list, getActivity());
        listView.setAdapter(dialogAdapter);

        carImage = view.findViewById(R.id.car_image);
        carImage.setImageBitmap(carImageBitmap);

        return builder.create();
    }

    public CarTimesDialog setItem (final ArrayList<T> uiEntities , Bitmap image){
        this.uiEntities = (ArrayList<T>) uiEntities.clone();
        carImageBitmap = image;

        arrayList = new ArrayList<>();
        arrayList.addAll(uiEntities);

        list = new ArrayList<>();
        for (T u : uiEntities) {
            if (u instanceof CarPlateHistory){
                list.add(u);
            }
        }
        return this;
    }
}
