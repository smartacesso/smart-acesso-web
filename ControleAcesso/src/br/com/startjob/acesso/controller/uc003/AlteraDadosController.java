package br.com.startjob.acesso.controller.uc003;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.utils.EncryptionUtils;

@SuppressWarnings("serial")
@Named("alteraDadosController")
@ViewScoped
@UseCase(classEntidade=UsuarioEntity.class, 
		 funcionalidade="Alterar senha",
		 queryEdicao="findById", urlNovoRegistro = "/alterarDados.xhtml")
public class AlteraDadosController extends BaseController {
	
	private String senha;
	private String confirmaSenha;
	
	@PostConstruct
	@Override
	public void init() {
		super.init();
		UsuarioEntity u = (UsuarioEntity) getEntidade();
		
		String token = getRequest().getParameter("token");
		if(token == null 
				|| u.getToken() == null 
				|| !token.equals(u.getToken()))
			redirect("/login.xhtml?acao=invalido");
		
	}
	
	@Override
	public String salvar() {
		
		try {
			
			UsuarioEntity u = (UsuarioEntity) getEntidade();
			u.setSenha(EncryptionUtils.encrypt(senha));
			u.setToken(null);
			
			String r = super.salvar();
			
			if(!"e".equals(r))
				redirect("/login.xhtml?acao=sa");
			
			return r;
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getConfirmaSenha() {
		return confirmaSenha;
	}

	public void setConfirmaSenha(String confirmaSenha) {
		this.confirmaSenha = confirmaSenha;
	}

}
