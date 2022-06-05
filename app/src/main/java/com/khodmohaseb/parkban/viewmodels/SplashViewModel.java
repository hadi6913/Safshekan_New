package com.khodmohaseb.parkban.viewmodels;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;

import com.khodmohaseb.parkban.BaseActivity;
import com.khodmohaseb.parkban.LoginActivity;
import com.khodmohaseb.parkban.SplashActivity;
import com.khodmohaseb.parkban.dialogs.ConfirmMessageDialog;
import com.khodmohaseb.parkban.persistence.ParkbanDatabase;
import com.khodmohaseb.parkban.repositories.ParkbanRepository;

public class SplashViewModel extends ViewModel {

    private ParkbanRepository parkbanRepository;
    private SplashActivity context;
    private static int SPLASH_TIME_OUT = 5000;
    public static final int STORAGE_PERMISSION = 1888;
    private boolean permission_access = false;
//    private MutableLiveData<Boolean> msgUpdate;

//    public LiveData<Boolean> getMsgUpdate() {
//        if (msgUpdate == null)
//            msgUpdate = new MutableLiveData<>();
//        return msgUpdate;
//    }

    public void init(SplashActivity context) {

        this.context = context;

//        if (msgUpdate == null)
//            msgUpdate = new MutableLiveData<>();
//        msgUpdate.setValue(false);

        if (parkbanRepository == null) {
            ParkbanDatabase.getInstance(context.getApplicationContext(), new ParkbanDatabase.DatabaseReadyCallback() {
                @Override
                public void onReady(ParkbanDatabase instance) {
                    parkbanRepository = new ParkbanRepository(instance);
//                    getLastVersion();
                    BaseActivity._isActive = true;
                    openLogin();
                }
            });
        }
//
//        GPSTracker gpsTracker = new GPSTracker(context);
//        gpsTracker.getLocation("");
    }

//    public void getLastVersion() {
//
//        parkbanRepository.getLastVersion(new ParkbanRepository.ServiceResultCallBack<StringResultDto>() {
//            @Override
//            public void onSuccess(StringResultDto result) {
//                String lastVersion = result.getValue();
//
//                if (needUpdateVersion(lastVersion)) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                != PackageManager.PERMISSION_GRANTED) {
//                            permission_access = true;
//                            showDialogMessage(context.getString(R.string.sd_card_permission), context.getString(R.string.new_version_permission));
//                        } else {
//                            if (permission_access) {
//                                BaseActivity._isActive = false;
//
//                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ParkbanServiceProvider.BASE_URL + "parkban/GetMobileAppFile"));
//                                context.startActivity(browserIntent);
//                            } else {
//                                permission_access = false;
//                                showDialogMessage("", context.getString(R.string.new_version_exit));
//                            }
//                        }
//                    } else {
//                        permission_access = false;
//                        showDialogMessage("", context.getString(R.string.new_version_exit));
//                    }
//                    return;
//                } else {
//                    BaseActivity._isActive = true;
//                    openLogin();
//                }
//            }
//
//            @Override
//            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
//                context.showProgress(false);
//                switch (resultType) {
//                    case RetrofitError: {
//                        ShowToast.getInstance().showError(context, R.string.exception_msg);
//                        BaseActivity._isActive = true;
//                        openLogin();
//                        break;
//                    }
//                    case ServerError:
//                        if (errorCode != 0)
//                            ShowToast.getInstance().showError(context, errorCode);
//                        else
//                            ShowToast.getInstance().showError(context, R.string.connection_failed);
//                        break;
//                    default:
//                        ShowToast.getInstance().showError(context, resultType.ordinal());
//                }
//            }
//        });
//    }

    private void openLogin() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(context, LoginActivity.class);
                    context.startActivity(i);
                    context.finish();
                }
            }, SPLASH_TIME_OUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showDialogMessage(String title, String message) {

        ConfirmMessageDialog dialog = new ConfirmMessageDialog();
        dialog.setMessage(message);
        dialog.show(context.getFragmentManager(), "");
        dialog.setCallBack(new ConfirmMessageDialog.DialogCallBack() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCallBack(String state) {
                if (state == ConfirmMessageDialog.CONFIRM) {
                    if (permission_access) {
                        context.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
                    } else {
                        BaseActivity._isActive = false;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ParkbanServiceProvider.BASE_URL + "parkban/GetMobileAppFile"));
//                            context.startActivity(browserIntent);
                        } else {
//                            msgUpdate.setValue(true);
//                            BaseActivity._isActive = false;
//                            new DownloadFileServices(context.getApplicationContext()).execute(ParkbanServiceProvider.BASE_URL + "parkban/GetMobileAppFile");
                        }
                    }
                } else if (state == ConfirmMessageDialog.CANCEL) {
                    context.finishAffinity();
                }
            }
        });
    }

    private boolean needUpdateVersion(String newVersion) {
        int oldMnr, oldRls, oldBld;
        int newMnr, newRls, newBld;

        String lastVersion = BaseActivity.Version;

        String[] newVer = newVersion.split("\\.");
        newMnr = Integer.valueOf(newVer[0]);
        newRls = Integer.valueOf(newVer[1]);
        newBld = Integer.valueOf(newVer[2]);

        String[] oldVer = lastVersion.split("\\.");
        oldMnr = Integer.valueOf(oldVer[0]);
        oldRls = Integer.valueOf(oldVer[1]);
        oldBld = Integer.valueOf(oldVer[2]);

        if (newMnr > oldMnr ||
                ((newMnr == oldMnr) && (newRls > oldRls)) ||
                ((newMnr == oldMnr) && (newRls == oldRls) && (newBld > oldBld)) ||
                ((newMnr == oldMnr) && (newRls == oldRls) && (newBld == 0) && (oldBld != 0)))
            return true;
        else
            return false;

    }

}
