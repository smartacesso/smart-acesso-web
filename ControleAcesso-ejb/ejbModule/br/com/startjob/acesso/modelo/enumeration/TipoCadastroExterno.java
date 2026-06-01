package br.com.startjob.acesso.modelo.enumeration;

/**
 * Origem / fluxo do cadastro externo (link facial, 3 fotos, etc.).
 */
public enum TipoCadastroExterno {

	FACIAL_EXTERNO_3FOTOS,
	FACIAL_LINK_PRECADASTRO,
	FACIAL_LINK_CONVITE,
	/** Cadastro iniciado no totem; pode exigir aprovação da empresa visitada. */
	TOTEM_VISITANTE;

}
