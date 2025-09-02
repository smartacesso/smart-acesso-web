package br.com.startjob.acesso.controller.uc016;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.ejb.BaseEJB;
import br.com.startjob.acesso.modelo.entity.ResponsibleEntity;

@Named("consultaResponsavelController")
@ViewScoped
@UseCase(classEntidade=ResponsibleEntity.class, funcionalidade="Consulta responsavel", logicalRemove=true, 
		urlNovoRegistro="/paginas/sistema/responsaveis/cadastroResponsavel.xhtml", lazyLoad=true, quantPorPagina=10)
public class ConsultaResponsavelController  extends BaseController {

	private static final long serialVersionUID = 1L;
	
	private List<SelectItem> listaResponsavel;
	
	private String acao;
	
	private ResponsibleEntity responsavelSelecionado;
	
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
			mensagemInfo("", "#Responsavel cadastrado com sucesso!");
		}
		
		acao = null;
	}
	
	@Override
	public String buscar() {
		getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());
		setNamedQueryPesquisa("findAllNaoRemovido");
		return super.buscar();
	}
	
	
	
	public void excluirResponsavel() {
		if(responsavelSelecionado != null) {
			responsavelSelecionado.setLogin(null);
			responsavelSelecionado.setPassword(null);
			try {
				baseEJB.alteraObjeto(responsavelSelecionado);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			super.excluir(responsavelSelecionado.getId());
		}
	}

	public List<SelectItem> getListaResponsavel() {
		return listaResponsavel;
	}

	public void setListaResponsavel(List<SelectItem> listaResponsavel) {
		this.listaResponsavel = listaResponsavel;
	}

	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

	public ResponsibleEntity getResponsavelSelecionado() {
		return responsavelSelecionado;
	}

	public void setResponsavelSelecionado(ResponsibleEntity responsavelSelecionado) {
		this.responsavelSelecionado = responsavelSelecionado;
	}

}
