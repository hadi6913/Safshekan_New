package com.khodmohaseb.parkban.helper;

import android.app.Application;
import android.os.Handler;

import com.pax.dal.IDAL;
import com.pax.dal.IPicc;
import com.pax.dal.entity.EPiccType;
import com.pax.neptunelite.api.NeptuneLiteUser;

public class PrintParkbanApp extends Application {

    private static PrintParkbanApp instance;
    private Handler mHandler;
    private IDAL idal;
    private IPicc ipicc;

    public static PrintParkbanApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mHandler = new Handler();

        try {
            idal = NeptuneLiteUser.getInstance().getDal(this);
            ipicc = idal.getPicc(EPiccType.INTERNAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runOnUiThread(Runnable r) {
        mHandler.post(r);
    }

    public void runOnUiThreadDelayed(Runnable r, long millis) {
        mHandler.postDelayed(r, millis);
    }

    public IDAL getIdal() {
        return idal;
    }
    public IPicc getIpicc() {
        return ipicc;
    }

}
