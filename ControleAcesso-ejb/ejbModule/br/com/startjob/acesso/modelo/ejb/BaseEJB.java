package br.com.startjob.acesso.modelo.ejb;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.Session;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.MensagemEquipamentoEntity;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.PedestreRegraEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.entity.base.BaseEntity;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.enumeration.TipoRegra;
import br.com.startjob.acesso.modelo.exception.ConcurrenceException;
import br.com.startjob.acesso.modelo.utils.AppAmbienteUtils;
import br.com.startjob.acesso.modelo.utils.DateUtils;
/**
 * Session Bean implementation class BaseEJB
 * 
 * @author Gustavo Diniz
 * @since 03/02/2013
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BaseEJB implements BaseEJBRemote {

	
	/**
	 * Logger para realizar trace de execução dos métodos
	 * geralmente executado em modo de debug
	 */
	protected Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
			
	/**
	 * Nome do pacote onde devem ficar as entidades
	 */
	@PersistenceContext(unitName="ControleAcesso-pu")
	protected EntityManager em;

    /**
     * Default constructor. 
     */
    public BaseEJB() {
        logger.info("Instancia EJB...");
    	
    }
    
    /*
     * (non-Javadoc)
     * @see br.com.fitness.project.model.ejb.BaseEJB#execute()
     */
    public void execute(){
    	System.out.println("Executando o EJB!");
    }
    
    /*
     * (non-Javadoc)
     * @see br.com.fitness.project.model.ejb.BaseEJB#gravaObjeto(br.com.fitness.project.model.entity.BaseEntity)
     */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Object[] gravaObjeto(BaseEntity entidade) throws Exception {
		
		try {

			if (entidade == null) {
				throw new Exception("gravaObjeto.entidade.nula");
			}

			this.auditoriaEntidade(entidade);
			
			BaseEntity aux = em.merge(entidade);
			
			setId(entidade, aux);
			entidade.setVersao(aux.getVersao());
			
			((BaseEntity)entidade).setExistente(true);
			
			return new Object[]{entidade,"msg.generica.objeto.incluido.sucesso"};
			

		}
		catch (Exception e) {
			//encapsula exceção
			e.printStackTrace();
			
			//encapsula exceção
			if(e.getMessage().indexOf(BaseConstant.MSG_CONCORRENCIA) != -1){
				throw new Exception("msg.generica.erro.processamento", new ConcurrenceException());
			}
			throw new Exception("msg.generica.erro.processamento", e);
		}
		
	}

	private void setId(BaseEntity entidade, BaseEntity aux) {
		//setid
		try{
			Method setId = entidade.getClass().getDeclaredMethod("setId", new Class[]{Long.class});
			Method getId = entidade.getClass().getDeclaredMethod("getId", new Class[]{});
			if(setId != null && getId != null)
				setId.invoke(entidade, getId.invoke(aux, new Object[]{}));
		}catch(Exception e){
			//apenas avisa que não existe id;
			System.out.println("gravaObjeto: entidade sem campo ID");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.ejb.BaseEJB#gravaObjeto(br.com.fitness.project.model.entity.BaseEntity, java.lang.Object[])
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Object[] gravaObjeto(BaseEntity entidade, Object[] filhosExclusao) throws Exception {

		logger.debug("Excluindo filhos antes de alterar o pai.");
		
		excluiFilhos(filhosExclusao);
		
		logger.debug("Excluiu os filhos chama gravaObjeto para alterar o pai");
		
		return this.gravaObjeto(entidade);
	}
	
	/**
	 * 
	 * Atualiza entidade com os campos da auditoria básica.
	 * dataCriacao, dataAlteracao e nomeUsuario
	 *
	 * @author: Gustavo Diniz
	 * @param entidade - entidade para auditoria
	 */
	private void auditoriaEntidade(BaseEntity entidade) {

		//são faz para entidades que possuam os campos de auditoria
		if (entidade instanceof BaseEntity) {
			BaseEntity entidadeImp = (BaseEntity) entidade;
			if (entidadeImp.getDataCriacao() == null) {
				entidadeImp.setDataCriacao(new Date());
			}

			entidadeImp.setDataAlteracao(new Date());
		}

	}
	
	/**
	 * Exclui as listas de filhos antes da alteração do pai.
	 * 
	 * @author Gustavo Diniz
	 * @param filhosExclusao
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private void excluiFilhos(Object[] filhosExclusao) throws Exception {
		/*
		 * Exclui as listas de filhos antes da alteração do pai 
		 */
		for (int i = 0; i < filhosExclusao.length; i++) {
			Object object = filhosExclusao[i];
			
			//exclui só se for uma lista
			if(object != null && object instanceof List){
				
				List<? extends BaseEntity> filhos = (List<? extends BaseEntity>)object;
				for (Iterator iterator = filhos.iterator(); iterator.hasNext();) {
					BaseEntity entidadeFilho = (BaseEntity) iterator.next();
					
					this.excluiObjeto(entidadeFilho);
					
				}
				
			}
			
			
		}
	}

	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.ejb.BaseEJB#alteraObjeto(br.com.fitness.project.model.entity.BaseEntity)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Object[] alteraObjeto(BaseEntity entidade) throws Exception  {
		try{
			
			if (entidade == null) {
				throw new Exception("alteraObjeto.entidade.nula");
			}

			this.auditoriaEntidade(entidade);
			
			//grava objeto utilizando merge
			//necessário nesse caso para deletar orfãos
			BaseEntity aux = em.merge(entidade);
			entidade.setVersao(aux.getVersao()+1);
			
			return new Object[]{entidade,"msg.generica.objeto.incluido.sucesso"};
			
		} catch (Exception e) {
			
			logger.debug("Lançou erro no alteraObjeto.");
			//encapsula exceção
			e.printStackTrace();
			if (e instanceof javax.persistence.OptimisticLockException) {
				throw new Exception("msg.erro.lock.otimista", new ConcurrenceException());
			}
			
			if(e.getMessage() != null && e.getMessage().indexOf(BaseConstant.MSG_CONCORRENCIA) != -1){
				throw new Exception("msg.generica.erro.processamento", new ConcurrenceException());
			}
			throw new Exception("msg.generica.erro.processamento", e);
		}
	}
	
	
	@SuppressWarnings("unused")
	private void refreshVersion(BaseEntity entity){
		
		
		
		
	}
	
	

	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.ejb.BaseEJB#alteraObjeto(br.com.fitness.project.model.entity.BaseEntity, java.lang.Object[])
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Object[] alteraObjeto(BaseEntity entidade, Object[] filhosExclusao) throws Exception {
		
		logger.debug("Excluindo filhos antes de alterar o pai.");
		
		excluiFilhos(filhosExclusao);
		
		logger.debug("Excluiu os filhos chama alteraObjeto para alterar o pai");
		
		return this.alteraObjeto(entidade);
	}

	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.ejb.BaseEJB#excluiObjeto(br.com.fitness.project.model.entity.BaseEntity)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String excluiObjeto(BaseEntity entidade) throws Exception {
		try{
			
			if (entidade == null) {
				throw new Exception("excluiObjeto.entidade.nula");
			}

			///exclui objeto
			entidade = em.merge(entidade);
			
			em.remove(entidade);
			
			return "msg.generica.objeto.excluido.sucesso";
	
		}catch (Exception e) {
			//encapsula exceção
			logger.debug("Lançou erro no excluiObjeto.");
			e.printStackTrace();
			if(e.getMessage().indexOf(BaseConstant.MSG_CONCORRENCIA) != -1){
				throw new Exception("msg.generica.erro.processamento", new ConcurrenceException());
			}
			throw new Exception("msg.generica.erro.processamento", e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.ejb.BaseEJBRemote#excluiObjeto(br.com.fitness.project.model.entity.BaseEntity, java.lang.Object[])
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String excluiObjeto(BaseEntity entidade, Object[] filhosExclusao) throws Exception {
		try{
			
			if (entidade == null) {
				throw new Exception("excluiObjeto.entidade.nula");
			}

			//exclui objeto
			entidade = em.merge(entidade);
			
			excluiFilhos(filhosExclusao);
			
			em.remove(entidade);
			
			return "msg.generica.objeto.excluido.sucesso";
	
		}catch (Exception e) {
			//encapsula exceção
			logger.debug("Lançou erro no excluiObjeto.");
			e.printStackTrace();
			if(e.getMessage().indexOf(BaseConstant.MSG_CONCORRENCIA) != -1){
				throw new Exception("msg.generica.erro.processamento", new ConcurrenceException());
			}
			throw new Exception("msg.generica.erro.processamento", e);
		}
	}
	
//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
//	public String excluiObjetoPorId(Class<? extends BaseEntity> clazz, Long id) throws Exception {
//	    try {
//	        if (id == null) {
//	            throw new Exception("ID não pode ser nulo.");
//	        }
//
//	        BaseEntity entidade = em.find(clazz, id);
//
//	        if (entidade == null) {
//	            throw new Exception("Objeto não encontrado para exclusão.");
//	        }
//
//	        logger.debug("Objeto encontrado para exclusão: " + entidade);
//
//	        // Se a entidade tiver um campo 'removido', tente forçar a exclusão real
//	        em.createQuery("DELETE FROM " + clazz.getSimpleName() + " e WHERE e.id = :id")
//	          .setParameter("id", id)
//	          .executeUpdate();
//	        
//	        em.flush(); // Força a sincronização com o banco
//
//	        logger.debug("Objeto removido com sucesso!");
//
//	        return "Objeto excluído com sucesso.";
//
//	    } catch (Exception e) {
//	        logger.error("Erro ao excluir objeto por ID.", e);
//	        throw new Exception("Erro ao excluir objeto.", e);
//	    }
//	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String excluiObjetoPorId(Class<? extends BaseEntity> clazz, Long id) throws Exception {
	    try {
	        if (id == null) {
	            throw new Exception("ID não pode ser nulo.");
	        }

	        BaseEntity entidade = em.find(clazz, id);

	        if (entidade == null) {
	            throw new Exception("Objeto não encontrado para exclusão.");
	        }

	        logger.debug("Objeto encontrado para exclusão: " + entidade);

	        // Se for um PedestreRegraEntity, excluir os registros filhos antes
	        if (clazz.equals(PedestreRegraEntity.class)) {
	            em.createQuery("DELETE FROM HorarioEntity h WHERE h.pedestreRegra.id = :id")
	              .setParameter("id", id)
	              .executeUpdate();
	            em.flush();
	        }

	        em.createQuery("DELETE FROM " + clazz.getSimpleName() + " e WHERE e.id = :id")
	          .setParameter("id", id)
	          .executeUpdate();
	        em.flush();

	        logger.debug("Objeto removido com sucesso!");

	        return "Objeto excluído com sucesso.";

	    } catch (Exception e) {
	        logger.error("Erro ao excluir objeto por ID.", e);
	        throw new Exception("Erro ao excluir objeto.", e);
	    }
	}


	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.ejb.BaseEJBRemote#pesquisaSimples(java.lang.Class, java.lang.String)
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<BaseEntity> pesquisaSimples(Class classeEntidade,
			String namedQuery) throws Exception  {
		try{
			
			String nomeClasse = classeEntidade.getSimpleName();
			
			if (namedQuery == null) {
				throw new Exception("recuperaLista.namedQuery.nula");
			}

			Query query = getEntityManager().createNamedQuery(nomeClasse+"."+namedQuery);

			//executa query
			List<BaseEntity> result = new ArrayList<BaseEntity>();
			try{
				result = query.getResultList();
			}catch (NoResultException e) {
				return null;
			}
			
			//retiraResultadoSessao(result);
			
			return result;
		
		}catch (Exception e) {
			//encapsula exceção
			logger.debug("Lança erro no pesquisaSimples");
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	/**
	 * Retira objetos de resultado da sessão hibernate
	 *
	 * @author: Gustavo Diniz
	 * @param result - resultado
	 */
	@SuppressWarnings("rawtypes")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void retiraResultadoSessao(List<? extends BaseEntity> result) {
		
		if(result == null){
			return;
		}
		
		for (Iterator iterator = result.iterator(); iterator.hasNext();) {
			BaseEntity object = (BaseEntity) iterator.next();
			em.detach(object);
			logger.debug("Entidade: " + object.toString() + " retirado da sessão.");
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.ejb.BaseEJBRemote#pesquisaSimples(java.lang.Class, java.lang.String, java.util.Map)
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<? extends Object> pesquisaSimples(Class classeEntidade,
			String namedQuery, Map<String, Object> arg) throws Exception {
		
		try{
			
			String nomeClasse = classeEntidade.getSimpleName();
			
			if (namedQuery == null) {
				throw new Exception("recuperaLista.namedQuery.nula");
			}

			Query query = getEntityManager().createNamedQuery(nomeClasse+"."+namedQuery);

			if (arg != null) {

				//complementa query com argumentos
				String queryStr = this.complementaQuery(query.unwrap(org.hibernate.Query.class).getQueryString(), arg, null);
				query = getEntityManager().createQuery(queryStr);

				//seta argumentos
				if (queryStr.indexOf("where") != -1) {
					this.setaArgumentos(arg, query);
				}

			}

			//executa query
			List<BaseEntity> result = new ArrayList<BaseEntity>();
			try{
				result = query.getResultList();
			}catch (NoResultException e) {
				return null;
			}
			
//			retiraResultadoSessao(result);
			
			return result;
		
		}catch (Exception e) {
			//encapsula exceção
			logger.debug("Lança erro no pesquisaSimples com argumentos dinâmicos");
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	protected EntityManager getEntityManager() {
		
		Session session = em.unwrap(Session.class);
		session.enableFilter("departValido");
		session.enableFilter("centroValido");
		session.enableFilter("cargoValido");
		session.enableFilter("horarioValido");
//		session.enableFilter("validQuestion");
//		session.enableFilter("validDayTrain");
//		session.enableFilter("validPaymentSlips");
//		session.enableFilter("validPaymentChecks");
		
		return this.em;
	}

	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.ejb.BaseEJBRemote#pesquisaArgFixos(java.lang.Class, java.lang.String, java.util.Map)
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<?> pesquisaArgFixos(Class classeEntidade,
			String namedQuery, Map<String, Object> arg) throws Exception  {
		
		try{
			
			String nomeClasse = classeEntidade.getSimpleName();
			
			Query query = getEntityManager().createNamedQuery(nomeClasse+"."+namedQuery);

			if (arg != null) {

				//seta argumentos
				for (Iterator iter = arg.keySet().iterator();
						iter.hasNext();
						) {
					String chave = (String) iter.next();
					if (arg.get(chave) instanceof Collection) {
						query.setParameter(
								chave,
								(Collection) arg.get(chave));
					} else {
						query.setParameter(chave, arg.get(chave));
					}

				}

			}
			
			List<BaseEntity> result = new ArrayList<BaseEntity>();
			try{
				result = query.getResultList();
			}catch (NoResultException e) {
				return null;
			}
			
			//retiraResultadoSessao(result);
			
			return result;
		
		}catch (Exception e) {
			//encapsula exceção
			logger.debug("Lança erro no pesquisaArgFixos");
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
	}
	
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<?> pesquisaArgFixosLimitadoOrdenado(Class classeEntidade,
			String namedQuery, Map<String, Object> arg, List<String> order, int ini, int quant) throws Exception  {
		
		try{
			
			String nomeClasse = classeEntidade.getSimpleName();
			
			Query query = getEntityManager().createNamedQuery(nomeClasse+"."+namedQuery);
			
			// Implementar o order
			if (order != null && !order.isEmpty()) {
				StringBuffer queryStrBuff = new StringBuffer(query.unwrap(org.hibernate.Query.class).getQueryString());
				
				// remove o order da query original
				if (queryStrBuff.indexOf("order by") != -1)
					queryStrBuff.replace(queryStrBuff.indexOf("order by"), queryStrBuff.length(), "");
				
				String newOrderBy = "order by ";
				String virgula = "";
				for (String key : order) {
					newOrderBy += virgula + key;
					virgula = ", ";
				}
				queryStrBuff.append(" " + newOrderBy);
				query = getEntityManager().createQuery(queryStrBuff.toString());
			}
			
			if (arg != null) {

				//seta argumentos
				for (Iterator iter = arg.keySet().iterator(); iter.hasNext(); ) {
					String chave = (String) iter.next();
					if (arg.get(chave) instanceof Collection) {
						query.setParameter(chave,(Collection) arg.get(chave));
					} 
					else {
						query.setParameter(chave, arg.get(chave));
					}
				}
			}
			
			List<BaseEntity> result = new ArrayList<BaseEntity>();
			try {
				query.setFirstResult(ini);
				query.setMaxResults(quant);
				result = query.getResultList();
			}
			catch (NoResultException e) {
				return null;
			}
			
			return result;
		
		}catch (Exception e) {
			//encapsula exceção
			logger.debug("Lança erro no pesquisaArgFixos");
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.ejb.BaseEJBRemote#pesquisaArgFixos(java.lang.Class, java.lang.String, java.util.Map)
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<?> pesquisaArgFixosLimitado(Class classeEntidade,
			String namedQuery, Map<String, Object> arg, int ini, int quant) throws Exception  {
		
		try{
			
			String nomeClasse = classeEntidade.getSimpleName();
			
			Query query = getEntityManager().createNamedQuery(nomeClasse+"."+namedQuery);

			if (arg != null) {

				//seta argumentos
				for (Iterator iter = arg.keySet().iterator();
						iter.hasNext();
						) {
					String chave = (String) iter.next();
					if (arg.get(chave) instanceof Collection) {
						query.setParameter(
								chave,
								(Collection) arg.get(chave));
					} else {
						query.setParameter(chave, arg.get(chave));
					}

				}

			}
			List<BaseEntity> result = new ArrayList<BaseEntity>();
			try{
				query.setFirstResult(ini);
				query.setMaxResults(quant);
				result = query.getResultList();
			}catch (NoResultException e) {
				return null;
			}
			
			//retiraResultadoSessao(result);
			
			return result;
		
		}catch (Exception e) {
			//encapsula exceção
			logger.debug("Lança erro no pesquisaArgFixos");
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.ejb.BaseEJBRemote#pesquisaArgFixos(java.lang.Class, java.lang.String, java.util.Map, int)
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<? extends BaseEntity> pesquisaArgFixos(Class classeEntidade,
			String namedQuery, Map<String, Object> arg, int limit) throws Exception  {
		
		try{
			
			String nomeClasse = classeEntidade.getSimpleName();
			
			Query query = getEntityManager().createNamedQuery(nomeClasse+"."+namedQuery);

			if (arg != null) {

				//seta argumentos
				for (Iterator iter = arg.keySet().iterator();
						iter.hasNext();
						) {
					String chave = (String) iter.next();
					if (arg.get(chave) instanceof Collection) {
						query.setParameter(
								chave,
								(Collection) arg.get(chave));
					} else {
						query.setParameter(chave, arg.get(chave));
					}

				}

			}
			
			List<BaseEntity> result = new ArrayList<BaseEntity>();
			try{
				result = query.setMaxResults(limit).getResultList();
			}catch (NoResultException e) {
				return null;
			}
			
			//retiraResultadoSessao(result);
			
			return result;
		
		}catch (Exception e) {
			//encapsula exceção
			logger.debug("Lança erro no pesquisaArgFixos");
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.ejb.BaseEJBRemote#recuperaObjeto(java.lang.Class, java.lang.Object)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public BaseEntity recuperaObjeto(Class classeEntidade, Object id) throws Exception {
		try{
			
			logger.debug("Inicia recuperaObjeto");
	
			String nomeClasse = classeEntidade.getSimpleName();
	
			if (id == null) {
				throw new Exception("recuperaObjeto.chave.nula");
			}

			//descobre namedQuery
			String namedQuery = nomeClasse + ".findById";

			//cria query e seta id
			Query query = getEntityManager().createNamedQuery(namedQuery);
			query.setParameter("ID", id);
			
			try{
				BaseEntity entidade = (BaseEntity) query.getSingleResult();
				
				//this.em.detach(entidade);
				entidade.setExistente(true);
				return entidade;
			}catch (NoResultException e) {
				return null;
			}

		}catch (Exception e) {
			//encapsula exceção
			logger.debug("Lançou erro no recuperaObjeto");
			e.printStackTrace();
			throw new Exception(e.getMessage(),e);
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.ejb.BaseEJBRemote#recuperaObjeto(java.lang.Class, java.lang.Object)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public BaseEntity recuperaObjeto(Class classeEntidade, String namedQueryById, Object id) throws Exception {
		try{
			
			logger.debug("Inicia recuperaObjeto");
	
			String nomeClasse = classeEntidade.getSimpleName();
	
			if (id == null) {
				throw new Exception("recuperaObjeto.chave.nula");
			}

			//descobre namedQuery
			String namedQuery = nomeClasse + "." + namedQueryById;

			//cria query e seta id
			Query query = getEntityManager().createNamedQuery(namedQuery);
			query.setParameter("ID", id);
			
			try{
				BaseEntity entidade = (BaseEntity) query.getSingleResult();
				
				//this.em.detach(entidade);
				entidade.setExistente(true);
				return entidade;
			}catch (NoResultException e) {
				return null;
			}

		}catch (Exception e) {
			//encapsula exceção
			logger.debug("Lançou erro no recuperaObjeto");
			e.printStackTrace();
			throw new Exception(e.getMessage(),e);
		}
		
	}
    
	/**
	 * 
	 * Itera argumentos para adiciona-los na nova query
	 *
	 * @author: Gustavo Diniz
	 * @param argumentos - argumentos para adicionar
	 * @param query - query
	 */
	@SuppressWarnings("rawtypes")
	protected void setaArgumentos(Map<String, Object> argumentos, Query query) {
		for (Iterator iter = argumentos.keySet().iterator(); iter.hasNext();) {
			String chave = (String) iter.next();
			if (argumentos.get(chave) == null) {
				continue;
			}

			if ("".equals(argumentos.get(chave))) {
				continue;
			}

			Date data = null;
			if (chave.endsWith("_menor_data")
					&& argumentos.get(chave) instanceof String) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				formatter.setLenient(false);
				try {
					data = formatter.parse((String) argumentos.get(chave));
				} catch (Exception e) {
					e.printStackTrace();
				}
				//data = DateUtils.getInstance().setLastHour(data);
			} else if (chave.endsWith("_menor_data")
					&& argumentos.get(chave) instanceof Date) {
				data = (Date) argumentos.get(chave);// = DateUtils.getInstance().setLastHour((Date) argumentos.get(chave));
			} else if (chave.endsWith("_data")
					&& argumentos.get(chave) instanceof String) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				formatter.setLenient(false);
				try {
					data = formatter.parse((String) argumentos.get(chave));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if("null".equals(argumentos.get(chave)) || "null_empty".equals(argumentos.get(chave))){
				continue;
			}
			
			
			if(chave.endsWith("_in")){
				query.setParameter(this.formataNomeParametro(chave),
						(List)argumentos.get(chave));
			}
			else {
				query.setParameter(this.formataNomeParametro(chave),
						data != null ? data : argumentos.get(chave));
			}
			
			
		}
	}

	/**
	 * 
	 * Complementa query com os argumentos passados
	 *
	 * @author: Gustavo Diniz
	 * @param query - query para complementação
	 * @param argumentos - argumentos que devem ser inseridos
	 * @return query completa
	 */
	@SuppressWarnings("rawtypes")
	public String complementaQuery(String query, Map<String, Object> argumentos, Map<String, String> order) {

		if (query != null) {

			StringBuffer queryStr = new StringBuffer(query.toString());


			//retira select
			String select = "";
			if(queryStr.indexOf("select") != -1){
				select = queryStr.substring(queryStr.indexOf("select"), queryStr.indexOf("from"));
				queryStr.replace(queryStr.indexOf("select"), queryStr.indexOf("from"), "");
			}


			//retira "order by" ou "group by" para colocar posteriormente
			String orderBy = "";
			if (queryStr.indexOf("order") != -1) {
				orderBy =
						queryStr.substring(
								queryStr.indexOf("order"),
								queryStr.length());
				queryStr.replace(
						queryStr.indexOf("order"),
						queryStr.length(),
						"");
			}
			String groupBy = "";
			if (queryStr.indexOf("group") != -1) {
				groupBy =
						queryStr.substring(
								queryStr.indexOf("group"),
								queryStr.length());
				queryStr.replace(
						queryStr.indexOf("group"),
						queryStr.length(),
						"");
			}

			//adiciona argumentos
			String and = "";
			for (Iterator iter = argumentos.keySet().iterator();
					iter.hasNext();
					) {
				String chave = (String) iter.next();

				if (argumentos.get(chave) == null) {
					continue;
				}

				if ("".equals(argumentos.get(chave))) {
					continue;
				}
				
				/*
				 * verifica se é uma data, se Não for, pula para baixo
				 */
				String parameterString = ":" + this.formataNomeParametro(chave);
				if ("null".equals(argumentos.get(chave))) {
					
					String isNull = " is null";// or obj." + chave + " = '' ";
					if(chave.contains("_dif")){
						chave = chave.substring(0, chave.indexOf("_dif"));
						isNull = " is not null";// and obj." + chave + " != '' ";
					}
					
					
					//monta query
					if (queryStr.indexOf("where") != -1) {
						//tem where
						and = " and ";
						String queryPart = " obj." + chave + isNull;

						queryStr.insert(
								queryStr.indexOf("where") + "where".length(),
								queryPart + and);

					} else {
						//adiciona where
						queryStr.insert(
								queryStr.length(),
								" where obj." + chave + isNull);
					}
					
				}else if ("null_empty".equals(argumentos.get(chave))) {
					
					String isNull = " is null or obj." + chave + " = '' ";
					if(chave.contains("_dif")){
						chave = chave.substring(0, chave.indexOf("_dif"));
						isNull = " is not null and obj." + chave + " != '' ";
					}
					
					
					//monta query
					if (queryStr.indexOf("where") != -1) {
						//tem where
						and = " and ";
						String queryPart = " ( obj." + chave + isNull + ") ";

						queryStr.insert(
								queryStr.indexOf("where") + "where".length(),
								queryPart + and);

					} else {
						//adiciona where
						queryStr.insert(
								queryStr.length(),
								" where ( obj." + chave + isNull + ") ");
					}
					
				}else if (chave.endsWith("_numero_maior")) {

					complementaQueryMaior(queryStr, chave, parameterString);

				}else if (chave.endsWith("_numero_menor")) {

					complementaQueryMenor(queryStr, chave, parameterString);

				}else if (chave.endsWith("_data")) {

					complementaQueryData(queryStr, chave, parameterString);

				}else if(chave.endsWith("_boolean")){
					
					complementaQueryBoolean(argumentos, queryStr, chave,
							parameterString);
					
				}else if(chave.endsWith("_dif")){
					
					complementaQueryDiferente(queryStr, chave, parameterString);
					
				}else if(chave.endsWith("_in")){
					
					complementaQueryEntre(queryStr, chave, parameterString);
					
				}else if(chave.endsWith("_equals")){
					
					complementaQueryIgual(queryStr, chave, parameterString);
					
				}else if(chave.endsWith("bloco_or")){
					
					//monta query
					if (queryStr.indexOf("where") != -1) {
						//tem where
						and = " and ";
					}else{
						and = " where ";
					}
					
					queryStr.append(and + " ("+argumentos.get(chave)+") ");
					
					iter.remove();
					continue;
					
					
				}else {

					/*
					 * faz para itens que Não sÃ£o datas
					 */

					//like ou igual
					String operacao = " = ";
					if(!(argumentos.get(chave) instanceof Number)
							&& argumentos.get(chave) instanceof String) {
						operacao = " like '%'||";
					}

					//monta query
					if (queryStr.indexOf("where") != -1) {
						//tem where
						and = " and ";
						String queryPart = " obj." + chave + operacao + parameterString;

						if(operacao.lastIndexOf("like") > 0){
							queryPart = queryPart.concat("||'%'");
						}

						queryStr.insert(
								queryStr.indexOf("where") + "where".length(),
								queryPart + and);

						if(operacao.lastIndexOf("like") > 0){
							parameterString = parameterString.concat("||'%'");
						}

					} else {
						if(operacao.lastIndexOf("like") > 0){
							parameterString = parameterString.concat("||'%'");
						}
						//adiciona where
						queryStr.insert(
								queryStr.length(),
								" where obj." + chave + operacao + parameterString);
					}

				}

				and = " and ";

			}

			queryStr.insert(0, select);
			if(order == null){
				//trabalha com ordenação original
				queryStr.append(" " + groupBy + " " + orderBy);
			}else{
				//monta nova ordenação conforme
				//indicaca
				String newOrderBy = "order by ";
				String virgula = "";
				for (String key : order.keySet()) {
					newOrderBy += virgula + "obj."+key + " " + order.get(key);
					virgula = ", ";
				}
				queryStr.append(" " + groupBy + " " + newOrderBy);
			}

			return queryStr.toString();
		}

		return "";
	}

	private void complementaQueryMaior(StringBuffer queryStr, String chave, String parameterString) {
		String and;
		String queryDatas = "";
		String chaveOriginal = "";

		if (chave.endsWith("_numero_maior")) {
			
			//seta data normal
			chaveOriginal =
					chave.substring(0, chave.indexOf("_numero_maior"));
			queryDatas =
					" obj." + chaveOriginal + " >= " + parameterString;
			
		}
		
		if (!"".equals(queryDatas)) {
			if (queryStr.indexOf("where") != -1) {
				//tem where
				and = " and ";
				queryStr.insert(
						queryStr.indexOf("where") + "where".length(),
						queryDatas + and);

			} else {
				//adiciona where
				queryStr.insert(
						queryStr.length(),
						" where " + queryDatas);
			}
		}
		
	}
	
	private void complementaQueryMenor(StringBuffer queryStr, String chave, String parameterString) {
		String and;
		String queryDatas = "";
		String chaveOriginal = "";

		if (chave.endsWith("_numero_menor")) {
			
			//seta data normal
			chaveOriginal =
					chave.substring(0, chave.indexOf("_numero_menor"));
			queryDatas =
					" obj." + chaveOriginal + " <= " + parameterString;
			
		}
		
		if (!"".equals(queryDatas)) {
			if (queryStr.indexOf("where") != -1) {
				//tem where
				and = " and ";
				queryStr.insert(
						queryStr.indexOf("where") + "where".length(),
						queryDatas + and);

			} else {
				//adiciona where
				queryStr.insert(
						queryStr.length(),
						" where " + queryDatas);
			}
		}
		
	}

	/**
	 * Complementa query que possuem itens que são listas
	 *
	 * @author: Gustavo Diniz
	 * @param queryStr - query
	 * @param chave - chave
	 * @param parameterString - parametros
	 */
	private void complementaQueryEntre(StringBuffer queryStr, String chave,
			String parameterString) {

		String chaveOriginal = "";
		String queryNova = "";
		
		if(chave.contains("_not_in")) {
			chaveOriginal = chave.substring(0, chave.indexOf("_not_in"));
			queryNova = " obj." + chaveOriginal + " not in (" + parameterString + ") ";
		}else {
			chaveOriginal = chave.substring(0, chave.indexOf("_in"));
			queryNova = " obj." + chaveOriginal + " in (" + parameterString + ") ";
		}
		
		if (queryStr.indexOf("where") == -1){
			queryStr.insert(queryStr.length()," where " + queryNova);
		}else{
			queryStr.insert(queryStr.length()," and (" + queryNova+" ) ");
		}
		
	}

	/**
	 * Complementa query que possuem itens que serão diferentes na pesquisa
	 *
	 * @author: Gustavo Diniz
	 * @param queryStr - query
	 * @param chave - chave
	 * @param parameterString - parametros
	 */
	private void complementaQueryDiferente(StringBuffer queryStr, String chave,
			String parameterString) {
		//monta query

		String chaveOriginal = chave.substring(0, chave.indexOf("_dif"));

		String queryNova = null;
		if(parameterString.equals("null"))
			queryNova = " obj." + chaveOriginal + " is not null";
		else
			queryNova = " obj." + chaveOriginal + " != " + parameterString + " ";

		if (queryStr.indexOf("where") == -1)
		{
			queryStr.insert(queryStr.length()," where " + queryNova);
		}else{
			queryStr.insert(queryStr.length()," and (" + queryNova+" ) ");
		}
	}
	
	/**
	 * Complementa query que possuem itens que serão diferentes na pesquisa
	 *
	 * @author: Gustavo Diniz
	 * @param queryStr - query
	 * @param chave - chave
	 * @param parameterString - parametros
	 */
	private void complementaQueryIgual(StringBuffer queryStr, String chave,
			String parameterString) {
		//monta query
		String chaveOriginal = chave.substring(0, chave.indexOf("_equals"));
		String queryNova = " obj." + chaveOriginal + " = " + parameterString + " ";

		if (queryStr.indexOf("where") == -1){
			queryStr.insert(queryStr.length()," where " + queryNova);
		}else{
			queryStr.insert(queryStr.length()," and (" + queryNova+" ) ");
		}
	}

	/**
	 * Complementa query que possuem itens que serão convertidos para booleano
	 *
	 * @author: Gustavo Diniz
	 * @param argumentos - argumentos
	 * @param queryStr - query
	 * @param chave - chave
	 * @param parameterString - parametros
	 */
	private void complementaQueryBoolean(Map<String, Object> argumentos, StringBuffer queryStr,
			String chave, String parameterString) {
		//monta query
		/*
		 * Eh necessario fazer a conversao do booleano para String.
		 */
		if(((Boolean)argumentos.get(chave)).booleanValue()){
			argumentos.put(chave, "S");
		}else{
			argumentos.put(chave, "N");
		}

		String chaveOriginal = chave.substring(0, chave.indexOf("_boolean"));

		String queryDatas = " obj." + chaveOriginal + " like '%'||" + parameterString + "||'%'";

		if (queryStr.indexOf("where") == -1)
		{
			queryStr.insert(queryStr.length()," where " + queryDatas);
		}else{
			queryStr.insert(queryStr.length()," and (" + queryDatas+" ) ");
		}
	}

	/**
	 * Realiza lógica para complementar querys que possuem datas
	 *
	 * @author: Gustavo Diniz
	 * @param queryStr - query
	 * @param chave - chave
	 * @param parameterString - parametro
	 */
	private void complementaQueryData(StringBuffer queryStr, String chave,
			String parameterString) {
		String and;
		String queryDatas = "";
		String chaveOriginal = "";

		if (chave.endsWith("_ini_data")) {

			//se for data inicial faz between e espera data final
			chaveOriginal =
					chave.substring(0, chave.indexOf("_ini_data"));
			String chaveIni = chave;
			String chaveFim = chaveOriginal + "_fim_data";

			queryDatas =
					" obj."
							+ chaveOriginal
							+ " between "
							+ " :"
							+ this.formataNomeParametro(chaveIni)
							+ " and :"
							+ this.formataNomeParametro(chaveFim);

		}else if(chave.endsWith("_between_data")){
			chaveOriginal =
					chave.substring(0, chave.indexOf("_between_data"));
			
			queryDatas =
					"(obj." + chaveOriginal + "Ini >= " + parameterString +
					" and obj." + chaveOriginal + "Fim <= " + parameterString+ ") ";
		} else if (chave.endsWith("_menor_data")) {

			//seta data normal
			chaveOriginal =
					chave.substring(0, chave.indexOf("_menor_data"));
			queryDatas =
					" obj." + chaveOriginal + " <= " + parameterString;

		} else if (chave.endsWith("_maior_data")) {
			
			//seta data normal
			chaveOriginal =
					chave.substring(0, chave.indexOf("_maior_data"));
			queryDatas =
					" obj." + chaveOriginal + " >= " + parameterString;
			
		} else if (!chave.endsWith("_fim_data")) {

			//seta data normal
			chaveOriginal =
					chave.substring(0, chave.indexOf("_data"));
			queryDatas =
					" obj." + chaveOriginal + " = " + parameterString;

		}
		if (!"".equals(queryDatas)) {
			if (queryStr.indexOf("where") != -1) {
				//tem where
				and = " and ";
				queryStr.insert(
						queryStr.indexOf("where") + "where".length(),
						queryDatas + and);

			} else {
				//adiciona where
				queryStr.insert(
						queryStr.length(),
						" where " + queryDatas);
			}
		}
	}
	
	/**
	 * 
	 * Formata nome do parametro para query
	 *
	 * @author: Gustavo Diniz
	 * @param param - parametro para formatação
	 * @return s
	 */
	public String formataNomeParametro(String param){
		return param.replace('.', '_');
	}
	
	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.dao.BaseDAO#recuperaUnicoRegistro(java.util.Map, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Object recuperaUnicoRegistro(Map<String, Object> argumentos, String namedQuery)
			throws Exception {

		try {

			if (namedQuery == null) {
				throw new Exception("recuperaLista.namedQuery.nula");
			}

			Query query = getEntityManager().createNamedQuery(namedQuery);

			if (argumentos != null) {

				//complementa query com argumentos
				String queryStr =
						this.complementaQuery(query.unwrap(org.hibernate.Query.class).getQueryString(), argumentos, null);
				query = getEntityManager().createQuery(queryStr);

				//seta argumentos
				if (queryStr.indexOf("where") != -1) {
					this.setaArgumentos(argumentos, query);
				}

			}

			//executa query
			try{
				
				List<BaseEntity> resultado = query.getResultList();

				if (resultado.isEmpty()) {
					return null;
				} else {
					return resultado.get(0);
				}
			
			}catch (NoResultException e) {
				return null;
			}

		} catch (Exception e) {
			//encapsula exceção
			throw new Exception(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.ejb.BaseEJBRemote#pesquisaSimplesLimitado(java.lang.Class, java.lang.String, java.util.Map, int, int)
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<?> pesquisaSimplesLimitado(Class classeEntidade,
			String namedQuery, Map<String, Object> arg, int ini, int quant)
			throws Exception {
		try{
			
			String nomeClasse = classeEntidade.getSimpleName();
			
			if (namedQuery == null) {
				throw new Exception("recuperaLista.namedQuery.nula");
			}

			Query query = getEntityManager().createNamedQuery(nomeClasse+"."+namedQuery);

			if (arg != null) {

				//complementa query com argumentos
				String queryStr = this.complementaQuery(query.unwrap(org.hibernate.Query.class).getQueryString(), arg, null);
				query = getEntityManager().createQuery(queryStr);
				
				//seta argumentos
				if (queryStr.indexOf("where") != -1) {
					this.setaArgumentos(arg, query);
				}

			}

			//executa query
			List<Object> result = new ArrayList<Object>();
			try{
				//busca resultados limitados
				query.setFirstResult(ini);
				query.setMaxResults(quant);
				List<Object> resultQuery = query.getResultList();
				if(resultQuery != null && !resultQuery.isEmpty()){
					for (Object object : resultQuery) {
						result.add(object);
					}
				}
			}catch (NoResultException e) {
				return null;
			}
			
			return result;
			
		}catch (Exception e) {
			//encapsula exceção
			logger.debug("Lança erro no pesquisaSimplesLimitado com argumentos dinâmicos");
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.ejb.BaseEJBRemote#pesquisaSimplesLimitado(java.lang.Class, java.lang.String, java.util.Map, int, int)
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<?> pesquisaSimplesLimitado(Class classeEntidade,
			String namedQuery, Map<String, Object> arg, Map<String, String> order, int ini, int quant)
			throws Exception {
		try{
			
			String nomeClasse = classeEntidade.getSimpleName();
			
			if (namedQuery == null) {
				throw new Exception("recuperaLista.namedQuery.nula");
			}

			Query query = getEntityManager().createNamedQuery(nomeClasse+"."+namedQuery);

			if (arg != null) {

				//complementa query com argumentos
				String queryStr = this.complementaQuery(query.unwrap(org.hibernate.Query.class).getQueryString(), arg, order);
				query = getEntityManager().createQuery(queryStr);
				
				//seta argumentos
				if (queryStr.indexOf("where") != -1) {
					this.setaArgumentos(arg, query);
				}
				
			}

			//executa query
			List<Object> result = new ArrayList<Object>();
			try{
				//busca resultados limitados
				query.setFirstResult(ini);
				query.setMaxResults(quant);
				List<Object> resultQuery = query.getResultList();
				if(resultQuery != null && !resultQuery.isEmpty()){
					for (Object object : resultQuery) {
						result.add(object);
					}
				}
			}catch (NoResultException e) {
				return null;
			}
			
			return result;
			
		}catch (Exception e) {
			//encapsula exceção
			logger.debug("Lança erro no pesquisaSimplesLimitado com argumentos dinâmicos");
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.ejb.BaseEJBRemote#pesquisaSimplesCount(java.lang.Class, java.lang.String, java.util.Map)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public int pesquisaSimplesCount(Class classeEntidade, String namedQuery,
			Map<String, Object> arg) throws Exception {
		
		try{
			
			String nomeClasse = classeEntidade.getSimpleName();
			
			if (namedQuery == null) {
				throw new Exception("recuperaLista.namedQuery.nula");
			}

			Query query = getEntityManager().createNamedQuery(nomeClasse+"."+namedQuery);

			if (arg != null) {

				//complementa query com argumentos
				String queryStr = this.complementaQuery(query.unwrap(org.hibernate.Query.class).getQueryString(), arg, null);
				
				StringBuffer queryBufferStr = new StringBuffer(queryStr.toString());

				//retira select completo e coloca o count
				if(queryStr.indexOf("select") != -1){
					queryBufferStr.replace(queryStr.indexOf("select"), 
							queryStr.indexOf("from"), " select count(distinct obj.id) ");
				}
				
				if(queryStr.indexOf("order by") != -1){
					queryBufferStr.replace(queryBufferStr.indexOf("order by"), queryBufferStr.length(), "");
				}
				
				if(queryStr.indexOf("group by") != -1){
					queryBufferStr.replace(queryBufferStr.indexOf("group by"), queryBufferStr.length(), "");
				}
				
				query = getEntityManager().createQuery(queryBufferStr.toString().replace("fetch", ""));

				//seta argumentos
				if (queryStr.indexOf("where") != -1) {
					this.setaArgumentos(arg, query);
				}

			}else{
				
				String queryStr = query.unwrap(org.hibernate.Query.class).getQueryString();
				StringBuffer queryBufferStr = new StringBuffer(queryStr.toString());

				//retira select completo e coloca o count
				if(queryStr.indexOf("select") != -1){
					queryBufferStr.replace(queryStr.indexOf("select"), 
							queryStr.indexOf("from"), " select count(obj.id) ");
				}
				
				if(queryStr.indexOf("order by") != -1){
					queryBufferStr.replace(queryBufferStr.indexOf("order by"), queryBufferStr.length(), "");
				}
				
				if(queryStr.indexOf("group by") != -1){
					queryBufferStr.replace(queryBufferStr.indexOf("group by"), queryBufferStr.length(), "");
				}
				
				query = getEntityManager().createQuery(queryBufferStr.toString().replace("fetch", ""));
				
			}

			//executa query
			try{
				Long val = (Long) query.getSingleResult();
				
				if(val == null)
					return 0;
				return val.intValue();
			}catch (NoResultException e) {
				return 0;
			}
			
		}catch (Exception e) {
			//encapsula exceção
			logger.debug("Lança erro no pesquisaSimples com argumentos dinâmicos");
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.ejb.BaseEJBRemote#pesquisaSimplesCount(java.lang.Class, java.lang.String, java.util.Map)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public int pesquisaSimplesCount(Class classeEntidade, String namedQuery,
			Map<String, Object> arg, String field) throws Exception {
		
		try{
			
			String nomeClasse = classeEntidade.getSimpleName();
			
			if (namedQuery == null) {
				throw new Exception("recuperaLista.namedQuery.nula");
			}

			Query query = getEntityManager().createNamedQuery(nomeClasse+"."+namedQuery);

			if (arg != null) {

				//complementa query com argumentos
				String queryStr = this.complementaQuery(query.unwrap(org.hibernate.Query.class).getQueryString(), arg, null);
				
				StringBuffer queryBufferStr = new StringBuffer(queryStr.toString());

				//retira select completo e coloca o count
				if(queryStr.indexOf("select") != -1){
					queryBufferStr.replace(queryStr.indexOf("select"), 
							queryStr.indexOf("from"), " select count(distinct obj."+field+") ");
				}
				
				if(queryStr.indexOf("order by") != -1){
					queryBufferStr.replace(queryBufferStr.indexOf("order by"), queryBufferStr.length(), "");
				}
				
				if(queryStr.indexOf("group by") != -1){
					queryBufferStr.replace(queryBufferStr.indexOf("group by"), queryBufferStr.length(), "");
				}
				
				query = getEntityManager().createQuery(queryBufferStr.toString().replace("fetch", ""));

				//seta argumentos
				if (queryStr.indexOf("where") != -1) {
					this.setaArgumentos(arg, query);
				}

			}else{
				
				String queryStr = query.unwrap(org.hibernate.Query.class).getQueryString();
				StringBuffer queryBufferStr = new StringBuffer(queryStr.toString());

				//retira select completo e coloca o count
				if(queryStr.indexOf("select") != -1){
					queryBufferStr.replace(queryStr.indexOf("select"), 
							queryStr.indexOf("from"), " select count(obj."+field+") ");
				}
				
				if(queryStr.indexOf("order by") != -1){
					queryBufferStr.replace(queryBufferStr.indexOf("order by"), queryBufferStr.length(), "");
				}
				
				if(queryStr.indexOf("group by") != -1){
					queryBufferStr.replace(queryBufferStr.indexOf("group by"), queryBufferStr.length(), "");
				}
				
				query = getEntityManager().createQuery(queryBufferStr.toString().replace("fetch", ""));
				
			}

			//executa query
			try{
				Long val = (Long) query.getSingleResult();
				if(val == null)
					return 0;
				return val.intValue();
			}catch (NoResultException e) {
				return 0;
			}
			
		}catch (Exception e) {
			//encapsula exceção
			logger.debug("Lança erro no pesquisaSimples com argumentos dinâmicos");
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
	}
	
	
	@Override
	@SuppressWarnings("rawtypes")
	public int pesquisaArgFixosLimitadoCount(Class classeEntidade, String namedQuery,
			Map<String, Object> arg) throws Exception {
		
		try{
			
			String nomeClasse = classeEntidade.getSimpleName();
			
			if (namedQuery == null) {
				throw new Exception("recuperaLista.namedQuery.nula");
			}

			Query query = getEntityManager().createNamedQuery(nomeClasse+"."+namedQuery);
			
			String queryStr = query.unwrap(org.hibernate.Query.class).getQueryString();
			StringBuffer queryBufferStr = new StringBuffer(queryStr);
			
			//retira select completo e coloca o count
			if(queryStr.indexOf("select") != -1){
				queryBufferStr.replace(queryStr.indexOf("select"), 
						queryStr.lastIndexOf("from " + nomeClasse), " select count(obj.id) ");
			}
			
			if(queryStr.indexOf("order by") != -1){
				queryBufferStr.replace(queryBufferStr.indexOf("order by"), queryBufferStr.length(), "");
			}
			
			query = getEntityManager().createQuery(queryBufferStr.toString().replace("fetch", ""));
			
			if (arg != null) {
				//seta argumentos
				for (Iterator iter = arg.keySet().iterator(); iter.hasNext(); ) {
					String chave = (String) iter.next();
					if (arg.get(chave) instanceof Collection) {
						query.setParameter(chave,(Collection) arg.get(chave));
					} 
					else {
						query.setParameter(chave, arg.get(chave));
					}
				}
			}

			//executa query
			try{
				Long val = (Long) query.getSingleResult();
				if(val == null)
					return 0;
				return val.intValue();
			}catch (NoResultException e) {
				return 0;
			}
			
		}catch (Exception e) {
			//encapsula exceção
			logger.debug("Lança erro no pesquisaSimples com argumentos dinâmicos");
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.ejb.BaseEJBRemote#pesquisaSimplesSum(java.lang.Class, java.lang.String, java.util.Map)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public double pesquisaSimplesSum(Class classeEntidade, String namedQuery,
			Map<String, Object> arg) throws Exception {
		
		try{
			
			String nomeClasse = classeEntidade.getSimpleName();
			
			if (namedQuery == null) {
				throw new Exception("recuperaLista.namedQuery.nula");
			}

			Query query = getEntityManager().createNamedQuery(nomeClasse+"."+namedQuery);

			if (arg != null) {

				//complementa query com argumentos
				String queryStr = this.complementaQuery(query.unwrap(org.hibernate.Query.class).getQueryString(), arg, null);
								
				query = getEntityManager().createQuery(queryStr);

				//seta argumentos
				if (queryStr.indexOf("where") != -1) {
					this.setaArgumentos(arg, query);
				}

			}

			//executa query
			try{
				Double val = (Double) query.getSingleResult();
				if(val == null)
					return 0d;
				return val;
			}catch (NoResultException e) {
				return 0;
			}
			
		}catch (Exception e) {
			//encapsula exceção
			logger.debug("Lança erro no pesquisaSimples com argumentos dinâmicos");
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see br.com.fitness.project.model.ejb.BaseEJBRemote#recuperaUsuariosArgFixos(java.util.Map, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UsuarioEntity> recuperaUsuariosArgFixos(
			Map<String, Object> argumentos, String namedQuery) throws Exception {
		
		List<UsuarioEntity> lista = ((List<UsuarioEntity>) pesquisaArgFixos(UsuarioEntity.class, namedQuery, argumentos));
		
		//recupera lazy
		for (UsuarioEntity user : lista) {
			//TODO : itens
		}
		
		return lista;
	}

	/*
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected List<PedestreEntity> recuperaPedestresArgFixos(
			Map<String, Object> argumentos, String namedQuery) throws Exception {
		
		List<PedestreEntity> lista = ((List<PedestreEntity>)
				pesquisaArgFixos(PedestreEntity.class, namedQuery, argumentos));
		
		return lista;
	}
	
	/*
     * (non-Javadoc)
     * @see br.com.fitness.project.model.ejb.parameters.GerenciaParametrosEJBRemote#gravaTodosParametros(java.util.List)
     */
	@Override
    public List<BaseEntity> gravaTodos(List<BaseEntity> lista) throws Exception{
    	
    	if(lista != null && !lista.isEmpty()){
    		for (BaseEntity e : lista) {
    			Method getId = e.getClass().getMethod("getId", new Class[] {});
    			if(getId != null && getId.invoke(e, new Object[] {}) != null )
    				this.alteraObjeto(e);
    			else
    				this.gravaObjeto(e);
			}
    	}
    	
    	return lista;
    }
	
	/**
	 * Retorna parametros indicado do usuário logado, caso ele seja standAlone
	 * pega dele, senão pega do usuário pai.
	 * 
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ParametroEntity getParametroSistema(String parameterName, Long idCliente){
		
		try {
			
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("ID_CLIENTE", idCliente);
			args.put("NOME", parameterName);
			List<ParametroEntity> list = (List<ParametroEntity>) 
					pesquisaArgFixosLimitado(ParametroEntity.class, "findByClienteIdNome", args, 0, 1);
			if(list != null && !list.isEmpty())
				return list.get(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@TransactionTimeout(unit=TimeUnit.HOURS, value=2)
	public void saveRegisterLogs(List<AcessoEntity> logs) throws Exception {
		if(logs == null || logs.isEmpty()) {
			return;
		}
		
		for(AcessoEntity log : logs) {
			if(log.getIdPedestre() == null) {
				log = (AcessoEntity) gravaObjeto(log)[0];
				continue;
			}

			PedestreEntity pedestre = (PedestreEntity) recuperaObjeto(PedestreEntity.class, log.getIdPedestre());
			
			if(pedestre == null) {
				log = (AcessoEntity) gravaObjeto(log)[0];
				continue;
			}
			
			if(log.getData() != null) {
				AcessoEntity acessoExistente = buscaAcessoByIdPedestreAndAccessDate(log.getIdPedestre(), log.getData());
				if(acessoExistente != null) {
					System.out.println("Log de Acesso já existe! " + log.getIdPedestre() + "  com a data "  + log.getData());
					continue;
				}
			}
			
			AcessoEntity ultimoAcessoIndefinido = buscaUltimoLogIndefinidoPedestre(pedestre.getId());
			if(ultimoAcessoIndefinido != null
					&& isDiferencaMenorQueXSegundos(log.getData(), ultimoAcessoIndefinido.getData())) {

				excluiObjeto(ultimoAcessoIndefinido);
			}
			
			if(!"INDEFINIDO".equalsIgnoreCase(log.getTipo())) {
				List<MensagemEquipamentoEntity> mensagensPedestre = buscaMensagensPedestre(pedestre.getId());
				if(mensagensPedestre != null && !mensagensPedestre.isEmpty()) {
					decrementaQuantidadeDaMensagem(mensagensPedestre);
				}
				
				if(!"Regras ignoradas".equals(log.getRazao()) 
						&& ("SAIDA".equals(log.getSentido()) || !log.getBloquearSaida())) {
					PedestreRegraEntity pedestreRegra = buscaRegraAtiva(pedestre.getId());
					
					if(pedestre.getTipo().equals(TipoPedestre.VISITANTE) 
							&& Objects.isNull(pedestre.getDataCadastroFotoNaHikivision())) {
						apagaDadosCartaoAcessoVisitate(pedestre, pedestreRegra);
					}

					decrementaCreditoRegraPedestre(pedestreRegra);
					
					pedestre = (PedestreEntity) alteraObjeto(pedestre)[0];
					
					if(pedestreRegra != null) {
						alteraObjeto(pedestreRegra);
					}
				}
			}
			
			log.setPedestre(pedestre);
			log = (AcessoEntity) gravaObjeto(log)[0];
		}
	}
	
	@SuppressWarnings("unchecked")
	private AcessoEntity buscaAcessoByIdPedestreAndAccessDate(Long idPedestre, Date data) {
		Map<String, Object> args = new HashMap<>();
		args.put("ID_PEDESTRE", idPedestre);
		args.put("DATA", data); 
		
		try {
			List<AcessoEntity> acessos = (List<AcessoEntity>) pesquisaArgFixosLimitado(AcessoEntity.class, 
						"findLastAccessByIdAndCurrentLastAccess", args, 0, 1);
	
			if(acessos != null && !acessos.isEmpty()) {
				return acessos.get(0);				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private AcessoEntity buscaUltimoLogIndefinidoPedestre(Long idPedestre) {
		Map<String, Object> args = new HashMap<>();
		args.put("ID_PEDESTRE", idPedestre);
		
		try {
			List<AcessoEntity> acessos = (List<AcessoEntity>) pesquisaArgFixosLimitado(AcessoEntity.class, 
						"findLastAccessIndefinidoByIdPedestre", args, 0, 1);
	
			if(acessos != null && !acessos.isEmpty())
				return acessos.get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private boolean isDiferencaMenorQueXSegundos(Date acessoAtual, Date acessoIndefinido) {
		Long maxSecondsAcceptable = Long.valueOf(AppAmbienteUtils.getConfig(
				AppAmbienteUtils.CONFIG_TEMPO_DISPOSITIVO));
		
		
		LocalDateTime acessoAtualDateTime = acessoAtual.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime acessoIndefinidoDateTime = acessoIndefinido.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		
		Long differenceInSeconds = ChronoUnit.SECONDS.between(acessoIndefinidoDateTime, acessoAtualDateTime);
		
		return differenceInSeconds < maxSecondsAcceptable;
	}

	@SuppressWarnings("unchecked")
	private List<MensagemEquipamentoEntity> buscaMensagensPedestre(Long idPedestre) {
		List<MensagemEquipamentoEntity> mensagemPedestre = null;
		
		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("ID_PEDESTRE", idPedestre);
			args.put("DATA_ATUAL", new Date());
			
			mensagemPedestre = (List<MensagemEquipamentoEntity>) pesquisaArgFixos(MensagemEquipamentoEntity.class, 
											"findAllAtivas", args);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return mensagemPedestre;
	}
	
	@SuppressWarnings("unchecked")
	private PedestreRegraEntity buscaRegraAtiva(Long idPedestre) {
		PedestreRegraEntity pedestreRegra = null;
		
		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("ID_PEDESTRE", idPedestre);

			List<PedestreRegraEntity> listaPedestreRegra = (List<PedestreRegraEntity>) 
								pesquisaArgFixos(PedestreRegraEntity.class, "findPedestreRegraAtivo", args);
			
			if(listaPedestreRegra != null && !listaPedestreRegra.isEmpty()) {
				pedestreRegra = listaPedestreRegra.get(0);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return pedestreRegra;
	}

	private void apagaDadosCartaoAcessoVisitate(PedestreEntity visitante, PedestreRegraEntity pedestreRegra) {
		if(pedestreRegra == null || pedestreRegra.getRegra() == null) {
			visitante.setCodigoCartaoAcesso(null);
		
		} else {
			if(TipoRegra.ACESSO_CREDITO.equals(pedestreRegra.getRegra().getTipo())
					&& pedestreRegra.getQtdeDeCreditos().equals(1l)) {
				visitante.setCodigoCartaoAcesso(null);
			
			} else if(TipoRegra.ACESSO_UNICO.equals(pedestreRegra.getRegra().getTipo())) {
				visitante.setCodigoCartaoAcesso(null);
			}
		}
	}
	
	private void decrementaCreditoRegraPedestre(PedestreRegraEntity pedestreRegra) {
		if(pedestreRegra == null || pedestreRegra.getQtdeDeCreditos() == null)
			return;
		
		if(pedestreRegra.getQtdeDeCreditos() > 0l 
				&& (pedestreRegra.getRegra() == null 
						|| TipoRegra.ACESSO_CREDITO.equals(pedestreRegra.getRegra().getTipo())
						|| TipoRegra.ACESSO_UNICO.equals(pedestreRegra.getRegra().getTipo()))) {
			pedestreRegra.setQtdeDeCreditos(pedestreRegra.getQtdeDeCreditos() - 1l);
		}
	}
	
	private void decrementaQuantidadeDaMensagem(List<MensagemEquipamentoEntity> mensagensPedestre) {
		for(MensagemEquipamentoEntity m : mensagensPedestre) {
			if(m.getQuantidade() > 0) {
				m.setQuantidade(m.getQuantidade() - 1);
				try {
					alteraObjeto(m);
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void updateDataAlteracao(Class classe, Date data, Long id) throws Exception {

		String hql = "update " + classe.getSimpleName() + " set dataAlteracao = :DATA where id = :ID";
		Query q = getEntityManager().createQuery(hql);
		q.setParameter("DATA", data);
		q.setParameter("ID", id);
		
		q.executeUpdate();
		
	}
}
