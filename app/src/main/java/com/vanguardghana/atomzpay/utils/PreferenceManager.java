package com.vanguardghana.atomzpay.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.vanguardghana.atomzpay.models.Agent;

public class PreferenceManager {

    private static final String LOGIN = "login";

    public static void putAgent(@NonNull Context context, @NonNull Agent agent) {
        SharedPreferences prefs = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("id", agent.getId());
        editor.putString("code", agent.getCollectorCode());
        editor.putString("name", agent.getName());
        editor.putString("email", agent.getEmail());
        editor.putString("phone", agent.getPhoneNumber());
        editor.putInt("locId", agent.getLocationId());
        editor.putString("locCode", agent.getLocationCode());
        editor.putString("locName", agent.getLocationName());
        editor.apply();
    }

    public static Agent getAgent(@NonNull Context context) {
        Agent agent = new Agent();
        SharedPreferences prefs = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        agent.setId(prefs.getInt("id", -1));
        agent.setCollectorCode(prefs.getString("code", ""));
        agent.setName(prefs.getString("name", ""));
        agent.setEmail(prefs.getString("email", ""));
        agent.setPhoneNumber(prefs.getString("phone", ""));
        agent.setLocationId(prefs.getInt("locId", -1));
        agent.setLocationCode(prefs.getString("locCode", ""));
        agent.setLocationName(prefs.getString("locName", ""));
        return agent;
    }

    public static void putToken(@NonNull Context context, @NonNull String token) {
        SharedPreferences prefs = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public static String getToken(@NonNull Context context) {
        SharedPreferences prefs = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        return prefs.getString("token", null);
    }

    public static void clearPreferences(@NonNull Context context) {
        SharedPreferences prefs = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

}
