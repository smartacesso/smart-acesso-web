package com.age.soc.services.exportaDados.parameters;

public class FuncionarioParam {
	
	//valores fixos
	private String empresa;
	private String codigo = "158145";
	private String chave = "1529604199704dc79b48";
	private String tipoSaida = "json";
	private String ativo 	= "SIM";
	private String inativo = "SIM";
	private String afastado = "SIM";
	private String pendente = "SIM";
	private String ferias = "SIM";
	
	public FuncionarioParam(String empresa, String codigo, String chave) {	
		this.empresa = empresa;
		this.codigo = codigo;
		this.chave = chave;
	}
	
	public String getEmpresa() {
		return empresa;
	}
	public String getCodigo() {
		return codigo;
	}
	public String getChave() {
		return chave;
	}
	public String getTipoSaida() {
		return tipoSaida;
	}
	public String getAtivo() {
		return ativo;
	}

	public String getInativo() {
		return inativo;
	}

	public void setInativo(String inativo) {
		this.inativo = inativo;
	}

	public String getAfastado() {
		return afastado;
	}

	public void setAfastado(String afastado) {
		this.afastado = afastado;
	}

	public String getPendente() {
		return pendente;
	}

	public void setPendente(String pendente) {
		this.pendente = pendente;
	}

	public String getFerias() {
		return ferias;
	}

	public void setFerias(String ferias) {
		this.ferias = ferias;
	}

}
