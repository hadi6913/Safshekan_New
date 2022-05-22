package com.safshekan.parkban.utils;

import android.util.Log;


public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    private static Utils instance;

    private Utils() {
    }

    public synchronized static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }

        return instance;
    }

    /**
     * padding position
     */
    public static enum EPaddingPosition {
        /**
         * padding left
         */
        PADDING_LEFT,
        /**
         * padding right
         */
        PADDING_RIGHT
    }

    public String bcdToStr(byte[] b) throws IllegalArgumentException {
        if (b == null) {
            Log.e(TAG, "bcdToStr input arg is null");
            throw new IllegalArgumentException("bcdToStr input arg is null");
        }

        char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }

        return sb.toString();
    }

    public byte[] strToBcd(String str, EPaddingPosition paddingPosition) throws IllegalArgumentException {
        if (str == null || paddingPosition == null) {
            Log.e(TAG, "strToBcd input arg is null");
            throw new IllegalArgumentException("strToBcd input arg is null");
        }

        int len = str.length();
        int mod = len % 2;
        if (mod != 0) {
            if (paddingPosition == EPaddingPosition.PADDING_RIGHT) {
                str = str + "0";
            } else {
                str = "0" + str;
            }
            len = str.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = str.getBytes();
        int j, k;
        for (int p = 0; p < str.length() / 2; p++) {
            if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else if ((abt[2 * p] >= 'A') && (abt[2 * p] <= 'Z')) {
                j = abt[2 * p] - 'A' + 0x0a;
            } else {
                j = abt[2 * p] - '0';
            }

            if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else if ((abt[2 * p + 1] >= 'A') && (abt[2 * p + 1] <= 'Z')) {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            } else {
                k = abt[2 * p + 1] - '0';
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }
}
