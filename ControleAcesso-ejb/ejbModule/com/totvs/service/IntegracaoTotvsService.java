package com.totvs.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.senior.services.dto.EmpresaSeniorDto;
import com.totvs.dto.FuncionarioTotvsDto;

public class IntegracaoTotvsService {

	private final static String base_url = "http://10.1.2.63:9500/rest";
	private final static String getFuncionariosUrl = "/rh/v1/employeeDataContent?product=Protheus&companyid=01&branchId=01";
	Gson gson = new Gson();
	
	private String usuario;
	private String senha;

	public IntegracaoTotvsService(String usuario, String senha) {
		this.usuario = usuario;
		this.senha = senha;
	}

	// Método comum para realizar a requisição SOAP
	private String getFuncionarios() {
		try {
			// Iniciando a conexão
			final URL url = new URL(base_url + getFuncionariosUrl);
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
	
	public List<EmpresaSeniorDto> buscarFuncionarios(final Date lastUpdate) {
		String reponse = getFuncionarios();

		if (reponse != null) {
			gson.fromJson(reponse, FuncionarioTotvsDto.class);
		}
		
		return null;
	}
}
