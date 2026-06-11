package com.senior.services;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.senior.services.dto.FuncionarioSeniorDto;
import com.senior.services.dto.HorarioPedestreDto;

import br.com.startjob.acesso.modelo.ejb.HorarioSeniorDto;
import br.com.startjob.acesso.modelo.utils.EncryptionUtils;

public final class SeniorSyncUtils {

	private static final String FOLGA = "FOLGA";
	private static final String SEM_INTERVALO = "SEM_INTERVALO";

	private SeniorSyncUtils() {
	}

	public static String nvl(String value) {
		return value == null ? "" : value.trim();
	}

	private static String formatHorario(Date horario) {
		if (horario == null) {
			return "";
		}
		return new SimpleDateFormat("HH:mm").format(horario);
	}

	public static String buildDadosSnapshot(FuncionarioSeniorDto f) {
		if (f == null) {
			return "";
		}
		return String.join("|",
				nvl(f.getNome()),
				nvl(f.getNumeroMatricula()),
				nvl(f.getNumEmpresa()),
				nvl(f.getEmpresa()),
				nvl(f.getNumCracha()),
				nvl(f.getRg()),
				nvl(f.getDddtelefone()),
				nvl(f.getNumtelefone()),
				nvl(f.getDatDem()),
				nvl(f.getDatAfa()),
				nvl(f.getDesAfa()),
				nvl(f.getCodPrm()),
				nvl(f.getUsaRef()));
	}

	public static String buildPermissaoSnapshot(String codPrm, String usaRef) {
		return nvl(codPrm) + "|" + nvl(usaRef);
	}

	public static String buildPermissaoSnapshot(FuncionarioSeniorDto f) {
		if (f == null) {
			return "";
		}
		return buildPermissaoSnapshot(f.getCodPrm(), f.getUsaRef());
	}

	public static String buildEscalaSnapshotFolga() {
		return FOLGA;
	}

	public static String buildEscalaSnapshotSemIntervalo(HorarioPedestreDto escala) {
		if (escala == null) {
			return SEM_INTERVALO;
		}
		return String.join("|",
				SEM_INTERVALO,
				nvl(escala.getIdescala()),
				nvl(escala.getEscalaSenior()),
				nvl(escala.getHorarioSenior()),
				nvl(escala.getIntervaloSenior()));
	}

	public static String buildEscalaSnapshot(HorarioPedestreDto escala, List<HorarioSeniorDto> horarios) {
		if (escala == null) {
			return buildEscalaSnapshotFolga();
		}

		StringBuilder sb = new StringBuilder();
		sb.append(nvl(escala.getIdescala())).append("|");
		sb.append(nvl(escala.getEscalaSenior())).append("|");
		sb.append(nvl(escala.getHorarioSenior())).append("|");
		sb.append(nvl(escala.getHorarioSeniorDesc())).append("|");
		sb.append(nvl(escala.getIntervaloSenior())).append("|");

		if (horarios != null && !horarios.isEmpty()) {
			List<HorarioSeniorDto> ordenados = new ArrayList<>(horarios);
			Collections.sort(ordenados, Comparator
					.comparing((HorarioSeniorDto h) -> nvl(h.getDiaSemana()))
					.thenComparing(h -> formatHorario(h.getInicio()))
					.thenComparing(h -> formatHorario(h.getFim()))
					.thenComparing(h -> nvl(h.getNome())));

			for (HorarioSeniorDto h : ordenados) {
				sb.append(nvl(h.getDiaSemana())).append(":");
				sb.append(formatHorario(h.getInicio())).append(":");
				sb.append(formatHorario(h.getFim())).append(":");
				sb.append(nvl(h.getNome())).append(";");
			}
		}

		return sb.toString();
	}

	public static String computeHash(String snapshot) {
		try {
			return EncryptionUtils.encrypt(snapshot);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new IllegalStateException("Erro ao calcular hash Senior", e);
		}
	}

	public static String computeDadosHash(FuncionarioSeniorDto funcionario) {
		return computeHash(buildDadosSnapshot(funcionario));
	}

	public static String computeEscalaHash(String escalaSnapshot) {
		return computeHash(escalaSnapshot);
	}

}
