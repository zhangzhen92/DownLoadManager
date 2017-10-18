package com.example.zz.downloadmanager.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * 类描述：DownLoadManager下载service类
 * 创建人：zz
 * 创建时间： 2017/10/17 13:15
 */


public class DownLoadService extends Service {
    private static final String TAG = "DownLoadService";
    private DownloadManager manager;
    private DownLoadSReceiver receiver;
    private static final String DOWNLOAD_PATH = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
    private long downLoadId;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        initManager();
        return new MyBiner();
    }

    public class MyBiner extends Binder {
        public DownLoadService getService() {
            return DownLoadService.this;
        }
    }

    /**
     * 开始下载
     */
    public void startDownLoad() {
        initManager();
    }


    /**
     * 初始化downLoadManager
     */
    private void initManager() {
        receiver = new DownLoadSReceiver();                                //下载完成的广播
        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        //设置下载的地址
        Uri uri = Uri.parse(DOWNLOAD_PATH);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        //设置下载时手机的网络的类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        setNotification(request);
        //设置存放的位置
        String apkName = uri.getLastPathSegment();
        setDownLoadPath(request, apkName);
        downLoadId = manager.enqueue(request);
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    /**
     * 设置下载Notification
     *
     * @param request
     */
    private void setNotification(DownloadManager.Request request) {
        request.setTitle("这是title");
        request.setDescription("描述信息");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);     //设置通知栏是否显示
        }
        //显示下载界面
        request.setVisibleInDownloadsUi(false);
    }

    /**
     * 设置下载路径
     *
     * @param request
     * @param apkName
     */
    private void setDownLoadPath(DownloadManager.Request request, String apkName) {
        //下载到 android/data/包名/file/downloads文件夹下(软件卸载下载的文
        // 件自动清除)
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, apkName);
        //下载到SD卡/apk文件夹下
        // request.setDestinationInExternalPublicDir("/apk/",apkName);
    }


    class DownLoadSReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //判断是否下载完成的action
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                    Uri uriForDownloadedFile = manager.getUriForDownloadedFile(downloadId);
                    installApkNew(uriForDownloadedFile);

                }
            }
        }
    }

    //安装apk
    protected void installApkNew(Uri uri) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    public void cancelDownLoad() {
        manager.remove(downLoadId);            //如果移除当前任务则移除所有下载的东西，包括通知也移除
    }


    /**
     * 查询当前的下载任务在队里中的状态
     *
     * @return
     */
    public void getTaskMessage() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downLoadId);
        Cursor cursor = manager.query(query);
        if (null == cursor) {
            Log.d(TAG, "getTaskMessage: can't found");
        } else {
            Log.d(TAG, "getTaskMessage: " + getStatus(cursor));
        }
    }

    /**
     * 查询状态
     *
     * @param c
     * @return
     */
    private String getStatus(Cursor c) {
        switch (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_FAILED:
                return " failed";
            case DownloadManager.STATUS_PAUSED:
                return " paused";
            case DownloadManager.STATUS_PENDING:
                return " pending";
            case DownloadManager.STATUS_RUNNING:
                return "Download in progress!";
            case DownloadManager.STATUS_SUCCESSFUL:
                return " finished";
            default:
                return "Unknown Information";
        }
    }
}


