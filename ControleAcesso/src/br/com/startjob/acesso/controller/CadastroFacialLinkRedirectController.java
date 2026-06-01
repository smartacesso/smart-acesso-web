package br.com.startjob.acesso.controller;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@Named("cadastroFacialLinkRedirectController")
@ViewScoped
public class CadastroFacialLinkRedirectController implements Serializable {

	private static final long serialVersionUID = 1L;

	public void redirecionarParaCadastroFacialPorLink() throws IOException {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		HttpServletRequest req = (HttpServletRequest) ec.getRequest();

		String qs = req.getQueryString();
		String alvo = req.getContextPath() + "/cadastroFacialPorLink.xhtml";
		if (qs != null && !qs.isEmpty()) {
			alvo += "?" + qs;
		}

		ec.redirect(alvo);
		fc.responseComplete();
	}

}
