package com.khodmohaseb.parkban.services;

import android.app.Service;
import android.content.Intent;
import android.device.PiccManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.khodmohaseb.parkban.EnterMifareActivity;
import com.khodmohaseb.parkban.MainActivity;
import com.khodmohaseb.parkban.utils.Utils;

public class EnterMifareReaderHandler extends Service implements Runnable {

    private final static String TAG = "EnterMifareReader";



    private Thread runningThread;
    private boolean stopConditions = false;
    private static EnterMifareReaderHandler instance;
    Callbacks activity;
    EnterMifareActivity enterMifareActivity;
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
        Log.d(TAG, "onCreate > PiccManager opened , result : "+result);



    }

    public static EnterMifareReaderHandler getInstance() {
        return instance;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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

    public void registerClient(EnterMifareActivity enterMifareActivity) {
        this.activity = (Callbacks) enterMifareActivity;
        this.enterMifareActivity = enterMifareActivity;
    }

    public class LocalBinder extends Binder {
        public EnterMifareReaderHandler getServiceInstance() {
            return EnterMifareReaderHandler.this;
        }
    }

    @Override
    public void run() {
        while (!stopConditions) {
            try {
                handleCard();
                Thread.sleep(100);
            } catch (Exception ex) {
                Log.d(TAG, "exception outside handleCard : "+ex.getMessage());
            }
        }
    }





    public static long getValue(byte[] input)
    {
        long val = 0;
        for (int i = 0; i < input.length ; i++)
        {
            val = (val * 256) + (input[i] & 0xFF);
        }
        return val;
    }



    public interface Callbacks {
        void showOnUi(String serialNumber);
//
//        void openShift(Long cardSerial, String welcomeMsg, boolean resume);
//
//        void gotoLineAdminPage(Long cardSerial, String adminCode);
//
//        void gotoOperatorPage(Long cardSerial, String operatorCode);
//
//        void gotoDriverPage(Long cardSerial, String welcomeMsg);
//
//        void closeShift(Long cardSerial, boolean isSystem);
//
//        void closeShift(Long cardSerial, boolean isSystem, boolean isLineAdmin);
//
//        void openMaintenance();
//
//        void setTransactionCountOnUI(String count);
//
//        boolean isInitialTaskCheckedShiftStatus();
//
//        void setBrightness(BrightnessType type);
//
//        BrightnessType getBrightness();
//
//        UiCommandTypeEnum getCurrentUiPage();
    }
//
//    private void handleCrewCard() {
////        try {
////            FindCardResult result = searchingCard();
////            switch (result.getCardType()) {
////                case Mifare_1K:
////                    if (!cardDetected) {
////                        cardDetected = true;
////                        if (activity != null) {
////                            lastInterActionDate = Calendar.getInstance().getTime();
////                            activity.setBrightness(BrightnessType.FULL);
////                        }
////                        ProcessCardResult processRes = processCardForShiftOrMaintenance(result.getCardSerialNumber());
////                        if (processRes == null) {
////                            lastCardSerial = result.getCardSerialNumber();
////                            lastTransDate = Calendar.getInstance().getTime();
////                        } else {
////                            if (activity != null) {
////                                if (processRes.getValidationResults().equals(CardValidationResults.NOT_A_CREW_CARD) || processRes.getValidationResults().equals(CardValidationResults.PROBLEM_WHILE_VALIDATING_CREW_CARD))
////                                    activity.showOnUi(processRes);
////                            }
////                        }
////                    }
////                    break;
////                default:
////                    cardDetected = false;
////                    break;
////            }
////        } catch (Exception ex) {
////            log.error("ReaderHandler > handleCrewCard():" + ex.getMessage());
////            StringWriter errors = new StringWriter();
////            ex.printStackTrace(new PrintWriter(errors));
////            log.error(errors.toString());
////        }
//    }
//
    private void handleCard() {


        Log.d(TAG, "handleCard has been called");


        try {


            byte CardType[] = new byte[2];
            byte Atq[] = new byte[14];
            char SAK = 1;
            byte sak[] = new byte[1];
            sak[0] = (byte) SAK;
            byte SN[] = new byte[10];
            scan_card = piccManager.request(CardType, Atq);
            if(scan_card > 0) {

                SNLen = piccManager.antisel(SN, sak);
                Log.d(TAG, " SNLen = " + SNLen);
                Log.d(TAG, " SN = " + Utils.getInstance().bcdToStr(SN));


                if(SNLen == 4){
                    int firstPart = (SN[2]&0xff);
                    byte temp[] = new byte[] { SN[1], SN[0]};
                    long secondPart  = getValue(temp);

                    activity.showOnUi(firstPart+""+secondPart+"");
                }




            }

        }catch (Exception ex){
            Log.d(TAG, "exception inside handleCard : "+ex.getMessage());
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
//
//    private ProcessCardResult processCardForShiftOrMaintenance(Long cardSerial) {
//        List<Byte> bData = new ArrayList<>();
//        Card card = new Card(ByteUtils.convertByteToPrimitive(bData));
//        currentCardSerial = cardSerial;
//        try {
//            card.setuId(cardSerial);
//            if (lastCardSerial != 0L && card.getuId().equals(lastCardSerial) && Math.abs(Calendar.getInstance().getTime().getTime() - lastTransDate.getTime()) < 5000) {
//                return new ProcessCardResult(card, CardValidationResults.BYPASS_TIME);
//            }
//            byte[] keyData = new byte[6];
//            for (int i = 0; i < 6; i++) {
//                keyData[i] = 0;
//            }
//            key.clear();
//            mainBlockData.clear();
//            log.error("MohsenCard SamDBEfore: ");
//            byte[] tempRes = getSAMDebitKey(ByteUtils.toByteArray(card.getuId(), 4));
//            log.error("MohsenCard SamDAfter: ");
//            if (tempRes == null) {
//                log.error("ReaderHandler > processCardForShiftOrMaintenance(): Could not get Key");
//                ProcessCardResult result = new ProcessCardResult(card, CardValidationResults.PROBLEM_WHILE_VALIDATING_CREW_CARD);
//                return result;
//            }
//            key = ByteUtils.convertPrimitiveToByte(tempRes);
//            if (key.size() != 6) {
//                log.error("ReaderHandler > processCardForShiftOrMaintenance(): Key size error");
//                ProcessCardResult result = new ProcessCardResult(card, CardValidationResults.PROBLEM_WHILE_VALIDATING_CREW_CARD);
//                return result;
//            }
//            for (int i = 0; i < 6; i++) {
//                keyData[i] = key.get(i);
//            }
//            List<Byte> blockData = new ArrayList<>();
//            //check for old card
//            if (mifareAuthenticateWithKey((byte) 1, keyA, 0) != 0) {
//                searchingCard();
//                if (mifareAuthenticateWithKey(Constants.BLK_CardInfo, ByteUtils.convertByteToPrimitive(key), 1) != 0) {
//                    log.error("ReaderHandler > processCardForShiftOrMaintenance(): Auth value v failed");
//                    ProcessCardResult result = new ProcessCardResult(card, CardValidationResults.PROBLEM_WHILE_VALIDATING_CREW_CARD);
//                    return result;
//                }
//                card.setCardVersion(1);
//            } else {
//                //set old version
//                card.setCardVersion(0);
//                ProcessCardResult result = new ProcessCardResult(card, CardValidationResults.NOT_A_CREW_CARD);
//                return result;
//            }
//            if (mifareAuthenticateWithKey(Constants.BLK_CardInfo, ByteUtils.convertByteToPrimitive(key), 1) != 0) {
//                log.error("ReaderHandler > processCardForShiftOrMaintenance(): Auth value v failed");
//                ProcessCardResult result = new ProcessCardResult(card, CardValidationResults.PROBLEM_WHILE_VALIDATING_CREW_CARD);
//                return result;
//            }
//            blockData.clear();
//            blockData = mifareReadBlockNoAuthenticate(Constants.BLK_CardInfo);
//            card.setContractType(blockData.get(0));
//            if (ByteUtils.match(card.getContractType(), driverCardCode)) {
//                //driver
//                if (!OutOfServiceHandler.isThereError() && !activity.getCurrentUiPage().equals(UiCommandTypeEnum.LINE_ADMIN_PAGE)) {
//                    if (!activity.isInitialTaskCheckedShiftStatus()) {
//                        return null;
//                    }
//                    if (StateHandler.getInstance().isShiftOpen()) {
//                        staffID = null;
//                        activity.closeShift(cardSerial, false);
//                    } else {
//                        if (!activity.getCurrentUiPage().equals(UiCommandTypeEnum.LINE_ADMIN_PAGE) &&
//                                !activity.getCurrentUiPage().equals(UiCommandTypeEnum.SUMMARY_REPORT) &&
//                                !activity.getCurrentUiPage().equals(UiCommandTypeEnum.DRIVER_STATS) &&
//                                !activity.getCurrentUiPage().equals(UiCommandTypeEnum.OPERATOR_PAGE) &&
//                                !activity.getCurrentUiPage().equals(UiCommandTypeEnum.MAINTENANCE) &&
//                                !activity.getCurrentUiPage().equals(UiCommandTypeEnum.UPLOAD_DOWNLOAD_PAGE) &&
//                                !activity.getCurrentUiPage().equals(UiCommandTypeEnum.REGISTER_PAGE)) {
//                            //staffID = getStaffID(card);
//                            DriverCardStatus status = isOkToModifyShiftWithThisDriver(cardSerial);
//                            if (status.getStatus().equals(DriverCardStatusEnum.OK)) {
//                                staffID = cardSerial;
//                                String code = String.valueOf(ByteUtils.getValue(ByteUtils.convertByteToPrimitive(getStaffID(card))));
//                                StringBuilder builder = new StringBuilder();
//                                builder.append("آقای " + status.getName() + " " + status.getFamily() + "\n");
//                                builder.append("با شماره پرسنلی " + code + "\n");
//                                builder.append("خوش آمدید");
//                                String welcomeMsg = builder.toString();
//                                //activity.openShift(cardSerial, welcomeMsg, false);
//                                activity.gotoDriverPage(cardSerial, welcomeMsg);
//                            } else {
//                                activity.showOnUi(new ProcessCardResult(null, CardValidationResults.INVALID_DRIVER));
//                            }
//                        }
//                    }
//                }
//                return null;
//            } else if (ByteUtils.match(card.getContractType(), lineAdminCardCode)) {
//                //lineAdmin
//                /*if (!OutOfServiceHandler.isThereError()) {
//                    if (StateHandler.getInstance().isShiftOpen()) {
//                        staffID = null;
//                        activity.closeShift(cardSerial, false, true);
//                    } else {
//                        staffID = cardSerial;
//                        String code = String.valueOf(ByteUtils.getValue(ByteUtils.convertByteToPrimitive(getStaffID(card))));
//                        String welcomeMsg = "مامور محترم به شماره \n"+code+"\n خوش آمدید.";
//                        activity.openShift(cardSerial, welcomeMsg, true);
//                    }
//                }else{
//                    activity.openMaintenance();
//                }*/
//
//                if (!activity.isInitialTaskCheckedShiftStatus()) {
//                    return null;
//                }
//                if (!activity.getCurrentUiPage().equals(UiCommandTypeEnum.DRIVER_STATS)) {
//                    if (activity != null) {
//                        staffID = cardSerial;
//                        String code = String.valueOf(ByteUtils.getValue(ByteUtils.convertByteToPrimitive(getStaffID(card))));
//                        activity.gotoLineAdminPage(cardSerial, code);
//                    }
//                }
//                return null;
//            } else if (ByteUtils.match(card.getContractType(), operatorCardCode)) {
//                //operator
//                if (!activity.isInitialTaskCheckedShiftStatus()) {
//                    return null;
//                }
//                if (!activity.getCurrentUiPage().equals(UiCommandTypeEnum.LINE_ADMIN_PAGE) &&
//                        !activity.getCurrentUiPage().equals(UiCommandTypeEnum.SUMMARY_REPORT) &&
//                        !activity.getCurrentUiPage().equals(UiCommandTypeEnum.DRIVER_STATS)) {
//                    staffID = cardSerial;
//                    String code = String.valueOf(ByteUtils.getValue(ByteUtils.convertByteToPrimitive(getStaffID(card))));
//                    activity.gotoOperatorPage(cardSerial, code);
//                }
//                return null;
//            }
//        } catch (Exception ex) {
//            log.error("ReaderHandler > processCardForShiftOrMaintenance():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        ProcessCardResult result = new ProcessCardResult(card, CardValidationResults.PROBLEM_WHILE_VALIDATING_CREW_CARD);
//        return result;
//    }
//
//    private DriverCardStatus isOkToModifyShiftWithThisDriver(Long cardSerial) {
//        try {
//            if (!ParameterUtils.getInstance().getOperatorsList().isEmpty()) {
//                for (Operator operator : ParameterUtils.getInstance().getOperatorsList()) {
//                    if (cardSerial.equals(operator.getCardSerial())) {
//                        //we have driver card serial in our end
//                        //lets check what is available bus codes
//                        if (operator.getBusList().isEmpty())
//                            return new DriverCardStatus(operator.getName(), operator.getFamily(), DriverCardStatusEnum.FOUND_BUT_FORBIDDEN_FOR_THIS_BUS);
//                        for (Integer code : operator.getBusList()) {
//                            if (StateHandler.getInstance().getBusId() == code.intValue()) {
//                                return new DriverCardStatus(operator.getName(), operator.getFamily(), DriverCardStatusEnum.OK);
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            log.error("ReaderHandler > isOkToModifyShiftWithThisDriver():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return new DriverCardStatus("", "", DriverCardStatusEnum.NOT_FOUND);
//    }
//
//    private Date startOpTime;
//    private Long lastCardSerial = 0L;
//    private Date lastTransDate = Calendar.getInstance().getTime();
//
//    private ProcessCardResult processCard(Long cardSerial) {
//        try {
//            currentCardSerial = cardSerial;
//            startOpTime = new Date();
//            Date _now = Calendar.getInstance().getTime();
//            List<Byte> blockData = new ArrayList<>();
//            Card card = new Card(ByteUtils.convertByteToPrimitive(blockData));
//            card.setuId(cardSerial);
//            Long diff = Math.abs(Calendar.getInstance().getTime().getTime() - lastTransDate.getTime());
//            if (lastCardSerial != 0L) {
//                if (card.getuId().equals(lastCardSerial)) {
//                    if (ParameterUtils.getInstance().getAntiPassBack() != null) {
//                        if (diff < ParameterUtils.getInstance().getAntiPassBack())
//                            return new ProcessCardResult(card, CardValidationResults.BYPASS_TIME);
//                    } else {
//                        if (diff < 5000)
//                            return new ProcessCardResult(card, CardValidationResults.BYPASS_TIME);
//                    }
//                }
//            }
//            if (!readCardData(card)) {
//                return new ProcessCardResult(card, CardValidationResults.READ_CARD_INFO_ERROR);
//            }
//            if (ByteUtils.match(card.getContractType(), driverCardCode)) {
//                //close shift
//                activity.closeShift(cardSerial, false);
//                return null;
//            } else if (ByteUtils.match(card.getContractType(), operatorCardCode)) {
//                //alert, please close shift first.
//                return new ProcessCardResult(card, CardValidationResults.CLOSE_SHIFT_FIRST);
//            } else if (ByteUtils.match(card.getContractType(), lineAdminCardCode)) {
//                //line admin
//                activity.closeShift(cardSerial, false, true);
//                StuffActivityFileManager.getInstance().writeNewFile(cardSerial, true, StuffActivityType.CLOSE_SHIFT);
//                return null;
//            }
//            if (card.getCardVersion() == 0) {
//
//                //return processOldCard(card);
//                return new ProcessCardResult(card, CardValidationResults.OLD_CARD);
//            }
//            card.setQomCardType(QomCardType.NEW);
//            if (card.getLastTransactionInfo().getTotalAmount() > 2000000L) {
//                return new ProcessCardResult(card, CardValidationResults.INSUFFICIENT_BALANCE_CHECK_CARD);
//            }
//            //Long fare = calculateFare(false, card);
//            Long fare = 1L;
//            if (fare == -1L) {
//                return new ProcessCardResult(card, CardValidationResults.CALCULATE_FARE_ERROR);
//            }
//            card.getNewTransactionInfo().setFare(fare);
//            if (card.getLastTransactionInfo().getTotalAmount() < fare) {
//                return new ProcessCardResult(card, CardValidationResults.INSUFFICIENT_BALANCE);
//            }
//            card.getNewTransactionInfo().setTotalAmount(card.getLastTransactionInfo().getTotalAmount() - fare);
//            card.getNewTransactionInfo().setTripCount(0);
//            long cc = ByteUtils.dateTimeValueToLong(_now);
//            card.getNewTransactionInfo().setTransactionDateTime(ByteUtils.dateTimeValueToLong(_now));
//            card.getNewTransactionInfo().setStationId(0);
//            card.getNewTransactionInfo().setSeqNumber(card.getLastTransactionInfo().getSeqNumber() + 1);
//            List<Byte> data = new ArrayList<>();
//            data.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getSeqNumber(), 4));
//            data.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getTotalAmount(), 4));
//            data.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getTripCount(), 2));
//            data.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getTransactionDateTime(), 4));
//            data.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getStationId(), 2));
//            List<Byte> transactionDataEncode = encodeDecodePacket(ByteUtils.convertByteToPrimitive(data), (int) card.getLastTransactionLocation(), card.getuId());
//            byte currentTransactionLocation = 0;
//            if (ByteUtils.match(card.getLastTransactionLocation(), Constants.BLK_Transaction_2_Info)) {
//                currentTransactionLocation = Constants.BLK_Transaction_4_Info;
//                card.setTransactionSectorPointer(Constants.SectorPointer4);
//            } else if (ByteUtils.match(card.getLastTransactionLocation(), Constants.BLK_Transaction_3_Info)) {
//                currentTransactionLocation = Constants.BLK_Transaction_2_Info;
//                card.setTransactionSectorPointer(Constants.SectorPointer2);
//            } else if (ByteUtils.match(card.getLastTransactionLocation(), Constants.BLK_Transaction_4_Info)) {
//                currentTransactionLocation = Constants.BLK_Transaction_3_Info;
//                card.setTransactionSectorPointer(Constants.SectorPointer3);
//            }
//            log.error("MohsenCard SamSignBefore: ");
//            List<Byte> signFromSAM = generateTransactionSignature(card);
//            log.error("MohsenCard SamSignAfter: ");
//            if (signFromSAM == null) {
//                return new ProcessCardResult(card, CardValidationResults.FAIL);
//            }
//            List<Byte> sign = encodeDecodePacket(ByteUtils.convertByteToPrimitive(signFromSAM), currentTransactionLocation + 2, card.getuId());
//            if (sign == null) {
//                log.error("ReaderHandler > processCard(): sign failed");
//                return new ProcessCardResult(card, CardValidationResults.FAIL);
//            }
//            card.getNewTransactionInfo().setSignature(ByteUtils.convertByteToPrimitive(sign));
//            if (mifareAuthenticateWithKey(currentTransactionLocation, ByteUtils.convertByteToPrimitive(key), 1) != 0) {
//                log.error("ReaderHandler > processCard(): Auth value v failed");
//                return new ProcessCardResult(card, CardValidationResults.FAIL);
//            }
//            Boolean state = mifareWriteBlockNoAuthenticate(currentTransactionLocation, transactionDataEncode);
//            if (!state) {
//                log.error("ReaderHandler > processCard(): Write filed");
//                return new ProcessCardResult(card, CardValidationResults.FAIL);
//            }
//            //write signature
//            state = mifareWriteBlockNoAuthenticate((byte) (currentTransactionLocation + 2), sign);
//            if (!state) {
//                log.error("ReaderHandler > processCard(): Write filed");
//                return new ProcessCardResult(card, CardValidationResults.FAIL);
//            }
//            //decrement Counter
//            if (mifareAuthenticateWithKey(Constants.BLK_DecrementalCounter, ByteUtils.convertByteToPrimitive(key), 1) != 0) {
//                log.error("ReaderHandler > processCard(): Auth value v failed(decremental op)");
//                return new ProcessCardResult(card, CardValidationResults.FAIL);
//            }
//            int res = mifareDecrementWithTransfer(Constants.BLK_DecrementalCounter, 1);
//            if (res != 0) {
//                log.error("ReaderHandler > processCard(): decrement op transfer failed");
//                return new ProcessCardResult(card, CardValidationResults.FAIL);
//            }
//            //transaction Sector Pointer
//            if (mifareAuthenticateWithKey(Constants.BLK_TransactionSectorPointer, ByteUtils.convertByteToPrimitive(key), 1) != 0) {
//                log.error("ReaderHandler > processCard(): Auth value v failed(transaction Sector Pointer)");
//                return new ProcessCardResult(card, CardValidationResults.FAIL);
//            }
//            state = mifareWriteBlockNoAuthenticate(Constants.BLK_TransactionSectorPointer, ByteUtils.toByteArray(card.getTransactionSectorPointer(), 2));
//            if (state) {
//                lastCardSerial = card.getuId();
//                lastTransDate = Calendar.getInstance().getTime();
//                log.error("MohsenCardB: " + Math.abs(Calendar.getInstance().getTimeInMillis() - startOpTime.getTime()));
//                log.error("MohsenCard: " + Math.abs(Calendar.getInstance().getTimeInMillis() - startOpTime.getTime()));
//                return new ProcessCardResult(card, CardValidationResults.SUCCESS);
//            }
//        } catch (Exception ex) {
//            log.error("ReaderHandler > processCard():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return null;
//    }
//
//    private Long calculateFare(boolean isOldCard, Card card) {
//        try {
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//            if (StateHandler.getInstance().getLineId() == 0)
//                return -1L;
//            if (ParameterUtils.getInstance().getLineList() == null)
//                return -1L;
//            Line myLine = null;
//            for (Line line : ParameterUtils.getInstance().getLineList()) {
//                if (line.getLineCode().equals(StateHandler.getInstance().getLineId())) {
//                    myLine = line;
//                }
//            }
//            if (myLine == null)
//                return -1L;
//            if (ParameterUtils.getInstance().getLocalCalendarMap().isEmpty())
//                return -1L;
//            LocalCalendarDateType myLocalCalDateType;
//            if (!ParameterUtils.getInstance().getLocalCalendarMap().containsKey(simpleDateFormat.format(Calendar.getInstance().getTime())))
//                myLocalCalDateType = LocalCalendarDateType.WORKDAY;
//            else
//                myLocalCalDateType = ParameterUtils.getInstance().getLocalCalendarMap().get(simpleDateFormat.format(Calendar.getInstance().getTime()));
//            if (ParameterUtils.getInstance().getFareClassList() == null)
//                return -1L;
//            FareClass myFareClass = null;
//            for (FareClass fareClass : ParameterUtils.getInstance().getFareClassList()) {
//                if (fareClass.getCode().equals(myLine.getFareClassCode())) {
//                    myFareClass = fareClass;
//                }
//            }
//            if (myFareClass == null)
//                return -1L;
//            Fare myFare = null;
//            if (isOldCard) {
//                if (!myFareClass.getFareMap().containsKey(232))
//                    return -1L;
//                myFare = myFareClass.getFareMap().get(232);
//            } else {
//                if (!myFareClass.getFareMap().containsKey(ByteUtils.getInt(card.getContractType())))
//                    return -1L;
//                myFare = myFareClass.getFareMap().get(ByteUtils.getInt(card.getContractType()));
//            }
//            DayMoments myDayMoment = isNightMoments();
//            if (myDayMoment.equals(DayMoments.UNKNOWN))
//                return -1L;
//            if (myLocalCalDateType.equals(LocalCalendarDateType.WORKDAY)) {
//                if (isOldCard) {
//                    if (myDayMoment.equals(DayMoments.NIGHT))
//                        return myFare.getNightBaseFare() / 10;
//                    else
//                        return myFare.getBaseFare() / 10;
//                } else {
//                    if (myDayMoment.equals(DayMoments.NIGHT))
//                        return myFare.getNightBaseFare();
//                    else
//                        return myFare.getBaseFare();
//                }
//            } else {
//                if (isOldCard) {
//                    if (myDayMoment.equals(DayMoments.NIGHT))
//                        return myFare.getHolidayNightFare() / 10;
//                    else
//                        return myFare.getHolidayBaseFare() / 10;
//                } else {
//                    if (myDayMoment.equals(DayMoments.NIGHT))
//                        return myFare.getHolidayNightFare();
//                    else
//                        return myFare.getHolidayBaseFare();
//                }
//            }
//        } catch (Exception ex) {
//            log.error("ReaderHandler > calculateFare():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return -1L;
//    }
//
//    private DayMoments isNightMoments() {
//        try {
//            if (ParameterUtils.getInstance().getNightTimeFrom().equals("") || ParameterUtils.getInstance().getNightTimeTo().equals(""))
//                return DayMoments.UNKNOWN;
//            Calendar from = Calendar.getInstance();
//            Calendar to = Calendar.getInstance();
//            Calendar now = Calendar.getInstance();
//            to.add(Calendar.DATE, 1);
//            Integer fromHour = Integer.parseInt(ParameterUtils.getInstance().getNightTimeFrom().split(":")[0]);
//            Integer fromMin = Integer.parseInt(ParameterUtils.getInstance().getNightTimeFrom().split(":")[1]);
//            Integer toHour = Integer.parseInt(ParameterUtils.getInstance().getNightTimeTo().split(":")[0]);
//            Integer toMin = Integer.parseInt(ParameterUtils.getInstance().getNightTimeTo().split(":")[1]);
//            from.set(Calendar.HOUR_OF_DAY, fromHour);
//            from.set(Calendar.MINUTE, fromMin);
//            to.set(Calendar.HOUR_OF_DAY, toHour);
//            to.set(Calendar.MINUTE, toMin);
//            if ((to.getTimeInMillis() > now.getTimeInMillis()) && (now.getTimeInMillis() >= from.getTimeInMillis()))
//                return DayMoments.NIGHT;
//            return DayMoments.DAY;
//        } catch (Exception ex) {
//            log.error("ReaderHandler > isNightMoments():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return DayMoments.UNKNOWN;
//    }
//
//    private List<Byte> buildTransactionLog(Card card) {
//        List<Byte> res = new ArrayList<>();
//        try {
//            res.add((byte) 'V');
//            res.add((byte) 'A');
//            log.error("mohDate:diff " + ByteUtils.dateTimeValueToLong(Calendar.getInstance().getTime()));
//            log.error("mohDate:mill " + Calendar.getInstance().getTime().getTime());
//            log.error("mohDate:time " + Calendar.getInstance().getTime());
//            List<Byte> innerRes = new ArrayList<>();
//            innerRes.addAll(ByteUtils.toByteArray(ByteUtils.dateTimeValueToLong(Calendar.getInstance().getTime()), 4));
//            //card serial
//            innerRes.add(card.getExtraUId());
//            innerRes.addAll(ByteUtils.toByteArray(card.getuId(), 4));
//            //ticketType
//            if (card.getCardVersion() == 1) {
//                //new card
//                innerRes.add((byte) 0x01);
//                //payment type
//                innerRes.add((byte) 0x00); //0: offline, 1: online, 2:wallet
//                //transaction amount
//                innerRes.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getFare(), 4));
//                //card transaction counter
//                innerRes.addAll(ByteUtils.toByteArray(StateHandler.getInstance().getValidationCount(), 4));
//                //card transaction counter
//                innerRes.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getSeqNumber(), 4));
//                //last balance
//                innerRes.addAll(ByteUtils.toByteArray(card.getLastTransactionInfo().getTotalAmount(), 4));
//                //new balance
//                innerRes.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getTotalAmount(), 4));
//                //deposit value
//                innerRes.addAll(ByteUtils.toByteArray(card.getDepositValue(), 4));
//                //contract type
//                innerRes.add(card.getContractType());
//                //tag type
//                innerRes.addAll(ByteUtils.toByteArray(card.getTagType(), 2));
//                //trip count
//                innerRes.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getTripCount(), 2));
//                //station ID
//                innerRes.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getStationId(), 2));
//                //last station ID
//                innerRes.addAll(ByteUtils.toByteArray(card.getLastTransactionInfo().getStationId(), 2));
//                //discount percent
//                innerRes.add(card.getNewTransactionInfo().getDiscountInfo().getDiscountPercent().byteValue());
//                //discount type
//                innerRes.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getDiscountInfo().getTransactionType(), 2));
//                //discount trip count
//                innerRes.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getDiscountInfo().getDiscountTripCount(), 2));
//                //discount amount
//                innerRes.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getDiscountInfo().getDiscountAmount(), 2));
//                //discount initial date
//                innerRes.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getDiscountInfo().getDiscountInitDate(), 2));
//                //discount expire date
//                innerRes.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getDiscountInfo().getDiscountExpDate(), 2));
//                //direction
//                innerRes.add(card.getNewTransactionInfo().getDirection());
//                //service code 1
//                innerRes.add(card.getNewTransactionInfo().getDiscountInfo().getSrvCode1());
//                //service code 2
//                innerRes.add(card.getNewTransactionInfo().getDiscountInfo().getSrvCode2());
//                //service code 3
//                innerRes.add(card.getNewTransactionInfo().getDiscountInfo().getSrvCode3());
//                //service code 4
//                innerRes.add(card.getNewTransactionInfo().getDiscountInfo().getSrvCode4());
//                //service code 5
//                innerRes.add(card.getNewTransactionInfo().getDiscountInfo().getSrvCode5());
//                //signature
//                innerRes.addAll(ByteUtils.convertPrimitiveToByte(card.getNewTransactionInfo().getSignature()));
//                //latitude
//                innerRes.addAll(ByteUtils.convertPrimitiveToByte(ByteUtils.toByteArray(LocationHandler.getInstance().getCurrentLat(), 7)));
//                //longitude
//                innerRes.addAll(ByteUtils.convertPrimitiveToByte(ByteUtils.toByteArray(LocationHandler.getInstance().getCurrentLon(), 7)));
//            } else {
//                //old card
//                innerRes.add((byte) 0x02);
//                //payment type
//                innerRes.add((byte) 0x00); //0: offline, 1: online, 2:wallet
//                //transaction amount
//                innerRes.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getFare() * 10, 4));
//                //card transaction counter
//                innerRes.addAll(ByteUtils.toByteArray(StateHandler.getInstance().getValidationCount(), 4));
//                //card transaction counter
//                innerRes.addAll(ByteUtils.convertPrimitiveToByte(new byte[4]));
//                //last balance
//                innerRes.addAll(ByteUtils.toByteArray(card.getLastTransactionInfo().getTotalAmount() * 10, 4));
//                //new balance
//                innerRes.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getTotalAmount() * 10, 4));
//                //deposit value
//                innerRes.addAll(ByteUtils.toByteArray(0L, 4));
//                //contract type
//                innerRes.add((byte) 0);
//                //tag type
//                innerRes.addAll(ByteUtils.toByteArray(0, 2));
//                //trip count
//                innerRes.addAll(ByteUtils.toByteArray(0, 2));
//                //station ID
//                innerRes.addAll(ByteUtils.toByteArray(0, 2));
//                //last station ID
//                innerRes.addAll(ByteUtils.toByteArray(0, 2));
//                //discount percent
//                innerRes.add((byte) 0);
//                //discount type
//                innerRes.addAll(ByteUtils.toByteArray(0L, 2));
//                //discount trip count
//                innerRes.addAll(ByteUtils.toByteArray(0L, 2));
//                //discount amount
//                innerRes.addAll(ByteUtils.toByteArray(0L, 2));
//                //discount initial date
//                innerRes.addAll(ByteUtils.toByteArray(0, 2));
//                //discount expire date
//                innerRes.addAll(ByteUtils.toByteArray(0, 2));
//                //direction
//                innerRes.add((byte) 0);
//                //service code 1
//                innerRes.add((byte) 0);
//                //service code 2
//                innerRes.add((byte) 0);
//                //service code 3
//                innerRes.add((byte) 0);
//                //service code 4
//                innerRes.add((byte) 0);
//                //service code 5
//                innerRes.add((byte) 0);
//                //signature
//                innerRes.addAll(ByteUtils.convertPrimitiveToByte(new byte[16]));
//                //latitude
//                innerRes.addAll(ByteUtils.convertPrimitiveToByte(ByteUtils.toByteArray(LocationHandler.getInstance().getCurrentLat(), 7)));
//                //longitude
//                innerRes.addAll(ByteUtils.convertPrimitiveToByte(ByteUtils.toByteArray(LocationHandler.getInstance().getCurrentLon(), 7)));
//            }
//            res.add((byte) 0x00);
//            res.add((byte) innerRes.size());
//            res.addAll(innerRes);
//        } catch (Exception ex) {
//            log.error("ReaderHandler > buildTransactionLog():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return res;
//    }
//
//    private ProcessCardResult processOldCard(Card card) {
//        try {
//            card.setQomCardType(QomCardType.OLD);
//            //Long fare = calculateFare(true, card);
//            Long fare = 1L;
//            if (fare == -1L) {
//                return new ProcessCardResult(card, CardValidationResults.CALCULATE_FARE_ERROR);
//            }
//            if (mifareAuthenticateWithKey((byte) 20, keyB, 1) != 0) {
//                log.error("ReaderHandler > processOldCard(): Auth value v failed");
//                return new ProcessCardResult(card, CardValidationResults.FAIL);
//            }
//            List<Byte> res = mifareReadBlockNoAuthenticate((byte) 20);
//            if (res == null) {
//                log.error("ReaderHandler > processOldCard(): Block data is NULL");
//                return new ProcessCardResult(card, CardValidationResults.FAIL);
//            }
//            card.getLastTransactionInfo().setTotalAmount(ByteUtils.getValueLSB(res.subList(0, 4)));
//            if (card.getLastTransactionInfo().getTotalAmount() > 20000L) {
//                return new ProcessCardResult(card, CardValidationResults.INSUFFICIENT_BALANCE_CHECK_CARD);
//            }
//            card.getNewTransactionInfo().setTotalAmount(card.getLastTransactionInfo().getTotalAmount() - fare);
//            card.getNewTransactionInfo().setFare(fare);
//            if (card.getLastTransactionInfo().getTotalAmount() < fare) {
//                return new ProcessCardResult(card, CardValidationResults.INSUFFICIENT_BALANCE);
//            }
//            Integer s = mifareDecrementWithTransfer((byte) 20, fare.intValue());
//            if (s != 0) {
//                log.error("ReaderHandler > processOldCard(): decrement op transfer failed");
//                return new ProcessCardResult(card, CardValidationResults.FAIL);
//            }
//            lastCardSerial = card.getuId();
//            lastTransDate = Calendar.getInstance().getTime();
//            Log.e("MOHSEN", "t: " + (lastTransDate.getTime() - startOpTime.getTime()));
//            return new ProcessCardResult(card, CardValidationResults.SUCCESS);
//        } catch (Exception ex) {
//            log.error("ReaderHandler > processOldCard():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return null;
//    }
//
//    private List<Byte> generateTransactionSignature(Card card) {
//        try {
//            List<Byte> signatureData = new ArrayList<>();
//            //lastTransactionInfo
//            signatureData.addAll(ByteUtils.toByteArray(card.getLastTransactionInfo().getDiscountInfo().getDiscountAmount(), 4).subList(0, 2));
//            signatureData.addAll(ByteUtils.toByteArray(card.getLastTransactionInfo().getTripCount(), 2));
//            signatureData.addAll(ByteUtils.toByteArray(card.getLastTransactionInfo().getTotalAmount(), 4));
//            signatureData.addAll(ByteUtils.toByteArray(card.getLastTransactionInfo().getDiscountInfo().getDiscountTripCount(), 2));
//            signatureData.addAll(ByteUtils.toByteArray(card.getLastTransactionInfo().getDiscountInfo().getDiscountExpDate(), 2));
//            signatureData.addAll(ByteUtils.toByteArray(card.getLastTransactionInfo().getTransactionDateTime(), 4));
//            //lastTransactionInfo signature
//            signatureData.addAll(ByteUtils.convertPrimitiveToByte(card.getLastTransactionInfo().getSignature()));
//            //newTransactionInfo
//            signatureData.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getDiscountInfo().getDiscountAmount(), 4).subList(0, 2));
//            signatureData.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getTripCount(), 2));
//            signatureData.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getTotalAmount(), 4));
//            signatureData.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getDiscountInfo().getDiscountTripCount(), 2));
//            signatureData.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getDiscountInfo().getDiscountExpDate(), 2));
//            signatureData.addAll(ByteUtils.toByteArray(card.getNewTransactionInfo().getTransactionDateTime(), 4));
//            //cardUID
//            signatureData.addAll(ByteUtils.toByteArray(card.getuId(), 4));
//            //decrementalCounter
//            signatureData.addAll(ByteUtils.toByteArray(card.getDecrementalCounter(), 4));
//            //contractType
//            signatureData.add(card.getContractType());
//            List<Byte> sign = generateTransactionSignatureBySAM(signatureData);
//            if (sign == null)
//                return null;
//            return sign.subList(0, 16);
//        } catch (Exception ex) {
//            log.error("ReaderHandler > generateTransactionSignature():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return null;
//    }
//
//    private Boolean readCardData(Card card) {
//        try {
//            List<Byte> bData = new ArrayList<>();
//            byte[] keyData = new byte[6];
//            for (int i = 0; i < 6; i++) {
//                keyData[i] = 0;
//            }
//            key.clear();
//            mainBlockData.clear();
//            log.error("MohsenCard SamDBefore: ");
//            byte[] tempRes = getSAMDebitKey(ByteUtils.toByteArray(card.getuId(), 4));
//            log.error("MohsenCard SamDAfter: ");
//            if (tempRes == null) {
//                log.error("ReaderHandler > readCardData(): Could not get Key");
//                return false;
//            }
//            key = ByteUtils.convertPrimitiveToByte(tempRes);
//            if (key.size() != 6) {
//                log.error("ReaderHandler > readCardData(): Key size error");
//                return false;
//            }
//            for (int i = 0; i < 6; i++) {
//                keyData[i] = key.get(i);
//            }
//            List<Byte> blockData = new ArrayList<>();
//            //check for old card
//            if (mifareAuthenticateWithKey((byte) 1, keyA, 0) != 0) {
//                searchingCard();
//                if (mifareAuthenticateWithKey(Constants.BLK_CardInfo, ByteUtils.convertByteToPrimitive(key), 1) != 0) {
//                    log.error("ReaderHandler > readCardData(): Auth value v failed");
//                    return false;
//                }
//                card.setCardVersion(1);
//            } else {
//                //set old version
//                card.setCardVersion(0);
//            }
//            if (card.getCardVersion() == 0) {
//                blockData = mifareReadBlockNoAuthenticate((byte) 1);
//                if (blockData == null) {
//                    log.error("ReaderHandler > readCardData(): Block data is NULL(old version)");
//                    return false;
//                }
//                card.setTagType((int) ByteUtils.getValue(blockData, 0, 2));
//                //card.setuId(ByteUtils.getValue(blockData, 2, 4));
//                return true;
//            } else {
//                if (mifareAuthenticateWithKey(Constants.BLK_CardInfo, ByteUtils.convertByteToPrimitive(key), 1) != 0) {
//                    log.error("ReaderHandler > readCardData(): Auth value v failed");
//                    return false;
//                }
//                blockData.clear();
//                blockData = mifareReadBlockNoAuthenticate(Constants.BLK_CardInfo);
//                if (blockData == null) {
//                    log.error("ReaderHandler > readCardData(): Block data is NULL");
//                    return false;
//                }
//                mainBlockData = blockData;
//                card.setContractType(blockData.get(0));
//                log.error("MohsenCard: ct: " + ByteUtils.getInt(card.getContractType()));
//                card.setExactCT(blockData.get(1));
//                card.setExtraUId(blockData.get(6));
//                card.setTagType((int) ByteUtils.getValue(ByteUtils.convertByteToPrimitive(blockData), 7, 2));
//                card.setDepositValue(ByteUtils.getValue(ByteUtils.convertByteToPrimitive(blockData), 9, 4));
//                card.setValidation(blockData.get(13));
//                card.setCity((int) ByteUtils.getValue(ByteUtils.convertByteToPrimitive(blockData), 7, 2));
//                if (mifareAuthenticateWithKey(Constants.BLK_TransactionSectorPointer, ByteUtils.convertByteToPrimitive(key), 1) != 0) {
//                    log.error("ReaderHandler > readCardData(): Auth value v failed(sector point)");
//                    return false;
//                }
//                blockData.clear();
//                blockData = mifareReadBlockNoAuthenticate(Constants.BLK_TransactionSectorPointer);
//                if (blockData == null) {
//                    log.error("ReaderHandler > readCardData(): Block data is NULL(sector point)");
//                    return false;
//                }
//                card.setTransactionSectorPointer(ByteUtils.getValue(blockData, 0, 2));
//                if (mifareAuthenticateWithKey(Constants.BLK_DecrementalCounter, ByteUtils.convertByteToPrimitive(key), 1) != 0) {
//                    log.error("ReaderHandler > readCardData(): Auth value v failed(decremental counter)");
//                    return false;
//                }
//                blockData.clear();
//                blockData = mifareReadBlockNoAuthenticate(Constants.BLK_DecrementalCounter);
//                if (blockData == null) {
//                    log.error("ReaderHandler > readCardData(): Block data is NULL(decremental counter)");
//                    return false;
//                }
//                card.setDecrementalCounter(ByteUtils.getValueLSB(blockData.subList(0, 4)));
//                if (card.getDecrementalCounter() % 3 == 0)
//                    card.setLastTransactionLocation(Constants.BLK_Transaction_2_Info);
//                else if (card.getDecrementalCounter() % 3 == 1)
//                    card.setLastTransactionLocation(Constants.BLK_Transaction_3_Info);
//                else if (card.getDecrementalCounter() % 3 == 2)
//                    card.setLastTransactionLocation(Constants.BLK_Transaction_4_Info);
//                if (mifareAuthenticateWithKey(card.getLastTransactionLocation(), ByteUtils.convertByteToPrimitive(key), 1) != 0) {
//                    log.error("ReaderHandler > readCardData(): Auth value v failed(last transaction location)");
//                    return false;
//                }
//                blockData.clear();
//                blockData = mifareReadBlockNoAuthenticate(card.getLastTransactionLocation());
//                if (blockData == null) {
//                    log.error("ReaderHandler > readCardData(): Block data is NULL(last transaction location)");
//                    return false;
//                }
//                List<Byte> data = encodeDecodePacket(ByteUtils.convertByteToPrimitive(blockData), (int) card.getLastTransactionLocation(), card.getuId());
//                card.getLastTransactionInfo().setSeqNumber(ByteUtils.getValue(data.subList(0, 4)));
//                card.getLastTransactionInfo().setTotalAmount(ByteUtils.getValue(data.subList(4, 8)));
//                card.getLastTransactionInfo().setTripCount((int) ByteUtils.getValue(data.subList(8, 10)));
//                card.getLastTransactionInfo().setTransactionDateTime(ByteUtils.getValue(data.subList(10, 14)));
//                card.getLastTransactionInfo().setStationId((int) ByteUtils.getValue(data.subList(14, 16)));
//                if (mifareAuthenticateWithKey((byte) (card.getLastTransactionLocation() + 1), ByteUtils.convertByteToPrimitive(key), 1) != 0) {
//                    log.error("ReaderHandler > readCardData(): Auth value v failed(discount)");
//                    return false;
//                }
//                blockData.clear();
//                data.clear();
//                blockData = mifareReadBlockNoAuthenticate((byte) (card.getLastTransactionLocation() + 1));
//                if (blockData == null) {
//                    log.error("ReaderHandler > readCardData(): Block data is NULL(discount)");
//                    return false;
//                }
//                data = encodeDecodePacket(ByteUtils.convertByteToPrimitive(blockData), card.getLastTransactionLocation() + 1, card.getuId());
//                card.getLastTransactionInfo().getDiscountInfo().setDiscountTripCount((int) ByteUtils.getValue(data.subList(0, 2)));
//                card.getLastTransactionInfo().getDiscountInfo().setDiscountAmount(ByteUtils.getValue(data.subList(2, 4)));
//                if (ByteUtils.getValue(data.subList(6, 8)) > 0) {
//                    card.getLastTransactionInfo().getDiscountInfo().setDiscountInitDate((int) ByteUtils.getValue(data.subList(6, 8)));
//                    card.getLastTransactionInfo().getDiscountInfo().setDiscountExpDate((int) ByteUtils.getValue(data.subList(8, 10)));
//                }
//                card.getLastTransactionInfo().getDiscountInfo().setSrvCode1(data.get(10));
//                card.getLastTransactionInfo().getDiscountInfo().setSrvCode2(data.get(11));
//                card.getLastTransactionInfo().getDiscountInfo().setSrvCode3(data.get(12));
//                card.getLastTransactionInfo().getDiscountInfo().setSrvCode4(data.get(13));
//                card.getLastTransactionInfo().getDiscountInfo().setSrvCode5(data.get(14));
//                card.getLastTransactionInfo().getDiscountInfo().setTransactionType(data.get(15));
//                if (mifareAuthenticateWithKey((byte) (card.getLastTransactionLocation() + 2), ByteUtils.convertByteToPrimitive(key), 1) != 0) {
//                    log.error("ReaderHandler > readCardData(): Auth value v failed(signature)");
//                    return false;
//                }
//                blockData.clear();
//                data.clear();
//                blockData = mifareReadBlockNoAuthenticate((byte) (card.getLastTransactionLocation() + 2));
//                if (blockData == null) {
//                    log.error("ReaderHandler > readCardData(): Block data is NULL(signature)");
//                    return false;
//                }
//                data = encodeDecodePacket(ByteUtils.convertByteToPrimitive(blockData), card.getLastTransactionLocation() + 2, card.getuId());
//                card.getLastTransactionInfo().setSignature(ByteUtils.convertByteToPrimitive(data));
//                return true;
//            }
//        } catch (Exception ex) {
//            log.error("ReaderHandler > readCardData():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return false;
//    }
//
//    private List<Byte> getStaffID(Card card) {
//        List<Byte> blockData = new ArrayList<>();
//        try {
//            if (mifareAuthenticateWithKey(Constants.BLK_Personal_Info, ByteUtils.convertByteToPrimitive(key), 1) != 0) {
//                log.error("ReaderHandler > readCardData(): Auth value v failed(signature)");
//                return new ArrayList<>();
//            }
//            blockData.clear();
//            blockData = mifareReadBlockNoAuthenticate(Constants.BLK_Personal_Info);
//            if (blockData == null) {
//                log.error("ReaderHandler > readCardData(): Block data is NULL(last transaction location)");
//                return new ArrayList<>();
//            }
//            return blockData.subList(8, 15);
//        } catch (Exception ex) {
//            log.error("ReaderHandler > getPersonalInfo():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return new ArrayList<>();
//    }
//
//    private List<Byte> encodeDecodePacket(byte[] dataIn, Integer blockNo, Long serial) {
//        try {
//            byte[] fixedBytes = new byte[5];
//            byte counterA = 0;
//            byte counterB = 0;
//            byte[] dataOut = new byte[16];
//            byte[] uId = ByteUtils.convertByteToPrimitive(ByteUtils.toByteArray(serial, 4));
//            int numberBlockInSector = 0;
//            if (blockNo == 8 || blockNo == 12 || blockNo == 16)
//                numberBlockInSector = 0;
//            else if (blockNo == 9 || blockNo == 13 || blockNo == 17)
//                numberBlockInSector = 1;
//            else if (blockNo == 10 || blockNo == 14 || blockNo == 18)
//                numberBlockInSector = 2;
//            if (numberBlockInSector == 0)
//                fixedBytes = ByteUtils.HexToByteArr("04080A0E0E");
//            else
//                fixedBytes = ByteUtils.HexToByteArr("0206080C0C");
//            for (counterA = 0; counterA < 16; counterA++) {
//                if (ByteUtils.match(counterA, fixedBytes[counterB]) && counterB <= 3) {
//                    dataOut[counterA] = dataIn[counterA];
//                    counterB++;
//                } else {
//                    dataOut[counterA] = (byte) (uId[counterA % 4] ^ dataIn[counterA] ^ dataIn[fixedBytes[counterB]]);
//                }
//            }
//            return ByteUtils.convertPrimitiveToByte(dataOut);
//        } catch (Exception ex) {
//            log.error("ReaderHandler > encodeDecodePacket():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return null;
//    }
//
//    private Integer mifareDecrementWithTransfer(byte blockNo, int value) {
//        try {
//            long temp = value;
//            MIFARE_DECREMENT_TRANSFER[4] = blockNo;
//            MIFARE_DECREMENT_TRANSFER[9] = blockNo;
//            for (int i = 0; i < 4; i++) {
//                MIFARE_DECREMENT_TRANSFER[5 + i] = (byte) (temp % 256);
//                temp /= 256;
//            }
//            byte[] res = executeCMDonReader(MIFARE_DECREMENT_TRANSFER);
//            if (res == null)
//                return -1;
//            return (int) res[0];
//        } catch (Exception ex) {
//            log.error("ReaderHandler > mifareDecrementWithTransfer():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return -1;
//    }
//
//    private Integer mifareAuthenticateWithKey(byte blockNo, byte[] key, int keyType) {
//        try {
//            Authentication_Key_CMD[11] = blockNo;
//            Authentication_Key_CMD[4] = (byte) ((keyType == 1) ? 0x04 : 0x00);
//            for (int k = 0; k < 6; k++)
//                Authentication_Key_CMD[k + 5] = key[k];
//            byte[] res = executeCMDonReader(Authentication_Key_CMD);
//            if (res == null)
//                return -2;
//            if (ByteUtils.match(res[0], 0)) {
//                return 0;
//            } else
//                return -1;
//        } catch (Exception ex) {
//            log.error("ReaderHandler > mifareAuthenticateWithKey():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return -1;
//    }
//
//    private List<Byte> mifareReadBlockNoAuthenticate(byte block) {
//        try {
//            ReadBlock_CMD[4] = block;
//            byte[] res = executeCMDonReader(ReadBlock_CMD);
//            if (res != null) {
//                if (res[0] == 0) {
//                    return ByteUtils.convertPrimitiveToByte(res).subList(1, 17);
//                }
//            }
//        } catch (Exception ex) {
//            log.error("ReaderHandler > mifareReadBlockNoAuthenticate():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return null;
//    }
//
//    private Boolean mifareWriteBlockNoAuthenticate(byte block, List<Byte> data) {
//        try {
//            WriteBlock_CMD[4] = block;
//            for (int k = 0; k <= (data.size() - 1); k++)
//                WriteBlock_CMD[k + 5] = data.get(k);
//            byte[] res = executeCMDonReader(WriteBlock_CMD);
//            if (res != null) {
//                if (res[0] == 0) {
//                    return true;
//                }
//            }
//        } catch (Exception ex) {
//            log.error("ReaderHandler > mifareWriteBlockNoAuthenticate():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return false;
//    }
//
//    private byte[] getSAMDebitKey(List<Byte> cardSerial) {
//        try {
//            List<Byte> APDU = new ArrayList<>();
//            APDU.add((byte) 0x91);
//            APDU.add((byte) 0x10);
//            APDU.add((byte) 0x00);
//            APDU.add((byte) 0x00);
//            APDU.add((byte) 0x04);
//            APDU.addAll(cardSerial);
//            APDU.add((byte) 0x06);
//            //byte[] data = generateAPDU(0x38, samSlot, ByteUtils.convertByteToPrimitive(APDU));
//            //if (data != null){
//            ComBean comBean = samTransfer(ByteUtils.convertPrimitiveToByte(APDU), samSlot);
//            if (comBean != null) {
//                List<Byte> finalRes = ByteUtils.convertPrimitiveToByte(comBean.bRec);
//                List<Byte> completeRes = finalRes.subList(5, finalRes.size() - 1);
//                if (completeRes.size() != 8 || !ByteUtils.match(completeRes.get(6), 0x90) || !ByteUtils.match(completeRes.get(7), 0x00))
//                    return null;
//                return ByteUtils.convertByteToPrimitive(completeRes.subList(0, 6));
//            }
//            // }
//        } catch (Exception ex) {
//            log.error("ReaderHandler > getSAMDebitKey():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return null;
//    }
//
//    public class SerialPortWrapper extends SerialHelper {
//
//        public SerialPortWrapper() {
//        }
//
//        @Override
//        protected void onDataReceived(ComBean ComRecData) {
//        }
//    }
//
//    private byte[] generateAPDU(int header, boolean isPPS, int samSlot, byte[] data) {
//        try {
//            if (header == 0x37) {
//                if (isPPS) {
//                    //pps cmd
//                    List<Byte> res = new ArrayList<>();
//                    res.add((byte) 0xAA);
//                    res.add((byte) 0x66);
//                    res.add((byte) 0x00);
//                    res.add((byte) (7));
//                    res.add((byte) header);
//                    res.addAll(ByteUtils.convertPrimitiveToByte(data));
//                    int innerRes = 0;
//                    for (byte b : res) {
//                        innerRes += b;
//                    }
//                    innerRes = innerRes - 0xAA - 0X66;
//                    byte check = (byte) (innerRes % 256);
//                    res.add(check);
//                    return ByteUtils.convertByteToPrimitive(res);
//                }
//                //reset cmd
//                byte[] res = new byte[7];
//                res[0] = (byte) 0xAA;
//                res[1] = 0x66;
//                res[2] = 0x00;
//                res[3] = (byte) (4);
//                res[4] = (byte) header;
//                if (samSlot == 0) {
//                    res[5] = 0x00;
//                    res[6] = (byte) 0x3B;
//                } else if (samSlot == 1) {
//                    res[5] = 0x10;
//                    res[6] = (byte) 0x4B;
//                } else if (samSlot == 2) {
//                    res[5] = 0x20;
//                    res[6] = (byte) 0x5B;
//                } else if (samSlot == 3) {
//                    res[5] = 0x30;
//                    res[6] = (byte) 0x6B;
//                }
//                return res;
//            } else if (header == 0x15) {
//                List<Byte> res = new ArrayList<>();
//                res.add((byte) 0xAA);
//                res.add((byte) 0x66);
//                res.add((byte) 0x00);
//                res.add((byte) (4));
//                res.add((byte) header);
//                res.addAll(ByteUtils.convertPrimitiveToByte(data));
//                int innerRes = 0;
//                for (byte b : res) {
//                    innerRes += b;
//                }
//                innerRes = innerRes - 0xAA - 0X66;
//                byte check = (byte) (innerRes % 256);
//                res.add(check);
//                return ByteUtils.convertByteToPrimitive(res);
//            } else {
//                List<Byte> res = new ArrayList<>();
//                res.add((byte) 0xAA);
//                res.add((byte) 0x66);
//                res.addAll(ByteUtils.toByteArray(4 + data.length, 2));
//                res.add((byte) header);
//                res.add((byte) samSlot);
//                res.addAll(ByteUtils.convertPrimitiveToByte(data));
//                int innerRes = 0;
//                for (byte b : res) {
//                    innerRes += b;
//                }
//                innerRes = innerRes - 0xAA - 0X66;
//                byte check = (byte) (innerRes % 256);
//                res.add(check);
//                return ByteUtils.convertByteToPrimitive(res);
//            }
//        } catch (Exception ex) {
//            log.error("ReaderHandler > generateAPDU():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return null;
//    }
//
//    private byte[] generateCMDForReader(byte[] data) {
//        try {
//            data[data.length - 1] = 0;
//            for (int i = 1; i < data.length - 1; i++)
//                data[data.length - 1] = (byte) ((int) data[data.length - 1] ^ (int) data[i]);
//        } catch (Exception ex) {
//            log.error("ReaderHandler > generateCMDForReader():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return data;
//    }
//
//    private byte[] executeCMDonReader(byte[] cmd) {
//        try {
//            readerSerialPortWrapper.send(generateCMDForReader(cmd));
//            byte[] res = readerSerialPortWrapper.readReader();
//            return res;
//        } catch (Exception ex) {
//            log.error("ReaderHandler > executeCMDonReader():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return null;
//    }
//
//    private FindCardResult searchingCard() {
//        FindCardResult result = new FindCardResult();
//        try {
//            byte[] res = executeCMDonReader(RF_OFF);
//            res = executeCMDonReader(REQA);
//            if (res == null) {
//                return result;
//            }
//            if (ByteUtils.match(res[0], 0x00) && (res.length == 4)) {
//                if ((ByteUtils.match(res[1], 0x04) && ByteUtils.match(res[2], 0x00)) || (ByteUtils.match(res[1], 0x02) && ByteUtils.match(res[2], 0x00))) {
//                    byte[] innerRes = antiCollide();
//                    if (innerRes == null) {
//                        return result;
//                    }
//                    if (innerRes.length >= 4) {
//                        byte sak = selectCard(innerRes);
//                        if (ByteUtils.match(sak, 0x08) || ByteUtils.match(sak, 0x38) || ByteUtils.match(sak, 0x18)) {
//                            List<Byte> sn = new ArrayList<>();
//                            for (int i = 3; i >= 0; i--) {
//                                sn.add(innerRes[i]);
//                            }
//                            result.setCardType(CardType.Mifare_1K);
//                            result.setCardSerialNumber(ByteUtils.getValue(sn));
//                            result.setCardId(innerRes);
//                            return result;
//                        }
//                    }
//                } else if (ByteUtils.match(res[1], 0x44) && ByteUtils.match(res[2], 0x03)) {
//                    //Desfire Detected
//                    byte[] innerRes = executeCMDonReader(F_Card);
//                    if (innerRes == null)
//                        return result;
//                    if (innerRes.length == 17) {
//                        result.setCardType(CardType.DESFire);
//                        return result;
//                    } else if (ByteUtils.match(innerRes[0], 2)) {
//                        return result;
//                    }
//                }
//            } else if (ByteUtils.match(res[0], 2)) {
//                return result;
//            }
//        } catch (Exception ex) {
//            log.error("ReaderHandler > searchingCard():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return result;
//    }
//
//    private byte selectCard(byte[] cardSerialNumber) {
//        try {
//            for (int i = 0; i < 4; i++) {
//                Select_CMD[4 + i] = cardSerialNumber[i];
//            }
//            byte[] res = executeCMDonReader(Select_CMD);
//            if (res != null) {
//                if (ByteUtils.match(res[0], 0)) {
//                    return res[1];
//                }
//            }
//        } catch (Exception ex) {
//            log.error("ReaderHandler > selectCard():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return 0x00;
//    }
//
//    private byte[] antiCollide() {
//        try {
//            byte[] cardSerialNumber = new byte[4];
//            byte[] res = executeCMDonReader(Anti_Collision_CMD);
//            if (res == null)
//                return null;
//            if (res != null && ByteUtils.match(res[0], 0) && res.length == 6) {
//                for (int i = 1; i < 5; i++) {
//                    cardSerialNumber[i - 1] = res[i];
//                }
//                return cardSerialNumber;
//            }
//        } catch (Exception ex) {
//            log.error("ReaderHandler > antiCollide():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return null;
//    }
//
//    private long getSerial(List<Byte> data) {
//        if (data != null && data.size() > 10)
//            return ByteUtils.getValue(ByteUtils.reverse(data), 1, 4);
//        return -1;
//    }
//
//    private List<Byte> generateTransactionSignatureBySAM(List<Byte> inData) {
//        try {
//            List<Byte> APDU = new ArrayList<>();
//            APDU.add((byte) 0x91);
//            APDU.add((byte) 0x12);
//            APDU.add((byte) 0x02);
//            APDU.add((byte) 0x02);
//            APDU.add((byte) 0x39);
//            APDU.addAll(inData);
//            APDU.add((byte) 0x13);
//            //byte[] data = generateAPDU(0x38, samSlot, ByteUtils.convertByteToPrimitive(APDU));
//            //if (data != null) {
//            ComBean comBean = samTransfer(ByteUtils.convertPrimitiveToByte(APDU), samSlot);
//            if (comBean != null) {
//                List<Byte> finalRes = ByteUtils.convertPrimitiveToByte(comBean.bRec);
//                List<Byte> completeRes = finalRes.subList(5, finalRes.size() - 1);
//                if (!ByteUtils.match(completeRes.get(completeRes.size() - 2), 0x90) || !ByteUtils.match(completeRes.get(completeRes.size() - 1), 0x00))
//                    return null;
//                return completeRes.subList(0, 19);
//            }
//            //}
//        } catch (Exception ex) {
//            log.error("ReaderHandler > generateTransactionSignatureBySAM():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return null;
//    }
//
//    private Boolean findSlot() {
//        try {
//            Boolean samAvailable_ = false;
//            byte[] SAM_ATR;
//            byte[] data2 = generateAPDU(0x15, false, 0, new byte[]{0x09});
//            samSerialPortWrapper.send(wrapAA00(data2));
//            ComBean comBean2 = samSerialPortWrapper.read();
//            if (comBean2 != null) {
//                if (ByteUtils.match(comBean2.bRec[4], 0x15)) {
//                    samSerialPortWrapper.close();
//                    samSerialPortWrapper.setBaudRate(Constants.samBaudRate);
//                    samSerialPortWrapper.open();
//                }
//            }
//            for (int i = 0; i < 5; i++) {
//                if (!samSerialPortWrapper.isOpen())
//                    return false;
//                byte[] data = generateAPDU(0x37, false, i, null);
//                samSerialPortWrapper.send(wrapAA00(data));
//                ComBean comBean = samSerialPortWrapper.read();
//                if (comBean != null) {
//                    if (comBean.bRec.length > 5) {
//                        if (ByteUtils.match(comBean.bRec[4], 0x37)) {
//                            samSlot = i;
//                            samAvailable_ = true;
//                        }
//                    }
//                }
//            }
//            return samAvailable_;
//        } catch (Exception ex) {
//            log.error("ReaderHandler > findSlot():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return false;
//    }
//
//    private Boolean samSelectApp() {
//        try {
//            List<Byte> APDU = new ArrayList<>();
//            APDU.add((byte) 0x01);
//            APDU.add((byte) 0xA4);
//            APDU.add((byte) 0x04);
//            APDU.add((byte) 0x00);
//            APDU.add((byte) 0x0A);
//            APDU.addAll(ByteUtils.convertPrimitiveToByte(new byte[]{(byte) 0xA0, 0x00, 0x00, 0x54, 0x53, 0x01, 0x54, 0x45, 0x48, 0x43}));
//            APDU.add((byte) 0X03); // LE
//            ComBean comBean = samTransfer(ByteUtils.convertPrimitiveToByte(APDU), samSlot);
//            if (comBean != null) {
//                if (ByteUtils.match(comBean.bRec[comBean.bRec.length - 3], 0x90) && ByteUtils.match(comBean.bRec[comBean.bRec.length - 2], 0x00)) {
//                    samID = ByteUtils.convertPrimitiveToByte(comBean.bRec).subList(5, 8);
//                    return true;
//                }
//            }
//        } catch (Exception ex) {
//            log.error("ReaderHandler > SamSelectApp():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return false;
//    }
//
//    private ComBean samTransfer(List<Byte> apdu, Integer samSlot) {
//        try {
//            int tmp = apdu.size() + 2;
//            List<Byte> cmd = new ArrayList<>();
//            //cmd.add((byte)0x02);
//            //cmd.add((byte)(tmp / 256));
//            //cmd.add((byte)(tmp % 256));
//            //cmd.add((byte) 0xC4);
//            //cmd.add(samSlot.byteValue());
//            //cmd.addAll(apdu);
//            //cmd.add((byte)0x00);
//            byte[] data = generateAPDU(0x38, false, samSlot, ByteUtils.convertByteToPrimitive(apdu));
//            samSerialPortWrapper.send(wrapAA00(data));
//            ComBean comBean = samSerialPortWrapper.read();
//            comBean.bRec = skipAA00(comBean.bRec);
//            return comBean;
//        } catch (Exception ex) {
//            log.error("ReaderHandler > samTransfer():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return null;
//    }
//
//    private byte[] skipAA00(byte[] cmd) {
//        try {
//            if (cmd != null && cmd.length > 2) {
//                byte old = 0x10;
//                List<Byte> ret = new ArrayList<>();
//                ret.add(cmd[0]);
//                for (int i = 1; i < cmd.length; i++) {
//                    if (ByteUtils.match(cmd[i], 0x00) && ByteUtils.match(old, 0xAA)) {
//                        ;//skip
//                    } else
//                        ret.add(cmd[i]);
//                    old = cmd[i];
//                }
//                return ByteUtils.convertByteToPrimitive(ret);
//            }
//        } catch (Exception ex) {
//            log.error("ReaderHandler > skipAA00():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return cmd;
//    }
//
//    private byte[] wrapAA00(byte[] cmd) {
//        try {
//            if (cmd != null && cmd.length > 2) {
//                List<Byte> ret = new ArrayList<>();
//                ret.add(cmd[0]);
//                ret.add(cmd[1]);
//                for (int i = 2; i < cmd.length; i++) {
//                    ret.add(cmd[i]);
//                    if (ByteUtils.match(cmd[i], 0xAA)) {
//                        ret.add((byte) 0x00);
//                    }
//                }
//                return ByteUtils.convertByteToPrimitive(ret);
//            }
//        } catch (Exception ex) {
//            log.error("ReaderHandler > wrapAA00():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return cmd;
//    }
//
//    private Boolean samVerifyPin() {
//        try {
//            List<Byte> APDU = new ArrayList<>();
//            APDU.add((byte) 0x01);
//            APDU.add((byte) 0x20);
//            APDU.add((byte) 0x00);
//            APDU.add((byte) 0x01);
//            APDU.add((byte) 0x02);
//            APDU.add((byte) 0x49);
//            APDU.add((byte) 0x52);
//            APDU.add((byte) 0x00);
//            //byte[] data = generateAPDU(0x38, samSlot, ByteUtils.convertByteToPrimitive(APDU));
//            ComBean comBean = samTransfer(ByteUtils.convertPrimitiveToByte(APDU), samSlot);
//            if (comBean != null) {
//                if (ByteUtils.match(comBean.bRec[comBean.bRec.length - 3], 0x90) && ByteUtils.match(comBean.bRec[comBean.bRec.length - 2], 0x00))
//                    return true;
//            }
//        } catch (Exception ex) {
//            log.error("ReaderHandler > samVerifyPin():" + ex.getMessage());
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            log.error(errors.toString());
//        }
//        return false;
//    }
//
//    public List<Byte> getSamID() {
//        return samID;
//    }
//
//    public Long getStaffID() {
//        return staffID;
//    }
//
//    public Integer getTransactionCountForUI() {
//        return transactionCountForUI;
//    }
//
//    public void setTransactionCountForUI(Integer transactionCountForUI) {
//        this.transactionCountForUI = transactionCountForUI;
//        if (activity != null)
//            activity.setTransactionCountOnUI(transactionCountForUI.toString());
//    }
//
//    public Long getCurrentCardSerial() {
//        return currentCardSerial;
//    }
//
//    public void setCurrentCardSerial(Long currentCardSerial) {
//        this.currentCardSerial = currentCardSerial;
//    }
}
