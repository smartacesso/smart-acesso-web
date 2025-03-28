package com.totvs.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.totvs.dto.ApiResponse;
import com.totvs.dto.FuncionarioTotvsDto;

import br.com.startjob.acesso.modelo.entity.ClienteEntity;

public class IntegracaoTotvsService {

	//private final static String getFuncionariosUrl = "/rh/v1/employeeDataContent?product=Protheus&companyid=01&branchId=01";
	private final static Gson gson = new Gson();
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss");
	
	private final String usuario;
	private final String senha;
	private final String baseUrl; //= "http://10.1.2.63:9500/rest";
	private final String product;
	private final String companyid;
	private final String branchId;

	public IntegracaoTotvsService(ClienteEntity cliente) {
		this.usuario = cliente.getIntegracaoTotvs().getUsuario();
		this.senha = cliente.getIntegracaoTotvs().getSenha();
		this.baseUrl = cliente.getIntegracaoTotvs().getUrl();
		this.product = cliente.getIntegracaoTotvs().getProduct();
		this.companyid = cliente.getIntegracaoTotvs().getCompanyid();
		this.branchId = cliente.getIntegracaoTotvs().getBranchId();
	}
	
	
	private String buildFuncionariosUrl(String lastUpdate) {
		StringBuilder urlBuilder = new StringBuilder(baseUrl);
		urlBuilder.append("/rh/v1/employeeDataContent?");
		urlBuilder.append("product=").append(product);
		urlBuilder.append("&companyid=").append(companyid);
		urlBuilder.append("&branchId=").append(branchId);

		if (lastUpdate != null && !lastUpdate.isEmpty()) {
			urlBuilder.append("&data=").append(lastUpdate);
		}
		return urlBuilder.toString();
	}

	// Método comum para realizar a requisição SOAP
	private String getFuncionarios(final String lastUpdate) {
	    try {
	        final String requestUrl = buildFuncionariosUrl(lastUpdate);
	        System.out.println("URL gerada: " + requestUrl);

	        final URL url = new URL(requestUrl);
	        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	        connection.setDoOutput(true);
	        connection.setRequestMethod("GET");

	        // Adicionando cabeçalhos
	        connection.setRequestProperty("Authorization", "Basic dXNyX3Jlc3RfcHJvZDpxbVN1N3VyYWh2dG5jNUY=");
	        connection.setRequestProperty("Accept-Encoding", "identity");
	        connection.setRequestProperty("Host", "10.1.2.63:9500");
	        connection.setRequestProperty("Connection", "Keep-Alive");
	        connection.setRequestProperty("User-Agent", "Apache-HttpClient/4.5.5 (Java/17.0.12)");
	        connection.setRequestProperty("Accept", "application/json"); // Se a API esperar JSON

	        // Verificando resposta
	        int responseCode = connection.getResponseCode();
	        System.out.println("Response Code: " + responseCode);

	        if (responseCode == 200) { 
	            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	            StringBuilder response = new StringBuilder();
	            String inputLine;

	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	            in.close();
	            return response.toString();
	        } else {
	            System.out.println("Erro na requisição: Código " + responseCode);
	            BufferedReader errorStream = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
	            StringBuilder errorResponse = new StringBuilder();
	            String line;
	            while ((line = errorStream.readLine()) != null) {
	                errorResponse.append(line);
	            }
	            System.out.println("Resposta do erro: " + errorResponse.toString());
	            return null;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	
	@SuppressWarnings("unchecked")
	public List<FuncionarioTotvsDto> buscarFuncionarios(final Date lastUpdate) {
	    String reponse = getFuncionarios(Objects.nonNull(lastUpdate) ? sdf.format(lastUpdate) : null);

	    if (reponse != null) {	        
	        // Agora, desserializamos para a classe ApiResponse
	        ApiResponse apiResponse = gson.fromJson(reponse, ApiResponse.class);

	        // Retornamos a lista de Funcionarios
	        return apiResponse.getItems();
	    }
	    
	    return null;
	}

	
}
