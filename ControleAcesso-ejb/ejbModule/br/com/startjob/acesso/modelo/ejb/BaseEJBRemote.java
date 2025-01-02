package br.com.startjob.acesso.modelo.ejb;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.entity.base.BaseEntity;

/**
 * 
 * Interface EJB para funções básicas.
 * 
 * @author Gustavo Diniz
 * @since 03/02/2013
 */
@Remote
public interface BaseEJBRemote {
	
	/**
	 * Testa EJB
	 * 
	 * @author Gustavo Diniz
	 */
	public void execute();
	
	/**
	 * Grava entidade no banco de dados usando hibernate
	 *
	 * @author: Gustavo Diniz
	 * @param entidade - nova entidade
	 * @return array no layout [entidade,mensagem] 
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Object [] gravaObjeto(BaseEntity entidade) throws Exception;
	
	/**
	 * 
	 * Grava entidade no banco de dados usando hibernate e exclui filhos das listas no array
	 *
	 * @author: Gustavo Diniz
	 * @param entidade - entidade já existente
	 * @param filhosExclusao - array que contém as listas dos filhos para exclusão
	 * @return e
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Object [] gravaObjeto(BaseEntity entidade, Object [] filhosExclusao) throws Exception ;
	
	/**
	 * 
	 * Altera entidade no banco de dados usando hibernate
	 *
	 * @author: Gustavo Diniz
	 * @param entidade - entidade já existente
	 * @return array no layout [entidade,mensagem]
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Object [] alteraObjeto(BaseEntity entidade) throws Exception ;
	
	/**
	 * 
	 * Altera entidade no banco de dados usando hibernate e exclui filhos das listas no array
	 *
	 * @author: Gustavo Diniz
	 * @param entidade - entidade já existente
	 * @param filhosExclusao - array que contém as listas dos filhos para exclusão
	 * @return array no layout [entidade,mensagem]
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Object [] alteraObjeto(BaseEntity entidade, Object [] filhosExclusao) throws Exception ;
	
	
	/**
	 * 
	 * Exclui entidade no banco de dados usando hibernate
	 *
	 * @author: Gustavo Diniz
	 * @param entidade - entidade já existente
	 * @return e
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String excluiObjeto(BaseEntity entidade) throws Exception ;
	
	/**
	 * 
	 * Exclui entidade no banco de dados usando hibernate e exclui filhos das listas no array
	 *
	 * @author: Gustavo Diniz
	 * @param entidade - entidade já existente
	 * @param filhosExclusao - array que contém as listas dos filhos para exclusão
	 * @return e
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String excluiObjeto(BaseEntity entidade, Object [] filhosExclusao) throws Exception ;
	
	
	/**
	 * 
	 * Recupera lista de entidades do banco sem argumentos.
	 *
	 * @author: Gustavo Diniz 
	 * @param classeEntidade - classe representativa
	 * @param namedQuery - namedQuery usada para pesquisa
	 * @return lista de entidades
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@SuppressWarnings("rawtypes")
	public List<BaseEntity> pesquisaSimples(Class classeEntidade, String namedQuery) throws Exception ;
	
	/**
	 * 
	 * Recupera quantidade de registros no banco utilizando os argumentos indicados.
	 *
	 * @author: Gustavo Diniz 
	 * @param classeEntidade - classe representativa
	 * @param namedQuery - namedQuery usada para pesquisa
	 * @param arg - map com os argumentos para pesquisa
	 * @return lista de entidades
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@SuppressWarnings("rawtypes")
	public int pesquisaSimplesCount(Class classeEntidade, String namedQuery, Map<String, Object> arg) throws Exception ;
	
	/**
	 * 
	 * Recupera quantidade de registros no banco utilizando os argumentos indicados.
	 *
	 * @author: Gustavo Diniz 
	 * @param classeEntidade - classe representativa
	 * @param namedQuery - namedQuery usada para pesquisa
	 * @param arg - map com os argumentos para pesquisa
	 * @return lista de entidades
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@SuppressWarnings("rawtypes")
	public int pesquisaSimplesCount(Class classeEntidade, String namedQuery,Map<String, Object> arg, String field) throws Exception;
	
	/**
	 * 
	 * Recupera quantidade de registros no banco utilizando os argumentos fixos.
	 *
	 * @param classeEntidade - classe representativa
	 * @param namedQuery - namedQuery usada para pesquisa
	 * @param arg - map com os argumentos para pesquisa
	 * @return lista de entidades
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@SuppressWarnings("rawtypes")
	public int pesquisaArgFixosLimitadoCount(Class classeEntidade, String namedQuery, Map<String, Object> arg) throws Exception ;
	
	/**
	 * 
	 * Recupera a soma de registros no banco utilizando os argumentos indicados.
	 *
	 * @author: Juscelino Oliveira
	 * @param classeEntidade - classe representativa
	 * @param namedQuery - namedQuery usada para pesquisa
	 * @param arg - map com os argumentos para pesquisa
	 * @return lista de entidades
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@SuppressWarnings("rawtypes")
	public double pesquisaSimplesSum(Class classeEntidade, String namedQuery, Map<String, Object> arg) throws Exception ;
	
	/**
	 * 
	 * Recupera lista de entidades do banco com argumentos.
	 *
	 * @author: Gustavo Diniz 
	 * @param classeEntidade - classe representativa
	 * @param namedQuery - namedQuery usada para pesquisa
	 * @param arg - map com os argumentos para pesquisa
	 * @return lista de entidades
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@SuppressWarnings("rawtypes")
	public List<?> pesquisaSimples(Class classeEntidade, String namedQuery, Map<String, Object> arg) throws Exception ;
	
	/**
	 * 
	 * Recupera lista de entidades do banco com argumentos.
	 *
	 * @author: Gustavo Diniz 
	 * @param classeEntidade - classe representativa
	 * @param namedQuery - namedQuery usada para pesquisa
	 * @param arg - map com os argumentos para pesquisa
	 * @param ini - início
	 * @para quant - quantidade a partir do início
	 * @return lista de entidades
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@SuppressWarnings("rawtypes")
	public List<?> pesquisaSimplesLimitado(Class classeEntidade, String namedQuery, Map<String, Object> arg, int ini, int quant) throws Exception;
	
	/**
	 * 
	 * Recupera lista de entidades do banco com argumentos e ordenação.
	 *
	 * @author: Gustavo Diniz 
	 * @param classeEntidade - classe representativa
	 * @param namedQuery - namedQuery usada para pesquisa
	 * @param arg - map com os argumentos para pesquisa
	 * @param ini - início
	 * @para quant - quantidade a partir do início
	 * @return lista de entidades
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@SuppressWarnings("rawtypes")
	public List<?> pesquisaSimplesLimitado(Class classeEntidade, String namedQuery, Map<String, Object> arg, Map<String, String> order, int ini, int quant) throws Exception;
	
	/**
	 * 
	 * Recupera lista de entidades do banco com argumentos fixos.
	 *
	 * @author: Jairo Zanutto
	 * @param classeEntidade - classe representativa
	 * @param namedQuery - namedQuery usada para pesquisa
	 * @param arg - map com os argumentos para pesquisa
	 * @return lista de entidades
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@SuppressWarnings("rawtypes")
	public List<?> pesquisaArgFixos(Class classeEntidade, String namedQuery, Map<String, Object> arg) throws Exception ;
	
	/**
	 * 
	 * Recupera lista de entidades do banco com argumentos fixos.
	 *
	 * @author: Jairo Zanutto
	 * @param classeEntidade - classe representativa
	 * @param namedQuery - namedQuery usada para pesquisa
	 * @param arg - map com os argumentos para pesquisa
	 * @return lista de entidades
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@SuppressWarnings("rawtypes")
	public List<?> pesquisaArgFixosLimitado(Class classeEntidade, String namedQuery, Map<String, Object> arg, int ini, int quant) throws Exception ;
	
	/**
	 * 
	 * Recupera lista de entidades do banco com argumentos fixos.
	 *
	 * @author: Jairo Zanutto
	 * @param classeEntidade - classe representativa
	 * @param namedQuery - namedQuery usada para pesquisa
	 * @param arg - map com os argumentos para pesquisa
	 * @param limit - limite de resgistros
	 * @return lista de entidades
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@SuppressWarnings("rawtypes")
	public List<? extends BaseEntity> pesquisaArgFixos(Class classeEntidade, String namedQuery, Map<String, Object> arg, int limit) throws Exception ;
	
	/**
	 * Recupera lista de entidades do banco com argumentos fixos e ordem variada, que sobrescreverá a ordem da named query.
	 * Se a ordem não for informada, será usada a ordem definida na named query. 
	 * @param classeEntidade
	 * @param namedQuery
	 * @param arg
	 * @param order
	 * @return
	 * @throws Exception
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@SuppressWarnings("rawtypes")
	public List<?> pesquisaArgFixosLimitadoOrdenado(Class classeEntidade,
			String namedQuery, Map<String, Object> arg, List<String> order, int ini, int quant) throws Exception ;
	
	/**
	 * 
	 * Recupera objeto do banco de dados 
	 * atraves da namedQuery "findById".
	 *
	 * @author: Gustavo Diniz
	 * @param classeEntidade - classe para recupera
	 * @param id - id a ser recuperado
	 * @return classe
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@SuppressWarnings("rawtypes")
	public BaseEntity recuperaObjeto(Class classeEntidade, Object id) throws Exception ;
	
	/**
	 * 
	 * Recupera objeto do banco de dados 
	 * atraves da namedQuery "findById".
	 *
	 * @author: Gustavo Diniz
	 * @param classeEntidade - classe para recupera
	 * @param id - id a ser recuperado
	 * @return classe
	 * @throws RemoteException e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@SuppressWarnings("rawtypes")
	public BaseEntity recuperaObjeto(Class classeEntidade, String namedQuery, Object id) throws Exception ;
	
	/**
	 * 
	 * @param argumentos
	 * @param namedQuery
	 * @return
	 * @throws Exception
	 */
	public List<UsuarioEntity> recuperaUsuariosArgFixos(
			Map<String, Object> argumentos, String namedQuery) throws Exception;
	
	/**
	 * 
	 * @param lista
	 * @return
	 * @throws Exception
	 */
	public List<BaseEntity> gravaTodos(List<BaseEntity> lista) throws Exception;
	
	/**
	 * 
	 * @param parameterName
	 * @param idCliente
	 * @return
	 */
	public ParametroEntity getParametroSistema(String parameterName, Long idCliente);

	/**
	 * 
	 * @param logs
	 * @throws Exception
	 */
	public void saveRegisterLogs(List<AcessoEntity> logs) throws Exception;
	
	/**
	 * 
	 * @param classe
	 * @param data
	 * @throws Exception
	 */
	public void updateDataAlteracao(Class classe, Date data, Long id) throws Exception;
}
