package com.protreino.luxandserver.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpConnection {
	
	private URL url;
	private HttpURLConnection con;
	
	public HttpConnection(){
	}
	
	public HttpConnection(String stringURL) throws IOException{
		makeConnection(stringURL);
	}
	
	public void makeConnection(String stringURL) throws IOException{
		url = new URL(stringURL);
		con = (HttpURLConnection) url.openConnection();
		con.setConnectTimeout(4000);
		con.setReadTimeout(30000);
	}
	
	public int getResponseCode() throws IOException{
		if (con != null){
			return con.getResponseCode();
		}
		else
			return 0;
	}
	
	public String getResponse() {
		try {
			if (con != null){
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
				StringBuilder stringBuilder = new StringBuilder();
				String bufferedString = null;
				while (null != (bufferedString = bufferedReader.readLine()))
					stringBuilder.append(bufferedString);
				return stringBuilder.toString();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String getErrorResponse() {
		String retorno = "";
		try {
			retorno = getResponse();
			if ("".equals(retorno)) {
				if (con != null){
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8));
					StringBuilder stringBuilder = new StringBuilder();
					String bufferedString = null;
					while (null != (bufferedString = bufferedReader.readLine()))
						stringBuilder.append(bufferedString);
					return stringBuilder.toString();
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return retorno;	
	}
	
	public int sendJson(String json) throws IOException{
		if (con != null){
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty( "Content-Type", "application/json; charset=UTF-8");
			con.setRequestProperty( "Content-Length", String.valueOf(json.length()));
			
			OutputStream os = con.getOutputStream();
	        os.write(json.getBytes("UTF-8"));
	        os.close();
	        
	        return con.getResponseCode();
		}
		else
			return 0;
	}
	
	public int sendData(byte[] data) throws IOException{
		if (con != null){
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty( "Content-Type", "application/octet-stream");
			con.setRequestProperty( "Content-Length", String.valueOf(data.length));
			
			OutputStream os = con.getOutputStream();
	        os.write(data);
	        os.flush();
	        os.close();
	        
	        return con.getResponseCode();
		}
		else
			return 0;
	}

	public HttpURLConnection getHttpURLConnection() {
		return con;
	}

	public void setRequestMethod(String method) throws IOException {
		if (con != null)
			con.setRequestMethod(method);
	}

}
