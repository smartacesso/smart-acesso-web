package com.protreino.luxandapp.async;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.protreino.luxandapp.interfaces.TaskCallback;
import com.protreino.luxandapp.model.ResultWrapper;
import com.protreino.luxandapp.util.HttpConnection;
import com.protreino.services.enumeration.TaskType;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class GetConfigurationsTask extends AsyncTask<Void, Integer, ResultWrapper<Map<String, String>>> {

    private TaskCallback callback;
    private HttpConnection httpConnection;
    private String url;

    public GetConfigurationsTask(TaskCallback callback, String url){
        super();
        this.callback = callback;
        this.url = url;
    }

    @Override
    protected ResultWrapper<Map<String, String>> doInBackground(Void... params) {
        try {
            httpConnection = new HttpConnection(url + "/recognition/configurations");

            int status = httpConnection.getResponseCode();

            if (status == HttpURLConnection.HTTP_OK) {
                String response = httpConnection.getResponseString();
                TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
                Map<String, String> configurationsMap = new ObjectMapper().readValue(response, typeRef);
                return new ResultWrapper<>(configurationsMap, TaskType.GET_CONFIGURATIONS);
            }
            else {
                return new ResultWrapper<>(httpConnection.getErrorString(), TaskType.GET_CONFIGURATIONS);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResultWrapper<>("Não foi possível obter lista de catracas.", TaskType.GET_CONFIGURATIONS);
        }
        finally {
            if (httpConnection != null && httpConnection.getHttpURLConnection() != null)
                httpConnection.getHttpURLConnection().disconnect();
        }
    }

    @Override
    protected void onPostExecute(ResultWrapper<Map<String, String>> resultWrapper) {
        super.onPostExecute(resultWrapper);
        callback.onTaskCompleted(resultWrapper);
    }

}
