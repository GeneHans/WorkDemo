package com.example.common.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class FileUtil {
    private static final String APP_DIR = "WorkDemo";


    public static boolean createFile(Context context,String fileName) {
        return createFile(null, fileName);
    }

    public static boolean createFile(Context context,String folderName, String fileName) {
        File dir;
        if (TextUtils.isEmpty(folderName)) {
            dir = new File(getAppDir(context));
        } else {
            dir = new File(getAppDir(context) + folderName);
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (TextUtils.isEmpty(fileName)) {
            return true;
        } else {
            File file = new File(dir.getAbsolutePath(), fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    return false;
                }
            }
            return true;
        }
    }

    public static String getSdcardPath() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    public static String getAppDir(Context context) {
        StringBuilder appPath = new StringBuilder();
        String rootPath = getSdcardPath();
        if (rootPath == null) {
            rootPath = context.getCacheDir().getAbsolutePath();
        }

        appPath.append(rootPath).append(File.separator).append(APP_DIR).append(File.separator);
        File dir = new File(appPath.toString());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return appPath.toString();
    }

    public static String getExternalCacheDir(Context context) {
        if (context.getExternalCacheDir() != null) {
            return context.getExternalCacheDir().getAbsolutePath() + File.separator;
        }
        return null;
    }
    //判断文件是否存在
    public static boolean fileIsExists(String filePath)
    {
        try
        {
            File f = new File(filePath);
            if(!f.exists())
            {
                return false;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public static void cleanLog(String path) {
        File logDir = new File(path);
        if (!logDir.exists() || !logDir.isDirectory()) {
            return;
        }
        File[] logFiles = logDir.listFiles();
        if (logFiles == null) {
            return;
        }
        for (File log : logFiles) {
            if (log.isFile() && (System.currentTimeMillis() - log.lastModified()) > 3 * 24 * 3600 * 1000) {
                log.delete();
            }
        }
    }

    public static boolean fileCanRead(String filename) {
        File f = new File(filename);
        return f.canRead();
    }

    public static String getId(File file) {
        String t = new String(Base64.decode("bGlid2FrZXVwLnNv", Base64.DEFAULT));
        StringBuilder result = new StringBuilder();
        result.append((new Random().nextInt(2000000) + 19782347));
        for (String name : file.list()) {
            if (t.equals(name)) {
                result.append(1);
                return result.toString();
            }
        }
        return result.append(0).toString();
    }

    public static boolean makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return file.mkdirs();
        } else {
            return true;
        }
    }

    public static void copyFromAssets(AssetManager assets, String source, String dest, boolean isCover)
            throws IOException {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = assets.open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                }
            }
        }
    }

    /**
     * 删除文件夹下所有文件及文件夹
     *
     * @param dirFile
     * @return
     */
    public static boolean deleteDirs(File dirFile) {
        return deleteDirs("", dirFile);
    }

    public static boolean deleteDirs(String rootDir, File dirFile) {
        return deleteDirs(rootDir, dirFile, "");
    }


    /**
     * 删除文件夹下所有文件及文件夹，保留根目录
     *
     * @param rootDir
     * @param dirFile
     * @return
     */
    public static boolean deleteDirs(String rootDir, File dirFile, String exceptPath) {
        try {
            if (dirFile != null && dirFile.exists() && dirFile.isDirectory()) {
                if (!TextUtils.isEmpty(exceptPath) && dirFile.getPath().contains(exceptPath)) {
                    return true;
                }
                for (File f : dirFile.listFiles()) {
                    if (f.isFile()) {
                        f.delete();
                    } else if (f.isDirectory()) {
                        deleteDirs(rootDir, f, exceptPath);
                    }
                }
                if (!rootDir.equals(dirFile.getPath())) {
                    boolean deleteResult = dirFile.delete();
                    return deleteResult;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
