package br.com.startjob.acesso.controller.uc004;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.CadastroBaseController;
import br.com.startjob.acesso.modelo.entity.EnderecoEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.enumeration.PerfilAcesso;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.utils.EncryptionUtils;

@Named("cadastroUsuarioController")
@ViewScoped
@UseCase(classEntidade=UsuarioEntity.class, funcionalidade="Cadastro de usuários", 
		urlNovoRegistro="/paginas/sistema/usuarios/cadastroUsuarios.jsf", queryEdicao="findByIdComplete")
public class CadastroUsuarioController extends CadastroBaseController {

	private static final long serialVersionUID = 1L;
	
	private List<SelectItem> listaStatus;
	private List<SelectItem> listaPerfilAcesso;
	
	private String usuarioLogin;
	private String usuarioSenha;
	
	@PostConstruct
	@Override
	public void init() {
		super.init();
		UsuarioEntity usuario = (UsuarioEntity) getEntidade();
		
		if(usuario != null && usuario.getEndereco() == null) {
			usuario.setEndereco(new EnderecoEntity());
		}
		
		if(usuario.getId() != null) {
			buscarLoginUsuario();
		}
		
		montaListaStatus();
		montaListaPerfilAcesso();
	}
	
	@Override
	public String salvar() {
		UsuarioEntity usuario = (UsuarioEntity) getEntidade();
		
		if(usuario.getId() == null && 
				usuario.getEmail() != null &&
				!"".equals(usuario.getEmail()) &&
				verificaEmailExistente(usuario.getEmail())) {

			mensagemFatal("", "msg.email.existente");
			return "";
		}
		
		if(usuario.getId() == null 
				&& verificaLoginExistente(usuario.getLogin(), getUsuarioLogado().getCliente().getId())) {
			mensagemFatal("", "msg.login.existente");
			return "";
		}
		
		if(usuario.getId() == null) {
			try {
				usuario.setSenha(EncryptionUtils.encrypt(usuario.getSenha()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		usuario.setCliente(getUsuarioLogado().getCliente());

		if(usuario.getEndereco().getCep() == null || usuario.getEndereco().getCep().equals("")) {
			usuario.setEndereco(null);
		}
		
		String retorno = super.salvar();
		
		redirect("/paginas/sistema/usuarios/pesquisaUsuarios.xhtml?acao=OK");
		
		return retorno;
	}
	
	public void buscarLoginUsuario() {
		UsuarioEntity usuario = (UsuarioEntity) getEntidade();
		
		if(usuario != null) {
			usuarioLogin = usuario.getLogin();
		}
	}
	
	public void alterarLoginSenhaUsuarioUsuario() throws Exception {
		UsuarioEntity usuario = (UsuarioEntity) getEntidade();
		
		if(usuarioLogin.equals("")) {
			mensagemFatal("", "msg.login.vazio");
			return;
			
		} else if(!usuario.getLogin().equals(usuarioLogin) 
				&& verificaLoginExistente(usuarioLogin, getUsuarioLogado().getCliente().getId())) {
			mensagemFatal("", "msg.login.existente");
			return;
			
		} else if(usuarioSenha.equals("")) {
			mensagemFatal("", "msg.senha.vazio");
			
		} else {
			try {
				usuario.setLogin(usuarioLogin);
				usuario.setSenha(EncryptionUtils.encrypt(usuarioSenha));
				
				baseEJB.alteraObjeto(usuario);
				usuario.setVersao(usuario.getVersao()+1);
				mensagemInfo("", "msg.uc014.alteracao.realizada");
				
			} catch (Exception e) {
				mensagemFatal("", "#Não foi possível alterar os dados do usuário.");
			}
		}
		
		usuarioLogin = "";
		usuarioSenha = "";
	}
	
	private void montaListaStatus() {
		listaStatus = new ArrayList<SelectItem>();
		listaStatus.add(new SelectItem(null, "Selecione"));
		listaStatus.add(new SelectItem(Status.ATIVO, Status.ATIVO.toString()));
		listaStatus.add(new SelectItem(Status.INATIVO, Status.INATIVO.toString()));
	}
	
	public void montaListaPerfilAcesso() {
		listaPerfilAcesso = new ArrayList<SelectItem>();
		listaPerfilAcesso.add(new SelectItem(null, "Selecione"));
		listaPerfilAcesso.add(new SelectItem(PerfilAcesso.ADMINISTRADOR, PerfilAcesso.ADMINISTRADOR.toString()));
		listaPerfilAcesso.add(new SelectItem(PerfilAcesso.GERENTE, PerfilAcesso.GERENTE.toString()));
		listaPerfilAcesso.add(new SelectItem(PerfilAcesso.OPERADOR, PerfilAcesso.OPERADOR.toString()));
		listaPerfilAcesso.add(new SelectItem(PerfilAcesso.PORTEIRO, PerfilAcesso.PORTEIRO.toString()));
		listaPerfilAcesso.add(new SelectItem(PerfilAcesso.CUIDADOR, PerfilAcesso.CUIDADOR.toString()));
		listaPerfilAcesso.add(new SelectItem(PerfilAcesso.RESPONSAVEL, PerfilAcesso.RESPONSAVEL.toString()));
	}
	
	
	public List<SelectItem> getListaStatus() {
		return listaStatus;
	}

	public List<SelectItem> getListaPerfilAcesso() {
		return listaPerfilAcesso;
	}

	public String getUsuarioLogin() {
		return usuarioLogin;
	}

	public void setUsuarioLogin(String usuarioLogin) {
		this.usuarioLogin = usuarioLogin;
	}

	public String getUsuarioSenha() {
		return usuarioSenha;
	}

	public void setUsuarioSenha(String usuarioSenha) {
		this.usuarioSenha = usuarioSenha;
	}
	
}
