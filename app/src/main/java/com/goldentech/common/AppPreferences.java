package com.goldentech.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class AppPreferences {


    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private static final String APP_SHARED_PREFS = AppPreferences.class.getSimpleName(); //  Name of the file -.xml
    private SharedPreferences _sharedPrefs;
    private SharedPreferences.Editor _prefsEditor;

    private static final String USER_ID = "user_id";
    private static final String USER_TYPE = "user_type";
    private static final String KRY_THEME_STYLE = "theme_style";

    private static final String USER_NAME = "user_name";

    private static final String IS_ENABLED_APP_LOCK = "is_enable_app_lock";
    private static final String APP_LOCK_PASSWORD = "app_lock_password";


    @SuppressLint("CommitPrefEdits")
    public AppPreferences(Context context) {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();

    }

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public void setFirstTimeLaunch(boolean isFirstTime) {
        _prefsEditor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        _prefsEditor.commit();
    }


    public boolean isFirstTimeLaunch() {
        return _sharedPrefs.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }


    public String getThemeStyle() {
        return _sharedPrefs.getString(KRY_THEME_STYLE, "");
    }

    public void setThemeStyle(String _theme_style) {
        _prefsEditor.putString(KRY_THEME_STYLE, _theme_style);
        _prefsEditor.apply();
    }

    private static final String IS_SECURE_LOGIN = "is_secure_login";

    public boolean isSecureLogin() {
        return _sharedPrefs.getBoolean(IS_SECURE_LOGIN, false);
    }

    public void setSecureLogin(boolean isSecureLogin) {

        _prefsEditor.putBoolean(IS_SECURE_LOGIN, isSecureLogin);
        _prefsEditor.apply();
    }

    public boolean isAppLocked() {
        return _sharedPrefs.getBoolean(IS_ENABLED_APP_LOCK, false);
    }


    public void setAppLocked(boolean appLocked) {
        _prefsEditor.putBoolean(IS_ENABLED_APP_LOCK, appLocked);
        _prefsEditor.apply();
    }

    public String getAppLockPassword() {
        return _sharedPrefs.getString(APP_LOCK_PASSWORD, "");
    }

    public void setAppLockPassword(String appLockPassword) {
        _prefsEditor.putString(APP_LOCK_PASSWORD, appLockPassword);
        _prefsEditor.apply();
    }

    public String getUserType() {
        return _sharedPrefs.getString(USER_TYPE, "");
    }

    public void setUserType(String userType) {
        _prefsEditor.putString(USER_TYPE, userType);
        _prefsEditor.apply();
    }


    public String getUserId() {
        return _sharedPrefs.getString(USER_ID, "");
    }

    public void setUserId(String userId) {
        _prefsEditor.putString(USER_ID, userId);
        _prefsEditor.apply();
    }


    public void createLogin(String user_id, String user_name, String type) {
        _prefsEditor.putString(USER_ID, user_id);
        _prefsEditor.putString(USER_NAME, user_name);
        _prefsEditor.putString(USER_TYPE, type);
        _prefsEditor.putBoolean(KEY_IS_LOGGED_IN, true);
        _prefsEditor.apply();
    }

    public boolean isLoggedIn() {
        return _sharedPrefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        _prefsEditor.clear();
        _prefsEditor.apply();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> profile = new HashMap<>();
        profile.put("user_id", _sharedPrefs.getString(USER_ID, null));
        profile.put("user_name", _sharedPrefs.getString(USER_NAME, null));
        profile.put("type", _sharedPrefs.getString(USER_TYPE, null));
        return profile;
    }
}
