package br.com.startjob.acesso.modelo.enumeration;

public enum StatusCadastroExterno {

	AGUARDANDO_CADASTRO,
	CADASTRADO,
	PROCESSADO,
	ERRO;
	
	public String getNomeFormated() {
		if(AGUARDANDO_CADASTRO.equals(this))
			return "Aguardando cadastro";
		else if(CADASTRADO.equals(this))
			return "Cadastro em validação";
		else if(PROCESSADO.equals(this))
			return "Processado";
		else if(ERRO.equals(this))
			return "Erro";
		return "";
	}
	
}
