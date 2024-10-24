package br.com.startjob.acesso.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
import br.com.startjob.acesso.modelo.utils.AppAmbienteUtils;

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
	@SuppressWarnings("rawtypes")
	private void validaPermissaoUsuario(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		List<String> paginas = montaListaPaginasPermissao(req);// .getInstance(null,req).paginasPorUsuario();
		boolean redirecionaErro = true;

		if (paginas != null && !paginas.isEmpty()) {
			// verifica as paginas do usuário
			// se ele estiver tentando acessar uma página
			// que não pertence, envia para página avisando que
			// usuário não tem permissão

			for (Iterator iterator = paginas.iterator(); iterator.hasNext();) {
				String pagina = (String) iterator.next();
				if (req.getRequestURI().contains(pagina)) {
					redirecionaErro = false;
					break;
				}
			}

		} else {
			// envia para página avisando que o usuário não tem
			// permissão de acesso
			redirecionaErro = false;
		}

		if (redirecionaErro) {
			// envia para tela de erro de permissão
			// req.getRequestDispatcher(BaseConstant.URL_APLICACAO+"/permissionDanied.jsf").forward(req,
			// resp);
			RequestDispatcher requestDispatcher = req.getRequestDispatcher(getMainSite(req) + "/bloqueado");
			requestDispatcher.forward(req, resp);
		}
	}

	/**
	 * Monta lista de páginas que usuário tem permissão
	 * 
	 * @param req
	 * @return
	 */
	private List<String> montaListaPaginasPermissao(HttpServletRequest req) {

		return null;
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

