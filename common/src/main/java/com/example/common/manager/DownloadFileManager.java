package com.example.common.manager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.example.common.util.FileUtil;
import com.example.common.util.Logger;
import com.example.common.util.PermissionManager;

import java.io.File;

public class DownloadFileManager {
    private static DownloadManager downloadManager;
    //下载ID
    private static long downloadId;
    public static final String testUrl = "https://cache1.didapinche.com/get-taxi-meter/DidaMeter-Production-xian_daohang-v2.3.5.apk";

    public static void download(Context context, String downLoadUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downLoadUrl));
        //设置漫游条件下是否可以下载
        request.setAllowedOverRoaming(false);
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //设置通知标题
        request.setTitle("通知标题，随意修改");
        //设置通知标题message
        request.setDescription("新版zip下载中...");
        request.setVisibleInDownloadsUi(true);
        //设置文件存放路径
        File file = new File(context.getExternalFilesDir(""), "DidaMeter-Production-xian_daohang-v2.3.5.apk");
        Logger.d(file.getAbsolutePath());
        if(FileUtil.fileIsExists(file.getPath())){
            Logger.d("文件已存在");
        }
        if(PermissionManager.checkPermissions(context,PermissionManager.ReadAndWritePermissions)){
            Logger.d("有文件读写权限");
        }
        else{
            Logger.d("无文件读写权限");
        }
        request.setDestinationUri(Uri.fromFile(file));
//                pathstr = file.getAbsolutePath();
        if (downloadManager == null)
            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
        if (downloadManager != null) {
            downloadId = downloadManager.enqueue(request);
        }
        //注册广播监听下载状况
        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    //广播监听下载的各个状态
    public static BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkStatus();
        }
    };

    //检查下载状态
    private static void checkStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        //通过下载的id查找
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                //下载暂停
                case DownloadManager.STATUS_PAUSED:{
                    Logger.d("下载暂停");
                }
                    break;
                //下载延迟
                case DownloadManager.STATUS_PENDING:{
                    Logger.d("下载延迟，等待开始");
                }
                    break;
                //正在下载
                case DownloadManager.STATUS_RUNNING:{
                    Logger.d("正在下载");
                }
                    break;
                //下载完成
                case DownloadManager.STATUS_SUCCESSFUL:
                    //下载完成
                    Logger.d("下载完成");
                    cursor.close();
                    break;
                //下载失败
                case DownloadManager.STATUS_FAILED:
                    Logger.d("下载失败");
                    cursor.close();
                    break;
            }
        }
    }

    /**
     * 下载前先移除前一个任务，防止重复下载
     *
     * @param downloadId
     */
    public static void clearCurrentTask(long downloadId, Context context) {
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        try {
            dm.remove(downloadId);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    /*获取下载进度*/
    private int getDownloadPercent(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int downloadBytesIdx = c.getColumnIndexOrThrow(
                    DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
            int totalBytesIdx = c.getColumnIndexOrThrow(
                    DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
            long totalBytes = c.getLong(totalBytesIdx);
            long downloadBytes = c.getLong(downloadBytesIdx);
            return (int) (downloadBytes * 100 / totalBytes);
        }
        return 0;
    }

    /*获取下载进度*/
    private int getDownloadPercent(Cursor c) {
        if (c.moveToFirst()) {
            int downloadBytesIdx = c.getColumnIndexOrThrow(
                    DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
            int totalBytesIdx = c.getColumnIndexOrThrow(
                    DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
            long totalBytes = c.getLong(totalBytesIdx);
            long downloadBytes = c.getLong(downloadBytesIdx);
            return (int) (downloadBytes * 100 / totalBytes);
        }
        return 0;
    }
}
