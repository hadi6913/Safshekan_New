package com.khodmohaseb.parkban.utils;

public class PelakUtility {
    public static String convertToCode(String inputChar) {
        String result = "";
        switch (inputChar) {
            case "الف":
                result = "01";
                break;

            case "ب":
                result = "02";
                break;

            case "پ":
                result = "03";
                break;
            case "ت":
                result = "04";
                break;

            case "ث":
                result = "05";
                break;

            case "ج":
                result = "06";
                break;

            case "د":
                result = "07";
                break;

            case "ز":
                result = "08";
                break;

            case "ژ":
                result = "09";
                break;

            case "س":
                result = "10";
                break;

            case "ش":
                result = "11";
                break;

            case "ص":
                result = "12";
                break;

            case "ط":
                result = "13";
                break;

            case "ع":
                result = "14";
                break;

            case "ف":
                result = "15";
                break;

            case "ق":
                result = "16";
                break;

            case "ک":
                result = "17";
                break;

            case "گ":
                result = "18";
                break;

            case "ل":
                result = "19";
                break;

            case "م":
                result = "20";
                break;

            case "ن":
                result = "21";
                break;

            case "و":
                result = "22";
                break;

            case "ﻫ":
                result = "23";
                break;

            case "ی":
                result = "24";
                break;

            case "S":
                result = "25";
                break;

            case "D":
                result = "26";
                break;

            case "♿":
                result = "27";
                break;


        }

        return result;


    }

    public static String convertFromCode(String inputIndex) {
        String result = "";
        switch (inputIndex) {
            case "01":
                result = "الف";
                break;

            case "02":
                result = "ب";
                break;

            case "03":
                result = "پ";
                break;
            case "04":
                result = "ت";
                break;

            case "05":
                result = "ث";
                break;

            case "06":
                result = "ج";
                break;

            case "07":
                result = "د";
                break;

            case "08":
                result = "ز";
                break;

            case "09":
                result = "ژ";
                break;

            case "10":
                result = "س";
                break;

            case "11":
                result = "ش";
                break;

            case "12":
                result = "ص";
                break;

            case "13":
                result = "ط";
                break;

            case "14":
                result = "ع";
                break;

            case "15":
                result = "ف";
                break;

            case "16":
                result = "ق";
                break;

            case "17":
                result = "ک";
                break;

            case "18":
                result = "گ";
                break;

            case "19":
                result = "ل";
                break;

            case "20":
                result = "م";
                break;

            case "21":
                result = "ن";
                break;

            case "22":
                result = "و";
                break;

            case "23":
                result = "ﻫ";
                break;

            case "24":
                result = "ی";
                break;

            case "25":
                result = "S";
                break;

            case "26":
                result = "D";
                break;

            case "27":
                result = "♿";
                break;


        }

        return result;


    }

}
