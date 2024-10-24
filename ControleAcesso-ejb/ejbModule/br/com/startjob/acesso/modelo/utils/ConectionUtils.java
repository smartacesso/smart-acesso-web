package br.com.startjob.acesso.modelo.utils;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.age.soc.services.exportaDados.results.FuncionarioResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConectionUtils {
	
	public static String post(String url, String json) {

		try {

			HttpPost post = new HttpPost();
			post.setURI(new URI(url));
			post.setHeader("Content-Type", "application/json");
			post.setEntity(new StringEntity(json, "UTF-8"));

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000000);
			HttpConnectionParams.setSoTimeout(params, 3000000);

			HttpResponse httpResponse = httpClient.execute(post);

			String r = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

			return r;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	public static String post(String url, String json, Map<String, String> header) {

		try {

			HttpPost post = new HttpPost();
			post.setURI(new URI(url));
			post.setHeader("Content-Type", "application/json");
			if(header != null){
				for (String key : header.keySet()) {
					post.setHeader(key, header.get(key));
				}
			}
			post.setEntity(new StringEntity(json, "UTF-8"));

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000000);
			HttpConnectionParams.setSoTimeout(params, 3000000);

			HttpResponse httpResponse = httpClient.execute(post);

			String r = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

			return r;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public static String get(String url) {
		try {

			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			HttpResponse response = null;
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000000);
			HttpConnectionParams.setSoTimeout(params, 3000000);

			response = client.execute(get);

			String r = converteResposta(response.getEntity());

			get.abort();

			return r;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public static String get(String url, String key) {
		try {
			
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			HttpResponse response = null;
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000000);
			HttpConnectionParams.setSoTimeout(params, 3000000);
			get.addHeader("auth-key",key);
			
			response = client.execute(get);

			String r = converteResposta(response.getEntity());

			get.abort();

			return r;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * Converte resposta do JSON.
	 * 
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	private static String converteResposta(HttpEntity entity) throws Exception {
		if (entity != null) {
			InputStreamReader isr = null;
			try {
				isr = new InputStreamReader(entity.getContent());
				StringBuilder sb = new StringBuilder();
				int len = -1;
				char[] cArray = new char[4096];
				while ((len = isr.read(cArray)) != -1) {
					sb.append(cArray, 0, len);
				}
				return sb.toString();
			} finally {
				if (isr != null)
					isr.close();
			}
		} else {
			return null;
		}

	}
	
//	public static String createJSON(Object to) throws JsonProcessingException{
//		
////		Gson gson = new GsonBuilder()
////		.serializeNulls()
////		.setDateFormat("ddMMyyyy-HHmmss").create();
//		ObjectMapper mapper = new ObjectMapper();
//		return mapper.writeValueAsString(to);
//		
//		
//	}
	
	public static Object parseJSON(String json, Class<?> classe){
		
		Gson gson = new GsonBuilder()
		.serializeNulls()
		.setDateFormat("ddMMyyyy-HHmmss").create();

		return gson.fromJson(json, classe);
		
	}
	
	
	public static Object createObject(String json, Type classOfT){
//		Gson gson = new GsonBuilder()
//		.serializeNulls()
//		.setDateFormat("ddMMyyyy-HHmmss").create();
		
		ObjectMapper mapper = new ObjectMapper();
		List<FuncionarioResult> result = null;
		try {
			result = mapper.readValue(json, new TypeReference<List<FuncionarioResult>>() {});

		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

//		return gson.fromJson(json, classOfT);
		return result;
	}

}
