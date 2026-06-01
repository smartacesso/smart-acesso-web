package com.rhid.services.fonte;

/**
 * Factory da fonte de funcionários. Trocar implementação quando a API externa estiver pronta.
 */
public final class RhidFuncionarioFonteFactory {

	private RhidFuncionarioFonteFactory() {
	}

	public static RhidFuncionarioFonte criarFonte() {
		// TODO: retornar RhidFuncionarioFonteApi quando a API externa estiver disponível
		return new RhidFuncionarioFonteMock();
	}
}
