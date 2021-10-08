package fr.yncrea.scorpion.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import fr.yncrea.scorpion.ScorpionApplication;
import fr.yncrea.scorpion.utils.notifications.NotificationUtils;
import fr.yncrea.scorpion.utils.notifications.ScorpionNotification;
import fr.yncrea.scorpion.utils.security.EncryptionUtils;

public class PreferenceUtils {

	@SuppressWarnings("unused")
	private static SharedPreferences getSharedPreferences(Context context){
		return context.getSharedPreferences(Constants.Preferences.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
	}
	
	private static SharedPreferences getSharedPreferences(){
		return ScorpionApplication.getContext().getSharedPreferences(Constants.Preferences.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
	}

	public static String getLogin(){
		final SharedPreferences prefs = getSharedPreferences();
		return EncryptionUtils.decrypt(prefs.getString(Constants.Preferences.PREF_LOGIN, null));
	}

	public static void setLogin(String login){
		final SharedPreferences prefs = getSharedPreferences();
		final String encodedLogin = EncryptionUtils.encrypt(login);
		prefs.edit().putString(Constants.Preferences.PREF_LOGIN, encodedLogin).apply();
	}
	public static String getSessionId(){
		final SharedPreferences prefs = getSharedPreferences();
		return prefs.getString(Constants.Preferences.PREF_SESSION_ID, null);
	}

	public static void setSessionId(String id){
		final SharedPreferences prefs = getSharedPreferences();
		prefs.edit().putString(Constants.Preferences.PREF_SESSION_ID, id).apply();
	}
	
	public static String getPassword(){
		final SharedPreferences prefs = getSharedPreferences();
		return EncryptionUtils.decrypt(prefs.getString(Constants.Preferences.PREF_PASSWORD, null));
	}
	
	public static void setPassword(String password){
		final SharedPreferences prefs = getSharedPreferences();
		final String encodedPassword = EncryptionUtils.encrypt(password);
		prefs.edit().putString(Constants.Preferences.PREF_PASSWORD, encodedPassword).apply();
	}

	public static String getName(){
		final SharedPreferences prefs = getSharedPreferences();
		return prefs.getString(Constants.Preferences.PREF_NAME, null);
	}

	public static void setName(String name){
		final SharedPreferences prefs = getSharedPreferences();
		prefs.edit().putString(Constants.Preferences.PREF_NAME, name).apply();
	}

	public static String getAcceptedEula(){
		final SharedPreferences prefs = getSharedPreferences();
		return prefs.getString(Constants.Preferences.PREF_ACCEPTED_EULA, "");
	}

	public static void setAcceptedEula(String val){
		final SharedPreferences prefs = getSharedPreferences();
		prefs.edit().putString(Constants.Preferences.PREF_ACCEPTED_EULA, val).apply();
	}

	public static String getMustReset(){
		final SharedPreferences prefs = getSharedPreferences();
		return prefs.getString(Constants.Preferences.PREF_MUST_RESET, "");
	}

	public static void setMustReset(String val){
		final SharedPreferences prefs = getSharedPreferences();
		prefs.edit().putString(Constants.Preferences.PREF_MUST_RESET, val).apply();
	}
	
	public static Long getUpdateTime(){
		final SharedPreferences prefs = getSharedPreferences();
		return prefs.getLong(Constants.Preferences.PREF_UPDATE_TIME, 0L);
	}

	public static void setUpdateTime(Long time){
		final SharedPreferences prefs = getSharedPreferences();
		prefs.edit().putLong(Constants.Preferences.PREF_UPDATE_TIME, time).apply();
	}

	public static List<ScorpionNotification> getNotifications() {
		final SharedPreferences prefs = getSharedPreferences();
		String notifs = prefs.getString(Constants.Preferences.PREF_NOTIFICATIONS_ARRAY, null);
		ArrayList<ScorpionNotification> notifications = new ArrayList<>();

		if(notifs == null) return notifications;
		for(String notifString : notifs.split(";END_OF_SCORPION_LINE;")) {
			String[] data = notifString.split(";");
			if(data.length != 5) return notifications;
			notifications.add(new ScorpionNotification(
					ScorpionApplication.getContext(),
					NotificationUtils.TYPE.valueOf(data[0]),
					Long.parseLong(data[1]),
					data[3],
					data[4],
					Long.parseLong(data[2])
			));
		}
		return notifications;
	}

	public static void setNotifications(List<ScorpionNotification> notifications) {
		final SharedPreferences prefs = getSharedPreferences();
		StringBuilder builder = new StringBuilder();

		for(ScorpionNotification notif : notifications) {
			builder.append(notif.toString()).append(";END_OF_SCORPION_LINE;");
		}
		prefs.edit().putString(Constants.Preferences.PREF_NOTIFICATIONS_ARRAY, builder.toString()).apply();
	}
}
