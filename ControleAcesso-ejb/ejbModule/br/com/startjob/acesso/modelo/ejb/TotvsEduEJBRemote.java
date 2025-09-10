package br.com.startjob.acesso.modelo.ejb;


import javax.ejb.Remote;

import br.com.startjob.acesso.modelo.entity.CredenciaisIntegracaoTotvsEntity;

@Remote
public interface TotvsEduEJBRemote extends BaseEJBRemote{
	CredenciaisIntegracaoTotvsEntity findByUsuario(String usuario);
}
