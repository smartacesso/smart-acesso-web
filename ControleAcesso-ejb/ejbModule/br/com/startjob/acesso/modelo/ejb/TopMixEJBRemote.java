package br.com.startjob.acesso.modelo.ejb;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import br.com.startjob.acesso.modelo.entity.AcessoEntity;


@Remote
public interface TopMixEJBRemote extends BaseEJBRemote {

	List<AcessoEntity> findAccessByCPF(final String cpf, Date dataInicio, Date dataFim,  int page, int size);

}
