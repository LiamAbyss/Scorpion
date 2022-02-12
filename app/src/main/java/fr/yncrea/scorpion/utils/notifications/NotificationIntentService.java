package fr.yncrea.scorpion.utils.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

import fr.yncrea.scorpion.R;
import fr.yncrea.scorpion.SplashScreenActivity;
import fr.yncrea.scorpion.utils.PreferenceUtils;

public class NotificationIntentService extends Service {

    private static final String CHANNEL_ID = "SCORPION_NOTIFICATION";
    private static final String channelName = "Notifications de cours";
    private String title;
    private String body;
    private int id;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    private void startMyOwnForeground() {
        Log.d("NOTIF", "Foreground started");
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O && manager.getNotificationChannel(CHANNEL_ID) == null) {
            NotificationChannel chan = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(chan);
        }

        Intent notifyIntent = new Intent(this, SplashScreenActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Notification notification = builder.build();
        Log.d("NOTIF", "Notification built");

        // Prevent most crashes
        startForeground(id, new NotificationCompat.Builder(this, CHANNEL_ID).build());
        stopForeground(true);
        stopSelf();

        manager.notify(id, notification);
        /*startForeground(id, notification);
        stopForeground(false);*/
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(intent.hasExtra("ID")) {
            Long id = intent.getLongExtra("ID", 0);
            ArrayList<ScorpionNotification> notifications = (ArrayList<ScorpionNotification>) PreferenceUtils.getNotifications();
            for(ScorpionNotification notif : notifications) {
                if(notif.getId() == id) {
                    title = notif._title;
                    body = notif._body;
                    this.id = id.intValue();
                    break;
                }
            }
        }
        startMyOwnForeground();
        return START_STICKY_COMPATIBILITY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, ReminderBroadcast.class);
        this.sendBroadcast(broadcastIntent);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}