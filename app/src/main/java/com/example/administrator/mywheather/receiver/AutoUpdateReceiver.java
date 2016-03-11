package com.example.administrator.mywheather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.administrator.mywheather.service.AutoUpdateService;

/**
 * Created by Administrator on 2016/3/10.
 */
public class AutoUpdateReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {            //在onReceive()方法中，获取随广播而来的Intent数据
        Intent i = new Intent(context, AutoUpdateService.class);     //实现后台定时更新功能
        context.startService(i);
    }
}
