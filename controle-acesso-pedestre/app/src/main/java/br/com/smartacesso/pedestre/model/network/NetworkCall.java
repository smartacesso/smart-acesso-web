package br.com.smartacesso.pedestre.model.network;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


import br.com.smartacesso.pedestre.model.to.ResponseService;
import br.com.smartacesso.pedestre.utils.AppConstants;
import br.com.smartacesso.pedestre.utils.SharedPreferencesUtil;
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


    public static Retrofit getDefaultConfig(Context context){

        String url = getURL(context);

        return getConfig(url + "/restful-services/");
    }

    public static Retrofit getConfig(String url){

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
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
                //.registerTypeHierarchyAdapter(byte[].class,
               //         new ByteArrayToBase64TypeAdapter())
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
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {

            //se tem internet, verifica se esta funcionando corretamente
            try {
                LoginService service = getDefaultConfig(context).create(LoginService.class);

                Call<String> response = service.isWorking();
                String resposta = null;
                if (response != null) {
                    Response<String> rServer = response.execute();

                    if (rServer.errorBody() != null) {
                        Converter<ResponseBody, String> errorConverter =
                                NetworkCall.getDefaultConfig(context).responseBodyConverter(ResponseService.class, new Annotation[0]);
                        resposta = errorConverter.convert(rServer.errorBody());
                    } else {
                        resposta = rServer.body();
                    }
                }

                if ("working".equals(resposta))
                    return true;
                else
                    return false;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }

        } else {
            return false;
        }
    }

    @NonNull
    public static String getURL(Context context) {
        String url = SharedPreferencesUtil.getSharePreference(SharedPreferencesUtil.SharedPreferencesKey.URL_SERVIDOR, context);
        if (url == null || "".equals(url))
            url = "http://smartacesso.com.br/sistema";
        return url;
    }

    public static boolean somenteUsandoWifi(Context context) {
        ConnectivityManager connectivitymanager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivitymanager.getAllNetworkInfo();


        String usaSomenteWifiGravado = SharedPreferencesUtil
                .getSharePreference("usaSomenteWifi", context);

        //se nulo ou falso, usa outras conexoes
        if(usaSomenteWifiGravado == null
                || Boolean.FALSE.equals(Boolean.valueOf(usaSomenteWifiGravado)))
            return true;

        //senão, verifica se exta na wifi ou não
        for (NetworkInfo netInfo : networkInfo) {
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI") && netInfo.isConnected())
                return true;
        }

        Log.i("NetworkCall", "Não está usando Wifi");
        return false;
    }

}
