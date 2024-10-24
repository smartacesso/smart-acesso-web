package br.com.startjob.acesso.modelo.enumeration;

public enum TipoArquivo {
	
	ARQUIVO_CSV("Arquivo csv"),
	ARQUIVO_TXT("Arquivo txt com ;"),
	ARQUIVO_TXT_COM_TAB("Arquivo txt com tab"),
	ARQUIVO_TXT_COM_ESPACO("Arquivo txt com espaço");
	
	private String descricao;
	
	private TipoArquivo(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
