package com.senior.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.senior.services.Enum.SoapOperation;
import com.senior.services.dto.CargoSeniorDto;
import com.senior.services.dto.CentroDeCustoSeniorDto;
import com.senior.services.dto.EmpresaSeniorDto;
import com.senior.services.dto.FuncionarioSeniorDto;

public class IntegracaoSeniorService {

	private final static String url = "http://10.1.1.228:8080/g5-senior-services/rubi_Synccom_senior_g5_rh_fp_customSmart";
	private String usuario;
	private String senha;

	public IntegracaoSeniorService(String usuario, String senha) {
		this.usuario = usuario;
		this.senha = senha;
	}

	// Método comum para realizar a requisição SOAP
	private String enviarSoapRequest(String soapBody) {
		try {
			// Iniciando a conexão
			URL urlSoap = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlSoap.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
			connection.setRequestProperty("Accept", "application/xml");

			// Enviando o XML no corpo da requisição
			try (OutputStream os = connection.getOutputStream()) {
				byte[] input = soapBody.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}

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

	// Método para gerar o body SOAP sem parametro
	// EMPRESAS, PERMISAO, EQUIPAMENTO
	private String gerarSoapBodySemEmpresa(SoapOperation operation) {
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.senior.com.br\">"
				+ "   <soapenv:Header/>" + "   <soapenv:Body>" + "      <ser:" + operation.getOperation() + ">"
				+ "         <user>" + usuario + "</user>" + "         <password>" + senha + "</password>"
				+ "         <encryption>0</encryption>" + "         <parameters>" + "         </parameters>"
				+ "      </ser:" + operation.getOperation() + ">" + "   </soapenv:Body>" + "</soapenv:Envelope>";
	}

	// Método para gerar o body SOAP, recebendo parâmetros de empresa
	// PEDESTRE, CARGO, CENTRO DE CUSTO, LOCAL
	private String gerarSoapBodyComEmpresa(SoapOperation operation, String numEmp) {
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.senior.com.br\">"
				+ "   <soapenv:Header/>" + "   <soapenv:Body>" + "      <ser:" + operation.getOperation() + ">"
				+ "         <user>" + usuario + "</user>" + "         <password>" + senha + "</password>"
				+ "         <encryption>0</encryption>" + "         <parameters>" + "            <numEmp>" + numEmp
				+ "</numEmp>" + "         </parameters>" + "      </ser:" + operation.getOperation() + ">"
				+ "   </soapenv:Body>" + "</soapenv:Envelope>";
	}

	// Método para gerar o body SOAP, recebendo parâmetros de empresa
	// PEDESTRE ADMITIDO, DEMITIDO E ATUALIZADO
	private String gerarSoapBodyComData(SoapOperation operation, String numEmp, String data) {
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.senior.com.br\">"
				+ "   <soapenv:Header/>" + "   <soapenv:Body>" + "      <ser:" + operation.getOperation() + ">"
				+ "         <user>" + usuario + "</user>" + "         <password>" + senha + "</password>"
				+ "         <encryption>0</encryption>" + "         <parameters>" + "            <numEmp>" + numEmp
				+ "</numEmp>" + "            <data>" + data + "</data>" + // Campo de data
				"         </parameters>" + "      </ser:" + operation.getOperation() + ">" + "   </soapenv:Body>"
				+ "</soapenv:Envelope>";
	}

	// busca empresas
	public List<EmpresaSeniorDto> buscarEmpresas() {
		String soapBodyEmpresas = gerarSoapBodySemEmpresa(SoapOperation.EMPRESA);
		String responseXml = enviarSoapRequest(soapBodyEmpresas);
		if (responseXml != null) {
			return parseEmpresasFromXml(responseXml);
		}
		return null;
	}

	// bussca funcionarios
	public List<FuncionarioSeniorDto> buscarFuncionarios(String numEmp) {
		String soapBodyFuncionarios = gerarSoapBodyComEmpresa(SoapOperation.PEDESTRE, numEmp);
		String responseXml = enviarSoapRequest(soapBodyFuncionarios);
		if (responseXml != null) {
			return parseFuncionariosFromXml(responseXml);
		}
		return null;
	}
	
	public List<FuncionarioSeniorDto> buscarFuncionariosAtualizadosNoDia(String numEmp, String d) {
		String soapBodyFuncionarios = gerarSoapBodyComData(SoapOperation.PEDESTRE, numEmp, d);
		String responseXml = enviarSoapRequest(soapBodyFuncionarios);
		if (responseXml != null) {
			return parseFuncionariosFromXml(responseXml);
		}
		return null;
	}

	// bussca funcionarios
	public List<FuncionarioSeniorDto> buscarFuncionariosAdmitidos(String numEmp, String data) {
		String soapBodyFuncionarios = gerarSoapBodyComData(SoapOperation.PEDESTRE_ADMITIDOS, numEmp, data);
		String responseXml = enviarSoapRequest(soapBodyFuncionarios);
		if (responseXml != null) {
			return parseFuncionariosFromXml(responseXml);
		}
		return null;
	}

	// bussca funcionarios
	public List<FuncionarioSeniorDto> buscarFuncionariosDemitidos(String numEmp, String data) {
		String soapBodyFuncionarios = gerarSoapBodyComData(SoapOperation.PEDESTRE_DEMITIDOS, numEmp, data);
		String responseXml = enviarSoapRequest(soapBodyFuncionarios);
		if (responseXml != null) {
			return parseFuncionariosFromXml(responseXml);
		}
		return null;
	}

	// bussca cargo
	public List<CargoSeniorDto> buscaCargos(String numEmp) {
		String soapBodyCargo = gerarSoapBodyComEmpresa(SoapOperation.CARGO, numEmp);
		String responseXml = enviarSoapRequest(soapBodyCargo);
		if (responseXml != null) {
			return parseCargoFromXml(responseXml);
		}
		return null;
	}

	// bussca cargo
	public List<CentroDeCustoSeniorDto> buscaCentroDeCusto(String numEmp) {
		String soapBodyCentroDeCusto = gerarSoapBodyComEmpresa(SoapOperation.CENTRO_CUSTO, numEmp);
		String responseXml = enviarSoapRequest(soapBodyCentroDeCusto);
		if (responseXml != null) {
			return parseCentroCustoFromXml(responseXml);
		}
		return null;
	}

	// Método para extrair empresas do XML de resposta
	private List<EmpresaSeniorDto> parseEmpresasFromXml(String xml) {
		// Implementação de parsing...
		List<EmpresaSeniorDto> empresas = new ArrayList<>();
		String[] retornos = xml.split("<retorno>");
		for (String retornoXml : retornos) {
			if (retornoXml.contains("</retorno>")) {
				EmpresaSeniorDto empresa = new EmpresaSeniorDto();
				empresa.setDddTel(getTagValue("dddTel", retornoXml));
				empresa.setEmaEmp(getTagValue("emaEmp", retornoXml));
				empresa.setNomEmp(getTagValue("nomEmp", retornoXml));
				empresa.setNumEmp(getTagValue("numEmp", retornoXml));
				empresa.setNumIns(getTagValue("numIns", retornoXml));
				empresa.setNumTel(getTagValue("numTel", retornoXml));

				empresas.add(empresa);
			}
		}
		return empresas;
	}

	// Método para extrair empresas do XML de resposta
	private List<CargoSeniorDto> parseCargoFromXml(String xml) {
		// Implementação de parsing...
		List<CargoSeniorDto> cargos = new ArrayList<>();
		String[] retornos = xml.split("<retorno>");
		for (String retornoXml : retornos) {
			if (retornoXml.contains("</retorno>")) {
				CargoSeniorDto cargo = new CargoSeniorDto();
				cargo.setCodigoCargo(getTagValue("codCar", retornoXml));
				cargo.setDataCriacao(getTagValue("datCri", retornoXml));
				cargo.setCargo(getTagValue("titRed", retornoXml));

				cargos.add(cargo);
			}
		}
		return cargos;
	}

	// Método para extrair empresas do XML de resposta
	private List<CentroDeCustoSeniorDto> parseCentroCustoFromXml(String xml) {
		// Implementação de parsing...
		List<CentroDeCustoSeniorDto> centroCustos = new ArrayList<>();
		String[] retornos = xml.split("<retorno>");
		for (String retornoXml : retornos) {
			if (retornoXml.contains("</retorno>")) {
				CentroDeCustoSeniorDto centroCusto = new CentroDeCustoSeniorDto();
				centroCusto.setCodigoCentroCusto(getTagValue("codCcu", retornoXml));
				centroCusto.setDataCriacao(getTagValue("datCri", retornoXml));
				centroCusto.setCentroCusto(getTagValue("nomCcu", retornoXml));

				centroCustos.add(centroCusto);
			}
		}
		return centroCustos;
	}

	// Método para extrair empresas do XML de resposta
	private List<FuncionarioSeniorDto> parseFuncionariosFromXml(String xml) {
		// Implementação de parsing...
		List<FuncionarioSeniorDto> funcionarios = new ArrayList<>();
		String[] retornos = xml.split("<retorno>");
		for (String retornoXml : retornos) {
			if (retornoXml.contains("</retorno>")) {
				FuncionarioSeniorDto funcionario = new FuncionarioSeniorDto();
				funcionario.setCodPrm(getTagValue("codPrm", retornoXml));
				funcionario.setDatAdm(getTagValue("datAdm", retornoXml));
				funcionario.setDatDem(getTagValue("datDem", retornoXml));
				funcionario.setDatNas(getTagValue("datNas", retornoXml));
				funcionario.setEmailComercial(getTagValue("emaCom", retornoXml));
				funcionario.setEmailPessoal(getTagValue("emaPar", retornoXml));
				funcionario.setNome(getTagValue("nomFun", retornoXml));
				funcionario.setEmpresa(getTagValue("numEmp", retornoXml));
				funcionario.setNumeroMatricula(getTagValue("numCad", retornoXml));
				funcionario.setRg(getTagValue("numCid", retornoXml));
				funcionario.setNumCracha(getTagValue("numCra", retornoXml));
				funcionario.setNumEmpresa(getTagValue("numEmp", retornoXml));
				funcionario.setNumFisicoCracha(getTagValue("numFis", retornoXml));
				funcionario.setTipCol(getTagValue("tipCol", retornoXml));
				funcionario.setCargo(getTagValue("titRed", retornoXml));
				funcionario.setDddtelefone(getTagValue("dddTel", retornoXml));
				funcionario.setNumtelefone(getTagValue("numTel", retornoXml));

				funcionarios.add(funcionario);
			}
		}
		return funcionarios;
	}

	// Método auxiliar para pegar o valor de uma tag XML
	private String getTagValue(String tag, String xml) {
		String openTag = "<" + tag + ">";
		String closeTag = "</" + tag + ">";
		int start = xml.indexOf(openTag);
		int end = xml.indexOf(closeTag);

		if (start != -1 && end != -1) {
			return xml.substring(start + openTag.length(), end);
		}
		return null;
	}
}
