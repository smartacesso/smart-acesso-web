package br.com.smartacesso.pedestre.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import br.com.smartacesso.pedestre.R;
import br.com.smartacesso.pedestre.activity.MainActivity_;
import br.com.smartacesso.pedestre.model.bo.PedestreBO;
import br.com.smartacesso.pedestre.model.entity.Pedestre;
import br.com.smartacesso.pedestre.model.network.NetworkCall;
import br.com.smartacesso.pedestre.utils.ServiceUtil;
import br.com.smartacesso.pedestre.utils.SharedPreferencesUtil;

/**
 * Created by gustavo on 10/03/17.
 */

public class SyncronizeService extends Service {


    private SyncronizeServiceTimerTask myTask = null;
    private Timer myTimer = null;

    private PendingIntent pendingIntent;
    private AlarmManager manager;

    public static final String TAG_SYNC = "SyncronizeService";
    public static final SimpleDateFormat sdfSync = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private static final int SYNC_ALL = 5000;
    private static final int SYNC     = 5001;

    public static Boolean isSync = false;

    public static final String[] SYNC_SERVICES
            = new String[]{"sincronizaDadosPessoais",
                           "sincronizaFacial"};

    public static final String BROADCAST_ACTION = "SyncronizeService.atualiza";
    public final Handler handler = new Handler();
    public Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            Log.i(TAG_SYNC, "Envia: " + BROADCAST_ACTION);
            Intent intent = new Intent(BROADCAST_ACTION);
            sendBroadcast(intent);
        }
    };



    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {

        Log.i(TAG_SYNC, "OnCreate.");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ServiceUtil.createNonImportantNotificationChannel(this);

            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())
                    .putExtra(Settings.EXTRA_CHANNEL_ID, ServiceUtil.NON_IMPORTANT_CHANNEL_ID);

            android.app.Notification myNotification = new NotificationCompat.Builder(this, ServiceUtil.NON_IMPORTANT_CHANNEL_ID)
                    .setContentTitle(getString(R.string.title_dados_segundo_plano))
                    .setContentText(getString(R.string.label_toque_detalhes))
                    //.setSmallIcon(R.drawable.ic_stat_new_program_train)
                    .setPriority(android.app.Notification.PRIORITY_DEFAULT)
                    .setContentIntent(PendingIntent.getActivity(
                            this,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    ))
                    .build();

            startForeground(SYNC, myNotification);

        }

        /*
         * inicaliza serviço com alarm
         */
        Intent alarmIntent = new Intent(this, SyncronizeServiceAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);


        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), StartTasksService.TIMER_EXEC_SYNCRONIZE, pendingIntent);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG_SYNC, "OnStart.");


        if(intent != null) {
            boolean syncAll = intent.getBooleanExtra("syncAll", false);
            boolean sync    = intent.getBooleanExtra("sync", false);

            if(sync){

                try {

                    Log.i(TAG_SYNC, "Sincroniza dados.");

                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                sincroniza(SyncronizeService.this, true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            SyncronizeService.this.handler.removeCallbacks(SyncronizeService.this.sendUpdatesToUI);
                            SyncronizeService.this.handler.postDelayed(SyncronizeService.this.sendUpdatesToUI, 1);

                        }
                    }.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else if (syncAll) {

                try {

                    //cria notificação de sincronização
                    criaNotificacaoSincronizacaoTotal(this);

                    Date uCriacao = sdfSync.parse("01/01/2020 00:00:00"); //sincronizacao inicial
                    SimpleDateFormat sdfSync = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    for (String syncService : SyncronizeService.SYNC_SERVICES)
                        SharedPreferencesUtil
                                .setSharePreference(syncService,
                                        sdfSync.format(uCriacao), this);

                    //realiza sincronização
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                sincroniza(SyncronizeService.this, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //ajusta todas as datas do serviço para agora
                            for (String syncService : SyncronizeService.SYNC_SERVICES)
                                SharedPreferencesUtil
                                        .setSharePreference(syncService,
                                                sdfSync.format(new Date()), SyncronizeService.this);

                            //remove notificação de sincronização
                            stopForeground(true);

                            criaNotificacaoSincronizacaoCompleta(SyncronizeService.this);
                        }
                    }.start();


                } catch (Exception e) {
                    e.printStackTrace();
                    //remove notificação de sincronização
                    stopForeground(true);
                }
            }
        }



        return START_STICKY_COMPATIBILITY;
    }

    /**
     * Task para envio de dados para o servidor.
     *
     * @author gustavo.diniz
     */
    public static class SyncronizeServiceTimerTask extends TimerTask {
        SyncronizeService context;
        public SyncronizeServiceTimerTask(SyncronizeService context) {
            this.context = context;
        }
        public void run() {
            try {
                Log.i(TAG_SYNC, "Sincroniza dados.");
                SyncronizeService.sincroniza(context, true);

                context.handler.removeCallbacks(context.sendUpdatesToUI);
                context.handler.postDelayed(context.sendUpdatesToUI, 1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void sincroniza(Context context, boolean notifica) throws Exception {

        if (NetworkCall.verificaConexao(context)
                && NetworkCall.somenteUsandoWifi(context)
                && SharedPreferencesUtil.getLoggedUser(context) != null
                && !isSync) {

            isSync = true;

            sincronizaDadosPessoais(context);

            SharedPreferencesUtil
                    .setSharePreference("lastSync",
                            sdfSync.format(new Date()), context);

            isSync = false;

        }

    }

    public static void sincronizaDadosPessoais(Context context) {
        Log.i(TAG_SYNC, "sincronizaDadosPessoais.");
        try {

            String lastSync = SharedPreferencesUtil
                    .getSharePreference("sincronizaDadosPessoais", context);
            Date dLastSync = lastSync != null
                    ? sdfSync.parse(lastSync)
                    : SharedPreferencesUtil
                    .getLoggedUser(context).getDataCriacao();

            //busca avaliações depois da data
            //da última sincronização
            PedestreBO bo = new PedestreBO(context);

            //busca alterados e criados depois
            //da data da ultima sincronização
            Pedestre uSync = bo.get(
                    SharedPreferencesUtil.getLoggedUser(context)
                            .getToken().toString(), dLastSync);

            if (uSync != null) {

                SharedPreferencesUtil
                        .setSharePreference(SharedPreferencesUtil.SharedPreferencesKey.LOGGED_USER,
                                NetworkCall.getGson().toJson(uSync), context);

                SharedPreferencesUtil
                        .setSharePreference("sincronizaDadosPessoais",
                                sdfSync.format(new Date()), context);
            }

        }catch (Exception e){
            //envia console
            e.printStackTrace();
        }

    }

    private static void criaNotificacaoSincronizacaoTotal(Context context) {

        String msg = context.getString(R.string.msg_aguarde_sincronizacao);

        ServiceUtil.createNotificationChannel(context);
        android.app.Notification myNotification = new NotificationCompat.Builder(context, ServiceUtil.CHANNEL_ID)
                .setContentTitle(context.getString(R.string.title_sincronizando))
                .setContentText(msg)
                .setSmallIcon(Build.VERSION.SDK_INT >  Build.VERSION_CODES.KITKAT_WATCH
                        ? R.drawable.ic_stat_sync: R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round))
                .setAutoCancel(false)
                .setOngoing(true)
                .setWhen(new Date().getTime())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .build();

        ((SyncronizeService)context)
                .startForeground(SYNC_ALL, myNotification);


    }

    private static void criaNotificacaoSincronizacaoCompleta(Context context){

        ServiceUtil.createNotificationChannel(context);
        android.app.Notification myNotification = new NotificationCompat.Builder(context, ServiceUtil.CHANNEL_ID)
                .setContentTitle(context.getString(R.string.title_sincronizacao_finalizada))
                .setContentText(context.getString(R.string.msg_sincronizacao_completa_p1) + context.getString(R.string.app_name) + context.getString(R.string.msg_sincronizacao_completa_p2))
                .setSmallIcon(Build.VERSION.SDK_INT >  Build.VERSION_CODES.KITKAT_WATCH
                        ? R.drawable.ic_stat_done_all: R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round))
                .setAutoCancel(true)
                .setOngoing(false)
                .setWhen(new Date().getTime())
                .setContentIntent(criaIntent(context, null))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVibrate(new long[] { 1000, 1000})
                .build();

        comportamentoNotificacao(context, myNotification);

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(SYNC_ALL, myNotification);
    }

    private static PendingIntent criaIntent(Context context, String tela) {
        Intent resultIntent = new Intent(context, MainActivity_.class);
        resultIntent.putExtra("startOn", tela);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    private static void comportamentoNotificacao(Context context, android.app.Notification myNotification) {
//        String somAtivado = SharedPreferencesUtil
//                .getSharePreference("somAtivado", context);
//        if(somAtivado == null || Boolean.valueOf(somAtivado))
//            myNotification.defaults |= android.app.Notification.DEFAULT_SOUND;
//
//        String vibracaoAtivada = SharedPreferencesUtil
//                .getSharePreference("vibracaoAtivada", context);
//        if(vibracaoAtivada == null || Boolean.valueOf(vibracaoAtivada))
//            myNotification.defaults |= android.app.Notification.DEFAULT_VIBRATE;
    }

}