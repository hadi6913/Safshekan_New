package com.safshekan.parkban.core.anpr;

import android.content.Context;
import android.graphics.Bitmap;

public interface BaseAnprProvider {

    void getPlate(Context context , Bitmap plateImage , onPlateDetectedCallback callback);

}
