package fr.yncrea.scorpion.utils.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import fr.yncrea.scorpion.ScorpionApplication;
import fr.yncrea.scorpion.utils.PreferenceUtils;

public class NotificationUtils {
    public static enum TYPE {
        COURSE_NOTIFICATION,
        GRADE_NOTIFICATION,
        HOMEWORK_NOTIFICATION
    }

    public static void clearAllNotifications() {
        ArrayList<ScorpionNotification> notifications = (ArrayList<ScorpionNotification>) PreferenceUtils.getNotifications();
        PreferenceUtils.setNotifications(new ArrayList<>());

        for(ScorpionNotification notif : notifications) {
            AlarmManager alarmManager = (AlarmManager) ScorpionApplication.getContext().getSystemService(Context.ALARM_SERVICE);

            Intent notifyIntent = new Intent(ScorpionApplication.getContext(), ReminderBroadcast.class);
            notifyIntent.setAction(notif.getId().toString());
            PendingIntent pendingIntent = PendingIntent.getBroadcast
                    (ScorpionApplication.getContext(), 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            if(pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }
}
