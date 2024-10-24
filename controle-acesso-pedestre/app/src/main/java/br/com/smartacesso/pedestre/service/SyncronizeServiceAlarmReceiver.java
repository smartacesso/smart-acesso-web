package br.com.smartacesso.pedestre.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class SyncronizeServiceAlarmReceiver extends BroadcastReceiver {

    public SyncronizeServiceAlarmReceiver(){

    }

    @Override
    public void onReceive(Context context, Intent arg1) {

        try {

            if(SyncronizeService.isSync == false) {
                Intent service = new Intent(context, SyncronizeService.class);
                service.putExtra("sync", true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    ContextCompat.startForegroundService(context, service);
                else
                    context.startService(service);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}