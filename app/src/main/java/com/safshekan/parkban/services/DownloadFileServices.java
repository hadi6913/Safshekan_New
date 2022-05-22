package com.safshekan.parkban.services;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.safshekan.parkban.R;
import com.safshekan.parkban.helper.ShowToast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFileServices extends AsyncTask<String, String, String> {

    public static String root = Environment.getExternalStorageDirectory().toString();
    public static String filename = "/Parkban.apk";

    Context mContext;

    public DownloadFileServices(Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(String... f_url) {
        String errorMessage = "";
        int count;
        try {
            URL url = new URL(f_url[0]);
            URLConnection connection = url.openConnection();
            connection.connect();

            int lengthOfFile = connection.getContentLength();

            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            OutputStream output = new FileOutputStream(root + filename);

            byte data[] = new byte[1024];
            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lengthOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            if (e.toString().contains("FileNotFoundException"))
                errorMessage = mContext.getResources().getString(R.string.apk_not_found);
            else
                errorMessage = mContext.getResources().getString(R.string.unhandled_error_from_server);
        }
        return errorMessage;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String errorMessage) {
        if (errorMessage.length() > 0)
             ShowToast.getInstance().showErrorStringMsg(mContext, errorMessage);
        else {
            File toInstall = new File(root, filename);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName()+ ".provider", toInstall);
                Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent.setData(apkUri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mContext.startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(toInstall), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }

        }
    }
}
