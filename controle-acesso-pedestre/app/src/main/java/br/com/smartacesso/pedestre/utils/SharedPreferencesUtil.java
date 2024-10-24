package br.com.smartacesso.pedestre.utils;

import android.content.Context;
import android.content.SharedPreferences;

import br.com.smartacesso.pedestre.model.entity.Pedestre;
import br.com.smartacesso.pedestre.model.network.NetworkCall;

/**
 * Created by gustavo on 07/06/17.
 */

public class SharedPreferencesUtil {

    /**
     * Grava json para usar depois.
     *
     * @param value
     * @param name
     */
    public static void setSharePreference(String name, String value, Context context){

        SharedPreferences prefs = context.getSharedPreferences(
                AppConstants.APP_SHARED_PROPS, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(name, value);
        editor.commit();

    }

    /**
     * Retorna string que foi definida do preference.
     *
     * @param nome - nome da propriedade
     */
    public static String getSharePreference(String nome, Context context){

        SharedPreferences prefs = context.getSharedPreferences(
                AppConstants.APP_SHARED_PROPS, Context.MODE_PRIVATE);
        return prefs.getString(nome, null);

    }

    /**
     * Retorna string que foi definida do preference.
     *
     * @param nome - nome da propriedade
     */
    public static void removeSharePreference(String nome, Context context){

        SharedPreferences prefs = context.getSharedPreferences(
                AppConstants.APP_SHARED_PROPS, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(nome);
        editor.commit();

    }

    public static Pedestre getLoggedUser(Context context){

        String userJson = getSharePreference(SharedPreferencesKey.LOGGED_USER, context);
        if(userJson != null){
            Pedestre u = NetworkCall.getGson().fromJson(userJson.trim(), Pedestre.class);
            if(u != null)
                return u;
        }

        return null;
    }

    public interface SharedPreferencesKey {

        public static final String LOGGED_USER = "user";
        public static final String URL_SERVIDOR = "urlServidor";
    }

}
