package fr.yncrea.scorpion.utils.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import fr.yncrea.scorpion.ScorpionApplication;
import fr.yncrea.scorpion.utils.PreferenceUtils;

public class ScorpionNotification {
    public String _title;
    public String _body;
    public Long _time;
    private Long _id;
    private Context _context;
    private static NotificationUtils.TYPE _type;
    //private static String channelName = "Notifications de cours";
    private static final String CHANNEL_ID = "SCORPION_NOTIFICATION";

    /**
     * @param type Type of the notification (course, grade, homework...)
     * @param title Title of the notification
     * @param body Body of the notification
     * @param time Time when the notification should trigger
     */
    public ScorpionNotification(Context context, NotificationUtils.TYPE type, String title, String body, Long time) {
        _type = type;
        _title = title;
        _body = body;
        _time = time;
        _id = 0L;
        _context = context;
    }

    /**
     * Shouldn't be called manually outside of PreferenceUtils
     * @param type Type of the notification (course, grade, homework...)
     * @param id Id of the notification (automatic)
     * @param title Title of the notification
     * @param body Body of the notification
     * @param time Time when the notification should trigger
     */
    public ScorpionNotification(Context context, NotificationUtils.TYPE type, Long id, String title, String body, Long time) {
        _type = type;
        _id = id;
        _title = title;
        _body = body;
        _time = time;
        _context = context;
    }

    public Long getId() { return _id; }

    private void cleanTimeoutNotifications() {
        ArrayList<ScorpionNotification> notifications = (ArrayList<ScorpionNotification>) PreferenceUtils.getNotifications();
        ArrayList<ScorpionNotification> cleanedNotifications = new ArrayList<>();

        for(ScorpionNotification notif : notifications) {
            if(notif._time + 600_000 >= System.currentTimeMillis()) {
                cleanedNotifications.add(notif);
            }
        }

        PreferenceUtils.setNotifications(cleanedNotifications);
    }

    /**
     * Confirm the notification and store it in Preferences
     * Updates the notification if it exists
     */
    public void commit() {
        cleanTimeoutNotifications();

        ArrayList<ScorpionNotification> notifications = (ArrayList<ScorpionNotification>) PreferenceUtils.getNotifications();
        if(notifications.isEmpty()) {
            _id = 1L;
        }
        else {
            if(_id == 0L && notifications.get(0)._id == 1L)
                _id = notifications.get(notifications.size() - 1)._id + 1L;
            else if(_id == 0L && notifications.get(0)._id != 1L)
                _id = 1L;
            else {
                for(int i = 0; i < notifications.size(); i++) {
                    if (notifications.get(i)._id.equals(_id)) {
                        notifications.set(i, this);
                    }
                }
            }
        }
        notifications.add(this);

        PreferenceUtils.setNotifications(notifications);

        // Prepare notification
        Intent notifyIntent = new Intent(_context, ReminderBroadcast.class);
        notifyIntent.setAction(_id.toString());
        notifyIntent.putExtra("ID", _id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (_context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, _time, pendingIntent);
        /*AlarmManager alarmManager2 = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
        alarmManager2.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 7000, pendingIntent);*/
    }

    @NonNull
    @Override
    public String toString() {
        return _type.toString() + ";" + _id + ";" + _time.toString() + ";" + _title + ";" + _body;
    }
}
