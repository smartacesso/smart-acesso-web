package br.com.startjob.acesso.controller.uc007;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.controller.MenuController;
import br.com.startjob.acesso.modelo.ejb.WebPermissaoEJBRemote;
import br.com.startjob.acesso.modelo.enumeration.PerfilAcesso;
import br.com.startjob.acesso.modelo.enumeration.WebPermissao;
import br.com.startjob.acesso.to.WebPermissaoLinhaTO;

@ViewScoped
@Named("matrizPermissoesController")
public class MatrizPermissoesController extends BaseController {

	private static final long serialVersionUID = 1L;

	@EJB
	private WebPermissaoEJBRemote webPermissaoEJB;

	@Inject
	private MenuController menuController;

	private List<WebPermissaoLinhaTO> linhas = new ArrayList<>();

	@PostConstruct
	public void init() {
		if (!temPermissaoWeb(WebPermissao.CONFIG_WEB_PERMISSOES_EDITAR)
				&& !temPermissaoWeb(WebPermissao.CONFIG_WEB_PERMISSOES_VER)) {
			return;
		}
		carregar();
	}

	public void carregar() {
		try {
			Long idCliente = getUsuarioLogado().getCliente().getId();
			Map<PerfilAcesso, Set<String>> matriz = webPermissaoEJB.carregarMatrizEfetiva(idCliente);
			linhas = new ArrayList<>();
			for (WebPermissao permissao : WebPermissao.values()) {
				WebPermissaoLinhaTO linha = new WebPermissaoLinhaTO(permissao);
				linha.setAdministrador(contem(matriz, PerfilAcesso.ADMINISTRADOR, permissao));
				linha.setGerente(contem(matriz, PerfilAcesso.GERENTE, permissao));
				linha.setOperador(contem(matriz, PerfilAcesso.OPERADOR, permissao));
				linha.setPorteiro(contem(matriz, PerfilAcesso.PORTEIRO, permissao));
				linha.setCuidador(contem(matriz, PerfilAcesso.CUIDADOR, permissao));
				linha.setResponsavel(contem(matriz, PerfilAcesso.RESPONSAVEL, permissao));
				linhas.add(linha);
			}
		} catch (Exception e) {
			e.printStackTrace();
			mensagemFatal("", "#" + e.getMessage());
		}
	}

	public String salvar() {
		if (!temPermissaoWeb(WebPermissao.CONFIG_WEB_PERMISSOES_EDITAR)) {
			mensagemFatal("", "msg.web.permissao.acesso.negado");
			return "";
		}
		try {
			Map<PerfilAcesso, Set<String>> matriz = new EnumMap<>(PerfilAcesso.class);
			for (PerfilAcesso perfil : PerfilAcesso.values()) {
				matriz.put(perfil, new HashSet<>());
			}
			for (WebPermissaoLinhaTO linha : linhas) {
				adicionarSe(matriz, PerfilAcesso.ADMINISTRADOR, linha.isAdministrador(), linha.getCodigo());
				adicionarSe(matriz, PerfilAcesso.GERENTE, linha.isGerente(), linha.getCodigo());
				adicionarSe(matriz, PerfilAcesso.OPERADOR, linha.isOperador(), linha.getCodigo());
				adicionarSe(matriz, PerfilAcesso.PORTEIRO, linha.isPorteiro(), linha.getCodigo());
				adicionarSe(matriz, PerfilAcesso.CUIDADOR, linha.isCuidador(), linha.getCodigo());
				adicionarSe(matriz, PerfilAcesso.RESPONSAVEL, linha.isResponsavel(), linha.getCodigo());
			}
			webPermissaoEJB.salvarMatrizCliente(getUsuarioLogado().getCliente().getId(), matriz);
			java.util.Set<String> permissoesAtualizadas = webPermissaoEJB.resolverPermissoesWeb(getUsuarioLogado());
			setSessioAtrribute(br.com.startjob.acesso.modelo.BaseConstant.LOGIN.WEB_PERMISSIONS,
					permissoesAtualizadas);
			if (webPermission != null) {
				webPermission.carregar(permissoesAtualizadas);
			}
			mensagemInfo("", "msg.web.permissao.salvo.sucesso");
			if (menuController != null) {
				menuController.setMenu(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			mensagemFatal("", "#" + e.getMessage());
		}
		return "";
	}

	public void restaurarPadrao() {
		if (!temPermissaoWeb(WebPermissao.CONFIG_WEB_PERMISSOES_EDITAR)) {
			mensagemFatal("", "msg.web.permissao.acesso.negado");
			return;
		}
		linhas = new ArrayList<>();
		for (WebPermissao permissao : WebPermissao.values()) {
			WebPermissaoLinhaTO linha = new WebPermissaoLinhaTO(permissao);
			linha.setAdministrador(contemPadrao(PerfilAcesso.ADMINISTRADOR, permissao));
			linha.setGerente(contemPadrao(PerfilAcesso.GERENTE, permissao));
			linha.setOperador(contemPadrao(PerfilAcesso.OPERADOR, permissao));
			linha.setPorteiro(contemPadrao(PerfilAcesso.PORTEIRO, permissao));
			linha.setCuidador(contemPadrao(PerfilAcesso.CUIDADOR, permissao));
			linha.setResponsavel(contemPadrao(PerfilAcesso.RESPONSAVEL, permissao));
			linhas.add(linha);
		}
		mensagemInfo("", "msg.web.permissao.restaurado.padrao");
	}

	private boolean contem(Map<PerfilAcesso, Set<String>> matriz, PerfilAcesso perfil, WebPermissao permissao) {
		Set<String> set = matriz.get(perfil);
		return set != null && set.contains(permissao.getCodigo());
	}

	private boolean contemPadrao(PerfilAcesso perfil, WebPermissao permissao) {
		return br.com.startjob.acesso.modelo.web.WebPermissaoMatriz.permissoesPadrao(perfil).contains(permissao);
	}

	private void adicionarSe(Map<PerfilAcesso, Set<String>> matriz, PerfilAcesso perfil, boolean habilitado,
			String codigo) {
		if (habilitado) {
			matriz.get(perfil).add(codigo);
		}
	}

	public List<WebPermissaoLinhaTO> getLinhas() {
		return linhas;
	}

	public void setLinhas(List<WebPermissaoLinhaTO> linhas) {
		this.linhas = linhas;
	}
}
