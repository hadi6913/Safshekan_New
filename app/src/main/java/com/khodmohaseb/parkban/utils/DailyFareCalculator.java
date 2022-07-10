package com.khodmohaseb.parkban.utils;

import android.util.Log;

import com.khodmohaseb.parkban.services.dto.khodmohaseb.parkinginfo.Tariff;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DailyFareCalculator {
    private final static String TAG = "xeagle6913";
    static SimpleDateFormat sdfShow = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static long calculateDailyFareVehicle1(Date enterDateTime, Date exitDateTime, long roundedTotalStayLength, Tariff tariff) {
        Log.d(TAG, "calculateDailyFareVehicle1 > roundedTime:" + roundedTotalStayLength);
        Log.d(TAG, "calculateDailyFareVehicle1 > enterDate:" + sdfShow.format(enterDateTime));
        Log.d(TAG, "calculateDailyFareVehicle1 > exitDate:" + sdfShow.format(exitDateTime));

        long price = 0;

        if (roundedTotalStayLength < tariff.getCircadianCalcBase()) {
            price = HourlyFareCalculator.calculateStepFareVehicle1(roundedTotalStayLength, tariff);
            return price;
        }


        int totalDays = Days.daysBetween(new LocalDate(enterDateTime), new LocalDate(exitDateTime)).getDays();
        if (totalDays == 0) {
            price = tariff.getVehicleTariff1().getCostNight1();
            return price;
        }

        totalDays = totalDays + 1;

        Date enterTime = getDate(Integer.parseInt(sdfShow.format(enterDateTime).substring(11, 13)), Integer.parseInt(sdfShow.format(enterDateTime).substring(14, 16)));
        Date main24 = getDate(24, 00);
        int diff1 = getDiff(main24, enterTime);

        if (diff1 < tariff.getCircadianCalcBase()) {
            totalDays = totalDays - 1;
        }


        Date exitTime = getDate(Integer.parseInt(sdfShow.format(exitDateTime).substring(11, 13)), Integer.parseInt(sdfShow.format(exitDateTime).substring(14, 16)));
        Date main00 = getDate(00, 00);
        int diff2 = getDiff(exitTime, main00);

        if (diff2 < tariff.getCircadianCalcBase()) {
            totalDays = totalDays - 1;
        }

        Log.d(TAG, "calculateDailyFareVehicle1 > totalDays:" + totalDays);


        long remainingNights = totalDays - tariff.getVehicleTariff1().getDurationNight1();
        if (remainingNights >= 0) {
            price = price + tariff.getVehicleTariff1().getCostNight1();
            if (tariff.getVehicleTariff1().getDurationNight2() != null) {
                price = price +
                        ((remainingNights / tariff.getVehicleTariff1().getDurationNight2()) * tariff.getVehicleTariff1().getCostNight2()) +
                        (((remainingNights % tariff.getVehicleTariff1().getDurationNight2()) * tariff.getVehicleTariff1().getCostNight2()) / tariff.getVehicleTariff1().getDurationNight2());
                return price;
            } else {
                price = price +
                        ((remainingNights / tariff.getVehicleTariff1().getDurationNight1()) * tariff.getVehicleTariff1().getCostNight1()) +
                        (((remainingNights % tariff.getVehicleTariff1().getDurationNight1()) * tariff.getVehicleTariff1().getCostNight1()) / tariff.getVehicleTariff1().getDurationNight1());
                return price;
            }
        } else {
            price = price + ((totalDays * tariff.getVehicleTariff1().getCostNight1()) / tariff.getVehicleTariff1().getDurationNight1());
            return price;
        }


    }


    public static long calculateDailyFareVehicle2(Date enterDateTime, Date exitDateTime, long roundedTotalStayLength, Tariff tariff) {
        Log.d(TAG, "calculateDailyFareVehicle2 > roundedTime:" + roundedTotalStayLength);
        Log.d(TAG, "calculateDailyFareVehicle2 > enterDate:" + sdfShow.format(enterDateTime));
        Log.d(TAG, "calculateDailyFareVehicle2 > exitDate:" + sdfShow.format(exitDateTime));

        long price = 0;

        if (roundedTotalStayLength < tariff.getCircadianCalcBase()) {
            price = HourlyFareCalculator.calculateStepFareVehicle2(roundedTotalStayLength, tariff);
            return price;
        }


        int totalDays = Days.daysBetween(new LocalDate(enterDateTime), new LocalDate(exitDateTime)).getDays();
        if (totalDays == 0) {
            price = tariff.getVehicleTariff2().getCostNight1();
            return price;
        }

        totalDays = totalDays + 1;

        Date enterTime = getDate(Integer.parseInt(sdfShow.format(enterDateTime).substring(11, 13)), Integer.parseInt(sdfShow.format(enterDateTime).substring(14, 16)));
        Date main24 = getDate(24, 00);
        int diff1 = getDiff(main24, enterTime);

        if (diff1 < tariff.getCircadianCalcBase()) {
            totalDays = totalDays - 1;
        }


        Date exitTime = getDate(Integer.parseInt(sdfShow.format(exitDateTime).substring(11, 13)), Integer.parseInt(sdfShow.format(exitDateTime).substring(14, 16)));
        Date main00 = getDate(00, 00);
        int diff2 = getDiff(exitTime, main00);

        if (diff2 < tariff.getCircadianCalcBase()) {
            totalDays = totalDays - 1;
        }

        Log.d(TAG, "calculateDailyFareVehicle2 > totalDays:" + totalDays);


        long remainingNights = totalDays - tariff.getVehicleTariff2().getDurationNight1();
        if (remainingNights >= 0) {
            price = price + tariff.getVehicleTariff2().getCostNight1();
            if (tariff.getVehicleTariff2().getDurationNight2() != null) {
                price = price +
                        ((remainingNights / tariff.getVehicleTariff2().getDurationNight2()) * tariff.getVehicleTariff2().getCostNight2()) +
                        (((remainingNights % tariff.getVehicleTariff2().getDurationNight2()) * tariff.getVehicleTariff2().getCostNight2()) / tariff.getVehicleTariff2().getDurationNight2());
                return price;
            } else {
                price = price +
                        ((remainingNights / tariff.getVehicleTariff2().getDurationNight1()) * tariff.getVehicleTariff2().getCostNight1()) +
                        (((remainingNights % tariff.getVehicleTariff2().getDurationNight1()) * tariff.getVehicleTariff2().getCostNight1()) / tariff.getVehicleTariff2().getDurationNight1());
                return price;
            }
        } else {
            price = price + ((totalDays * tariff.getVehicleTariff2().getCostNight1()) / tariff.getVehicleTariff2().getDurationNight1());
            return price;
        }


    }


    public static long calculateDailyFareVehicle3(Date enterDateTime, Date exitDateTime, long roundedTotalStayLength, Tariff tariff) {
        Log.d(TAG, "calculateDailyFareVehicle3 > roundedTime:" + roundedTotalStayLength);
        Log.d(TAG, "calculateDailyFareVehicle3 > enterDate:" + sdfShow.format(enterDateTime));
        Log.d(TAG, "calculateDailyFareVehicle3 > exitDate:" + sdfShow.format(exitDateTime));

        long price = 0;

        if (roundedTotalStayLength < tariff.getCircadianCalcBase()) {
            price = HourlyFareCalculator.calculateStepFareVehicle3(roundedTotalStayLength, tariff);
            return price;
        }


        int totalDays = Days.daysBetween(new LocalDate(enterDateTime), new LocalDate(exitDateTime)).getDays();
        if (totalDays == 0) {
            price = tariff.getVehicleTariff3().getCostNight1();
            return price;
        }

        totalDays = totalDays + 1;

        Date enterTime = getDate(Integer.parseInt(sdfShow.format(enterDateTime).substring(11, 13)), Integer.parseInt(sdfShow.format(enterDateTime).substring(14, 16)));
        Date main24 = getDate(24, 00);
        int diff1 = getDiff(main24, enterTime);

        if (diff1 < tariff.getCircadianCalcBase()) {
            totalDays = totalDays - 1;
        }


        Date exitTime = getDate(Integer.parseInt(sdfShow.format(exitDateTime).substring(11, 13)), Integer.parseInt(sdfShow.format(exitDateTime).substring(14, 16)));
        Date main00 = getDate(00, 00);
        int diff2 = getDiff(exitTime, main00);

        if (diff2 < tariff.getCircadianCalcBase()) {
            totalDays = totalDays - 1;
        }

        Log.d(TAG, "calculateDailyFareVehicle3 > totalDays:" + totalDays);


        long remainingNights = totalDays - tariff.getVehicleTariff3().getDurationNight1();
        if (remainingNights >= 0) {
            price = price + tariff.getVehicleTariff3().getCostNight1();
            if (tariff.getVehicleTariff3().getDurationNight2() != null) {
                price = price +
                        ((remainingNights / tariff.getVehicleTariff3().getDurationNight2()) * tariff.getVehicleTariff3().getCostNight2()) +
                        (((remainingNights % tariff.getVehicleTariff3().getDurationNight2()) * tariff.getVehicleTariff3().getCostNight2()) / tariff.getVehicleTariff3().getDurationNight2());
                return price;
            } else {
                price = price +
                        ((remainingNights / tariff.getVehicleTariff3().getDurationNight1()) * tariff.getVehicleTariff3().getCostNight1()) +
                        (((remainingNights % tariff.getVehicleTariff3().getDurationNight1()) * tariff.getVehicleTariff3().getCostNight1()) / tariff.getVehicleTariff3().getDurationNight1());
                return price;
            }
        } else {
            price = price + ((totalDays * tariff.getVehicleTariff3().getCostNight1()) / tariff.getVehicleTariff3().getDurationNight1());
            return price;
        }


    }


    public static long calculateDailyFareVehicle4(Date enterDateTime, Date exitDateTime, long roundedTotalStayLength, Tariff tariff) {
        Log.d(TAG, "calculateDailyFareVehicle4 > roundedTime:" + roundedTotalStayLength);
        Log.d(TAG, "calculateDailyFareVehicle4 > enterDate:" + sdfShow.format(enterDateTime));
        Log.d(TAG, "calculateDailyFareVehicle4 > exitDate:" + sdfShow.format(exitDateTime));

        long price = 0;

        if (roundedTotalStayLength < tariff.getCircadianCalcBase()) {
            price = HourlyFareCalculator.calculateStepFareVehicle4(roundedTotalStayLength, tariff);
            return price;
        }


        int totalDays = Days.daysBetween(new LocalDate(enterDateTime), new LocalDate(exitDateTime)).getDays();
        if (totalDays == 0) {
            price = tariff.getVehicleTariff4().getCostNight1();
            return price;
        }

        totalDays = totalDays + 1;

        Date enterTime = getDate(Integer.parseInt(sdfShow.format(enterDateTime).substring(11, 13)), Integer.parseInt(sdfShow.format(enterDateTime).substring(14, 16)));
        Date main24 = getDate(24, 00);
        int diff1 = getDiff(main24, enterTime);

        if (diff1 < tariff.getCircadianCalcBase()) {
            totalDays = totalDays - 1;
        }


        Date exitTime = getDate(Integer.parseInt(sdfShow.format(exitDateTime).substring(11, 13)), Integer.parseInt(sdfShow.format(exitDateTime).substring(14, 16)));
        Date main00 = getDate(00, 00);
        int diff2 = getDiff(exitTime, main00);

        if (diff2 < tariff.getCircadianCalcBase()) {
            totalDays = totalDays - 1;
        }

        Log.d(TAG, "calculateDailyFareVehicle4 > totalDays:" + totalDays);


        long remainingNights = totalDays - tariff.getVehicleTariff4().getDurationNight1();
        if (remainingNights >= 0) {
            price = price + tariff.getVehicleTariff4().getCostNight1();
            if (tariff.getVehicleTariff4().getDurationNight2() != null) {
                price = price +
                        ((remainingNights / tariff.getVehicleTariff4().getDurationNight2()) * tariff.getVehicleTariff4().getCostNight2()) +
                        (((remainingNights % tariff.getVehicleTariff4().getDurationNight2()) * tariff.getVehicleTariff4().getCostNight2()) / tariff.getVehicleTariff4().getDurationNight2());
                return price;
            } else {
                price = price +
                        ((remainingNights / tariff.getVehicleTariff4().getDurationNight1()) * tariff.getVehicleTariff4().getCostNight1()) +
                        (((remainingNights % tariff.getVehicleTariff4().getDurationNight1()) * tariff.getVehicleTariff4().getCostNight1()) / tariff.getVehicleTariff4().getDurationNight1());
                return price;
            }
        } else {
            price = price + ((totalDays * tariff.getVehicleTariff4().getCostNight1()) / tariff.getVehicleTariff4().getDurationNight1());
            return price;
        }


    }


    public static long calculateDailyFareVehicle5(Date enterDateTime, Date exitDateTime, long roundedTotalStayLength, Tariff tariff) {
        Log.d(TAG, "calculateDailyFareVehicle5 > roundedTime:" + roundedTotalStayLength);
        Log.d(TAG, "calculateDailyFareVehicle5 > enterDate:" + sdfShow.format(enterDateTime));
        Log.d(TAG, "calculateDailyFareVehicle5 > exitDate:" + sdfShow.format(exitDateTime));

        long price = 0;

        if (roundedTotalStayLength < tariff.getCircadianCalcBase()) {
            price = HourlyFareCalculator.calculateStepFareVehicle5(roundedTotalStayLength, tariff);
            return price;
        }


        int totalDays = Days.daysBetween(new LocalDate(enterDateTime), new LocalDate(exitDateTime)).getDays();
        if (totalDays == 0) {
            price = tariff.getVehicleTariff5().getCostNight1();
            return price;
        }

        totalDays = totalDays + 1;

        Date enterTime = getDate(Integer.parseInt(sdfShow.format(enterDateTime).substring(11, 13)), Integer.parseInt(sdfShow.format(enterDateTime).substring(14, 16)));
        Date main24 = getDate(24, 00);
        int diff1 = getDiff(main24, enterTime);

        if (diff1 < tariff.getCircadianCalcBase()) {
            totalDays = totalDays - 1;
        }


        Date exitTime = getDate(Integer.parseInt(sdfShow.format(exitDateTime).substring(11, 13)), Integer.parseInt(sdfShow.format(exitDateTime).substring(14, 16)));
        Date main00 = getDate(00, 00);
        int diff2 = getDiff(exitTime, main00);

        if (diff2 < tariff.getCircadianCalcBase()) {
            totalDays = totalDays - 1;
        }

        Log.d(TAG, "calculateDailyFareVehicle5 > totalDays:" + totalDays);


        long remainingNights = totalDays - tariff.getVehicleTariff5().getDurationNight1();
        if (remainingNights >= 0) {
            price = price + tariff.getVehicleTariff5().getCostNight1();
            if (tariff.getVehicleTariff5().getDurationNight2() != null) {
                price = price +
                        ((remainingNights / tariff.getVehicleTariff5().getDurationNight2()) * tariff.getVehicleTariff5().getCostNight2()) +
                        (((remainingNights % tariff.getVehicleTariff5().getDurationNight2()) * tariff.getVehicleTariff5().getCostNight2()) / tariff.getVehicleTariff5().getDurationNight2());
                return price;
            } else {
                price = price +
                        ((remainingNights / tariff.getVehicleTariff5().getDurationNight1()) * tariff.getVehicleTariff5().getCostNight1()) +
                        (((remainingNights % tariff.getVehicleTariff5().getDurationNight1()) * tariff.getVehicleTariff5().getCostNight1()) / tariff.getVehicleTariff5().getDurationNight1());
                return price;
            }
        } else {
            price = price + ((totalDays * tariff.getVehicleTariff5().getCostNight1()) / tariff.getVehicleTariff5().getDurationNight1());
            return price;
        }

    }


    public static long calculateDailyFareVehicle6(Date enterDateTime, Date exitDateTime, long roundedTotalStayLength, Tariff tariff) {


        Log.d(TAG, "calculateDailyFareVehicle6 > roundedTime:" + roundedTotalStayLength);
        Log.d(TAG, "calculateDailyFareVehicle6 > enterDate:" + sdfShow.format(enterDateTime));
        Log.d(TAG, "calculateDailyFareVehicle6 > exitDate:" + sdfShow.format(exitDateTime));

        long price = 0;

        if (roundedTotalStayLength < tariff.getCircadianCalcBase()) {
            price = HourlyFareCalculator.calculateStepFareVehicle6(roundedTotalStayLength, tariff);
            return price;
        }


        int totalDays = Days.daysBetween(new LocalDate(enterDateTime), new LocalDate(exitDateTime)).getDays();
        if (totalDays == 0) {
            price = tariff.getVehicleTariff6().getCostNight1();
            return price;
        }

        totalDays = totalDays + 1;

        Date enterTime = getDate(Integer.parseInt(sdfShow.format(enterDateTime).substring(11, 13)), Integer.parseInt(sdfShow.format(enterDateTime).substring(14, 16)));
        Date main24 = getDate(24, 00);
        int diff1 = getDiff(main24, enterTime);

        if (diff1 < tariff.getCircadianCalcBase()) {
            totalDays = totalDays - 1;
        }


        Date exitTime = getDate(Integer.parseInt(sdfShow.format(exitDateTime).substring(11, 13)), Integer.parseInt(sdfShow.format(exitDateTime).substring(14, 16)));
        Date main00 = getDate(00, 00);
        int diff2 = getDiff(exitTime, main00);

        if (diff2 < tariff.getCircadianCalcBase()) {
            totalDays = totalDays - 1;
        }

        Log.d(TAG, "calculateDailyFareVehicle6 > totalDays:" + totalDays);


        long remainingNights = totalDays - tariff.getVehicleTariff6().getDurationNight1();
        if (remainingNights >= 0) {
            price = price + tariff.getVehicleTariff6().getCostNight1();
            if (tariff.getVehicleTariff6().getDurationNight2() != null) {
                price = price +
                        ((remainingNights / tariff.getVehicleTariff6().getDurationNight2()) * tariff.getVehicleTariff6().getCostNight2()) +
                        (((remainingNights % tariff.getVehicleTariff6().getDurationNight2()) * tariff.getVehicleTariff6().getCostNight2()) / tariff.getVehicleTariff6().getDurationNight2());
                return price;
            } else {
                price = price +
                        ((remainingNights / tariff.getVehicleTariff6().getDurationNight1()) * tariff.getVehicleTariff6().getCostNight1()) +
                        (((remainingNights % tariff.getVehicleTariff6().getDurationNight1()) * tariff.getVehicleTariff6().getCostNight1()) / tariff.getVehicleTariff6().getDurationNight1());
                return price;
            }
        } else {
            price = price + ((totalDays * tariff.getVehicleTariff6().getCostNight1()) / tariff.getVehicleTariff6().getDurationNight1());
            return price;
        }


    }

    public static long calculateDailyFareVehicle7(Date enterDateTime, Date exitDateTime, long roundedTotalStayLength, Tariff tariff) {


        Log.d(TAG, "calculateDailyFareVehicle7 > roundedTime:" + roundedTotalStayLength);
        Log.d(TAG, "calculateDailyFareVehicle7 > enterDate:" + sdfShow.format(enterDateTime));
        Log.d(TAG, "calculateDailyFareVehicle7 > exitDate:" + sdfShow.format(exitDateTime));

        long price = 0;

        if (roundedTotalStayLength < tariff.getCircadianCalcBase()) {
            price = HourlyFareCalculator.calculateStepFareVehicle7(roundedTotalStayLength, tariff);
            return price;
        }


        int totalDays = Days.daysBetween(new LocalDate(enterDateTime), new LocalDate(exitDateTime)).getDays();
        if (totalDays == 0) {
            price = tariff.getVehicleTariff7().getCostNight1();
            return price;
        }

        totalDays = totalDays + 1;

        Date enterTime = getDate(Integer.parseInt(sdfShow.format(enterDateTime).substring(11, 13)), Integer.parseInt(sdfShow.format(enterDateTime).substring(14, 16)));
        Date main24 = getDate(24, 00);
        int diff1 = getDiff(main24, enterTime);

        if (diff1 < tariff.getCircadianCalcBase()) {
            totalDays = totalDays - 1;
        }


        Date exitTime = getDate(Integer.parseInt(sdfShow.format(exitDateTime).substring(11, 13)), Integer.parseInt(sdfShow.format(exitDateTime).substring(14, 16)));
        Date main00 = getDate(00, 00);
        int diff2 = getDiff(exitTime, main00);

        if (diff2 < tariff.getCircadianCalcBase()) {
            totalDays = totalDays - 1;
        }

        Log.d(TAG, "calculateDailyFareVehicle7 > totalDays:" + totalDays);


        long remainingNights = totalDays - tariff.getVehicleTariff7().getDurationNight1();
        if (remainingNights >= 0) {
            price = price + tariff.getVehicleTariff7().getCostNight1();
            if (tariff.getVehicleTariff7().getDurationNight2() != null) {
                price = price +
                        ((remainingNights / tariff.getVehicleTariff7().getDurationNight2()) * tariff.getVehicleTariff7().getCostNight2()) +
                        (((remainingNights % tariff.getVehicleTariff7().getDurationNight2()) * tariff.getVehicleTariff7().getCostNight2()) / tariff.getVehicleTariff7().getDurationNight2());
                return price;
            } else {
                price = price +
                        ((remainingNights / tariff.getVehicleTariff7().getDurationNight1()) * tariff.getVehicleTariff7().getCostNight1()) +
                        (((remainingNights % tariff.getVehicleTariff7().getDurationNight1()) * tariff.getVehicleTariff7().getCostNight1()) / tariff.getVehicleTariff7().getDurationNight1());
                return price;
            }
        } else {
            price = price + ((totalDays * tariff.getVehicleTariff7().getCostNight1()) / tariff.getVehicleTariff7().getDurationNight1());
            return price;
        }


    }


    public static long calculateDailyFareVehicle8(Date enterDateTime, Date exitDateTime, long roundedTotalStayLength, Tariff tariff) {


        Log.d(TAG, "calculateDailyFareVehicle8 > roundedTime:" + roundedTotalStayLength);
        Log.d(TAG, "calculateDailyFareVehicle8 > enterDate:" + sdfShow.format(enterDateTime));
        Log.d(TAG, "calculateDailyFareVehicle8 > exitDate:" + sdfShow.format(exitDateTime));

        long price = 0;

        if (roundedTotalStayLength < tariff.getCircadianCalcBase()) {
            price = HourlyFareCalculator.calculateStepFareVehicle8(roundedTotalStayLength, tariff);
            return price;
        }


        int totalDays = Days.daysBetween(new LocalDate(enterDateTime), new LocalDate(exitDateTime)).getDays();
        if (totalDays == 0) {
            price = tariff.getVehicleTariff8().getCostNight1();
            return price;
        }

        totalDays = totalDays + 1;

        Date enterTime = getDate(Integer.parseInt(sdfShow.format(enterDateTime).substring(11, 13)), Integer.parseInt(sdfShow.format(enterDateTime).substring(14, 16)));
        Date main24 = getDate(24, 00);
        int diff1 = getDiff(main24, enterTime);

        if (diff1 < tariff.getCircadianCalcBase()) {
            totalDays = totalDays - 1;
        }


        Date exitTime = getDate(Integer.parseInt(sdfShow.format(exitDateTime).substring(11, 13)), Integer.parseInt(sdfShow.format(exitDateTime).substring(14, 16)));
        Date main00 = getDate(00, 00);
        int diff2 = getDiff(exitTime, main00);

        if (diff2 < tariff.getCircadianCalcBase()) {
            totalDays = totalDays - 1;
        }

        Log.d(TAG, "calculateDailyFareVehicle8 > totalDays:" + totalDays);


        long remainingNights = totalDays - tariff.getVehicleTariff8().getDurationNight1();
        if (remainingNights >= 0) {
            price = price + tariff.getVehicleTariff8().getCostNight1();
            if (tariff.getVehicleTariff8().getDurationNight2() != null) {
                price = price +
                        ((remainingNights / tariff.getVehicleTariff8().getDurationNight2()) * tariff.getVehicleTariff8().getCostNight2()) +
                        (((remainingNights % tariff.getVehicleTariff8().getDurationNight2()) * tariff.getVehicleTariff8().getCostNight2()) / tariff.getVehicleTariff8().getDurationNight2());
                return price;
            } else {
                price = price +
                        ((remainingNights / tariff.getVehicleTariff8().getDurationNight1()) * tariff.getVehicleTariff8().getCostNight1()) +
                        (((remainingNights % tariff.getVehicleTariff8().getDurationNight1()) * tariff.getVehicleTariff8().getCostNight1()) / tariff.getVehicleTariff8().getDurationNight1());
                return price;
            }
        } else {
            price = price + ((totalDays * tariff.getVehicleTariff8().getCostNight1()) / tariff.getVehicleTariff8().getDurationNight1());
            return price;
        }


    }


    public static Date getDate(int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 0);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 0);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static int getDiff(Date date1, Date date2) {
        /* this function get the difference between date1 and date2
         * represented in minutes
         * assume that the formate is HH:MM */
        int min = 0;
        int hr = 0;
        if (date1.getMinutes() >= date2.getMinutes()) {
            min = date1.getMinutes() - date2.getMinutes();
        } else {
            min = date1.getMinutes() + (60 - date2.getMinutes());
            hr = -1;
        }
        if ((date1.getHours() + hr) >= date2.getHours()) {
            hr += date1.getHours() - date2.getHours();
        } else {
            hr += date1.getHours() + (24 - date2.getHours());
        }
        return min + (hr * 60);
    }


}
