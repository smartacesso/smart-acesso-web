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
import com.senior.services.dto.HorarioPedestreDto;

import br.com.startjob.acesso.modelo.ejb.HorarioSeniorDto;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;

public class IntegracaoSeniorService {

	private String url;
	private String usuario;
	private String senha;

	public IntegracaoSeniorService(ClienteEntity cliente) {
		this.url = cliente.getIntegracaoSenior().getUrl();
		this.usuario = cliente.getIntegracaoSenior().getUsuario();
		this.senha = cliente.getIntegracaoSenior().getSenha();
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
	
	
	private String gerarSoapBodyComEscala(SoapOperation operation, String escala) {
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.senior.com.br\">"
				+ "   <soapenv:Header/>" + "   <soapenv:Body>" + "      <ser:" + operation.getOperation() + ">"
				+ "         <user>" + usuario + "</user>" + "         <password>" + senha + "</password>"
				+ "         <encryption>0</encryption>" + "         <parameters>" + "            <escala>" + escala
				+ "</escala>" + "         </parameters>" + "      </ser:" + operation.getOperation() + ">"
				+ "   </soapenv:Body>" + "</soapenv:Envelope>";
	}
	
	
	private String gerarSoapBodyComHorarioPedestre(SoapOperation operation, String data, String numCad, Integer numEmp, Integer tipCol) {
	    return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.senior.com.br\">"
	            + "   <soapenv:Header/>"
	            + "   <soapenv:Body>"
	            + "      <ser:" + operation.getOperation() + ">"
	            + "         <user>" + usuario + "</user>"
	            + "         <password>" + senha + "</password>"
	            + "         <encryption>0</encryption>"
	            + "         <parameters>"
	            + "            <data>" + data + "</data>"
	            + "            <numCad>" + numCad + "</numCad>"
	            + "            <numEmp>" + numEmp + "</numEmp>"
	            + "            <tipCol>" + tipCol + "</tipCol>"
	            + "         </parameters>"
	            + "      </ser:" + operation.getOperation() + ">"
	            + "   </soapenv:Body>"
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

	// bussca cerntro de custo
	public List<CentroDeCustoSeniorDto> buscaCentroDeCusto(String numEmp) {
		String soapBodyCentroDeCusto = gerarSoapBodyComEmpresa(SoapOperation.CENTRO_CUSTO, numEmp);
		String responseXml = enviarSoapRequest(soapBodyCentroDeCusto);
		if (responseXml != null) {
			return parseCentroCustoFromXml(responseXml);
		}
		return null;
	}
	
	// busca horarios
	public List<HorarioSeniorDto> buscarHorarios(String escala) {
		String soapBodyEmpresas = gerarSoapBodyComEscala(SoapOperation.HORARIOS, escala);
		String responseXml = enviarSoapRequest(soapBodyEmpresas);
		if (responseXml != null) {
			return parseHorarioFromXml(responseXml);
		}
		return null;
	}
	
	// busca horarios pedestre
	public List<HorarioPedestreDto> buscarHorariosPedestre(String data, String numCad, Integer numEmp, Integer tipCol) {
		String soapBodyEmpresas = gerarSoapBodyComHorarioPedestre(SoapOperation.HORARIO_PEDESTRE, data, numCad, numEmp, tipCol);
		String responseXml = enviarSoapRequest(soapBodyEmpresas);
		if (responseXml != null) {
			return parseHorarioPedestreFromXml(responseXml);
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
				funcionario.setDatAfa(getTagValue("datAfa", retornoXml));
				funcionario.setDatTer(getTagValue("datTer", retornoXml));
				funcionario.setDatAlt(getTagValue("datAlt", retornoXml));
				funcionario.setDatDem(getTagValue("datDem", retornoXml));
				funcionario.setDatNas(getTagValue("datNas", retornoXml));
				funcionario.setDesAfa(getTagValue("desAfa", retornoXml));
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
				funcionario.setUsaRef(getTagValue("usaRef", retornoXml));

				funcionarios.add(funcionario);
			}
		}
		return funcionarios;
	}
	
	
	// Método para extrair horarios do XML de resposta
	private List<HorarioSeniorDto> parseHorarioFromXml(String xml) {
		// Implementação de parsing...
		List<HorarioSeniorDto> horarios = new ArrayList<>();
		String[] retornos = xml.split("<retorno>");
		for (String retornoXml : retornos) {
			if (retornoXml.contains("</retorno>")) {
				HorarioSeniorDto horario = new HorarioSeniorDto();
				horario.setIdEscala(getTagValue("escala", retornoXml));
				horario.setIdHorario(getTagValue("horario", retornoXml));
				horario.setDiaSemana(getTagValue("diasemana", retornoXml));
				horario.setInicio(getTagValue("inicio", retornoXml));
				horario.setFim(getTagValue("fim", retornoXml));
				horario.setNome(getTagValue("nome", retornoXml));
				horarios.add(horario);
			}
		}
		return horarios;
	}
	
	
	// Método para extrair horarios do XML de resposta
	private List<HorarioPedestreDto> parseHorarioPedestreFromXml(String xml) {
		// Implementação de parsing...
		List<HorarioPedestreDto> horariosPedestre = new ArrayList<>();
		String[] retornos = xml.split("<retorno>");
		for (String retornoXml : retornos) {
			if (retornoXml.contains("</retorno>")) {
				HorarioPedestreDto horarioPed = new HorarioPedestreDto();
				horarioPed.setIdescala(getTagValue("escala", retornoXml));
	
				horariosPedestre.add(horarioPed);
			}
		}
		return horariosPedestre;
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
