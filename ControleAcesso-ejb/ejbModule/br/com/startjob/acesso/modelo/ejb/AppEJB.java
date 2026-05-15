package br.com.startjob.acesso.modelo.ejb;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.TypedQuery;

import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.CorrespondenciaEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.to.app.AcessoDTO;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        long fim = System.nanoTime();
	        System.out.println("Tempo de busca (ms): " + (fim - inicio) / 1_000_000);
	    }
		return null;
	}
	
	public List<AcessoEntity> buscarAcessosPaginados(Long userId, Long cliente, Date dataInicio, Date dataFim, int pagina, int tamanho) {

	    StringBuilder jpql = new StringBuilder();
	    jpql.append("SELECT new br.com.startjob.acesso.modelo.entity.AcessoEntity(");
	    jpql.append("a.id, a.data, a.sentido) "); // Ordem deve ser igual ao construtor
	    jpql.append("FROM AcessoEntity a ");
	    jpql.append("WHERE a.cliente.id = :cliente "); 
	    jpql.append("AND a.pedestre.id = :idPedestre "); 

	    if (dataInicio != null) {
	        jpql.append("AND a.data >= :inicio ");
	    }
	    if (dataFim != null) {
	        jpql.append("AND a.data <= :fim ");
	    }

	    jpql.append("ORDER BY a.data DESC");

	    TypedQuery<AcessoEntity> query = em.createQuery(jpql.toString(), AcessoEntity.class);

	    query.setParameter("cliente", cliente);
	    query.setParameter("idPedestre", userId);
	    
	    if (dataInicio != null) query.setParameter("inicio", dataInicio);
	    if (dataFim != null) query.setParameter("fim", dataFim);

	    query.setFirstResult(pagina * tamanho);
	    query.setMaxResults(tamanho);

	    return query.getResultList();
	}

	public List<CorrespondenciaEntity> buscarEncomendasPaginada(Long userId, Long idCliente, int pagina, int tamanho) {
	    
	    StringBuilder jpql = new StringBuilder();
	    jpql.append("SELECT new br.com.startjob.acesso.modelo.entity.CorrespondenciaEntity(");
	    jpql.append("c.id, c.dataRecebimento, c.dataRetirada , c.tipo, c.codigoRastreio, c.confirmaRetirada, c.nomeQuemRetirou, c.documentoQuemRetirou) "); 
	    jpql.append("FROM CorrespondenciaEntity c ");
	    jpql.append("WHERE c.cliente.id = :idCliente "); 
	    jpql.append("AND c.destinatario.id = :idPedestre "); 
	    jpql.append("AND (c.removido = false or c.removido is null) ");
	    jpql.append("ORDER BY c.dataRecebimento DESC");

	    TypedQuery<CorrespondenciaEntity> query = em.createQuery(jpql.toString(), CorrespondenciaEntity.class);

	    query.setParameter("idCliente", idCliente);
	    query.setParameter("idPedestre", userId);
	    
	    query.setFirstResult(pagina * tamanho);
	    query.setMaxResults(tamanho);

	    return query.getResultList();
	}
}
