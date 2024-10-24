package com.protreino.luxandapp.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;

import java.util.List;

public class ServiceUtil {

	public static final String CHANNEL_ID 				= "global";
	public static final String NON_IMPORTANT_CHANNEL_ID = "nonImportantGlobal";
	public static final String DEFAULT_CHANNEL_ID		= "defaultGlobal";

	public static boolean isServiceRunning(Context context, String servicename) {
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (servicename.equals(service.service.getClassName())) {
	            return true;
	        }
	    }

		final List<RunningServiceInfo> services = manager.getRunningServices(Integer.MAX_VALUE);
		for (RunningServiceInfo runningServiceInfo : services) {
			if (runningServiceInfo.service.getClassName().equals(servicename)){
				return true;
			}
		}


	    return false;
	}

	public static void createNotificationChannel(Context context) {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = "Notificações gerais";
			String description = "Exibe todas as notificações do sistema";
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance) ;
			channel.setDescription(description);

			channel.enableLights(true);
			channel.enableVibration(true);

			AudioAttributes attributes = new AudioAttributes.Builder()
					.setUsage(AudioAttributes.USAGE_NOTIFICATION)
					.build();

			channel.setSound(RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), attributes);

			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
	}

	public static void createNonImportantNotificationChannel(Context context){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

			CharSequence name = "Serviços de atualização";
			String description = "Atualiza dados do app em segundo plano";
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel(NON_IMPORTANT_CHANNEL_ID, name, importance);
			channel.setDescription(description);

			channel.enableLights(false);
			channel.enableVibration(false);
			channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            channel.setShowBadge(false);

			channel.setSound(null, null);

			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);

		}
	}

	public static void createDefaultNotificationChannel(Context context){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

			CharSequence name = "Ações do treino";
			String description = "Dados de cronometro e tempo de descanso";
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel(DEFAULT_CHANNEL_ID, name, importance);
			channel.setDescription(description);

			channel.enableLights(false);
			channel.enableVibration(false);
			channel.setShowBadge(true);

			channel.setSound(null, null);

			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);

		}
	}

}
