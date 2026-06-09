package br.com.startjob.acesso.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.utils.AppAmbienteUtils;
import br.com.startjob.acesso.modelo.web.WebPermissaoMatriz;
import br.com.startjob.acesso.security.WebPermissionContext;
import br.com.startjob.acesso.security.WebUrlPermissaoMap;

/**
 * Servlet Filter implementation class SecurityFilter
 * 
 * 
 * 
 */
@WebFilter("/*")
public class SecurityFilter implements Filter {
	

	/**
	 * Construtor padrão
	 *
	 * @author: Gustavo Diniz
	 * 
	 */
	public SecurityFilter() {
		super();
	}

	/**
	 * init
	 *
	 * @author: Gustavo Diniz
	 * @param arg0
	 *            - parametro
	 * @throws javax.servlet.ServletException
	 *             e
	 */
	public void init(FilterConfig arg0) throws ServletException {
		if (arg0 != null) {
			arg0.toString();
		}
	}

	boolean redirected = false;

	/**
	 * Realiza filter
	 *
	 * @author: Gustavo Diniz
	 * @param request
	 *            - resquest
	 * @param response
	 *            - response
	 * @param chain
	 *            - chain
	 * @throws java.io.IOException
	 *             e
	 * @throws javax.servlet.ServletException
	 *             e
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		
		processaAcessoPadrao(req, resp);

		if (req.getRequestURI().contains("paginas") || req.getRequestURI().contains("app")) {

			/// verifica se existe usuário na sessão
			validaUsuarioAutenticado(req, resp);
			
			// valida permissões gerais
			validaPermissaoUsuario(req, resp);

		} else {

			// verifica se é alguma das páginas externas
			// se for envia para a tela inicial apos o login
			if (req.getSession(true) != null && req.getSession(true).getAttribute("usuario") != null
					&& (req.getRequestURI().endsWith("login.xhtml") || req.getRequestURI().endsWith("/sistema/") 
							|| req.getRequestURI().endsWith("/sistema"))  ) {
				RequestDispatcher requestDispatcher = req
						.getRequestDispatcher("/paginas/principal.xhtml");
				requestDispatcher.forward(req, resp);
			} else if (req.getSession(true) == null || req.getSession(true).getAttribute("usuario") == null
					&& req.getRequestURI().contains("principal.xthml")) {
				final int quant = 0;
				String urlCompleta = getMainSite(req)
						+ "/login.xhtml?URL=" + req.getRequestURI().substring(quant, req.getRequestURI().length());

				String sep = "?";
				String pa  = "";
				
				List<String> parans = Collections.list(req.getParameterNames());
				if(parans != null && !parans.isEmpty()) {
					for (String pName : parans) {
						pa += sep + pName + "=" + req.getParameter(pName);
						sep = "&";
					}
				}
				
				if(!"".equals(pa))
					urlCompleta += pa;
				
				RequestDispatcher requestDispatcher = req.getRequestDispatcher(urlCompleta);
				requestDispatcher.forward(req, resp);
			}

		}
		
		if (!redirected) {
			// segue com filter
			try {
				chain.doFilter(req, resp);
			} catch (Error e) {
				e.printStackTrace();
			}
		} else
			redirected = false;

	}

	private void processaAcessoPadrao(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String path = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_MANAGEMENT_PATH);
		if(path != null && !"".equals(path)) {
			
			/*
			 * verifica redirecionamentos para personalizações
			 */
			
			if(req.getRequestURI().equals("/")) {
				if(req.getSession(true).getAttribute(BaseConstant.LOGIN.USER_ENTITY) == null) {
					RequestDispatcher requestDispatcher = req.getRequestDispatcher(getMainSite(req)+"/login.xhtml");
					requestDispatcher.forward(req, resp);
				} else{
					RequestDispatcher requestDispatcher = req.getRequestDispatcher(getMainSite(req)+"/paginas/principal.xhtml");
					requestDispatcher.forward(req, resp);
				}
			}
			
		} else {
			
			/*
			 * verifica redirecionamentos
			 */
			if(req.getRequestURI().equals("/") || req.getRequestURI().equals("")){
				if(req.getRequestURL().toString().contains("localhost")){
					RequestDispatcher requestDispatcher = req.getRequestDispatcher(getMainSite(req)+"/login.xhtml");
					requestDispatcher.forward(req, resp);
				}
			}
			
		}
		
	}

	/**
	 * Verifica se usuário esta logado.
	 *
	 * @author: Gustavo Diniz
	 * @param req
	 *            - request
	 * @param resp
	 *            - response
	 * @throws IOException
	 *             <em>
	 * @throws ServletException
	 *             e
	 */
	private void validaUsuarioAutenticado(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		if (req.getSession(true) != null && req.getSession(true).getAttribute(BaseConstant.LOGIN.USER_ENTITY) == null
				&& req.getRequestURI().contains("paginas")) {
			// se não tiver logado redireciona para login
			final int quant = 0;
			
			String urlCompleta = getMainSite(req)
					+ "/login.xhtml?URL=" + req.getRequestURI().substring(quant, req.getRequestURI().length());

			String sep = "?";
			String pa  = "";
			
			List<String> parans = Collections.list(req.getParameterNames());
			if(parans != null && !parans.isEmpty()) {
				for (String pName : parans) {
					pa += sep + pName + "=" + req.getParameter(pName);
					sep = "&";
				}
			}
			
			if(!"".equals(pa))
				urlCompleta += pa;
			
			resp.sendRedirect(urlCompleta);
			resp.flushBuffer();
			redirected = true;
			
		} else if (req.getSession(true).getAttribute(BaseConstant.LOGIN.USER_ENTITY) != null
				&& req.getRequestURI().contains("login")) {
			// se tiver usuário e esta na página de login, vai para a principal
			RequestDispatcher requestDispatcher = req
					.getRequestDispatcher(getMainSite(req) + "/paginas/principal.xhtml");
			requestDispatcher.forward(req, resp);
		}
	}

	private String getMainSite(HttpServletRequest req) {
		return BaseConstant.URL_APLICACAO;
	}

	/**
	 * Verifica se usuário tem permissão de acesso a esta página.
	 *
	 * @author: Gustavo Diniz
	 * @param req
	 *            - request
	 * @param resp
	 *            - response
	 * @throws IOException
	 *             e
	 * @throws ServletException
	 */
	private void validaPermissaoUsuario(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		if (req.getSession(false) == null
				|| req.getSession(false).getAttribute(BaseConstant.LOGIN.USER_ENTITY) == null) {
			return;
		}

		String uri = req.getRequestURI();
		java.util.Set<String> permissoes = WebPermissionContext.permissoesDaSessao(req);
		if (permissoes.isEmpty()) {
			Object usuario = req.getSession(false).getAttribute(BaseConstant.LOGIN.USER_ENTITY);
			if (usuario instanceof UsuarioEntity) {
				permissoes = WebPermissaoMatriz.codigosPadrao(((UsuarioEntity) usuario).getPerfil());
			}
		}
		if (!WebUrlPermissaoMap.permissoesParaUri(uri).isEmpty()
				&& !WebUrlPermissaoMap.possuiPermissaoParaUri(uri, permissoes)) {
			RequestDispatcher requestDispatcher = req.getRequestDispatcher(
					getMainSite(req) + "/paginas/bloqueado.xhtml");
			requestDispatcher.forward(req, resp);
			redirected = true;
		}
	}

	/**
	 * Procedimentos de destruição
	 *
	 * @author: Gustavo Diniz
	 * 
	 */
	public void destroy() {
		// vazio
	}
	
}

