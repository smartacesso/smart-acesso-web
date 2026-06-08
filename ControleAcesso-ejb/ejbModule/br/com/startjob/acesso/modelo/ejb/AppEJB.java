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
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.to.app.AvisoListItem;
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
	 * Encomendas do destinatário logado ({@code destinatario.id = userId}).
	 * Perfil GERENCIAL: ainda restrito ao próprio pedestre; ampliar escopo exige regra de negócio explícita.
	 */
	@Override
	public PageResult<CorrespondenciaEntity> buscarEncomendasPaginada(Long userId, Long idCliente, Date dataInicio,
			Date dataFim, String status, String busca, int pagina, int tamanho) {

	    StringBuilder fromWhere = buildEncomendasFromWhere(dataInicio, dataFim, status, busca);

	    StringBuilder jpqlData = new StringBuilder();
	    jpqlData.append("SELECT new br.com.startjob.acesso.modelo.entity.CorrespondenciaEntity(");
	    jpqlData.append("c.id, c.dataRecebimento, c.dataRetirada, c.tipo, c.codigoRastreio, c.confirmaRetirada, ");
	    jpqlData.append("c.nomeQuemRetirou, c.documentoQuemRetirou) ");
	    jpqlData.append(fromWhere);
	    jpqlData.append("ORDER BY c.dataRecebimento DESC");

	    TypedQuery<CorrespondenciaEntity> query = em.createQuery(jpqlData.toString(), CorrespondenciaEntity.class);
	    setEncomendasParameters(query, idCliente, userId, dataInicio, dataFim, status, busca);
	    query.setFirstResult(pagina * tamanho);
	    query.setMaxResults(tamanho);
	    List<CorrespondenciaEntity> items = query.getResultList();

	    StringBuilder jpqlCount = new StringBuilder();
	    jpqlCount.append("SELECT COUNT(c.id) ");
	    jpqlCount.append(fromWhere);
	    TypedQuery<Long> countQuery = em.createQuery(jpqlCount.toString(), Long.class);
	    setEncomendasParameters(countQuery, idCliente, userId, dataInicio, dataFim, status, busca);
	    long total = countQuery.getSingleResult();

	    return new PageResult<>(new ArrayList<>(items), total);
	}

	@Override
	public long contarEncomendasPendentes(Long userId, Long idCliente) {
		String jpql = "SELECT COUNT(c.id) FROM CorrespondenciaEntity c "
				+ "WHERE c.cliente.id = :idCliente "
				+ "AND c.destinatario.id = :idPedestre "
				+ "AND (c.removido = false OR c.removido IS NULL) "
				+ "AND (c.confirmaRetirada IS NULL OR c.confirmaRetirada <> 'S')";

		return em.createQuery(jpql, Long.class)
				.setParameter("idCliente", idCliente)
				.setParameter("idPedestre", userId)
				.getSingleResult();
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

	private StringBuilder buildEncomendasFromWhere(Date dataInicio, Date dataFim, String status, String busca) {
		StringBuilder jpql = new StringBuilder();
		jpql.append("FROM CorrespondenciaEntity c ");
		jpql.append("WHERE c.cliente.id = :idCliente ");
		jpql.append("AND c.destinatario.id = :idPedestre ");
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
			jpql.append("OR LOWER(c.nomeQuemRetirou) LIKE :busca) ");
		}
		return jpql;
	}

	private void setEncomendasParameters(TypedQuery<?> query, Long idCliente, Long userId, Date dataInicio, Date dataFim,
			String status, String busca) {
		query.setParameter("idCliente", idCliente);
		query.setParameter("idPedestre", userId);
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
}
