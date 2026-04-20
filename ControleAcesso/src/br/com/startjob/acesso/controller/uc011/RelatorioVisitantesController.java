package br.com.startjob.acesso.controller.uc011;

import java.text.SimpleDateFormat;
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
	}
	

	@Override
	public String buscar() {
	    // Como o BaseController e os botões padrão chamam o buscar(), 
	    // nós o direcionamos para a busca da tela por padrão.
	    return buscarTela();
	}
	
	public String buscarTela() {
	    prepararFiltrosComuns();
	    
	    // Para a tela, usamos a query com "SELECT NEW" que retorna a Entidade montada
	    setNamedQueryPesquisa("findAllComPedestreEmpresaECargo");
	    
	    // Deixa o BaseController fazer a paginação nativa
	    return super.buscar();
	}
	
	public String buscarRelatorioVisitantes() {
	    prepararFiltrosComuns();
//		//salva parametros na sessão
	    
	    // Para o relatório, você pode usar a mesma query, OU usar aquela 
	    // query focada em Object[] se for puxar milhares de registros de uma vez.
	    setNamedQueryPesquisa("findAllComPedestreEmpresaECargoOtimizado");
	    
	    // Se o relatório for apenas preencher uma lista na memória para gerar o Excel:
	     try {
			@SuppressWarnings("unchecked")
			List<AcessoEntity> dadosRelatorio = (List<AcessoEntity>)  baseEJB.pesquisaSimples(AcessoEntity.class, getNamedQueryPesquisa(), getParans());
			exportarExcelCustomizado(dadosRelatorio);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
	     return "";
	}


	private void prepararFiltrosComuns() {
		// 1. Cliente logado
		getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());
		getParans().put("pedestre.tipo", TipoPedestre.VISITANTE);

		// 3. Tratamento seguro de Datas
		Object menor = getParans().get("data_menor_data");
		Object maior = getParans().get("data_maior_data");

		if (menor instanceof Date) {
			dataFim = sdf.format((Date) menor);
		} else if (menor != null && !menor.toString().trim().isEmpty()) {
			dataFim = menor.toString();
		} else {
			dataFim = null; // Proteção essencial
			getParans().put("data_menor_data", null);
		}

		if (maior instanceof Date) {
			dataInicio = sdf.format((Date) maior);
		} else if (maior != null && !maior.toString().trim().isEmpty()) {
			dataInicio = maior.toString();
		} else {
			dataInicio = null; // Proteção essencial
			getParans().put("data_maior_data", null);
		}

		// --- 4. PREPARAÇÃO DOS FILTROS DE TEXTO E COMBOS ---

		// Textos: Se o usuário apagar o texto e ficar "", transforma em null
		Object nome = getParans().get("pedestre.nome");
		if (nome != null && nome.toString().trim().isEmpty()) {
			getParans().put("pedestre.nome", null);
		}

		Object equip = getParans().get("equipamento");
		if (equip != null && equip.toString().trim().isEmpty()) {
			getParans().put("equipamento", null);
		}

		// Combos (IDs): Garante que IDs não válidos (0, vazio ou null) virem Null
		limparIdZerado("pedestre.empresa.id");
		limparIdZerado("pedestre.departamento.id");
		limparIdZerado("pedestre.cargo.id");
		limparIdZerado("pedestre.centroCusto.id");
	}

	private void limparIdZerado(String chave) {
	    Object valor = getParans().get(chave);
	    
	    if (valor == null) {
	        getParans().put(chave, null); // Apenas garante que a chave exista
	        return;
	    }
	    
	    if (valor instanceof String && ((String) valor).trim().isEmpty()) {
	        getParans().put(chave, null);
	    } else if (valor instanceof Number && ((Number) valor).longValue() == 0L) {
	        getParans().put(chave, null);
	    }
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
