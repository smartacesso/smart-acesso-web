package br.com.startjob.acesso.modelo.enumeration;

/**
 * Origem / fluxo do cadastro externo (link facial, 3 fotos, etc.).
 */
public enum TipoCadastroExterno {

	FACIAL_EXTERNO_3FOTOS,
	FACIAL_LINK_PRECADASTRO,
	FACIAL_LINK_CONVITE,
	/** Cadastro iniciado no totem; pode exigir aprovação da empresa visitada. */
	TOTEM_VISITANTE,
	/** Falha no cadastro interno (tela cadastro simplificado). */
	CADASTRO_INTERNO_SIMPLIFICADO,
	/** Falha no cadastro interno (tela cadastro pedestre completo). */
	CADASTRO_INTERNO_COMPLETO;

	public String getNomeFormated() {
		if (FACIAL_EXTERNO_3FOTOS.equals(this)) {
			return "Facial externo (3 fotos)";
		}
		if (FACIAL_LINK_PRECADASTRO.equals(this)) {
			return "Link pré-cadastro";
		}
		if (FACIAL_LINK_CONVITE.equals(this)) {
			return "Link convite";
		}
		if (TOTEM_VISITANTE.equals(this)) {
			return "Totem";
		}
		if (CADASTRO_INTERNO_SIMPLIFICADO.equals(this)) {
			return "Cadastro simplificado";
		}
		if (CADASTRO_INTERNO_COMPLETO.equals(this)) {
			return "Cadastro completo";
		}
		return name();
	}

}
