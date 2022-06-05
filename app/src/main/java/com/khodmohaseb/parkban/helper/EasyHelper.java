package com.khodmohaseb.parkban.helper;

import android.app.Activity;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class EasyHelper {

    public static final int GOOD_PAYMENT = 1;
    public static final int GOOD_CONFIRM = 2;
    public static final int BALANCE = 3;
    public static final int AUTHORISE = 7;
    public static final int RECEIPT = 8;
    public static final int GET_STATUS = 20;
    public static final int TXN_SUCCESS = 1;
    public static final int TXN_INPROCESS = 2;
    public static final int TXN_FAILED = 5;
    public static final int ANNOUNCEMENT_REQ = 14;


    public interface Receiver {
        void onReceiveResult(int serviceId, int resultCode, Bundle resultData);
    }

    public static class PahpatException extends Exception {

        public PahpatException(String message) {
            super(message);
        }

    }

    public static class EMerchantInfoResult {
        String MID;
        String name;
        String tel;
        String address;
        String message;
        String postalCode;
        String terminalId;

        public String getMID() {
            return MID;
        }

        public void setMID(String MID) {
            this.MID = MID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getTerminalId() {
            return terminalId;
        }

        public void setTerminalId(String terminalId) {
            this.terminalId = terminalId;
        }
    }

    public static class EMerchantAccount {
        String MID;
        String title;
        String bankName;
        String localBankName;
        String number;
        boolean active;

        public String getMID() {
            return MID;
        }

        public void setMID(String MID) {
            this.MID = MID;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getLocalBankName() {
            return localBankName;
        }

        public void setLocalBankName(String localBankName) {
            this.localBankName = localBankName;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }


    //********************************************
    @MainThread
    public static void announcementReq(int serviceId, Handler receiverHandler, Activity activity, AnnouncementRequestTypes type) throws PahpatException {
        if (!(activity instanceof Receiver))
            throw new PahpatException("Activity must implement Pahpat.Receiver interface.");

        new AnnouncementReq(serviceId, receiverHandler, activity, type).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class AnnouncementReq extends AsyncTask {


        private final Handler receiverHandler;
        private final Activity activity;
        private final int serviceId;
        private final AnnouncementRequestTypes reqType;

        AnnouncementReq(int serviceId, Handler receiverHandler, Activity activity, AnnouncementRequestTypes type) {
            this.serviceId = serviceId;
            this.receiverHandler = receiverHandler;
            this.activity = activity;
            this.reqType = type;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            PahpatResultReceiver mReceiver = new PahpatResultReceiver(receiverHandler);
            mReceiver.setReceiver((Receiver) activity);
            mReceiver.setServiceId(serviceId);
            Intent mobileInquiryIntent = new Intent("com.tosantechno.pahpat.services.AnnouncementService");
            mobileInquiryIntent.setPackage("com.tosantechno.pahpat");
            mobileInquiryIntent.putExtra("ResultReceiver", receiverForSending(mReceiver));
            mobileInquiryIntent.putExtra("TransType", ANNOUNCEMENT_REQ);
            mobileInquiryIntent.putExtra("RequestType", reqType.getTypeCode());
            activity.startService(mobileInquiryIntent);
            return null;
        }
    }

    //********************************************
    @MainThread
    public static void printReceipt(@NonNull Activity activity, @NonNull Handler receiverHandler, int TransType, Bundle data) throws PahpatException {
        if (!(activity instanceof Receiver))
            throw new PahpatException("Activity must implement Pahpat.Receiver interface.");

        new PrintReceipt(activity, receiverHandler, TransType, data).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class PrintReceipt extends AsyncTask {

        private final int transType;
        private final Bundle data;
        private final Handler receiverHandler;
        private final Activity activity;

        public PrintReceipt(Activity activity, Handler receiverHandler, int transType, Bundle data) {

            this.activity = activity;
            this.receiverHandler = receiverHandler;
            this.transType = transType;
            this.data = data;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            PahpatResultReceiver mReceiver = new PahpatResultReceiver(receiverHandler);
            mReceiver.setReceiver((Receiver) activity);
            mReceiver.setServiceId(RECEIPT);
            Intent receiptIntent = new Intent("com.tosantechno.pahpat.services.ReceiptService");
            receiptIntent.setPackage("com.tosantechno.pahpat");
            receiptIntent.putExtras(data);
            receiptIntent.putExtra("ResultReceiver", receiverForSending(mReceiver));
            receiptIntent.putExtra("TransType", transType);


            activity.startService(receiptIntent);
            return null;
        }
    }

    //********************************************
    @MainThread
    public static void getStatus(int serviceId, Handler receiverHandler, Activity activity) throws PahpatException {
        if (!(activity instanceof Receiver))
            throw new PahpatException("Activity must implement Pahpat.Receiver interface.");

        new GetStatus(serviceId, receiverHandler, activity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class GetStatus extends AsyncTask {


        private final Handler receiverHandler;
        private final Activity activity;
        private final int serviceId;

        GetStatus(int serviceId, Handler receiverHandler, Activity activity) {
            this.serviceId = serviceId;
            this.receiverHandler = receiverHandler;
            this.activity = activity;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            PahpatResultReceiver mReceiver = new PahpatResultReceiver(receiverHandler);
            mReceiver.setReceiver((Receiver) activity);
            mReceiver.setServiceId(serviceId);
            Intent pahpatServiceIntent = new Intent("com.tosantechno.pahpat.services.PahpatService");
            pahpatServiceIntent.setPackage("com.tosantechno.pahpat");
            pahpatServiceIntent.putExtra("ResultReceiver", receiverForSending(mReceiver));
            activity.startService(pahpatServiceIntent);
            return null;
        }
    }

    //********************************************
    @MainThread
    public static void balanceTxn(@NonNull Activity activity, @NonNull Handler receiverHandler, @NonNull String resNum) throws PahpatException {
        if (!(activity instanceof Receiver))
            throw new PahpatException("Activity must implement Pahpat.Receiver interface.");

        PahpatResultReceiver resultReceiver = new PahpatResultReceiver(receiverHandler);
        resultReceiver.setServiceId(EasyHelper.BALANCE);
        resultReceiver.setReceiver((Receiver) activity);

        Intent intent = new Intent("com.tosantechno.pahpat.BALANCE");
        intent.putExtra("TransType", 3);
        intent.putExtra("ResNum", resNum);
        intent.putExtra("ResultReceiver", EasyHelper.receiverForSending(resultReceiver));
        activity.startActivityForResult(intent, EasyHelper.BALANCE);

    }


    //**********************************************************************************************************************************************
    //**********************************************************************************************************************************************
    //**********************************************************************************************************************************************
    /**
     * Attention ::
     * This method need use permission in android manifest like below :
     * <uses-permission android:name="com.tosantechno.pahpat.permission.PAYMENT_SERVICE" />
     *
     * @param activity
     * @param receiverHandler
     * @param approvalCode
     * @param decideType
     * @throws PahpatException
     */
    @MainThread
    public static void confirmTxn(@NonNull Activity activity, @NonNull Handler receiverHandler, @NonNull String approvalCode, @NonNull boolean decideType) throws PahpatException {
        if (!(activity instanceof Receiver))
            throw new PahpatException("Activity must implement Pahpat.Receiver interface.");

        PahpatResultReceiver resultReceiver = new PahpatResultReceiver(receiverHandler);
        resultReceiver.setServiceId(EasyHelper.GOOD_CONFIRM);
        resultReceiver.setReceiver((Receiver) activity);

        Intent confirmIntent = new Intent("com.tosantechno.pahpat.DO_CONFIRM");
        confirmIntent.putExtra("ApprovalCode", approvalCode);
        confirmIntent.putExtra("DecideType", decideType);
        confirmIntent.putExtra("ResultReceiver", EasyHelper.receiverForSending(resultReceiver));
        activity.startActivityForResult(confirmIntent, EasyHelper.GOOD_CONFIRM);

    }

    @MainThread
    public static void purchaseTxn(@NonNull Activity activity, @NonNull Handler receiverHandler, @NonNull String amount, @NonNull String merchantMobileNo,
                                   @NonNull String resNum, @NonNull String currency, @NonNull String lang,
                                   @Nullable HashMap<String, String> apportionmentList, @Nullable String goodReferenceId, @Nullable String tipAmount) throws PahpatException {

        if (!(activity instanceof Receiver))
            throw new PahpatException("Activity must implement Pahpat.Receiver interface.");

        Intent paymentIntent = new Intent("com.tosantechno.pahpat.DO_PAYMENT");

        PahpatResultReceiver resultReceiver = new PahpatResultReceiver(receiverHandler);
        resultReceiver.setServiceId(EasyHelper.GOOD_PAYMENT);
        resultReceiver.setReceiver((Receiver) activity);

        if (!amount.equals("")) {

            paymentIntent.putExtra("TransType", 1);
            paymentIntent.putExtra("Amount", amount);
            paymentIntent.putExtra("MerchantMobileNo", merchantMobileNo);
            paymentIntent.putExtra("ResNum", resNum);
            paymentIntent.putExtra("Currency", currency);
            paymentIntent.putExtra("Lang", lang);
            paymentIntent.putExtra("ResultReceiver", EasyHelper.receiverForSending(resultReceiver));
            if (apportionmentList != null && !apportionmentList.isEmpty())
                paymentIntent.putExtra("ApportionmentList", apportionmentList);
            if (goodReferenceId != null && !goodReferenceId.isEmpty())
                paymentIntent.putExtra("GoodReferenceId", goodReferenceId);
            if (tipAmount != null && !tipAmount.isEmpty())
                paymentIntent.putExtra("TipAmount", tipAmount);

            activity.startActivityForResult(paymentIntent, EasyHelper.GOOD_PAYMENT);
        }
    }

    public static void purchaseTxn(@NonNull Activity activity, @NonNull Handler receiverHandler, @NonNull String amount, @NonNull String merchantMobileNo,
                                   @NonNull String resNum, @NonNull String currency, @NonNull String lang) throws PahpatException {
        purchaseTxn(activity, receiverHandler, amount, merchantMobileNo, resNum, currency, lang, null, null, null);
    }

    public static void purchaseTxn(@NonNull Activity activity, @NonNull Handler receiverHandler, @NonNull String amount, @NonNull String merchantMobileNo,
                                   @NonNull String resNum, @NonNull String currency, @NonNull String lang, @Nullable HashMap<String, String> apportionmentList) throws PahpatException {
        purchaseTxn(activity, receiverHandler, amount, merchantMobileNo, resNum, currency, lang, apportionmentList, null, null);
    }

    public static void purchaseTxn(@NonNull Activity activity, @NonNull Handler receiverHandler, @NonNull String amount, @NonNull String merchantMobileNo,
                                   @NonNull String resNum, @NonNull String currency, @NonNull String lang, @Nullable String goodReferenceId, @Nullable String tipAmount) throws PahpatException {
        purchaseTxn(activity, receiverHandler, amount, merchantMobileNo, resNum, currency, lang, null, goodReferenceId, tipAmount);
    }
    //**********************************************************************************************************************************************
    //**********************************************************************************************************************************************
    //**********************************************************************************************************************************************

    /***/
    public static EMerchantInfoResult getMerchantInfo(Context context) throws PahpatException, RemoteException {
        Uri contentURI = Uri.parse("content://com.tosantechno.pahpat.contentProvider.merchants/merchant");
        ContentProviderClient CR = context.getContentResolver().acquireContentProviderClient(contentURI);
        EMerchantInfoResult merchantInfoResult = new EMerchantInfoResult();
        Cursor cpCursor = null;
        if (CR != null) {
            try {
                cpCursor = CR.query(contentURI, null, null, null, null);
                if (cpCursor != null && cpCursor.moveToFirst()) {

                    for (String key : cpCursor.getColumnNames()) {
                        Log.d("Merchant", key + " : " + cpCursor.getString(cpCursor.getColumnIndex(key)));
                    }

                    merchantInfoResult.setMID(cpCursor.getString(cpCursor.getColumnIndex("MID")));
                    merchantInfoResult.setName(cpCursor.getString(cpCursor.getColumnIndex("name")));
                    merchantInfoResult.setTel(cpCursor.getString(cpCursor.getColumnIndex("tel")));
                    merchantInfoResult.setAddress(cpCursor.getString(cpCursor.getColumnIndex("address")));
                    merchantInfoResult.setMessage(cpCursor.getString(cpCursor.getColumnIndex("message")));
                    merchantInfoResult.setPostalCode(cpCursor.getString(cpCursor.getColumnIndex("postalCode")));
                    merchantInfoResult.setTerminalId(cpCursor.getString(cpCursor.getColumnIndex("terminalId")));
                }
            } catch (Exception e) {
                throw new PahpatException(e.getMessage());
            }

            cpCursor.close();
            CR.release();
            return merchantInfoResult;
        } else
            throw new PahpatException("content provider not exits");
    }

    public static List<EMerchantAccount> getMerchantAccount(Context context, String merchantID) throws PahpatException, RemoteException {
        Uri contentURI = Uri.parse("content://com.tosantechno.pahpat.contentProvider.merchantAccounts/merchantAccount/" + merchantID);
        ContentProviderClient CR = context.getContentResolver().acquireContentProviderClient(contentURI);
        List<EMerchantAccount> merchantAccountList = new ArrayList<>();
        Cursor cpCursor = null;
        if (CR != null) {
            try {
                cpCursor = CR.query(contentURI, null, null, null, null);
                if (cpCursor != null && cpCursor.moveToFirst()) {

                    for (String key : cpCursor.getColumnNames()) {
                        Log.d("Merchant account", key + " : " + cpCursor.getString(cpCursor.getColumnIndex(key)));
                    }

                    do {
                        EMerchantAccount merchantAccount = new EMerchantAccount();
                        merchantAccount.setMID(cpCursor.getString(cpCursor.getColumnIndex("MID")));
                        merchantAccount.setTitle(cpCursor.getString(cpCursor.getColumnIndex("title")));
                        merchantAccount.setNumber(cpCursor.getString(cpCursor.getColumnIndex("number")));
                        merchantAccount.setBankName(cpCursor.getString(cpCursor.getColumnIndex("bankName")));
                        merchantAccount.setLocalBankName(cpCursor.getString(cpCursor.getColumnIndex("localBankName")));
                        merchantAccount.setActive(cpCursor.getInt(cpCursor.getColumnIndex("active")) == 1);
                        merchantAccountList.add(merchantAccount);
                        // do what ever you want here
                    } while (cpCursor.moveToNext());
                }
            } catch (Exception e) {
                throw new PahpatException(e.getMessage());
            }

            cpCursor.close();
            CR.release();
            return merchantAccountList;
        } else
            throw new PahpatException("content provider not exits");
    }

    //********************************************


    //**********************************************************************************************************************************************
    //**********************************************************************************************************************************************
    //**********************************************************************************************************************************************

    public static ResultReceiver receiverForSending(ResultReceiver actualReceiver) {
        Parcel parcel = Parcel.obtain();
        actualReceiver.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        ResultReceiver receiverForSending = ResultReceiver.CREATOR.createFromParcel(parcel);
        parcel.recycle();
        return receiverForSending;
    }

    public static class PahpatResultReceiver extends ResultReceiver {

        Receiver mReceiver = null;
        int serviceId;

        public PahpatResultReceiver(Handler handler) {
            super(handler);
        }

        public void setServiceId(int serviceId) {
            this.serviceId = serviceId;
        }

        public void setReceiver(Receiver receiver) {
            mReceiver = receiver;
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle resultData) {
            Log.d("EasyHelper:", resultCode + "");
            if (mReceiver != null) {
                mReceiver.onReceiveResult(serviceId, resultCode, resultData);
            }
        }
    }

    //**********************************************************************************************************************************************
    //**********************************************************************************************************************************************

    public enum AnnouncementRequestTypes {
        RequestRoll("R"),
        DeviceFailure("D");

        private final String typeCode;

        AnnouncementRequestTypes(String type) {
            this.typeCode = type;
        }

        public String getTypeCode() {
            return typeCode;
        }
    }
}
