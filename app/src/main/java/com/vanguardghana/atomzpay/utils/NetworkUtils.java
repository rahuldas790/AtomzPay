package com.vanguardghana.atomzpay.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.Headers;
import com.koushikdutta.async.http.NameValuePair;
import com.koushikdutta.ion.Ion;

public class NetworkUtils {

    private static final String END_POINT = "http://41.204.42.117:8080/rsms_tolls/api/";

    private static final String loginUrl = "login";
    private static final String vehicleUrl = "vehicle_types";

    public static Future<JsonObject> getLoginRequest(Context context,
                                                     String username,
                                                     String password) {
        return Ion.with(context)
                .load(END_POINT + loginUrl)
                .setHeader("Accept", "application/json")
                .setBodyParameter("collector_code", username)
                .setBodyParameter("password", password)
                .asJsonObject();
    }

    public static Future<JsonObject> getVehicleRequest(Context context,
                                                       String token) {
        return Ion.with(context)
                .load(END_POINT + vehicleUrl)
                .setHeader("Authorization", "Bearer " + token)
                .asJsonObject();
    }

}
