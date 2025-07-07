package br.com.startjob.acesso.repositories;


import br.com.startjob.acesso.api.BaseService;
import br.com.startjob.acesso.modelo.ejb.TotvsEduEJBRemote;
import br.com.startjob.acesso.modelo.entity.CredenciaisIntegracaoTotvsEntity;

public class CredencialTotvsRepository extends BaseService {
	
	public CredenciaisIntegracaoTotvsEntity findByUsuario(String usuario) throws Exception {
		TotvsEduEJBRemote tovsEduEJB = (TotvsEduEJBRemote) getEjb("TotvsEduEJB");

		CredenciaisIntegracaoTotvsEntity credencial = tovsEduEJB.findByUsuario(usuario);

		return credencial;
	}
}
