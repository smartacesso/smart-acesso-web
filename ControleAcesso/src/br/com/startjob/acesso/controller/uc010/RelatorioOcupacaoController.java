package br.com.startjob.acesso.controller.uc010;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.RelatorioController;
import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.utils.AppAmbienteUtils;

@SuppressWarnings("serial")
@Named("relatorioOcupacaoController")
@ViewScoped
@UseCase(classEntidade=AcessoEntity.class, funcionalidade="Relatório de ocupação", 
		lazyLoad = true, quantPorPagina = 10)
public class RelatorioOcupacaoController extends RelatorioController {
	
	private List<SelectItem> listaEmpresas;
	private List<SelectItem> listaEquipamentos;
	
	private List<AcessoEntity> listaAcessosHora;
	
	private LineChartModel graficoLinha;
	
	private Date dataSelecionada;
	
	private boolean permiteCampoAdicionalCrachaMatricula = true;

	@PostConstruct
	@Override
	public void init() {
		super.init();

		dataSelecionada = new Date();
		
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
		
		getParans().put("data_maior_data", formatarDataInicioDia(dataSelecionada));
		getParans().put("data_menor_data", formatarDataFimDia(dataSelecionada));
		
		setNamedQueryPesquisa("findAllPedestresSemSaida");
		
		montaGraficoLinha();

		return super.buscar();
	}
	
	@SuppressWarnings("unchecked")
	private List<AcessoEntity> buscaListaComDadosOcupacao(){
		List<AcessoEntity> listaAcessos = null;
		
		try {
			
			String sgdb = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_SGDB);
			
			Map<String,	Object> args = new HashMap<String, Object>();
			args.put("ID_CLIENTE", getUsuarioLogado().getCliente().getId());
			args.put("DATA", dataSelecionada);
			args.put("NOME_PEDESTRE", "plsql".equals(sgdb) 
					? ("%"+getParans().get("pedestre.nome")+"%") : 
						getParans().get("pedestre.nome"));
			args.put("ID_EMPRESA", getParans().get("pedestre.empresa.id"));
			args.put("EQUIPAMENTO", getParans().get("equipamento"));
			
			
			String query = "";
			
			if("plsql".equals(sgdb)) {
				query = "findOcupacaoPorHoraSqlServer";
			}else if("oracle".equals(sgdb)) {
				query = "findOcupacaoPorHoraOracle";
			} else {
				query = "findOcupacaoPorHora";
			}
			
			listaAcessos = (List<AcessoEntity>) 
						baseEJB.pesquisaArgFixos(AcessoEntity.class, query, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return listaAcessos;
	}
	
	private void montaGraficoLinha() {
		graficoLinha = new LineChartModel();
		graficoLinha.setSeriesColors("007ad9");
		graficoLinha.setExtender("customExtender");
		
		listaAcessosHora = buscaListaComDadosOcupacao();
		
		DateAxis axis = new DateAxis("Horas");
	    axis.setTickAngle(-50);
	    axis.setMin("00:00");
	    axis.setMax("23:00");
	    axis.setTickFormat("%H:%M");
	    axis.setTickInterval("3600");
	    
	    Axis y = graficoLinha.getAxis(AxisType.Y);
		y.setLabel("Quantidade");
		y.setMin(0);
		y.setTickInterval("1");

		LineChartSeries s = new LineChartSeries();
		s.setLabel("Pedestres");
		graficoLinha.getAxes().put(AxisType.X, axis);
		s.setSmoothLine(true);
		
		for (int i = 0; i < 24; i++) {
			boolean addZero = true;
			
			for (AcessoEntity item : listaAcessosHora) {
				if(item.getHora().intValue() == i) {
					s.set(item.getHora() + ":00", item.getQtdePedestresHora());
					addZero = false;
					break;
				}
			}
			
			if(addZero) {
				s.set(i + ":00", 0);
			}
		}
		graficoLinha.addSeries(s);
		graficoLinha.setLegendPosition("e");
		graficoLinha.setAnimate(true);
	}

	public List<SelectItem> getListaEmpresas() {
		return listaEmpresas;
	}

	public LineChartModel getGraficoLinha() {
		return graficoLinha;
	}

	public List<AcessoEntity> getListaAcessosHora() {
		return listaAcessosHora;
	}

	public Date getDataSelecionada() {
		return dataSelecionada;
	}

	public void setDataSelecionada(Date dataSelecionada) {
		this.dataSelecionada = dataSelecionada;
	}

	public List<SelectItem> getListaEquipamentos() {
		return listaEquipamentos;
	}

	public boolean isPermiteCampoAdicionalCrachaMatricula() {
		return permiteCampoAdicionalCrachaMatricula;
	}
}
