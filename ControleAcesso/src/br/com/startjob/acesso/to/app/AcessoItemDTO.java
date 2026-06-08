package br.com.startjob.acesso.to.app;

import java.util.Date;

public class AcessoItemDTO {

	private String sentido;
	private Date data;
	private String local;
	private PedestreResumoDTO pedestre;

	public String getSentido() {
		return sentido;
	}

	public void setSentido(String sentido) {
		this.sentido = sentido;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public PedestreResumoDTO getPedestre() {
		return pedestre;
	}

	public void setPedestre(PedestreResumoDTO pedestre) {
		this.pedestre = pedestre;
	}
}
