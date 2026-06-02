package com.rhid.services.fonte.totvs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import br.com.startjob.acesso.modelo.entity.ConfiguracaoRhidEntity;

/**
 * Cliente SOAP para wsConsultaSQL / RealizarConsultaSQL (TOTVS RM).
 */
public final class RhidTotvsSoapClient {

	private static final String SOAP_ACTION = "http://www.totvs.com/IwsConsultaSQL/RealizarConsultaSQL";

	private RhidTotvsSoapClient() {
	}

	public static String consultar(ConfiguracaoRhidEntity config, String parameters) throws Exception {
		validarConfig(config);

		String codSentenca = valorOuPadrao(config.getCodSentencaTotvs(), "API.PTO.001");
		String codColigada = valorOuPadrao(config.getCodColigadaTotvs(), "1");
		String codSistema = valorOuPadrao(config.getCodSistemaTotvs(), "A");

		String soapRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tot=\"http://www.totvs.com/\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<tot:RealizarConsultaSQL>"
				+ "<tot:codSentenca>" + escapeXml(codSentenca) + "</tot:codSentenca>"
				+ "<tot:codColigada>" + escapeXml(codColigada) + "</tot:codColigada>"
				+ "<tot:codSistema>" + escapeXml(codSistema) + "</tot:codSistema>"
				+ "<tot:parameters>" + escapeXml(parameters) + "</tot:parameters>"
				+ "</tot:RealizarConsultaSQL>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";

		String auth = Base64.getEncoder()
				.encodeToString((config.getUsuarioTotvs() + ":" + config.getSenhaTotvs())
						.getBytes(StandardCharsets.UTF_8));

		HttpURLConnection conn = (HttpURLConnection) new URL(config.getUrlTotvs()).openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
		conn.setRequestProperty("Accept", "text/xml");
		conn.setRequestProperty("SOAPAction", SOAP_ACTION);
		conn.setRequestProperty("Authorization", "Basic " + auth);
		conn.setDoOutput(true);

		byte[] xmlBytes = soapRequest.getBytes(StandardCharsets.UTF_8);
		conn.setRequestProperty("Content-Length", String.valueOf(xmlBytes.length));

		try (OutputStream os = conn.getOutputStream()) {
			os.write(xmlBytes);
		}

		int status = conn.getResponseCode();
		InputStream is = status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream();
		if (is == null) {
			throw new IllegalStateException("TOTVS não retornou conteúdo (HTTP " + status + ").");
		}

		StringBuilder response = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line).append('\n');
			}
		}

		if (status < 200 || status >= 300) {
			throw new IllegalStateException("Erro HTTP " + status + " na consulta TOTVS: "
					+ truncar(response.toString(), 500));
		}

		return response.toString();
	}

	private static void validarConfig(ConfiguracaoRhidEntity config) {
		if (config.getUrlTotvs() == null || config.getUrlTotvs().trim().isEmpty()) {
			throw new IllegalStateException("Informe a URL do wsConsultaSQL TOTVS na configuração RHID.");
		}
		if (config.getUsuarioTotvs() == null || config.getUsuarioTotvs().trim().isEmpty()) {
			throw new IllegalStateException("Informe o usuário TOTVS na configuração RHID.");
		}
		if (config.getSenhaTotvs() == null || config.getSenhaTotvs().trim().isEmpty()) {
			throw new IllegalStateException("Informe a senha TOTVS na configuração RHID.");
		}
	}

	private static String valorOuPadrao(String valor, String padrao) {
		return valor != null && !valor.trim().isEmpty() ? valor.trim() : padrao;
	}

	private static String escapeXml(String value) {
		if (value == null) {
			return "";
		}
		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&apos;");
	}

	private static String truncar(String texto, int max) {
		if (texto == null) {
			return "";
		}
		return texto.length() <= max ? texto : texto.substring(0, max) + "...";
	}
}
