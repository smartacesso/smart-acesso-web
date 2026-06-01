package br.com.startjob.acesso.controller.uc008;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import com.senior.services.Enum.ModoImportacaoFuncionario;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.ejb.EmpresaEJBRemote;
import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.ImportacaoEntity;
import br.com.startjob.acesso.modelo.entity.LocalEntity;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.PedestreRegraEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.TipoArquivo;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.service.CadastroFacialLinkService;

@Named("consultaPedestreController")
@ViewScoped
@UseCase(classEntidade=PedestreEntity.class, funcionalidade="Consulta pedestres", logicalRemove = true, 
		urlNovoRegistro="/paginas/sistema/pedestres/cadastroPedestre.xhtml", lazyLoad = true, quantPorPagina = 6)
public class ConsultaPedestreController extends BaseController {

	private static final long serialVersionUID = 1L;
	
	private List<SelectItem> listaTiposPedestre;
	private List<SelectItem> listaEmpresas;
	private List<SelectItem> listaCargos;
	private List<SelectItem> listaDepartamentos;
	private List<SelectItem> listaCentrosDeCusto;
	private List<SelectItem> listaTipoArquivo;
	private List<SelectItem> listaLocais;
	
	private String empresaSelecionada;
	private String codFil;
	private Date data;
	private String numCad;
	private String tipCol;

	
	@EJB
	private PedestreEJBRemote pedestreEJB;
	
	@EJB
	private EmpresaEJBRemote empresaEJB;
	
	private String contatoPesquisa;
	
	private String cpfNovoPedestre;
	
	private String acao;
	private String tipo;
	private PedestreEntity pedestreSelecionado;
	
	private UploadedFile uploadedFile;
	private byte[] dados;
	private TipoArquivo tipoArquivo;
	
	private boolean permiteCampoAdicionalCrachaMatricula = true;
	private boolean habilitaAppPedestre = false;

	private String linkGeradoFacial;
	private Long idEmpresaLinkConvite;

	@PostConstruct
	@Override
	public void init() {
	    long t0 = System.currentTimeMillis();
	    super.init();
	    System.out.println("PERF: super.init() demorou " + (System.currentTimeMillis() - t0) + "ms");
	    
	    long tAcao = System.currentTimeMillis();
	    acao = getRequest().getParameter("acao");
	    tipo = getRequest().getParameter("tipo");
	    
	    if(tipo != null && !tipo.isEmpty()) {
	        if("pe".equals(tipo)) {
	            getParans().put("tipo", TipoPedestre.PEDESTRE);
	        } else if("vi".equals(tipo)) {
	            getParans().put("tipo", TipoPedestre.VISITANTE);
	        }
	        setUrlNovoRegistro(getUrlNovoRegistro() + "?tipo=" + tipo);
	    }
	    System.out.println("PERF: Setup parâmetros demorou " + (System.currentTimeMillis() - tAcao) + "ms");
	    
	    long tBusca = System.currentTimeMillis();
	    
	    // --- O PULO DO GATO ---
	    // Apenas chamamos o SEU novo método buscar()! 
	    // Ele vai mapear tudo certinho e avisar o BaseController para usar a Query Otimizada.
	    buscar();
	    // ----------------------
	    
	    System.out.println("PERF: buscar() principal demorou " + (System.currentTimeMillis() - tBusca) + "ms");
	    
	    long tListas = System.currentTimeMillis();
	    montaListaEmpresas();
	    montaListaLocais();
	    montaListaTiposPedestre();
	    System.out.println("PERF: Montar Combos (Empresas/Locais) demorou " + (System.currentTimeMillis() - tListas) + "ms");
	    
	    long tParam = System.currentTimeMillis();
	    ParametroEntity param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.PERMITIR_CAMPO_ADICIONAL_CRACHA,
	            getUsuarioLogado().getCliente().getId());
	    if(param != null)
	        permiteCampoAdicionalCrachaMatricula = Boolean.valueOf(param.getValor());
	    System.out.println("PERF: Buscar Parâmetro Sistema demorou " + (System.currentTimeMillis() - tParam) + "ms");
	    
	    System.out.println("PERF: INIT TOTAL demorou " + (System.currentTimeMillis() - t0) + "ms");
	}
	
	public void excluirPedestre() {
		if(pedestreSelecionado != null) {
			try {
				
				ParametroEntity param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.REMOVE_CARTAO_EXCLUIDO,
						getUsuarioLogado().getCliente().getId());
				if(param != null)
					habilitaAppPedestre = Boolean.valueOf(param.getValor());
				
				if(habilitaAppPedestre) {
					System.out.println("excluiu cartão");
					pedestreSelecionado.setCodigoCartaoAcesso(null);
				}
				pedestreSelecionado.setStatus(Status.INATIVO);
				pedestreSelecionado.setDataRemovido(new Date());
				pedestreSelecionado.setRemovido(true);
				pedestreSelecionado.setStatus(Status.INATIVO);
				pedestreSelecionado.setCodigoCartaoAcesso(null);
				
				baseEJB.alteraObjeto(pedestreSelecionado);
				
				buscar();
				mensagemInfo("", "msg.generica.objeto.excluido.sucesso");
			} catch(Exception e) {
				e.printStackTrace();
				mensagemFatal("", "#Não foi possível excluir este pedestre.");
			}
		}
	}
	
	@Override
	public String limpar() {
		contatoPesquisa = "";
		
		return super.limpar();
	}
	
	@Override
	public void exibeMensagens(ComponentSystemEvent evt) throws AbortProcessingException {
		super.exibeMensagens(evt);
		
		if("OK".equals(acao)) {
			mensagemInfo("", "msg.pedestre.cadastrado.sucesso");
		} else if("OKV".equals(acao)) {
			mensagemInfo("", "msg.visitante.cadastrado.sucesso");
		} else if("ECS".equals(acao)) {
			mensagemInfo("", "msg.pedestre.exluido.sucesso");
		} else if("IMP".equals(acao)) {
			mensagemInfo("", "msg.pedestre.importacao");
		} else if("EIMP".equals(acao)) {
			mensagemFatal("", "msg.pedestre.importacao.erro");
		}
		
		acao = null;
	}
	
	@Override
	public String buscar() {
	    long t0 = System.currentTimeMillis();
	    
	    // Limpa para evitar lixo de pesquisas anteriores
	    getParans().remove("bloco_or");
	    
	    if(contatoPesquisa != null && !contatoPesquisa.trim().isEmpty()) {
	        getParans().put("bloco_or", " (obj.telefone like '%" + contatoPesquisa + "%' or obj.celular like '%" + contatoPesquisa + "%')");
	    }
	    
	    getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());
	    
	    // Garanta que o tipo está no mapa de parâmetros
	    if("pe".equals(tipo)) getParans().put("tipo", TipoPedestre.PEDESTRE);
	    else if("vi".equals(tipo)) getParans().put("tipo", TipoPedestre.VISITANTE);
	    
	    setNamedQueryPesquisa("findAllComEmpresaOtimizado");
	    
	    String retorno = super.buscar();
	    
	    System.out.println("PERF: Tempo dentro do método buscar(): " + (System.currentTimeMillis() - t0) + "ms");
	    return retorno;
	}
	
	private void montaListaTiposPedestre() {
		listaTiposPedestre = new ArrayList<SelectItem>();

		if ("pe".equals(tipo)) {
			listaTiposPedestre.add(new SelectItem(TipoPedestre.PEDESTRE, TipoPedestre.PEDESTRE.toString()));

		} else if ("vi".equals(tipo)) {
			listaTiposPedestre.add(new SelectItem(TipoPedestre.VISITANTE, TipoPedestre.VISITANTE.toString()));

		} else {
			listaTiposPedestre.add(new SelectItem(null, "Filtrar por tipo de pedestre"));
			listaTiposPedestre.add(new SelectItem(TipoPedestre.PEDESTRE, TipoPedestre.PEDESTRE.toString()));
			listaTiposPedestre.add(new SelectItem(TipoPedestre.VISITANTE, TipoPedestre.VISITANTE.toString()));
		}
	}
	
	public void eventoEmpresaSelecionada(ValueChangeEvent event) {
		Long idEmpresaSelecionada = (Long) event.getNewValue();

		if (idEmpresaSelecionada != null) {
			EmpresaEntity empresaSelecionada = buscaEmpresaPeloId(idEmpresaSelecionada);
			
			montaListaDepartamentos(empresaSelecionada);
			montaListaCentroDeCusto(empresaSelecionada);
			montaListaCargo(empresaSelecionada);
			
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
			List<EmpresaEntity> empresas = (List<EmpresaEntity>) empresaEJB.pesquisaArgFixos(EmpresaEntity.class,
					"findByIdComplete", args);

			if (empresas != null && !empresas.isEmpty())
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
	
	private void montaListaDepartamentos(EmpresaEntity empresa) {
		listaDepartamentos = new ArrayList<SelectItem>();
		listaDepartamentos.add(new SelectItem(null, "Selecione"));
		
		empresa.getDepartamentos().forEach(departamento -> {
			listaDepartamentos.add(new SelectItem(departamento.getId(), departamento.getNome()));
		});
	}
	
	private void montaListaCentroDeCusto(EmpresaEntity empresa) {
		listaCentrosDeCusto = new ArrayList<SelectItem>();
		listaCentrosDeCusto.add(new SelectItem(null, "Selecione"));
		
		empresa.getCentros().forEach(centro -> {
			listaCentrosDeCusto.add(new SelectItem(centro.getId(), centro.getNome()));
		});
	}
	
	private void montaListaCargo(EmpresaEntity empresa) {
		listaCargos = new ArrayList<SelectItem>();
		listaCargos.add(new SelectItem(null, "Selecione"));
		
		empresa.getCargos().forEach(cargo -> {
			listaCargos.add(new SelectItem(cargo.getId(), cargo.getNome()));
		});
	}
	
	public void upload(FileUploadEvent event) {
		uploadedFile = event.getFile();
		dados = event.getFile().getContent();
	}
	
	public void iniciarImportacaoArquivo() {
		if(uploadedFile == null) {
			mensagemFatal("", "#O arquivo é obrigatório.");
			return;
		}
		
		String nomeArquivo = uploadedFile.getFileName();
		
		if(!isArquivoAceito(tipoArquivo, nomeArquivo)) {
			return;
		}
		
		try {
			ImportacaoEntity importacao = new ImportacaoEntity();
			importacao.setArquivo(dados);
			importacao.setData(new Date());
			importacao.setUsuario(getUsuarioLogado());
			importacao.setCliente(getUsuarioLogado().getCliente());
			importacao.setNomeArquivo(nomeArquivo);
			
			String separador = null;
			if(tipoArquivo.equals(TipoArquivo.ARQUIVO_CSV)
					|| tipoArquivo.equals(TipoArquivo.ARQUIVO_TXT))
				separador = ";";
			
			else if(tipoArquivo.equals(TipoArquivo.ARQUIVO_TXT_COM_TAB))
				separador = "\t";
			else if(tipoArquivo.equals(TipoArquivo.ARQUIVO_TXT_COM_ESPACO))
				separador = " ";

			pedestreEJB.importarArquivo(importacao, separador);
			
			uploadedFile = null;
			
			redirect("/paginas/sistema/pedestres/pesquisaPedestre.xhtml?acao=IMP");
			
		} catch (Exception e) {
			e.printStackTrace();
			redirect("/paginas/sistema/pedestres/pesquisaPedestre.xhtml?acao=EIMP");
		}
	}
	
	public boolean isArquivoAceito(TipoArquivo tipoArquivo, String nomeArquivo) {
		
		if(tipoArquivo.equals(TipoArquivo.ARQUIVO_CSV) && !nomeArquivo.endsWith(".csv")) {
			mensagemFatal("", "#Arquivo inválido.");
			return false;
		}
		
		if(tipoArquivo.equals(TipoArquivo.ARQUIVO_TXT) && !nomeArquivo.endsWith(".txt")) {
			mensagemFatal("", "#Arquivo inválido.");
			return false;
		}
		
		if(tipoArquivo.equals(TipoArquivo.ARQUIVO_TXT_COM_TAB) && !nomeArquivo.endsWith(".txt")) {
			mensagemFatal("", "#Arquivo inválido.");
			return false;
		}
		
		if(tipoArquivo.equals(TipoArquivo.ARQUIVO_TXT_COM_ESPACO) && !nomeArquivo.endsWith(".txt")) {
			mensagemFatal("", "#Arquivo inválido.");
			return false;
		}
		
		return true;
	}
	
	public void montaListaTipoArquivo() {
		listaTipoArquivo = new ArrayList<SelectItem>();
		
		for(TipoArquivo arquivo : TipoArquivo.values()) {
			listaTipoArquivo.add(new SelectItem(arquivo, arquivo.getDescricao()));
		}
	}
	
	public void executarIntegracaoPedestre() {
	    try {
	    	// Decide o modo de importação a partir da presença da data
	    	ModoImportacaoFuncionario modo;
	    	String dataParaBusca = null;

	    	if (data == null) {
	    	    modo = ModoImportacaoFuncionario.COMPLETA;
	    	} else {
	    	    modo = ModoImportacaoFuncionario.INCREMENTAL;

	    	    // converte java.util.Date para LocalDate
	    	    LocalDate dataLocal = data.toInstant()  // pega o Instant
	    	                               .atZone(ZoneId.systemDefault()) // aplica o fuso local
	    	                               .toLocalDate();               // pega LocalDate

	    	    // formata para dd/MM/yyyy
	    	    dataParaBusca = dataLocal.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	    	}

	    	// chama o EJB
	    	pedestreEJB.importaFuncionariosSenior(
	    	    empresaSelecionada, 
	    	    getUsuarioLogado().getCliente(), 
	    	    modo,
	    	    codFil, 
	    	    dataParaBusca,   // null se completa, dd/MM/yyyy se incremental
	    	    numCad, 
	    	    tipCol
	    	);

	        FacesContext.getCurrentInstance().addMessage(null, 
	            new FacesMessage(FacesMessage.SEVERITY_INFO, "Integração concluída", null));
	    } catch (Exception e) {
	        FacesContext.getCurrentInstance().addMessage(null, 
	            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro: " + e.getMessage(), null));
	    }
	}

	public boolean exibeImportarPedestresSenior() {
	    if (getUsuarioLogado() == null) {
	        return false;
	    }
	    if (getUsuarioLogado().getCliente() == null) {
	        return false;
	    }
	    if (getUsuarioLogado().getCliente().getIntegracaoSenior() == null) {
	        return false;
	    }
	    
	    String url = getUsuarioLogado().getCliente().getIntegracaoSenior().getUrl();
	    return url != null && !url.trim().isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	public void montaListaLocais() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("ID_CLIENTE", getUsuarioLogado().getCliente().getId());

		listaLocais = new ArrayList<SelectItem>();
		listaLocais.add(new SelectItem(null, "Selecione"));

		try {
			List<LocalEntity> locais = (List<LocalEntity>) baseEJB.pesquisaArgFixos(LocalEntity.class,
					"findAllByIdClienteAdd", args);

			if (locais != null && !locais.isEmpty()) {
				locais.forEach(local -> {
					listaLocais.add(new SelectItem(local.getUuid(), local.getNome()));
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String buscaRegra(PedestreEntity pedestre) {

		if (pedestre == null || pedestre.getId() == null) {
			return "";
		}

		Map<String, Object> args = new HashMap<>();
		args.put("ID_PEDESTRE", pedestre.getId());

		List<PedestreRegraEntity> regraAtiva;

		try {
			@SuppressWarnings("unchecked")
			List<PedestreRegraEntity> resultado =
				(List<PedestreRegraEntity>) baseEJB.pesquisaArgFixos(
					PedestreRegraEntity.class,
					"findPedestreRegraAtivo",
					args
				);

			regraAtiva = resultado;

		} catch (Exception e) {
			return "";
		}

		if (regraAtiva == null || regraAtiva.isEmpty()) {
			return "";
		}

		PedestreRegraEntity regra = regraAtiva.get(0);

		if (regra.getRegra() == null || regra.getRegra().getNome() == null) {
			return "";
		}

		return regra.getRegra().getNome();
	}
	
	public PedestreEntity getPedestreSelecionado() {
		return pedestreSelecionado;
	}

	public void setPedestreSelecionado(PedestreEntity pedestreSelecionado) {
		this.pedestreSelecionado = pedestreSelecionado;
	}

	public List<SelectItem> getListaTiposPedestre() {
		return listaTiposPedestre;
	}

	public List<SelectItem> getListaEmpresas() {
		return listaEmpresas;
	}

	public String getContatoPesquisa() {
		return contatoPesquisa;
	}

	public void setContatoPesquisa(String contatoPesquisa) {
		this.contatoPesquisa = contatoPesquisa;
	}

	public String getCpfNovoPedestre() {
		return cpfNovoPedestre;
	}

	public void setCpfNovoPedestre(String cpfNovoPedestre) {
		this.cpfNovoPedestre = cpfNovoPedestre;
	}

	public List<SelectItem> getListaTipoArquivo() {
		return listaTipoArquivo;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}

	public void setTipoArquivo(TipoArquivo tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public byte[] getDados() {
		return dados;
	}

	public void setDados(byte[] dados) {
		this.dados = dados;
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

	public String getTipo() {
		return tipo;
	}

	public boolean isPermiteCampoAdicionalCrachaMatricula() {
		return permiteCampoAdicionalCrachaMatricula;
	}

	public void setPermiteCampoAdicionalCrachaMatricula(boolean permiteCampoAdicionalCrachaMatricula) {
		this.permiteCampoAdicionalCrachaMatricula = permiteCampoAdicionalCrachaMatricula;
	}

	public String getCodFil() {
		return codFil;
	}

	public void setCodFil(String codFil) {
		this.codFil = codFil;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getNumCad() {
		return numCad;
	}

	public void setNumCad(String numCad) {
		this.numCad = numCad;
	}

	public String getTipCol() {
		return tipCol;
	}

	public void setTipCol(String tipCol) {
		this.tipCol = tipCol;
	}

	public PedestreEJBRemote getPedestreEJB() {
		return pedestreEJB;
	}

	public void setPedestreEJB(PedestreEJBRemote pedestreEJB) {
		this.pedestreEJB = pedestreEJB;
	}

	public EmpresaEJBRemote getEmpresaEJB() {
		return empresaEJB;
	}

	public void setEmpresaEJB(EmpresaEJBRemote empresaEJB) {
		this.empresaEJB = empresaEJB;
	}

	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

	public boolean isHabilitaAppPedestre() {
		return habilitaAppPedestre;
	}

	public void setHabilitaAppPedestre(boolean habilitaAppPedestre) {
		this.habilitaAppPedestre = habilitaAppPedestre;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setListaTiposPedestre(List<SelectItem> listaTiposPedestre) {
		this.listaTiposPedestre = listaTiposPedestre;
	}

	public void setListaEmpresas(List<SelectItem> listaEmpresas) {
		this.listaEmpresas = listaEmpresas;
	}

	public void setListaCargos(List<SelectItem> listaCargos) {
		this.listaCargos = listaCargos;
	}

	public void setListaDepartamentos(List<SelectItem> listaDepartamentos) {
		this.listaDepartamentos = listaDepartamentos;
	}

	public void setListaCentrosDeCusto(List<SelectItem> listaCentrosDeCusto) {
		this.listaCentrosDeCusto = listaCentrosDeCusto;
	}

	public void setListaTipoArquivo(List<SelectItem> listaTipoArquivo) {
		this.listaTipoArquivo = listaTipoArquivo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getEmpresaSelecionada() {
		return empresaSelecionada;
	}

	public void setEmpresaSelecionada(String empresaSelecionada) {
		this.empresaSelecionada = empresaSelecionada;
	}

	public List<SelectItem> getListaLocais() {
		return listaLocais;
	}

	public void setListaLocais(List<SelectItem> listaLocais) {
		this.listaLocais = listaLocais;
	}

	public boolean isTelaVisitantes() {
		Object t = getParans().get("tipo");
		return TipoPedestre.VISITANTE.equals(t) || "vi".equals(tipo);
	}

	public Long getFiltroEmpresaId() {
		String chave = isTelaVisitantes() ? "empresaVisitadaRef.id" : "empresa.id";
		Object v = getParans().get(chave);
		if (v instanceof Long) {
			return (Long) v;
		}
		if (v != null && !v.toString().trim().isEmpty()) {
			try {
				return Long.valueOf(v.toString());
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

	public void setFiltroEmpresaId(Long id) {
		if (isTelaVisitantes()) {
			getParans().put("empresaVisitadaRef.id", id);
			getParans().remove("empresa.id");
		} else {
			getParans().put("empresa.id", id);
			getParans().remove("empresaVisitadaRef.id");
		}
	}

	public void abrirDialogLinkConviteVisitante() {
		if (!validarPermissaoGerarLinkCadastroFacial()) {
			return;
		}
		linkGeradoFacial = null;
	}

	public boolean isLinkConviteGerado() {
		return linkGeradoFacial != null && !linkGeradoFacial.trim().isEmpty();
	}

	public List<SelectItem> getListaEmpresasConvite() {
		List<SelectItem> convite = new ArrayList<>();
		if (listaEmpresas == null) {
			return convite;
		}
		for (SelectItem item : listaEmpresas) {
			if (item.getValue() != null) {
				convite.add(item);
			}
		}
		return convite;
	}

	/**
	 * Link para visitante sem cadastro prévio (convite). Empresa escolhida pelo operador web.
	 */
	public void gerarLinkConviteVisitante() {
		if (!validarPermissaoGerarLinkCadastroFacial()) {
			return;
		}

		if (idEmpresaLinkConvite == null) {
			mensagemFatal("", "msg.link.cadastro.facial.empresa.obrigatoria");
			return;
		}

		try {
			CadastroFacialLinkService linkService = new CadastroFacialLinkService(pedestreEJB);
			Long idCliente = getUsuarioLogado().getCliente().getId();
			EmpresaEntity empresa = linkService.buscaEmpresaPorId(idEmpresaLinkConvite, idCliente);
			if (empresa == null) {
				mensagemFatal("", "msg.link.cadastro.facial.empresa.invalida");
				return;
			}

			long token = linkService.calcularTokenValidade(idCliente);
			ClienteEntity cliente = getUsuarioLogado().getCliente();
			linkService.gravarCadastroExternoConvite(cliente, empresa, token, null);

			linkGeradoFacial = linkService.montarUrlConvite(idCliente, empresa.getId(), token);
			mensagemInfo("", "msg.link.cadastro.facial.gerado.sucesso");

		} catch (Exception e) {
			e.printStackTrace();
			mensagemFatal("", "msg.link.cadastro.facial.erro.gravar.token");
		}
	}

	/**
	 * Link para visitante já cadastrado (completar facial). Exige autoAtendimento e celular.
	 */
	public void gerarLinkFacialVisitanteExistente() {
		if (!validarPermissaoGerarLinkCadastroFacial()) {
			return;
		}

		PedestreEntity p = pedestreSelecionado;
		if (p == null || p.getId() == null) {
			mensagemFatal("", "msg.link.cadastro.facial.pedestre.nao.selecionado");
			return;
		}

		if (!TipoPedestre.VISITANTE.equals(p.getTipo())) {
			mensagemFatal("", "msg.link.cadastro.facial.apenas.visitante");
			return;
		}

		if (!Boolean.TRUE.equals(p.getAutoAtendimento())) {
			mensagemFatal("", "msg.link.cadastro.facial.gerar.sem.liberacao");
			return;
		}

		if (p.getCelular() == null || p.getCelular().trim().isEmpty()) {
			mensagemFatal("", "msg.celular.nulo");
			return;
		}

		try {
			CadastroFacialLinkService linkService = new CadastroFacialLinkService(pedestreEJB);
			Long idCliente = getUsuarioLogado().getCliente().getId();
			long token = linkService.calcularTokenValidade(idCliente);

			PedestreEntity completo = buscaPedestreParaLink(p.getId(), idCliente);
			if (completo == null) {
				mensagemFatal("", "msg.link.cadastro.facial.invalido");
				return;
			}

			completo.setAutoAtendimentoAt(new Date());
			pedestreEJB.alteraObjeto(completo);

			linkService.gravarCadastroExternoPrecadastro(completo, getUsuarioLogado().getCliente(), token);
			linkGeradoFacial = linkService.montarUrlPrecadastro(idCliente, completo.getId(), token);

			String celphone = p.getCelular().replace("-", "").replace("(", "").replace(")", "").replace(" ", "");
			String msg = "Olá! Acesse o link para concluir seu cadastro facial:\n" + linkGeradoFacial;
			String encodedMsg = URLEncoder.encode(msg, "UTF-8");
			PrimeFaces.current().executeScript("window.open('" + BaseConstant.URL_WHATSAPP + "55" + celphone + "&text="
					+ encodedMsg + "','whatsAppTab');");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			mensagemFatal("", "msg.link.cadastro.facial.erro.gravar.token");
		}
	}

	@SuppressWarnings("unchecked")
	private PedestreEntity buscaPedestreParaLink(Long id, Long idCliente) {
		Map<String, Object> args = new HashMap<>();
		args.put("ID", id);
		args.put("ID_CLIENTE", idCliente);
		try {
			List<PedestreEntity> lista = (List<PedestreEntity>) pedestreEJB.pesquisaArgFixos(PedestreEntity.class,
					"findByIdPedestreAndIdCliente", args);
			if (lista != null && !lista.isEmpty()) {
				return lista.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getLinkGeradoFacial() {
		return linkGeradoFacial;
	}

	public Long getIdEmpresaLinkConvite() {
		return idEmpresaLinkConvite;
	}

	public void setIdEmpresaLinkConvite(Long idEmpresaLinkConvite) {
		this.idEmpresaLinkConvite = idEmpresaLinkConvite;
	}
}

