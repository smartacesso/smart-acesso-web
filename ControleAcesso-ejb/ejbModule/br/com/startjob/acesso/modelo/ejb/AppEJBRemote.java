package br.com.startjob.acesso.modelo.ejb;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.CorrespondenciaEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;

@Remote
public interface AppEJBRemote extends BaseEJBRemote {

	PedestreEntity buscarPorLoginECliente(String login, String cliente);

	List<AcessoEntity> buscarAcessosPaginados(Long userID, Long cliente, Date dataInicio, Date dataFim, int pagina, int tamanho);

	List<CorrespondenciaEntity> buscarEncomendasPaginada(Long userId, Long idCliente, int pagina, int tamanho);

}
