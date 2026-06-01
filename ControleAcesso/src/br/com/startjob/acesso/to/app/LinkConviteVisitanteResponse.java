package br.com.startjob.acesso.to.app;

public class LinkConviteVisitanteResponse {

	private String link;
	private Long token;
	private Long idEmpresa;

	public LinkConviteVisitanteResponse() {
	}

	public LinkConviteVisitanteResponse(String link, Long token, Long idEmpresa) {
		this.link = link;
		this.token = token;
		this.idEmpresa = idEmpresa;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Long getToken() {
		return token;
	}

	public void setToken(Long token) {
		this.token = token;
	}

	public Long getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Long idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

}
