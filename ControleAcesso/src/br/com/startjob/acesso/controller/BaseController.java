package br.com.startjob.acesso.controller;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import javax.mail.Session;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.collections.LazyLoadingList;
import br.com.startjob.acesso.modelo.collections.LazyLoadingList.LazyLoadingItemListener;
import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.entity.base.BaseEntity;
import br.com.startjob.acesso.modelo.utils.AppAmbienteUtils;
import br.com.startjob.acesso.utils.CookieUtils;
import br.com.startjob.acesso.utils.ResourceBundleUtils;


/**
 * Classe base para os Controladores/ManagedBeans aplicaÃ§Ã£o.
 * 
 * @author Gustavo Diniz
 * @since 01/02/2013
 *
 */
@SuppressWarnings("serial")
public abstract class BaseController implements Serializable {
	
	/**
	 * Quantidade padrÃ£o de registros que serÃ£o apresentados na tela de pesquisa
	 */
	protected static final Integer QUANTIDADE_REGISTROS_PADRAO = new Integer(10);

	/**
	 * Sufixo das telas de operaÃ§Ãµes
	 */
	protected static final String SUFIXO_TELAS_OPERACAO = "Oper";
	
	/**
	 * Nome do caso de uso que o Controller se refere
	 * Deve ser configurado no Construtor
	 */
	private String funcionalidade;
	
	/**
	 * Entidade padrÃ£o do ManagedBean
	 */
	private BaseEntity entidade;

	/**
	 * Classe da entidade do ManagedBean
	 */
	@SuppressWarnings("rawtypes")
	private Class classEntidade;

	/**
	 * Map com parametros de pesquisa
	 */
	private Map<String, Object> parans;
	
	/**
	 * Map com ordenaÃ§Ã£o da pesquisa
	 */
	private Map<String, String> orders;

	/**
	 * Resultado da busca
	 */
	private List<BaseEntity> result;
	
	/**
	 * NamedQuery padrÃ£o para uso nas pesquisas
	 */
	private String namedQueryPesquisa;

	/**
	 * Quantidade de resgistros que a pesquisa deve devolver
	 */
	private Integer quantPorPagina = QUANTIDADE_REGISTROS_PADRAO;
	
	/**
	 * Exibe mensagem padrÃ£o para o caso de uso.
	 */
	private boolean exibeMensagensPadrao = true;
	
	/**
	 * Lista de filhos para exclusao
	 */
	private Object [] listaFilhosExclusao;
	
	/**
	 * EJB para tarefas bÃ¡sicas
	 */
	@EJB
	protected BaseEJBRemote baseEJB;
	
	/**
	 * Query para ediÃ§Ã£o dos dados
	 */
	protected String queryEdicao;
	
	/**
	 * Mail session.
	 */
	@Resource(mappedName="java:/mail/suporte")
	protected Session mailSession;
	
	private Boolean mobile = Boolean.FALSE;
	
	private UseCase confCasoUso;

	protected String idSession;
	
	protected Long tempId;
	
	protected LazyLoadingItemListener listener;
	
	/**
	 * 
	 * Construtor para configuraÃ§Ã£o do Caso de Uso.
	 * Os dados fornecidos para esse construtor vÃ£o ser usados
	 * para processamento de um fluxo padrÃ£o de aÃ§Ãµes como CRUD's.
	 *
	 * @author: Gustavo Diniz
	 * 
	 */
	@SuppressWarnings("unchecked")
	public BaseController() {
		
		
		try {
			
			//System.out.println(this.getClass().getName());
			
			/*
			 * Configura "Caso de Uso" pela anotaÃ§Ã£o
			 */
			confCasoUso = this.getClass().getAnnotation(
					UseCase.class);
			if (confCasoUso != null) {
				// seta atritutos necessÃ¡rios
				this.classEntidade = confCasoUso.classEntidade();
				this.quantPorPagina = confCasoUso.quantPorPagina();
				this.namedQueryPesquisa = confCasoUso.namedQueryPesquisa();
				this.funcionalidade = confCasoUso.funcionalidade();
				this.urlNovoRegistro = confCasoUso.urlNovoRegistro();
				this.queryEdicao = confCasoUso.queryEdicao();
				
				//inicializa dados
				this.entidade = (BaseEntity) classEntidade.newInstance();
				this.parans = new HashMap<String, Object>();
				
			} else {
				// nÃ£o deixa entidade nula
				this.entidade = new BaseEntity();
			}
		
			if(getSessionAtrribute("parans_" + funcionalidade) != null
					&& confCasoUso.saveParans()){
				setParans((Map<String, Object>) 
						getSessionAtrribute("parans_" + funcionalidade));
				
			}else{
				setParans(new HashMap<String, Object>());
			}
			
			String [] uri = getRequest().getRequestURI().split("/");
			
			String qtdCookie = CookieUtils.getCookiePropetie(
					"qtdPagina_"+uri[uri.length-1], getFacesContext());
			
			if(qtdCookie != null)
				quantPorPagina = Integer.valueOf(qtdCookie);
			
			setSessioAtrribute("mostraOper", true);
			
			verificaTipoAcesso();
			
			montaTourAjuda(this.funcionalidade);

		} catch (Exception e) {
			mensagemFatal("", "#Erro na configuraÃ§Ã£o do ManagedBean "
					+ this.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void init() {
		
		try {
			String id = getRequest().getParameter("id");
			
			if(id != null) {
				//recupera registro para ediÃ§Ã£o usando o id
				if(queryEdicao != null && !"".equals(queryEdicao)){
					Map<String, Object> args = new HashMap<String, Object>();
					args.put("ID", Long.valueOf(id));
					List<BaseEntity> registros = (List<BaseEntity>) baseEJB
							.pesquisaArgFixos(getClassEntidade(), queryEdicao, args);
					
					if(registros != null && !registros.isEmpty()) {
						registros.get(0).setExistente(true);
						setEntidade(registros.get(0));
					}
				}else{
					BaseEntity registro = baseEJB.recuperaObjeto(getClassEntidade(), Long.valueOf(id));
					if(registro != null)
						setEntidade(registro);
				}
			}
		
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	
	/**
	 * Monta tour de ajuda para a tela especifica.
	 * 
	 * Deve ser implementado.
	 * 
	 */
	protected void montaTourAjuda(String funcionalidade) {
		
		
	}
	
	/**
	 * Salva entidade presente no atributo "entidade" e executa outras tarefas de salvamento. 
	 * 
	 * @author: Gustavo Diniz 
	 * @return navegaÃ§Ã£o
	 */
	public String salvar() {

		String retornoStr = "ok";
		
		try {
			
 			if (entidade != null) {
				
 				BaseEntity entity = (BaseEntity) entidade;
				Object[] retorno = null;

				/*
				 * 
				 * salva entidade
				 */
				if (!entity.getExistente()) {
					// insere novo
					retorno = baseEJB.gravaObjeto(entity);
				} else {
					// altera existente
					entity.setDataAlteracao(new Date());
					if(listaFilhosExclusao != null){
						retorno = baseEJB.alteraObjeto(entity, listaFilhosExclusao);
					}else{
						retorno = baseEJB.alteraObjeto(entity);
					}
					
					listaFilhosExclusao = null;
				}

				if (retorno != null && retorno.length == 2) {

					entidade = (BaseEntity) retorno[0];
					String msg = (String) retorno[1];
					
					try {
						Method getId = entidade.getClass().getMethod("getId", new Class[] {});
						tempId = (Long) getId.invoke(entidade, new Object[] {});
					}catch (Exception e) {
					}
					
					/*
					 * 
					 * emite mensagem recebida
					 */
					if (isExibeMensagensPadrao()) {
						if (msg.indexOf("sucesso") != -1) {
							// se mensagem for de sucesso
							mensagemInfo("", msg);
						} else {
							// senÃ£o Ã© de erro
							mensagemFatal("", msg);
							retornoStr = "e";
						}
					} else {
						setExibeMensagensPadrao(true);
					}

					// atualiza lista de resultados se ela existir
					if (result != null && !result.isEmpty()) {
						buscar();
					}

				} else {
					mensagemFatal("",
							"msg.salvar.retorno.diferente.do.esperado");
					retornoStr = "e";
				}

			}
		} catch (Throwable e) {
			e.printStackTrace();
			
			if(e.getCause() != null 
					&& e.getCause().getClass().getName().contains("MySQLIntegrityConstraint")){
				mensagemFatal("", "msg.erro.delete.violacao.constraint.gravacao");
			} else if(e.getCause() != null && e.getCause().getCause() != null 
					&& e.getCause().getCause().getClass().getName().contains("MySQLIntegrityConstraint")){
				mensagemFatal("", "msg.erro.delete.violacao.constraint.gravacao");
			} else if(e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause().getCause() != null 
					&& e.getCause().getCause().getCause().getClass().getName().contains("MySQLIntegrityConstraint")){
				mensagemFatal("", "msg.erro.delete.violacao.constraint.gravacao");
			} else if(e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause().getCause() != null
					&& e.getCause().getCause().getCause().getCause() != null 
					&& e.getCause().getCause().getCause().getCause().getClass().getName().contains("MySQLIntegrityConstraint")){
				
				if(e.getCause().getCause().getCause().getCause().getMessage().contains("LOGIN_UNIQUE")) 
					mensagemFatal("", "msg.erro.delete.violacao.constraint.gravacao.login");
				else
					mensagemFatal("", "msg.erro.delete.violacao.constraint.gravacao");
			} else if(e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause().getCause() != null
					&& e.getCause().getCause().getCause().getCause() != null && e.getCause().getCause().getCause().getCause().getCause() != null 
					&& e.getCause().getCause().getCause().getCause().getCause().getClass().getName().contains("MySQLIntegrityConstraint")){
				mensagemFatal("", "msg.erro.delete.violacao.constraint.gravacao");
			}else if(e.getMessage() != null 
					&& (e.getMessage().contains("erro.processamento")
							|| e.getMessage().contains("erro.lock.otimista"))){
				mensagemFatal("", e.getMessage());
			}else{
				mensagemFatal("", "#" + e.getMessage());
			}
			
			retornoStr = "e";
			
		}
		
		//roda carbage collector para nÃ£o deixar lixo para traz
		

		return retornoStr;

	}

	/**
	 * 
	 * Preenche lista "result" com a classe definida em "classeEntidade".
	 * Busca com paginaÃ§Ã£o e sem paginaÃ§Ã£o.
	 *
	 * @author: Gustavo Diniz
	 * @return navegaÃ§Ã£o
	 */
	public String buscar() {
		
		//salva parametros na sessÃ£o
		parametrosSessao();
		
		if(confCasoUso.lazyLoad()){
			//realiza a busca paginada
			buscarPaginado();
		}else{
			//realiza a busca normal
			buscaCompleto();
		}
		
		return "";
	}
	
//	public void ordena(ActionEvent event){
//		CommandSortHeader sort = (CommandSortHeader)event.getSource();
//		
//		//identifica campo e ordem
//		if(orders == null)
//			orders = new LinkedHashMap<String, String>();
//		orders.put(sort.getColumnName(), 
//				"asc".equals(orders.get(sort.getColumnName())) ? "desc" : "asc" );
//		//reorganiza para nova ordenaÃ§Ã£o
//		List<String> keys = new ArrayList<String>();
//		for (String key : orders.keySet()) {
//			if(!key.equals(sort.getColumnName()))
//				keys.add(key);
//		}
//		for (String key : keys)
//			orders.remove(key);
//		//System.out.println(orders);
//		
//		//realiza nova busca
//		buscar();
//		
//	}

	/**
	 * Organiza parametros na sessÃ£o do usuÃ¡rio.
	 */
	protected void parametrosSessao() {
		if(getParans() != null 
				&& !getParans().isEmpty() 
				&& confCasoUso.saveParans()){
			
			HashMap<String, Object> newParans = new HashMap<String, Object>();
			for (String key : getParans().keySet()) {
				newParans.put(key, getParans().get(key));
			}
			
			setSessioAtrribute("parans_"+confCasoUso.funcionalidade()
					, newParans);
			
		}else{
			setSessioAtrribute("parans_"+confCasoUso.funcionalidade(), new HashMap<String, Object>());
			setSessioAtrribute("parans_vencimento_"+confCasoUso.funcionalidade(), null);
		}
	}

	/**
	 * Busca lista completa.
	 */
	@SuppressWarnings("unchecked")
	protected void buscaCompleto() {
		try {

			//limpa lista de resultados
			result = new ArrayList<BaseEntity>();
			

			if (parans != null && parans.isEmpty()) {
				
				// executa pesquisa sem argumentos
				result = baseEJB.pesquisaSimples(classEntidade,
						namedQueryPesquisa);

			} else {
				
				preparaArgumentos();

				// executa pesquisa com argumentos
				result = (List<BaseEntity>) baseEJB.pesquisaSimples(classEntidade,
						namedQueryPesquisa, parans);

			}
		} catch (Exception e) {
			e.printStackTrace();
			mensagemFatal("", "#" + e.getMessage());
		}
	}
	
	/**
	 * 
	 * Preenche lista "result" com a classe definida em "classeEntidade".
	 * Busca com paginaÃ§Ã£o e sem paginaÃ§Ã£o.
	 *
	 * @author: Gustavo Diniz
	 * @return navegaÃ§Ã£o
	 */
	@SuppressWarnings("unchecked")
	public String buscarPaginado() {

		try {
			
			//fazer o count
			int count = baseEJB.pesquisaSimplesCount(classEntidade, namedQueryPesquisa, parans);
			
			//limpa lista de resultados
			result = new LazyLoadingList(baseEJB, 
					classEntidade, namedQueryPesquisa, 
					getParans(), getOrders(), 
					quantPorPagina, count, listener);
			
	
		} catch (Exception e) {
			e.printStackTrace();
			mensagemFatal("", "#" + e.getMessage());
		}
		
		return "";
	}
	
	/**
	 * Prepara argumentos para pesquisa
	 */
	private void preparaArgumentos() {
		if(parans.containsKey("id") 
				&& parans.get("id") != null
				&& (parans.get("id") instanceof BigDecimal || parans.get("id") instanceof BigInteger)) {
			parans.put("id", Long.valueOf(parans.get("id").toString()));
		}
		
		
	}

	/**
	 * 
	 * Verifica se a tela de operaÃ§Ãµes serÃ¡ exibida no modo de ediÃ§Ã£o
	 * ou no modo de adiÃ§Ã£o.
	 *
	 * @author: Gustavo Diniz
	 * @param event evento da tela
	 */
	public void edita(ActionEvent event) {

		try {

			Object id = event.getComponent().getAttributes().get("idLinha");

			if (id == null) {
				// novo registro
				entidade = (BaseEntity) classEntidade.newInstance();
				entidade.setExistente(Boolean.FALSE);
			} else {
				// ediÃ§Ã£o do registro jÃ¡ existente
				entidade = (BaseEntity) baseEJB.recuperaObjeto(
						classEntidade, id);
				entidade.setExistente(true);
			}

			//exibe tela
			getRequest().getSession().setAttribute("mostraOper", Boolean.TRUE);

		} catch (Exception e) {
			mensagemFatal("", "#" + e.getMessage());
		}
		return;
	}

	/**
	 * 
	 * DescriÃ§Ã£o: Exclui objeto presente no atributo "entidade"
	 * Projeto/RequisiÃ§Ã£o: MESFlorestal/Arquitetura
	 *
	 * @author: Gustavo Diniz
	 * Fornecedor: Stefanini IT Solutions
	 * Data alteraÃ§Ã£o: 28/02/2012
	 * @param id - Id da entidade
	 * @return navegaÃ§Ã£o
	 */
	public String excluir(Object id) {

		try {
			if (id != null) {
				// ediÃ§Ã£o do registro jÃ¡ existente
				entidade = baseEJB.recuperaObjeto(classEntidade, id);
				entidade.setExistente(true);
			}
			String msg = "";
			
			if(confCasoUso.logicalRemove()) {
				
				//exclusÃ£o lÃ³gica
				entidade.setRemovido(true);
				entidade.setDataRemovido(new Date());
				
				baseEJB.alteraObjeto(entidade);
				
				msg = "msg.generica.objeto.excluido.sucesso";
			}else {

				// se encontrou o objeto, exclui
				if (entidade != null) {
	
					BaseEntity entity = (BaseEntity) entidade;
	
					/*
					 * exclui entidade
					 */
					msg = baseEJB.excluiObjeto(entity);

				}
			
			}
			atualizaListaResultados(entidade, true);
			
			/*
			 * emite mensagem recebida
			 */
			if (msg.indexOf("sucesso") != -1) {
				// se mensagem for de sucesso
				mensagemInfo("", msg);
			} else {
				// senÃ£o Ã© de erro
				mensagemFatal("", msg);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
			if(e.getCause() != null && e.getCause()
					.getClass().getName().contains("MySQLIntegrityConstraint")){
				mensagemFatal("", "msg.erro.delete.violacao.constraint");
				return "violacao.constraint";
			} else if(e.getCause().getCause() != null && e.getCause().getCause()
					.getClass().getName().contains("MySQLIntegrityConstraint")){
				mensagemFatal("", "msg.erro.delete.violacao.constraint");
				return "violacao.constraint";
			} else if(e.getCause().getCause().getCause() != null && e.getCause().getCause().getCause()
					.getClass().getName().contains("MySQLIntegrityConstraint")){
				mensagemFatal("", "msg.erro.delete.violacao.constraint");
				return "violacao.constraint";
			} else if(e.getCause().getCause().getCause().getCause() != null && e.getCause().getCause().getCause().getCause() 
					.getClass().getName().contains("MySQLIntegrityConstraint")){
				mensagemFatal("", "msg.erro.delete.violacao.constraint");
				return "violacao.constraint";
			} else if(e.getCause().getCause().getCause().getCause().getCause() != null && e.getCause().getCause().getCause().getCause().getCause()
					.getClass().getName().contains("MySQLIntegrityConstraint")){
				mensagemFatal("", "msg.erro.delete.violacao.constraint");
				return "violacao.constraint";
			}
			
			mensagemFatal("", "#" + e.getMessage());
		}
		
		
		return "";
	}
	

	/**
	 * Atualiza lista de resultados confirme item enviado
	 *
	 * @author: Gustavo Diniz 
	 * @param exclui - se "true" retira item da lista
	 * @param atualiza - objeto para atualizaÃ§Ã£o
	 */
	@SuppressWarnings("rawtypes")
	protected void atualizaListaResultados(BaseEntity atualiza, boolean exclui) {

		if(confCasoUso != null && confCasoUso.lazyLoad()){
			//atualiza buscando novamente os dados
			buscar();
		}else{
			//atualiza retirando o dado especÃ­fico da lista
			if (result != null && !result.isEmpty()) {
				for (Iterator iter = result.iterator(); iter.hasNext();) {
					BaseEntity entity = (BaseEntity) iter.next();
					if (entity.equals(atualiza) && exclui) {
						iter.remove();
						break;
					} else if (entity.equals(atualiza)) {
						entity = atualiza;
					}
				}
			}
		}

	}

	/**
	 * 
	 * DescriÃ§Ã£o: Limpa tela por completo (o comportamento de limpa Ã© novo Ã©
	 * 			  considerado o mesmo)
	 * Projeto/RequisiÃ§Ã£o: MESFlorestal/Arquitetura
	 *
	 * @author: Gustavo Diniz
	 * Fornecedor: Stefanini IT Solutions
	 * Data alteraÃ§Ã£o: 28/02/2012
	 * @return navegaÃ§Ã£o
	 */
	public String limpar() {

		try {

			if (getRequest().getRequestURI().indexOf(SUFIXO_TELAS_OPERACAO) == -1) {
				//limpa telas de pesquisa
				result = new ArrayList<BaseEntity>();
				parans = new HashMap<String, Object>();
				setSessioAtrribute("parans_"+funcionalidade, null);
			} else {
				//limpa telas de operaÃ§Ã£o
				entidade = (BaseEntity) classEntidade.newInstance();
			}

		} catch (Exception e) {
			e.printStackTrace();
			mensagemFatal("", "#" + e.getMessage());
		}

		return "";
	}

	/**
	 * 
	 * Imprimi tela em PDF
	 *
	 * @author: Gustavo Diniz
	 * @return navegaÃ§Ã£o
	 * @throws Exception - e 
	 */
	public String imprimirPdf() throws Exception {

		mensagemErro("", "msg.pdf.erro");

		return "";
	}
	
	public void imprimir(){
		
//		JavaScriptRunner.runScript(
//				FacesContext.getCurrentInstance(),
//				"window.print();");
		
	}
	
	/**
	 * 
	 * Imprimi tela em Excel
	 *
	 * @author: Gustavo Diniz
	 * @return navegaÃ§Ã£o
	 * @throws IOException - e 
	 */
	public String imprimirExcel() throws IOException {

		mensagemFatal("", "msg.excel.erro");

		mensagemFatal("", "msg.excel.erro");

		mensagemFatal("", "msg.excel.erro");

		return "";
	}

	

	
	
	/**
	 * 
	 * Retorna request da aplicaÃ§Ã£o
	 *
	 * @author: Gustavo Diniz
	 * @return request
	 */
	public HttpServletRequest getRequest() {
		
		return (HttpServletRequest) FacesContext
			.getCurrentInstance()
			.getExternalContext()
			.getRequest();
	}
	
	/**
	 * 
	 * Retorna request da aplicaÃ§Ã£o
	 *
	 * @author: Gustavo Diniz
	 * @return request
	 */
	protected HttpServletResponse getResponse() {
		return (HttpServletResponse) FacesContext
			.getCurrentInstance()
			.getExternalContext()
			.getResponse();
	}
	
	/**
	 * 
	 * Retorna facesContext atual
	 *
	 * @author: Gustavo Diniz
	 * @return faces
	 */
	public FacesContext getFacesContext() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return facesContext;
	}
	
	/**
	 * 
	 * Redireciona para a url desejada.
	 *
	 * @author: Gustavo Diniz
	 * @param url - url para redirecionamento
	 */
	public void redirect(String url) {
		try {
			
			String appName =  "/"+AppAmbienteUtils.getConfig(
					AppAmbienteUtils.CONFIG_AMBIENTE_NOME_APP);
			
			FacesContext.getCurrentInstance().getExternalContext().redirect(
					appName + url);
			FacesContext.getCurrentInstance().responseComplete();
			
		} catch (IOException e) {
			throw new FacesException(
				"Cannot redirect to " + url + " due to IO exception.",
				e);
		}
	}
	
	/**
	 * 
	 * LanÃ§a mensagem de erro fatal do JSF
	 *
	 * @author: Gustavo Diniz
	 * @param campo - campo
	 * @param key - chave
	 */
	public void mensagemFatal(String campo, String key) {
		mensagem(campo, key, null, FacesMessage.SEVERITY_FATAL);
	}

	/**
	 * 
	 * DescriÃ§Ã£o: LanÃ§a mensagem de informacao do JSF
	 *
	 * @author: Gustavo Diniz
	 * @param campo - campo
	 * @param key - chave
	 */
	public void mensagemInfo(String campo, String key) {
		mensagem(campo, key, null, FacesMessage.SEVERITY_INFO);
	}
	
	/**
	 * 
	 * DescriÃ§Ã£o: LanÃ§a mensagem de erro do JSF
	 *
	 * @author: Gustavo Diniz
	 * @param campo - campo
	 * @param key - chave
	 */
	public void mensagemErro(String campo, String key) {
		mensagem(campo, key, null, FacesMessage.SEVERITY_ERROR);
	}
	
	/**
	 * 
	 * DescriÃ§Ã£o: LanÃ§a mensagem de erro do JSF
	 *
	 * @author: Gustavo Diniz
	 * @param key - chave
	 * @param params - parametros
	 */
	public void mensagemErro(String key, Object[] params) {
		mensagem(null, key, params, FacesMessage.SEVERITY_ERROR);
	}
	
	/**
	 * 
	 * DescriÃ§Ã£o: LanÃ§a mensagem de erro do JSF
	 *
	 * @author: Gustavo Diniz
	 * @param key - chave
	 * @param params - parametros
	 */
	public void mensagemFatal(String key, Object[] params) {
		mensagem(null, key, params, FacesMessage.SEVERITY_FATAL);
	}
	
	public void mensagemAviso(String campo, String key) {
		mensagem(campo, key, null, FacesMessage.SEVERITY_WARN);
	}

	/**
	 * 
	 * DescriÃ§Ã£o: LanÃ§a mensagem
	 *
	 * @author: Gustavo Diniz
	 * @param campo - campo
	 * @param key - chave
	 * @param params - parametros
	 * @param severity - severity
	 */
	protected void mensagem(
		String campo,
		String key,
		Object[] params,
		Severity severity) {
		String mensagem = null;

		if (params == null) {
			mensagem =
				ResourceBundleUtils.getInstance().recuperaChave(
					key,
					getFacesContext());
		} else {
			mensagem =
				ResourceBundleUtils.getInstance().recuperaChave(
					key,
					getFacesContext(),
					params);
		}

		if (mensagem == null) {
			mensagem = key;
		}

		FacesContext.getCurrentInstance().addMessage(campo,
				new FacesMessage( severity, "*", mensagem));
	}
	
	public void clearMessages() {
		FacesContext context = FacesContext.getCurrentInstance();
		Iterator<FacesMessage> it = context.getMessages();
		while ( it.hasNext() ) {
		    it.next();
		    it.remove();
		}
		
	}

	/**
	 * 
	 * DescriÃ§Ã£o: Retorna parametro do facesContext
	 *
	 * @author: Gustavo Diniz
	 * @param fc - faces context atual
	 * @param param - parametro
	 * @return valor do parametro
	 */
	public Object getParam(FacesContext fc, String param) {

		Map<String, String> params = fc.getExternalContext().getRequestParameterMap();

		return params.get(param);

	}

	/**
	 * 
	 * 
	 * 
	 * @author: Gustavo Diniz
	 * @param entitys  - Lista de objetos a ser convertida
	 * @param nomeValue - nome do atributo que serÃ¡ o value
	 * @param nomeLabel - nome do atributo que serÃ¡ o label
	 * @return List - List<SelectItem>
	 */
	@SuppressWarnings("rawtypes")
	protected List<SelectItem> converteSelectItens(List<Object> entitys, String nomeValue,
			String nomeLabel) {

		List<SelectItem> select = new ArrayList<SelectItem>();

		for (Iterator iter = entitys.iterator(); iter.hasNext();) {
			Object element = (Object) iter.next();

			try {

				Method getValue = element.getClass().getMethod(
						"get" + StringUtils.capitalize(nomeValue),
						new Class[] {});
				Method getLabel = element.getClass().getMethod(
						"get" + StringUtils.capitalize(nomeLabel),
						new Class[] {});

				Object value = getValue.invoke(element, new Object[] {});
				Object label = getLabel.invoke(element, new Object[] {});

				SelectItem item = new SelectItem();
				item.setLabel(label.toString());
				item.setValue(value);

				select.add(item);

			} catch (Exception e) {
				mensagemFatal("", "#" + e.getMessage());
			}

		}

		return select;

	}
	
	/**
	 * 
	 * 
	 * @author: Gustavo Diniz
	 * @param entitys - Lista de objetos a ser convertida
	 * @param nomeValue - nome do atributo que serÃ¡ o value
	 * @param nomeLabel - array nome do atributo que serÃ¡ o label
	 * @return List - List<SelectItem>
	 */
	@SuppressWarnings("rawtypes")
	protected List<SelectItem> converteSelectItens(List<Object> entitys, String nomeValue,
			String [] nomeLabel) {

		List<SelectItem> select = new ArrayList<SelectItem>();

		for (Iterator iter = entitys.iterator(); iter.hasNext();) {
			Object element = (Object) iter.next();

			try {

				Method getValue = element.getClass().getMethod(
						"get" + StringUtils.capitalize(nomeValue),
						new Class[] {});
				
				Object value = getValue.invoke(element, new Object[] {});
				
				String nomesLabel = "";
				String traco = "";
				for (int i = 0; i < nomeLabel.length; i++) {
					Method getLabel = element.getClass().getMethod(
							"get" + StringUtils.capitalize(nomeLabel[i]),
							new Class[] {});
					Object label = getLabel.invoke(element, new Object[] {});
					nomesLabel += traco + label.toString();
					traco = " - ";
				}

				SelectItem item = new SelectItem();
				item.setLabel(nomesLabel);
				item.setValue(value);

				select.add(item);

			} catch (Exception e) {
				mensagemFatal("", "#" + e.getMessage());
			}

		}

		return select;

	}
	
	/**
	 * 
	 * DescriÃ§Ã£o: Seta atributo na sessÃ£o Projeto/RequisiÃ§Ã£o:
	 * 
	 * @author: Gustavo Diniz 
	 * @param nome - nome do atributo
	 * @param o - objeto
	 */
	protected void setSessioAtrribute(String nome, Object o) {
		getRequest().getSession().setAttribute(nome, o);
	}

	/**
	 * 
	 * DescriÃ§Ã£o: Retorna atributo na sessÃ£o
	 *
	 * @author: Gustavo Diniz
	 * @param nome - nome do atributo
	 * @return valor
	 */
	protected Object getSessionAtrribute(String nome) {
		return getRequest().getSession().getAttribute(nome);
	}
	
	/**
	 * DescriÃ§Ã£o: Seta atributo no request
	 *
	 * @author: Gustavo Diniz
	 * @param nome - nome do atributo
	 * @param valor - valor do atributo
	 */
	protected void setAtributoResquest(String nome, Object valor) {

		ServletRequest req =
			(ServletRequest) FacesContext
				.getCurrentInstance()
				.getExternalContext()
				.getRequest();

		req.setAttribute(nome, valor);

	}
	
	protected String urlNovoRegistro;
	
	/**
	 * Envia para a tela de novo registro
	 * @throws 
	 */
	public void novo() throws Exception{
		//createLogUserAction("MÃ©todo: BaseController.novo /");
		
		if(urlNovoRegistro != null 
				&& !"".equals(urlNovoRegistro)){
			this.entidade = (BaseEntity) classEntidade.newInstance();
			
			this.entidade = preparaEntidadeNovo(this.entidade);
			
			redirect(urlNovoRegistro);
		}
		
	}
	
	/**
	 * template method, deve ser implementado por demanda
	 */
	protected BaseEntity preparaEntidadeNovo(BaseEntity entidade)  throws Exception {
		
		return entidade;
	}

	/**
	 * Envia para a tela de novo registro
	 * @throws 
	 */
	@SuppressWarnings("unchecked")
	public void editar(Object id) throws Exception{
		
		if(urlNovoRegistro != null 
				&& !"".equals(urlNovoRegistro)
				&& id != null){
			
			if(urlNovoRegistro.contains("?")) {
				redirect(urlNovoRegistro+"&id="+id);
			}else {
				redirect(urlNovoRegistro+"?id="+id);
			}
			
		}
		
	}
	
	/**
	 * Retorna usuÃ¡rio logado 
	 * 
	 * @return
	 */
	protected UsuarioEntity getUsuarioLogado(){
		
		UsuarioEntity user = (UsuarioEntity)getSessionAtrribute(BaseConstant.LOGIN.USER_ENTITY);
		
		if(user != null)
			return user;
		
		return new UsuarioEntity();
	}

	/**
	 * Retorna URL que a aplicaÃ§Ã£o esta rodando
	 * 
	 * @return
	 */
	public String getURLApp(){
		
		StringBuffer urlBuffer = getRequest().getRequestURL();
		String [] slip = urlBuffer.toString().split("/");
		
		return slip[0]+"/"+slip[1]+"/"+slip[2] + "/"  + BaseConstant.URL_APLICACAO;
	}
	
	/**
	 * Exibe mensagens na tela
	 * @param evt
	 * @throws AbortProcessingException
	 */
	public void exibeMensagens(ComponentSystemEvent evt)throws AbortProcessingException{  
		
		
		
	}
	
	public void verificaTipoAcesso() {
		
		//verifica se Ã© mobile ou desktop
		if(getRequest() != null && getRequest().getHeaderNames() != null){
			if(getRequest().getHeader("user-agent").toUpperCase().contains("ANDROID") 
					|| getRequest().getHeader("user-agent").toUpperCase().contains("IOS")
					|| getRequest().getHeader("user-agent").toUpperCase().contains("IPHONE")){
				mobile = Boolean.TRUE;
			}
		}
		
	}
	
	public ResourceBundle getResourceBundle(){
		String bundle = FacesContext.getCurrentInstance().getApplication().getMessageBundle();
		String path = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_MANAGEMENT_PATH);
		if(path != null && !"".equals(path)){
			path = path.replace("/flavours/", "")+"_";
			bundle = bundle.replace("message", path+"message");
		}
		ResourceBundle resource = ResourceBundle.getBundle(bundle, new Locale("pt","BR"));
		return resource;
	}
	
	public void download(byte [] file, String name){
		setSessioAtrribute(BaseConstant.EXPORT.BYTES,
				file);

		setSessioAtrribute(
				BaseConstant.EXPORT.FILE_NAME,
				name);

//		JavaScriptRunner.runScript(
//				FacesContext.getCurrentInstance(),
//				"download('"+BaseConstant.URL_APLICACAO+"/export');");
	}
	
	public static String getDeviceType(HttpServletRequest request){
		
		String userAgent = request.getHeader("User-Agent");
		//System.out.println("USER-AGENT:" + userAgent);
		
		String device = BaseConstant.WEB;
		if(userAgent.contains("Apache-HttpClient/UNAVAILABLE"))
			device = BaseConstant.ANDROID;
		else if(userAgent.contains("Pro-Treino-Mobile") 
				|| (userAgent.contains("Pro-Treino") && userAgent.contains("Mobile")))
			device = BaseConstant.IPHONE;
		else{
			device = userAgent;
		}
		
		return device;
		
	}
	
//	public void createLogUserActionListenerMenuItem(ActionEvent event) {
//		try{
//			createLogUserAction("MenuItem: " + ((MenuItem)event.getSource()).getId());
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			MenuItem item = (MenuItem)event.getSource();
//			if("_blank".equals(item.getTarget()))
//				JavaScriptRunner.runScript(
//						FacesContext.getCurrentInstance(),
//						"window.open('"+item.getLink()+"','_blank');");
//			else
//				redirect(item.getLink());
//		}
//	}
	
	
	protected void excluiEntidade(Class<?> classe, Long id) {
		try {
			BaseEntity entidade = baseEJB.recuperaObjeto(classe, id);
			if (entidade != null)
				baseEJB.excluiObjeto(entidade);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected boolean isNullOrEmpty(String string) {
		if (string == null)
			return true;
		if (string.isEmpty())
			return true;
		return false;
	}
	
	
	protected boolean isNullOrZero(Number number) {
		if (number == null)
			return true;
		if (number.doubleValue() == 0d)
			return true;
		return false;
	}
	
	
	protected <T> Object inicializaBean(String beanName, Class<T> clazz){
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		return application.evaluateExpressionGet(context, "#{" + beanName + "}", clazz);
	}
	
	
	public String getCurrencySymbol(){
		try {
			String symbol = (String) getSessionAtrribute("currencySymbol");
			if (symbol != null && !"".equals(symbol))
				return symbol;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "R$";
	}
	
	public NumberFormat getCurrencyFormatter(){
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		try {
			formatter.setMaximumFractionDigits(2);
			formatter.setMinimumFractionDigits(2);
			DecimalFormatSymbols dfs = new DecimalFormatSymbols();
			dfs.setCurrencySymbol(getCurrencySymbol());
			dfs.setGroupingSeparator('.');
			dfs.setMonetaryDecimalSeparator(',');
			((DecimalFormat) formatter).setDecimalFormatSymbols(dfs);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return formatter;
	}
	
	public String getFuncionalidade() {
		return funcionalidade;
	}
	
	public void setFuncionalidade(String funcionalidade) {
		this.funcionalidade = funcionalidade;
	}

	public BaseEntity getEntidade() {
		return entidade;
	}

	public void setEntidade(BaseEntity entidade) {
		this.entidade = entidade;
	}

	@SuppressWarnings("rawtypes")
	public Class getClassEntidade() {
		return classEntidade;
	}

	@SuppressWarnings("rawtypes")
	public void setClassEntidade(Class classEntidade) {
		this.classEntidade = classEntidade;
	}

	public Map<String, Object> getParans() {
		return parans;
	}

	public void setParans(Map<String, Object> parans) {
		this.parans = parans;
	}

	public List<BaseEntity> getResult() {
		return result;
	}

	public void setResult(List<BaseEntity> result) {
		this.result = result;
	}

	public String getNamedQueryPesquisa() {
		return namedQueryPesquisa;
	}

	public void setNamedQueryPesquisa(String namedQueryPesquisa) {
		this.namedQueryPesquisa = namedQueryPesquisa;
	}

	public Integer getQuantPorPagina() {
		return quantPorPagina;
	}

	public void setQuantPorPagina(Integer quantPorPagina) {
		this.quantPorPagina = quantPorPagina;
	}

	public boolean isExibeMensagensPadrao() {
		return exibeMensagensPadrao;
	}

	public void setExibeMensagensPadrao(boolean exibeMensagensPadrao) {
		this.exibeMensagensPadrao = exibeMensagensPadrao;
	}

	public Object[] getListaFilhosExclusao() {
		return listaFilhosExclusao;
	}

	public void setListaFilhosExclusao(Object[] listaFilhosExclusao) {
		this.listaFilhosExclusao = listaFilhosExclusao;
	}

	public String getUrlNovoRegistro() {
		return urlNovoRegistro;
	}

	public void setUrlNovoRegistro(String urlNovoRegistro) {
		this.urlNovoRegistro = urlNovoRegistro;
	}


	public Boolean getMobile() {
		
		//verifica se Ã© mobile ou desktop
		if(getRequest() != null && getRequest().getHeaderNames() != null){
			if(getRequest().getHeader("user-agent").toUpperCase().contains("ANDROID") 
					|| getRequest().getHeader("user-agent").toUpperCase().contains("IOS")
					|| getRequest().getHeader("user-agent").toUpperCase().contains("IPHONE")){
				mobile = Boolean.TRUE;
			}else {
				mobile = Boolean.FALSE;
			}
		}
		
		return mobile;
	}

	public void setMobile(Boolean mobile) {
		this.mobile = mobile;
	}

	public UseCase getConfCasoUso() {
		return confCasoUso;
	}

	public String getIdSession() {
		return idSession;
	}

	public void setIdSession(String idSession) {
		this.idSession = idSession;
	}

	public Map<String, String> getOrders() {
		return orders;
	}

	public void setOrders(Map<String, String> orders) {
		this.orders = orders;
	}

	public Boolean getFiltroFlag(String sessionAttributeName) {
		Object obj = getSessionAtrribute(sessionAttributeName);
		if (Boolean.TRUE.equals((Boolean) obj))
			return true;
		return false;
	}

	public void toggleFiltroFlag(String sessionAttributeName) {
		Object obj = getSessionAtrribute(sessionAttributeName);
		setSessioAtrribute(sessionAttributeName, Boolean.TRUE.equals((Boolean) obj) ? false : true);
	}

	
}