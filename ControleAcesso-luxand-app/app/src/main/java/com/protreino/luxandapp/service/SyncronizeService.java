package com.protreino.luxandapp.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.protreino.luxandapp.R;
import com.protreino.luxandapp.main.App;
import com.protreino.luxandapp.rest.FacialRest;
import com.protreino.luxandapp.ui.home.HomeActivity;
import com.protreino.luxandapp.util.AppConstants;
import com.protreino.luxandapp.util.NetworkCall;
import com.protreino.luxandapp.util.ServiceUtil;
import com.protreino.luxandapp.util.SharedPreferencesUtil;
import com.protreino.services.to.SendReceiveTrackerParams;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;


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
            = new String[]{"sincronizaArquivoReconhecimento"};

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
                        }
                    }.start();

                    this.handler.removeCallbacks(this.sendUpdatesToUI);
                    this.handler.postDelayed(this.sendUpdatesToUI, 1);

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
                && SharedPreferencesUtil.getLoggedUser(context) != null
                && !isSync) {

            isSync = true;

            sincronizaArquivoReconhecimento(context);

            SharedPreferencesUtil
                    .setSharePreference("lastSync",
                            sdfSync.format(new Date()), context);

            isSync = false;

        }

    }

    private static void sincronizaArquivoReconhecimento(Context context) {

        Log.i(TAG_SYNC, "sincronizaArquivoReconhecimento.");
        try {

            String lastSync = SharedPreferencesUtil
                    .getSharePreference("sincronizaArquivoReconhecimento", context);
            Date dLastSync = lastSync != null
                    ? sdfSync.parse(lastSync)
                    : new Date();

            String idClient = SharedPreferencesUtil.getLoggedUser(context).getIdClient();
            FacialRest facialRest = NetworkCall.getDefaultConfig().create(FacialRest.class);
            Call<SendReceiveTrackerParams> response = facialRest.requestBackupFileTracker(idClient);

            if(response != null ) {
                Response<SendReceiveTrackerParams> rServer = response.execute();
                SendReceiveTrackerParams r = null;
                if (rServer.errorBody() == null) {
                    //processa registro encontrado
                    r = rServer.body();
                    if(r.getFileTracker() != null){

                        //grava novo arquivo
                        String mPath = Environment.getExternalStorageDirectory().toString() + "/smartacesso";

                        File folder = new File(mPath);
                        if(!folder.isDirectory())
                            folder.mkdir();

                        File trakerFile = new File(mPath  + "/tracker.dat");

                        FileOutputStream outputStream = new FileOutputStream(trakerFile);
                        outputStream.write(r.getFileTracker());
                        outputStream.flush();
                        outputStream.close();

                        //indica data de sincronizacao do arquivo
                        SharedPreferencesUtil
                                .setSharePreference("sincronizaArquivoReconhecimento",
                                        sdfSync.format(new Date()), context);

                    }
                }else{
                    throw new Exception(rServer.errorBody().string());
                }
            }
        }catch (Exception e){
            //envia console
            e.printStackTrace();
        }


    }

    private static void criaNotificacaoSincronizacaoCompleta(Context context){

        ServiceUtil.createNotificationChannel(context);
        android.app.Notification myNotification = new NotificationCompat.Builder(context, ServiceUtil.CHANNEL_ID)
                .setContentTitle(context.getString(R.string.title_sincronizacao_finalizada))
                .setContentText(context.getString(R.string.msg_sincronizacao_completa_p1))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round))
                .setAutoCancel(true)
                .setOngoing(false)
                .setWhen(new Date().getTime())
                .setContentIntent(criaIntent(context, null))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVibrate(new long[] { 1000, 1000})
                .build();


        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(SYNC_ALL, myNotification);
    }

    private static void criaNotificacaoSincronizacaoTotal(Context context) {

        String msg = context.getString(R.string.msg_aguarde_sincronizacao);

        ServiceUtil.createNotificationChannel(context);
        android.app.Notification myNotification = new NotificationCompat.Builder(context, ServiceUtil.CHANNEL_ID)
                .setContentTitle(context.getString(R.string.title_sincronizando))
                .setContentText(msg)
                .setSmallIcon(R.mipmap.ic_launcher_round)
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

    private static PendingIntent criaIntent(Context context, String tela) {
        Intent resultIntent = new Intent(context, HomeActivity.class);
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


}