package br.com.startjob.acesso.modelo.enumeration;

public enum Genero {
	
	MASCULINO("Masculino"),
	FEMININO("Feminino");
	
	private String descricao;
	
	private Genero(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
