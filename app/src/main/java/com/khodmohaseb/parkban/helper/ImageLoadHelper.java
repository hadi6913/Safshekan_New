package com.khodmohaseb.parkban.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class ImageLoadHelper {

    private static final String TAG = "ImageLoadHelper";
    public static final int REQUEST_IMAGE_WIDTH = 640;

    private static ImageLoadHelper instance;

    private ImageLoadHelper() {
    }

    public static ImageLoadHelper getInstance() {
        if (instance == null) {
            instance = new ImageLoadHelper();
        }
        return instance;
    }

    public Bitmap loadImage(Context context, String imageFilePath) {
        Bitmap orgImg = null;
        Bitmap cropImg = null;


        Log.d("xxxxhhhhhh", ""+imageFilePath);

        if (imageFilePath.contains("/")) {
            Log.d("xeagle69", "camera >>> takeNewPhoto 7-1");
            orgImg = loadBitmapFromFile(imageFilePath, REQUEST_IMAGE_WIDTH, REQUEST_IMAGE_WIDTH);
        } else {
            orgImg = loadPhotoFromPrivateFile(context, imageFilePath);
        }

        if (orgImg != null){
            Log.d("xeagle69", "camera >>> takeNewPhoto 7-2");
            cropImg = cropCenterBitmap(orgImg);
        }


        return cropImg;
//         return orgImg;
    }

    public static Bitmap cropCenterBitmap(Bitmap sourceBitmap) {
        if (sourceBitmap.getWidth() >= sourceBitmap.getHeight()) {
            return Bitmap.createBitmap(sourceBitmap,
                    sourceBitmap.getWidth() / 2 - sourceBitmap.getHeight() / 2, 0,
                    sourceBitmap.getHeight(), sourceBitmap.getHeight());
        } else {
            return Bitmap.createBitmap(sourceBitmap,
                    0, sourceBitmap.getHeight() / 2 - sourceBitmap.getWidth() / 2,
                    sourceBitmap.getWidth(), sourceBitmap.getWidth()
            );
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            while ((height / inSampleSize) >= reqHeight
                    && (width / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static Bitmap loadBitmapFromFile(String fileName, int reqWidth, int reqHeight) {
        Bitmap result = null;
        try {
            File f = new File(fileName);

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;

            result = BitmapFactory.decodeFile(fileName, options);



            try {

                ExifInterface exif = new ExifInterface(fileName);
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                Log.d("xeagle69", "camera 7-1-1>>> rotation in int >>> "+rotation);
                int rotationInDegrees = exifToDegrees(rotation);
                Log.d("xeagle69", "camera 7-1-2>>> rotation in degree >>> "+rotationInDegrees);
                Matrix matrix = new Matrix();
                if (rotation != 0f) {
                    Log.d("xeagle69", " ><><><><><><><");
                    matrix.preRotate(rotationInDegrees);
                    result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in read Photo ExifInterface", e);
            }






        }catch (Exception e){
            Log.e(TAG, "Error in read Photo ExifInterface", e);
        }
        return result;
    }

    public static Bitmap loadPhotoFromPrivateFile(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            Bitmap photo = BitmapFactory.decodeStream(fis);
            fis.close();
            return photo;
        } catch (Exception e) {
            Log.d(TAG, "Error in loadPhotoFromPrivateFile", e);
        }
        return null;
    }

    public Bitmap convertFileToBitmap(Context context, String fileName) {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File outFile = new File(storageDir, fileName);
        String absolutePath = outFile.getAbsolutePath();
        Bitmap image = getInstance().loadImage(context, absolutePath);
        return image;
    }

    public int[] convertBitmapToIntArray(Bitmap image) {

//        Mat imIn = new Mat();
//        Utils.bitmapToMat(image, imIn);
//        Imgproc.resize(imIn, imIn, new Size(400, imIn.rows() * 400 / imIn.cols()), 0, 0, Imgproc.INTER_CUBIC);
//        Bitmap finalBitmap = Bitmap.createBitmap(imIn.cols(), imIn.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(imIn,finalBitmap);

        byte[] byteArray = null;

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(image, 300, 200, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 10, stream);
        byteArray = stream.toByteArray();

        //Convert Byte Array To Int Array
        int[] ret = new int[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            ret[i] = byteArray[i] & 0xff; // Range 0 to 255, not -128 to 127
        }

        return ret;
    }

    public int[] convertImageFileToIntArray(Context context, String fileName) {
        byte[] byteArray = null;
        int[] intArray = null;

        try {
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File outFile = new File(storageDir, fileName);
            String absolutePath = outFile.getAbsolutePath();
            Bitmap image = getInstance().loadImage(context, absolutePath);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(image, 300, 200, true);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            byteArray = stream.toByteArray();

            //Convert Byte Array To Int Array
            intArray = new int[byteArray.length];
            for (int i = 0; i < byteArray.length; i++) {
                intArray[i] = byteArray[i] & 0xff; // Range 0 to 255, not -128 to 127
            }
        } catch (Exception e) {
            Log.i("------>", e.getMessage());
        }

        return intArray;
    }

    public Bitmap convertDrawableToBitmap(Context context, int drawableID) {
        return BitmapFactory.decodeResource(context.getResources(),
                drawableID);
    }


//    public Bitmap scaleImage(Context context, Uri photoUri) throws IOException {
//        InputStream is = context.getContentResolver().openInputStream(photoUri);
//        BitmapFactory.Options dbo = new BitmapFactory.Options();
//        dbo.inJustDecodeBounds = true;
//        BitmapFactory.decodeStream(is, null, dbo);
//        is.close();
//
//        int rotatedWidth, rotatedHeight;
//        int orientation = getOrientation(context, photoUri);
//
//        if (orientation == 90 || orientation == 270) {
//            rotatedWidth = dbo.outHeight;
//            rotatedHeight = dbo.outWidth;
//        } else {
//            rotatedWidth = dbo.outWidth;
//            rotatedHeight = dbo.outHeight;
//        }
//
//        Bitmap srcBitmap;
//        is = context.getContentResolver().openInputStream(photoUri);
//        if (rotatedWidth > REQUEST_IMAGE_WIDTH || rotatedHeight > REQUEST_IMAGE_WIDTH) {
//            float widthRatio = ((float) rotatedWidth) / ((float) REQUEST_IMAGE_WIDTH);
//            float heightRatio = ((float) rotatedHeight) / ((float) REQUEST_IMAGE_WIDTH);
//            float maxRatio = Math.max(widthRatio, heightRatio);
//
//            // Create the bitmap from file
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = (int) maxRatio;
//            srcBitmap = BitmapFactory.decodeStream(is, null, options);
//        } else {
//            srcBitmap = BitmapFactory.decodeStream(is);
//        }
//        is.close();
//
//        /*
//         * if the orientation is not 0 (or -1, which means we don't know), we
//         * have to do a rotation.
//         */
//        if (orientation > 0) {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(orientation);
//
//            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
//                    srcBitmap.getHeight(), matrix, true);
//        }
//
//        String type = context.getContentResolver().getType(photoUri);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        if (type.equals("image/png")) {
//            srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        } else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
//            srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        }
//        byte[] bMapArray = baos.toByteArray();
//        baos.close();
//        return BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
//    }
//
//    public static int getOrientation(Context context, Uri photoUri) {
//        /* it's on the external media. */
//        Cursor cursor = context.getContentResolver().query(photoUri,
//                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);
//
//        if (cursor.getCount() != 1) {
//            return -1;
//        }
//
//        cursor.moveToFirst();
//        return cursor.getInt(0);
//    }

}
