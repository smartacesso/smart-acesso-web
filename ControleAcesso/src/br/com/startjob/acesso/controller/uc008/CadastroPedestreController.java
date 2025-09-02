package br.com.startjob.acesso.controller.uc008;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.imageio.stream.FileImageOutputStream;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CaptureEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.CroppedImage;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.api.WebSocketCadastroEndpoint;
import br.com.startjob.acesso.controller.CadastroBaseController;
import br.com.startjob.acesso.controller.MenuController;
import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.ejb.PedestreEJB;
import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.BiometriaEntity;
import br.com.startjob.acesso.modelo.entity.CadastroExternoEntity;
import br.com.startjob.acesso.modelo.entity.CargoEntity;
import br.com.startjob.acesso.modelo.entity.CentroCustoEntity;
import br.com.startjob.acesso.modelo.entity.DepartamentoEntity;
import br.com.startjob.acesso.modelo.entity.DocumentoEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.EnderecoEntity;
import br.com.startjob.acesso.modelo.entity.EquipamentoEntity;
import br.com.startjob.acesso.modelo.entity.HistoricoCotaEntity;
import br.com.startjob.acesso.modelo.entity.HorarioEntity;
import br.com.startjob.acesso.modelo.entity.MensagemEquipamentoEntity;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEquipamentoEntity;
import br.com.startjob.acesso.modelo.entity.PedestreRegraEntity;
import br.com.startjob.acesso.modelo.entity.RegraEntity;
import br.com.startjob.acesso.modelo.entity.ResponsibleEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.enumeration.Genero;
import br.com.startjob.acesso.modelo.enumeration.PerfilAcesso;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.StatusCadastroExterno;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.enumeration.TipoQRCode;
import br.com.startjob.acesso.modelo.enumeration.TipoRegra;
import br.com.startjob.acesso.modelo.utils.AppAmbienteUtils;
import br.com.startjob.acesso.modelo.utils.EncryptionUtils;
import br.com.startjob.acesso.modelo.utils.MailUtils;
import br.com.startjob.acesso.to.WebSocketPedestrianAccessTO;
import br.com.startjob.acesso.utils.ResourceBundleUtils;
import br.com.startjob.acesso.utils.Utils;

@SuppressWarnings("serial")
@Named("cadastroPedestreController")
@ViewScoped
@UseCase(classEntidade = PedestreEntity.class, funcionalidade = "Cadastro de pedestres", urlNovoRegistro = "/paginas/sistema/regras/cadastroRegra.jsf", queryEdicao = "findByIdComplete")
public class CadastroPedestreController extends CadastroBaseController {

	@EJB
	private PedestreEJBRemote pedestreEJB;

	private String fileNameTemp;

	private String caminhoCompleto;

	private CroppedImage croppedImage;

	private byte[] arquivoDocumento;

	private List<SelectItem> listaStatus;
	private List<SelectItem> listaGenero;

	private List<SelectItem> listaTipoUsario;
	private List<SelectItem> listaEmpresas;
	private List<SelectItem> listaDepartamentos;
	private List<SelectItem> listaCentrosDeCusto;
	private List<SelectItem> listaCargos;
	private List<SelectItem> listaTipoRegra;
	private List<SelectItem> listaTipoQRCode;

	private Long idEmpresaSelecionada;

	private DocumentoEntity documento;
	private List<DocumentoEntity> listaDocumentos;

	private MensagemEquipamentoEntity mensagemEquipamento;
	private List<MensagemEquipamentoEntity> listaMensagensEquipamento;

	private List<SelectItem> listaEquipamentosDisponiveis;
	private PedestreEquipamentoEntity pedestreEquipamento;
	private List<PedestreEquipamentoEntity> listaPedestresEquipamentos;

	private List<EquipamentoEntity> equipamentos;
	private Long idEquipamentoSelecionado;
	
	private List<HistoricoCotaEntity> listaCotas;
	private Integer mes;
	private Integer ano;
	private Long cotaMensal;


	private BiometriaEntity biometria;
	private StreamedContent imagemBiometria;

	private String cpfNovoPedestre;

	private PedestreRegraEntity pedestreRegra;
	
	private List<ResponsibleEntity> responsaveis;
	private ResponsibleEntity responsavel;

	private List<PedestreRegraEntity> listaPedestreRegra;

	private PedestreEntity pedestreComCartaoAcesoExistente;

	private boolean exibeCrop;

	private String linkCadastroFacialExterno;
	private Long tokenCadastroFacialExterno;
	private int diasValidadeLinkCadastroFacialExterno;

	private boolean matriculaSequencial = false;
	private boolean permiteCampoAdicionalCrachaMatricula = true;
	private boolean validarMatriculasDuplicadas = false;
	private boolean validarCPFDuplicado = false;
	private boolean validarRGDuplicado = false;
	private boolean validarCartaoAcessoDuplicado = false;
	private boolean cadastroEmLote = false;
	private boolean permitirAcessoViaQrCode = false;
	private boolean habilitaTiposQRCode = false;
	private boolean exibeCampoSempreLiberadoParaTodos = false;
	private boolean exibeCampoLinkCadastroFacialExterno = false;
	private boolean habilitaAppPedestre = false;
	private boolean gerarCartaoAutomatico = false;

	private TipoQRCode tipoPadraoQrCode = null;

	private String chaveDeIntegracaoComtele;

	private Integer qtdeDigitosCartao = 10;

	private String camposObrigatorios;

	private String origem;
	private String tipo;

	private String acao;

	private CadastroExternoEntity ultimoCadastroExterno;

	private MenuController menuController = new MenuController();
	
	private AcessoEntity relatorio = new AcessoEntity(); // para binding do formulário
	private List<AcessoEntity> listaRelatorios = new ArrayList<>();
	
	private String linkGerado;
	private final Gson gson = new Gson();
	
	@PostConstruct
	@Override
	public void init() {
		baseEJB = pedestreEJB;

		super.init();

		PedestreEntity pedestre = getPedestreAtual();
		
		getParans().put("data_maior_data", ajustarDataInicio(new Date()));
		getParans().put("data_menor_data", ajustarDataFim(new Date()));
		
		acao = getRequest().getParameter("acao");
		origem = getRequest().getParameter("origem");
		tipo = getRequest().getParameter("tipo");

		iniciaListas();

		buscaConfiguracoes();

		if (pedestre.getId() == null)
			iniciaVariaveisNovoPedestre(pedestre);

		if (pedestre.getId() != null)
			iniciaVariaveisEditarPedestre(pedestre);

		if (habilitaTiposQRCode)
			montaTipoQRCode();

		montaListaTipoUsuario();
		montaListaStatus();
		montaListaGenero();

		montaListaEmpresas();
		montaListaEquipamentosDisponiveis();
		
		pedestre.setCodigoCartaoAcesso(gerarCartao(pedestre));
	}

	private void montaTipoQRCode() {
		listaTipoQRCode = new ArrayList<SelectItem>();
		listaTipoQRCode.add(new SelectItem(TipoQRCode.ESTATICO, TipoQRCode.ESTATICO.toString()));
		listaTipoQRCode.add(new SelectItem(TipoQRCode.DINAMICO_TEMPO, TipoQRCode.DINAMICO_TEMPO.toString()));
		listaTipoQRCode.add(new SelectItem(TipoQRCode.DINAMICO_USO, TipoQRCode.DINAMICO_USO.toString()));

	}

	@Override
	public void exibeMensagens(ComponentSystemEvent evt) throws AbortProcessingException {
		super.exibeMensagens(evt);

		if ("EQC".equalsIgnoreCase(acao)) {
			PedestreEntity p = (PedestreEntity) getEntidade();
			if (TipoQRCode.DINAMICO_TEMPO.equals(p.getTipoQRCode()))
				mensagemInfo("", "msg.qrcode.dinamico.tempo.gerado.sucesso");
			else
				PrimeFaces.current().executeScript("PF('verQrCode').show();");
		}

		else if ("QCA".equalsIgnoreCase(acao))
			mensagemInfo("", "msg.qrcode.apagado.sucesso");

		else if (acao != null)
			mensagemInfo("", "msg.pedestre.cadastrado.sucesso");

		acao = null;
	}

	private void buscaConfiguracoes() {
		ParametroEntity param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.GERAR_MATRICULA_SEQUENCIAL,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			matriculaSequencial = Boolean.valueOf(param.getValor());

		param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.ESCOLHER_QTDE_DIGITOS_CARTAO,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			qtdeDigitosCartao = Integer.valueOf(param.getValor());

		param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.PERMITIR_CAMPO_ADICIONAL_CRACHA,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			permiteCampoAdicionalCrachaMatricula = Boolean.valueOf(param.getValor());

		param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.CAMPOS_OBRIGATORIOS_CADASTRO_PEDESTRE,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			camposObrigatorios = param.getValor();

		param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.VALIDAR_MATRICULAS_DUPLICADAS,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			validarMatriculasDuplicadas = Boolean.valueOf(param.getValor());

		param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.VALIDAR_CPF_DUPLICADO,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			validarCPFDuplicado = Boolean.valueOf(param.getValor());

		param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.VALIDAR_RG_DUPLICADO,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			validarRGDuplicado = Boolean.valueOf(param.getValor());

		param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.VALIDAR_CARTAO_ACESSO_DUPLICADO,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			validarCartaoAcessoDuplicado = Boolean.valueOf(param.getValor());

		param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.CADASTRO_EM_LOTE,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			cadastroEmLote = Boolean.valueOf(param.getValor());

		param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.PERMITIR_ACESSO_QR_CODE,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			permitirAcessoViaQrCode = Boolean.valueOf(param.getValor());

		param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.TIPO_QR_CODE,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			habilitaTiposQRCode = Boolean.valueOf(param.getValor());

		param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.TIPO_QR_CODE_PADRAO,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			tipoPadraoQrCode = TipoQRCode.valueOf(param.getValor());

		param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.CHAVE_DE_INTEGRACAO_COMTELE,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			chaveDeIntegracaoComtele = param.getValor();

		param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.EXIBE_CAMPO_SEMPRE_LIBERADO_PARA_TODOS,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			exibeCampoSempreLiberadoParaTodos = Boolean.valueOf(param.getValor());

		param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.HABILITA_ACESSO_POR_RECONHECIMENTO_FACIAL,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			exibeCampoLinkCadastroFacialExterno = Boolean.valueOf(param.getValor());
		param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.HABILITA_APP_PEDESTRE,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			habilitaAppPedestre = Boolean.valueOf(param.getValor());
		param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.PREENCHER_CARTAO_AUTO,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			gerarCartaoAutomatico = Boolean.valueOf(param.getValor());
	}

	public boolean verificaObrigatorio(String campo) {
		if (camposObrigatorios == null)
			return false;

		return camposObrigatorios.contains(campo);
	}

	public void iniciaListas() {
		documento = new DocumentoEntity();
		mensagemEquipamento = new MensagemEquipamentoEntity();
		pedestreEquipamento = new PedestreEquipamentoEntity();
		pedestreRegra = new PedestreRegraEntity();
		responsavel = new ResponsibleEntity();

		listaDocumentos = new ArrayList<DocumentoEntity>();
		listaMensagensEquipamento = new ArrayList<MensagemEquipamentoEntity>();
		listaPedestresEquipamentos = new ArrayList<PedestreEquipamentoEntity>();
		listaPedestreRegra = new ArrayList<PedestreRegraEntity>();
		responsaveis = new ArrayList<ResponsibleEntity>();
		listaCotas = new ArrayList<HistoricoCotaEntity>(); 
	}

	public void iniciaVariaveisNovoPedestre(PedestreEntity pedestre) {
		pedestre.setEndereco(new EnderecoEntity());
		pedestre.setEmpresa(new EmpresaEntity());
		pedestre.setDepartamento(new DepartamentoEntity());
		pedestre.setCargo(new CargoEntity());
		pedestre.setCentroCusto(new CentroCustoEntity());

		if (tipo != null && !tipo.isEmpty()) {
			if ("pe".equals(tipo)) {
				pedestre.setTipo(TipoPedestre.PEDESTRE);
			} else if ("vi".equals(tipo)) {
				pedestre.setTipo(TipoPedestre.VISITANTE);
			}

		} else {
			pedestre.setTipo(TipoPedestre.PEDESTRE);
		}
	}

	public void iniciaVariaveisEditarPedestre(PedestreEntity pedestre) {
		if (Objects.isNull(pedestre.getEndereco())) {
			pedestre.setEndereco(new EnderecoEntity());
		}

		if (Objects.isNull(pedestre.getEmpresa())) {
			pedestre.setEmpresa(new EmpresaEntity());
		}

		if (Objects.isNull(pedestre.getCentroCusto())) {
			pedestre.setCentroCusto(new CentroCustoEntity());
		}

		if (Objects.isNull(pedestre.getCargo())) {
			pedestre.setCargo(new CargoEntity());
		}

		if (Objects.isNull(pedestre.getDepartamento())) {
			pedestre.setDepartamento(new DepartamentoEntity());
		}
		

		if (pedestre.getDocumentos() != null && !pedestre.getDocumentos().isEmpty()) {
			listaDocumentos = pedestre.getDocumentos();
		}

		if (pedestre.getMensagensPersonalizadas() != null && !pedestre.getMensagensPersonalizadas().isEmpty()) {
			listaMensagensEquipamento = pedestre.getMensagensPersonalizadas();
		}

		if (pedestre.getEquipamentos() != null && !pedestre.getEquipamentos().isEmpty()) {
			listaPedestresEquipamentos = pedestre.getEquipamentos();
		}

		if (pedestre.getRegras() != null && !pedestre.getRegras().isEmpty()) {
			listaPedestreRegra = pedestre.getRegras().stream()
					.sorted(Comparator.comparingLong(PedestreRegraEntity::getId).reversed())
					.collect(Collectors.toList());
		}
		
		if (pedestre.getResponsaveis() != null && !pedestre.getResponsaveis().isEmpty()) {
		    responsaveis = new ArrayList<>(pedestre.getResponsaveis());
		} else {
		    responsaveis = new ArrayList<>();
		}



		if (pedestre.getEmpresa() != null && pedestre.getEmpresa().getId() != null) {
			idEmpresaSelecionada = pedestre.getEmpresa().getId();
			montaListaCargo();
			montaListaCentroDeCusto();
			montaListaDepartamentos();
		}

		if (pedestre.getTipoQRCode() != null) {
			tipoPadraoQrCode = pedestre.getTipoQRCode();
		}
		
		if (pedestre.getCotas() != null && !pedestre.getCotas().isEmpty()) {
			listaCotas = pedestre.getCotas();
		}
		
	}

	@Override
	public String salvar() {
		PedestreEntity pedestre = getPedestreAtual();
		pedestre.setCliente(getUsuarioLogado().getCliente());

		boolean naoPossuiCamposRepetidos = verificaCamposRepetidos();
		if (!naoPossuiCamposRepetidos)
			return "";

		boolean valido = validaCamposObrigatorios(pedestre);
		if (!valido)
			return "";

		if (matriculaSequencial && pedestre.getMatricula() == null) {
			String matricula = buscaUltimaMatriculaCadastrada();
			pedestre.setMatricula(matricula);
		}

		pedestre.setUsuario(getUsuarioLogado());
		pedestre.setDataAlteracaoFoto(new Date());
			
		validaListasPedestre(pedestre);
		String retorno = super.salvar();

		if (!retorno.equals("ok")) {
			mensagemFatal("", "msg.fatal.pedestre.nao.gravado");
			return "";
		}
		
		pedestre = (PedestreEntity) getEntidade();
		
		try {
			pedestre = (PedestreEntity) baseEJB.recuperaObjeto(PedestreEntity.class, pedestre.getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (Objects.nonNull(pedestre) && Objects.nonNull(pedestre.getRegras())) {
			for (PedestreRegraEntity p : pedestre.getRegras()) {
				if (Objects.nonNull(p.getRegra()) && p.getRegra().getTipo() == TipoRegra.ACESSO_HORARIO) {
					List<HorarioEntity> horarios = buscaHorariosByIdPedestreRegra(p.getId());
					p.setHorarios(horarios);
				}
			}
		}
		
		String jsonStr = gson.toJson(WebSocketPedestrianAccessTO.fromPedestre(pedestre));
		JsonObject json = JsonParser.parseString(jsonStr).getAsJsonObject();

		// buscar cliente correto pelo idCliente e pegar unidade organizacional
		WebSocketCadastroEndpoint.enviarParaLocal(pedestre.getCliente().getId().toString(), json.toString());
		
		
		String retornoStr = "";
		if (cadastroEmLote) {
			retornoStr = "/paginas/sistema/pedestres/cadastroPedestre.xhtml?acao=OK";
		} else {
			pedestre = getPedestreAtual();
			retornoStr = "/paginas/sistema/pedestres/cadastroPedestre.xhtml?id=" + pedestre.getId() + "&acao=OK";
		}

		if (tipo != null && !tipo.isEmpty())
			retornoStr += "&tipo=" + tipo;
		redirect(retornoStr);

		return retorno;
	}

	private boolean verificaCamposRepetidos() {
		boolean valido = true;

		PedestreEntity pedestre = getPedestreAtual();
		Long idPedestre = pedestre.getId() != null ? pedestre.getId() : null;

		if (validarMatriculasDuplicadas && pedestre.getMatricula() != null && !pedestre.getMatricula().isEmpty()
				&& matriculaJaExistente(pedestre.getMatricula(), getUsuarioLogado().getCliente().getId(), idPedestre)) {
			valido = false;
			mensagemFatal("", "msg.uc008.matricula.existente");
		}

		if (validarCPFDuplicado && pedestre.getCpf() != null && !pedestre.getCpf().isEmpty()
				&& cpfJaExistente(pedestre.getCpf(), getUsuarioLogado().getCliente().getId(), idPedestre)) {
			valido = false;
			mensagemFatal("", "msg.uc008.cpf.existente");
		}

		if (validarRGDuplicado && pedestre.getRg() != null && !pedestre.getRg().isEmpty()
				&& rgJaExistente(pedestre.getRg(), getUsuarioLogado().getCliente().getId(), idPedestre)) {
			valido = false;
			mensagemFatal("", "msg.uc008.rg.existente");
		}

		if (validarCartaoAcessoDuplicado && pedestre.getCodigoCartaoAcesso() != null
				&& !pedestre.getCodigoCartaoAcesso().isEmpty() && cartaoAcessoJaExistente(
						pedestre.getCodigoCartaoAcesso(), getUsuarioLogado().getCliente().getId(), idPedestre)) {

			valido = false;
			pedestreComCartaoAcesoExistente = buscaPedestrePeloCartaoAcesso(pedestre.getCodigoCartaoAcesso());
			PrimeFaces.current().executeScript("PF('cartaoAcessoExistente').show();");
		}

		return valido;
	}

	public boolean validaCamposObrigatorios(PedestreEntity pedestre) {
		boolean valido = false;

		if (Boolean.TRUE.equals(pedestre.getSempreLiberado())) {
			valido = true;
		} else if (pedestre.getTipo().equals(TipoPedestre.PEDESTRE)) {
			if (listaPedestreRegra != null && !listaPedestreRegra.isEmpty()) {
				for (PedestreRegraEntity p : listaPedestreRegra) {
					if (p.getDataRemovido() == null) {
						valido = true;
						break;
					}
				}
			}
		} else if (pedestre.getTipo().equals(TipoPedestre.VISITANTE)) {
			valido = true;
		}

		if (!valido)
			mensagemFatal("", "msg.fatal.add.regra");

		return valido;
	}

	public void validaListasPedestre(PedestreEntity pedestre) {

		if (pedestre.getEndereco().getCep() == null || "".equals(pedestre.getEndereco().getCep()))
			pedestre.setEndereco(null);

		if (pedestre.getEmpresa().getId() == null) {
			pedestre.setEmpresa(null);
			pedestre.setDepartamento(null);
			pedestre.setCargo(null);
			pedestre.setCentroCusto(null);
		}

		if (pedestre.getDepartamento() == null || pedestre.getDepartamento().getId() == null)
			pedestre.setDepartamento(null);

		if (pedestre.getCargo() == null || pedestre.getCargo().getId() == null)
			pedestre.setCargo(null);

		if (pedestre.getCentroCusto() == null || pedestre.getCentroCusto().getId() == null)
			pedestre.setCentroCusto(null);

		if (listaDocumentos != null && !listaDocumentos.isEmpty())
			pedestre.setDocumentos(listaDocumentos);
		else
			pedestre.setDocumentos(new ArrayList<>());
		if (listaCotas != null && !listaCotas.isEmpty()) {
			
			pedestre.setCotas(listaCotas);
		}else {
			pedestre.setCotas(new ArrayList<>());
		}
		if (listaMensagensEquipamento != null && !listaMensagensEquipamento.isEmpty())
			pedestre.setMensagensPersonalizadas(listaMensagensEquipamento);
		else
			pedestre.setMensagensPersonalizadas(new ArrayList<>());

		if (listaPedestresEquipamentos != null && !listaPedestresEquipamentos.isEmpty()) {
			pedestre.setEquipamentos(listaPedestresEquipamentos);			
		} else {
			pedestre.setEquipamentos(new ArrayList<>());
		}
		
		if (responsaveis != null) {
		    pedestre.setResponsaveis(responsaveis.stream()
		        .filter(r -> r != null && r.getId() != null)
		        .collect(Collectors.toList()));
		} else {
		    pedestre.setResponsaveis(new ArrayList<>());
		}
		
		if (listaPedestreRegra != null && !listaPedestreRegra.isEmpty()) {
			pedestre.setRegras(listaPedestreRegra);

		} else if (TipoPedestre.VISITANTE.equals(pedestre.getTipo())
				&& (pedestre.getRegras() == null || pedestre.getRegras().isEmpty())) {

			PedestreRegraEntity pedestreRegra = buscaPedestreRegraPadraoVisitante();
			pedestreRegra.setPedestre(pedestre);
			pedestre.setRegras(Arrays.asList(pedestreRegra));

		} else
			pedestre.setRegras(new ArrayList<>());
	}

	private PedestreRegraEntity buscaPedestreRegraPadraoVisitante() {
		RegraEntity regra = buscaRegraPeloNome(BaseConstant.NOME_REGRA_PADRAO_VISITANTE);

		try {
			if (regra == null)
				regra = cadastraNovaRegra(BaseConstant.NOME_REGRA_PADRAO_VISITANTE);

		} catch (Exception e) {
			e.printStackTrace();
		}

		PedestreRegraEntity pedestreRegra = new PedestreRegraEntity();
		pedestreRegra.setRegra(regra);
		pedestreRegra.setQtdeTotalDeCreditos(1l);

		return pedestreRegra;
	}

	private RegraEntity cadastraNovaRegra(String nomeRegra) throws Exception {
		RegraEntity regra = new RegraEntity();
		regra.setNome(nomeRegra);
		regra.setTipoPedestre(TipoPedestre.VISITANTE);
		regra.setTipo(TipoRegra.ACESSO_UNICO);
		regra.setCliente(getUsuarioLogado().getCliente());
		regra.setStatus(Status.ATIVO);

		regra = (RegraEntity) baseEJB.gravaObjeto(regra)[0];

		return regra;
	}

	@SuppressWarnings("unchecked")
	private RegraEntity buscaRegraPeloNome(String nomeRegra) {
		Map<String, Object> args = new HashMap<>();
		args.put("NOME_REGRA", nomeRegra);
		args.put("ID_CLIENTE", getUsuarioLogado().getCliente().getId());

		List<RegraEntity> regras = null;

		try {
			regras = (List<RegraEntity>) baseEJB.pesquisaArgFixosLimitado(RegraEntity.class, "findByNome", args, 0, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return regras.stream().findFirst().orElse(null);
	}

	public void excluirPedestre() {
		try {
			PedestreEntity pedestre = getPedestreAtual();
			pedestre.setRemovido(true);
			pedestre.setDataRemovido(new Date());
			pedestre.setCodigoCartaoAcesso(null);
			baseEJB.alteraObjeto(pedestre);

			redirect("/paginas/sistema/pedestres/pesquisaPedestre.xhtml?acao=ECS");
		} catch (Exception e) {
			e.printStackTrace();
			mensagemFatal("", "msg.pedestre.nao.excluido");
		}

		return;
	}

	public void adicionarRegra() {
		for (PedestreRegraEntity p : listaPedestreRegra) {
			if (p.getDataRemovido() == null) {
				mensagemFatal("", "msg.fatal.limite.regras.ativas");
				return;
			}
		}

		if (pedestreRegra.getRegra() == null) {
			mensagemFatal("", "#A regra é obrigatória.");
			return;
		}

		if (pedestreRegra.getRegra().getQtdeDeCreditos() != null)
			pedestreRegra.setQtdeTotalDeCreditos(pedestreRegra.getRegra().getQtdeDeCreditos());
		if (pedestreRegra.getRegra().getDiasValidadeCredito() != null)
			pedestreRegra.setDiasValidadeCredito(pedestreRegra.getRegra().getDiasValidadeCredito());
		if (pedestreRegra.getRegra().getDataInicioPeriodo() != null)
			pedestreRegra.setDataInicioPeriodo(pedestreRegra.getRegra().getDataInicioPeriodo());
		if (pedestreRegra.getRegra().getDataFimPeriodo() != null)
			pedestreRegra.setDataFimPeriodo(pedestreRegra.getRegra().getDataFimPeriodo());
		
		if(Objects.nonNull(pedestreRegra.getRegra().getHorarios()) && !pedestreRegra.getRegra().getHorarios().isEmpty()) {
			List<HorarioEntity> horarios = pedestreRegra.getRegra().getHorarios()
				.stream()
				.map(pr -> pr.newHorarioEntity(pedestreRegra))
				.collect(Collectors.toList());
			
			pedestreRegra.setHorarios(horarios);
		}
		
		if(pedestreRegra.getRegra().getTipo().equals(TipoRegra.ACESSO_CREDITO)
				&& pedestreRegra.getQtdeDeCreditos() == null) {
			mensagemFatal("", "#A quantidade de créditos é obrigatória.");
			return;
		}

		if (pedestreRegra.getRegra().getTipo().equals(TipoRegra.ACESSO_PERIODO)
				&& (pedestreRegra.getDataInicioPeriodo() == null || pedestreRegra.getDataFimPeriodo() == null)) {
			mensagemFatal("", "#As datas inicial e final são obrigatórias.");
			return;
		}

		if (TipoRegra.ACESSO_UNICO.equals(pedestreRegra.getRegra().getTipo())) {
			pedestreRegra.setQtdeTotalDeCreditos(1l);
		}
		
		pedestreRegra.setPedestre(getPedestreAtual());
		listaPedestreRegra.add(0, pedestreRegra);
		pedestreRegra = new PedestreRegraEntity();
	}
	
	public void bindDependencies() {
		PedestreEntity pedestre = getPedestreAtual();

		if (responsavel == null) {
			throw new IllegalStateException("Responsável não pode ser nulo!");
		}

		if (responsaveis == null) {
			responsaveis = new ArrayList<>();
		}

		if (!responsaveis.contains(responsavel)) {
			responsaveis.add(responsavel);
		}

		pedestre.setResponsaveis(new ArrayList<>(responsaveis));

	}

	public void removerRegra(PedestreRegraEntity pedestreRegraSelecionado) {
		if (pedestreRegraSelecionado != null) {
			listaPedestreRegra.remove(pedestreRegraSelecionado);
			pedestreRegraSelecionado.setDataRemovido(new Date());
			pedestreRegraSelecionado.setRemovido(true);
			listaPedestreRegra.add(pedestreRegraSelecionado);
		}
	}
	
	public void removeResponsible(ResponsibleEntity responsavel) {
		if (Objects.nonNull(responsavel)) {
			responsaveis.remove(responsavel);
			responsavel.setDataRemovido(new Date());
			responsavel.setRemovido(true);
		}
	}

	@SuppressWarnings("unchecked")
	public List<RegraEntity> buscarRegraAutoComplete(String nome) {
		List<RegraEntity> regras = null;

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("NOME", "%" + nome + "%");
			args.put("TIPO_PEDESTRE", getPedestreAtual().getTipo());
			args.put("ID_CLIENTE", getUsuarioLogado().getCliente().getId());

			regras = (List<RegraEntity>) baseEJB.pesquisaArgFixos(RegraEntity.class, "findAllByNome", args);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return regras;
	}

	@SuppressWarnings("unchecked")
	public List<ResponsibleEntity> findResponsibleAutoFill(final String nome) {
		List<ResponsibleEntity> responsible = null;

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("NOME", "%" + nome + "%");
			args.put("ID_CLIENTE", getUsuarioLogado().getCliente().getId());

			responsible = (List<ResponsibleEntity>) baseEJB.pesquisaArgFixos(ResponsibleEntity.class, "findAllByNome",
					args);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return responsible;
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

	public void adicionaPedestreEquipamento() {
		if (idEquipamentoSelecionado != null) {
			equipamentos.forEach(equipamento -> {
				if (equipamento.getId().equals(idEquipamentoSelecionado)) {
					pedestreEquipamento.setEquipamento(equipamento);
				}
			});
		}
		pedestreEquipamento.setPedestre(getPedestreAtual());
		listaPedestresEquipamentos.add(pedestreEquipamento);
		pedestreEquipamento = new PedestreEquipamentoEntity();
		idEquipamentoSelecionado = null;
	}

	public void removerPedestreEquipamento(PedestreEquipamentoEntity pedestreEquipamento) {
		if (pedestreEquipamento != null)
			listaPedestresEquipamentos.remove(pedestreEquipamento);
	}

	public boolean isFormatoAceitoDocumento(String nome) {
		boolean retorno = false;

		if (nome.endsWith(".png"))
			return true;
		if (nome.endsWith(".jpeg"))
			return true;
		if (nome.endsWith(".jpg"))
			return true;
		if (nome.endsWith(".pdf"))
			return true;

		return retorno;
	}

	public void adicionaDocumento() {
		PedestreEntity pedestre = getPedestreAtual();

		if (documento.getNome() != null && isFormatoAceitoDocumento(documento.getNome())) {

			if (arquivoDocumento != null) {
				documento.setArquivo(arquivoDocumento);
			} else {
				mensagemFatal("", "#O arquivo do documento é obrigatório.");
				return;
			}

			documento.setPedestre(pedestre);

			listaDocumentos.add(documento);
			documento = new DocumentoEntity();
			arquivoDocumento = null;

			if (caminhoCompleto != null)
				removerArquivo(caminhoCompleto);
		} else {
			mensagemFatal("", "msg.formato.documento.invalido");
			return;
		}

	}

	public void removerDocumento(DocumentoEntity documento) {
		if (documento != null)
			listaDocumentos.remove(documento);
	}

	public void removerBiometria(BiometriaEntity biometriaSelecionada) {
		if (biometriaSelecionada != null) {
			PedestreEntity pedestre = getPedestreAtual();
			pedestre.getBiometrias().remove(biometriaSelecionada);
		}
	}

	public void adicionarMensagem() {
		mensagemEquipamento.setPedestre(getPedestreAtual());
		listaMensagensEquipamento.add(mensagemEquipamento);
		mensagemEquipamento = new MensagemEquipamentoEntity();
	}

	public void removerMensagem(MensagemEquipamentoEntity mensagem) {
		if (mensagem != null)
			listaMensagensEquipamento.remove(mensagem);
	}

	public void upload(FileUploadEvent event) {
		byte[] imagem = event.getFile().getContent();
		fileNameTemp = event.getFile().getFileName();
		fileNameTemp = fileNameTemp.replaceAll(" ", "_");

		exibeCrop = true;

		criarArquivo(imagem, fileNameTemp);
	}

	public void capturarFoto(CaptureEvent event) {
		byte[] data = event.getData();
		fileNameTemp = getRandomImageName() + ".png";
		exibeCrop = true;
		System.out.println("caminho : " + BaseConstant.URL_APLICACAO);
		criarArquivo(data, fileNameTemp);
	}

	public void uploadDocumento(FileUploadEvent event) {
		arquivoDocumento = event.getFile().getContent();
		documento.setNome(event.getFile().getFileName().replaceAll(" ", "_"));
	}

	public void capturarFotoDocumento(CaptureEvent event) {
		arquivoDocumento = event.getData();
		documento.setNome(getRandomImageName() + ".png");

		criarArquivo(arquivoDocumento, documento.getNome());
	}

	public void downloadDocumentoPedestre(DocumentoEntity documento) {
		setSessioAtrribute(BaseConstant.EXPORT.BYTES, documento.getArquivo());
		setSessioAtrribute(BaseConstant.EXPORT.FILE_NAME, documento.getNome());

		PrimeFaces.current().executeScript("download('" + BaseConstant.URL_APLICACAO + "/export');");
	}

	public void removerFotoDocumento() {
		removerArquivo(caminhoCompleto);
	}

	public void crop() {
		if(croppedImage == null) {

			return;
		}
		System.out.println("caminho : " + menuController.getUrlMain());
		
		PedestreEntity pedestre = (PedestreEntity) getEntidade();
		pedestre.setFoto(croppedImage.getBytes());
		pedestre.setDataAlteracaoFoto(new Date());
		exibeCrop = true;
		
		removerArquivo(caminhoCompleto);
	}

	public void cancelaCrop() {
		croppedImage = null;

		exibeCrop = false;
		removerArquivo(caminhoCompleto);
	}

	public void removerArquivo(String caminho) {
		try {
			if (caminho != null) {
				Path path = Paths.get(caminho);
				Files.deleteIfExists(path);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void criarArquivo(byte[] imagem, String nomeArquivo) {
	    String caminhoCompleto = AppAmbienteUtils.getResourcesFolder() + "upload/" + nomeArquivo;
	    System.out.println(caminhoCompleto);
	    try {
	        // Ensure the directory exists
	        File diretorio = new File(AppAmbienteUtils.getResourcesFolder() + "upload/");
	        if (!diretorio.exists()) {
	            diretorio.mkdirs(); // Create directory structure if it doesn't exist
	        }

	        // Write the image file
	        FileImageOutputStream fileImageOutputStream = new FileImageOutputStream(new File(caminhoCompleto));
	        fileImageOutputStream.write(imagem, 0, imagem.length);
	        fileImageOutputStream.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	private String getRandomImageName() {
		int i = (int) (Math.random() * 10000000);

		return String.valueOf(i);
	}

	public void eventoEmpresaSelecionada(ValueChangeEvent event) {
		idEmpresaSelecionada = (Long) event.getNewValue();

		if (idEmpresaSelecionada != null) {
			montaListaDepartamentos();
			montaListaCentroDeCusto();
			montaListaCargo();
		}
	}

	@SuppressWarnings("unchecked")
	public void montaListaEmpresas() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("ID_CLIENTE", getUsuarioLogado().getCliente().getId());

		listaEmpresas = new ArrayList<SelectItem>();
		listaEmpresas.add(new SelectItem(null, "Selecione"));

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

	@SuppressWarnings("unchecked")
	public void montaListaDepartamentos() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("ID_EMPRESA", idEmpresaSelecionada);

		listaDepartamentos = new ArrayList<SelectItem>();
		listaDepartamentos.add(new SelectItem(null, "Selecione"));

		try {
			List<DepartamentoEntity> departamentos = (List<DepartamentoEntity>) baseEJB
					.pesquisaArgFixos(DepartamentoEntity.class, "findAllByIdEmpresa", args);

			if (departamentos != null && !departamentos.isEmpty()) {

				departamentos.forEach(departamento -> {
					listaDepartamentos.add(new SelectItem(departamento.getId(), departamento.getNome()));
				});
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void montaListaCentroDeCusto() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("ID_EMPRESA", idEmpresaSelecionada);

		listaCentrosDeCusto = new ArrayList<SelectItem>();
		listaCentrosDeCusto.add(new SelectItem(null, "Selecione"));

		try {
			List<CentroCustoEntity> centrosDeCusto = (List<CentroCustoEntity>) baseEJB
					.pesquisaArgFixos(CentroCustoEntity.class, "findAllByIdEmpresa", args);

			if (centrosDeCusto != null && !centrosDeCusto.isEmpty()) {

				centrosDeCusto.forEach(centro -> {
					listaCentrosDeCusto.add(new SelectItem(centro.getId(), centro.getNome()));
				});
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void montaListaCargo() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("ID_EMPRESA", idEmpresaSelecionada);

		listaCargos = new ArrayList<SelectItem>();
		listaCargos.add(new SelectItem(null, "Selecione"));

		try {
			List<CargoEntity> cargos = (List<CargoEntity>) baseEJB.pesquisaArgFixos(CargoEntity.class,
					"findAllByIdEmpresa", args);

			if (cargos != null && !cargos.isEmpty()) {

				cargos.forEach(cargo -> {
					listaCargos.add(new SelectItem(cargo.getId(), cargo.getNome()));
				});
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private PedestreEntity getPedestreAtual() {
		return (PedestreEntity) getEntidade();
	}
	
	private void setPedestreAtual(PedestreEntity pedestre) {
		setEntidade(pedestre);
	}


	private void montaListaStatus() {
		listaStatus = new ArrayList<SelectItem>();
		listaStatus.add(new SelectItem(null, "Selecione"));
		listaStatus.add(new SelectItem(Status.ATIVO, Status.ATIVO.toString()));
		listaStatus.add(new SelectItem(Status.INATIVO, Status.INATIVO.toString()));
	}

	public void montaListaTipoUsuario() {
		listaTipoUsario = new ArrayList<SelectItem>();
		listaTipoUsario.add(new SelectItem(TipoPedestre.PEDESTRE, TipoPedestre.PEDESTRE.toString()));
		listaTipoUsario.add(new SelectItem(TipoPedestre.VISITANTE, TipoPedestre.VISITANTE.toString()));
	}

	public void montaListaGenero() {
		listaGenero = new ArrayList<SelectItem>();
		listaGenero.add(new SelectItem(null, "Selecione"));
		listaGenero.add(new SelectItem(Genero.MASCULINO, Genero.MASCULINO.getDescricao()));
		listaGenero.add(new SelectItem(Genero.FEMININO, Genero.FEMININO.getDescricao()));
	}

	@SuppressWarnings("unchecked")
	public void montaListaEquipamentosDisponiveis() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("ID_CLIENTE", getUsuarioLogado().getCliente().getId());

		listaEquipamentosDisponiveis = new ArrayList<SelectItem>();
		listaEquipamentosDisponiveis.add(new SelectItem(null, "Selecione"));

		try {
			equipamentos = (List<EquipamentoEntity>) baseEJB.pesquisaArgFixos(EquipamentoEntity.class,
					"findAllByIdCliente", args);

			if (equipamentos != null && !equipamentos.isEmpty()) {

				equipamentos.forEach(equipamento -> {
					listaEquipamentosDisponiveis.add(new SelectItem(equipamento.getId(), equipamento.getNome()));
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void visualizarDigital(BiometriaEntity biometriaSelecionada) {
		if (biometriaSelecionada != null)
			biometria = biometriaSelecionada;

		montaImagemBiometria();
	}

	public void montaImagemBiometria() {
		imagemBiometria = DefaultStreamedContent.builder().contentType("image/png")
				.stream(() -> new ByteArrayInputStream(biometria.getSample())).build();
	}

	public boolean isExibeCampoMatricula() {
		PedestreEntity pedestre = getPedestreAtual();

		return (TipoPedestre.PEDESTRE.equals(pedestre.getTipo()) && !matriculaSequencial
				&& permiteCampoAdicionalCrachaMatricula)
				|| (TipoPedestre.PEDESTRE.equals(pedestre.getTipo()) && pedestre.getId() != null
						&& permiteCampoAdicionalCrachaMatricula);
	}

	public boolean isDesabilitaCampoMatricula() {
		PedestreEntity pedestre = getPedestreAtual();

		return pedestre.getId() != null && matriculaSequencial;
	}

	@SuppressWarnings("unchecked")
	public String buscaUltimaMatriculaCadastrada() {
		String matricula = "1";

		Map<String, Object> args = new HashMap<String, Object>();
		args.put("ID_CLIENTE", getUsuarioLogado().getCliente().getId());

		try {
			List<PedestreEntity> listaPedestres = (List<PedestreEntity>) baseEJB
					.pesquisaArgFixosLimitado(PedestreEntity.class, "findUltimoPedestreCadastrado", args, 0, 1);

			if (listaPedestres != null && !listaPedestres.isEmpty()) {
				if (listaPedestres.get(0).getMatricula() != null) {
					Long m = Long.valueOf(listaPedestres.get(0).getMatricula().replace(".", "")) + 1l;
					matricula = m.toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return matricula;
	}

	public String getTituloPagina() {
		PedestreEntity pedestre = getPedestreAtual();

		if (TipoPedestre.PEDESTRE.equals(pedestre.getTipo())) {
			if (pedestre.getId() != null)
				return "title.uc008.editar.pedestre";
			else
				return "title.uc008.cadastrar.pedestre";

		} else if (TipoPedestre.VISITANTE.equals(pedestre.getTipo())) {
			if (pedestre.getId() != null)
				return "title.uc008.editar.visitante";
			else
				return "title.uc008.cadastrar.visitante";
		}
		return "title.uc008.cadastrar.pedestre";
	}

	public boolean exibeCampoSempreLiberado() {
		if (!getUsuarioLogado().getPerfil().equals(PerfilAcesso.ADMINISTRADOR)
				&& !getUsuarioLogado().getPerfil().equals(PerfilAcesso.GERENTE)) {
			return false;
		}

		return (getPedestreAtual() != null && getPedestreAtual().getTipo().equals(TipoPedestre.PEDESTRE)
				|| exibeCampoSempreLiberadoParaTodos);
	}

	public boolean exibeCampoCadastroFacialObrigatorio() {
		return getPedestreAtual() != null && TipoPedestre.VISITANTE.equals(getPedestreAtual().getTipo());
	}

	public boolean exibeBotaoAddCreditoVisitante() {
		PedestreEntity pedestre = getPedestreAtual();

		if (pedestre == null || pedestre.getId() == null || !TipoPedestre.VISITANTE.equals(pedestre.getTipo())) {
			return false;
		}

		if (pedestre.getRegras() != null && !pedestre.getRegras().isEmpty()) {
			for (PedestreRegraEntity pedestreRegra : pedestre.getRegras()) {
				if ((pedestreRegra.getRemovido() == null || !pedestreRegra.getRemovido())
						&& pedestreRegra.getQtdeDeCreditos() != null && pedestreRegra.getQtdeDeCreditos() == 0) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean exibeBotaoGerarQRCode() {
		return permitirAcessoViaQrCode && getPedestreAtual().getId() != null
				&& (getPedestreAtual().getQrCodeParaAcesso() == null
						|| getPedestreAtual().getQrCodeParaAcesso().trim().isEmpty());
	}

	public boolean exibeBotaoVerQRCode() {
		return permitirAcessoViaQrCode && getPedestreAtual().getId() != null
				&& (getPedestreAtual().getQrCodeParaAcesso() != null
						&& !getPedestreAtual().getQrCodeParaAcesso().trim().isEmpty());
	}

	public boolean exibeBotaoLinkCadastroFacialExterno() {
		return exibeCampoLinkCadastroFacialExterno && getPedestreAtual().getId() != null;
	}

	public boolean exibeCheckBoxDeEnvioSMS() {
		return chaveDeIntegracaoComtele != null && !chaveDeIntegracaoComtele.trim().isEmpty();
	}

	public void adicionaUmCreditoVisitante() {
		PedestreEntity pedestre = getPedestreAtual();

		if (pedestre.getRegras() != null && !pedestre.getRegras().isEmpty()) {
			for (PedestreRegraEntity pedestreRegra : pedestre.getRegras()) {
				if ((pedestreRegra.getRemovido() == null || !pedestreRegra.getRemovido())
						&& pedestreRegra.getQtdeDeCreditos() != null && pedestreRegra.getQtdeDeCreditos() == 0) {
					pedestreRegra.setQtdeDeCreditos(1l);
				}
			}
		}
		this.salvar();
	}

	@SuppressWarnings("unchecked")
	private PedestreEntity buscaPedestrePeloCartaoAcesso(String cartaoAcesso) {
		List<PedestreEntity> pedestres = null;

		Map<String, Object> args = new HashMap<String, Object>();
		args.put("codigoCartaoAcesso", cartaoAcesso);
		args.put("cliente.id", getUsuarioLogado().getCliente().getId());

		try {
			pedestres = (List<PedestreEntity>) baseEJB.pesquisaSimplesLimitado(PedestreEntity.class, "findAll", args, 0,
					1);
			if (pedestres != null && !pedestres.isEmpty())
				return pedestres.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<HorarioEntity> buscaHorariosByIdPedestreRegra(Long id) {
		HashMap<String, Object> args = new HashMap<>();
		args.put("ID", id);

		try {
			@SuppressWarnings("unchecked")
			List<PedestreRegraEntity> pedestreRegras = (List<PedestreRegraEntity>) pedestreEJB
					.pesquisaArgFixos(PedestreRegraEntity.class, "findByIdComHorarios", args);

			if (pedestreRegras != null && !pedestreRegras.isEmpty()) {
				return pedestreRegras.get(0).getHorarios();
			}
		} catch (Exception e) {
			System.err.println("Erro ao buscar horários do pedestre: " + e.getMessage());
			e.printStackTrace();
		}

		return Collections.emptyList();
	}
	
	@SuppressWarnings("unchecked")
	public void onCpfBlur() {
		PedestreEntity pedestre = getPedestreAtual();
		List<PedestreEntity> existente = null;

		String cpf = pedestre.getCpf();
		if (cpf == null || cpf.trim().isEmpty()) {
			cpf = "";
		}

		cpf = cpf.replace(".", "").replace("-", "").trim();
		
		
		if(Objects.isNull(cpf) || cpf.isEmpty() || "".equals(cpf)){
			return;
		}


		Map<String, Object> args = new HashMap<String, Object>();
		args.put("CPF", cpf);
		args.put("ID_CLIENTE", getUsuarioLogado().getCliente().getId());

		try {
			existente = (List<PedestreEntity>) baseEJB.pesquisaArgFixos(PedestreEntity.class, "findByCPFOnBlur", args);
			if (existente != null && !existente.isEmpty()) {
				PedestreEntity encontrado = existente.get(0);

				if (pedestre.getId() != null && pedestre.getId().equals(encontrado.getId())) {
					return;
				}

				if (encontrado.isVisitante()) {
					urlNovoRegistro = "/paginas/sistema/pedestres/cadastroPedestre.xhtml?tipo=vi";
				} else {
					urlNovoRegistro = "/paginas/sistema/pedestres/cadastroPedestre.xhtml?tipo=pe";
				}
				editar(encontrado.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void onRgBlur() {
		PedestreEntity pedestre = getPedestreAtual();
		List<PedestreEntity> existente = null;
	    
	    String rg = pedestre.getRg();
	    if (rg == null || rg.trim().isEmpty()) {
	        rg="";
	    }
		
		if(Objects.isNull(rg) || rg.isEmpty() || "".equals(rg)){
			return;
		}

		Map<String, Object> args = new HashMap<String, Object>();
		args.put("RG", rg);
		args.put("ID_CLIENTE", getUsuarioLogado().getCliente().getId());
		
	    try {
			existente = (List<PedestreEntity>) baseEJB.pesquisaArgFixos(PedestreEntity.class, "findByRGOnBlur", args);
			if (existente != null && !existente.isEmpty()) {
	            PedestreEntity encontrado = existente.get(0);

	            if (pedestre.getId() != null && pedestre.getId().equals(encontrado.getId())) {
	                return;
	            }

				if(encontrado.isVisitante()) {
					urlNovoRegistro = "/paginas/sistema/pedestres/cadastroPedestre.xhtml?tipo=vi";
				}else {
					urlNovoRegistro = "/paginas/sistema/pedestres/cadastroPedestre.xhtml?tipo=pe";
				}
				editar(encontrado.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void preencherCartaoAutomatico() {
		PedestreEntity pedestre = getPedestreAtual();

		String cartaoAtual = pedestre.getCodigoCartaoAcesso();

		// Considera "vazio" se for null, vazio ou só zeros
		boolean cartaoVazioOuInvalido = cartaoAtual == null ||
		                                 cartaoAtual.trim().isEmpty() ||
		                                 cartaoAtual.replace("0", "").isEmpty();

		System.out.println("pedestre cartao : " + cartaoAtual);

		if (cartaoVazioOuInvalido) {
			String codigo = null;

			if (pedestre.getMatricula() != null && !pedestre.getMatricula().trim().isEmpty()) {
				codigo = pedestre.getMatricula().replaceAll("\\D", ""); // apenas números
			} else if (pedestre.getCpf() != null && !pedestre.getCpf().trim().isEmpty()) {
				codigo = pedestre.getCpf().replaceAll("\\D", "");
			}

			if (codigo != null && !codigo.isEmpty()) {
				pedestre.setCodigoCartaoAcesso(codigo);
			} else {
				// fallback: gerar número randômico com 8 dígitos
				pedestre.setCodigoCartaoAcesso(String.format("%08d", new Random().nextInt(100000000)));
			}
		}
	}
	
	public String gerarCartao(PedestreEntity pedestre) {
		if(gerarCartaoAutomatico) {
			String cartaoAtual = pedestre.getCodigoCartaoAcesso();

			// Considera "vazio" se for null, vazio ou só zeros
			boolean cartaoVazioOuInvalido = cartaoAtual == null ||
			                                 cartaoAtual.trim().isEmpty() ||
			                                 cartaoAtual.replace("0", "").isEmpty();

			System.out.println("pedestre cartao : " + cartaoAtual);

			if (cartaoVazioOuInvalido) {
				String codigo = null;

				if (pedestre.getMatricula() != null && !pedestre.getMatricula().trim().isEmpty()) {
					codigo = pedestre.getMatricula().replaceAll("\\D", ""); // apenas números
				} else if (pedestre.getCpf() != null && !pedestre.getCpf().trim().isEmpty()) {
					codigo = pedestre.getCpf().replaceAll("\\D", "");
				}

				if (codigo != null && !codigo.isEmpty()) {
					pedestre.setCodigoCartaoAcesso(codigo);
				} else {
					// fallback: gerar número randômico com 8 dígitos
					pedestre.setCodigoCartaoAcesso(String.format("%08d", new Random().nextInt(100000000)));
				}
			}
		}
		
		return pedestre.getCodigoCartaoAcesso();
	}



	public String gerarLinkCadastroFacialExterno() {
		ultimoCadastroExterno = buscaUltimoCadastroExterno();

		ParametroEntity param = baseEJB.getParametroSistema(
				BaseConstant.PARAMETERS_NAME.DIAS_VALIDADE_LINK_CADASTRO_FACIAL_EXTERNO,
				getUsuarioLogado().getCliente().getId());
		if (param != null)
			diasValidadeLinkCadastroFacialExterno = Integer.valueOf(param.getValor());

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, diasValidadeLinkCadastroFacialExterno);
		tokenCadastroFacialExterno = calendar.getTimeInMillis();

		linkCadastroFacialExterno = (AppAmbienteUtils.isProdution()
				? (AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_MAIN_SITE)
						+ AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_NOME_APP) + "/")
				: "http://localhost:8081/") + "cadastroFacialExterno.xhtml?cliente="
				+ getUsuarioLogado().getCliente().getId() + "&idPedestre=" + getPedestreAtual().getId() + "&token="
				+ tokenCadastroFacialExterno;

		PrimeFaces.current().executeScript("PF('gerarLinkCadastroFacialExterno').show();");

		return "";
	}
	
	public void getLinkAutoatendimento() {
	    PedestreEntity pedestre = getPedestreAtual();
	    if (pedestre == null || pedestre.getId() == null ) {
	        FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Pedestre não selecionado."));
	        return;
	    }
	    pedestre.getTipo();

		if (pedestre == null || pedestre.getCelular() == null) {
			mensagemFatal("", "msg.celular.nulo");
			return;
		}

	    ParametroEntity param = baseEJB.getParametroSistema(
	            BaseConstant.PARAMETERS_NAME.DIAS_VALIDADE_LINK_CADASTRO_FACIAL_EXTERNO,
	            getUsuarioLogado().getCliente().getId());

	    int diasValidade = param != null ? Integer.parseInt(param.getValor()) : 1;

	    Calendar calendar = Calendar.getInstance();
	    calendar.add(Calendar.DAY_OF_YEAR, diasValidade);
	    long tokenValidade = calendar.getTimeInMillis();

	    String baseUrl = AppAmbienteUtils.isProdution()
	            ? AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_MAIN_SITE)
	              + AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_NOME_APP) + "/"
	            : "http://localhost:8081/";

	    this.linkGerado = baseUrl
	            + "cadastroAutoatendimento.xhtml"
	            + "?cliente=" + getUsuarioLogado().getCliente().getId()
	            + "&idPedestre=" + pedestre.getId()
	            + "&token=" + tokenValidade;
	    

		String celphone = pedestre.getCelular().replace("-", "").replace("(", "").replace(")", "").replace(" ", "");

		String msg = "Olá, acesse o link para cadastro facial:\n" + linkGerado;

		try {
		    String encodedMsg = URLEncoder.encode(msg, "UTF-8");
		    
		    PrimeFaces.current().executeScript(
		        "window.open('" + BaseConstant.URL_WHATSAPP + "55" + celphone + "&text=" + encodedMsg + "','whatsAppTab');");

		} catch (UnsupportedEncodingException e) {
		    e.printStackTrace();
		}

	}

	public StreamedContent getPrimeiraFotoStreamed() {
		return Utils.getStreamedContent(ultimoCadastroExterno.getPrimeiraFoto());
	}

	@SuppressWarnings("unchecked")
	private CadastroExternoEntity buscaUltimoCadastroExterno() {
		Map<String, Object> args = new HashMap<>();
		args.put("ID_PEDESTRE", getPedestreAtual().getId());

		try {
			List<CadastroExternoEntity> cadastrosExternos = (List<CadastroExternoEntity>) baseEJB
					.pesquisaArgFixosLimitado(CadastroExternoEntity.class, "findLastByIdPedestre", args, 0, 1);

			if (cadastrosExternos != null)
				return cadastrosExternos.stream().findFirst().orElse(null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void enviarPorEmailLinkCadastroFacialExterno() {
		PedestreEntity pedestre = getPedestreAtual();

		if (pedestre == null || pedestre.getEmail() == null || pedestre.getEmail().trim().isEmpty()) {
			mensagemFatal("", "msg.email.nulo");
			return;
		}

		String subject = ResourceBundleUtils.getInstance().recuperaChave("mail.title.link.cadastro.facial.externo",
				getFacesContext(),
				new Object[] { AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_NOME_APP) });

		String msg = ResourceBundleUtils.getInstance().recuperaChave("msg.corpo.link.cadastro.externo.facial",
				getFacesContext(), new Object[] { linkCadastroFacialExterno });

		try {
			MailUtils.getInstance(mailSession).send(pedestre.getEmail(), subject, msg, "");
			mensagemInfo("", "msg.email.enviado.sucesso");

			gravarCadastroExternoGerado();

		} catch (Exception e) {
			mensagemFatal("", "msg.falha.enviar.email");
			e.printStackTrace();
		}
	}

	public void enviarPorWhatsappLinkCadastroFacialExterno() {
		PedestreEntity pedestre = getPedestreAtual();

		if (pedestre == null || pedestre.getCelular() == null) {
			mensagemFatal("", "msg.celular.nulo");
			return;
		}

		String celphone = pedestre.getCelular().replace("-", "").replace("(", "").replace(")", "").replace(" ", "");

		String msg = ResourceBundleUtils.getInstance().recuperaChave("msg.corpo.link.cadastro.externo.facial",
				getFacesContext(), new Object[] { linkCadastroFacialExterno.replaceAll("&", "%26") });

		PrimeFaces.current().executeScript(
				"window.open('" + BaseConstant.URL_WHATSAPP + "55" + celphone + "&text=" + msg + "','whatsAppTab');");

		gravarCadastroExternoGerado();
	}

	public void gravarCadastroExternoGerado() {
		CadastroExternoEntity cadastroExterno = procuraCadastroExternoTokenAtivo(getPedestreAtual().getId());

		if (cadastroExterno != null) {
			try {
				cadastroExterno.setToken(tokenCadastroFacialExterno);
				baseEJB.alteraObjeto(cadastroExterno);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return;
		}

		try {
			cadastroExterno = new CadastroExternoEntity();
			cadastroExterno.setCliente(getUsuarioLogado().getCliente());
			cadastroExterno.setPedestre(getPedestreAtual());
			cadastroExterno.setToken(tokenCadastroFacialExterno);
			cadastroExterno.setStatusCadastroExterno(StatusCadastroExterno.AGUARDANDO_CADASTRO);

			baseEJB.gravaObjeto(cadastroExterno);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private CadastroExternoEntity procuraCadastroExternoTokenAtivo(Long idPedestre) {
		Map<String, Object> args = new HashMap<>();
		args.put("ID_PEDESTRE", idPedestre);
		args.put("STATUS", StatusCadastroExterno.AGUARDANDO_CADASTRO);
		args.put("TOKEN", Calendar.getInstance().getTimeInMillis());

		List<CadastroExternoEntity> cadastrosExternos = null;

		try {
			cadastrosExternos = (List<CadastroExternoEntity>) baseEJB.pesquisaArgFixos(CadastroExternoEntity.class,
					"findAllTokensActive", args);

			if (cadastrosExternos != null && !cadastrosExternos.isEmpty())
				return cadastrosExternos.get(0);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void gerarQrCode() {
		PedestreEntity pedestre = getPedestreAtual();

		if (tipoPadraoQrCode == null || TipoQRCode.ESTATICO.equals(tipoPadraoQrCode)) {
			// gera QRCode estático
			pedestre.setQrCodeParaAcesso(
					getUsuarioLogado().getCliente().getId() + "_" + padLeftZeros(pedestre.getId().toString(), 5));
		} else {
			// gera QRCode genérico
			String qrCode = EncryptionUtils.getRandomString(4);
			if (TipoQRCode.DINAMICO_USO.equals(tipoPadraoQrCode)) {
				// adiciona primeiro giro
				qrCode = "U_" + qrCode + "_0";
			} else {
				qrCode = "T_" + qrCode;
			}
			pedestre.setQrCodeParaAcesso(qrCode);
		}

		pedestre.setTipoQRCode(tipoPadraoQrCode);

		validaListasPedestre(pedestre);

		String retorno = super.salvar();

		if (!retorno.equals("ok")) {
			mensagemFatal("", "msg.erro.gerar.qr.code");
			return;
		}

		pedestre = getPedestreAtual();
		String retornoStr = "/paginas/sistema/pedestres/cadastroPedestre.xhtml?id=" + pedestre.getId() + "&acao=EQC";

		if (tipo != null && !tipo.isEmpty())
			retornoStr += "&tipo=" + tipo;

		redirect(retornoStr);
	}

	public static String padLeftZeros(String inputString, int length) {
		if (inputString.length() >= length) {
			return inputString;
		}
		StringBuilder sb = new StringBuilder();
		while (sb.length() < length - inputString.length()) {
			sb.append('0');
		}
		sb.append(inputString);

		return sb.toString();
	}

	public void apagarQrCode() {
		PedestreEntity pedestre = getPedestreAtual();
		pedestre.setQrCodeParaAcesso(null);
		pedestre.setTipoQRCode(null);

		validaListasPedestre(pedestre);

		String retorno = super.salvar();

		if (!retorno.equals("ok")) {
			mensagemFatal("", "msg.erro.apagar.qr.code");
			return;
		}

		pedestre = getPedestreAtual();
		String retornoStr = "/paginas/sistema/pedestres/cadastroPedestre.xhtml?id=" + pedestre.getId() + "&acao=QCA";

		if (tipo != null && !tipo.isEmpty())
			retornoStr += "&tipo=" + tipo;
		redirect(retornoStr);
	}

	public void enviarQRCodePorWhatsApp() {
		PedestreEntity pedestre = getPedestreAtual();

		String linkQrCode = getURLlinks() + "/qrCode?idUser=" + pedestre.getId();

		if (pedestre == null || pedestre.getCelular() == null) {
			mensagemFatal("", "msg.celular.nulo");
			return;
		}

		String celphone = pedestre.getCelular().replace("-", "").replace("(", "").replace(")", "").replace(" ", "");

		String msg = ResourceBundleUtils.getInstance().recuperaChave("msg.corpo.link.qrCode", getFacesContext(),
				new Object[] { linkQrCode });

		PrimeFaces.current().executeScript(
				"window.open('" + BaseConstant.URL_WHATSAPP + "55" + celphone + "&text=" + msg + "','whatsAppTab');");
	}

	public void enviarQRCodePorEmail() {
		PedestreEntity pedestre = getPedestreAtual();

		String linkQrCode = getURLlinks() + "/qrCode?idUser=" + pedestre.getId();

		if (pedestre == null || pedestre.getEmail() == null || pedestre.getEmail().trim().isEmpty()) {
			mensagemFatal("", "msg.email.nulo");
			return;
		}

		String subject = ResourceBundleUtils.getInstance().recuperaChave("mail.title.link.qrCode", getFacesContext(),
				new Object[] { AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_NOME_APP) });

		String msg = ResourceBundleUtils.getInstance().recuperaChave("msg.corpo.link.qrCode", getFacesContext(),
				new Object[] { linkQrCode });

		try {
			MailUtils.getInstance(mailSession).send(pedestre.getEmail(), subject, msg, "");
			mensagemInfo("", "msg.email.enviado.sucesso");

		} catch (Exception e) {
			mensagemFatal("", "msg.falha.enviar.email");
			e.printStackTrace();
		}
	}

	public String getURLlinks() {
		String urlLinks = AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_MAIN_LINK);
		if (urlLinks == null || "".equals(urlLinks))
			urlLinks = BaseConstant.URL_APLICACAO_COMPLETA;
		else
			urlLinks = urlLinks + AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_NOME_APP);
		return urlLinks;
	}
	
	public boolean usuarioTemPermissao() {
	    // Lógica para verificar se o usuário pode ver os dados
		UsuarioEntity usuarioLogado = menuController.getUsuarioLogado();
		return !usuarioLogado.getPerfil().equals(PerfilAcesso.CUIDADOR);
	}

	public String getCpfMascarado() {
	    if (usuarioTemPermissao() && getPedestreAtual() != null && getPedestreAtual().getCpf() != null) {
	        return getPedestreAtual().getCpf();
	    } else {
	        return "XXX-XXX-XXX-XX";
	    }
	}

	public String getRgMascarado() {
	    if (usuarioTemPermissao() && getPedestreAtual() != null && getPedestreAtual().getRg() != null) {
	        return getPedestreAtual().getRg();
	    } else {
	        return "XX-XXXXXXXX";
	    }
	}

	public String getEmailMascarado() {
	    if (usuarioTemPermissao() && getPedestreAtual() != null && getPedestreAtual().getEmail() != null) {
	        return getPedestreAtual().getEmail();
	    } else {
	        return "XXXXX@XXXXX";
	    }
	}

	public String getTelefoneMascarado() {
	    if (usuarioTemPermissao() && getPedestreAtual() != null && getPedestreAtual().getTelefone() != null) {
	        return getPedestreAtual().getTelefone();
	    } else {
	        return "(XX)XXXXXXXX";
	    }
	}

	public String getCelularMascarado() {
	    if (usuarioTemPermissao() && getPedestreAtual() != null && getPedestreAtual().getCelular() != null) {
	        return getPedestreAtual().getCelular();
	    } else {
	        return "(XX)XXXXXXXXX";
	    }
	}

	public void cadastrarCota() {
	    try {
	        PedestreEntity pedestre = getPedestreAtual();
	        if (pedestre == null || pedestre.getId() == null) {
	            mensagemErro("Erro", "Pedestre não encontrado.");
	            return;
	        }

	        HistoricoCotaEntity cota = new HistoricoCotaEntity();
	        cota.setPedestre(pedestre);
	        cota.setMes(mes);
	        cota.setAno(ano);
	        cota.setCotaMensal(cotaMensal);

	        listaCotas.add(cota); // Atualiza a lista de cotas

	        // Limpa os campos após o cadastro
	        mes = null;
	        ano = null;
	        cotaMensal = null;
	    } catch (Exception e) {
	        mensagemErro("Erro ao cadastrar cota", e.getMessage());
	    }
	}
	
	public void removerCotas(HistoricoCotaEntity cota) {
		if (cota != null) {
			cota.setRemovido(true);
			cota.setDataRemovido(getDataAtual());
			
			listaCotas.remove(cota);
		}
	}

	public void onAutoAtendimentoChange() {
		PedestreEntity pedestre = getPedestreAtual();
		
	    if (pedestre.getAutoAtendimento()) {
	    	pedestre.setAutoAtendimentoAt(getDataAtual());
	    } else {
	    	pedestre.setAutoAtendimentoAt(null); // ou manter a última data, se preferir
	    }
	}
	
	public boolean isAdminOrGerente() {
		if (PerfilAcesso.ADMINISTRADOR.equals(getUsuarioLogado().getPerfil())
				|| PerfilAcesso.GERENTE.equals(getUsuarioLogado().getPerfil())) {
			
			return true;
		}
		
	    return false;
	}
	
	public boolean isOperador() {
		if (PerfilAcesso.ADMINISTRADOR.equals(getUsuarioLogado().getPerfil())
				|| PerfilAcesso.GERENTE.equals(getUsuarioLogado().getPerfil())
				|| PerfilAcesso.OPERADOR.equals(getUsuarioLogado().getPerfil())) {
			return true;
		}
	    return false;
	}
	
	
	public boolean isCuidador() {
		if (PerfilAcesso.CUIDADOR.equals(getUsuarioLogado().getPerfil())
			|| PerfilAcesso.RESPONSAVEL.equals(getUsuarioLogado().getPerfil())
				|| PerfilAcesso.GERENTE.equals(getUsuarioLogado().getPerfil())
				 || PerfilAcesso.ADMINISTRADOR.equals(getUsuarioLogado().getPerfil())) {
			return true;
		}
	    return false;
	}
	
	public void adicionarRelatorio() {
		PedestreEntity pedestre = getPedestreAtual();
	    relatorio.setIdPedestre(pedestre.getId()); // associa ao pedestre em edição
	    relatorio.setCartaoAcessoRecebido(pedestre.getCodigoCartaoAcesso());
	    relatorio.setCliente(pedestre.getCliente());
	    relatorio.setPedestre(pedestre);
	    relatorio.setSentido(relatorio.getSentido());
	    relatorio.setTipo("ATIVO");
	    try {
			baseEJB.gravaObjeto(relatorio);
			listaRelatorios.add(relatorio);       // atualiza lista na tela
			relatorio = new AcessoEntity();    // reseta para próximo uso
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    carregarRelatorios(); 
	}

	public void removerRelatorio(AcessoEntity rel) {
		rel.setRemovido(true);
		rel.setDataRemovido(getDataAtual());
		try {
			baseEJB.gravaObjeto(rel);
			listaRelatorios.remove(rel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}           // ou serviço equivalente
	}
	
	@SuppressWarnings("unchecked")
	public void carregarRelatorios() {
		PedestreEntity pedestre = getPedestreAtual();
		
		getParans().put("data_maior_data", ajustarDataInicio(getParans().get("data_maior_data")));
		getParans().put("data_menor_data", ajustarDataFim(getParans().get("data_menor_data")));
		
		System.out.println("data inicio : " + getParans().get("data_maior_data"));
		System.out.println("data fim : " + getParans().get("data_menor_data"));
		
		Map<String, Object> args = new HashMap<>();
		args.put("ID_PEDESTRE", pedestre.getId());
		args.put("DATA_FIM", (getParans().get("data_menor_data")));
		args.put("DATA_INICIO", (getParans().get("data_maior_data")));
		

		List<AcessoEntity> acessos = null;

		try {
			acessos = (List<AcessoEntity>) baseEJB.pesquisaArgFixos(AcessoEntity.class, "findAllByIdPedestre", args);
		} catch (Exception e) {
			e.printStackTrace();
		}

	    this.listaRelatorios = acessos;
	}

	public void onTabChange(TabChangeEvent event) {
	    String tituloAba = event.getTab().getTitle();

	    if ("Relatórios".contains(tituloAba)) {
	        carregarRelatorios();
	    }
	}
	
	private Date ajustarDataInicio(Object obj) {
	    if (obj instanceof Date) {
	        Calendar cal = Calendar.getInstance();
	        cal.setTime((Date) obj);
	        cal.set(Calendar.HOUR_OF_DAY, 0);
	        cal.set(Calendar.MINUTE, 0);
	        cal.set(Calendar.SECOND, 0);
	        cal.set(Calendar.MILLISECOND, 0);
	        return cal.getTime();
	    }
	    return null;
	}

	private Date ajustarDataFim(Object obj) {
	    if (obj instanceof Date) {
	        Calendar cal = Calendar.getInstance();
	        cal.setTime((Date) obj);
	        cal.set(Calendar.HOUR_OF_DAY, 23);
	        cal.set(Calendar.MINUTE, 59);
	        cal.set(Calendar.SECOND, 59);
	        cal.set(Calendar.MILLISECOND, 999);
	        return cal.getTime();
	    }
	    return null;
	}
	
	public void alteraDataInicio(ValueChangeEvent event) {
		getParans().put("data_maior_data", event.getNewValue());
	}
	
	public void alteraDataFim(ValueChangeEvent event) {
		getParans().put("data_menor_data", event.getNewValue());
	}

	public void imprimirQRCode() {
		PrimeFaces.current().executeScript("printSimpleDiv('imageQrCodeDiv');");
	}

	public String nomeUsuarioLogado() {
		return getUsuarioLogado().getNome();
	}
	

	public Date getDataAtual() {
		return new Date();
	}

	public List<SelectItem> getListaStatus() {
		return listaStatus;
	}

	public void setListaStatus(List<SelectItem> listaStatus) {
		this.listaStatus = listaStatus;
	}

	public List<SelectItem> getListaTipoUsario() {
		return listaTipoUsario;
	}

	public void setListaTipoUsario(List<SelectItem> listaTipoUsario) {
		this.listaTipoUsario = listaTipoUsario;
	}

	public List<SelectItem> getListaEmpresas() {
		return listaEmpresas;
	}

	public void setListaEmpresas(List<SelectItem> listaEmpresas) {
		this.listaEmpresas = listaEmpresas;
	}

	public List<SelectItem> getListaDepartamentos() {
		return listaDepartamentos;
	}

	public void setListaDepartamentos(List<SelectItem> listaDepartamentos) {
		this.listaDepartamentos = listaDepartamentos;
	}

	public List<SelectItem> getListaCentrosDeCusto() {
		return listaCentrosDeCusto;
	}

	public void setListaCentrosDeCusto(List<SelectItem> listaCentrosDeCusto) {
		this.listaCentrosDeCusto = listaCentrosDeCusto;
	}

	public List<SelectItem> getListaCargos() {
		return listaCargos;
	}

	public void setListaCargos(List<SelectItem> listaCargos) {
		this.listaCargos = listaCargos;
	}

	public Long getIdEmpresaSelecionada() {
		return idEmpresaSelecionada;
	}

	public void setIdEmpresaSelecionada(Long idEmpresaSelecionada) {
		this.idEmpresaSelecionada = idEmpresaSelecionada;
	}

	public List<SelectItem> getListaGenero() {
		return listaGenero;
	}

	public void setListaGenero(List<SelectItem> listaGenero) {
		this.listaGenero = listaGenero;
	}

	public CroppedImage getCroppedImage() {
		return croppedImage;
	}

	public void setCroppedImage(CroppedImage croppedImage) {
		this.croppedImage = croppedImage;
	}

	public String getFileNameTemp() {
		return fileNameTemp;
	}

	public void setFileNameTemp(String fileNameTemp) {
		this.fileNameTemp = fileNameTemp;
	}

	public String getCaminhoCompleto() {
		return caminhoCompleto;
	}

	public void setCaminhoCompleto(String caminhoCompleto) {
		this.caminhoCompleto = caminhoCompleto;
	}

	public byte[] getArquivoDocumento() {
		return arquivoDocumento;
	}

	public void setArquivoDocumento(byte[] arquivoDocumento) {
		this.arquivoDocumento = arquivoDocumento;
	}

	public DocumentoEntity getDocumento() {
		return documento;
	}

	public void setDocumento(DocumentoEntity documento) {
		this.documento = documento;
	}

	public List<DocumentoEntity> getListaDocumentos() {
		return listaDocumentos;
	}

	public MensagemEquipamentoEntity getMensagemEquipamento() {
		return mensagemEquipamento;
	}

	public void setMensagemEquipamento(MensagemEquipamentoEntity mensagemEquipamento) {
		this.mensagemEquipamento = mensagemEquipamento;
	}

	public List<MensagemEquipamentoEntity> getListaMensagensEquipamento() {
		return listaMensagensEquipamento;
	}

	public List<SelectItem> getListaEquipamentosDisponiveis() {
		return listaEquipamentosDisponiveis;
	}

	public PedestreEquipamentoEntity getPedestreEquipamento() {
		return pedestreEquipamento;
	}

	public void setPedestreEquipamento(PedestreEquipamentoEntity pedestreEquipamento) {
		this.pedestreEquipamento = pedestreEquipamento;
	}

	public List<PedestreEquipamentoEntity> getListaPedestresEquipamentos() {
		return listaPedestresEquipamentos;
	}

	public Long getIdEquipamentoSelecionado() {
		return idEquipamentoSelecionado;
	}

	public void setIdEquipamentoSelecionado(Long idEquipamentoSelecionado) {
		this.idEquipamentoSelecionado = idEquipamentoSelecionado;
	}

	public List<EquipamentoEntity> getEquipamentos() {
		return equipamentos;
	}

	public BiometriaEntity getBiometria() {
		return biometria;
	}

	public void setBiometria(BiometriaEntity biometria) {
		this.biometria = biometria;
	}

	public StreamedContent getImagemBiometria() {
		return imagemBiometria;
	}

	public void setImagemBiometria(StreamedContent imagemBiometria) {
		this.imagemBiometria = imagemBiometria;
	}

	public String getCpfNovoPedestre() {
		return cpfNovoPedestre;
	}

	public void setCpfNovoPedestre(String cpfNovoPedestre) {
		this.cpfNovoPedestre = cpfNovoPedestre;
	}

	public PedestreRegraEntity getPedestreRegra() {
		return pedestreRegra;
	}

	public void setPedestreRegra(PedestreRegraEntity pedestreRegra) {
		this.pedestreRegra = pedestreRegra;
	}

	public List<PedestreRegraEntity> getListaPedestreRegra() {
		return listaPedestreRegra;
	}

	public List<SelectItem> getListaTipoRegra() {
		return listaTipoRegra;
	}

	public boolean isExibeCrop() {
		return exibeCrop;
	}

	public void setExibeCrop(boolean exibeCrop) {
		this.exibeCrop = exibeCrop;
	}

	public boolean isMatriculaSequencial() {
		return matriculaSequencial;
	}

	public void setMatriculaSequencial(boolean matriculaSequencial) {
		this.matriculaSequencial = matriculaSequencial;
	}

	public Integer getQtdeDigitosCartao() {
		return qtdeDigitosCartao;
	}

	public void setQtdeDigitosCartao(Integer qtdeDigitosCartao) {
		this.qtdeDigitosCartao = qtdeDigitosCartao;
	}

	public String getOrigem() {
		return origem;
	}

	public String getTipo() {
		return tipo;
	}

	public PedestreEntity getPedestreComCartaoAcesoExistente() {
		return pedestreComCartaoAcesoExistente;
	}

	public boolean isPermitirAcessoViaQrCode() {
		return permitirAcessoViaQrCode;
	}

	public void setPermitirAcessoViaQrCode(boolean permitirAcessoViaQrCode) {
		this.permitirAcessoViaQrCode = permitirAcessoViaQrCode;
	}

	public String getLinkCadastroFacialExterno() {
		return linkCadastroFacialExterno;
	}

	public CadastroExternoEntity getUltimoCadastroExterno() {
		return ultimoCadastroExterno;
	}

	public boolean isHabilitaTiposQRCode() {
		return habilitaTiposQRCode;
	}

	public void setHabilitaTiposQRCode(boolean habilitaTiposQRCode) {
		this.habilitaTiposQRCode = habilitaTiposQRCode;
	}

	public List<SelectItem> getListaTipoQRCode() {
		return listaTipoQRCode;
	}

	public void setListaTipoQRCode(List<SelectItem> listaTipoQRCode) {
		this.listaTipoQRCode = listaTipoQRCode;
	}

	public TipoQRCode getTipoPadraoQrCode() {
		return tipoPadraoQrCode;
	}

	public void setTipoPadraoQrCode(TipoQRCode tipoPadraoQrCode) {
		this.tipoPadraoQrCode = tipoPadraoQrCode;
	}

	public boolean isHabilitaAppPedestre() {
		return habilitaAppPedestre;
	}

	public void setHabilitaAppPedestre(boolean habilitaAppPedestre) {
		this.habilitaAppPedestre = habilitaAppPedestre;
	}

	public ResponsibleEntity getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(ResponsibleEntity responsavel) {
		this.responsavel = responsavel;
	}

	public List<ResponsibleEntity> getResponsaveis() {
		return responsaveis;
	}

	public void setResponsaveis(List<ResponsibleEntity> responsaveis) {
		this.responsaveis = responsaveis;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Long getCotaMensal() {
		return cotaMensal;
	}

	public void setCotaMensal(Long cotaMensal) {
		this.cotaMensal = cotaMensal;
	}

	public List<HistoricoCotaEntity> getListaCotas() {
		return listaCotas;
	}

	public void setListaCotas(List<HistoricoCotaEntity> listaCotas) {
		this.listaCotas = listaCotas;
	}

	public AcessoEntity getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(AcessoEntity relatorio) {
		this.relatorio = relatorio;
	}

	public List<AcessoEntity> getListaRelatorios() {
		return listaRelatorios;
	}

	public void setListaRelatorios(List<AcessoEntity> listaRelatorios) {
		this.listaRelatorios = listaRelatorios;
	}

	public String getLinkGerado() {
	    return linkGerado;
	}

	public void setLinkGerado(String linkGerado) {
	    this.linkGerado = linkGerado;
	}
	
	public String getCodigoCartao() {
	    return getPedestreAtual().getCodigoCartaoAcesso();
	}

	public void setCodigoCartao(String codigo) {
	    getPedestreAtual().setCodigoCartaoAcesso(codigo);
	}

}
