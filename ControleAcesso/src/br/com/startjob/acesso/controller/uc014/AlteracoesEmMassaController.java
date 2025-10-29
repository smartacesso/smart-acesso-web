package br.com.startjob.acesso.controller.uc014;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.ejb.EmpresaEJBRemote;
import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.PedestreRegraEntity;
import br.com.startjob.acesso.modelo.entity.RegraEntity;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.enumeration.TipoRegra;

@SuppressWarnings("serial")
@Named("alteracoesEmMassaController")
@ViewScoped
@UseCase(classEntidade=PedestreEntity.class, funcionalidade="Alterações em massa", logicalRemove = true, 
		lazyLoad = true, quantPorPagina = 10)
public class AlteracoesEmMassaController extends BaseController{

	private String acao;
	
	private Long idEmpresaSelecionada;
	
	private EmpresaEntity empresaSelecionada;
	
	private boolean marcarTodos = true;

	private PedestreRegraEntity pedestreRegraAlteracao;
	
	private RegraEntity regraAlteracao;
	
	private Long idEmpresaAlteracao;
	private Long idDepartamentoAlteracao;
	private Long idCentroCustoAlteracao;
	private Long idCargoAlteracao;
	
	@EJB
	protected EmpresaEJBRemote empresaEJB;
	
	@EJB
	protected PedestreEJBRemote pedestreEJB;
	
	private List<SelectItem> listaEmpresas;
	private List<SelectItem> listaCargos;
	private List<SelectItem> listaDepartamentos;
	private List<SelectItem> listaCentrosDeCusto;
	private List<SelectItem> listaTipoRegra;
	
	private boolean permiteCampoAdicionalCrachaMatricula = true;
	
	private Date dataInicioJustificativa;
	private Date dataFimJustificativa;
	private String justificativa;
	
	@PostConstruct
	@Override
	public void init() {
		baseEJB = empresaEJB;
		
		super.init();
		buscar();
		
		montaListaEmpresas();
		
		acao = getRequest().getParameter("acao");
		pedestreRegraAlteracao = new PedestreRegraEntity();
		
		ParametroEntity param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.PERMITIR_CAMPO_ADICIONAL_CRACHA,
				getUsuarioLogado().getCliente().getId());
		if(param != null)
			permiteCampoAdicionalCrachaMatricula = Boolean.valueOf(param.getValor());
	}
	
	@Override
	public void exibeMensagens(ComponentSystemEvent evt) throws AbortProcessingException {
		super.exibeMensagens(evt);
		
		if("OK".equals(acao)) {
			mensagemInfo("", "msg.uc014.alteracao.realizada");
		}
		
		acao = null;
	}
	
	@Override
	public String buscar() {
		getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());
		setNamedQueryPesquisa("findAllPedestresComEmpresa");
		return super.buscar();
	}
	
	public void iniciarAlteracaoEmMassa() {
		Long idCliente = getUsuarioLogado().getCliente().getId();
		pedestreRegraAlteracao.setRegra(regraAlteracao);
		
		pedestreEJB.alterarEmMassa(pedestreRegraAlteracao, idEmpresaAlteracao, idDepartamentoAlteracao, 
				idCentroCustoAlteracao, idCargoAlteracao, idCliente, getParans());
		
		buscar();
		
		mensagemInfo("", "#Alteração em massa executada com sucesso.");
	}
	
	
	public void salvarJustificativa() {
		System.out.println("teste");
		
		
		Long idCliente = getUsuarioLogado().getCliente().getId();
		pedestreRegraAlteracao.setRegra(regraAlteracao);
		
		pedestreEJB.salvarJustificativa(idCliente, dataInicioJustificativa, dataFimJustificativa, justificativa, getParans());
		
		buscar();
		
		mensagemInfo("", "#Alteração em massa executada com sucesso.");
	}
	
	public void acaoMarcarTodos(ValueChangeEvent value) {
		marcarTodos = (boolean) value.getNewValue();
		
		pedestreEJB.marcarOuDesmarcarTodos(marcarTodos, getParans(), getUsuarioLogado().getCliente().getId());
		
		buscar();
	}
	
	public void alteraValorCampoAlterarEmMassaPedestre(PedestreEntity pedestre) {
		try {
			baseEJB.alteraObjeto(pedestre);
			pedestre.setVersao(pedestre.getVersao() + 1);

		} catch (Exception e) {
			mensagemFatal("", "msg.fatal.nao.foi.possivel.salvar.pedestre");
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<RegraEntity> buscarRegraAutoComplete(String nome) {
		List<RegraEntity> regras = null;
		
		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("NOME", "%"+nome+"%");
			args.put("TIPO_PEDESTRE", TipoPedestre.PEDESTRE);
			args.put("ID_CLIENTE", getUsuarioLogado().getCliente().getId());
			
			regras = (List<RegraEntity>) baseEJB.pesquisaArgFixos(RegraEntity.class, "findAllByNome", args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return regras;
	}
	
	public void eventoEmpresaSelecionada(ValueChangeEvent event) {
		idEmpresaSelecionada = (Long) event.getNewValue();

		if (idEmpresaSelecionada != null) {
			empresaSelecionada = buscaEmpresaPeloId(idEmpresaSelecionada);
			
			montaListaDepartamentos();
			montaListaCentroDeCusto();
			montaListaCargo();
			
		} else {
			listaDepartamentos = null;
			listaCargos = null;
			listaCentrosDeCusto = null;
		}
	}
	
	public EmpresaEntity buscaEmpresaPeloId(Long id) {
		EmpresaEntity empresa = null;
		
		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("ID", id);

			@SuppressWarnings("unchecked")
			List<EmpresaEntity> empresas = (List<EmpresaEntity>) 
					baseEJB.pesquisaArgFixos(EmpresaEntity.class, "findByIdComplete", args);
			
			if(empresas != null && !empresas.isEmpty())
				empresa = empresas.get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return empresa;
	}
	
	@SuppressWarnings("unchecked")
	public void montaListaEmpresas() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("ID_CLIENTE", getUsuarioLogado().getCliente().getId());

		listaEmpresas = new ArrayList<SelectItem>();
		listaEmpresas.add(new SelectItem(null, "Filtrar por empresa"));

		try {
			List<EmpresaEntity> empresas = (List<EmpresaEntity>) baseEJB.pesquisaArgFixos(EmpresaEntity.class,
					"findAllByIdCliente2", args);

			if (empresas != null && !empresas.isEmpty()) {
				empresas.forEach(empresa -> {
					listaEmpresas.add(new SelectItem(empresa.getId(), empresa.getNome()));
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void montaListaDepartamentos() {
		listaDepartamentos = new ArrayList<SelectItem>();
		listaDepartamentos.add(new SelectItem(null, "Selecione"));
		
		empresaSelecionada.getDepartamentos().forEach(departamento -> {
			listaDepartamentos.add(new SelectItem(departamento.getId(), departamento.getNome()));
		});
	}
	
	public void montaListaCentroDeCusto() {
		listaCentrosDeCusto = new ArrayList<SelectItem>();
		listaCentrosDeCusto.add(new SelectItem(null, "Selecione"));
		
		empresaSelecionada.getCentros().forEach(centro -> {
			listaCentrosDeCusto.add(new SelectItem(centro.getId(), centro.getNome()));
		});
	}
	
	public void montaListaCargo() {
		listaCargos = new ArrayList<SelectItem>();
		listaCargos.add(new SelectItem(null, "Selecione"));
		
		empresaSelecionada.getCargos().forEach(cargo -> {
			listaCargos.add(new SelectItem(cargo.getId(), cargo.getNome()));
		});
	}
	
	public void montaListaTipoRegra() {
		listaTipoRegra = new ArrayList<SelectItem>();
		listaTipoRegra.add(new SelectItem(null, "Selecione"));
		
		listaTipoRegra.add(new SelectItem(TipoRegra.ACESSO_CREDITO, TipoRegra.ACESSO_CREDITO.getDescricao()));
		listaTipoRegra.add(new SelectItem(TipoRegra.ACESSO_ESCALA, TipoRegra.ACESSO_ESCALA.getDescricao()));
		listaTipoRegra.add(new SelectItem(TipoRegra.ACESSO_HORARIO, TipoRegra.ACESSO_HORARIO.getDescricao()));
		listaTipoRegra.add(new SelectItem(TipoRegra.ACESSO_PERIODO, TipoRegra.ACESSO_PERIODO.getDescricao()));
		listaTipoRegra.add(new SelectItem(TipoRegra.ACESSO_UNICO, TipoRegra.ACESSO_UNICO.getDescricao()));
		listaTipoRegra.add(new SelectItem(TipoRegra.ACESSO_ESCALA_3_3, TipoRegra.ACESSO_ESCALA_3_3.getDescricao()));
	}
	
	public List<SelectItem> getListaEmpresas() {
		return listaEmpresas;
	}

	public List<SelectItem> getListaCargos() {
		return listaCargos;
	}

	public List<SelectItem> getListaDepartamentos() {
		return listaDepartamentos;
	}

	public List<SelectItem> getListaCentrosDeCusto() {
		return listaCentrosDeCusto;
	}

	public List<SelectItem> getListaTipoRegra() {
		return listaTipoRegra;
	}

	public Long getIdEmpresaAlteracao() {
		return idEmpresaAlteracao;
	}

	public void setIdEmpresaAlteracao(Long idEmpresaAlteracao) {
		this.idEmpresaAlteracao = idEmpresaAlteracao;
	}

	public Long getIdDepartamentoAlteracao() {
		return idDepartamentoAlteracao;
	}

	public void setIdDepartamentoAlteracao(Long idDepartamentoAlteracao) {
		this.idDepartamentoAlteracao = idDepartamentoAlteracao;
	}

	public Long getIdCentroCustoAlteracao() {
		return idCentroCustoAlteracao;
	}

	public void setIdCentroCustoAlteracao(Long idCentroCustoAlteracao) {
		this.idCentroCustoAlteracao = idCentroCustoAlteracao;
	}

	public Long getIdCargoAlteracao() {
		return idCargoAlteracao;
	}

	public void setIdCargoAlteracao(Long idCargoAlteracao) {
		this.idCargoAlteracao = idCargoAlteracao;
	}

	public PedestreRegraEntity getPedestreRegraAlteracao() {
		return pedestreRegraAlteracao;
	}

	public void setPedestreRegraAlteracao(PedestreRegraEntity pedestreRegraAlteracao) {
		this.pedestreRegraAlteracao = pedestreRegraAlteracao;
	}

	public boolean isMarcarTodos() {
		return marcarTodos;
	}

	public void setMarcarTodos(boolean marcarTodos) {
		this.marcarTodos = marcarTodos;
	}

	public RegraEntity getRegraAlteracao() {
		return regraAlteracao;
	}

	public void setRegraAlteracao(RegraEntity regraAlteracao) {
		this.regraAlteracao = regraAlteracao;
	}

	public boolean isPermiteCampoAdicionalCrachaMatricula() {
		return permiteCampoAdicionalCrachaMatricula;
	}

	public Date getDataInicioJustificativa() {
		return dataInicioJustificativa;
	}

	public void setDataInicioJustificativa(Date dataInicioJustificativa) {
		this.dataInicioJustificativa = dataInicioJustificativa;
	}

	public Date getDataFimJustificativa() {
		return dataFimJustificativa;
	}

	public void setDataFimJustificativa(Date dataFimJustificativa) {
		this.dataFimJustificativa = dataFimJustificativa;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}
	
}
