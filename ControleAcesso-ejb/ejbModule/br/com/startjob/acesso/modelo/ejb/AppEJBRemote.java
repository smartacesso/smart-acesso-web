package br.com.startjob.acesso.modelo.ejb;

import javax.ejb.Remote;

import br.com.startjob.acesso.modelo.entity.PedestreEntity;

@Remote
public interface AppEJBRemote extends BaseEJBRemote {

	PedestreEntity buscarPorLoginECliente(String nome, String cliente);

}
