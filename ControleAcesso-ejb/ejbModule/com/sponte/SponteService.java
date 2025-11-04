package com.sponte;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SponteService {

	private static final String ENDPOINT = "https://api.sponteeducacional.net.br/WSAPIEdu.asmx";
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

	public static List<String> getAlunosPorIntervalo(String token, String codigoCliente, LocalDateTime inicio,
			LocalDateTime fim) throws IOException {
		List<String> respostas = new ArrayList<>();
		LocalDateTime atual = inicio;

		while (!atual.isAfter(fim)) {
			String parametrosBusca = "DataCadastro=" + atual.format(FORMATTER);

			String response = getAlunosSOAP(parametrosBusca, token, codigoCliente);
			respostas.add(response);

			// incrementa 1 dia (ou 1 minuto se precisar mais granular)
			atual = atual.plusDays(1);
		}

		return respostas;
	}

	public static String getAlunosSOAP(String parametrosBusca, String token, String codigoCliente) throws IOException {
		URL url = new URL(ENDPOINT);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		conn.setRequestProperty("SOAPAction", "http://api.sponteeducacional.net.br/GetAlunos");
		conn.setRequestProperty("User-Agent", "Java/17.0.13");
		conn.setDoOutput(true);

		System.out.println("Java Version: " + System.getProperty("java.version"));
		System.out.println("User-Agent: " + conn.getRequestProperty("User-Agent"));

		String xmlRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
				+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
				+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + "  <soap:Body>"
				+ "    <GetAlunos xmlns=\"http://api.sponteeducacional.net.br/\">" + "      <nCodigoCliente>"
				+ codigoCliente + "</nCodigoCliente>" + "      <sToken>" + token + "</sToken>"
				+ "      <sParametrosBusca>" + parametrosBusca + "</sParametrosBusca>" + "    </GetAlunos>"
				+ "  </soap:Body>" + "</soap:Envelope>";

		try (OutputStream os = conn.getOutputStream()) {
			os.write(xmlRequest.getBytes(StandardCharsets.UTF_8));
		}

		return readResponse(conn);
	}

	private static String readResponse(HttpURLConnection conn) throws IOException {
		BufferedReader br;
		if (conn.getResponseCode() >= 400) {
			br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
		} else {
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
		}

		System.out.println("HTTP: " + conn.getResponseCode());
		StringBuilder response = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			response.append(line).append("\n");
		}
		br.close();
		return response.toString();
	}
}