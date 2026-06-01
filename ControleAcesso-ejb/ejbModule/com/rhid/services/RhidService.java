package com.rhid.services;

import java.io.Serializable;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.rhid.services.dto.RhidCustomerDTO;
import com.rhid.services.dto.RhidLoginDTO;
import com.rhid.services.dto.RhidLoginResponseDTO;
import com.rhid.services.dto.RhidPersonDTO;

public class RhidService implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String baseUrl;
	private String email;
	private String senha;
	private String dominio;
	private String tokenCache;

	public RhidService(String baseUrl) {
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
	}

	public void inicializarCredenciais(String email, String senha, String dominio) {
		this.email = email;
		this.senha = senha;
		this.dominio = dominio;
		this.tokenCache = null;
	}

	public RhidLoginResponseDTO loginAutenticar() {
		if (email == null || senha == null) {
			throw new IllegalStateException("Credenciais do RHID não foram inicializadas.");
		}

		RhidLoginDTO dto = new RhidLoginDTO(email, senha, dominio);
		String jsonResposta = post("/login.svc/", dto);
		RhidLoginResponseDTO resposta = parseLoginResponse(jsonResposta);

		if (resposta.getAccessToken() != null && !resposta.getAccessToken().isEmpty()) {
			this.tokenCache = resposta.getAccessToken();
		}

		return resposta;
	}

	public String cadastrarFuncionario(String jsonPayload) {
		return executarPostComToken("/customerdb/person.svc/a", jsonPayload);
	}

	public String atualizarFuncionario(String jsonPayload) {
		return executarPutComToken("/customerdb/person.svc/a", jsonPayload);
	}

	public String baixarAfd(String dataIni, String dataFim, String jsonIdsEmpresas) {
		String endpoint = String.format("/report.svc/exporta_arquivo/?tipo=afd&ini=%s&fim=%s", dataIni, dataFim);
		return executarPostComToken(endpoint, jsonIdsEmpresas);
	}

	/**
	 * Listagem paginada estilo DataTables (customerdb/*.svc/a).
	 */
	public String listarCustomerDb(String endpoint, int start, int length) {
		garantirToken();
		Client client = criarClient();
		Response response = null;
		try {
			response = client.target(baseUrl + endpoint)
					.queryParam("draw", 1)
					.queryParam("start", start)
					.queryParam("length", length)
					.queryParam("search[value]", "")
					.queryParam("search[regex]", false)
					.queryParam("order[0][column]", 1)
					.queryParam("order[0][dir]", "asc")
					.queryParam("columns[0][data]", "id")
					.queryParam("columns[0][searchable]", true)
					.queryParam("columns[0][orderable]", true)
					.queryParam("columns[1][data]", "name")
					.queryParam("columns[1][searchable]", true)
					.queryParam("columns[1][orderable]", true)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + this.tokenCache)
					.get();
			if (response.getStatus() == 401) {
				response.close();
				loginAutenticar();
				response = client.target(baseUrl + endpoint)
						.queryParam("draw", 1)
						.queryParam("start", start)
						.queryParam("length", length)
						.queryParam("search[value]", "")
						.queryParam("search[regex]", false)
						.queryParam("order[0][column]", 1)
						.queryParam("order[0][dir]", "asc")
						.queryParam("columns[0][data]", "id")
						.queryParam("columns[0][searchable]", true)
						.queryParam("columns[0][orderable]", true)
						.queryParam("columns[1][data]", "name")
						.queryParam("columns[1][searchable]", true)
						.queryParam("columns[1][orderable]", true)
						.request(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + this.tokenCache)
						.get();
			}
			return tratarResposta(response);
		} catch (ProcessingException e) {
			throw new RuntimeException("Falha de conexão com RHID", e);
		} finally {
			if (response != null) {
				response.close();
			}
			client.close();
		}
	}

	private void garantirToken() {
		if (this.tokenCache == null) {
			RhidLoginResponseDTO login = loginAutenticar();
			if (login.requerSelecaoDominio()) {
				throw new RuntimeException("Credenciais com múltiplos domínios. Informe o domínio na configuração.");
			}
			if (this.tokenCache == null) {
				throw new RuntimeException("Não foi possível obter token de acesso no RHID.");
			}
		}
	}

	public Integer extrairNovoId(String jsonResposta) {
		try (JsonReader reader = Json.createReader(new StringReader(jsonResposta))) {
			JsonObject jsonObject = reader.readObject();
			if (jsonObject.containsKey("newID")) {
				return jsonObject.getInt("newID");
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException("Erro ao processar resposta do RHID: " + e.getMessage(), e);
		}
	}

	private RhidLoginResponseDTO parseLoginResponse(String jsonResposta) {
		RhidLoginResponseDTO resposta = new RhidLoginResponseDTO();
		try (JsonReader reader = Json.createReader(new StringReader(jsonResposta))) {
			JsonObject jsonObject = reader.readObject();

			if (jsonObject.containsKey("accessToken") && !jsonObject.isNull("accessToken")) {
				resposta.setAccessToken(jsonObject.getString("accessToken"));
			} else if (jsonObject.containsKey("token") && !jsonObject.isNull("token")) {
				resposta.setAccessToken(jsonObject.getString("token"));
			}

			if (jsonObject.containsKey("code") && !jsonObject.isNull("code")) {
				resposta.setCode(jsonObject.getInt("code"));
			}
			if (jsonObject.containsKey("error") && !jsonObject.isNull("error")) {
				resposta.setError(jsonObject.getString("error"));
			}
			if (jsonObject.containsKey("expiredPassword")) {
				resposta.setExpiredPassword(jsonObject.getBoolean("expiredPassword"));
			}
			if (jsonObject.containsKey("isPerson")) {
				resposta.setPerson(jsonObject.getBoolean("isPerson"));
			}
			if (jsonObject.containsKey("revendaInadimplente")) {
				resposta.setRevendaInadimplente(jsonObject.getBoolean("revendaInadimplente"));
			}

			if (jsonObject.containsKey("listCustomer") && jsonObject.get("listCustomer").getValueType() == JsonValue.ValueType.ARRAY) {
				JsonArray array = jsonObject.getJsonArray("listCustomer");
				for (int i = 0; i < array.size(); i++) {
					JsonObject item = array.getJsonObject(i);
					RhidCustomerDTO customer = new RhidCustomerDTO();
					if (item.containsKey("domain")) {
						customer.setDomain(item.getString("domain"));
					}
					if (item.containsKey("id")) {
						customer.setId(item.getJsonNumber("id").longValue());
					}
					if (item.containsKey("name")) {
						customer.setName(item.getString("name"));
					}
					resposta.getListCustomer().add(customer);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Erro ao processar JSON de login: " + e.getMessage(), e);
		}
		return resposta;
	}

	private <T> String executarPostComToken(String endpoint, T body) {
		return executarComToken("POST", endpoint, body);
	}

	private <T> String executarPutComToken(String endpoint, T body) {
		return executarComToken("PUT", endpoint, body);
	}

	private <T> String executarComToken(String metodo, String endpoint, T body) {
		if (this.tokenCache == null) {
			RhidLoginResponseDTO login = loginAutenticar();
			if (login.requerSelecaoDominio()) {
				throw new RuntimeException("Credenciais com múltiplos domínios. Informe o domínio na configuração.");
			}
			if (this.tokenCache == null) {
				throw new RuntimeException("Não foi possível obter token de acesso no RHID.");
			}
		}

		Client client = criarClient();
		Response response = null;
		try {
			response = executarRequest(client, metodo, endpoint, body);

			if (response.getStatus() == 401) {
				response.close();
				loginAutenticar();
				response = executarRequest(client, metodo, endpoint, body);
			}

			return tratarResposta(response);
		} catch (ProcessingException e) {
			throw new RuntimeException("Falha de conexão com RHID", e);
		} finally {
			if (response != null) {
				response.close();
			}
			client.close();
		}
	}

	private <T> Response executarRequest(Client client, String metodo, String endpoint, T body) {
		if ("PUT".equalsIgnoreCase(metodo)) {
			return client.target(baseUrl + endpoint)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + this.tokenCache)
					.put(Entity.entity(body, MediaType.APPLICATION_JSON));
		}
		return client.target(baseUrl + endpoint)
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + this.tokenCache)
				.post(Entity.entity(body, MediaType.APPLICATION_JSON));
	}

	private Client criarClient() {
		return ClientBuilder.newBuilder()
				.connectTimeout(10, TimeUnit.SECONDS)
				.readTimeout(30, TimeUnit.SECONDS)
				.build();
	}

	private <T> String post(String endpoint, T body) {
		Client client = criarClient();
		Response response = null;
		try {
			response = client.target(baseUrl + endpoint)
					.request(MediaType.APPLICATION_JSON)
					.post(Entity.entity(body, MediaType.APPLICATION_JSON));
			return tratarRespostaLogin(response);
		} finally {
			if (response != null) {
				response.close();
			}
			client.close();
		}
	}

	private String tratarRespostaLogin(Response response) {
		int status = response.getStatus();
		String body = lerCorpoResposta(response);

		if (status == 200 || status == 201) {
			return body;
		}

		if (status == 400 && body != null && body.contains("listCustomer")) {
			return body;
		}

		throw criarExcecaoHttp(status, body);
	}

	private String tratarResposta(Response response) {
		int status = response.getStatus();
		String body = lerCorpoResposta(response);

		if (status == 200 || status == 201) {
			return body;
		}

		throw criarExcecaoHttp(status, body);
	}

	private String lerCorpoResposta(Response response) {
		try {
			return response.readEntity(String.class);
		} catch (Exception e) {
			return null;
		}
	}

	private RuntimeException criarExcecaoHttp(int status, String body) {
		String mensagemRhid = extrairMensagemErroRhid(body);
		if (mensagemRhid != null) {
			return new RuntimeException(mensagemRhid);
		}

		String detalhe = body != null && !body.trim().isEmpty() ? body : "sem detalhes";
		switch (status) {
			case 400: return new RuntimeException("Dados inválidos enviados ao RHID: " + detalhe);
			case 401: return new RuntimeException("Não autorizado no RHID.");
			case 403: return new RuntimeException("Acesso negado ao RHID.");
			case 404: return new RuntimeException("Endpoint não encontrado no RHID.");
			case 500: return new RuntimeException("Erro no RHID: " + detalhe);
			default: return new RuntimeException("Erro " + status + " no RHID: " + detalhe);
		}
	}

	private String extrairMensagemErroRhid(String body) {
		if (body == null || body.trim().isEmpty()) {
			return null;
		}
		try (JsonReader reader = Json.createReader(new StringReader(body))) {
			JsonObject jsonObject = reader.readObject();
			if (jsonObject.containsKey("error") && !jsonObject.isNull("error")) {
				return jsonObject.getString("error");
			}
		} catch (Exception e) {
			// corpo não é JSON — usa mensagem genérica
		}
		return null;
	}
}
