package br.com.startjob.acesso.modelo.ejb;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.ResponsibleEntity;

public interface ResponsibleEJBRemote extends BaseEJBRemote {
	
	Optional<ResponsibleEntity> findResponsibleByLoginAndPassword(final String login, final String password);
	
	List<PedestreEntity> findAllDependentsPageable(final long idResponsible, final int page, final int size);
	
	List<AcessoEntity> findAllAccessPageable(final String login, final String password,final int Page, final int size);
}
