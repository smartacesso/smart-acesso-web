package br.com.startjob.acesso.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.MenuModel;
import org.primefaces.util.SerializableFunction;

import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.enumeration.PerfilAcesso;
import br.com.startjob.acesso.modelo.utils.AppAmbienteUtils;
import br.com.startjob.acesso.utils.CookieUtils;
import br.com.startjob.acesso.utils.ResourceBundleUtils;

/**
 * Controle responsável pela montagem do menu
 *
 * @author: Gustavo Diniz
 * @since 01/02/2012
 * 
 *        Alteração feita para modificação do Daniel
 */
@SuppressWarnings("serial")
@Named("menuController")
@SessionScoped
public class MenuController extends BaseController {

	protected MenuModel menu;
	protected MenuModel menuUsuario;

	protected UsuarioEntity usuarioLogado;
	private Boolean isProduction;
	protected List<String> versoes;
	private String currencySymbol = "R$";

	private String pathCode;
	private String path;

	private String urlTwitter;
	private String urlFacebook;
	private String urlGplus;
	private String urlInstagram;
	private String urlBlog;
	private String urlYoutube;
	private String urlMain;
	private String urlSite;
	private String appName;
	private String emailSuporte;

	private ResourceBundleUtils resource;

	@PostConstruct
	@Override
	public void init() {

		// recupera configurações do ambiente
		// para academias gestoras
		configuraAmbiente();

		montaNotasVersao();

		isProduction = AppAmbienteUtils.isProdution();

	}

	private boolean painelVisivel = false;

	public boolean isPainelVisivel() {
		return painelVisivel;
	}

	public void togglePainel() {
		painelVisivel = !painelVisivel;
	}

	protected void configuraAmbiente() {

		path = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_MANAGEMENT_PATH);
		appName = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_NOME_APP);

		if (path == null || "null".equals(path))
			path = "";
		else
			pathCode = path.replace("/flavors/", "") + "_";

		// recupera configurações de ambiente
		urlFacebook = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_FACEBOOK);
		urlTwitter = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_TWITTER);
		urlGplus = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_GPLUS);
		urlInstagram = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_INTAGRAM);
		urlBlog = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_BLOG);
		urlMain = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_MAIN_SITE);
		urlSite = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SITE);
		emailSuporte = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SUPPORT_EMAIL);

		resource = ResourceBundleUtils.getInstance();
	}

	protected void montaNotasVersao() {
		String[] versoesArray = ResourceBundleUtils.getInstance().recuperaListaChaves("versao.numero.2",
				getFacesContext(), true);
		versoes = new ArrayList<String>();
		for (String v : versoesArray)
			versoes.add(v);
		versoesArray = ResourceBundleUtils.getInstance().recuperaListaChaves("versao.numero.1", getFacesContext(),
				true);
		for (String v : versoesArray)
			versoes.add(v);
	}

	/**
	 * 
	 * Monta o menu para que seja exibido. Os menus são exibidos conforme o perfil
	 * do usuário.
	 *
	 * @author: Gustavo Diniz
	 * @throws Exception e
	 */
	public void montaMenu() throws Exception {

		// if(menu == null){
		// inicializa lista
		this.menu = new DefaultMenuModel();

		usuarioLogado = getUsuarioLogado();

		/*
		 * Adiciona os menus do primeiro nÃ­vel NOTA: Colocar um método para cada grupo
		 * de menus
		 */
		if (usuarioLogado != null) {
			verificaTipoAcesso();

			this.criaMenuCadastro();

			this.criaMenuRelatorio();

			this.criaMenuConfiguracao();

			this.criaMenuAdministracao();

			this.criaMenuAjuda();

			this.montaMenuUsuario();

		}

		// retiraMenusVazios();

	}

	private void criaMenuAdministracao() {

		/*
		 * if(!"smartponto".equals(usuarioLogado.getCliente().
		 * getNomeUnidadeOrganizacional()) &&
		 * !"startjob".equals(usuarioLogado.getCliente().getNomeUnidadeOrganizacional())
		 * && !"adminGeral".equals(usuarioLogado.getNome()) &&
		 * !"fiemg".equals(usuarioLogado.getCliente().getNomeUnidadeOrganizacional()))
		 * return;
		 */

		DefaultSubMenu adm = DefaultSubMenu.builder()
				.label(resource.recuperaChave("menu.administracao", getFacesContext())).build();

		DefaultMenuItem pesquisaCliente = DefaultMenuItem.builder()
				.value(resource.recuperaChave("menu.administracao.clientes", getFacesContext()))
				.styleClass("ui-simple-menu")
				.url(BaseConstant.URL_APLICACAO + "/paginas/sistema/clientes/pesquisaCliente.xhtml").build();
		adm.getElements().add(pesquisaCliente);

		menu.getElements().add(adm);

	}

	private void criaMenuConfiguracao() {

		// não adiciona menu para usuários que não forem admins
		if (!PerfilAcesso.ADMINISTRADOR.equals(usuarioLogado.getPerfil()))
			return;

		DefaultSubMenu configuracaoes = DefaultSubMenu.builder()
				.label(resource.recuperaChave("menu.configuracao", getFacesContext())).build();

		DefaultMenuItem regras = DefaultMenuItem.builder()
				.value(resource.recuperaChave("menu.configuracao.regras", getFacesContext()))
				.styleClass("ui-simple-menu")
				.url(BaseConstant.URL_APLICACAO + "/paginas/sistema/regras/pesquisaRegra.xhtml").build();
		configuracaoes.getElements().add(regras);

//        DefaultMenuItem importacao = DefaultMenuItem.builder()
//        		.value(resource.recuperaChave("menu.configuracao.importacao", getFacesContext()))
//                .styleClass("ui-simple-menu")
//                .url("/paginas/cadastro/pesquisaUsuarios.xhtml")
//                .build();
//        configuracaoes.getElements().add(importacao);

		DefaultMenuItem parametrosGerais = DefaultMenuItem.builder()
				.value(resource.recuperaChave("menu.configuracao.gerais", getFacesContext()))
				.styleClass("ui-simple-menu")
				.url(BaseConstant.URL_APLICACAO + "/paginas/sistema/configuracoes/gerenciarParametros.xhtml").build();
		configuracaoes.getElements().add(parametrosGerais);

		menu.getElements().add(configuracaoes);

	}

	private void criaMenuRelatorio() {

		// não adiciona menu para usuários que não forem admins ou gerentes
		if (!PerfilAcesso.ADMINISTRADOR.equals(usuarioLogado.getPerfil())
				&& !PerfilAcesso.GERENTE.equals(usuarioLogado.getPerfil()))
			return;

		DefaultSubMenu relatorios = DefaultSubMenu.builder()
				.label(resource.recuperaChave("menu.relatorio", getFacesContext())).build();

		DefaultMenuItem pedestres = DefaultMenuItem.builder()
				.value(resource.recuperaChave("menu.relatorio.acesso.pedestre", getFacesContext()))
				.styleClass("ui-simple-menu")
				.url(BaseConstant.URL_APLICACAO + "/paginas/sistema/relatorios/pedestres.xhtml").build();
		relatorios.getElements().add(pedestres);

		DefaultMenuItem visitantes = DefaultMenuItem.builder()
				.value(resource.recuperaChave("menu.relatorio.visitante", getFacesContext()))
				.styleClass("ui-simple-menu")
				.url(BaseConstant.URL_APLICACAO + "/paginas/sistema/relatorios/visitantes.xhtml").build();
		relatorios.getElements().add(visitantes);

		DefaultMenuItem ocupacao = DefaultMenuItem.builder()
				.value(resource.recuperaChave("menu.relatorio.ocupacao", getFacesContext()))
				.styleClass("ui-simple-menu")
				.url(BaseConstant.URL_APLICACAO + "/paginas/sistema/relatorios/ocupacao.xhtml").build();
		relatorios.getElements().add(ocupacao);

		DefaultMenuItem equipamentos = DefaultMenuItem.builder()
				.value(resource.recuperaChave("menu.relatorio.equipamentos", getFacesContext()))
				.styleClass("ui-simple-menu")
				.url(BaseConstant.URL_APLICACAO + "/paginas/sistema/relatorios/equipamentosConectados.xhtml").build();
		relatorios.getElements().add(equipamentos);

		DefaultMenuItem liberacoesManuais = DefaultMenuItem.builder()
				.value(resource.recuperaChave("menu.relatorio.liberacoes.manuais", getFacesContext()))
				.styleClass("ui-simple-menu")
				.url(BaseConstant.URL_APLICACAO + "/paginas/sistema/relatorios/liberacoesManuais.xhtml").build();
		relatorios.getElements().add(liberacoesManuais);

		DefaultMenuItem relatorioPermanencia = DefaultMenuItem.builder()
				.value(resource.recuperaChave("menu.relatorio.permanencia", getFacesContext()))
				.styleClass("ui-simple-menu")
				.url(BaseConstant.URL_APLICACAO + "/paginas/sistema/relatorios/relatorioPermanencia.xhtml").build();
		relatorios.getElements().add(relatorioPermanencia);

		menu.getElements().add(relatorios);

	}

	private void criaMenuCadastro() {
		// nao cria menu cadastro para responsavel
		if (PerfilAcesso.RESPONSAVEL.equals(usuarioLogado.getPerfil()))
			return;

		DefaultSubMenu cadastros = DefaultSubMenu.builder()
				.label(resource.recuperaChave("menu.cadastro", getFacesContext())).build();

		if (PerfilAcesso.ADMINISTRADOR.equals(usuarioLogado.getPerfil())
				|| PerfilAcesso.GERENTE.equals(usuarioLogado.getPerfil())) {
			DefaultMenuItem usuarios = DefaultMenuItem.builder()
					.value(resource.recuperaChave("menu.cadastro.usuario", getFacesContext()))
					.url(BaseConstant.URL_APLICACAO + "/paginas/sistema/usuarios/pesquisaUsuarios.xhtml")
					.styleClass("ui-simple-menu").build();
			cadastros.getElements().add(usuarios);
		}

		if (!PerfilAcesso.PORTEIRO.equals(usuarioLogado.getPerfil())
				&& !PerfilAcesso.CUIDADOR.equals(usuarioLogado.getPerfil())) {
			DefaultMenuItem empresas = DefaultMenuItem.builder()
					.value(resource.recuperaChave("menu.cadastro.empresa", getFacesContext()))
					.url(BaseConstant.URL_APLICACAO + "/paginas/sistema/empresas/pesquisaEmpresa.xhtml")
					.styleClass("ui-simple-menu").build();
			cadastros.getElements().add(empresas);
		}

		if (!PerfilAcesso.PORTEIRO.equals(usuarioLogado.getPerfil())) {
			DefaultMenuItem pedestres = DefaultMenuItem.builder()
					.value(resource.recuperaChave("menu.cadastro.pedestre", getFacesContext()))
					.url(BaseConstant.URL_APLICACAO + "/paginas/sistema/pedestres/pesquisaPedestre.xhtml?tipo=pe")
					.styleClass("ui-simple-menu").build();
			cadastros.getElements().add(pedestres);
		}

		DefaultMenuItem visitantes = DefaultMenuItem.builder()
				.value(resource.recuperaChave("menu.cadastro.visitante", getFacesContext()))
				.url(BaseConstant.URL_APLICACAO + "/paginas/sistema/pedestres/pesquisaPedestre.xhtml?tipo=vi")
				.styleClass("ui-simple-menu").build();
		cadastros.getElements().add(visitantes);

		if (!PerfilAcesso.CUIDADOR.equals(usuarioLogado.getPerfil())
				&& !PerfilAcesso.PORTEIRO.equals(usuarioLogado.getPerfil())) {
			DefaultMenuItem responsaveis = DefaultMenuItem.builder()
					.value(resource.recuperaChave("menu.cadastro.responsavel", getFacesContext()))
					.url(BaseConstant.URL_APLICACAO + "/paginas/sistema/responsaveis/pesquisaResponsavel.xhtml")
					.styleClass("ui-simple-menu").build();
			cadastros.getElements().add(responsaveis);
		}

		// para admins ou gerentes
		if (PerfilAcesso.ADMINISTRADOR.equals(usuarioLogado.getPerfil())
				|| PerfilAcesso.GERENTE.equals(usuarioLogado.getPerfil())) {
			DefaultMenuItem alteracaoEmMassa = DefaultMenuItem.builder()
					.value(resource.recuperaChave("menu.cadastro.alteracao.massa", getFacesContext()))
					.url(BaseConstant.URL_APLICACAO + "/paginas/sistema/alteracoes/alteracoesEmMassa.xhtml")
					.styleClass("ui-simple-menu").build();
			cadastros.getElements().add(alteracaoEmMassa);
		}

		menu.getElements().add(cadastros);
	}

	private void montaMenuUsuario() {

		menuUsuario = new DefaultMenuModel();

		DefaultSubMenu usuario = DefaultSubMenu.builder().label(usuarioLogado.getNome()).build();

//		DefaultMenuItem meusDados = DefaultMenuItem.builder()
//      .value(resources.recuperaChave("menu.usuario.meus.dados", getFacesContext()))
//      .url("/paginas/cadastro/pesquisaUsuarios.xhtml")
//      .styleClass("ui-simple-menu")
//      .build();
//		menuUsuario.getElements().add(meusDados);

		DefaultMenuItem sair = DefaultMenuItem.builder()
				.value(resource.recuperaChave("menu.usuario.sair", getFacesContext())).styleClass("ui-simple-menu")
				.function(new SerializableFunction<MenuItem, String>() {

					@Override
					public String apply(MenuItem t) {
						logoff();
						return null;
					}
				}).build();
		usuario.getElements().add(sair);

		menuUsuario.getElements().add(usuario);

	}

	private void criaMenuAjuda() {

		DefaultSubMenu ajuda = DefaultSubMenu.builder().label("Ajuda").build();

		if (PerfilAcesso.ADMINISTRADOR.equals(usuarioLogado.getPerfil())
				|| PerfilAcesso.GERENTE.equals(usuarioLogado.getPerfil())) {
			DefaultMenuItem downloadApp = DefaultMenuItem.builder().value("Controle Acesso Desktop")
					.styleClass("ui-simple-menu").onclick("PF('downloads').show()").build();
			ajuda.getElements().add(downloadApp);

			DefaultMenuItem controleFacialDesktop = DefaultMenuItem.builder().value("Controle Facial Desktop")
					.styleClass("ui-simple-menu").rendered(exibeMenuControleFacialDesktop())
					.onclick("PF('downloadSmartFaces').show()").build();
			ajuda.getElements().add(controleFacialDesktop);
		}

		DefaultMenuItem topicos = DefaultMenuItem.builder().value("Tópicos de ajuda").styleClass("ui-simple-menu")
				.url("/paginas/cadastro/pesquisaRegras.xhtml").build();
		ajuda.getElements().add(topicos);

		menu.getElements().add(ajuda);

	}

	/**
	 * Recupera menu
	 *
	 * @author: Gustavo Diniz
	 * @return menu - lista de menus
	 */
	public MenuModel getMenu() {
		try {

			if (menu == null)
				montaMenu();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return this.menu;
	}

	public void logoff() {

		// retira dados da sessão
		setSessioAtrribute("usuario", null);
		setSessioAtrribute("horaLogin", null);
		setSessioAtrribute("iUser", null);
		setSessioAtrribute("roles", null);
		getRequest().getSession().invalidate();

		// envia login.jsf
		redirect("/login.xhtml");

	}

	/**
	 * Defini menu
	 *
	 * @author: Gustavo Diniz
	 * @param list - lista de menus
	 */
	public void setMenu(MenuModel list) {
		this.menu = list;
	}

	public void alteraQuantidadePagina(ValueChangeEvent event) {

		Integer qtd = (Integer) event.getNewValue();

		String[] uri = getRequest().getRequestURI().split("/");

		CookieUtils.setCookiePropertie("qtdPagina_" + uri[uri.length - 1], qtd.toString(), getFacesContext());
	}

	private boolean exibeMenuControleFacialDesktop() {
		boolean habilitaAcessoPorReconhecimentoFacial = false;

		ParametroEntity param = baseEJB.getParametroSistema(
				BaseConstant.PARAMETERS_NAME.HABILITA_ACESSO_POR_RECONHECIMENTO_FACIAL,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			habilitaAcessoPorReconhecimentoFacial = Boolean.valueOf(param.getValor());

		return habilitaAcessoPorReconhecimentoFacial;
	}

	public boolean isProdution() {
		return AppAmbienteUtils.isProdution();
	}

	public void renovaSessao() {
		System.out.println("Sessão renovada para mais 2 horas para usuário " + getUsuarioLogado().getEmail() + ".");
	}

	public List<String> getVersoes() {
		return versoes;
	}

	public void setVersoes(List<String> versoes) {
		this.versoes = versoes;
	}

	public Boolean getIsProduction() {
		return isProduction;
	}

	public void setIsProduction(Boolean isProduction) {
		this.isProduction = isProduction;
	}

	public String getCurrencySymbol() {
		currencySymbol = (String) getSessionAtrribute("currencySymbol");
		return currencySymbol;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
		setSessioAtrribute("currencySymbol", this.currencySymbol);
	}

	public String getPathCode() {
		return pathCode;
	}

	public void setPathCode(String pathCode) {
		this.pathCode = pathCode;
	}

	public UsuarioEntity getUsuarioLogado() {

		usuarioLogado = (UsuarioEntity) getSessionAtrribute(BaseConstant.LOGIN.USER_ENTITY);

		return usuarioLogado;
	}

	public void setUsuarioLogado(UsuarioEntity usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUrlTwitter() {
		return urlTwitter;
	}

	public void setUrlTwitter(String urlTwitter) {
		this.urlTwitter = urlTwitter;
	}

	public String getUrlFacebook() {
		return urlFacebook;
	}

	public void setUrlFacebook(String urlFacebook) {
		this.urlFacebook = urlFacebook;
	}

	public String getUrlGplus() {
		return urlGplus;
	}

	public void setUrlGplus(String urlGplus) {
		this.urlGplus = urlGplus;
	}

	public String getUrlInstagram() {
		return urlInstagram;
	}

	public void setUrlInstagram(String urlInstagram) {
		this.urlInstagram = urlInstagram;
	}

	public String getUrlBlog() {
		return urlBlog;
	}

	public void setUrlBlog(String urlBlog) {
		this.urlBlog = urlBlog;
	}

	public String getUrlMain() {
		return urlMain;
	}

	public void setUrlMain(String urlMain) {
		this.urlMain = urlMain;
	}

	public String getUrlYoutube() {
		return urlYoutube;
	}

	public void setUrlYoutube(String urlYoutube) {
		this.urlYoutube = urlYoutube;
	}

	public String getUrlSite() {
		return urlSite;
	}

	public void setUrlSite(String urlSite) {
		this.urlSite = urlSite;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getEmailSuporte() {
		return emailSuporte;
	}

	public void setEmailSuporte(String emailSuporte) {
		this.emailSuporte = emailSuporte;
	}

	public MenuModel getMenuUsuario() {
		return menuUsuario;
	}

	public void setMenuUsuario(MenuModel menuUsuario) {
		this.menuUsuario = menuUsuario;
	}

}
