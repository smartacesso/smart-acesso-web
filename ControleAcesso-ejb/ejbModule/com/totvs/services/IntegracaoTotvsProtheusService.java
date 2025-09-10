package com.totvs.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.gson.Gson;
import com.totvs.dto.FuncionarioTotvsDto;

import br.com.startjob.acesso.modelo.entity.ClienteEntity;

public class IntegracaoTotvsProtheusService {

	//private final static String getFuncionariosUrl = "/rh/v1/employeeDataContent?product=Protheus&companyid=01&branchId=01";
	private final static Gson gson = new Gson();
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss");
	
	private final String baseUrl; //= "http://10.1.2.63:9500/rest";
	private final String basicAuth;


	public IntegracaoTotvsProtheusService(ClienteEntity cliente) {
		this.basicAuth = cliente.getIntegracaoTotvs().getSenha();
		this.baseUrl = cliente.getIntegracaoTotvs().getUrl();
	}
	
	private String getFuncionarios() {
	    try {
	    	System.out.println(">> Buscando funcionanário");
	        final URL url = new URL("http://10.1.2.63:8300/ws/WS_RH.apw"); //"http://10.1.2.63:8300/ws/WS_RH.apw"
	        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	        connection.setDoOutput(true);
	        connection.setRequestMethod("POST");

	        // Cabeçalhos
	        connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
	        connection.setRequestProperty("SOAPAction", "http://10.1.2.63:8300/ws/WS_RH.apw/VIEWSRA");
	        connection.setRequestProperty("Authorization", "Basic dXNyX3Jlc3RfcHJvZDp3QUVBVTJ1YQ=="); //"Basic dXNyX3Jlc3RfcHJvZDp3QUVBVTJ1YQ=="
	        connection.setRequestProperty("Cookie", "SESSIONID=578363793a9afc639f9505fe8ebd79fd");

	        // Corpo SOAP
	        String xmlRequest =
	            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
	                                 "xmlns:wsr=\"http://10.1.2.63:8300/ws/WS_RH.apw\">" +
	                "<soapenv:Header/>" +
	                "<soapenv:Body>" +
	                    "<wsr:VIEWSRA>" +
	                        "<wsr:FILTRO></wsr:FILTRO>" +
	                    "</wsr:VIEWSRA>" +
	                "</soapenv:Body>" +
	            "</soapenv:Envelope>";

	        // Envia a requisição
	        try (OutputStream os = connection.getOutputStream()) {
	            byte[] input = xmlRequest.getBytes("utf-8");
	            os.write(input, 0, input.length);
	        }

	        // Resposta
	        int responseCode = connection.getResponseCode();
	        System.out.println("Response Code: " + responseCode);

	        BufferedReader reader;
	        if (responseCode == 200) {
	            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        } else {
	            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
	        }

	        StringBuilder response = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            response.append(line);
	        }
	        reader.close();

	        return response.toString();

	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}


	public List<FuncionarioTotvsDto> buscarFuncionarios(final Date lastUpdate) {
	    String responseXml = getFuncionarios(); // ou com parâmetro formatado

	    if (responseXml != null && !responseXml.isEmpty()) {
	        List<FuncionarioTotvsDto> funcionarios = new ArrayList<>();

	        try {
	            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder builder = factory.newDocumentBuilder();
	            InputSource is = new InputSource(new StringReader(responseXml));
	            Document doc = builder.parse(is);
	            NodeList wsretrhList = doc.getElementsByTagName("WSRETRH");

	            for (int i = 0; i < wsretrhList.getLength(); i++) {
	                Element element = (Element) wsretrhList.item(i);
	                FuncionarioTotvsDto dto = new FuncionarioTotvsDto();

	                dto.setCodigoHorario(getElementValue(element, "CODIGOHORARIO"));
	                dto.setHoraFinal(getElementValue(element, "HORAFINAL"));
	                dto.setHoraInicial(getElementValue(element, "HORAINICIAL"));
	                dto.setMatricula(getElementValue(element, "MATRICULA"));
	                dto.setNome(getElementValue(element, "NOME"));
	                dto.setNomeHorario(getElementValue(element, "NOMEHORARIO"));
	                dto.setSituacaoFolha(getElementValue(element, "SITUACAOFOLHA"));
	                dto.setStatusTrabalho(getElementValue(element, "STATUSTRABALHO"));
	                
	                funcionarios.add(dto);
	            }

	            return funcionarios;

	        } catch (Exception e) {
	            e.printStackTrace(); // ou logar corretamente
	        }
	    }

	    return null;
	}

	private String getElementValue(Element parent, String tagName) {
	    NodeList nodeList = parent.getElementsByTagName(tagName);
	    if (nodeList != null && nodeList.getLength() > 0) {
	        Node node = nodeList.item(0);
	        return node.getTextContent();
	    }
	    return null;
	}

	
}
