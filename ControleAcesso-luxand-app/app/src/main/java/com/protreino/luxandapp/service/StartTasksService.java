package com.protreino.luxandapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.protreino.luxandapp.util.ServiceUtil;
import com.protreino.luxandapp.util.SharedPreferencesUtil;


public class StartTasksService extends BroadcastReceiver {

	public static final String BROADCAST_ACTION = "br.com.smartacesso.luxandapp";
    public static final int TIMER_EXEC_SYNCRONIZE = 1000*60*60; // a cada hora

	@Override
	public void onReceive(Context context, Intent intent) {

        //inicializa todos somente
        //se algum usuário estiver logado
        if(SharedPreferencesUtil.getLoggedUser(context) != null)
            inicializa(context);


	}

	public static void inicializa(Context context){
        /*
		 * inicializa serviço de sincronização de dados
		 */
        if(!ServiceUtil.isServiceRunning(context,
                SyncronizeService.class.getName())) {
            Intent service = new Intent(context, SyncronizeService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(service);
            else
                context.startService(service);
        }
    }

    public static void para(Context context){
         /*
		 * para serviço de sincronização de dados
		 */
        if(ServiceUtil.isServiceRunning(context,
                SyncronizeService.class.getName())) {
            Intent service = new Intent(context, SyncronizeService.class);
            context.stopService(service);
        }
    }

}
