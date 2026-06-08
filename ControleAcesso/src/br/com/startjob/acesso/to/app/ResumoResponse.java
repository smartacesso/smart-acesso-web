package br.com.startjob.acesso.to.app;

public class ResumoResponse {

	private long acessosHoje;
	private long encomendasPendentes;

	public long getAcessosHoje() {
		return acessosHoje;
	}

	public void setAcessosHoje(long acessosHoje) {
		this.acessosHoje = acessosHoje;
	}

	public long getEncomendasPendentes() {
		return encomendasPendentes;
	}

	public void setEncomendasPendentes(long encomendasPendentes) {
		this.encomendasPendentes = encomendasPendentes;
	}
}
