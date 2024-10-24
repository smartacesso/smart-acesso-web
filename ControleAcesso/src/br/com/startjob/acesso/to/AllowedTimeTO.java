package br.com.startjob.acesso.to;

public class AllowedTimeTO {
	
	private String inicio;
	private String fim;
	private String diasPermitidos;
	
	public AllowedTimeTO(String inicio, String fim, String diasPermitidos) {
		this.inicio = inicio;
		this.fim = fim;
		this.diasPermitidos = diasPermitidos;
	}

	public String getInicio() {
		return inicio;
	}

	public void setInicio(String inicio) {
		this.inicio = inicio;
	}

	public String getFim() {
		return fim;
	}

	public void setFim(String fim) {
		this.fim = fim;
	}

	public String getDiasPermitidos() {
		return diasPermitidos;
	}

	public void setDiasPermitidos(String diasPermitidos) {
		this.diasPermitidos = diasPermitidos;
	}
	
}
