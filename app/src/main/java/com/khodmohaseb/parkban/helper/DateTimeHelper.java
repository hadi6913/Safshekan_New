package com.khodmohaseb.parkban.helper;

import android.text.format.Time;

import com.khodmohaseb.parkban.persistence.models.Language;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateTimeHelper {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String TIME_FORMAT_HHMM = "HH:mm";
    private static final String PERSIAN_DATE_FORMAT = "%04d/%02d/%02d";

    public static int getHour(Date date) {
        return dateToCalendar(date).get(Calendar.HOUR_OF_DAY);
    }

    public static Date setHour(Date date, int hour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        return cal.getTime();
    }

    public static Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String DateToString(Date date) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", GetLocal(Language.en));
        String formatted = format1.format(date);
        return formatted;
    }

    public static Locale GetLocal(Language lan) {
        Locale locale = new Locale(lan.toString());
        return locale;

    }

    public static Date parseDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.US);
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parseDate(String dateStr, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.US);
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String parsTime(String dateStr, boolean fullTime) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.US);
        String time = null;
        try {
            Date date = formatter.parse(dateStr);
            String hours = String.valueOf(date.getHours());
            String minutes = String.valueOf(date.getMinutes());
            String seconds = String.valueOf(date.getSeconds());
            if (String.valueOf(minutes).length() == 1)
                minutes = "0" + minutes;
            if (fullTime == true)
                time = hours + ":" + minutes + ":" + seconds;
            else
                time = hours + ":" + minutes;
            return time;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String DateToStr(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.US);
        return formatter.format(date);
    }

    public static String DateToStr(Date date) {
        return DateToStr(date, DATE_TIME_FORMAT);
    }

    public static String TimeToStrHHMM(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT_HHMM, Locale.US);
        return formatter.format(date);
    }

    public static java.sql.Date convertUtilToSql(java.util.Date uDate) {
        java.sql.Date sDate = new java.sql.Date(uDate.getTime());
        return sDate;
    }

    public static long getDateDifferent(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static long getCurrentTimeForDB() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long time = calendar.getTime().getTime();
        Time timeFormat = new Time();
        timeFormat.set(time - TimeZone.getDefault().getOffset(time));
        return timeFormat.toMillis(true);
    }

    public static int getTimeMinute() {
        int minuteTime = 0;
        try {
            SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
            String time = localDateFormat.format(new Date());

            String hour = time.substring(0, 2);
            String min = time.substring(3, 5);

            minuteTime = Integer.parseInt(hour) * 60 + Integer.parseInt(min);

        } catch (Exception e) {

        }
        return minuteTime;
    }

    public static boolean checkShiftTime(int start, int end) {

        boolean isShift = true;
        int now = getTimeMinute();

        if (start <= now && now <= end)
            isShift = true;
        else
            isShift = false;

        return isShift;
    }

    public static Date getBeginCurrentMonth() {
        PersianCalendar p = new PersianCalendar();
        p.setTime(Calendar.getInstance().getTime());
        p.setPersianDate(p.getPersianYear(), p.getPersianMonth(), 1);
        return p.getTime();
    }

    public static Date getEndCurrentMonth() {
        PersianCalendar p = new PersianCalendar();
        p.setTime(Calendar.getInstance().getTime());
        p.setPersianDate(p.getPersianYear(), p.getPersianMonth(), 1);

        int lastDay;

        if (p.getPersianMonth() >= 0 && p.getPersianMonth() <= 5)
            lastDay = 31;
        else if (p.getPersianMonth() >= 6 && p.getPersianMonth() <= 10)
            lastDay = 30;
        else if (p.isLeapYear(p.getPersianYear()))
            lastDay = 30;
        else
            lastDay = 29;

        p.setPersianDate(p.getPersianYear(), p.getPersianMonth(), lastDay);
        return p.getTime();
    }

    /*** Persian Date and Time ***/
    private static class SolarCalendar {

        public String strWeekDay = "";
        public String strMonth = "";

        int date;
        int month;
        int year;
        int hour , minutes;

        public SolarCalendar()
        {
            Date MiladiDate = new Date();
            calcSolarCalendar(MiladiDate);
        }

        public SolarCalendar(Date MiladiDate)
        {
            calcSolarCalendar(MiladiDate);
        }

        private void calcSolarCalendar(Date MiladiDate) {

            int ld;

            int miladiYear = MiladiDate.getYear() + 1900;
            int miladiMonth = MiladiDate.getMonth() + 1;
            int miladiDate = MiladiDate.getDate();
            int WeekDay = MiladiDate.getDay();

            hour = MiladiDate.getHours();
            minutes = MiladiDate.getMinutes();

            int[] buf1 = new int[12];
            int[] buf2 = new int[12];

            buf1[0] = 0;
            buf1[1] = 31;
            buf1[2] = 59;
            buf1[3] = 90;
            buf1[4] = 120;
            buf1[5] = 151;
            buf1[6] = 181;
            buf1[7] = 212;
            buf1[8] = 243;
            buf1[9] = 273;
            buf1[10] = 304;
            buf1[11] = 334;

            buf2[0] = 0;
            buf2[1] = 31;
            buf2[2] = 60;
            buf2[3] = 91;
            buf2[4] = 121;
            buf2[5] = 152;
            buf2[6] = 182;
            buf2[7] = 213;
            buf2[8] = 244;
            buf2[9] = 274;
            buf2[10] = 305;
            buf2[11] = 335;

            if ((miladiYear % 4) != 0) {
                date = buf1[miladiMonth - 1] + miladiDate;

                if (date > 79) {
                    date = date - 79;
                    if (date <= 186) {
                        switch (date % 31) {
                            case 0:
                                month = date / 31;
                                date = 31;
                                break;
                            default:
                                month = (date / 31) + 1;
                                date = (date % 31);
                                break;
                        }
                        year = miladiYear - 621;
                    } else {
                        date = date - 186;

                        switch (date % 30) {
                            case 0:
                                month = (date / 30) + 6;
                                date = 30;
                                break;
                            default:
                                month = (date / 30) + 7;
                                date = (date % 30);
                                break;
                        }
                        year = miladiYear - 621;
                    }
                } else {
                    if ((miladiYear > 1996) && (miladiYear % 4) == 1) {
                        ld = 11;
                    } else {
                        ld = 10;
                    }
                    date = date + ld;

                    switch (date % 30) {
                        case 0:
                            month = (date / 30) + 9;
                            date = 30;
                            break;
                        default:
                            month = (date / 30) + 10;
                            date = (date % 30);
                            break;
                    }
                    year = miladiYear - 622;
                }
            } else {
                date = buf2[miladiMonth - 1] + miladiDate;

                if (miladiYear >= 1996) {
                    ld = 79;
                } else {
                    ld = 80;
                }
                if (date > ld) {
                    date = date - ld;

                    if (date <= 186) {
                        switch (date % 31) {
                            case 0:
                                month = (date / 31);
                                date = 31;
                                break;
                            default:
                                month = (date / 31) + 1;
                                date = (date % 31);
                                break;
                        }
                        year = miladiYear - 621;
                    } else {
                        date = date - 186;

                        switch (date % 30) {
                            case 0:
                                month = (date / 30) + 6;
                                date = 30;
                                break;
                            default:
                                month = (date / 30) + 7;
                                date = (date % 30);
                                break;
                        }
                        year = miladiYear - 621;
                    }
                }

                else {
                    date = date + 10;

                    switch (date % 30) {
                        case 0:
                            month = (date / 30) + 9;
                            date = 30;
                            break;
                        default:
                            month = (date / 30) + 10;
                            date = (date % 30);
                            break;
                    }
                    year = miladiYear - 622;
                }

            }

            switch (month) {
                case 1:
                    strMonth = "فروردين";
                    break;
                case 2:
                    strMonth = "ارديبهشت";
                    break;
                case 3:
                    strMonth = "خرداد";
                    break;
                case 4:
                    strMonth = "تير";
                    break;
                case 5:
                    strMonth = "مرداد";
                    break;
                case 6:
                    strMonth = "شهريور";
                    break;
                case 7:
                    strMonth = "مهر";
                    break;
                case 8:
                    strMonth = "آبان";
                    break;
                case 9:
                    strMonth = "آذر";
                    break;
                case 10:
                    strMonth = "دي";
                    break;
                case 11:
                    strMonth = "بهمن";
                    break;
                case 12:
                    strMonth = "اسفند";
                    break;
            }

            switch (WeekDay) {

                case 0:
                    strWeekDay = "يکشنبه";
                    break;
                case 1:
                    strWeekDay = "دوشنبه";
                    break;
                case 2:
                    strWeekDay = "سه شنبه";
                    break;
                case 3:
                    strWeekDay = "چهارشنبه";
                    break;
                case 4:
                    strWeekDay = "پنج شنبه";
                    break;
                case 5:
                    strWeekDay = "جمعه";
                    break;
                case 6:
                    strWeekDay = "شنبه";
                    break;
            }

        }

    }

    public static String getCurrentShamsidate() {
        Locale loc = new Locale("en_US");
        SolarCalendar sc = new SolarCalendar();
        return String.valueOf(sc.year) + "/" + String.format(loc, "%02d",
                sc.month) + "/" + String.format(loc, "%02d", sc.date ) + " " + sc.hour + ":" + sc.minutes ;
    }

}
