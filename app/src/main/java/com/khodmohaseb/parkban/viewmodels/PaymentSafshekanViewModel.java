package com.khodmohaseb.parkban.viewmodels;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.device.PrinterManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.khodmohaseb.parkban.BaseActivity;
import com.khodmohaseb.parkban.R;
import com.khodmohaseb.parkban.controls.PersianTextView;
import com.khodmohaseb.parkban.dialogs.PaymentDialog;
import com.khodmohaseb.parkban.helper.FontHelper;
import com.khodmohaseb.parkban.helper.PrinterUtils;
import com.khodmohaseb.parkban.helper.ShowToast;
import com.khodmohaseb.parkban.persistence.ParkbanDatabase;
import com.khodmohaseb.parkban.persistence.models.ResponseResultType;
import com.khodmohaseb.parkban.repositories.ParkbanRepository;
import com.khodmohaseb.parkban.services.dto.CashBillDto;
import com.khodmohaseb.parkban.services.dto.FirstElectronicPaymentDto;
import com.khodmohaseb.parkban.services.dto.ForthElectronicPaymentDto;
import com.khodmohaseb.parkban.services.dto.ThirdElectronicPaymentRequestDto;
import com.khodmohaseb.parkban.services.dto.ThirdElectronicPaymentResponseDto;
import com.khodmohaseb.parkban.utils.Animation_Constant;
import com.khodmohaseb.parkban.utils.MyBounceInterpolator;

public class PaymentSafshekanViewModel extends ViewModel {

    public static final String TAG = "xeagle6913 PaymentSafViewModel";

    private Bitmap mBitmap;
    PrinterManager mPrinterManager;

    private MutableLiveData<Long> dumpIdId;

    private MutableLiveData<Boolean> pelak;
    private MutableLiveData<Boolean> member;
    private MutableLiveData<Boolean> kart;
    private MutableLiveData<Boolean> car;
    private MutableLiveData<Boolean> motor;

    private MutableLiveData<Boolean> cash_transaction;
    private MutableLiveData<Boolean> electronic_transaction;

    private MutableLiveData<String> commonCost;
    private MutableLiveData<String> solarEnterDateTime;
    private MutableLiveData<String> formatedDuration;
    private MutableLiveData<String> memberCode;
    private MutableLiveData<String> cardNumber;
    private MutableLiveData<String> plate__0;
    private MutableLiveData<String> plate__1;
    private MutableLiveData<String> plate__2;
    private MutableLiveData<String> plate__3;
    private MutableLiveData<String> mplate__0;
    private MutableLiveData<String> mplate__1;

    private MutableLiveData<String> peygiri_code;

    public MutableLiveData<Integer> progress = new MutableLiveData<>();
    private Context myContext;
    private ParkbanRepository parkbanRepository;
    private Handler receiverHandler;

    //Printer gray value 0-4
    private final static int DEF_PRINTER_HUE_VALUE = 0;
    private final static int MIN_PRINTER_HUE_VALUE = 0;
    private final static int MAX_PRINTER_HUE_VALUE = 4;

    //Print speed value 0-9
    private final static int DEF_PRINTER_SPEED_VALUE = 9;
    private final static int MIN_PRINTER_SPEED_VALUE = 0;
    private final static int MAX_PRINTER_SPEED_VALUE = 9;

    // Printer status
    private final static int PRNSTS_OK = 0;                //OK
    private final static int PRNSTS_OUT_OF_PAPER = -1;    //Out of paper
    private final static int PRNSTS_OVER_HEAT = -2;        //Over heat
    private final static int PRNSTS_UNDER_VOLTAGE = -3;    //under voltage
    private final static int PRNSTS_BUSY = -4;            //Device is busy
    private final static int PRNSTS_ERR = -256;            //Common error
    private final static int PRNSTS_ERR_DRIVER = -257;    //Printer Driver error

    public LiveData<Integer> getProgress() {
        return progress;
    }

    public MutableLiveData<Long> getDumpIdId() {
        if (dumpIdId == null)
            dumpIdId = new MutableLiveData<>();
        return dumpIdId;
    }

    public MutableLiveData<Boolean> getPelak() {
        if (pelak == null)
            pelak = new MutableLiveData<>();
        return pelak;
    }

    public MutableLiveData<Boolean> getMember() {
        if (member == null)
            member = new MutableLiveData<>();
        return member;
    }

    public MutableLiveData<Boolean> getKart() {
        if (kart == null)
            kart = new MutableLiveData<>();
        return kart;
    }

    public MutableLiveData<Boolean> getCar() {
        if (car == null)
            car = new MutableLiveData<>();
        return car;
    }

    public MutableLiveData<Boolean> getMotor() {
        if (motor == null)
            motor = new MutableLiveData<>();
        return motor;
    }

    public MutableLiveData<Boolean> getCash_transaction() {
        if (cash_transaction == null)
            cash_transaction = new MutableLiveData<>();
        return cash_transaction;
    }

    public MutableLiveData<Boolean> getElectronic_transaction() {
        if (electronic_transaction == null)
            electronic_transaction = new MutableLiveData<>();
        return electronic_transaction;
    }

    public MutableLiveData<String> getCommonCost() {
        if (commonCost == null)
            commonCost = new MutableLiveData<>();
        return commonCost;
    }

    public MutableLiveData<String> getSolarEnterDateTime() {
        if (solarEnterDateTime == null)
            solarEnterDateTime = new MutableLiveData<>();
        return solarEnterDateTime;
    }

    public MutableLiveData<String> getFormatedDuration() {
        if (formatedDuration == null)
            formatedDuration = new MutableLiveData<>();
        return formatedDuration;
    }

    public MutableLiveData<String> getMemberCode() {
        if (memberCode == null)
            memberCode = new MutableLiveData<>();
        return memberCode;
    }

    public MutableLiveData<String> getCardNumber() {
        if (cardNumber == null)
            cardNumber = new MutableLiveData<>();
        return cardNumber;
    }

    public MutableLiveData<String> getPlate__0() {
        if (plate__0 == null)
            plate__0 = new MutableLiveData<>();
        return plate__0;
    }

    public MutableLiveData<String> getPlate__1() {
        if (plate__1 == null)
            plate__1 = new MutableLiveData<>();
        return plate__1;
    }

    public MutableLiveData<String> getPlate__2() {
        if (plate__2 == null)
            plate__2 = new MutableLiveData<>();
        return plate__2;
    }

    public MutableLiveData<String> getPlate__3() {
        if (plate__3 == null)
            plate__3 = new MutableLiveData<>();
        return plate__3;
    }

    public MutableLiveData<String> getMplate__0() {
        if (mplate__0 == null)
            mplate__0 = new MutableLiveData<>();
        return mplate__0;
    }

    public MutableLiveData<String> getMplate__1() {
        if (mplate__1 == null)
            mplate__1 = new MutableLiveData<>();
        return mplate__1;
    }

    public MutableLiveData<String> getPeygiri_code() {
        if (peygiri_code == null)
            peygiri_code = new MutableLiveData<>();
        return peygiri_code;
    }

    public void init(final Context context) {
        myContext = context;
        getElectronic_transaction().setValue(true);
        getCash_transaction().setValue(false);
        new CustomThread().start();
        if (parkbanRepository == null) {
            ParkbanDatabase.getInstance(context.getApplicationContext(), new ParkbanDatabase.DatabaseReadyCallback() {
                @Override
                public void onReady(ParkbanDatabase instance) {
                    parkbanRepository = new ParkbanRepository(instance);
                }
            });
        }
    }

    public void cash_payment_CheckClick(View view) {
        cash_transaction.setValue(true);
        electronic_transaction.setValue(false);
    }

    public void electronic_payment_CheckClick(View view) {
        electronic_transaction.setValue(true);
        cash_transaction.setValue(false);
    }

    public void Pardakht_Onclick(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.btn_bubble_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (commonCost.getValue().equals("0")) {
                    ((Activity) myContext).finish();
                } else {
                    if (getCash_transaction().getValue()) {
                        //cash_way
                        parkbanRepository.cashePayment(getDumpIdId().getValue(), Long.valueOf(getCommonCost().getValue()), new ParkbanRepository.ServiceResultCallBack<CashBillDto.RecipeCash>() {
                            @Override
                            public void onSuccess(CashBillDto.RecipeCash result) {
                                ShowToast.getInstance().showSuccess(myContext, R.string.payment_success);
                                showDilaogAndPrint(myContext, result.getUserName());
                            }

                            @Override
                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                ShowToast.getInstance().showError(myContext, R.string.payment_failed);
                            }
                        });
                    } else {
                        //electronic_way
                        parkbanRepository.firstElectronicPayment(getDumpIdId().getValue(), Long.valueOf(getCommonCost().getValue()), new ParkbanRepository.ServiceResultCallBack<FirstElectronicPaymentDto.FirstElectronicPaymentResponse>() {
                            @Override
                            public void onSuccess(FirstElectronicPaymentDto.FirstElectronicPaymentResponse result) {
                                Log.d("elecronic_pardakht >>>", "step 1 >>> factor id  : " + result.getFactorId());
                                PreferenceManager.getDefaultSharedPreferences(myContext).edit().putLong("factorId", result.getFactorId()).apply();
//                                    fanavaPaymentIntent.putExtra("amount",Long.valueOf(getCommonCost().getValue()));


                                try {

                                    Intent fanavaPaymentIntent = new Intent("ir.totan.pos.view.cart.TXN");
                                    fanavaPaymentIntent.putExtra("type", 3);
                                    fanavaPaymentIntent.putExtra("amount", getCommonCost().getValue());
                                    fanavaPaymentIntent.putExtra("res_num", result.getFactorId());
                                    ((Activity) myContext).startActivityForResult(fanavaPaymentIntent, 103);

                                } catch (Exception ex) {
                                    Log.d(TAG, "exception is the calling payment intent");
                                    Log.d(TAG, "exception : " + ex.getMessage());
                                }


//                                try {
//                                    EasyHelper.purchaseTxn((Activity) myContext,
//                                            receiverHandler,
//                                            "1000",
//                                            "",
//                                            String.valueOf(result.getFactorId()),
//                                            "IRR",
//                                            "FA");
//                                } catch (EasyHelper.PahpatException e) {
//                                    e.printStackTrace();
//                                    Log.d(TAG, " error for sending intent to pahpat >>> " + e.getMessage());
//                                }
                            }

                            @Override
                            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                                ShowToast.getInstance().showError(myContext, R.string.connection_failed);
                            }
                        });
                    }
                }
            }
        }, Animation_Constant.ANIMATION_VALUE);
    }

    public void part3(final Context mContext1, final Bundle b) {

//                        "Status   :" + b.getString("result") + '\n' +
//                        "rrn    :" + b.getString("rrn", null) + '\n' +
//                        "date    :"+ b.getLong("date", -1) + '\n' +
//                        "trace    :"+ b.getString("trace", null) + '\n' +
//                        "pan    :"+ b.getString("pan", null) + '\n' +
//                        "amount   :" + b.getString("amount", null) + '\n' +
//                        "res_num   :"+ b.getLong("res_num", -1) + '\n' +
//                        "charge_serial    :" + b.getString("charge_serial", null) + '\n' +
//                        "charge_pin    :" + b.getString("charge_pin", null) + '\n' +
//                        "message    :" + b.getString("message", null) + '\n';


        final ThirdElectronicPaymentRequestDto.ThirdPart requestObj = new ThirdElectronicPaymentRequestDto.ThirdPart();
        requestObj.setAmount(Long.parseLong(b.getString("amount")));
        requestObj.setId(b.getLong("res_num"));
        requestObj.setRRN(b.getString("rrn"));
        requestObj.setTraceNo(b.getString("trace"));
        PreferenceManager.getDefaultSharedPreferences(myContext).edit().putString("last_trace_number", b.getString("trace")).apply();
        requestObj.setApprovalCode("1" + b.getLong("res_num"));
        requestObj.setTransActionDateTime(b.getString("date"));


        parkbanRepository.thirdElectronicPayment(requestObj, new ParkbanRepository.ServiceResultCallBack<ThirdElectronicPaymentResponseDto>() {
            @Override
            public void onSuccess(ThirdElectronicPaymentResponseDto result) {
                if (result.getValue().isFactorId() == true) {
                    try {


                        part4(myContext, b.getLong("res_num") + "");


                    } catch (Exception e) {
                        ShowToast.getInstance().showError(mContext1, R.string.connection_failed);
                    }
                } else {
                    ShowToast.getInstance().showError(mContext1, R.string.connection_failed);
                }
            }

            @Override
            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                progress.setValue(0);
                switch (resultType) {
                    case RetrofitError:
                        ShowToast.getInstance().showError(mContext1, R.string.exception_msg);
                        break;
                    case ServerError:
                        if (errorCode != 0)
                            ShowToast.getInstance().showError(mContext1, errorCode);
                        else
                            ShowToast.getInstance().showError(mContext1, R.string.connection_failed);
                        break;
                    default:
                        ShowToast.getInstance().showError(mContext1, resultType.ordinal());
                }
            }
        });
    }

    public void part4(final Context mContext2, String factorId) {
        parkbanRepository.forthElectronicPayment(Long.parseLong(factorId), new ParkbanRepository.ServiceResultCallBack<ForthElectronicPaymentDto>() {
            @Override
            public void onSuccess(ForthElectronicPaymentDto result) {
                progress.setValue(0);
                Log.d("elecronic_pardakht >>>", "step 4 >>> Resid Nahaee >>>  " + result.getValue().getResult());
//                PreferenceManager.getDefaultSharedPreferences(myContext).edit().putString("ResidNahaee", result.getValue().getResult()).apply();
                ShowToast.getInstance().showSuccess(myContext, R.string.payment_success);

                showDilaogAndPrint(myContext, result.getValue().getResult());
            }

            @Override
            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                ShowToast.getInstance().showError(mContext2, R.string.connection_failed);
            }
        });
    }

    public void showDilaogAndPrint(final Context mContext2, String residNahaee) {
        getPeygiri_code().setValue(residNahaee);
        Log.d("pardakht >>> ", " shomare resid :" + residNahaee);
        Log.d("pardakht >>> ", " mablagh ghabel takhlie :" + commonCost.getValue());
        Log.d("pardakht >>> ", " entry date time :" + getSolarEnterDateTime().getValue());
        Log.d("pardakht >>> ", " duration :" + getFormatedDuration().getValue());
        if ((getPlate__0().getValue() == null) && (getMplate__0().getValue() == null)) {
            getCar().setValue(false);
            getMotor().setValue(false);
        } else {
            if (getCar().getValue()) {
                Log.d("pardakht >>> ", " car");
                Log.d("pardakht >>> ", " shomare pelak :" + getPlate__0().getValue() + getPlate__1().getValue() + getPlate__2().getValue() + getPlate__3().getValue());
            } else {
                Log.d("pardakht >>> ", " motor");
                Log.d("pardakht >>> ", " shomare pelak :" + getMplate__0().getValue() + getMplate__1().getValue());
            }
        }
        Log.d("pardakht >>> ", " member code :" + getMemberCode().getValue());
        Log.d("pardakht >>> ", " card code :" + getCardNumber().getValue());
        boolean cartStatus = true;
        boolean ozvStatus = true;
        boolean pelakStatus = true;
        if (getPlate__0().getValue() == null && getMplate__0().getValue() == null) {
            pelakStatus = false;
        }
        if (getMemberCode().getValue() == null) {
            ozvStatus = false;
        }
        if (getCardNumber().getValue() == null) {
            cartStatus = false;
        }
        final PaymentDialog dialog = new PaymentDialog();
        dialog.setItem(
                getSolarEnterDateTime().getValue(),
                getFormatedDuration().getValue(),
                commonCost.getValue(),
                getMemberCode().getValue(),
                getCardNumber().getValue(),
                getPlate__0().getValue(),
                getPlate__1().getValue(),
                getPlate__2().getValue(),
                getPlate__3().getValue(),
                getMplate__0().getValue(),
                getMplate__1().getValue(),
                residNahaee,
                ozvStatus,
                cartStatus,
                pelakStatus,
                getMotor().getValue(),
                getCar().getValue()
        );
        dialog.setCallBack(new PaymentDialog.DialogCallBackNew() {
            @Override
            public void onCallBack(String state) {
                PreferenceManager.getDefaultSharedPreferences(myContext).edit().putBoolean("comFmPardakhtDialog", true).apply();
                //call print method here
//                if (idal != null) {
//                    mBitmap = generateBitmapByLayout(myContext);
////                    mBitmap = generateTestBitmapByLayout(myContext);
//                    if (mPrintThread == null) {
//                        mPrintThread = new PaymentSafshekanViewModel.PrintThread();
//                    }
//                    mPrintThread.start();
//                }
                mBitmap = generateBitmapByLayout(myContext);
                Message msg = mPrintHandler.obtainMessage(PRINT_BITMAP);
                msg.obj = mBitmap;
                msg.sendToTarget();
                mPrintHandler.obtainMessage(PRINT_FORWARD).sendToTarget();
//                PrinterManager printerManager = new PrinterManager();
//                printerManager.setupPage(50,200);
//                printerManager.drawBitmap(mBitmap,0,0);
//                printerManager.printPage(0);
//                printerManager.clearPage();
//                printerManager.close();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        ((BaseActivity) myContext).finish();
                    }
                }, 2000);
            }
        });
        dialog.show(((Activity) myContext).getFragmentManager(), "");
    }

    public Bitmap generateBitmapByLayout(Context context) {
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.receipt_payment, null);
        PersianTextView entry_date_txt = view.findViewById(R.id.entry_date_ttttttt);
        PersianTextView duration_date_txt = view.findViewById(R.id.duration_date_ttttttttt);
        PersianTextView cost_txt = view.findViewById(R.id.cost_txt_ttttttttttt);
        PersianTextView ozv_txt = view.findViewById(R.id.ozv_txt_ttttttttt);
        PersianTextView card_txt = view.findViewById(R.id.cart_txt_tttttttt);
        PersianTextView plate_txt_0 = view.findViewById(R.id.p0_ttttttttttt);
        PersianTextView plate_txt_1 = view.findViewById(R.id.p1_ttttttttt);
        PersianTextView plate_txt_2 = view.findViewById(R.id.p2_tttttttt);
        PersianTextView plate_txt_3 = view.findViewById(R.id.p3_tttttttt);
        PersianTextView m_plate_txt_0 = view.findViewById(R.id.m0_ttttttttt);
        PersianTextView m_plate_txt_1 = view.findViewById(R.id.m1_tttttttt);
        PersianTextView peygiri_txt = view.findViewById(R.id.txt_peygiri_ttttt);
        RelativeLayout pelak_main_layoout = view.findViewById(R.id.plate_main_layout_ttttttttttt);
        LinearLayout cart_main_layout = view.findViewById(R.id.date_layout_ttttttttt);
        LinearLayout ozv_main_layout = view.findViewById(R.id.ozv_recipit_layout_ttttttttttt);
        LinearLayout motor_main_layout = view.findViewById(R.id.motor_layout_ttttttt);
        LinearLayout car_main_layout = view.findViewById(R.id.plate_layout_tttttttttt);
        entry_date_txt.setText(getSolarEnterDateTime().getValue());
        duration_date_txt.setText(getFormatedDuration().getValue());
        cost_txt.setText(FontHelper.RialFormatter(Long.parseLong(getCommonCost().getValue())) + " ریال");
        peygiri_txt.setText(getPeygiri_code().getValue());
        boolean cartStatus = true;
        boolean ozvStatus = true;
        boolean pelakStatus = true;
        if (getPlate__0().getValue() == null && getMplate__0().getValue() == null) {
            pelakStatus = false;
        }
        if (getMemberCode().getValue() == null) {
            ozvStatus = false;
        }
        if (getCardNumber().getValue() == null) {
            cartStatus = false;
        }
        if (pelakStatus == true) {
            pelak_main_layoout.setVisibility(View.VISIBLE);
            if (!getCar().getValue()) {
                motor_main_layout.setVisibility(View.VISIBLE);
                car_main_layout.setVisibility(View.GONE);
                m_plate_txt_0.setText(getMplate__0().getValue());
                m_plate_txt_1.setText(getMplate__1().getValue());
            } else {
                car_main_layout.setVisibility(View.VISIBLE);
                motor_main_layout.setVisibility(View.GONE);
                plate_txt_0.setText(getPlate__0().getValue());
                plate_txt_1.setText(getPlate__1().getValue());
                plate_txt_2.setText(getPlate__2().getValue());
                plate_txt_3.setText(getPlate__3().getValue());
            }
        } else {
            pelak_main_layoout.setVisibility(View.GONE);
        }
        if (ozvStatus == true) {
            ozv_main_layout.setVisibility(View.VISIBLE);
            ozv_txt.setText(memberCode.getValue());
        } else {
            ozv_main_layout.setVisibility(View.GONE);
        }
        if (cartStatus == true) {
            cart_main_layout.setVisibility(View.VISIBLE);
            card_txt.setText(cardNumber.getValue());
        } else {
            cart_main_layout.setVisibility(View.GONE);
        }
        return PrinterUtils.convertViewToBitmap(view);
    }
//    public Bitmap generateTestBitmapByLayout(Context context) {
//
//        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.test_print, null);
//        return PrinterUtils.convertViewToBitmap(view);
//    }

    /**
     * Execution printing
     * To print data with this class, use the following steps:
     * Obtain an instance of Printer with PrinterManager printer = new PrinterManager().
     * Call setupPage(int, int) to initialize the page size.
     * If necessary, append a line in the current page with drawLine(int , int , int , int , int ).
     * If necessary, append text in the current page with drawTextEx(String , int, int , int , int , String ,int , int , int , int ).
     * If necessary, append barcode data in the current page with drawBarcode(String , int , int , int ,int , int , int ).
     * If necessary, append picture data in the current page with drawBitmap(Bitmap , int , int ).
     * To begin print the current page session, call printPage(int).
     *
     * @param printerManager printerManager
     * @param type           PRINT_TEXT PRINT_BITMAP PRINT_BARCOD PRINT_FORWARD
     * @param content        content
     */
    private void doPrint(PrinterManager printerManager, int type, Object content) {
        int ret = printerManager.getStatus();   //Get printer status
        if (ret == PRNSTS_OK) {
            printerManager.setupPage(384, -1);   //Set paper size
            switch (type) {
                case PRINT_BITMAP:
                    Bitmap bitmap = (Bitmap) content;
                    if (bitmap != null) {
                        printerManager.drawBitmap(bitmap, 30, 0);  //print pictures
                    } else {
                        ShowToast.getInstance().showError(myContext, R.string.bitmap_null);
                    }
                    break;
            }
            ret = printerManager.printPage(0);  //Execution printing
            printerManager.paperFeed(16);  //paper feed
        }
        updatePrintStatus(ret);
    }

    private final int PRINT_TEXT = 0;   //Printed text
    private final int PRINT_BITMAP = 1;   //print pictures
    private final int PRINT_BARCOD = 2;   //Print bar code
    private final int PRINT_FORWARD = 3;   //Forward (paper feed)

    private Handler mPrintHandler;

    class CustomThread extends Thread {
        @Override
        public void run() {
            //To create a message loop
            Looper.prepare();   //1.Initialize looper
            mPrintHandler = new Handler() {   //2.Bind handler to looper object of customthread instance
                public void handleMessage(Message msg) {   //3.Define how messages are processed
                    switch (msg.what) {
                        case PRINT_TEXT:
                        case PRINT_BITMAP:
                        case PRINT_BARCOD:
                            doPrint(getPrinterManager(), msg.what, msg.obj);   //Print
                            break;
                        case PRINT_FORWARD:
//                            getPrinterManager().paperFeed(20);
                            getPrinterManager().paperFeed(1200);
//                            getPrinterManager().paperFeed(1200);
                            updatePrintStatus(100);
                            break;
                    }
                }
            };
            Looper.loop();   //4.Start message loop
        }
    }

    //Update printer status, toast reminder in case of exception
    private void updatePrintStatus(final int status) {
        if (status == PRNSTS_OUT_OF_PAPER) {
            ShowToast.getInstance().showError(myContext, R.string.tst_info_paper);
        } else if (status == PRNSTS_OVER_HEAT) {
            ShowToast.getInstance().showError(myContext, R.string.tst_info_temperature);
        } else if (status == PRNSTS_UNDER_VOLTAGE) {
            ShowToast.getInstance().showError(myContext, R.string.tst_info_voltage);
        } else if (status == PRNSTS_BUSY) {
            ShowToast.getInstance().showError(myContext, R.string.tst_info_busy);
        } else if (status == PRNSTS_ERR) {
            ShowToast.getInstance().showError(myContext, R.string.tst_info_error);
        } else if (status == PRNSTS_ERR_DRIVER) {
            ShowToast.getInstance().showError(myContext, R.string.tst_info_driver_error);
        }
    }

    public static boolean isNumeric(String string) {
        if (string != null && !string.equals("") && string.matches("\\d*")) {
            if (String.valueOf(Integer.MAX_VALUE).length() < string.length()) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    //Instantiate printermanager
    private PrinterManager getPrinterManager() {
        if (mPrinterManager == null) {
            mPrinterManager = new PrinterManager();
            mPrinterManager.open();
        }
        return mPrinterManager;
    }
}
