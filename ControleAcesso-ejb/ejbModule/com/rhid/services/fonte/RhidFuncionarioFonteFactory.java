package com.rhid.services.fonte;

import br.com.startjob.acesso.modelo.entity.ConfiguracaoRhidEntity;

/**
 * Factory da fonte de funcionários (TOTVS ou mock).
 */
public final class RhidFuncionarioFonteFactory {

	private RhidFuncionarioFonteFactory() {
	}

	public static RhidFuncionarioFonte criarFonte(ConfiguracaoRhidEntity config) {
		if (config != null && config.getUrlTotvs() != null && !config.getUrlTotvs().trim().isEmpty()) {
			return new RhidFuncionarioFonteTotvs(config);
		}
		return new RhidFuncionarioFonteMock();
	}
}
