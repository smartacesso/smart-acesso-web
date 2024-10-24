package com.protreino.luxandapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.protreino.luxandapp.service.LoginService;
import com.protreino.services.to.ResponseService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by gustavo on 06/06/17.
 */

public class NetworkCall {

    public static final Boolean PRODUCAO    = Boolean.TRUE;


    public static Retrofit getDefaultConfig(){

        String url = getURL();

        return getConfig(url + "/restful-services/");
    }

    public static Retrofit getConfig(String url){

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(6000, TimeUnit.SECONDS)
                .writeTimeout(6000, TimeUnit.SECONDS)
                .readTimeout(6000, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }


    public static Gson getGson(){
        return new GsonBuilder()
                .serializeNulls()
                .setDateFormat(AppConstants.JSON_DATE_FORMAT)
                .registerTypeHierarchyAdapter(Date.class,
                        new DateTypeAdapter())
                .registerTypeHierarchyAdapter(byte[].class,
                        new ByteArrayToBase64TypeAdapter())
                .setLenient()
                .create();
    }


    public static class DateTypeAdapter implements JsonDeserializer<Date>, JsonSerializer<Date> {

        @Override
        public Date deserialize(JsonElement element, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Date returnDate = null;
            try{
                //convert no padrão numérico
                Long l =  element.getAsLong();
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(l);
                returnDate = c.getTime();

            }catch (Exception ex) {
                //converte no padrão de string
                String date = element.getAsString();
                SimpleDateFormat formatter = new SimpleDateFormat(AppConstants.JSON_DATE_FORMAT);

                try {
                    returnDate = formatter.parse(date);
                } catch (ParseException e) {
                    returnDate = null;
                }
            }
            return returnDate;
        }

        @Override
        public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            SimpleDateFormat formatter = new SimpleDateFormat(AppConstants.JSON_DATE_FORMAT);
            String dateFormatAsString = formatter.format(date);
            return new JsonPrimitive(dateFormatAsString);
        }
    }


    public static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Base64.decode(json.getAsString(), Base64.NO_WRAP);
        }

        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64.encodeToString(src, Base64.NO_WRAP));
        }
    }

    public static boolean verificaConexao(Context context) {
        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected())
            return true;
        else
            return false;
    }

    @NonNull
    private static String getURL() {
        String url = "http://www.smartacesso.com.br/sistema";
        if (!PRODUCAO)
            url = "http://192.168.2.141:8081/sistema";
        return url;
    }

}
