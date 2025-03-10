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
import com.totvs.dto.FuncionarioTotvsDto;

import br.com.startjob.acesso.modelo.entity.ClienteEntity;

public class IntegracaoTotvsService {

	//private final static String getFuncionariosUrl = "/rh/v1/employeeDataContent?product=Protheus&companyid=01&branchId=01";
	private final static Gson gson = new Gson();
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyyTHH:mm:ss");
	
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
			// Iniciando a conexão
			final String requestUrl = buildFuncionariosUrl(lastUpdate);
			final URL url = new URL(requestUrl);
			
			
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", "Basic dXNyX3Jlc3RfcHJvZDpxbVN1N3VyYWh2dG5jNUY=");

			// Verificando a resposta
			int responseCode = connection.getResponseCode();
			System.out.println("Response Code: " + responseCode);

			if (responseCode == 200) { // Se a resposta for 200
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuilder response = new StringBuilder();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				return response.toString();
			} else {
				System.out.println("Erro na requisição: Código " + responseCode);
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
			final Type type = new TypeToken<List<FuncionarioTotvsDto>>() {
            }.getType();

            return (List<FuncionarioTotvsDto>) gson.fromJson(reponse, type);
		}
		
		return null;
	}
	
}
