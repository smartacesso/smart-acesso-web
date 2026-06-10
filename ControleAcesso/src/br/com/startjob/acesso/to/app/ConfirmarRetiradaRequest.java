package br.com.startjob.acesso.to.app;

public class ConfirmarRetiradaRequest {

	private Long id;
	private String nomeQuemRetirou;
	private String documentoQuemRetirou;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeQuemRetirou() {
		return nomeQuemRetirou;
	}

	public void setNomeQuemRetirou(String nomeQuemRetirou) {
		this.nomeQuemRetirou = nomeQuemRetirou;
	}

	public String getDocumentoQuemRetirou() {
		return documentoQuemRetirou;
	}

	public void setDocumentoQuemRetirou(String documentoQuemRetirou) {
		this.documentoQuemRetirou = documentoQuemRetirou;
	}
}
