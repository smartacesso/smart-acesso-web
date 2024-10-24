package com.protreino.luxandapp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpConnection {

    private HttpURLConnection con;
    private int timeout = 5000;

    public HttpConnection(String stringURL) throws IOException {
        makeConnection(stringURL);
    }

    public void makeConnection(String stringURL) throws IOException {
        URL url = new URL(stringURL);
        con = (HttpURLConnection) url.openConnection();
        con.setConnectTimeout(timeout);
        con.setReadTimeout(timeout);
    }

    public int getResponseCode() throws IOException {
        if (con != null) {
            return con.getResponseCode();
        } else
            return 0;
    }

    public String getResponseString() {
        try {
            if (con != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder stringBuilder = new StringBuilder();
                String bufferedString;
                while (null != (bufferedString = bufferedReader.readLine()))
                    stringBuilder.append(bufferedString);
                return stringBuilder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getErrorString() {
        String retorno = "";
        try {
            retorno = getResponseString();
            if ("".equals(retorno)) {
                if (con != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8));
                    StringBuilder stringBuilder = new StringBuilder();
                    String bufferedString;
                    while (null != (bufferedString = bufferedReader.readLine()))
                        stringBuilder.append(bufferedString);
                    return stringBuilder.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retorno;
    }

    public int sendResponse(String responseString) throws IOException {
        if (con != null) {
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Content-Length", Integer.toString(responseString.length()));
            OutputStream os = con.getOutputStream();
            os.write(responseString.getBytes(StandardCharsets.UTF_8));
            os.close();
            return con.getResponseCode();
        } else
            return 0;
    }

    public HttpURLConnection getHttpURLConnection() {
        return con;
    }

}
