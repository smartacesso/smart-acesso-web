package br.com.startjob.acesso.modelo.ejb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.TypedQuery;

import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.AvisoAppEntity;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.CorrespondenciaEntity;
import br.com.startjob.acesso.modelo.entity.DeviceTokenEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.enumeration.PerfilAcessoApp;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.to.app.AvisoListItem;
import br.com.startjob.acesso.modelo.to.app.EncomendaListItem;
import br.com.startjob.acesso.modelo.to.app.PageResult;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AppEJB extends BaseEJB implements AppEJBRemote {

	@Override
	public PedestreEntity buscarPorLoginECliente(String login, String cliente) {

	    long inicio = System.nanoTime();
	    
	    System.out.println("cliente recebido: [" + cliente + "]");
	    System.out.println("login recebido: [" + login + "]");

	    Map<String, Object> args = new HashMap<>();
	    args.put("LOGIN", login.trim().toLowerCase());
	    args.put("UNIDADE_ORGANIZACIONAL", cliente.trim().toLowerCase());

	    try {
	        @SuppressWarnings("unchecked")
	        List<PedestreEntity> resultado =
	                (List<PedestreEntity>) this.pesquisaArgFixos(
	                        PedestreEntity.class, "findByLoginOtimizado", args
	                );

	        if (resultado == null || resultado.isEmpty()) {
	            return null;
	        }

	        return resultado.get(0);

	    } catch (Exception e) {
			e.printStackTrace();
		} finally {
	        long fim = System.nanoTime();
	        System.out.println("Tempo de busca (ms): " + (fim - inicio) / 1_000_000);
	    }
		return null;
	}
	
	@Override
	public PageResult<AcessoEntity> buscarAcessosPaginados(List<Long> ids, Long cliente, Date dataInicio, Date dataFim,
			String sentido, String busca, int pagina, int tamanho) {

	    if (ids == null || ids.isEmpty()) {
	        return new PageResult<>(new ArrayList<>(), 0);
	    }

	    StringBuilder fromWhere = buildAcessosFromWhere(dataInicio, dataFim, sentido, busca);

	    StringBuilder jpqlData = new StringBuilder();
	    jpqlData.append("SELECT DISTINCT new br.com.startjob.acesso.modelo.entity.AcessoEntity(");
	    jpqlData.append("a.id, a.data, a.sentido, a.pedestre.nome, a.local) ");
	    jpqlData.append(fromWhere);
	    jpqlData.append("ORDER BY a.data DESC");

	    TypedQuery<AcessoEntity> query = em.createQuery(jpqlData.toString(), AcessoEntity.class);
	    setAcessosParameters(query, cliente, ids, dataInicio, dataFim, sentido, busca);
	    query.setFirstResult(pagina * tamanho);
	    query.setMaxResults(tamanho);
	    List<AcessoEntity> items = query.getResultList();

	    StringBuilder jpqlCount = new StringBuilder();
	    jpqlCount.append("SELECT COUNT(DISTINCT a.id) ");
	    jpqlCount.append(fromWhere);
	    TypedQuery<Long> countQuery = em.createQuery(jpqlCount.toString(), Long.class);
	    setAcessosParameters(countQuery, cliente, ids, dataInicio, dataFim, sentido, busca);
	    long total = countQuery.getSingleResult();

	    return new PageResult<>(new ArrayList<>(items), total);
	}

	@Override
	public long contarAcessosHoje(List<Long> ids, Long cliente) {
		if (ids == null || ids.isEmpty()) {
			return 0;
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date inicioDia = cal.getTime();

		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date fimDia = cal.getTime();

		String jpql = "SELECT COUNT(DISTINCT a.id) FROM AcessoEntity a "
				+ "WHERE a.cliente.id = :cliente "
				+ "AND a.pedestre.id IN :ids "
				+ "AND a.data >= :inicioDia AND a.data < :fimDia "
				+ "AND (a.removido = false OR a.removido IS NULL)";

		return em.createQuery(jpql, Long.class)
				.setParameter("cliente", cliente)
				.setParameter("ids", ids)
				.setParameter("inicioDia", inicioDia)
				.setParameter("fimDia", fimDia)
				.getSingleResult();
	}

	/**
	 * Encomendas do destinatário logado ou de todo o cliente (perfil GERENCIAL).
	 */
	@Override
	public PageResult<EncomendaListItem> buscarEncomendasPaginada(Long userId, Long idCliente, Date dataInicio,
			Date dataFim, String status, String busca, int pagina, int tamanho, boolean listarTodasDoCliente) {

	    StringBuilder fromWhere = buildEncomendasFromWhere(dataInicio, dataFim, status, busca, listarTodasDoCliente);

	    StringBuilder jpqlData = new StringBuilder();
	    jpqlData.append("SELECT new br.com.startjob.acesso.modelo.to.app.EncomendaListItem(");
	    jpqlData.append("c.id, c.dataRecebimento, c.dataRetirada, c.tipo, c.codigoRastreio, c.confirmaRetirada, ");
	    jpqlData.append("c.nomeQuemRetirou, c.documentoQuemRetirou, d.id, d.nome) ");
	    jpqlData.append(fromWhere);
	    jpqlData.append("ORDER BY c.dataRecebimento DESC");

	    TypedQuery<EncomendaListItem> query = em.createQuery(jpqlData.toString(), EncomendaListItem.class);
	    setEncomendasParameters(query, idCliente, userId, dataInicio, dataFim, status, busca, listarTodasDoCliente);
	    query.setFirstResult(pagina * tamanho);
	    query.setMaxResults(tamanho);
	    List<EncomendaListItem> items = query.getResultList();

	    StringBuilder jpqlCount = new StringBuilder();
	    jpqlCount.append("SELECT COUNT(c.id) ");
	    jpqlCount.append(fromWhere);
	    TypedQuery<Long> countQuery = em.createQuery(jpqlCount.toString(), Long.class);
	    setEncomendasParameters(countQuery, idCliente, userId, dataInicio, dataFim, status, busca, listarTodasDoCliente);
	    long total = countQuery.getSingleResult();

	    return new PageResult<>(new ArrayList<>(items), total);
	}

	@Override
	public EncomendaListItem confirmarRetiradaEncomenda(Long idEncomenda, Long idPedestreLogado, Long idCliente,
			String nomeQuemRetirou, String documentoQuemRetirou, boolean perfilGerencial) throws Exception {
		if (idEncomenda == null) {
			throw new Exception("INVALID_PARAMS");
		}
		if (nomeQuemRetirou == null || nomeQuemRetirou.trim().isEmpty()
				|| documentoQuemRetirou == null || documentoQuemRetirou.trim().isEmpty()) {
			throw new Exception("INVALID_PARAMS");
		}

		String jpql = "SELECT c FROM CorrespondenciaEntity c "
				+ "JOIN FETCH c.destinatario d "
				+ "WHERE c.id = :id AND c.cliente.id = :idCliente "
				+ "AND (c.removido = false OR c.removido IS NULL)";
		List<CorrespondenciaEntity> lista = em.createQuery(jpql, CorrespondenciaEntity.class)
				.setParameter("id", idEncomenda)
				.setParameter("idCliente", idCliente)
				.setMaxResults(1)
				.getResultList();

		if (lista.isEmpty()) {
			throw new Exception("NOT_FOUND");
		}

		CorrespondenciaEntity correspondencia = lista.get(0);
		if (!perfilGerencial) {
			if (correspondencia.getDestinatario() == null
					|| !idPedestreLogado.equals(correspondencia.getDestinatario().getId())) {
				throw new Exception("FORBIDDEN");
			}
		}

		if ("S".equals(correspondencia.getConfirmaRetirada())) {
			throw new Exception("ALREADY_CONFIRMED");
		}

		correspondencia.setConfirmaRetirada("S");
		correspondencia.setDataRetirada(new Date());
		correspondencia.setNomeQuemRetirou(nomeQuemRetirou.trim());
		correspondencia.setDocumentoQuemRetirou(documentoQuemRetirou.trim());
		correspondencia.setDataAlteracao(new Date());

		CorrespondenciaEntity salva = (CorrespondenciaEntity) alteraObjeto(correspondencia)[0];
		return toEncomendaListItem(salva);
	}

	@Override
	public long contarEncomendasPendentes(Long userId, Long idCliente, boolean listarTodasDoCliente) {
		StringBuilder jpql = new StringBuilder();
		jpql.append("SELECT COUNT(c.id) FROM CorrespondenciaEntity c ");
		jpql.append("WHERE c.cliente.id = :idCliente ");
		if (!listarTodasDoCliente) {
			jpql.append("AND c.destinatario.id = :idPedestre ");
		}
		jpql.append("AND (c.removido = false OR c.removido IS NULL) ");
		jpql.append("AND (c.confirmaRetirada IS NULL OR c.confirmaRetirada <> 'S')");

		TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class).setParameter("idCliente", idCliente);
		if (!listarTodasDoCliente) {
			query.setParameter("idPedestre", userId);
		}
		return query.getSingleResult();
	}

	@Override
	public PageResult<AvisoListItem> buscarAvisosPaginados(Long idCliente, String busca, int pagina, int tamanho) {
		StringBuilder fromWhere = new StringBuilder();
		fromWhere.append("FROM AvisoAppEntity a ");
		fromWhere.append("WHERE a.cliente.id = :idCliente ");
		fromWhere.append("AND (a.removido = false OR a.removido IS NULL) ");

		if (busca != null && !busca.trim().isEmpty()) {
			fromWhere.append("AND (LOWER(a.titulo) LIKE :busca OR LOWER(a.descricao) LIKE :busca) ");
		}

		StringBuilder jpqlData = new StringBuilder();
		jpqlData.append("SELECT new br.com.startjob.acesso.modelo.to.app.AvisoListItem(");
		jpqlData.append("a.id, a.titulo, a.descricao, a.dataPublicacao, ");
		jpqlData.append("CASE WHEN a.imagem IS NOT NULL THEN true ELSE false END) ");
		jpqlData.append(fromWhere);
		jpqlData.append("ORDER BY a.dataPublicacao DESC, a.id DESC");

		TypedQuery<AvisoListItem> query = em.createQuery(jpqlData.toString(), AvisoListItem.class);
		query.setParameter("idCliente", idCliente);
		if (busca != null && !busca.trim().isEmpty()) {
			query.setParameter("busca", "%" + busca.trim().toLowerCase() + "%");
		}
		query.setFirstResult(pagina * tamanho);
		query.setMaxResults(tamanho);
		List<AvisoListItem> items = query.getResultList();

		StringBuilder jpqlCount = new StringBuilder();
		jpqlCount.append("SELECT COUNT(a.id) ");
		jpqlCount.append(fromWhere);
		TypedQuery<Long> countQuery = em.createQuery(jpqlCount.toString(), Long.class);
		countQuery.setParameter("idCliente", idCliente);
		if (busca != null && !busca.trim().isEmpty()) {
			countQuery.setParameter("busca", "%" + busca.trim().toLowerCase() + "%");
		}
		long total = countQuery.getSingleResult();

		return new PageResult<>(new ArrayList<>(items), total);
	}

	@Override
	public AvisoAppEntity salvarAvisoApp(AvisoAppEntity aviso) throws Exception {
		if (aviso == null) {
			throw new Exception("salvarAvisoApp.aviso.nulo");
		}
		prepararClienteParaPersistencia(aviso);
		Long idCliente = aviso.getCliente() != null ? aviso.getCliente().getId() : null;

		if (aviso.getId() == null) {
			return (AvisoAppEntity) gravaObjeto(aviso)[0];
		}

		/*
		 * LOB lazy em entidade destacada: merge(aviso) pode não persistir IMAGEM.
		 * Carrega instância gerenciada, copia campos e grava.
		 */
		AvisoAppEntity managed = buscarAvisoAppPorId(aviso.getId(), idCliente);
		if (managed == null) {
			throw new Exception("salvarAvisoApp.aviso.nao.encontrado");
		}
		managed.setTitulo(aviso.getTitulo());
		managed.setDescricao(aviso.getDescricao());
		managed.setDataPublicacao(aviso.getDataPublicacao());
		managed.setImagem(aviso.getImagem());
		return (AvisoAppEntity) alteraObjeto(managed)[0];
	}

	private void prepararClienteParaPersistencia(AvisoAppEntity aviso) {
		if (aviso.getCliente() != null && aviso.getCliente().getId() != null) {
			aviso.setCliente(em.getReference(ClienteEntity.class, aviso.getCliente().getId()));
		}
	}

	@Override
	public AvisoAppEntity buscarAvisoAppPorId(Long id, Long idCliente) {
		String jpql = "SELECT a FROM AvisoAppEntity a WHERE a.id = :id AND a.cliente.id = :idCliente "
				+ "AND (a.removido = false OR a.removido IS NULL)";
		List<AvisoAppEntity> lista = em.createQuery(jpql, AvisoAppEntity.class).setParameter("id", id)
				.setParameter("idCliente", idCliente).setMaxResults(1).getResultList();
		return lista.isEmpty() ? null : lista.get(0);
	}

	@Override
	public void excluirAvisoApp(Long id, Long idCliente) throws Exception {
		if (id == null || idCliente == null) {
			throw new Exception("INVALID_PARAMS");
		}
		AvisoAppEntity aviso = buscarAvisoAppPorId(id, idCliente);
		if (aviso == null) {
			throw new Exception("NOT_FOUND");
		}
		aviso.setRemovido(true);
		aviso.setDataRemovido(new Date());
		aviso.setDataAlteracao(new Date());
		alteraObjeto(aviso);
	}

	@Override
	public byte[] buscarImagemAvisoApp(Long id, Long idCliente) {
		String jpql = "SELECT a.imagem FROM AvisoAppEntity a WHERE a.id = :id AND a.cliente.id = :idCliente "
				+ "AND (a.removido = false OR a.removido IS NULL)";
		List<byte[]> lista = em.createQuery(jpql, byte[].class).setParameter("id", id)
				.setParameter("idCliente", idCliente).setMaxResults(1).getResultList();
		if (lista.isEmpty()) {
			return null;
		}
		return lista.get(0);
	}

	private StringBuilder buildAcessosFromWhere(Date dataInicio, Date dataFim, String sentido, String busca) {
		StringBuilder jpql = new StringBuilder();
		jpql.append("FROM AcessoEntity a ");
		jpql.append("WHERE a.cliente.id = :cliente ");
		jpql.append("AND a.pedestre.id IN :ids ");
		jpql.append("AND (a.removido = false OR a.removido IS NULL) ");

		if (dataInicio != null) {
			jpql.append("AND a.data >= :inicio ");
		}
		if (dataFim != null) {
			jpql.append("AND a.data <= :fim ");
		}
		if (sentido != null && !sentido.trim().isEmpty()) {
			jpql.append("AND a.sentido = :sentido ");
		}
		if (busca != null && !busca.trim().isEmpty()) {
			jpql.append("AND (LOWER(a.pedestre.nome) LIKE :busca OR LOWER(a.local) LIKE :busca) ");
		}
		return jpql;
	}

	private void setAcessosParameters(TypedQuery<?> query, Long cliente, List<Long> ids, Date dataInicio, Date dataFim,
			String sentido, String busca) {
		query.setParameter("cliente", cliente);
		query.setParameter("ids", ids);
		if (dataInicio != null) {
			query.setParameter("inicio", dataInicio);
		}
		if (dataFim != null) {
			query.setParameter("fim", dataFim);
		}
		if (sentido != null && !sentido.trim().isEmpty()) {
			query.setParameter("sentido", sentido.trim().toUpperCase());
		}
		if (busca != null && !busca.trim().isEmpty()) {
			query.setParameter("busca", "%" + busca.trim().toLowerCase() + "%");
		}
	}

	private StringBuilder buildEncomendasFromWhere(Date dataInicio, Date dataFim, String status, String busca,
			boolean listarTodasDoCliente) {
		StringBuilder jpql = new StringBuilder();
		jpql.append("FROM CorrespondenciaEntity c JOIN c.destinatario d ");
		jpql.append("WHERE c.cliente.id = :idCliente ");
		if (!listarTodasDoCliente) {
			jpql.append("AND c.destinatario.id = :idPedestre ");
		}
		jpql.append("AND (c.removido = false OR c.removido IS NULL) ");

		if (dataInicio != null) {
			jpql.append("AND c.dataRecebimento >= :inicio ");
		}
		if (dataFim != null) {
			jpql.append("AND c.dataRecebimento <= :fim ");
		}
		if (status != null && !status.trim().isEmpty() && !"TODAS".equalsIgnoreCase(status.trim())) {
			if ("DISPONIVEL".equalsIgnoreCase(status.trim())) {
				jpql.append("AND (c.confirmaRetirada IS NULL OR c.confirmaRetirada <> 'S') ");
			} else if ("ENTREGUE".equalsIgnoreCase(status.trim())) {
				jpql.append("AND c.confirmaRetirada = 'S' ");
			}
		}
		if (busca != null && !busca.trim().isEmpty()) {
			jpql.append("AND (LOWER(c.codigoRastreio) LIKE :busca OR LOWER(c.tipo) LIKE :busca ");
			jpql.append("OR LOWER(c.nomeQuemRetirou) LIKE :busca OR LOWER(d.nome) LIKE :busca) ");
		}
		return jpql;
	}

	private void setEncomendasParameters(TypedQuery<?> query, Long idCliente, Long userId, Date dataInicio, Date dataFim,
			String status, String busca, boolean listarTodasDoCliente) {
		query.setParameter("idCliente", idCliente);
		if (!listarTodasDoCliente) {
			query.setParameter("idPedestre", userId);
		}
		if (dataInicio != null) {
			query.setParameter("inicio", dataInicio);
		}
		if (dataFim != null) {
			query.setParameter("fim", dataFim);
		}
		if (busca != null && !busca.trim().isEmpty()) {
			query.setParameter("busca", "%" + busca.trim().toLowerCase() + "%");
		}
	}

	@Override
	public List<Long> buscarIdsTutorados(Long userId) {
	    StringBuilder jpql = new StringBuilder();
	    jpql.append("SELECT p.id FROM PedestreEntity p ");
	    jpql.append("JOIN p.responsaveis r ");
	    jpql.append("WHERE r.id = :userId ");
	    jpql.append("AND (p.removido = false OR p.removido is null) ");

	    TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
	    query.setParameter("userId", userId);

	    return query.getResultList();
	}

	@Override
	public List<Long> buscarIdsFuncionarios(Long userId) {

	    String jpql =
	        "SELECT p.id " +
	        "FROM PedestreEntity p " +
	        "WHERE p.empresa.id = (" +
	        "   SELECT p2.empresa.id " +
	        "   FROM PedestreEntity p2 " +
	        "   WHERE p2.id = :userId" +
	        ") " +
	        "AND (p.removido = false OR p.removido IS NULL) " +
	        "AND p.tipo = 'PEDESTRE'";

	    return em.createQuery(jpql, Long.class)
	            .setParameter("userId", userId)
	            .getResultList();
	}

	@Override
	public DeviceTokenEntity upsertDeviceToken(Long idPedestre, String fcmToken, String platform, String appVersion) {
		if (idPedestre == null) {
			throw new IllegalArgumentException("idPedestre é obrigatório para registrar device token");
		}
		String tokenNorm = fcmToken.trim();
		String platformNorm = normalizarPlatform(platform);
		String versionNorm = normalizarAppVersion(appVersion);
		Date now = new Date();
		PedestreEntity pedestre = em.getReference(PedestreEntity.class, idPedestre);

		TypedQuery<DeviceTokenEntity> byToken = em.createNamedQuery("DeviceTokenEntity.findByFcmToken",
				DeviceTokenEntity.class);
		byToken.setParameter("fcmToken", tokenNorm);
		List<DeviceTokenEntity> existing = byToken.getResultList();

		DeviceTokenEntity entity;
		if (!existing.isEmpty()) {
			entity = existing.get(0);
		} else {
			entity = new DeviceTokenEntity();
			entity.setFcmToken(tokenNorm);
			entity.setPedestre(pedestre);
			entity.setPlatform(platformNorm);
			entity.setAppVersion(versionNorm);
			entity.setAtualizadoEm(now);
			entity.setAtivo(true);
			entity.setDataCriacao(now);
			entity.setDataAlteracao(now);
			em.persist(entity);
			return entity;
		}

		entity.setPedestre(pedestre);
		entity.setPlatform(platformNorm);
		entity.setAppVersion(versionNorm);
		entity.setAtualizadoEm(now);
		entity.setAtivo(true);
		entity.setDataAlteracao(now);
		return entity;
	}

	@Override
	public void invalidarDeviceToken(Long idPedestre, String fcmToken) {
		Date now = new Date();
		List<DeviceTokenEntity> tokens;

		if (fcmToken != null && !fcmToken.trim().isEmpty()) {
			TypedQuery<DeviceTokenEntity> query = em.createNamedQuery("DeviceTokenEntity.findByPedestreAndFcmToken",
					DeviceTokenEntity.class);
			query.setParameter("idPedestre", idPedestre);
			query.setParameter("fcmToken", fcmToken.trim());
			tokens = query.getResultList();
		} else {
			tokens = buscarDeviceTokensAtivos(idPedestre);
		}

		for (DeviceTokenEntity token : tokens) {
			token.setAtivo(false);
			token.setAtualizadoEm(now);
			token.setDataAlteracao(now);
		}
	}

	@Override
	public void invalidarDeviceTokenPorFcm(String fcmToken) {
		if (fcmToken == null || fcmToken.trim().isEmpty()) {
			return;
		}
		TypedQuery<DeviceTokenEntity> query = em.createNamedQuery("DeviceTokenEntity.findByFcmToken",
				DeviceTokenEntity.class);
		query.setParameter("fcmToken", fcmToken.trim());
		Date now = new Date();
		for (DeviceTokenEntity token : query.getResultList()) {
			token.setAtivo(false);
			token.setAtualizadoEm(now);
			token.setDataAlteracao(now);
		}
	}

	@Override
	public List<DeviceTokenEntity> buscarDeviceTokensAtivos(Long idPedestre) {
		TypedQuery<DeviceTokenEntity> query = em.createNamedQuery("DeviceTokenEntity.findAtivosByPedestre",
				DeviceTokenEntity.class);
		query.setParameter("idPedestre", idPedestre);
		return query.getResultList();
	}

	@Override
	public List<Long> buscarIdsResponsaveisPorPedestre(Long idPedestre) {
		String jpql = "SELECT r.id FROM PedestreEntity p JOIN p.responsaveis r "
				+ "WHERE p.id = :idPedestre AND (p.removido = false OR p.removido IS NULL) "
				+ "AND (r.removido = false OR r.removido IS NULL)";
		return em.createQuery(jpql, Long.class).setParameter("idPedestre", idPedestre).getResultList();
	}

	@Override
	public List<Long> buscarIdsResponsaveisAppPorPedestre(Long idPedestre) {
		if (idPedestre == null) {
			return new ArrayList<>();
		}
		String jpql = "SELECT r.id FROM PedestreEntity p JOIN p.responsaveis r "
				+ "WHERE p.id = :idPedestre "
				+ "AND r.perfilApp = :perfilResponsavel "
				+ "AND r.id <> p.id "
				+ "AND (p.removido = false OR p.removido IS NULL) "
				+ "AND (r.removido = false OR r.removido IS NULL)";
		return em.createQuery(jpql, Long.class)
				.setParameter("idPedestre", idPedestre)
				.setParameter("perfilResponsavel", PerfilAcessoApp.RESPONSAVEL)
				.getResultList();
	}

	@Override
	public List<Long> buscarIdsGerenciaisAppPorPedestre(Long idPedestre) {
		if (idPedestre == null) {
			return new ArrayList<>();
		}
		String jpql = "SELECT g.id FROM PedestreEntity p, PedestreEntity g "
				+ "WHERE p.id = :idPedestre "
				+ "AND p.tipo = :tipoPedestre "
				+ "AND p.empresa IS NOT NULL "
				+ "AND g.empresa.id = p.empresa.id "
				+ "AND g.perfilApp = :perfilGerencial "
				+ "AND g.id <> p.id "
				+ "AND (p.removido = false OR p.removido IS NULL) "
				+ "AND (g.removido = false OR g.removido IS NULL)";
		return em.createQuery(jpql, Long.class)
				.setParameter("idPedestre", idPedestre)
				.setParameter("tipoPedestre", TipoPedestre.PEDESTRE)
				.setParameter("perfilGerencial", PerfilAcessoApp.GERENCIAL)
				.getResultList();
	}

	@Override
	public List<Long> buscarIdsPedestresNotificaveisAppPorCliente(Long idCliente) {
		String jpql = "SELECT p.id FROM PedestreEntity p "
				+ "WHERE p.cliente.id = :idCliente AND p.perfilApp IS NOT NULL "
				+ "AND (p.removido = false OR p.removido IS NULL)";
		return em.createQuery(jpql, Long.class).setParameter("idCliente", idCliente).getResultList();
	}

	@Override
	public String buscarNomePedestre(Long idPedestre) {
		if (idPedestre == null) {
			return null;
		}
		PedestreEntity pedestre = em.find(PedestreEntity.class, idPedestre);
		return pedestre != null ? pedestre.getNome() : null;
	}

	private String normalizarPlatform(String platform) {
		if (platform == null) {
			return null;
		}
		String p = platform.trim().toLowerCase();
		if ("android".equals(p)) {
			return "ANDROID";
		}
		if ("ios".equals(p) || "iphone".equals(p) || "ipad".equals(p)) {
			return "IOS";
		}
		return platform.trim().toUpperCase();
	}

	private String normalizarAppVersion(String appVersion) {
		if (appVersion == null || appVersion.trim().isEmpty()) {
			return null;
		}
		String v = appVersion.trim();
		return v.length() > 50 ? v.substring(0, 50) : v;
	}

	@Override
	public CorrespondenciaEntity buscarCorrespondenciaPorIdECliente(Long id, Long idCliente) {
		if (id == null || idCliente == null) {
			return null;
		}
		String jpql = "SELECT c FROM CorrespondenciaEntity c JOIN FETCH c.destinatario d "
				+ "WHERE c.id = :id AND c.cliente.id = :idCliente "
				+ "AND (c.removido = false OR c.removido IS NULL)";
		List<CorrespondenciaEntity> lista = em.createQuery(jpql, CorrespondenciaEntity.class)
				.setParameter("id", id)
				.setParameter("idCliente", idCliente)
				.setMaxResults(1)
				.getResultList();
		return lista.isEmpty() ? null : lista.get(0);
	}

	private EncomendaListItem toEncomendaListItem(CorrespondenciaEntity correspondencia) {
		Long destinatarioId = null;
		String destinatarioNome = null;
		if (correspondencia.getDestinatario() != null) {
			destinatarioId = correspondencia.getDestinatario().getId();
			destinatarioNome = correspondencia.getDestinatario().getNome();
		}
		return new EncomendaListItem(correspondencia.getId(), correspondencia.getDataRecebimento(),
				correspondencia.getDataRetirada(), correspondencia.getTipo(), correspondencia.getCodigoRastreio(),
				correspondencia.getConfirmaRetirada(), correspondencia.getNomeQuemRetirou(),
				correspondencia.getDocumentoQuemRetirou(), destinatarioId, destinatarioNome);
	}
}
