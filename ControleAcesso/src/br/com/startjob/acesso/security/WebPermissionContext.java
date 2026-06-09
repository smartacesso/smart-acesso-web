package br.com.startjob.acesso.security;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.enumeration.WebPermissao;

@Named("webPermission")
@SessionScoped
public class WebPermissionContext implements Serializable {

	private static final long serialVersionUID = 1L;

	private Set<String> permissoes = new HashSet<>();

	public void carregar(Set<String> codigos) {
		permissoes = new HashSet<>();
		if (codigos != null) {
			permissoes.addAll(codigos);
		}
	}

	public void limpar() {
		permissoes.clear();
	}

	public boolean tem(WebPermissao permissao) {
		return permissao != null && permissoes.contains(permissao.getCodigo());
	}

	public boolean tem(String codigo) {
		return codigo != null && permissoes.contains(codigo);
	}

	public Set<String> getPermissoes() {
		return Collections.unmodifiableSet(permissoes);
	}

	public static Set<String> permissoesDaSessao(HttpServletRequest request) {
		if (request == null || request.getSession(false) == null) {
			return Collections.emptySet();
		}
		Object attr = request.getSession(false).getAttribute(BaseConstant.LOGIN.WEB_PERMISSIONS);
		if (attr instanceof Set) {
			@SuppressWarnings("unchecked")
			Set<String> set = (Set<String>) attr;
			return set;
		}
		return Collections.emptySet();
	}

	public static Set<String> permissoesDaSessao(FacesContext ctx) {
		if (ctx == null) {
			return Collections.emptySet();
		}
		return permissoesDaSessao((HttpServletRequest) ctx.getExternalContext().getRequest());
	}
}
