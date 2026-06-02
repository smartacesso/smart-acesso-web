package com.rhid.services.fonte.totvs;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.rhid.services.dto.RhidFuncionarioExternoDTO;

/**
 * Utilitários de data para integração TOTVS (granularidade diária, sem hora).
 */
public final class RhidTotvsDataUtil {

	private RhidTotvsDataUtil() {
	}

	public static LocalDate toLocalDate(Date data) {
		if (data == null) {
			return null;
		}
		// java.sql.Date (campos @Temporal DATE do JPA) não suporta toInstant()
		if (data instanceof java.sql.Date) {
			return ((java.sql.Date) data).toLocalDate();
		}
		return data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static Date toDateAtStartOfDay(LocalDate data) {
		if (data == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(data.getYear(), data.getMonthValue() - 1, data.getDayOfMonth(), 0, 0, 0);
		return cal.getTime();
	}

	public static String formatarDataIni(LocalDate data) {
		if (data == null) {
			return null;
		}
		return String.format("%04d-%02d-%02d", data.getYear(), data.getMonthValue(), data.getDayOfMonth());
	}

	/**
	 * DATA_INI do incremental TOTVS: mesma data gravada em {@code ultimaExportacao}.
	 * A sentença SQL filtra por dia (sem hora); reconsultar o dia evita perder alterações
	 * tardias no mesmo dia. Registros já sincronizados são atualizados via PUT (idempotente).
	 */
	public static LocalDate calcularDataIniIncremental(Date ultimaExportacao) {
		return toLocalDate(ultimaExportacao);
	}

	/** @deprecated usar {@link #calcularDataIniIncremental(Date)} — não soma mais 1 dia. */
	public static LocalDate calcularProximaDataIni(Date ultimaExportacao) {
		return calcularDataIniIncremental(ultimaExportacao);
	}

	public static LocalDate maxDataAlteracao(List<RhidFuncionarioExternoDTO> funcionarios) {
		LocalDate max = null;
		if (funcionarios == null) {
			return null;
		}
		for (RhidFuncionarioExternoDTO funcionario : funcionarios) {
			LocalDate d = toLocalDate(funcionario.getDataAlteracao());
			if (d != null && (max == null || d.isAfter(max))) {
				max = d;
			}
		}
		return max;
	}
}
