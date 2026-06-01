package com.rhid.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import com.rhid.services.dto.RhidFuncionarioExternoDTO;

import br.com.startjob.acesso.modelo.entity.ConfiguracaoRhidEntity;
import br.com.startjob.acesso.modelo.entity.DominioRhidEntity;

/**
 * Monta o JSON no formato esperado pelo endpoint /customerdb/person.svc/a.
 */
public final class RhidPersonPayloadBuilder {

	private static final SimpleDateFormat SHIFT_DATE = new SimpleDateFormat("yyyyMMdd");

	private RhidPersonPayloadBuilder() {
	}

	public static String toCreatePayload(RhidFuncionarioExternoDTO funcionario, ConfiguracaoRhidEntity config,
			DominioRhidEntity dominio) {
		Date referencia = funcionario.getDataAdmissao() != null ? funcionario.getDataAdmissao() : new Date();
		JsonObjectBuilder json = basePayload(funcionario, config, dominio, referencia, true);
		json.add("dateShiftsStartStr", SHIFT_DATE.format(referencia));
		json.add("newIdShift", funcionario.getIdShift());
		json.add("admissionDate", formatDotNetDate(referencia));
		json.addNull("admissionDateStr");
		return json.build().toString();
	}

	public static String toUpdatePayload(RhidFuncionarioExternoDTO funcionario, ConfiguracaoRhidEntity config,
			DominioRhidEntity dominio, Integer idRhid) {
		Date referencia = funcionario.getDataAdmissao() != null ? funcionario.getDataAdmissao() : new Date();
		JsonObjectBuilder json = basePayload(funcionario, config, dominio, referencia, false);
		json.add("id", idRhid);
		json.addNull("dateShiftsStartStr");
		json.addNull("newIdShift");
		json.add("admissionDate", formatDotNetDate(referencia));
		json.addNull("admissionDateStr");
		return json.build().toString();
	}

	private static JsonObjectBuilder basePayload(RhidFuncionarioExternoDTO funcionario, ConfiguracaoRhidEntity config,
			DominioRhidEntity dominio, Date referencia, boolean criacao) {
		JsonObjectBuilder json = Json.createObjectBuilder();

		json.add("excluded", false);
		json.add("admin", Boolean.TRUE.equals(funcionario.getAdmin()));
		json.add("applyAllGeofences", true);
		json.add("status", funcionario.getStatus() != null ? funcionario.getStatus() : 1);
		json.add("name", funcionario.getNome());
		json.add("idCompany", funcionario.getIdCompany());
		adicionarInteiroOpcional(json, "idDepartment", funcionario.getIdDepartment());
		adicionarInteiroOpcional(json, "idCostCenter", funcionario.getIdCostCenter());
		json.add("pis", resolvePisLong(funcionario));
		adicionarCpf(json, funcionario.getCpf());
		json.add("registration", textoOuVazio(funcionario.getMatricula()));
		adicionarEmail(json, funcionario.getEmail());
		adicionarTextoOpcional(json, "numFolha", funcionario.getNumFolha());
		adicionarTextoOpcional(json, "ctps", funcionario.getCtps());
		adicionarTextoOpcional(json, "rg", funcionario.getRg());
		adicionarTextoOpcional(json, "city", funcionario.getCidade());
		adicionarTextoOpcional(json, "address", funcionario.getEndereco());
		adicionarTextoOpcional(json, "district", funcionario.getBairro());
		adicionarTextoOpcional(json, "state", funcionario.getUf());
		adicionarTextoOpcional(json, "zip", funcionario.getCep());
		adicionarTextoOpcional(json, "phone", funcionario.getTelefone());
		adicionarInteiroOpcional(json, "idPersonRole", funcionario.getIdPersonRole());
		adicionarTextoOpcional(json, "idPersonBoss", funcionario.getIdPersonBoss());
		adicionarDataOpcional(json, "dateOfBirth", funcionario.getDataNascimento());

		json.add("area", 0);
		json.add("barcode", "");
		json.add("cardCode", 0);
		json.add("codigo", 0);
		json.add("password", 0);
		json.add("pin", 0);
		json.add("rfid", 0);
		json.add("saldoBancoHoras", 0);
		json.add("viaCracha", 1);
		json.add("idShiftAtual", 0);
		json.add("fotoPontoObrigatoriaWeb", true);
		json.add("genericShiftStartDateSelector", false);
		json.add("menuHistorico", true);
		json.add("menuInformacoesGerais", false);
		json.add("menuPontoDiario", false);
		json.add("menuQuiosque", true);
		json.add("menuSolicitacoes", true);
		json.add("permitirAlteracaoWeb", false);
		json.add("permitirMarcacaoWeb", false);
		json.add("useGeofencing", false);

		if (criacao) {
			json.add("statusStr", funcionario.getStatus() != null && funcionario.getStatus() == 0 ? "Inativo" : "Ativo");
		}

		return json;
	}

	private static void adicionarTextoOpcional(JsonObjectBuilder json, String campo, String valor) {
		if (valor == null || valor.trim().isEmpty()) {
			json.addNull(campo);
		} else {
			json.add(campo, valor.trim());
		}
	}

	private static void adicionarInteiroOpcional(JsonObjectBuilder json, String campo, Integer valor) {
		if (valor == null) {
			json.addNull(campo);
		} else {
			json.add(campo, valor);
		}
	}

	private static void adicionarDataOpcional(JsonObjectBuilder json, String campo, Date valor) {
		if (valor == null) {
			json.addNull(campo);
		} else {
			json.add(campo, formatDotNetDate(valor));
		}
	}

	private static void adicionarCpf(JsonObjectBuilder json, String cpf) {
		String numeros = RhidPisUtil.normalizarCpf11(cpf);
		if (numeros == null) {
			json.addNull("cpf");
			return;
		}
		json.add("cpf", Long.parseLong(numeros));
	}

	private static void adicionarEmail(JsonObjectBuilder json, String email) {
		if (email == null || email.trim().isEmpty()) {
			json.addNull("email");
		} else {
			json.add("email", email.trim());
		}
	}

	private static String formatDotNetDate(Date date) {
		TimeZone tz = TimeZone.getTimeZone("America/Sao_Paulo");
		int offsetMs = tz.getOffset(date.getTime());
		int offsetHours = offsetMs / (60 * 60 * 1000);
		int offsetMinutes = Math.abs((offsetMs / (60 * 1000)) % 60);
		String sinal = offsetHours >= 0 ? "+" : "-";
		String offset = String.format("%s%02d%02d", sinal, Math.abs(offsetHours), offsetMinutes);
		return "/Date(" + date.getTime() + offset + ")/";
	}

	private static long resolvePisLong(RhidFuncionarioExternoDTO funcionario) {
		return RhidPisUtil.resolverPisNumerico(funcionario);
	}

	private static String textoOuVazio(String valor) {
		return valor != null ? valor : "";
	}
}
