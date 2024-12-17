package br.com.startjob.acesso.to.teknisa;

import java.util.Date;

public class TeknisaAccessOutput {
	
	private String numeroDaOrganizacao;
	private String codigoDaFilial;
	private Date dataHoraEntrada;
	private String codigoCliente;
	private String nomeUsuario;
	
	
	public String getNumeroDaOrganizacao() {
		return numeroDaOrganizacao;
	}
	public void setNumeroDaOrganizacao(String numeroDaOrganizacao) {
		this.numeroDaOrganizacao = numeroDaOrganizacao;
	}
	public String getCodigoDaFilial() {
		return codigoDaFilial;
	}
	public void setCodigoDaFilial(String codigoDaFilial) {
		this.codigoDaFilial = codigoDaFilial;
	}
	public Date getDataHoraEntrada() {
		return dataHoraEntrada;
	}
	public void setDataHoraEntrada(Date dataHoraEntrada) {
		this.dataHoraEntrada = dataHoraEntrada;
	}
	public String getCodigoCliente() {
		return codigoCliente;
	}
	public void setCodigoCliente(String codigoCliente) {
		this.codigoCliente = codigoCliente;
	}
	public String getNomeUsuario() {
		return nomeUsuario;
	}
	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}
}
