package br.com.startjob.acesso.controller.uc004;

import javax.annotation.PostConstruct;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;

@SuppressWarnings("serial")
@Named("consultaUsuarioController")
@ViewScoped
@UseCase(classEntidade=UsuarioEntity.class, funcionalidade="Consulta usuários", logicalRemove = true,
		urlNovoRegistro="/paginas/sistema/usuarios/cadastroUsuarios.xhtml", lazyLoad = true, quantPorPagina = 10)
public class ConsultaUsuarioController extends BaseController {

	private UsuarioEntity usuarioSelecionado;
	private String acao;
	
	@PostConstruct
	@Override
	public void init() {
		super.init();
		buscar();
		
		acao = getRequest().getParameter("acao");
		
	}
	
	public void excluirUsuario() {
		if(usuarioSelecionado != null) {
			super.excluir(usuarioSelecionado.getId());
		}
	}
	
	@Override
	public String buscar() {
		getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());
		return super.buscar();
	}
	
	@Override
	public void exibeMensagens(ComponentSystemEvent evt) throws AbortProcessingException {
		super.exibeMensagens(evt);
		
		if("OK".equals(acao)) {
			mensagemInfo("", "#Usuário cadastrado com sucesso!");
		}
		
		acao = null;
	}

	public UsuarioEntity getUsuarioSelecionado() {
		return usuarioSelecionado;
	}

	public void setUsuarioSelecionado(UsuarioEntity usuarioSelecionado) {
		this.usuarioSelecionado = usuarioSelecionado;
	}
	
}
