package br.com.startjob.acesso.modelo.ejb;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.AvisoAppEntity;
import br.com.startjob.acesso.modelo.entity.CorrespondenciaEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.to.app.AvisoListItem;
import br.com.startjob.acesso.modelo.to.app.PageResult;

@Remote
public interface AppEJBRemote extends BaseEJBRemote {

	PedestreEntity buscarPorLoginECliente(String login, String cliente);

	PageResult<CorrespondenciaEntity> buscarEncomendasPaginada(Long userId, Long idCliente, Date dataInicio,
			Date dataFim, String status, String busca, int pagina, int tamanho);

	List<Long> buscarIdsTutorados(Long userId);

	PageResult<AcessoEntity> buscarAcessosPaginados(List<Long> idsPermitidos, Long idCliente, Date dataInicio,
			Date dataFim, String sentido, String busca, int pagina, int tamanho);

	List<Long> buscarIdsFuncionarios(Long userId);

	PageResult<AvisoListItem> buscarAvisosPaginados(Long idCliente, String busca, int pagina, int tamanho);

	AvisoAppEntity salvarAvisoApp(AvisoAppEntity aviso) throws Exception;

	AvisoAppEntity buscarAvisoAppPorId(Long id, Long idCliente);

	byte[] buscarImagemAvisoApp(Long id, Long idCliente);

	long contarAcessosHoje(List<Long> idsPermitidos, Long idCliente);

	long contarEncomendasPendentes(Long userId, Long idCliente);
}
