package br.com.startjob.acesso.controller.uc001;

import javax.annotation.PostConstruct;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;

@Named("consultaClienteController")
@ViewScoped
@UseCase(classEntidade=ClienteEntity.class, funcionalidade="Consulta clientes", logicalRemove = true,
		urlNovoRegistro="/paginas/sistema/clientes/cadastroCliente.xhtml", lazyLoad = true, quantPorPagina = 10)
public class ConsultaClienteController extends BaseController {

	private static final long serialVersionUID = 1L;
	
	private ClienteEntity clienteSelecionado;
	private String acao;
	
	@PostConstruct
	@Override
	public void init() {
		super.init();
		buscar();
		
		acao = getRequest().getParameter("acao");
	}
	
	@Override
	public void exibeMensagens(ComponentSystemEvent evt) throws AbortProcessingException {
		super.exibeMensagens(evt);
		
		if("OK".equals(acao)) {
			mensagemInfo("", "#Cliente cadastrado com sucesso!");
		}
		
		acao = null;
	}
	
	public void excluirCliente() {
		if(clienteSelecionado != null) {
			super.excluir(clienteSelecionado.getId());
		}
	}

	public ClienteEntity getClienteSelecionado() {
		return clienteSelecionado;
	}

	public void setClienteSelecionado(ClienteEntity clienteSelecionado) {
		this.clienteSelecionado = clienteSelecionado;
	}
	
}
