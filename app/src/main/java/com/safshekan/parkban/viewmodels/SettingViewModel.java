package com.safshekan.parkban.viewmodels;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.view.View;

import com.safshekan.parkban.R;
import com.safshekan.parkban.dialogs.ConfirmMessageDialog;
import com.safshekan.parkban.helper.Preferences;
import com.safshekan.parkban.helper.ShowToast;

public class SettingViewModel extends ViewModel {

    private MutableLiveData<Boolean> isAutomatic;
    private MutableLiveData<Boolean> isManual;
    private boolean saveClick = false;

    public LiveData<Boolean> getAutomatic() {
        if (isAutomatic == null)
            isAutomatic = new MutableLiveData<>();
        return isAutomatic;
    }

    public LiveData<Boolean> getManual() {
        if (isManual == null)
            isManual = new MutableLiveData<>();
        return isManual;
    }

    public void init(Context context) {

        saveClick = false;

        if (isAutomatic == null)
            isAutomatic = new MutableLiveData<>();

        if (isManual == null)
            isManual = new MutableLiveData<>();

        if (Preferences.getManualSending(context)) {
            isAutomatic.setValue(false);
            isManual.setValue(true);
        }else {
            isManual.setValue(false);
            isAutomatic.setValue(true);
        }
    }

    public void getAutomaticCheck(View view) {
        saveClick = false;
        isAutomatic.setValue(true);
        isManual.setValue(false);
    }

    public void getManualCheck(View view) {
        saveClick = false;
        showConfirmDialog(view.getContext());
    }

    private void showConfirmDialog(Context context) {
        final ConfirmMessageDialog dialog = new ConfirmMessageDialog();
        dialog.setMessage(context.getResources().getString(R.string.msg_for_manual_send));
        dialog.show(((Activity) context).getFragmentManager(), "");
        dialog.setCallBack(new ConfirmMessageDialog.DialogCallBack() {
            @Override
            public void onCallBack(String state) {
                if (state == ConfirmMessageDialog.CONFIRM) {
                    isAutomatic.setValue(false);
                    isManual.setValue(true);
                } else {
                    isAutomatic.setValue(true);
                    isManual.setValue(false);
                }
            }
        });
    }

    public void saveClick(View view) {
        saveClick = true;

        if (isAutomatic.getValue())
            Preferences.setManualSending(false, view.getContext());
        else if (isManual.getValue() != null && isManual.getValue())
            Preferences.setManualSending(true, view.getContext());

        ShowToast.getInstance().showSuccess(view.getContext(), R.string.save_success);
    }

    public void backSelect(final Context context) {
        if (!saveClick)
            if (Preferences.getManualSending(context) != isManual.getValue()) {

                final ConfirmMessageDialog dialog = new ConfirmMessageDialog();
                dialog.setMessage(context.getResources().getString(R.string.save_necessary));
                dialog.show(((Activity) context).getFragmentManager(), "");
                dialog.setCallBack(new ConfirmMessageDialog.DialogCallBack() {
                    @Override
                    public void onCallBack(String state) {
                        if (state == ConfirmMessageDialog.CONFIRM) {
                            if (isAutomatic.getValue())
                                Preferences.setManualSending(false, context);
                            else if (isManual.getValue() != null && isManual.getValue())
                                Preferences.setManualSending(true, context);

                            ((Activity) context).finish();
                            ShowToast.getInstance().showSuccess(context, R.string.save_success);
                        } else {
                            ((Activity) context).finish();
                        }
                    }
                });
            } else {
                ((Activity) context).finish();
            }

        else
            ((Activity) context).finish();
    }

}
