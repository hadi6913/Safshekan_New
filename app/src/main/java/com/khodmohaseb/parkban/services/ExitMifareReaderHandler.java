package com.khodmohaseb.parkban.services;

import android.app.Service;
import android.content.Intent;
import android.device.PiccManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.khodmohaseb.parkban.EnterMifareActivity;
import com.khodmohaseb.parkban.ExitMifareActivity;
import com.khodmohaseb.parkban.utils.ByteUtils;
import com.khodmohaseb.parkban.utils.Utils;

public class ExitMifareReaderHandler extends Service implements Runnable {

    private final static String TAG = "xeagle6913 ExitMifareReader";

    private byte[] block16 = new byte[16];
    private byte[] block17 = new byte[16];
    private byte[] block18 = new byte[16];



    private Thread runningThread;
    private boolean stopConditions = false;
    private static ExitMifareReaderHandler instance;
    Callbacks activity;
    ExitMifareActivity exitMifareActivity;
    private final IBinder mBinder = new LocalBinder();


    PiccManager piccManager;
    int scan_card = -1;
    int SNLen = -1;


    @Override
    public void onCreate() {

        if (instance == null)
            instance = this;

        piccManager = new PiccManager();
        Log.d(TAG, "onCreate > PiccManager object created");
        int result = piccManager.open();
        Log.d(TAG, "onCreate > PiccManager opened , result : " + result);


    }

    public static ExitMifareReaderHandler getInstance() {
        return instance;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        block16 = intent.getByteArrayExtra("bl20");
//        block17 = intent.getByteArrayExtra("bl21");
//        block18 = intent.getByteArrayExtra("bl22");


        runningThread = new Thread(this);
        runningThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        piccManager.close();
        stopConditions = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void registerClient(ExitMifareActivity exitMifareActivity) {
        this.activity = (ExitMifareReaderHandler.Callbacks) exitMifareActivity;
        this.exitMifareActivity = exitMifareActivity;
    }

    public class LocalBinder extends Binder {
        public ExitMifareReaderHandler getServiceInstance() {
            return ExitMifareReaderHandler.this;
        }
    }

    @Override
    public void run() {
        while (!stopConditions) {
            try {
                handleCard();
                Thread.sleep(300);
            } catch (Exception ex) {
                Log.d(TAG, "exception outside handleCard : " + ex.getMessage());
            }
        }
    }


    public static long getValue(byte[] input) {
        long val = 0;
        for (int i = 0; i < input.length; i++) {
            val = (val * 256) + (input[i] & 0xFF);
        }
        return val;
    }


    public interface Callbacks {
        void exitMifareResult(boolean result, byte[] bl16,byte[] bl17,byte[] bl18);
    }

    private void handleCard() {


        try {


            byte CardType[] = new byte[2];
            byte Atq[] = new byte[14];
            char SAK = 1;
            byte sak[] = new byte[1];
            sak[0] = (byte) SAK;
            byte SN[] = new byte[10];
            scan_card = piccManager.request(CardType, Atq);
            if (scan_card > 0) {

                SNLen = piccManager.antisel(SN, sak);
                Log.d(TAG, " SNLen = " + SNLen);
                Log.d(TAG, " SN = " + Utils.getInstance().bcdToStr(SN));


                if (SNLen == 4) {



                    byte[] bufferAuth = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,};



                    int auth = piccManager.m1_keyAuth(1, 16, 6, bufferAuth, SNLen, SN);


                    block16 = new byte[16];
                    block17 = new byte[16];
                    block18 = new byte[16];



                    int result1 =  piccManager.m1_readBlock(16,block16);
                    int result2 =  piccManager.m1_readBlock(17,block17);
                    int result3 =  piccManager.m1_readBlock(18,block18);

                    Log.d(TAG, "auth result  : "+auth);
                    Log.d(TAG, "read block 16  >>>  "+result1);
                    Log.d(TAG, "block 16  >>>  "+ ByteUtils.ByteArrToHex(block16) );
                    Log.d(TAG, "read block 17  >>>  "+result2);
                    Log.d(TAG, "block 17  >>>  "+ByteUtils.ByteArrToHex(block17));
                    Log.d(TAG, "read block 18  >>>  "+result3);
                    Log.d(TAG, "block 18  >>>  "+ByteUtils.ByteArrToHex(block18));

//
//                    byte[] bufferblock16 = new byte[16];
//                    byte[] bufferblock17 = new byte[16];
//                    byte[] bufferblock18 = new byte[16];
//                    int result20 = piccManager.m1_readBlock(16, bufferblock16);
//                    int result21 = piccManager.m1_readBlock(17, bufferblock17);
//                    int result22 = piccManager.m1_readBlock(18, bufferblock18);
//                    Log.d(TAG, "after write: \n" +
//                            "\nauth result : " + auth +
//                            "\nresult20 : " + result20 +
//                            "\nvalue20 : " + ByteUtils.ByteArrToHex(bufferblock16) +
//                            "\nresult21 : " + result21 +
//                            "\nvalue21 : " + ByteUtils.ByteArrToHex(bufferblock17) +
//                            "\nresult22 : " + result22 +
//                            "\nvalue22 : " + ByteUtils.ByteArrToHex(bufferblock18)
//                    );



                    if ((result1 == 16)&&(result2 ==16)&&(result3 == 16)){
                        activity.exitMifareResult(true,block16,block17,block18);
                    }else {
                        activity.exitMifareResult(false,block16,block17,block18);
                    }









































//                    if (auth == 0) {
//                        //its raw-card and first time mifare
//                        //lets change its password
//                        Log.d(TAG, "handleCard:  its raw mifare card");
//
//                        byte[] bufferbl_23 = new byte[]{
//                                (byte) 0x00,
//                                (byte) 0x00,
//                                (byte) 0x00,
//                                (byte) 0x00,
//                                (byte) 0x00,
//                                (byte) 0x00,
//
//                                (byte) 0x87,
//                                (byte) 0x88,
//                                (byte) 0x77,
//                                (byte) 0x00,
//
//                                (byte) 0x3F,
//                                (byte) 0x10,
//                                (byte) 0x12,
//                                (byte) 0x3F,
//                                (byte) 0x05,
//                                (byte) 0x3F,
//                        };
//
//
//                        byte[] bufferbl_20_21_22 = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,};
//                        piccManager.m1_writeBlock(24, 16, bufferbl_20_21_22);
//                        piccManager.m1_writeBlock(25, 16, bufferbl_20_21_22);
//                        piccManager.m1_writeBlock(26, 16, bufferbl_20_21_22);
//                        piccManager.m1_writeBlock(27, 16, bufferbl_23);
//
//
//                    } else {
//
//                        Log.d(TAG, "handleCard: its elmosanat mifare card");
//
//                        piccManager.close();
//                        piccManager.open();
//                        piccManager.request(CardType, Atq);
//                        piccManager.antisel(SN, sak);
//
//
//                        byte[] bufferbl_23___ = new byte[]{
//                                (byte) 0x3F,
//                                (byte) 0x10,
//                                (byte) 0x12,
//                                (byte) 0x3F,
//                                (byte) 0x05,
//                                (byte) 0x3F,
//                        };
//
//
//                        int auth_ = piccManager.m1_keyAuth(1, 24, 6, bufferbl_23___, SNLen, SN);
//
//
//
//
//                        if (auth == 0){
//
////                            piccManager.m1_writeBlock(20,16,block20);
////                            piccManager.m1_writeBlock(21,16,block21);
////                            piccManager.m1_writeBlock(22,16,block22);
//
//
//                            int result20 = piccManager.m1_readBlock(24, bufferblock20);
//                            int result21 = piccManager.m1_readBlock(25, bufferblock21);
//                            int result22 = piccManager.m1_readBlock(26, bufferblock22);
//
//
//                            Log.d(TAG, "after write: \n" +
//                                    "\nauth result : " + auth_ +
//                                    "\nresult20 : " + result20 +
//                                    "\nvalue20 : " + ByteUtils.ByteArrToHex(bufferblock20) +
//                                    "\nresult21 : " + result21 +
//                                    "\nvalue21 : " + ByteUtils.ByteArrToHex(bufferblock21) +
//                                    "\nresult22 : " + result22+
//                                    "\nvalue22 : " + ByteUtils.ByteArrToHex(bufferblock22)
//                            );
//
//                            activity.enterMifareResult(true);
//
//                        }else{
//
//                            activity.enterMifareResult(false);
//
//                        }
//
//
//
//
//
//
//
//
//
//                    }


                }


            }

        } catch (Exception ex) {
            Log.d(TAG, "exception inside handleCard : " + ex.getMessage());
            activity.exitMifareResult(false , block16,block17,block18);
        }


//        try {
//            FindCardResult result = searchingCard();
//            switch (result.getCardType()) {
//                case Mifare_1K:
//                    if (!cardDetected) {
//                        cardDetected = true;
//                        if (activity != null) {
//                            lastInterActionDate = Calendar.getInstance().getTime();
//                            activity.setBrightness(BrightnessType.FULL);
//                        }
//                        ProcessCardResult processRes = processCard(result.getCardSerialNumber());
//                        if (activity != null && processRes != null) {
//                            activity.showOnUi(processRes);
//                        }
//                        if (processRes != null && processRes.getValidationResults().equals(CardValidationResults.SUCCESS)) {
//                            setTransactionCountForUI(getTransactionCountForUI() + 1);
//                            StatFileManager.getInstance().writeMessage(ReaderFileTagType.VA, buildTransactionLog(processRes.getCard()));
//                        }
//                    }
//                    break;
//                default:
//                    cardDetected = false;
//                    break;
//            }
//        } catch (Exception ex) {
//            log.error("ReaderHandler > handleCard():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
    }
}
