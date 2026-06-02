package com.rhid.services.fonte.totvs;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.rhid.services.RhidPisUtil;
import com.rhid.services.dto.RhidFuncionarioExternoDTO;

/**
 * Converte o XML interno (NewDataSet/Resultado) retornado pelo wsConsultaSQL TOTVS.
 */
public final class RhidTotvsResultadoParser {

	private static final SimpleDateFormat FMT_DATA = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	private RhidTotvsResultadoParser() {
	}

	public static List<RhidFuncionarioExternoDTO> parse(String soapResponse) throws Exception {
		List<RhidFuncionarioExternoDTO> lista = new ArrayList<>();
		if (soapResponse == null || soapResponse.trim().isEmpty()) {
			return lista;
		}

		int start = soapResponse.indexOf("&lt;NewDataSet&gt;");
		if (start < 0) {
			start = soapResponse.indexOf("<NewDataSet>");
		}
		int end = soapResponse.indexOf("&lt;/NewDataSet&gt;");
		int endLen = "&lt;/NewDataSet&gt;".length();
		if (end < 0) {
			end = soapResponse.indexOf("</NewDataSet>");
			endLen = "</NewDataSet>".length();
		}

		if (start < 0 || end < 0) {
			return lista;
		}

		String innerXml = soapResponse.substring(start, end + endLen);
		innerXml = innerXml.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(false);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(innerXml.getBytes(StandardCharsets.UTF_8)));

		NodeList resultados = doc.getElementsByTagName("Resultado");
		for (int i = 0; i < resultados.getLength(); i++) {
			Element resultado = (Element) resultados.item(i);
			RhidFuncionarioExternoDTO dto = mapearResultado(resultado);
			if (dto != null) {
				lista.add(dto);
			}
		}
		return lista;
	}

	private static RhidFuncionarioExternoDTO mapearResultado(Element resultado) {
		String cpf = RhidPisUtil.normalizarCpf11(getTag(resultado, "CPF"));
		if (cpf == null) {
			cpf = RhidPisUtil.normalizarCpf11(getTag(resultado, "IDEXTERNO"));
		}
		if (cpf == null) {
			return null;
		}

		RhidFuncionarioExternoDTO dto = new RhidFuncionarioExternoDTO();
		dto.setIdExterno(cpf);
		dto.setCpf(cpf);
		dto.setNome(getTag(resultado, "NOME"));
		dto.setPis(somenteNumeros(getTag(resultado, "PIS")));
		dto.setMatricula(getTag(resultado, "MATRICULA"));
		dto.setStatus(parseIntSafe(getTag(resultado, "STATUS"), 1));
		dto.setDataAdmissao(parseDateSafe(getTag(resultado, "DATAADMISSAO")));
		dto.setDataAlteracao(parseDateSafe(getTag(resultado, "DATAALTERACAO")));
		dto.setCnpjEmpresa(getTag(resultado, "CNPJEMPRESA"));
		dto.setNomeHorario(getTag(resultado, "NOMEHORARIO"));
		dto.setNomeDepartamento(getTag(resultado, "NOMEDEPARTAMENTO"));
		dto.setNomeCargo(getTag(resultado, "NOMECARGO"));
		dto.setDataNascimento(parseDateSafe(getTag(resultado, "DATANASCIMENTO")));
		dto.setRg(getTag(resultado, "RG"));
		dto.setCtps(getTag(resultado, "CTPS"));
		dto.setNumFolha(getTag(resultado, "NUMFOLHA"));
		dto.setAdmin(parseBooleanSafe(getTag(resultado, "ADMIN")));
		dto.setEndereco(getTag(resultado, "ENDERECO"));
		dto.setBairro(getTag(resultado, "BAIRRO"));
		dto.setCidade(getTag(resultado, "CIDADE"));
		dto.setUf(getTag(resultado, "UF"));
		dto.setCep(getTag(resultado, "CEP"));
		dto.setTelefone(getTag(resultado, "TELEFONE"));

		// Códigos RM — não são IDs internos do RHID; resolução via catálogo por CNPJ/nomes.
		dto.setIdShift(null);
		dto.setIdDepartment(null);
		dto.setIdPersonRole(null);
		dto.setIdCompany(null);

		return dto;
	}

	private static String getTag(Element element, String tagName) {
		NodeList list = element.getElementsByTagName(tagName);
		if (list.getLength() == 0) {
			return null;
		}
		String text = list.item(0).getTextContent();
		return text != null ? text.trim() : null;
	}

	private static Date parseDateSafe(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		try {
			synchronized (FMT_DATA) {
				return FMT_DATA.parse(value.trim());
			}
		} catch (Exception e) {
			return null;
		}
	}

	private static int parseIntSafe(String value, int padrao) {
		if (value == null || value.trim().isEmpty()) {
			return padrao;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			return padrao;
		}
	}

	private static Boolean parseBooleanSafe(String value) {
		if (value == null || value.trim().isEmpty()) {
			return Boolean.FALSE;
		}
		return "1".equals(value.trim()) || "true".equalsIgnoreCase(value.trim());
	}

	private static String somenteNumeros(String valor) {
		if (valor == null || valor.trim().isEmpty()) {
			return null;
		}
		String numeros = valor.replaceAll("\\D", "");
		return numeros.isEmpty() ? null : numeros;
	}
}
