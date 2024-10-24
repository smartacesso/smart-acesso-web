package br.com.startjob.acesso.controller.uc005;

import javax.annotation.PostConstruct;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;

@Named("consultaEmpresaController")
@ViewScoped
@UseCase(classEntidade=EmpresaEntity.class, funcionalidade="Consulta empresas", logicalRemove = true, 
		urlNovoRegistro="/paginas/sistema/empresas/cadastroEmpresa.xhtml", lazyLoad = true, quantPorPagina = 10)
public class ConsultaEmpresaController extends BaseController{

	private static final long serialVersionUID = 1L;
	
	private String acao;
	private EmpresaEntity empresaSelecionada;
	
	@PostConstruct
	@Override
	public void init() {
		super.init();
		buscar();
		
		acao = getRequest().getParameter("acao");
	}
	
	public void excluirEmpresa() {
		if(empresaSelecionada != null) {
			super.excluir(empresaSelecionada.getId());
		}
	}
	
	@Override
	public void exibeMensagens(ComponentSystemEvent evt) throws AbortProcessingException {
		super.exibeMensagens(evt);
		
		if("OK".equals(acao)) {
			mensagemInfo("", "#Empresa cadastrada com sucesso!");
		}
		
		acao = null;
	}
	
	@Override
	public String buscar() {
		getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());
		return super.buscar();
	}

	public EmpresaEntity getEmpresaSelecionada() {
		return empresaSelecionada;
	}

	public void setEmpresaSelecionada(EmpresaEntity empresaSelecionada) {
		this.empresaSelecionada = empresaSelecionada;
	}
	
}
