package br.com.startjob.acesso.controller.uc009;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
	
	
	 // Constantes dos nomes das refeições
    public static final String CAFE_MANHA = "Cafe da Manha";
    public static final String CAFE_MANHA2 = "Cafe da Manha 2";
    public static final String ALMOCO = "Almoco";
    public static final String CAFE_TARDE = "Cafe da Tarde";
    public static final String CAFE_TARDE2 = "Cafe da Tarde 2";
    public static final String CAFE_TARDE_SEXTA = "Cafe da Tarde (Sexta)";
    public static final String CAFE_TARDE2_SEXTA = "Cafe da Tarde 2 (Sexta)";
    public static final String LANCHE_EXTRA = "Lanche Hora Extra";
    public static final String JANTAR = "Jantar";
    public static final String LANCHE_NOITE = "Lanche da Noite";
	private static final String LANCHE_EXTRA_NOITE = "Lanche extre noite";

	
	private Long idEmpresaSelecionada;
	private EmpresaEntity empresaSelecionada;
	
	private List<SelectItem> listaEmpresas;
	private List<SelectItem> listaDepartamentos;
	private List<SelectItem> listaCentrosDeCusto;
	private List<SelectItem> listaCargos;
	private List<SelectItem> listaEquipamentos;
	
	private Map<String, Long> refeicoesCount;
	private long totalRefeicoes;
	
	private boolean permiteCampoAdicionalCrachaMatricula = true;
	
	private String dataInicio;
	private String dataFim;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
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
		
		   Object menor = getParans().get("data_menor_data");
		    Object maior = getParans().get("data_maior_data");

		    if (menor instanceof Date) {
		        dataFim = sdf.format((Date) menor);
		    } else {
		        dataFim = menor.toString();
		    }

		    if (maior instanceof Date) {
		        dataInicio = sdf.format((Date) maior);
		    } else {
		        dataInicio = maior.toString();
		    }
		
		
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

	    boolean isSexta = (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY);
	    
	    if(Objects.isNull(empresaSelecionada)) {
	    	return "";
	    }
	    
	    
	    if (empresaSelecionada.getNome().contains("ALVO")) {

	        // Café da manhã: 06:30 – 06:55 (390 – 415)
	        if (minutosTotais >= 390 && minutosTotais <= 415) {
	            return CAFE_MANHA;
	        }

	        // Almoço: 11:00 – 13:00 (660 – 780)
	        if (minutosTotais >= 660 && minutosTotais <= 780) {
	            return ALMOCO;
	        }

	        // Café da tarde: 14:30 – 15:00 (870 – 900)
	        if (minutosTotais >= 870 && minutosTotais <= 900) {
	            return CAFE_TARDE;
	        }

	        // Jantar: 18:30 – 19:30 (1110 – 1170)
	        if (minutosTotais >= 1110 && minutosTotais <= 1170) {
	            return JANTAR;
	        }

	        // Lanche noturno: 22:30 – 22:40 (1350 – 1360)
	        if (minutosTotais >= 1350 && minutosTotais <= 1360) {
	            return LANCHE_NOITE;
	        }

	        // Lanche hora extra noturno: 00:00 – 02:00 (0 – 120)
	        if (minutosTotais >= 0 && minutosTotais <= 120) {
	            return LANCHE_EXTRA_NOITE;
	        }

	        return "Fora do horario de refeicao";
	    }

	    
	    
		if (empresaSelecionada.getNome().contains("RONA")) {

			// Café da manhã: 05:40 – 06:59 (340 – 419)
			if (minutosTotais >= 340 && minutosTotais <= 419) {
				return CAFE_MANHA;
			}

			// Café da manhã 2: 07:30 – 10:00 (450 – 600)
			if (minutosTotais >= 450 && minutosTotais <= 600) {
				return CAFE_MANHA2;
			}

			// Almoço: 10:50 – 13:30 (650 – 810)
			if (minutosTotais >= 650 && minutosTotais <= 810) {
				return ALMOCO;
			}

			if (isSexta) {
				// Na sexta: Café da tarde (13:25 – 15:25)
				if (minutosTotais >= 865 && minutosTotais <= 925) {
					return CAFE_TARDE_SEXTA;
				}

				// Na sexta: Café da tarde 2 (15:30 – 15:59)
				if (minutosTotais >= 990 && minutosTotais <= 959) {
					return CAFE_TARDE2_SEXTA;
				}
			} else {
				// Café da tarde normal: 14:25 – 16:25 (865 – 985)
				if (minutosTotais >= 865 && minutosTotais <= 985) {
					return CAFE_TARDE;
				}

				// Café da tarde 2 normal: 16:30 – 16:59 (990 – 1019)
				if (minutosTotais >= 990 && minutosTotais <= 1019) {
					return CAFE_TARDE2;
				}
			}

			// Lanche hora extra: 18:00 – 18:20 (1080 – 1100)
			if (minutosTotais >= 1080 && minutosTotais <= 1100) {
				return LANCHE_EXTRA;
			}

			// Jantar: 18:30 – 19:30 (1110 – 1170)
			if (minutosTotais >= 1110 && minutosTotais <= 1170) {
				return JANTAR;
			}

			// Lanche da noite: 22:30 – 22:50 (1350 – 1370)
			if (minutosTotais >= 1350 && minutosTotais <= 1370) {
				return LANCHE_NOITE;
			}

			return "Fora do horario de refeicao";
		}
		
		return "";
	}

	public void calcularRefeicoes() {
	    refeicoesCount = new LinkedHashMap<>();

	    List<AcessoEntity> acessos = super.getResult().stream()
	        .map(e -> (AcessoEntity) e)
	        .filter(e -> "ENTRADA".equalsIgnoreCase(e.getSentido())
	                && "ATIVO".equalsIgnoreCase(e.getTipo()))
	        .collect(Collectors.toList());

	    boolean isSexta = false;
	    if (!acessos.isEmpty()) {
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(acessos.get(0).getData());
	        isSexta = (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY);
	    }

	    // Identifica empresa
	    if(Objects.isNull(empresaSelecionada)) {
	    	return;
	    }
	    
	    
	    String nomeEmpresa = empresaSelecionada.getNome().toUpperCase();

	    if (nomeEmpresa.contains("ALVO")) {
	        // Intervalos da ALVO
	        refeicoesCount.put(CAFE_MANHA, 0L);
	        refeicoesCount.put(ALMOCO, 0L);
	        refeicoesCount.put(CAFE_TARDE, 0L);
	        refeicoesCount.put(JANTAR, 0L);
	        refeicoesCount.put(LANCHE_NOITE, 0L);
	        refeicoesCount.put(LANCHE_EXTRA_NOITE, 0L);

	    } else if (nomeEmpresa.contains("RONA")) {
	        // Intervalos da RONA
	        refeicoesCount.put(CAFE_MANHA, 0L);
	        refeicoesCount.put(CAFE_MANHA2, 0L);
	        refeicoesCount.put(ALMOCO, 0L);

	        if (isSexta) {
	            refeicoesCount.put(CAFE_TARDE_SEXTA, 0L);
	            refeicoesCount.put(CAFE_TARDE2_SEXTA, 0L);
	        } else {
	            refeicoesCount.put(CAFE_TARDE, 0L);
	            refeicoesCount.put(CAFE_TARDE2, 0L);
	        }

	        refeicoesCount.put(LANCHE_EXTRA, 0L);
	        refeicoesCount.put(JANTAR, 0L);
	        refeicoesCount.put(LANCHE_NOITE, 0L);
	    } else {
	        // Empresa sem configuração específica
	        refeicoesCount.put("DESCONHECIDA", 0L);
	    }

	    // Contagem
	    long total = 0L;
	    for (AcessoEntity acesso : acessos) {
	        String refeicao = getTipoRefeicao(acesso.getData());
	        if (refeicoesCount.containsKey(refeicao)) {
	            refeicoesCount.put(refeicao, refeicoesCount.get(refeicao) + 1);
	            total++;
	        }
	    }

	    this.totalRefeicoes = total;
	}

	public Map<String, Long> getRefeicoesCount() {
		calcularRefeicoes();
		return refeicoesCount;
	}

	public long getTotalRefeicoes() {
	    return totalRefeicoes;
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

	public String getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(String dataInicio) {
		this.dataInicio = dataInicio;
	}

	public String getDataFim() {
		return dataFim;
	}

	public void setDataFim(String dataFim) {
		this.dataFim = dataFim;
	}
}
