package com.safshekan.parkban.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import com.safshekan.parkban.R;
import com.safshekan.parkban.controls.PersianTextView;

public class RecordsStatusDialog extends DialogFragment {

    private PersianTextView allRecords , successRecord , failRecord ;
    private String recordCount , successCount , failedCount;

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_records_status, null);
        builder.setView(view);

        allRecords = view.findViewById(R.id.allRecord);
        successRecord = view.findViewById(R.id.successRecord);
        failRecord = view.findViewById(R.id.failRecord);

        allRecords.setText(recordCount);
        successRecord.setText(successCount);
        failRecord.setText(failedCount);

        return builder.create();
    }

    public void setItem (String recordCount , String success , String failed){
        this.recordCount = recordCount;
        this.successCount = success;
        this.failedCount = failed;
    }

}
