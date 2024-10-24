package com.protreino.luxandserver.to;

public class ResultadoProcessamentoCadastroExterno {
	
	private int codigoResultado;
	private String descricaoResultado;
	private Long idCadastro;
	private Long luxandIdentifier;

	public ResultadoProcessamentoCadastroExterno() {
	}

	public int getCodigoResultado() {
		return codigoResultado;
	}

	public void setCodigoResultado(int codigoResultado) {
		this.codigoResultado = codigoResultado;
	}

	public String getDescricaoResultado() {
		return descricaoResultado;
	}

	public void setDescricaoResultado(String descricaoResultado) {
		this.descricaoResultado = descricaoResultado;
	}

	public Long getIdCadastro() {
		return idCadastro;
	}

	public void setIdCadastro(Long idCadastro) {
		this.idCadastro = idCadastro;
	}

	public Long getLuxandIdentifier() {
		return luxandIdentifier;
	}

	public void setLuxandIdentifier(Long luxandIdentifier) {
		this.luxandIdentifier = luxandIdentifier;
	}
	
}
