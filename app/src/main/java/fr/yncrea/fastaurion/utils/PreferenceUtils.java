package fr.yncrea.fastaurion.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import fr.yncrea.fastaurion.FastAurionApplication;

public class PreferenceUtils {

	@SuppressWarnings("unused")
	private static SharedPreferences getSharedPreferences(Context context){
		return context.getSharedPreferences(Constants.Preferences.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
	}
	
	private static SharedPreferences getSharedPreferences(){
		return FastAurionApplication.getContext().getSharedPreferences(Constants.Preferences.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
	}

	public static String getLogin(){
		final SharedPreferences prefs = getSharedPreferences();
		return prefs.getString(Constants.Preferences.PREF_LOGIN, null);
	}

	public static void setLogin(String login){
		final SharedPreferences prefs = getSharedPreferences();
		prefs.edit().putString(Constants.Preferences.PREF_LOGIN, login).apply();
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
		return prefs.getString(Constants.Preferences.PREF_PASSWORD, null);
	}
	
	public static void setPassword(String password){
		final SharedPreferences prefs = getSharedPreferences();
		prefs.edit().putString(Constants.Preferences.PREF_PASSWORD, password).apply();
	}

	public static void setPlanning(List<Course> planning){
		final SharedPreferences prefs = getSharedPreferences();
		StringBuilder planningString = new StringBuilder();
		for(Course c : planning){
			planningString.append(c.toString()).append("#");
		}
		prefs.edit().putString(Constants.Preferences.PREF_PLANNING, planningString.toString()).apply();
	}

	public static List<Course> getPlanning(){
		final SharedPreferences prefs = getSharedPreferences();
		String planningString =  prefs.getString(Constants.Preferences.PREF_PLANNING, null);
		if(null == planningString){
			return new ArrayList<>();
		}
		String[] coursesString = planningString.split("#");
		List<Course> courses = new ArrayList<>();
		for(String course : coursesString){
			courses.add(Course.fromString(course));
		}
		return courses;
	}

	public static String getName(){
		final SharedPreferences prefs = getSharedPreferences();
		return prefs.getString(Constants.Preferences.PREF_NAME, null);
	}

	public static void setName(String name){
		final SharedPreferences prefs = getSharedPreferences();
		prefs.edit().putString(Constants.Preferences.PREF_NAME, name).apply();
	}
}
