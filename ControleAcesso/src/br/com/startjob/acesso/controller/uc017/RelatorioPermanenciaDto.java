package br.com.startjob.acesso.controller.uc017;

public class RelatorioPermanenciaDto {
	
	private Long idPedestre;
	private String nome;
	private Long tempoTotal; // Armazena em segundos
	private String tempoTotalFormatado; // Armazena como "HH:mm:ss"
	private Long cotaMensal;
	private Long saldoRestante;
	private String saldoRestanteFormatado;
	private String cotaMensalFormatada;

	public String getCotaMensalFormatada() {
	    return cotaMensalFormatada;
	}

	public void setCotaMensalFormatada(String cotaMensalFormatada) {
	    this.cotaMensalFormatada = cotaMensalFormatada;
	}

	
	public Long getIdPedestre() {
		return idPedestre;
	}
	public void setIdPedestre(Long idPedestre) {
		this.idPedestre = idPedestre;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Long getTempoTotal() {
		return tempoTotal;
	}
	public void setTempoTotal(Long tempoTotal) {
		this.tempoTotal = tempoTotal;
	}
	public String getTempoTotalFormatado() {
		return tempoTotalFormatado;
	}
	public void setTempoTotalFormatado(String tempoTotalFormatado) {
		this.tempoTotalFormatado = tempoTotalFormatado;
	}
	public Long getCotaMensal() {
		return cotaMensal;
	}
	public void setCotaMensal(Long cotaMensal) {
		this.cotaMensal = cotaMensal;
	}
	public Long getSaldoRestante() {
		return saldoRestante;
	}
	public void setSaldoRestante(Long saldoRestante) {
		this.saldoRestante = saldoRestante;
	}
	public String getSaldoRestanteFormatado() {
		return saldoRestanteFormatado;
	}
	public void setSaldoRestanteFormatado(String saldoRestanteFormatado) {
		this.saldoRestanteFormatado = saldoRestanteFormatado;
	}
	
	

}
