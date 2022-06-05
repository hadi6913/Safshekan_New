package com.khodmohaseb.parkban.persistence.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.khodmohaseb.parkban.helper.FontHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CarItems {

    private String part0, part1, part2, part3, carItemSize, lastTime = "", state;
    private Bitmap imageFile;
    private List<String> timeList;
    private long carId;
    private boolean allPlateSent = false;
    private boolean carType = true;

    public CarItems(Context context, Car car) {

        Log.i("===========****" , "CarItems ");

        if (car.getPlateNo() != null) {
            if (car.getPlateNo().length() == 10) {
                carType = true;
                part0 = car.getPlateNo().substring(0, 2);
                part1 = car.getPlateNo().substring(2, 5);
                part2 = car.getPlateNo().substring(5, 8);
                part3 = car.getPlateNo().substring(8, 10);
            } else {
                if (checkRidingType(car.getPlateNo())) {
                    carType = false;
                    part0 = car.getPlateNo().substring(0, 3);
                    part1 = car.getPlateNo().substring(3, car.getPlateNo().length());
                } else {
                    carType = true;
                    part0 = car.getPlateNo().substring(0, 2);
                    part1 = car.getPlateNo().substring(2, 3);
                    part2 = car.getPlateNo().substring(3, 6);
                    part3 = car.getPlateNo().substring(6, 8);
                }
            }

            if (car.getCarPlates().size() > 0) {
                // String seenDate = car.getCarPlates().get(car.getCarPlates().size() - 1).getSeenDate();
                // lastTime = DateTimeHelper.parsTime(seenDate, false);

                Date date = new Date(car.getCarPlates().get(0).getRecordDate().getTime());
                int hours = date.getHours();
                int minutes = date.getMinutes();

                lastTime = FontHelper.IntegerFormat(hours) + ":" + FontHelper.IntegerFormat(minutes);

//                File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//                File outFile = new File(storageDir, car.getCarPlates().get(car.getCarPlates().size() - 1).getPlateFileName());
//                String absolutePath = outFile.getAbsolutePath();
//                imageFile = ImageLoadHelper.getInstance().loadImage(context, absolutePath);
//                imageFile = Bitmap.createScaledBitmap(imageFile, 95, 95, false);


                carItemSize = String.valueOf(car.getCarPlates().size());
                Log.i("*************---", "carItemSize " + String.valueOf(car.getCarPlates().size()));

            }

            int send = 0;
            for (int i = 0; i < car.getCarPlates().size(); i++) {
                if (car.getCarPlates().get(i).getStatus() == 1) {
                    send++;
                }

                if (send == car.getCarPlates().size()) {
                    allPlateSent = true;
                } else
                    allPlateSent = false;
            }

            carId = car.getId();
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

    public long getCarId() {
        return carId;
    }

    public void setCarId(long carId) {
        this.carId = carId;
    }

    public String getPart0() {
        return part0;
    }

    public void setPart0(String part0) {
        this.part0 = part0;
    }

    public String getPart1() {
        return part1;
    }

    public void setPart1(String part1) {
        this.part1 = part1;
    }

    public String getPart2() {
        return part2;
    }

    public void setPart2(String part2) {
        this.part2 = part2;
    }

    public String getPart3() {
        return part3;
    }

    public void setPart3(String part3) {
        this.part3 = part3;
    }

    public String getCarItemSize() {
        return carItemSize;
    }

    public void setCarItemSize(String carItemSize) {
        this.carItemSize = carItemSize;
    }

    public Bitmap getImageFile() {
        return imageFile;
    }

    public void setImageFile(Bitmap imageFile) {
        this.imageFile = imageFile;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public List<String> getTimeList() {
        if (timeList == null)
            timeList = new ArrayList<>();
        return timeList;
    }

    public void setTimeList(List<String> timeList) {
        this.timeList = timeList;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isAllPlateSent() {
        return allPlateSent;
    }

    public void setAllPlateSent(boolean allPlateSent) {
        this.allPlateSent = allPlateSent;
    }

    public boolean isCarType() {
        return carType;
    }

    public void setCarType(boolean carType) {
        this.carType = carType;
    }
}
