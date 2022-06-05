package com.khodmohaseb.parkban.anpr.farsi_ocr_anpr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.khodmohaseb.parkban.core.anpr.BaseAnprProvider;
import com.khodmohaseb.parkban.core.anpr.helpers.PlateDetectionState;
import com.khodmohaseb.parkban.core.anpr.helpers.RidingType;
import com.khodmohaseb.parkban.core.anpr.onPlateDetectedCallback;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;

import ir.shahaabco.ANPRNDK;

import static ir.shahaabco.ANPRNDK.anpr_recognize_mat;
import static ir.shahaabco.ANPRNDK.anpr_set_params;

public class FarsiOcrAnprProvider implements BaseAnprProvider {

    private static final String TAG = "ANPR-CAM";
    private Mat plateMat;
    private Context context;
    private ANPRNDK.SPlate p;
    private RidingType ridingType;

    public FarsiOcrAnprProvider(final Context context) {

        this.context = context;

        File dir = context.getExternalFilesDir(null);
        String mcs_path = dir.getPath();

        openCVLoader();

    }

    private void openCVLoader() {

        System.loadLibrary("anpr_ndk");

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, context, mLoaderCallback);

        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private boolean checkValidInt(String s) {
        try {
            Integer.parseInt(s);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkRidingType(String s) {
        try {
            Integer.parseInt(s);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(context) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    plateMat = new Mat();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @SuppressLint("StaticFieldLeak")
    @Override
    public void getPlate(final Context context, final Bitmap plateImage, final onPlateDetectedCallback callback) {

        this.context = context;

        if (plateMat == null)
            plateMat = new Mat();

        new AsyncTask<Void, Void, String>() {

            String part0 = "";
            String part1 = "";
            String part2 = "";
            String part3 = "";

            @Override
            protected String doInBackground(Void... results) {
                try {
                    Utils.bitmapToMat(plateImage, plateMat);

                    short resize_thresh = 500;
                    anpr_set_params((byte) 0, (byte) 8, (byte) 0, (byte) 1, resize_thresh);
                    p = anpr_recognize_mat(plateMat);
                    while ((p.str_fa.length() < 8) && resize_thresh < 1500) {
                        resize_thresh += 500;
                        anpr_set_params((byte) 0, (byte) 8, (byte) 0, (byte) 1, resize_thresh);
                        ANPRNDK.SPlate p2 = anpr_recognize_mat(plateMat);
                        if (p2.cnf[0] > p.cnf[0])
                            p = p2;
                    }

                    return p.str_fa;
                } catch (Exception e) {
                    return "";
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                String plate = "";

                if (checkRidingType(s))
                    ridingType = RidingType.MOTORCYCLE;
                else
                    ridingType = RidingType.CAR;

                if (p.cnf[0] < 0.6) {
                    callback.onPlateDetected(PlateDetectionState.NOT_FOUND, ridingType, "", plateImage, part0, part1, part2, part3);
                } else {
                    if (s.length() > 0 && s.length() == 8) {

                        if (ridingType == RidingType.CAR) {
                            if (
                                    checkValidInt(s.substring(0, Math.min(2, s.length())))
                                            && !checkValidInt(s.substring(2, Math.min(3, s.length())))
                                            && checkValidInt(s.substring(3, Math.min(6, s.length())))
                                            && checkValidInt(s.substring(6, Math.min(8, s.length())))
                                            && s.substring(0, Math.min(2, s.length())).length() == 2
                                            && s.substring(3, Math.min(6, s.length())).length() == 3
                                            && s.substring(6, Math.min(8, s.length())).length() == 2) {

                                part0 = s.substring(0, Math.min(2, s.length()));
                                part1 = s.substring(2, Math.min(3, s.length()));
                                if ((s.substring(2, Math.min(3, s.length())).equals("ا"))) {
                                    part1 = "الف";
                                }
                                if ((s.substring(2, Math.min(3, s.length())).equals("ژ")))
                                    part1 = "♿";

                                part2 = s.substring(3, Math.min(6, s.length()));
                                part3 = s.substring(6, Math.min(8, s.length()));

                                plate = part0 + part1 + part2 + part3;

                                callback.onPlateDetected(PlateDetectionState.DETECTED, ridingType, plate, plateImage, part0, part1, part2, part3);
                            } else {
                                callback.onPlateDetected(PlateDetectionState.NOT_FOUND, ridingType, plate, plateImage, part0, part1, part2, part3);
                            }
                        } else if (ridingType == RidingType.MOTORCYCLE) {
                            callback.onPlateDetected(PlateDetectionState.DETECTED, ridingType, s, plateImage, part0, part1, part2, part3);
                        }
                    } else {
                        callback.onPlateDetected(PlateDetectionState.NOT_FOUND, ridingType, plate, plateImage, part0, part1, part2, part3);
                    }
                }
            }
        }.execute();


    }
}
