package br.com.startjob.acesso.to.app;

/**
 * Pedido de geração de link de convite para visitante (perfil gerencial no app).
 */
public class LinkConviteVisitanteRequest {

	private Long idEmpresa;

	public Long getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Long idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

}
