package com.safshekan.parkban.helper;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ExportDataBase extends AsyncTask<Object, Integer, Integer> {

    private final File appDbFile;
    private final String exportFileName;

    public ExportDataBase(File appDbFile, String exportFileName) {
        this.appDbFile = appDbFile;
        this.exportFileName = exportFileName;
    }

    @Override
    protected Integer doInBackground(Object... params) {
        try {

            String path = Environment.getExternalStorageDirectory().getPath() + "/TestDb/";
            File dbFile = new File(path, "parkban_db");

            checkAndCreateDirectory(path);

            copyFile(appDbFile, dbFile);


        } catch (Exception e) {
            return 0;
        }
        return 1;
    }

    public static void checkAndCreateDirectory(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdir())
                throw new IOException("mkdir Error:" + path);
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                throw new IOException("copyFile Error", e);
            }
        } else {
            try {
                doCopyFile(sourceFile, destFile);
            } catch (Exception e) {
                throw new IOException("copyFile Error", e);
            }
        }
    }

    private static void doCopyFile(File sourceFile, File destFile) throws IOException {
        try (InputStream in = new FileInputStream(sourceFile)) {
            try (OutputStream out = new FileOutputStream(destFile)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }
}

