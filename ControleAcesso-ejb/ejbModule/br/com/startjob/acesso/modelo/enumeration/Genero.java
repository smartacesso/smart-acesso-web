package br.com.startjob.acesso.modelo.enumeration;

public enum Genero {

	MASCULINO("Masculino"), FEMININO("Feminino");

	private String descricao;

	private Genero(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public static Genero fromString(String valor) {
		if (valor == null || valor.trim().isEmpty()) {
			return null; // ou lance exceção
		}

		for (Genero genero : Genero.values()) {
			if (genero.descricao.equalsIgnoreCase(valor.trim())) {
				return genero;
			}
		}

		throw new IllegalArgumentException("Gênero inválido: " + valor);
	}
}
