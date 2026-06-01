package br.com.startjob.acesso.modelo.enumeration;

public enum StatusCadastroExterno {

	AGUARDANDO_CADASTRO,
	CADASTRADO,
	PROCESSADO,
	ERRO,
	/** Totem: aguardando aprovação da empresa visitada (foto ainda não enviada aos faciais). */
	AGUARDANDO_APROVACAO,
	/** Totem: aprovado e visitante liberado. */
	APROVADO,
	/** Totem: recusado pela empresa/recepção. */
	RECUSADO;
	
	public String getNomeFormated() {
		if (AGUARDANDO_CADASTRO.equals(this)) {
			return "Aguardando cadastro";
		}
		if (CADASTRADO.equals(this)) {
			return "Cadastro em validação";
		}
		if (PROCESSADO.equals(this)) {
			return "Processado";
		}
		if (ERRO.equals(this)) {
			return "Erro";
		}
		if (AGUARDANDO_APROVACAO.equals(this)) {
			return "Aguardando aprovação";
		}
		if (APROVADO.equals(this)) {
			return "Aprovado";
		}
		if (RECUSADO.equals(this)) {
			return "Recusado";
		}
		return "";
	}
	
}
