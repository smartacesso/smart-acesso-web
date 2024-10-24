package br.com.startjob.acesso.controller.uc011;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.RelatorioController;
import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.utils.DateUtils;

@SuppressWarnings("serial")
@Named("relatorioVisitantesController")
@ViewScoped
@UseCase(classEntidade=AcessoEntity.class, funcionalidade="Relatório de visitantes", 
		lazyLoad = true, quantPorPagina = 10)
public class RelatorioVisitantesController extends RelatorioController {
	
	private Long idEmpresaSelecionada;
	private EmpresaEntity empresaSelecionada;
	
	private List<SelectItem> listaEmpresas;
	private List<SelectItem> listaDepartamentos;
	private List<SelectItem> listaCentrosDeCusto;
	private List<SelectItem> listaCargos;
	private List<SelectItem> listaEquipamentos;

	@PostConstruct
	@Override
	public void init() {
		super.init();
		
		getParans().put("data_maior_data", formatarDataInicioDia(new Date()));
		getParans().put("data_menor_data", formatarDataFimDia(new Date()));
		
		buscar();
		listaEmpresas = montaListaEmpresas();
		listaEquipamentos = montaListaEquipamentos();
	}
	
	@Override
	public String buscar() {
		getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());
		getParans().put("pedestre.tipo", TipoPedestre.VISITANTE);
		
		setNamedQueryPesquisa("findAllComPedestreEmpresa");
		return super.buscar();
	}
	
	public void eventoEmpresaSelecionada(ValueChangeEvent event) {
		idEmpresaSelecionada = (Long) event.getNewValue();

		if (idEmpresaSelecionada != null) {
			empresaSelecionada = buscaEmpresaPeloId(idEmpresaSelecionada);
			
			listaDepartamentos = montaListaDepartamentos(empresaSelecionada);
			listaCentrosDeCusto = montaListaCentroDeCusto(empresaSelecionada);
			listaCargos = montaListaCargo(empresaSelecionada);
			
		} else {
			listaDepartamentos = null;
			listaCargos = null;
			listaCentrosDeCusto = null;
		}
	}
	
	public List<SelectItem> getListaEmpresas() {
		return listaEmpresas;
	}

	public List<SelectItem> getListaDepartamentos() {
		return listaDepartamentos;
	}

	public List<SelectItem> getListaCentrosDeCusto() {
		return listaCentrosDeCusto;
	}

	public List<SelectItem> getListaCargos() {
		return listaCargos;
	}

	public Long getIdEmpresaSelecionada() {
		return idEmpresaSelecionada;
	}

	public void setIdEmpresaSelecionada(Long idEmpresaSelecionada) {
		this.idEmpresaSelecionada = idEmpresaSelecionada;
	}

	public EmpresaEntity getEmpresaSelecionada() {
		return empresaSelecionada;
	}

	public void setEmpresaSelecionada(EmpresaEntity empresaSelecionada) {
		this.empresaSelecionada = empresaSelecionada;
	}

	public List<SelectItem> getListaEquipamentos() {
		return listaEquipamentos;
	}

}
