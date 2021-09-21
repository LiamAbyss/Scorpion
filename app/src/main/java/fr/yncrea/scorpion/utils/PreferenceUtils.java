package fr.yncrea.scorpion.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.annotation.RequiresApi;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;

import fr.yncrea.scorpion.ScorpionApplication;
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
		final String login = EncryptionUtils.decrypt(prefs.getString(Constants.Preferences.PREF_LOGIN, null));
		return login;
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
		final String password = EncryptionUtils.decrypt(prefs.getString(Constants.Preferences.PREF_PASSWORD, null));
		return password;
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
}
