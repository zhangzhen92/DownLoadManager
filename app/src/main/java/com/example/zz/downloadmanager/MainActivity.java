package com.example.zz.downloadmanager;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.zz.downloadmanager.service.DownLoadService;
/**
 * 类描述：测试类
 * 创建人：zz
 * 创建时间：2017/10/17 17:32
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView textUpdate;
    private Button buttonCancel;
    private DownLoadService downLoadService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 初始化UI
     */
    private void initView() {
        textUpdate = ((TextView) findViewById(R.id.textview_update));
        textUpdate.setOnClickListener(this);
        buttonCancel = ((Button) findViewById(R.id.button_cancel));
        buttonCancel.setOnClickListener(this);
        Intent intent = new Intent(MainActivity.this, DownLoadService.class);
        bindService(intent,conn,BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textview_update:
                downLoadService.startDownLoad();
                break;
            case R.id.button_cancel:
                downLoadService.cancelDownLoad();
                break;
        }
    }




    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downLoadService = ((DownLoadService.MyBiner)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
