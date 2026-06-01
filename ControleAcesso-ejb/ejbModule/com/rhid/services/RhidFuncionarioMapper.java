package com.rhid.services;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.rhid.services.dto.RhidFuncionarioExternoDTO;
import com.rhid.services.dto.RhidPersonDTO;

import br.com.startjob.acesso.modelo.entity.ConfiguracaoRhidEntity;
import br.com.startjob.acesso.modelo.entity.DominioRhidEntity;

public final class RhidFuncionarioMapper {

	private static final SimpleDateFormat SHIFT_DATE = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat ADMISSION_DATE = new SimpleDateFormat("yyyy-MM-dd");

	private RhidFuncionarioMapper() {
	}

	public static RhidPersonDTO toDto(RhidFuncionarioExternoDTO funcionario, ConfiguracaoRhidEntity config,
			DominioRhidEntity dominio, Integer idRhid) {
		RhidPersonDTO dto = new RhidPersonDTO();
		Date referencia = funcionario.getDataAdmissao() != null ? funcionario.getDataAdmissao() : new Date();

		dto.setStatus(funcionario.getStatus() != null ? funcionario.getStatus() : 1);
		dto.setDateShiftsStartStr(SHIFT_DATE.format(referencia));
		dto.setNewIdShift(funcionario.getIdShift());
		dto.setIdCompany(funcionario.getIdCompany());
		dto.setName(funcionario.getNome());
		dto.setCpf(somenteNumeros(funcionario.getCpf()));
		dto.setPis(resolvePis(funcionario));
		dto.setAdmissionDateStr(ADMISSION_DATE.format(referencia));
		dto.setRegistration(funcionario.getMatricula());
		dto.setEmail(funcionario.getEmail());

		if (idRhid != null) {
			dto.setId(idRhid);
		}

		return dto;
	}

	private static String resolvePis(RhidFuncionarioExternoDTO funcionario) {
		if (funcionario.getPis() != null && !funcionario.getPis().trim().isEmpty()) {
			return somenteNumeros(funcionario.getPis());
		}
		String cpf = somenteNumeros(funcionario.getCpf());
		if (cpf != null && cpf.length() >= 11) {
			return cpf.substring(0, 11);
		}
		return "00000000000";
	}

	private static String somenteNumeros(String valor) {
		if (valor == null) {
			return null;
		}
		return valor.replaceAll("\\D", "");
	}
}
