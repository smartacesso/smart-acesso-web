package br.com.startjob.acesso.modelo.ejb;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.AvisoAppEntity;
import br.com.startjob.acesso.modelo.entity.CorrespondenciaEntity;
import br.com.startjob.acesso.modelo.entity.DeviceTokenEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.to.app.AvisoListItem;
import br.com.startjob.acesso.modelo.to.app.EncomendaListItem;
import br.com.startjob.acesso.modelo.to.app.PageResult;

@Remote
public interface AppEJBRemote extends BaseEJBRemote {

	PedestreEntity buscarPorLoginECliente(String login, String cliente);

	PageResult<EncomendaListItem> buscarEncomendasPaginada(Long userId, Long idCliente, Date dataInicio,
			Date dataFim, String status, String busca, int pagina, int tamanho, boolean listarTodasDoCliente);

	EncomendaListItem confirmarRetiradaEncomenda(Long idEncomenda, Long idPedestreLogado, Long idCliente,
			String nomeQuemRetirou, String documentoQuemRetirou, boolean perfilGerencial) throws Exception;

	List<Long> buscarIdsTutorados(Long userId);

	PageResult<AcessoEntity> buscarAcessosPaginados(List<Long> idsPermitidos, Long idCliente, Date dataInicio,
			Date dataFim, String sentido, String busca, int pagina, int tamanho);

	List<Long> buscarIdsFuncionarios(Long userId);

	PageResult<AvisoListItem> buscarAvisosPaginados(Long idCliente, String busca, int pagina, int tamanho);

	AvisoAppEntity salvarAvisoApp(AvisoAppEntity aviso) throws Exception;

	AvisoAppEntity buscarAvisoAppPorId(Long id, Long idCliente);

	void excluirAvisoApp(Long id, Long idCliente) throws Exception;

	byte[] buscarImagemAvisoApp(Long id, Long idCliente);

	long contarAcessosHoje(List<Long> idsPermitidos, Long idCliente);

	long contarEncomendasPendentes(Long userId, Long idCliente, boolean listarTodasDoCliente);

	DeviceTokenEntity upsertDeviceToken(Long idPedestre, String fcmToken, String platform, String appVersion);

	void invalidarDeviceToken(Long idPedestre, String fcmToken);

	void invalidarDeviceTokenPorFcm(String fcmToken);

	List<DeviceTokenEntity> buscarDeviceTokensAtivos(Long idPedestre);

	List<Long> buscarIdsResponsaveisPorPedestre(Long idPedestre);

	/** Responsáveis com perfil app RESPONSAVEL do pedestre que acessou (push app novo). */
	List<Long> buscarIdsResponsaveisAppPorPedestre(Long idPedestre);

	/** Gerentes com perfil app GERENCIAL da mesma empresa do pedestre que acessou (push app novo). */
	List<Long> buscarIdsGerenciaisAppPorPedestre(Long idPedestre);

	List<Long> buscarIdsPedestresNotificaveisAppPorCliente(Long idCliente);

	String buscarNomePedestre(Long idPedestre);

	CorrespondenciaEntity buscarCorrespondenciaPorIdECliente(Long id, Long idCliente);
}
