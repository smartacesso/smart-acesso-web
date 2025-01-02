package br.com.startjob.acesso.controller.uc006;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.entity.RegraEntity;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.enumeration.TipoRegra;

@Named("consultaRegraAcessoController")
@ViewScoped
@UseCase(classEntidade=RegraEntity.class, funcionalidade="Consulta regras", logicalRemove=true, 
		urlNovoRegistro="/paginas/sistema/regras/cadastroRegra.xhtml", lazyLoad=true, quantPorPagina=10)
public class ConsultaRegraAcessoController extends BaseController {
	
	private static final long serialVersionUID = 1L;
	
	private List<SelectItem> listaTiposPedestre;
	private List<SelectItem> listaTipoRegra;
	
	private String acao;
	
	private RegraEntity regraSelecionada;
	
	@PostConstruct
	@Override
	public void init() {
		super.init();
		buscar();
		
		acao = getRequest().getParameter("acao");
		
		montaListaTiposPedestre();
		montaListaTipoRegra();
	}
	
	public void excluirRegra() {
		if(regraSelecionada != null) {
			super.excluir(regraSelecionada.getId());
		}
	}
	
	@Override
	public void exibeMensagens(ComponentSystemEvent evt) throws AbortProcessingException {
		super.exibeMensagens(evt);
		
		if("OK".equals(acao)) {
			mensagemInfo("", "#Regra cadastrada com sucesso!");
		}
		
		acao = null;
	}
	
	@Override
	public String buscar() {
		getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());
		setNamedQueryPesquisa("findAllComEmpresa");
		return super.buscar();
	}
	
	private void montaListaTiposPedestre() {
		listaTiposPedestre = new ArrayList<SelectItem>();
		listaTiposPedestre.add(new SelectItem(null, "Filtrar por tipo de pedestre"));
		listaTiposPedestre.add(new SelectItem(TipoPedestre.PEDESTRE, TipoPedestre.PEDESTRE.toString()));
		listaTiposPedestre.add(new SelectItem(TipoPedestre.VISITANTE, TipoPedestre.VISITANTE.toString()));
	}
	
	private void montaListaTipoRegra() {
		listaTipoRegra = new ArrayList<SelectItem>();
		listaTipoRegra.add(new SelectItem(null, "Filtrar por tipo de regra"));
		listaTipoRegra.add(new SelectItem(TipoRegra.ACESSO_CREDITO, TipoRegra.ACESSO_CREDITO.getDescricao()));
		listaTipoRegra.add(new SelectItem(TipoRegra.ACESSO_ESCALA, TipoRegra.ACESSO_ESCALA.getDescricao()));
		listaTipoRegra.add(new SelectItem(TipoRegra.ACESSO_HORARIO, TipoRegra.ACESSO_HORARIO.getDescricao()));
		listaTipoRegra.add(new SelectItem(TipoRegra.ACESSO_PERIODO, TipoRegra.ACESSO_PERIODO.getDescricao()));
		listaTipoRegra.add(new SelectItem(TipoRegra.ACESSO_UNICO, TipoRegra.ACESSO_UNICO.getDescricao()));
	}

	public RegraEntity getRegraSelecionada() {
		return regraSelecionada;
	}

	public void setRegraSelecionada(RegraEntity regraSelecionada) {
		this.regraSelecionada = regraSelecionada;
	}

	public List<SelectItem> getListaTipoRegra() {
		return listaTipoRegra;
	}

	public void setListaTipoRegra(List<SelectItem> listaTipoRegra) {
		this.listaTipoRegra = listaTipoRegra;
	}

	public List<SelectItem> getListaTiposPedestre() {
		return listaTiposPedestre;
	}

	public void setListaTiposPedestre(List<SelectItem> listaTiposPedestre) {
		this.listaTiposPedestre = listaTiposPedestre;
	}
}
