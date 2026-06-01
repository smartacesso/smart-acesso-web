package com.rhid.services;

/**
 * Comportamento quando o funcionário da fonte externa não informa nomeDominio.
 */
public enum RhidSemDominioAcaoEnum {

	/** Não exporta o funcionário; registra mensagem no resultado. */
	IGNORAR("Ignorar funcionário"),

	/** Envia o funcionário para todos os domínios cadastrados na configuração. */
	ENVIAR_TODOS("Enviar para todos os domínios");

	private final String label;

	RhidSemDominioAcaoEnum(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
