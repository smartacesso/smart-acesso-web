package com.totvs.services;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.totvs.dto.CadastroDTO;

public class IntegracaoTotvsEducacionalService {

    private static final String ENDPOINT_CONSULTA = "https://inspetoriasao142787.rm.cloudtotvs.com.br:1503/wsConsultaSQL/IwsConsultaSQL";
    private static final String USER = "suporte.smart";
    private static final String PASS = "Senha@1234";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    
    public String getCadastros(Date lastUpdate) {
        String codSentenca = "RDC.API.002";
        String codColigada = "0";
        String codSistema = "S";
        String parameters;
             	
        if(Objects.isNull(lastUpdate)) {
            // 01/01/ANO ATUAL 00:00:00
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            String dataDefault = sdf.format(calendar.getTime());
        	
        	parameters = "$CODCOLIGADA=1;$FILTRAR_STATUS="+ "1" + ";$DATAALTERACAO=" + dataDefault + ";$FILTRAR_SITUACAO_FUNC="+ "1";
        	System.out.println("Primeira importacao. Apenas ativos");
        	
        } else {
        	String dataFormatada = sdf.format(lastUpdate);
        	parameters = "$CODCOLIGADA=1;$FILTRAR_STATUS="+ "0" + ";$DATAALTERACAO=" + dataFormatada + ";$FILTRAR_SITUACAO_FUNC="+ "0";
        	System.out.println("Atualizando...");
        }

        // Monta a string base64 para Basic Auth
        String auth = Base64.getEncoder().encodeToString((USER + ":" + PASS).getBytes(StandardCharsets.UTF_8));

        String soapRequest =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
              + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tot=\"http://www.totvs.com/\">"
              + "   <soapenv:Header/>"
              + "   <soapenv:Body>"
              + "      <tot:RealizarConsultaSQL>"
              + "         <tot:codSentenca>" + codSentenca + "</tot:codSentenca>"
              + "         <tot:codColigada>" + codColigada + "</tot:codColigada>"
              + "         <tot:codSistema>" + codSistema + "</tot:codSistema>"
              + "         <tot:parameters>" + parameters + "</tot:parameters>"
              + "      </tot:RealizarConsultaSQL>"
              + "   </soapenv:Body>"
              + "</soapenv:Envelope>";

        try {
            URL url = new URL(ENDPOINT_CONSULTA); // ex: https://.../wsConsultaSQL/IwsConsultaSQL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST"); 
            conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
            conn.setRequestProperty("Accept", "text/xml");
            conn.setRequestProperty("SOAPAction", "\"http://www.totvs.com/IwsConsultaSQL/RealizarConsultaSQL\""); 

            // 🔐 Adiciona Basic Auth
            conn.setRequestProperty("Authorization", "Basic " + auth);

            conn.setDoOutput(true);

            byte[] xmlBytes = soapRequest.getBytes(StandardCharsets.UTF_8);
            conn.setRequestProperty("Content-Length", String.valueOf(xmlBytes.length));

            try (OutputStream os = conn.getOutputStream()) {
                os.write(xmlBytes);
            }

            int status = conn.getResponseCode();
            System.out.println("HTTP Code: " + status);

            InputStream is = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();
            if (is == null) {
                System.out.println("Nenhum conteúdo retornado pelo servidor.");
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            reader.close();

            return response.toString();

        } catch (Exception e) {
            System.err.println("Erro ao realizar consulta SQL:");
            e.printStackTrace();
            return null;
        }
    }
    
	public List<CadastroDTO> parseCadastros(String soapResponse) throws Exception {
		List<CadastroDTO> lista = new ArrayList<>();

		// 1️⃣ Extrai o trecho do XML interno (<NewDataSet>...</NewDataSet>)
		int start = soapResponse.indexOf("&lt;NewDataSet&gt;");
		int end = soapResponse.indexOf("&lt;/NewDataSet&gt;") + "&lt;/NewDataSet&gt;".length();
		if (start == -1 || end == -1) {
			System.out.println("Nenhum resultado encontrado no XML.");
			return lista;
		}

		String innerXml = soapResponse.substring(start, end);
		// Converte entidades HTML (&lt; &gt;) para XML real
		innerXml = innerXml.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");

		// 2️⃣ Faz o parse do XML convertido
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(false);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(innerXml.getBytes(StandardCharsets.UTF_8)));

		NodeList resultados = doc.getElementsByTagName("Resultado");

		// 3️⃣ Itera sobre cada <Resultado>
		for (int i = 0; i < resultados.getLength(); i++) {
			Element resultado = (Element) resultados.item(i);

			CadastroDTO dto = new CadastroDTO();

			// Numéricos (usa parseInt com fallback para 0 caso vazio)
			dto.setCodColigada(parseIntSafe(getTag(resultado, "CODCOLIGADA")));
			dto.setCodFilial(parseIntSafe(getTag(resultado, "CODFILIAL")));
			dto.setCodResponsavel(parseIntSafe(getTag(resultado, "CODRESPONSAVEL")));
			dto.setIdHabilitacaoFilial(parseIntSafe(getTag(resultado, "IDHABILITACAOFILIAL")));
			dto.setIdPerLet(parseIntSafe(getTag(resultado, "IDPERLET")));

			// Strings
			dto.setNomeColigada(getTag(resultado, "NOMECOLIGADA"));
			dto.setCnpjColigada(getTag(resultado, "CNPJCOLIGADA"));
			dto.setNomeFilial(getTag(resultado, "NOMEFILIAL"));
			dto.setCnpjFilial(getTag(resultado, "CNPJFILIAL"));
			dto.setNome(getTag(resultado, "NOME"));
			dto.setMatricula(getTag(resultado, "MATRICULA"));
			dto.setCpf(getTag(resultado, "CPF"));
			dto.setNomeResponsavel(getTag(resultado, "NOMERESPONSAVEL"));
			dto.setCpfCnpfResponsavel(getTag(resultado, "CPFCNPFRESPONSAVEL"));
			dto.setCodStatus(getTag(resultado, "CODSTATUS"));
			dto.setStatus(getTag(resultado, "STATUS"));
			dto.setNivelEnsino(getTag(resultado, "NIVELENSINO"));
			dto.setOrigem(getTag(resultado, "ORIGEM"));

			// Data (converte string para Date)
			dto.setDataAlteracao(parseDateSafe(getTag(resultado, "DATAALTERACAO")));

			lista.add(dto);
		}

		return lista;
	}

    private String getTag(org.w3c.dom.Element element, String tagName) {
        NodeList list = element.getElementsByTagName(tagName);
        return (list.getLength() > 0) ? list.item(0).getTextContent() : "";
    }
    
    private int parseIntSafe(String value) {
        try {
            return (value != null && !value.trim().isEmpty()) ? Integer.parseInt(value.trim()) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Date parseDateSafe(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            // O formato do TOTVS é 2025-04-22T15:24:09
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(value.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
