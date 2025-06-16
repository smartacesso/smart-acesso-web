package br.com.startjob.acesso.controller.uc009;

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
import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;

@SuppressWarnings("serial")
@Named("relatorioPedestresController")
@ViewScoped
@UseCase(classEntidade=AcessoEntity.class, funcionalidade="Relatório de pedestres", 
		lazyLoad = true, quantPorPagina = 10)
public class RelatorioPedestresController extends RelatorioController {
	
	private Long idEmpresaSelecionada;
	private EmpresaEntity empresaSelecionada;
	
	private List<SelectItem> listaEmpresas;
	private List<SelectItem> listaDepartamentos;
	private List<SelectItem> listaCentrosDeCusto;
	private List<SelectItem> listaCargos;
	private List<SelectItem> listaEquipamentos;
	
	private boolean permiteCampoAdicionalCrachaMatricula = true;
	
	@PostConstruct
	@Override
	public void init() {
		super.init();
		
		getParans().put("data_maior_data", formatarDataInicioDia(new Date()));
		getParans().put("data_menor_data", formatarDataFimDia(new Date()));
		
		buscar();
		listaEmpresas = montaListaEmpresas();
		listaEquipamentos = montaListaEquipamentos();
		
		ParametroEntity param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.PERMITIR_CAMPO_ADICIONAL_CRACHA,
				getUsuarioLogado().getCliente().getId());
		if(param != null)
			permiteCampoAdicionalCrachaMatricula = Boolean.valueOf(param.getValor());
	}
	
	@Override
	public String buscar() {
		getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());
		getParans().put("pedestre.tipo", TipoPedestre.PEDESTRE);
		
		setNamedQueryPesquisa("findAllComPedestreEmpresaECargo");
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
	
	public String getTipoRefeicao(java.sql.Timestamp timestamp) {
	    return getTipoRefeicao((Date) timestamp);
	}

	
	public String getTipoRefeicao(Date dataAcesso) {
	    if (dataAcesso == null) {
	        return "--";
	    }
	    
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(dataAcesso);
	    int hora = cal.get(Calendar.HOUR_OF_DAY);
	    int minuto = cal.get(Calendar.MINUTE);
	    int minutosTotais = hora * 60 + minuto;

	    if (minutosTotais >= 330 && minutosTotais <= 480) { // 05:30 - 08:00
	        return "Lanche Matutino";
	    } else if (minutosTotais >= 600 && minutosTotais <= 750) { // 10:00 - 13:30
	        return "Almoço";
	    } else if (minutosTotais >= 840 && minutosTotais <= 930) { // 14:00 - 15:30
	        return "Lanche Vespertino";
	    } else if (minutosTotais >= 1080 && minutosTotais <= 1260) { // 18:00 - 21:00
	        return "Jantar";
	    } else if (minutosTotais >= 1320 && minutosTotais <= 1410) { // 22:00 - 23:30
	        return "Lanche Noturno";
	    } else if ((minutosTotais >= 60 && minutosTotais <= 240)) { // 01:00 - 04:00
	        return "Ceia";
	    }

	    return "Fora do horário de refeição";
	}


	public Long getIdEmpresaSelecionada() {
		return idEmpresaSelecionada;
	}

	public EmpresaEntity getEmpresaSelecionada() {
		return empresaSelecionada;
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

	public List<SelectItem> getListaEquipamentos() {
		return listaEquipamentos;
	}

	public boolean isPermiteCampoAdicionalCrachaMatricula() {
		return permiteCampoAdicionalCrachaMatricula;
	}
}
