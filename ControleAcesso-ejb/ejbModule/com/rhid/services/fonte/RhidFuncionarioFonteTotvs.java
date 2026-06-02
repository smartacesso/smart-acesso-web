package com.rhid.services.fonte;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.rhid.services.dto.RhidFuncionarioExternoDTO;
import com.rhid.services.fonte.totvs.RhidTotvsDataUtil;
import com.rhid.services.fonte.totvs.RhidTotvsResultadoParser;
import com.rhid.services.fonte.totvs.RhidTotvsSoapClient;

import br.com.startjob.acesso.modelo.entity.ConfiguracaoRhidEntity;

/**
 * Fonte de funcionários via TOTVS wsConsultaSQL (sentença API.PTO.001).
 * <p>
 * Completa: DATA_INI = data início configurada, ATIVO=1 (somente ativos).<br>
 * Incremental: DATA_INI = ultimaExportacao (mesmo dia), ATIVO=0 (alterações e inativos).
 */
public class RhidFuncionarioFonteTotvs implements RhidFuncionarioFonte {

	private final ConfiguracaoRhidEntity config;

	public RhidFuncionarioFonteTotvs(ConfiguracaoRhidEntity config) {
		this.config = config;
	}

	@Override
	public List<RhidFuncionarioExternoDTO> buscarTodos() {
		LocalDate dataIni = RhidTotvsDataUtil.toLocalDate(config.getDataInicioCompleta());
		if (dataIni == null) {
			dataIni = LocalDate.of(2010, 1, 1);
		}
		return consultar(dataIni, 1);
	}

	@Override
	public List<RhidFuncionarioExternoDTO> buscarAlteradosDesde(Date dataReferencia) {
		LocalDate dataIni = RhidTotvsDataUtil.calcularDataIniIncremental(dataReferencia);
		if (dataIni == null) {
			return buscarTodos();
		}
		return consultar(dataIni, 0);
	}

	private List<RhidFuncionarioExternoDTO> consultar(LocalDate dataIni, int ativo) {
		try {
			String parameters = "DATA_INI=" + RhidTotvsDataUtil.formatarDataIni(dataIni) + ";ATIVO=" + ativo;
			String resposta = RhidTotvsSoapClient.consultar(config, parameters);
			List<RhidFuncionarioExternoDTO> lista = RhidTotvsResultadoParser.parse(resposta);
			return lista != null ? lista : Collections.emptyList();
		} catch (Exception e) {
			throw new RuntimeException("Falha ao consultar funcionários TOTVS (DATA_INI="
					+ RhidTotvsDataUtil.formatarDataIni(dataIni) + ", ATIVO=" + ativo + "): "
					+ (e.getMessage() != null ? e.getMessage() : "erro desconhecido"), e);
		}
	}
}
