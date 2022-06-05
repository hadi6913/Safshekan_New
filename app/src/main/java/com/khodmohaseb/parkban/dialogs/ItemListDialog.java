package com.khodmohaseb.parkban.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.khodmohaseb.parkban.R;
import com.khodmohaseb.parkban.adapters.DialogAdapter;
import com.khodmohaseb.parkban.helper.FontHelper;
import com.khodmohaseb.parkban.persistence.models.ChargeAmount;
import com.khodmohaseb.parkban.services.dto.WorkShiftTypes;

import java.util.ArrayList;
import java.util.List;

public class ItemListDialog<T> extends DialogFragment {

    ListView listView;
    TextView titleDialog;

    private ArrayList<T> uiEntities;
    private ArrayList<T> arrayList;
    private int title;
    private DialogAdapter dialogAdapter;
    private List<String> list;
    DialogOnItemSelectedListener dialogOnItemSelectedListener;
    private Activity context;
    private AlertDialog alertDialog;

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_items, null);
        builder.setView(view);
        alertDialog =  builder.create();

        listView = view.findViewById(R.id.list_view);

        titleDialog = view.findViewById(R.id.dialog_title);
        titleDialog.setText(title);

        dialogAdapter = new DialogAdapter(uiEntities, getActivity());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (dialogOnItemSelectedListener != null)
                    dialogOnItemSelectedListener.OnItemSelected(uiEntities.get(position));
                dismiss();
            }
        });

        listView.setAdapter(dialogAdapter);

        alertDialog.setCanceledOnTouchOutside(false);

        return alertDialog;
    }

    public ItemListDialog setItems(final ArrayList<T> uiEntities, int title, final DialogOnItemSelectedListener dialogOnItemSelectedListener) {
        this.uiEntities = (ArrayList<T>) uiEntities.clone();
        this.title = title;
        this.dialogOnItemSelectedListener = dialogOnItemSelectedListener;

        arrayList = new ArrayList<>();
        arrayList.addAll(uiEntities);

        list = new ArrayList<>();
        for (T u : uiEntities) {
            if (u instanceof WorkShiftTypes) {
                list.add(((WorkShiftTypes) u).getDescription());
            }
            if (u instanceof ChargeAmount){
                list.add(FontHelper.RialFormatter(((ChargeAmount) u).getValue()));
            }
        }
        return this;
    }

    public interface DialogOnItemSelectedListener {
        void OnItemSelected(Object selectedItem);
    }

}