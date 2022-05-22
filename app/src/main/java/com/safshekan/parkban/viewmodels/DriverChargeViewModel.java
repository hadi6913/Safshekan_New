package com.safshekan.parkban.viewmodels;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.safshekan.parkban.BaseActivity;
import com.safshekan.parkban.R;
import com.safshekan.parkban.anpr.farsi_ocr_anpr.FarsiOcrAnprProvider;
import com.safshekan.parkban.core.anpr.BaseAnprProvider;
import com.safshekan.parkban.core.anpr.helpers.PlateDetectionState;
import com.safshekan.parkban.core.anpr.helpers.RidingType;
import com.safshekan.parkban.core.anpr.onPlateDetectedCallback;
import com.safshekan.parkban.dialogs.ChargeValueDialog;
import com.safshekan.parkban.dialogs.ConfirmMessageDialog;
import com.safshekan.parkban.dialogs.FailedConfirmDialog;
import com.safshekan.parkban.dialogs.PaymentResultDialog;
import com.safshekan.parkban.helper.DateTimeHelper;
import com.safshekan.parkban.helper.EasyHelper;
import com.safshekan.parkban.helper.FontHelper;
import com.safshekan.parkban.helper.ImageLoadHelper;
import com.safshekan.parkban.helper.PrintParkbanApp;
import com.safshekan.parkban.helper.PrinterUtils;
import com.safshekan.parkban.helper.ShowToast;
import com.safshekan.parkban.persistence.ParkbanDatabase;
import com.safshekan.parkban.persistence.models.CarPlate;
import com.safshekan.parkban.persistence.models.ChargeAmount;
import com.safshekan.parkban.persistence.models.ResponseResultType;
import com.safshekan.parkban.repositories.ParkbanRepository;
import com.safshekan.parkban.services.dto.BooleanResultDto;
import com.safshekan.parkban.services.dto.DriverDebtResultDto;
import com.safshekan.parkban.services.dto.IncreaseDriverWalletCardResultDto;
import com.safshekan.parkban.services.dto.IncreaseDriverWalletResultDto;
import com.safshekan.parkban.services.dto.ThirdPartDto;
import com.safshekan.parkban.services.dto.ThirdPartResponseDto;
import com.pax.dal.IDAL;
import com.pax.dal.IPrinter;
import com.pax.dal.exceptions.PrinterDevException;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

public class DriverChargeViewModel extends ViewModel {


    //******************************************************************************************************************************************
    //******************************************************************************************************************************************
    //declare variables
    private BaseAnprProvider anprProvider;
    private Uri photoURI;
    private String newPhotoFilePath;
    private static final String MEDIA_FILE_TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";
    private MutableLiveData<Boolean> car;
    private MutableLiveData<Boolean> motor;
    private MutableLiveData<String> amount, debtValue, wallet;
    private String phoneNumber, confirmPhoneNumber;
    //    private String plate0, plate1, plate2, plate3;
//    private String mPlate0, mPlate1;
    private String carPlate, motorPlate;
    private List<ChargeAmount> chargeAmountList;
    private ArrayList<ChargeAmount> chargeAmountArrayList;
    private long chargeItemSelected;
    private NumberFormat formatter = new DecimalFormat("#,###");
    private ParkbanRepository parkbanRepository;
    private MutableLiveData<Boolean> enableCharge, hastDebt, noDebt, hasWallet;
    private String receiptCode, QRCode, dateTime;
    private IDAL idal;
    private IPrinter iPrinter;
    private Bitmap mBitmap;
    private CarPlate carPlateActual;
    private PrintThread mPrintThread;
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private MutableLiveData<Boolean> cash_transaction;
    private MutableLiveData<Boolean> electronic_transaction;
    private Handler receiverHandler;
    //    private String ResNum;
//    private String ApprovalCode;
    private Context myContext;
    private MutableLiveData<Integer> shouldShowKifePoolDiloag;
    private MutableLiveData<Integer> shouldShowElectronicPayDiloag;
    private MutableLiveData<Integer> shouldShowEntirePayDiloag;
    private Bitmap plateBitmap;
    public Spinner mSpinner;
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public TextWatcher phoneTextWatcher, confirmTextWatcher;


    private MutableLiveData<String> plate__0;
    private MutableLiveData<String> plate__1;
    private MutableLiveData<String> plate__2;
    private MutableLiveData<String> plate__3;
    private MutableLiveData<String> mplate__0;
    private MutableLiveData<String> mplate__1;


    //******************************************************************************************************************************************
    //******************************************************************************************************************************************
    //declare getters and setters for variables of view model
    public MutableLiveData<Integer> progress = new MutableLiveData<>();


    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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

    public MutableLiveData<Integer> getShouldShowKifePoolDiloag() {
        if (shouldShowKifePoolDiloag == null)
            shouldShowKifePoolDiloag = new MutableLiveData<>();
        return shouldShowKifePoolDiloag;
    }

    public MutableLiveData<Integer> getShouldShowElectronicPayDiloag() {
        if (shouldShowElectronicPayDiloag == null)
            shouldShowElectronicPayDiloag = new MutableLiveData<>();
        return shouldShowElectronicPayDiloag;
    }

    public MutableLiveData<Integer> getShouldShowEntirePayDiloag() {
        if (shouldShowEntirePayDiloag == null)
            shouldShowEntirePayDiloag = new MutableLiveData<>();
        return shouldShowEntirePayDiloag;
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


    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


    public LiveData<Integer> getProgress() {
        return progress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getConfirmPhoneNumber() {
        return confirmPhoneNumber;
    }

    public void setConfirmPhoneNumber(String confirmPhoneNumber) {
        this.confirmPhoneNumber = confirmPhoneNumber;
    }

    public LiveData<String> getAmount() {
        if (amount == null)
            amount = new MutableLiveData<>();
        return amount;
    }

    public LiveData<String> getDebtValue() {
        if (debtValue == null)
            debtValue = new MutableLiveData<>();
        return debtValue;
    }

    public LiveData<String> getWallet() {
        if (wallet == null)
            wallet = new MutableLiveData<>();
        return wallet;
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

    public LiveData<Boolean> getEnableCharge() {
        if (enableCharge == null)
            enableCharge = new MutableLiveData<>();
        return enableCharge;
    }

    public LiveData<Boolean> getHasDebt() {
        if (hastDebt == null)
            hastDebt = new MutableLiveData<>();
        return hastDebt;
    }

    public LiveData<Boolean> getNoDebt() {
        if (noDebt == null)
            noDebt = new MutableLiveData<>();
        return noDebt;
    }

    public LiveData<Boolean> getHasWallet() {
        if (hasWallet == null)
            hasWallet = new MutableLiveData<>();
        return hasWallet;
    }

//    public String getPlate0() {
//        return plate0;
//    }
//
//    public void setPlate0(String plate0) {
//        this.plate0 = plate0;
//    }
//
//    public String getPlate1() {
//        return plate1;
//    }
//
//    public void setPlate1(String plate1) {
//        this.plate1 = plate1;
//    }
//
//    public String getPlate2() {
//        return plate2;
//    }
//
//    public void setPlate2(String plate2) {
//        this.plate2 = plate2;
//    }
//
//    public String getPlate3() {
//        return plate3;
//    }
//
//    public void setPlate3(String plate3) {
//        this.plate3 = plate3;
//    }
//
//    public String getMPlate0() {
//        return mPlate0;
//    }
//
//    public void setMPlate0(String mPlate0) {
//        this.mPlate0 = mPlate0;
//    }
//
//    public String getMPlate1() {
//        return mPlate1;
//
//    }
//
//    public void setMPlate1(String mPlate1) {
//        this.mPlate1 = mPlate1;
//    }


    //******************************************************************************************************************************************
    //******************************************************************************************************************************************
    // nothing important , just initialize default values for variables
    public void init(Context context) {


        if (plate__0 == null)
            plate__0 = new MutableLiveData<>();
        plate__0.setValue("");
        if (plate__1 == null)
            plate__1 = new MutableLiveData<>();
        plate__1.setValue("");
        if (plate__2 == null)
            plate__2 = new MutableLiveData<>();
        plate__2.setValue("");
        if (plate__3 == null)
            plate__3 = new MutableLiveData<>();
        plate__3.setValue("");
        if (mplate__0 == null)
            mplate__0 = new MutableLiveData<>();
        mplate__0.setValue("");
        if (mplate__1 == null)
            mplate__1 = new MutableLiveData<>();
        mplate__1.setValue("");


        try {
            idal = PrintParkbanApp.getInstance().getIdal();
            if (idal != null)
                iPrinter = idal.getPrinter();
        } catch (Exception e) {

        }

        if (car == null)
            car = new MutableLiveData<>();
        car.setValue(true);

        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


        Log.d("xeagle69", "Dirver_chage_view_model>>enable_kife_pool>>" + PreferenceManager.getDefaultSharedPreferences(context).getBoolean("enablekifepool", false));
        PreferenceManager.getDefaultSharedPreferences(context).getBoolean("enablekifepool", false);


        try {
            idal = PrintParkbanApp.getInstance().getIdal();
            if (idal == null) {
                if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("enablekifepool", false)) {


                    if (shouldShowElectronicPayDiloag == null)
                        shouldShowElectronicPayDiloag = new MutableLiveData<>();
                    shouldShowElectronicPayDiloag.setValue(View.GONE);

                    if (shouldShowKifePoolDiloag == null)
                        shouldShowKifePoolDiloag = new MutableLiveData<>();
                    shouldShowKifePoolDiloag.setValue(View.GONE);

                    if (shouldShowEntirePayDiloag == null)
                        shouldShowEntirePayDiloag = new MutableLiveData<>();
                    shouldShowEntirePayDiloag.setValue(View.GONE);
                } else {
                    // here we should hide pardakhte electronic
                    if (shouldShowEntirePayDiloag == null)
                        shouldShowEntirePayDiloag = new MutableLiveData<>();
                    shouldShowEntirePayDiloag.setValue(View.VISIBLE);
                    if (shouldShowElectronicPayDiloag == null)
                        shouldShowElectronicPayDiloag = new MutableLiveData<>();
                    shouldShowElectronicPayDiloag.setValue(View.GONE);
                    if (cash_transaction == null)
                        cash_transaction = new MutableLiveData<>();
                    cash_transaction.setValue(true);
                    if (electronic_transaction == null)
                        electronic_transaction = new MutableLiveData<>();
                    electronic_transaction.setValue(false);

                }

            } else {

                if (electronic_transaction == null)
                    electronic_transaction = new MutableLiveData<>();
                electronic_transaction.setValue(true);
                if (shouldShowEntirePayDiloag == null)
                    shouldShowEntirePayDiloag = new MutableLiveData<>();
                shouldShowEntirePayDiloag.setValue(View.VISIBLE);
                if (shouldShowElectronicPayDiloag == null)
                    shouldShowElectronicPayDiloag = new MutableLiveData<>();
                shouldShowElectronicPayDiloag.setValue(View.VISIBLE);
            }

        } catch (Exception e) {

        }
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("enablekifepool", false)) {
            //here we should set visibility of (kife pool) and (electronic pay) to visible
            if (shouldShowKifePoolDiloag == null)
                shouldShowKifePoolDiloag = new MutableLiveData<>();
            shouldShowKifePoolDiloag.setValue(View.VISIBLE);
        } else {
            //here we should set visibility of kife pool to gone but (electronic pay) to visible
            if (shouldShowKifePoolDiloag == null)
                shouldShowKifePoolDiloag = new MutableLiveData<>();
            shouldShowKifePoolDiloag.setValue(View.GONE);
        }


        myContext = context;
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        if (enableCharge == null)
            enableCharge = new MutableLiveData<>();
        enableCharge.setValue(true);

        if (hastDebt == null)
            hastDebt = new MutableLiveData<>();
        hastDebt.setValue(false);

        if (noDebt == null)
            noDebt = new MutableLiveData<>();
        noDebt.setValue(false);

        if (hasWallet == null)
            hasWallet = new MutableLiveData<>();
        hasWallet.setValue(false);

        if (debtValue == null)
            debtValue = new MutableLiveData<>();

        if (wallet == null)
            wallet = new MutableLiveData<>();

        //put value of VacationRequest Enum in a List
        chargeAmountList = new ArrayList<ChargeAmount>(EnumSet.allOf(ChargeAmount.class));
        //convert List to ArrayList
        chargeAmountArrayList = new ArrayList<>(chargeAmountList.size());
        chargeAmountArrayList.addAll(chargeAmountList);

        if (amount == null)
            amount = new MutableLiveData<>();
        chargeItemSelected = ChargeAmount.two.getValue();
        amount.setValue(formatter.format(chargeItemSelected) + " ریال");

        if (parkbanRepository == null) {
            ParkbanDatabase.getInstance(context.getApplicationContext(), new ParkbanDatabase.DatabaseReadyCallback() {
                @Override
                public void onReady(ParkbanDatabase instance) {
                    parkbanRepository = new ParkbanRepository(instance);
                }
            });
        }

        phoneTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                hastDebt.setValue(false);
                noDebt.setValue(false);
                hasWallet.setValue(false);
            }
        };

        confirmTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                hastDebt.setValue(false);
                noDebt.setValue(false);
                hasWallet.setValue(false);
            }
        };
        //hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
        if (anprProvider == null) {
            anprProvider = new FarsiOcrAnprProvider(context);
        }
        carPlateActual = new CarPlate();
    }

    // this is click-listener for horizontal linear layout that shows dept of driver via its phone_number
    public void showDebtClick(final View view) {

        hastDebt.setValue(false);
        noDebt.setValue(false);
        hasWallet.setValue(false);

        if (checkPhoneValidation(view.getContext()))
            if (!phoneNumber.equals("")) {
                progress.setValue(10);
                parkbanRepository.getDriverFullDebtAndWallet(Long.parseLong(phoneNumber), new ParkbanRepository.ServiceResultCallBack<DriverDebtResultDto>() {
                    @Override
                    public void onSuccess(DriverDebtResultDto result) {
                        progress.setValue(0);
                        if (result.getValue().getDebt() > 0) {
                            hastDebt.setValue(true);
                            debtValue.setValue(FontHelper.RialFormatter(result.getValue().getDebt()));
                        } else {
                            noDebt.setValue(true);
                        }
                        hasWallet.setValue(true);
                        wallet.setValue(FontHelper.RialFormatter(result.getValue().getWallet()));
                    }

                    @Override
                    public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                        progress.setValue(0);
                        switch (resultType) {
                            case RetrofitError:
                                ShowToast.getInstance().showError(view.getContext(), R.string.exception_msg);
                                break;
                            case ServerError:
                                if (errorCode != 0)
                                    ShowToast.getInstance().showError(view.getContext(), errorCode);
                                else
                                    ShowToast.getInstance().showError(view.getContext(), R.string.connection_failed);
                                break;
                            default:
                                ShowToast.getInstance().showError(view.getContext(), resultType.ordinal());
                        }
                    }
                });
            }

    }

    // its clear as sun
    public boolean checkPhoneValidation(Context context) {
        boolean status = true;
        if (phoneNumber == null || phoneNumber.equals("")) {
            ShowToast.getInstance().showWarning(context, R.string.phone_number_need);
            status = false;
        } else if (phoneNumber != null)
            if (!phoneNumber.isEmpty()) {
                if (phoneNumber.length() < 11) {
                    ShowToast.getInstance().showWarning(context, R.string.phone_number_length);
                    status = false;
                } else if (!phoneNumber.substring(0, 2).contains("09")) {
                    ShowToast.getInstance().showWarning(context, R.string.phone_number_format);
                    status = false;
                }
                if (confirmPhoneNumber == null) {
                    ShowToast.getInstance().showWarning(context, R.string.phone_number_confirm);
                    status = false;
                } else {
                    if (confirmPhoneNumber.length() < 11) {
                        ShowToast.getInstance().showWarning(context, R.string.phone_number_confirm_length);
                        status = false;
                    }
                    if (!confirmPhoneNumber.substring(0, 2).contains("09")) {
                        ShowToast.getInstance().showWarning(context, R.string.phone_number_confirm_format);
                        status = false;
                    }
                    if (!confirmPhoneNumber.equals(phoneNumber)) {
                        ShowToast.getInstance().showWarning(context, R.string.phone_number_not_same_confirm);
                        status = false;
                    }
                }
            }

        return status;
    }

    // listener for radio button that refer to car
    public void carCheckClick(View view) {

        car.setValue(true);
        motor.setValue(false);
        carPlate = plate__0.getValue() + plate__1.getValue() + plate__2.getValue() + plate__3.getValue();
    }

    // listener for radio button that refer to motor
    public void motorCheckClick(View view) {
        car.setValue(false);
        motor.setValue(true);
        motorPlate = mplate__0.getValue() + mplate__1.getValue();
    }

    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // listener for radio button that refer to cash_transaction
    public void cash_CheckClick(View view) {
        cash_transaction.setValue(true);
        electronic_transaction.setValue(false);
    }

    // listener for radio button that refer to card_transaction
    public void card_CheckClick(View view) {
        electronic_transaction.setValue(true);
        cash_transaction.setValue(false);
        enableCharge.setValue(true);
    }
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


    // this is click-listener for cardview control (that is a custom view ) layout that shows a dialog fragment (charge value dialog) for entering the value of charge value
    public void chargeAmountClick(final View view) {

        ChargeValueDialog dialog = new ChargeValueDialog();
        dialog.setItems(chargeAmountArrayList, R.string.charge_amounts);
        dialog.setCallBack(new ChargeValueDialog.DialogCallBack() {
            @Override
            public void onCallBack(String state, long chargeValue) {
                if (state == ChargeValueDialog.CONFIRM) {
                    setAmount(chargeValue, view.getContext());
                } else {

                }
            }
        });
        dialog.show(((Activity) view.getContext()).getFragmentManager(), "");

//        ItemListDialog dialog = new ItemListDialog();
//        dialog.setItems(chargeAmountArrayList, R.string.charge_amounts, new ItemListDialog.DialogOnItemSelectedListener() {
//            @Override
//            public void OnItemSelected(Object selectedItem) {
//                setAmount((ChargeAmount) selectedItem, view.getContext());
//            }
//        });
//        dialog.show(((Activity) view.getContext()).getFragmentManager(), "");
    }

    // below function will run after user confirm amount of charge value! (important thing about it is : it will check account of parkban via this >>> hasParkbanCredit(chargeItemSelected))
    private void setAmount(long itemSelected, Context context) {
        chargeItemSelected = itemSelected;
        amount.setValue(formatter.format(chargeItemSelected) + " ریال");


        hasParkbanCredit(chargeItemSelected, context);


    }

    //below function will run for checking the account of parkban for input value that is >>> amount (long)
    private void hasParkbanCredit(long amount, final Context context) {
        progress.setValue(10);
        parkbanRepository.hasParkbanCredit(amount, new ParkbanRepository.ServiceResultCallBack<BooleanResultDto>() {
            @Override
            public void onSuccess(BooleanResultDto result) {
                progress.setValue(0);
                if (!result.getValue()) {
                    if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("enablekifepool", false)) {
                        ShowToast.getInstance().showError(context, R.string.credit_not_enough);
//                        enableCharge.setValue(false);
                    } else {
                        enableCharge.setValue(true);
                    }

                    return;
                } else
                    enableCharge.setValue(true);
            }

            @Override
            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                progress.setValue(0);
                switch (resultType) {
                    case RetrofitError:
                        ShowToast.getInstance().showError(context, R.string.exception_msg);
                        break;
                    case ServerError:
                        if (errorCode != 0)
                            ShowToast.getInstance().showError(context, errorCode);
                        else
                            ShowToast.getInstance().showError(context, R.string.connection_failed);
                        break;
                    default:
                        ShowToast.getInstance().showError(context, resultType.ordinal());
                }
            }
        });
    }

    //below thing is listener that will run when user clicked on charge-btn (i mean the final charge button !!!!)
    public void chargeClick(final View view) {

        if (!enableCharge.getValue()) {
            return;
        }

        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        if (PreferenceManager.getDefaultSharedPreferences(view.getContext()).getBoolean("enablekifepool", false)) {
            if (electronic_transaction.getValue()) {
                Log.d("xxxhhhaaa", "cash");
                try {
                    idal = PrintParkbanApp.getInstance().getIdal();
                    if (idal != null)
                        chargeDriverWalletFromCard(view.getContext(), 0);
                    else
                        ShowToast.getInstance().showError(myContext, R.string.device_dont_have_card_reader);
                } catch (Exception e) {
                }


            } else {
                Log.d("xxxhhhaaa", "card");
                chargeDriverWallet(view.getContext(), 0);
            }
        } else {
            chargeDriverWalletFromCard(view.getContext(), 0);
        }


        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    }

    //hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
    public File createPrivateImageFile(Context context) throws IOException {
        String timeStamp = DateTimeHelper.DateToStr(new Date(), MEDIA_FILE_TIMESTAMP_FORMAT);
        String imageFileName = "PLATE_" + timeStamp;
        carPlateActual.setPlateFileName(imageFileName);
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    //hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
    public void captureOnlyPlate(final View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(myContext.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createPrivateImageFile(myContext);
                newPhotoFilePath = photoFile.getAbsolutePath();

                //set file name in model for save to database
                carPlateActual.setPlateFileName(photoFile.getName());

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("xxxxhhhhhh", "error >>> " + e.getMessage());
            }

            if (photoFile != null) {

                try {


                    //for Android 4.4-
//                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
//                        photoURI = Uri.fromFile(photoFile);
//                    } else
                    photoURI = FileProvider.getUriForFile(myContext, "com.safshekan.parkban", photoFile);

                    List<ResolveInfo> resolvedIntentActivities = myContext.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                        String packageName = resolvedIntentInfo.activityInfo.packageName;
                        myContext.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }

                    //takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    ((Activity) myContext).startActivityForResult(takePictureIntent, 1369);

                } catch (Exception e) {

                }
            }
        }


    }

    //hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
    public void processActivityResult(Context context, int requestCode, int resultCode, Intent
            data) {
        if (requestCode == 1369 && resultCode == Activity.RESULT_OK) {


            Log.d("xxxhhhhhhhhhhhh", "" + newPhotoFilePath);
            plateBitmap = ImageLoadHelper.getInstance().loadImage(context, newPhotoFilePath);

            //*****************************************************************************************************


            try {
                idal = PrintParkbanApp.getInstance().getIdal();
                if (idal == null) {

                    try {

                        ExifInterface exif = new ExifInterface(newPhotoFilePath);
                        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        Log.d("xeagle69", "camera XXX>>> rotation in int >>> " + rotation);
                        if (rotation == 1) {

                        } else {
                            Matrix matrix = new Matrix();
                            matrix.setRotate(90, 0, 0);
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(plateBitmap, plateBitmap.getWidth(), plateBitmap.getHeight(), true);
                            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                            plateBitmap = rotatedBitmap;

                        }

                    } catch (Exception e) {

                    }


                } else {

                    try {

                        ExifInterface exif = new ExifInterface(newPhotoFilePath);
                        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        Log.d("xeagle69", "camera XXX>>> rotation in int >>> " + rotation);
                        if (rotation == 0) {

                        } else {
                            Matrix matrix = new Matrix();
                            matrix.setRotate(-90, 0, 0);
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(plateBitmap, plateBitmap.getWidth(), plateBitmap.getHeight(), true);
                            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                            plateBitmap = rotatedBitmap;

                        }

                    } catch (Exception e) {
//
                    }

                }

            } catch (Exception e) {

            }


            //*****************************************************************************************************

            carPlateActual.setPlateImage(plateBitmap);
//            carPlateLiveData.setValue(carPlate);
            doDetectPlate(context, plateBitmap);
//            carPhoto.setValue(true);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            //show toast
        }
    }

    //hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
    private void doDetectPlate(final Context context, Bitmap plateImage) {


        anprProvider.getPlate(context, plateImage, new onPlateDetectedCallback() {
            @Override
            public void onPlateDetected(PlateDetectionState state, RidingType ridingType, String plateNo, Bitmap plateImage, String part0, String part1, String part2, String part3) {


                Log.d("xxxxhhhhhh", "part0 >>" + part0);
                Log.d("xxxxhhhhhh", "part1 >>" + part1);
                Log.d("xxxxhhhhhh", "part2 >>" + part2);
                Log.d("xxxxhhhhhh", "part3 >>" + part3);
                Log.d("xxxxhhhhhh", "state >>" + state.toString());
                Log.d("xxxxhhhhhh", "type >>" + ridingType.toString());


                if (state == PlateDetectionState.DETECTED) {
                    if (ridingType == RidingType.CAR) {
                        motor.setValue(false);
                        car.setValue(true);
                        plate__0.setValue(part0);
                        String[] alphabetArray = context.getResources().getStringArray(R.array.image_array);
                        for (int i = 1; i < alphabetArray.length; i++) {
                            if (alphabetArray[i].equals(part1))
                                mSpinner.setSelection(i);
                        }

                        plate__2.setValue(part2);
                        plate__3.setValue(part3);
                    } else {
                        motor.setValue(true);
                        car.setValue(false);
                        Log.d("xxxxhhhhhh", "Motor section");
                        if (plateNo != null) {
                            mplate__0.setValue(plateNo.substring(0, 3));
                            mplate__1.setValue(plateNo.substring(3, plateNo.length()));

                        }
                    }

                }else {
                    ShowToast.getInstance().showError(myContext,R.string.plate_not_detected);
                }


            }
        });
    }


    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    //below function will use for charging driver wallet via  card transaction
    private void chargeDriverWalletFromCard(final Context context, final long realPhoneNumber) {
        // verification for pelake  car  or motorcycle ! and assign the value for it !!!
        if (car.getValue()) {
            Log.d("xxxhhh", "chargeDriverWallet >>> car");
            if (plate__0 == null || plate__1 == null || plate__2 == null || plate__3 == null) {
                ShowToast.getInstance().showWarning(context, R.string.plate_not_complete);
                return;
            } else if (plate__0.getValue().length() < 2 || plate__2.getValue().length() < 3 || plate__3.getValue().length() < 2) {
                ShowToast.getInstance().showWarning(context, R.string.plate_not_complete);
                return;
            } else
                carPlate = plate__0.getValue() + plate__1.getValue() + plate__2.getValue() + plate__3.getValue();
            Log.d("xxxhhh", "chargeDriverWallet >>> car - " + carPlate);
        } else if (motor.getValue()) {
            Log.d("xxxhhh", "chargeDriverWallet >>> motor");
            if (mplate__0.getValue() == null || mplate__1.getValue() == null) {
                ShowToast.getInstance().showWarning(context, R.string.plate_not_complete);
                return;
            } else if (mplate__0.getValue().length() < 3 || mplate__1.getValue().length() < 5) {
                ShowToast.getInstance().showWarning(context, R.string.plate_not_complete);
                return;
            } else
                motorPlate = mplate__0.getValue() + mplate__1.getValue();
            Log.d("xxxhhh", "chargeDriverWallet >>> car - " + motorPlate);
        }
        // verification for phone number  of  driver  ! and assign the value for it !!!
        if (phoneNumber == null) {
            ShowToast.getInstance().showWarning(context, R.string.phone_number_need);
            return;
        } else if (phoneNumber != null || phoneNumber.equals(""))
            if (!phoneNumber.isEmpty()) {
                if (phoneNumber.length() < 11) {
                    ShowToast.getInstance().showWarning(context, R.string.phone_number_length);
                    return;
                } else if (!phoneNumber.substring(0, 2).contains("09")) {
                    ShowToast.getInstance().showWarning(context, R.string.phone_number_format);
                    return;
                }
                if (confirmPhoneNumber == null) {
                    ShowToast.getInstance().showWarning(context, R.string.phone_number_confirm);
                    return;
                } else {
                    if (confirmPhoneNumber.length() < 11) {
                        ShowToast.getInstance().showWarning(context, R.string.phone_number_confirm_length);
                        return;
                    }
                    if (!confirmPhoneNumber.substring(0, 2).contains("09")) {
                        ShowToast.getInstance().showWarning(context, R.string.phone_number_confirm_format);
                        return;
                    }
                    if (!confirmPhoneNumber.equals(phoneNumber)) {
                        ShowToast.getInstance().showWarning(context, R.string.phone_number_not_same_confirm);
                        return;
                    }
                }
            }
        //*!*!*!*!*!*!**!*!*!*!*!*!*!*!*!*!*!
        enableCharge.setValue(false);
        IncreaseDriverWalletCardResultDto.IncreaseDriverWalletDto wallet = new IncreaseDriverWalletCardResultDto.IncreaseDriverWalletDto();

        if (realPhoneNumber != 0)
            wallet.setPhoneNumber(realPhoneNumber);
        else
            wallet.setPhoneNumber(java.lang.Long.parseLong(phoneNumber));

        wallet.setAmount(chargeItemSelected);
        wallet.setFactorId(0);

        if (car.getValue()) {
            Log.d("xxxhhh", "chargeDriverWallet >>> car - prepare wallet obj " + carPlate + "wallet.setPlate");
            wallet.setPlate(carPlate);
        } else {
            Log.d("xxxhhh", "chargeDriverWallet >>> motor - prepare wallet obj " + motorPlate + "wallet.setPlate");
            wallet.setPlate(motorPlate);

        }


        progress.setValue(10);
        parkbanRepository.increaseDiverWalletCard(wallet, new ParkbanRepository.ServiceResultCallBack<IncreaseDriverWalletCardResultDto>() {
            @Override
            public void onSuccess(IncreaseDriverWalletCardResultDto result) {
                if (result.getValue().getErrorMessage() == null || result.getValue().getErrorMessage() == "") {

                    PreferenceManager.getDefaultSharedPreferences(myContext).edit().putString("factorId", String.valueOf(result.getValue().getFactorId())).apply();

                    try {
                        String phoneNumberCard = "";
                        if (realPhoneNumber != 0)
                            phoneNumberCard = String.valueOf(realPhoneNumber);
                        else
                            phoneNumberCard = phoneNumber;

                        EasyHelper.purchaseTxn((Activity) context,
                                receiverHandler,
                                String.valueOf(chargeItemSelected),
                                phoneNumberCard,
                                String.valueOf(result.getValue().getFactorId()),
                                "IRR",
                                "FA");


                    } catch (EasyHelper.PahpatException e) {
                        Toast.makeText(context, "خطای پرداخت", Toast.LENGTH_SHORT).show();
                        Log.d("xeagle69", "error pahpat >>>  " + e.getMessage());
                    }


                    enableCharge.setValue(true);

                } else if (result.getValue().getRealPhoneNumber() != 0) {
                    final long realNumber = result.getValue().getRealPhoneNumber();
                    ConfirmMessageDialog dialog = new ConfirmMessageDialog();

                    //set * for some character
                    String realPhone = String.valueOf(result.getValue().getRealPhoneNumber());
                    StringBuilder builder = new StringBuilder(realPhone);

                    builder.setCharAt(4, '*');
                    builder.setCharAt(5, '*');
                    builder.setCharAt(6, '*');

                    dialog.setMessage("این پلاک به این شماره تعلق دارد \n " + builder + "\n افزایش اعتبار برای این شماره انجام خواهد شد \n آیا از ادامه شارژ اطمینان دارید ؟ ");
                    dialog.setCallBack(new ConfirmMessageDialog.DialogCallBack() {
                        @Override
                        public void onCallBack(String state) {
                            if (state == ConfirmMessageDialog.CONFIRM) {
                                chargeDriverWalletFromCard(context, realNumber);
                            }
                        }
                    });
                    dialog.show(((Activity) context).getFragmentManager(), "");

                    enableCharge.setValue(true);
                }

                progress.setValue(0);
            }

            @Override
            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                progress.setValue(0);
                switch (resultType) {
                    case RetrofitError:
                        ShowToast.getInstance().showError(context, R.string.exception_msg);
                        break;
                    case ServerError:
                        if (errorCode != 0)
                            ShowToast.getInstance().showError(context, errorCode);
                        else
                            ShowToast.getInstance().showError(context, R.string.connection_failed);
                        break;
                    default:
                        ShowToast.getInstance().showError(context, resultType.ordinal());
                }
            }
        });


    }


    public void part3(final Context mContext1, Bundle resultData) {

        final ThirdPartDto.ThirdPart requestObj = new ThirdPartDto.ThirdPart();
        requestObj.setAmount(Long.parseLong(resultData.getString("Amount")));
        requestObj.setId(Long.parseLong(resultData.getString("ResNum")));
        requestObj.setRRN(resultData.getString("RRN"));
        requestObj.setTraceNo(resultData.getString("TraceNo"));
        PreferenceManager.getDefaultSharedPreferences(myContext).edit().putString("last_trace_number", resultData.getString("TraceNo")).apply();
        requestObj.setApprovalCode(resultData.getString("ApprovalCode"));
        requestObj.setTransActionDateTime(resultData.getString("TransDate"));


        parkbanRepository.thirdPartRepoAction(requestObj, new ParkbanRepository.ServiceResultCallBack<ThirdPartResponseDto>() {
            @Override
            public void onSuccess(ThirdPartResponseDto result) {
                if (result.getValue().isResultAns() == true) {
                    PreferenceManager.getDefaultSharedPreferences(myContext).edit().putString("last_date_time", result.getValue().getDateTimeSolar()).apply();
                    Log.d("xeagle69", "onSuccess >>> part3 >>> inside true >>> " + result.getValue().getDateTimeSolar());
                    try {
                        EasyHelper.confirmTxn((Activity) mContext1, receiverHandler, requestObj.getApprovalCode(), true);
                    } catch (EasyHelper.PahpatException e) {
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

        parkbanRepository.forthPartRepoAction(Long.parseLong(factorId), new ParkbanRepository.ServiceResultCallBack<IncreaseDriverWalletCardResultDto>() {
            @Override
            public void onSuccess(IncreaseDriverWalletCardResultDto result) {
                if (result.getValue().getErrorMessage() == null || result.getValue().getErrorMessage() == "") {
                    if (result.getValue().getReceiptCode() != null) {
                        receiptCode = result.getValue().getReceiptCode();
                        QRCode = result.getValue().getQRCodeBase64();
                        dateTime = result.getValue().getSolarDateTime();

                        PreferenceManager.getDefaultSharedPreferences(myContext).edit().putString("receiptCode", receiptCode).apply();
                        PreferenceManager.getDefaultSharedPreferences(myContext).edit().putString("QRCode", QRCode).apply();
                        PreferenceManager.getDefaultSharedPreferences(myContext).edit().putString("dateTime", dateTime).apply();
                        PreferenceManager.getDefaultSharedPreferences(myContext).edit().putString("DriverWalletCashAmount", String.valueOf(result.getValue().getDriverWalletCashAmount())).apply();
                        PreferenceManager.getDefaultSharedPreferences(myContext).edit().putBoolean("FinalConfirm", true).apply();
                        PreferenceManager.getDefaultSharedPreferences(myContext).edit().putBoolean("ShowDialog", false).apply();


                    } else {
                        PreferenceManager.getDefaultSharedPreferences(myContext).edit().putBoolean("FinalConfirm", false).apply();
                        PreferenceManager.getDefaultSharedPreferences(myContext).edit().putBoolean("ShowDialog", true).apply();
                    }

                    enableCharge.setValue(true);

                } else {
                    PreferenceManager.getDefaultSharedPreferences(myContext).edit().putBoolean("FinalConfirm", false).apply();
                    PreferenceManager.getDefaultSharedPreferences(myContext).edit().putBoolean("ShowDialog", true).apply();
                }

                progress.setValue(0);


            }

            @Override
            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                progress.setValue(0);
                switch (resultType) {
                    case RetrofitError:
                        PreferenceManager.getDefaultSharedPreferences(myContext).edit().putBoolean("FinalConfirm", false).apply();
                        PreferenceManager.getDefaultSharedPreferences(myContext).edit().putBoolean("ShowDialog", true).apply();
                        ShowToast.getInstance().showError(myContext, R.string.exception_msg);
                        break;
                    case ServerError:
                        PreferenceManager.getDefaultSharedPreferences(myContext).edit().putBoolean("FinalConfirm", false).apply();
                        PreferenceManager.getDefaultSharedPreferences(myContext).edit().putBoolean("ShowDialog", true).apply();
                        if (errorCode != 0)
                            ShowToast.getInstance().showError(myContext, errorCode);
                        else
                            ShowToast.getInstance().showError(myContext, R.string.connection_failed);
                        break;
                    default:
                        PreferenceManager.getDefaultSharedPreferences(myContext).edit().putBoolean("FinalConfirm", false).apply();
                        PreferenceManager.getDefaultSharedPreferences(myContext).edit().putBoolean("ShowDialog", true).apply();
                        ShowToast.getInstance().showError(myContext, resultType.ordinal());
                }
            }
        });
    }


    public void showDialogAndPrint() {
        String receiptCode = PreferenceManager.getDefaultSharedPreferences(myContext).getString("receiptCode", "0");
        String QRCode = PreferenceManager.getDefaultSharedPreferences(myContext).getString("QRCode", "0");
        String dateTime = PreferenceManager.getDefaultSharedPreferences(myContext).getString("dateTime", "0");
        String DriverWalletCashAmount = PreferenceManager.getDefaultSharedPreferences(myContext).getString("DriverWalletCashAmount", "0");


        PaymentResultDialog dialog = new PaymentResultDialog();
        dialog.setItem(receiptCode, Long.parseLong(DriverWalletCashAmount), QRCode);
        dialog.setCallBack(new PaymentResultDialog.DialogCallBack() {
            @Override
            public void onCallBack(String state) {
                ((BaseActivity) myContext).finish();
            }
        });
        dialog.show(((Activity) myContext).getFragmentManager(), "");

        //print receipt
        if (idal != null) {
            mBitmap = generateBitmapByLayout(myContext);
            if (mPrintThread == null) {
                mPrintThread = new PrintThread();
            }
            mPrintThread.start();
        }

        enableCharge.setValue(true);
        progress.setValue(0);

    }


    public void showDialogForRetryOrPrint(final Context context3) {

        FailedConfirmDialog dialog = new FailedConfirmDialog();

        dialog.setMessage("تایید نهایی انجام نشد \n " + "\n افزایش اعتبار برای این شماره انجام خواهد شد \n ");


        dialog.setCallBack(new FailedConfirmDialog.DialogCallBack() {
            @Override
            public void onCallBack(String state) {
                if (state == FailedConfirmDialog.RETRY) {
                    String myFactorId = PreferenceManager.getDefaultSharedPreferences(context3).getString("factorId", "0");
                    //retry logic must implement here!
                    parkbanRepository.forthPartRepoAction(Long.parseLong(myFactorId), new ParkbanRepository.ServiceResultCallBack<IncreaseDriverWalletCardResultDto>() {
                        @Override
                        public void onSuccess(IncreaseDriverWalletCardResultDto result) {

                            if (result.getValue().getErrorMessage() == null || result.getValue().getErrorMessage() == "") {
                                if (result.getValue().getReceiptCode() != null) {
                                    receiptCode = result.getValue().getReceiptCode();
                                    QRCode = result.getValue().getQRCodeBase64();
                                    dateTime = result.getValue().getSolarDateTime();

                                    PreferenceManager.getDefaultSharedPreferences(myContext).edit().putString("receiptCode", receiptCode).apply();
                                    PreferenceManager.getDefaultSharedPreferences(myContext).edit().putString("QRCode", QRCode).apply();
                                    PreferenceManager.getDefaultSharedPreferences(myContext).edit().putString("dateTime", dateTime).apply();
                                    PreferenceManager.getDefaultSharedPreferences(myContext).edit().putString("DriverWalletCashAmount", String.valueOf(result.getValue().getDriverWalletCashAmount())).apply();
//                                    PreferenceManager.getDefaultSharedPreferences(myContext).edit().putBoolean("FinalConfirm", true).apply();
                                    PreferenceManager.getDefaultSharedPreferences(context3).edit().putBoolean("commingFromPahpat", false).apply();
                                    PreferenceManager.getDefaultSharedPreferences(context3).edit().putBoolean("ShowDialog", false).apply();
                                    showDialogAndPrint();

                                } else {
                                    ShowToast.getInstance().showError(context3, R.string.connection_failed);
                                    showDialogForRetryOrPrint(context3);

                                }

                                enableCharge.setValue(true);

                            } else {
                                ShowToast.getInstance().showError(context3, R.string.connection_failed);
                                showDialogForRetryOrPrint(context3);
                            }

                        }

                        @Override
                        public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                            ShowToast.getInstance().showError(context3, R.string.connection_failed);
                            showDialogForRetryOrPrint(context3);
                        }
                    });
                } else if (state == FailedConfirmDialog.PRINT) {


                    //print custom recipit must implement here!

                    if (idal != null) {
                        mBitmap = generateBitmapByLayout_Failed(context3);
                        if (mPrintThread == null) {
                            mPrintThread = new PrintThread();
                        }
                        mPrintThread.start();
                    }

                    //also we should add it to list of failed factor ids
                    String failedFactors = PreferenceManager.getDefaultSharedPreferences(context3).getString("failedFactors", "");
                    String myFactorId = PreferenceManager.getDefaultSharedPreferences(context3).getString("factorId", "0");
                    if (failedFactors.equals("")) {
                        failedFactors = myFactorId;
                    } else {
                        failedFactors = failedFactors + "," + myFactorId;
                    }
                    Log.d("xeagle69", "failed factor  added , its final >>> " + failedFactors);
                    PreferenceManager.getDefaultSharedPreferences(context3).edit().putString("failedFactors", failedFactors).apply();

                    PreferenceManager.getDefaultSharedPreferences(context3).edit().putBoolean("commingFromPahpat", false).apply();
                    PreferenceManager.getDefaultSharedPreferences(context3).edit().putBoolean("ShowDialog", false).apply();

                    progress.setValue(10);
                    ((BaseActivity) context3).finish();

                }
            }
        });


        dialog.show(((Activity) context3).getFragmentManager(), "");

    }


    public Bitmap generateBitmapByLayout_Failed(Context context) {
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.receipt_payment_failed, null);
        TextView receiptCodeText = view.findViewById(R.id.receipt_code_failed);
        TextView receiptFactorCodeText = view.findViewById(R.id.receipt_factor_code_failed);

//        ImageView QRImage = view.findViewById(R.id.QR_image_fa);
        TextView date = view.findViewById(R.id.date_failed);
        TextView time = view.findViewById(R.id.time_failed);
        LinearLayout carPlateLayout = view.findViewById(R.id.plate_layout_failed);
        LinearLayout motorPlateLayout = view.findViewById(R.id.motor_layout_failed);
        TextView p0 = view.findViewById(R.id.p0_failed);
        TextView p1 = view.findViewById(R.id.p1_failed);
        TextView p2 = view.findViewById(R.id.p2_failed);
        TextView p3 = view.findViewById(R.id.p3_failed);
        TextView m0 = view.findViewById(R.id.m0_failed);
        TextView m1 = view.findViewById(R.id.m1_failed);
        TextView parkbanPhone = view.findViewById(R.id.parkban_tel_failed);
        TextView complaintsTel = view.findViewById(R.id.complaints_tel_failed);
        TextView printType = view.findViewById(R.id.print_type_failed);
        printType.setText(R.string.charge_failed);

        LinearLayout costLayout = view.findViewById(R.id.cost_layout_failed);
        TextView costValue = view.findViewById(R.id.cost_value_failed);

        costLayout.setVisibility(View.VISIBLE);
        costValue.setText(String.valueOf(chargeItemSelected));

        complaintsTel.setText(BaseActivity.PhoneComplaints);

        if (!car.getValue()) {
            carPlateLayout.setVisibility(View.GONE);
            motorPlateLayout.setVisibility(View.VISIBLE);
            m0.setText(mplate__0.getValue());
            m1.setText(mplate__1.getValue());
        } else {
            carPlateLayout.setVisibility(View.VISIBLE);
            motorPlateLayout.setVisibility(View.GONE);
            p0.setText(plate__0.getValue());
            p1.setText(plate__1.getValue());
            p2.setText(plate__2.getValue());
            p3.setText(plate__3.getValue());
        }

        //this should be changed
        //printed shomare peygiri
        receiptCodeText.setText("کد پیگیری : " + PreferenceManager.getDefaultSharedPreferences(myContext).getString("last_trace_number", ""));
        //this should be assigned
        //printed  last three digits of parkban phone + factor id
        receiptFactorCodeText.setText("کد فاکتور : " + String.valueOf(chargeItemSelected) + PreferenceManager.getDefaultSharedPreferences(myContext).getString("factorId", "0"));

//        byte[] decodedString = Base64.decode(QRCode, Base64.DEFAULT);
//        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//        QRImage.setImageBitmap(decodedByte);

        date.setText(PreferenceManager.getDefaultSharedPreferences(myContext).getString("last_date_time", ""));
        parkbanPhone.setText(String.valueOf(BaseActivity.ParkbanPhoneNumber));

        return PrinterUtils.convertViewToBitmap(view);
    }


    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


    //below function will use for charging driver wallet via cache
    private void chargeDriverWallet(final Context context, long realPhoneNumber) {

        // verification for pelake  car  or motorcycle ! and assign the value for it !!!
        if (car.getValue()) {
            Log.d("xxxhhh", "chargeDriverWallet >>> car");
            if (plate__0 == null || plate__1 == null || plate__2 == null || plate__3 == null) {
                ShowToast.getInstance().showWarning(context, R.string.plate_not_complete);
                return;
            } else if (plate__0.getValue().length() < 2 || plate__2.getValue().length() < 3 || plate__3.getValue().length() < 2) {
                ShowToast.getInstance().showWarning(context, R.string.plate_not_complete);
                return;
            } else
                carPlate = plate__0.getValue() + plate__1.getValue() + plate__2.getValue() + plate__3.getValue();
            Log.d("xxxhhh", "chargeDriverWallet >>> car - " + carPlate);
        } else if (motor.getValue()) {
            Log.d("xxxhhh", "chargeDriverWallet >>> motor");
            if (mplate__1.getValue() == null || mplate__0.getValue() == null) {
                ShowToast.getInstance().showWarning(context, R.string.plate_not_complete);
                return;
            } else if (mplate__0.getValue().length() < 3 || mplate__1.getValue().length() < 5) {
                ShowToast.getInstance().showWarning(context, R.string.plate_not_complete);
                return;
            } else
                motorPlate = mplate__0.getValue() + mplate__1.getValue();
            Log.d("xxxhhh", "chargeDriverWallet >>> car - " + motorPlate);
        }
        // verification for phone number  of  driver  ! and assign the value for it !!!
        if (phoneNumber == null) {
            ShowToast.getInstance().showWarning(context, R.string.phone_number_need);
            return;
        } else if (phoneNumber != null || phoneNumber.equals(""))
            if (!phoneNumber.isEmpty()) {
                if (phoneNumber.length() < 11) {
                    ShowToast.getInstance().showWarning(context, R.string.phone_number_length);
                    return;
                } else if (!phoneNumber.substring(0, 2).contains("09")) {
                    ShowToast.getInstance().showWarning(context, R.string.phone_number_format);
                    return;
                }
                if (confirmPhoneNumber == null) {
                    ShowToast.getInstance().showWarning(context, R.string.phone_number_confirm);
                    return;
                } else {
                    if (confirmPhoneNumber.length() < 11) {
                        ShowToast.getInstance().showWarning(context, R.string.phone_number_confirm_length);
                        return;
                    }
                    if (!confirmPhoneNumber.substring(0, 2).contains("09")) {
                        ShowToast.getInstance().showWarning(context, R.string.phone_number_confirm_format);
                        return;
                    }
                    if (!confirmPhoneNumber.equals(phoneNumber)) {
                        ShowToast.getInstance().showWarning(context, R.string.phone_number_not_same_confirm);
                        return;
                    }
                }
            }
        //*!*!*!*!*!*!**!*!*!*!*!*!*!*!*!*!*!
        enableCharge.setValue(false);
        IncreaseDriverWalletResultDto.IncreaseDriverWalletDto wallet = new IncreaseDriverWalletResultDto.IncreaseDriverWalletDto();

        if (realPhoneNumber != 0)
            wallet.setPhoneNumber(realPhoneNumber);
        else
            wallet.setPhoneNumber(java.lang.Long.parseLong(phoneNumber));

        wallet.setAmount(chargeItemSelected);

        if (car.getValue()) {
            Log.d("xxxhhh", "chargeDriverWallet >>> car - prepare wallet obj " + carPlate + "wallet.setPlate");
            wallet.setPlate(carPlate);
        } else {
            Log.d("xxxhhh", "chargeDriverWallet >>> motor - prepare wallet obj " + motorPlate + "wallet.setPlate");
            wallet.setPlate(motorPlate);

        }


        progress.setValue(10);
        parkbanRepository.increaseDiverWallet(wallet, new ParkbanRepository.ServiceResultCallBack<IncreaseDriverWalletResultDto>() {
            @Override
            public void onSuccess(final IncreaseDriverWalletResultDto result) {
                if (result.getValue().getErrorMessage() == null || result.getValue().getErrorMessage() == "") {
                    if (result.getValue().getReceiptCode() != null) {
                        receiptCode = result.getValue().getReceiptCode();
                        QRCode = result.getValue().getQRCodeBase64();
                        dateTime = result.getValue().getSolarDateTime();

                        PaymentResultDialog dialog = new PaymentResultDialog();
                        dialog.setItem(receiptCode, result.getValue().getDriverWalletCashAmount(), QRCode);
                        dialog.setCallBack(new PaymentResultDialog.DialogCallBack() {
                            @Override
                            public void onCallBack(String state) {
                                ((BaseActivity) context).finish();
                            }
                        });
                        dialog.show(((Activity) context).getFragmentManager(), "");

                        //print receipt
                        if (idal != null) {
                            mBitmap = generateBitmapByLayout(context);
                            if (mPrintThread == null) {
                                mPrintThread = new PrintThread();
                            }
                            mPrintThread.start();
                        }
                    }

                    enableCharge.setValue(true);

                } else if (result.getValue().getRealPhoneNumber() != 0) {
                    final long realNumber = result.getValue().getRealPhoneNumber();
                    ConfirmMessageDialog dialog = new ConfirmMessageDialog();

                    //set * for some character
                    String realPhone = String.valueOf(result.getValue().getRealPhoneNumber());
                    StringBuilder builder = new StringBuilder(realPhone);

                    builder.setCharAt(4, '*');
                    builder.setCharAt(5, '*');
                    builder.setCharAt(6, '*');

                    dialog.setMessage("این پلاک به این شماره تعلق دارد \n " + builder + "\n افزایش اعتبار برای این شماره انجام خواهد شد \n آیا از ادامه شارژ اطمینان دارید ؟ ");
                    dialog.setCallBack(new ConfirmMessageDialog.DialogCallBack() {
                        @Override
                        public void onCallBack(String state) {
                            if (state == ConfirmMessageDialog.CONFIRM) {
                                chargeDriverWallet(context, realNumber);
                            }
                        }
                    });
                    dialog.show(((Activity) context).getFragmentManager(), "");

                    enableCharge.setValue(true);
                }

                progress.setValue(0);
            }

            @Override
            public void onFailed(ResponseResultType resultType, String message, int errorCode) {
                progress.setValue(0);
                switch (resultType) {
                    case RetrofitError:
                        ShowToast.getInstance().showError(context, R.string.exception_msg);
                        break;
                    case ServerError:
                        if (errorCode != 0)
                            ShowToast.getInstance().showError(context, errorCode);
                        else
                            ShowToast.getInstance().showError(context, R.string.connection_failed);
                        break;
                    default:
                        ShowToast.getInstance().showError(context, resultType.ordinal());
                }
            }
        });
    }


    class PrintThread extends Thread {
        @Override
        public void run() {
            try {
                iPrinter.init();
                iPrinter.setGray(100);
                iPrinter.printBitmap(mBitmap);
                iPrinter.start();
            } catch (PrinterDevException e) {
                e.printStackTrace();
            }
        }

    }

    public Bitmap generateBitmapByLayout(Context context) {
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.receipt_payment, null);
//        TextView receiptCodeText = view.findViewById(R.id.receipt_code);
//        ImageView QRImage = view.findViewById(R.id.QR_image);
//        TextView date = view.findViewById(R.id.date);
//        TextView time = view.findViewById(R.id.time);
//        LinearLayout carPlateLayout = view.findViewById(R.id.plate_layout);
//        LinearLayout motorPlateLayout = view.findViewById(R.id.motor_layout);
//        TextView p0 = view.findViewById(R.id.p0);
//        TextView p1 = view.findViewById(R.id.p1);
//        TextView p2 = view.findViewById(R.id.p2);
//        TextView p3 = view.findViewById(R.id.p3);
//        TextView m0 = view.findViewById(R.id.m0);
//        TextView m1 = view.findViewById(R.id.m1);
//        TextView parkbanPhone = view.findViewById(R.id.parkban_tel);
//        TextView complaintsTel = view.findViewById(R.id.complaints_tel);
//        TextView printType = view.findViewById(R.id.print_type);
//        printType.setText(R.string.charge_type);
//
//        LinearLayout costLayout = view.findViewById(R.id.cost_layout);
//        TextView costValue = view.findViewById(R.id.cost_value);
//
//        costLayout.setVisibility(View.VISIBLE);
//        costValue.setText(String.valueOf(chargeItemSelected));
//
//        complaintsTel.setText(BaseActivity.PhoneComplaints);
//
//        if (!car.getValue()) {
//            carPlateLayout.setVisibility(View.GONE);
//            motorPlateLayout.setVisibility(View.VISIBLE);
//            m0.setText(mplate__0.getValue());
//            m1.setText(mplate__1.getValue());
//        } else {
//            carPlateLayout.setVisibility(View.VISIBLE);
//            motorPlateLayout.setVisibility(View.GONE);
//            p0.setText(plate__0.getValue());
//            p1.setText(plate__1.getValue());
//            p2.setText(plate__2.getValue());
//            p3.setText(plate__3.getValue());
//        }
//
//        receiptCodeText.setText(receiptCode);
//
//        byte[] decodedString = Base64.decode(QRCode, Base64.DEFAULT);
//        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//        QRImage.setImageBitmap(decodedByte);
//        date.setText(dateTime);
//        parkbanPhone.setText(String.valueOf(BaseActivity.ParkbanPhoneNumber));

        return PrinterUtils.convertViewToBitmap(view);
    }


}
