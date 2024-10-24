package br.com.smartacesso.pedestre.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import br.com.smartacesso.pedestre.utils.ServiceUtil;
import br.com.smartacesso.pedestre.utils.SharedPreferencesUtil;

public class StartTasksService extends BroadcastReceiver {

	public static final String BROADCAST_ACTION = "com.startjob.pro_treino.activities.services.StartTasksService";
    public static final int TIMER_EXEC_SYNCRONIZE = 1000*60*5; // 5 minutos

	@Override
	public void onReceive(Context context, Intent intent) {

        //inicializa todos somente
        //se algum usuário estiver logado
        if(SharedPreferencesUtil.getLoggedUser(context) != null)
            inicializa(context);


	}

	public static void inicializa(Context context){
        /*
		 * inicializa serviço de monitoramento cardiaco
		 */
//        if(!ServiceUtil.isServiceRunning(context,
//                HeartMonitorService.class.getName())) {
//            Intent service = new Intent(context, HeartMonitorService.class);
//            service.putExtra("start-current", true);
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                context.startForegroundService(service);
//            else
//                context.startService(service);
//        }
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
		 * para serviço de monitoramento cardiaco
		 */
//        if(ServiceUtil.isServiceRunning(context,
//                HeartMonitorService.class.getName())) {
//            Intent service = new Intent(context, HeartMonitorService.class);
//            service.putExtra("stop-current", true);
//            context.stopService(service);
//        }
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
