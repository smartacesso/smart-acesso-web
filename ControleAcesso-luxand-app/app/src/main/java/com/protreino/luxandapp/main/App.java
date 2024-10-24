package com.protreino.luxandapp.main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.protreino.luxandapp.R;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class App extends Application {

    private static Context context;
    private static SharedPreferences sharedPreferences;
    private static Properties properties;
    private static volatile App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = getApplicationContext();
        sharedPreferences = getSharedPreferences(getString(R.string.pref_app_id), Context.MODE_PRIVATE);

        try {
            InputStream inputStream = context.getAssets().open("app.properties");
            properties = new Properties();
            properties.load(inputStream);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Context getAppContext() {
        return App.context;
    }

    public static String getProperty(int key) {
        if (properties != null)
            return properties.getProperty(context.getString(key));
        return "";
    }

    public static String getPreferenceAsString(int key) {
        return sharedPreferences.getString(context.getString(key), "");
    }

    public static Integer getPreferenceAsInteger(int key) {
        return sharedPreferences.getInt(context.getString(key), -1);
    }

    public static Object getPreferenceAsObject(int key, Class T) {
        try {
            String s = sharedPreferences.getString(context.getString(key), null);
            if (s == null)
                return null;
            return new Gson().fromJson(s, T);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> getPreferenceAsListOf(int key, Class T) {
        try {
            String s = sharedPreferences.getString(context.getString(key), null);
            if (s == null)
                return null;
            Type type = new TypeToken<ArrayList<T>>(){}.getType();
            return new Gson().fromJson(s, type);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> getPreferenceAsMap(int key) {
        try {
            String s = sharedPreferences.getString(context.getString(key), null);
            if (s == null)
                return null;
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            return new Gson().fromJson(s, type);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void savePreferenceAsString(int key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(key), value);
        editor.commit();
    }

    public static void savePreferenceAsInteger(int key, Integer value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(key), value);
        editor.commit();
    }

    public static void savePreferenceAsObject(int key, Object value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(key), new Gson().toJson(value));
        editor.commit();
    }

}
