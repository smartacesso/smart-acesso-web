package br.com.startjob.acesso.controller.uc008;

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

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.ejb.EmpresaEJBRemote;
import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.ImportacaoEntity;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.TipoArquivo;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;

@Named("consultaPedestreController")
@ViewScoped
@UseCase(classEntidade=PedestreEntity.class, funcionalidade="Consulta pedestres", logicalRemove = true, 
		urlNovoRegistro="/paginas/sistema/pedestres/cadastroPedestre.xhtml", lazyLoad = true, quantPorPagina = 10)
public class ConsultaPedestreController extends BaseController {

	private static final long serialVersionUID = 1L;
	
	private List<SelectItem> listaTiposPedestre;
	private List<SelectItem> listaEmpresas;
	private List<SelectItem> listaCargos;
	private List<SelectItem> listaDepartamentos;
	private List<SelectItem> listaCentrosDeCusto;
	private List<SelectItem> listaTipoArquivo;
	
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

	@PostConstruct
	@Override
	public void init() {
		super.init();
		
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
		
		buscar();
		montaListaEmpresas();
		montaListaTiposPedestre();
		
		tipoArquivo = TipoArquivo.ARQUIVO_TXT;
		montaListaTipoArquivo();
		
		ParametroEntity param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.PERMITIR_CAMPO_ADICIONAL_CRACHA,
				getUsuarioLogado().getCliente().getId());
		if(param != null)
			permiteCampoAdicionalCrachaMatricula = Boolean.valueOf(param.getValor());
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
		if(contatoPesquisa != null && !"".equals(contatoPesquisa)) {
			getParans().put("bloco_or", " (obj.telefone like '%" + contatoPesquisa 
					+ "%' or obj.celular like '%" + contatoPesquisa + "%')");
		}
		
		getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());
		setNamedQueryPesquisa("findAllComEmpresa");
		return super.buscar();
	}
	
	private void montaListaTiposPedestre() {
		listaTiposPedestre = new ArrayList<SelectItem>();
		
		if("pe".equals(tipo)) {
			listaTiposPedestre.add(new SelectItem(TipoPedestre.PEDESTRE, TipoPedestre.PEDESTRE.toString()));
		
		} else if("vi".equals(tipo)) {
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
			List<EmpresaEntity> empresas = (List<EmpresaEntity>) 
					empresaEJB.pesquisaArgFixos(EmpresaEntity.class, "findByIdComplete", args);
			
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
}

