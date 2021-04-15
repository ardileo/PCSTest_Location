package com.ardileo.pcstest.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ardileo.pcstest.Model.UserData;
import com.google.gson.Gson;

public class SessionManager {
    private SharedPreferences sPref;
    private Context mContext;
    private SharedPreferences.Editor sEditor;
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "pcstest.session";

    private static String IS_LOGIN;
    public static final String USER_DATAS = "USER_DATAS";
    public static final String USER_STATS = "USER_STATS";


    public SessionManager(Context c) {
        this.mContext = c;
        sPref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        sEditor = sPref.edit();
    }


    public UserData getUser() {
        return new Gson().fromJson(sPref.getString(USER_DATAS, null), UserData.class);
    }

    public void setUser(UserData user) {
        sEditor.putString(USER_DATAS, user.toString());
        sEditor.commit();
    }

    public boolean isLoggedIn() {
        return sPref.getBoolean(IS_LOGIN, false);
    }

    public void setIsLoggedIn(boolean b) {
        sEditor.putBoolean(IS_LOGIN, b);
        sEditor.commit();
    }

    public void logout() {
        sPref.edit().clear().apply();
        sEditor.clear();
        sEditor.apply();
    }

    public String getAuthToken() {
        if (!isLoggedIn()) return "none";
        String token = getUser().getToken() != null ? getUser().getToken() : "none";
        return "Bearer " + token;
    }
}
