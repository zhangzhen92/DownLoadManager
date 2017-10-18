# 使用DownloadManager下载App

第一次写markdown,想想还有点小激动。。。。

**android中经常会遇到app自更新这样的需求，市场上大致上分为软件内部可视化下载更新和通知栏下载更新。**
## 软件内部可视化更新
 1. 通过访问服务器获取下载的地址；
 2. 通过网络请求获取下载的app文件；
 3. 安装该文件。


 ## 常见的方式如下：
 ![](http://oxzz05lat.bkt.clouddn.com/update_in_app.png)
 
 **此处要介绍的是通过通知栏进行下载，一直对通知栏下载没接触，所以专门研究了下，发现so easy....**
 
```
 manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
 //设置下载的地址
 Uri uri = Uri.parse(DOWNLOAD_PATH);
 DownloadManager.Request request = new DownloadManager.Request(uri);
 //设置下载时手机的网络的类型(WIFI或者手机网络)
 request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE |    DownloadManager.Request.NETWORK_WIFI);
 setNotification(request);
 String apkName = uri.getLastPathSegment();
  //设置存放的位置
 setDownLoadPath(request, apkName);
 downLoadId = manager.enqueue(request);
```
**此时下载新的App的路径可以自己定义**

 ```
 private void setDownLoadPath(DownloadManager.Request request, String apkName) {
  //下载到 android/data/包名/file/downloads文件夹下(软件卸载下载的文件自动清除)
  request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, apkName);
  //下载到SD卡/apk文件夹下
  // request.setDestinationInExternalPublicDir("/apk/",apkName);
    }
```
### 通知栏显示的样式
![](http://oxzz05lat.bkt.clouddn.com/notification_small.png)
 
 这是常规的样式，可以用户通过设置从而控制其他的效果
   ```
 private void setNotification(DownloadManager.Request request) {
    request.setTitle("这是title");
    request.setDescription("描述信息");
    //设置通知栏是否显示
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);     
     }
    //显示下载界面
    request.setVisibleInDownloadsUi(false);
    }
```
通过mananger.enqueue(request)获取到的id，从而还可以控制取消当前任务以及查询当前任务的状态。此时取消任务则清空当前文件（无论是否下载完成，则把下载的东西全部清空）。若想移除任务直接用 **manager.remove(id);** 则可取消任务。也可以通过id获取到当前下载的任务的状态：

```
DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downLoadId);
        Cursor cursor = manager.query(query);
        if (null == cursor) {
            Log.d(TAG, "getTaskMessage: can't found");
        } else {
            Log.d(TAG, "getTaskMessage: " + getStatus(cursor));
        }
```

```
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
```

只是一个简单的downloadmanager使用，有兴趣的可以看下




