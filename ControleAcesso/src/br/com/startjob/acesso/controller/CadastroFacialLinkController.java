package br.com.startjob.acesso.controller;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CaptureEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.startjob.acesso.api.WebSocketCadastroEndpoint;
import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.CadastroExternoEntity;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.HorarioEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.PedestreRegraEntity;
import br.com.startjob.acesso.modelo.entity.RegraEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.StatusCadastroExterno;
import br.com.startjob.acesso.modelo.enumeration.TipoCadastroExterno;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.enumeration.TipoRegra;
import br.com.startjob.acesso.to.WebSocketPedestrianAccessTO;

/**
 * Cadastro facial via link (WhatsApp).
 * <ul>
 * <li>Modo pré-cadastro: {@code idPedestre} na URL (visitante já cadastrado)</li>
 * <li>Modo convite: sem {@code idPedestre} (novo visitante ou CPF existente)</li>
 * </ul>
 */
@SuppressWarnings("serial")
@Named("cadastroFacialLinkController")
@ViewScoped
public class CadastroFacialLinkController extends BaseController {

	private static final Logger LOG = Logger.getLogger(CadastroFacialLinkController.class.getName());

	public static final int STEP_CPF = 0;
	public static final int STEP_DADOS = 1;
	public static final int STEP_FOTO = 2;

	@EJB
	private PedestreEJBRemote pedestreEJB;

	private PedestreEntity pedestre;
	private CadastroExternoEntity cadastroExterno;
	private final Gson gson = new Gson();

	private Long idCliente;
	private Long idPedestre;
	private Long idEmpresaUrl;
	private Long token;

	private boolean acessoPermitido;
	private boolean modoConvite;
	private boolean modoPrecadastro;
	private boolean dadosSomenteLeitura;
	private int step = STEP_CPF;

	private List<SelectItem> listaEmpresas = new ArrayList<>();
	private Long idEmpresaSelecionada;
	private String empresaVisitadaUi;

	@PostConstruct
	public void init() {
		baseEJB = pedestreEJB;
		acessoPermitido = false;
		modoConvite = false;
		modoPrecadastro = false;

		try {
			idCliente = Long.valueOf(getRequest().getParameter("cliente"));
			token = Long.valueOf(getRequest().getParameter("token"));
			String idPedestreParam = getRequest().getParameter("idPedestre");
			String idEmpresaParam = getRequest().getParameter("idEmpresa");

			if (idPedestreParam != null && !idPedestreParam.trim().isEmpty()) {
				idPedestre = Long.valueOf(idPedestreParam);
				iniciarModoPrecadastro();
			} else {
				if (idEmpresaParam != null && !idEmpresaParam.trim().isEmpty()) {
					idEmpresaUrl = Long.valueOf(idEmpresaParam);
				}
				iniciarModoConvite();
			}

		} catch (Exception e) {
			LOG.log(Level.WARNING, "Parâmetros inválidos no link de cadastro facial", e);
			mensagemFatal("", "msg.link.cadastro.facial.invalido");
		}
	}

	private void iniciarModoPrecadastro() {
		modoPrecadastro = true;

		if (!isTokenValido(token)) {
			mensagemFatal("", "msg.link.cadastro.facial.invalido");
			return;
		}

		pedestre = buscaPedestrePeloId(idPedestre, idCliente);
		if (pedestre == null) {
			mensagemFatal("", "msg.link.cadastro.facial.invalido");
			return;
		}

		carregarHorariosDasRegras(pedestre);
		pedestre.migrarLegadoEmpresaVisitadaSeNecessario();
		carregarEmpresaVisitadaUiLink();

		if (!pedestre.autoAtendimentoLiberado()) {
			mensagemFatal("", "msg.link.cadastro.facial.nao.liberado");
			return;
		}

		cadastroExterno = buscaCadastroExternoPrecadastro(idPedestre, idCliente, token);
		if (cadastroExterno == null) {
			mensagemFatal("", "msg.link.cadastro.facial.invalido");
			return;
		}

		step = STEP_FOTO;
		acessoPermitido = true;
	}

	private void iniciarModoConvite() {
		modoConvite = true;

		if (!isTokenValido(token)) {
			mensagemFatal("", "msg.link.cadastro.facial.invalido");
			return;
		}

		cadastroExterno = buscaCadastroExternoConvite(idCliente, token);
		if (cadastroExterno == null) {
			mensagemFatal("", "msg.link.cadastro.facial.invalido");
			return;
		}

		EmpresaEntity empresaConvite = cadastroExterno.getEmpresa();
		if (empresaConvite == null) {
			mensagemFatal("", "msg.link.cadastro.facial.invalido");
			return;
		}

		EmpresaEntity empresaValida = buscaEmpresaPorIdCliente(empresaConvite.getId(), idCliente);
		if (empresaValida == null) {
			mensagemFatal("", "msg.link.cadastro.facial.invalido");
			return;
		}

		if (idEmpresaUrl != null && !idEmpresaUrl.equals(empresaConvite.getId())) {
			mensagemFatal("", "msg.link.cadastro.facial.invalido");
			return;
		}

		idEmpresaSelecionada = empresaConvite.getId();
		montarListaEmpresas();

		pedestre = new PedestreEntity();
		pedestre.setCliente(cadastroExterno.getCliente());
		empresaVisitadaUi = empresaConvite.getNome();

		step = STEP_CPF;
		acessoPermitido = true;
	}

	public void verificarCpfLink() {
		if (!modoConvite || pedestre == null) {
			return;
		}

		String cpf = pedestre.getCpf();
		if (cpf == null || cpf.replaceAll("\\D", "").length() < 11) {
			mensagemFatal("", "msg.link.cadastro.facial.cpf.invalido");
			return;
		}

		String cpfLimpo = cpf.replaceAll("\\D", "");
		pedestre.setCpf(cpfLimpo);

		PedestreEntity encontrado = buscaPedestrePeloCpf(cpfLimpo, idCliente);

		if (encontrado != null) {
			if (encontrado.isPedestre()) {
				mensagemFatal("", "msg.link.cadastro.facial.colaborador");
				return;
			}

			pedestre = encontrado;
			carregarHorariosDasRegras(pedestre);
			dadosSomenteLeitura = true;
			pedestre.migrarLegadoEmpresaVisitadaSeNecessario();
			carregarEmpresaVisitadaUiLink();

			mensagemInfo("", "msg.link.cadastro.facial.cpf.encontrado");
			step = STEP_FOTO;
			return;
		}

		dadosSomenteLeitura = false;
		pedestre.setNome(null);
		step = STEP_DADOS;
	}

	public void avancarParaFoto() {
		if (!modoConvite) {
			return;
		}
		if (pedestre.getNome() == null || pedestre.getNome().trim().isEmpty()) {
			mensagemFatal("", "msg.link.cadastro.facial.nome.obrigatorio");
			return;
		}
		if (!resolverEmpresaVisitada()) {
			mensagemFatal("", "msg.link.cadastro.facial.empresa.visitada.obrigatoria");
			return;
		}
		step = STEP_FOTO;
	}

	public String salvar() {
		if (!validarAcessoParaSalvar()) {
			return null;
		}

		if (pedestre.getFoto() == null || pedestre.getFoto().length == 0) {
			mensagemFatal("", "msg.link.cadastro.facial.foto.obrigatoria");
			return null;
		}

		try {
			if (modoConvite) {
				if (!prepararPedestreConviteParaGravacao()) {
					return null;
				}
			}

			pedestre.setDataAlteracaoFoto(new Date());
			pedestre.setDataCadastroFotoNaHikivision(new Date());

			String jsonStr = gson.toJson(WebSocketPedestrianAccessTO.fromPedestre(pedestre));
			JsonObject json = JsonParser.parseString(jsonStr).getAsJsonObject();

			String resposta = WebSocketCadastroEndpoint.enviarEEsperar(pedestre.getCliente().getId().toString(),
					json.toString());

			if (!"ok".equals(resposta)) {
				tratarErroHikivision(resposta);
				return null;
			}

			if (pedestre.getId() == null) {
				baseEJB.gravaObjeto(pedestre);
			} else {
				baseEJB.alteraObjeto(pedestre);
			}

			finalizarCadastroExterno();

			if (modoPrecadastro) {
				desligarLiberacaoFacialLink();
			}

			LOG.info(String.format("Cadastro facial por link concluído: cliente=%s pedestre=%s convite=%s", idCliente,
					pedestre.getId(), modoConvite));

			FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
			mensagemInfo("", "msg.info.pedestre.gravado.sucesso");
			redirect("/capturaDeFaceConcluida.xhtml");
			return null;

		} catch (TimeoutException e) {
			FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
			mensagemAviso("", "msg.info.foto.nao.enviada_time_out");
			return null;

		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Erro ao salvar cadastro facial por link", e);
			FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
			mensagemAviso("", "msg.info.usuario.nao.enviada");
			return null;
		}
	}

	private boolean prepararPedestreConviteParaGravacao() throws Exception {
		if (!resolverEmpresaVisitada()) {
			mensagemFatal("", "msg.link.cadastro.facial.empresa.visitada.obrigatoria");
			return false;
		}

		String cpfLimpo = pedestre.getCpf() != null ? pedestre.getCpf().replaceAll("\\D", "") : "";
		if (cpfLimpo.length() < 11) {
			mensagemFatal("", "msg.link.cadastro.facial.cpf.invalido");
			return false;
		}
		pedestre.setCpf(cpfLimpo);

		if (pedestre.getId() == null) {
			PedestreEntity duplicado = buscaPedestrePeloCpf(cpfLimpo, idCliente);
			if (duplicado != null) {
				if (duplicado.isPedestre()) {
					mensagemFatal("", "msg.link.cadastro.facial.colaborador");
					return false;
				}
				pedestre = duplicado;
				carregarHorariosDasRegras(pedestre);
			}
		}

		if (pedestre.getId() == null) {
			pedestre.setTipo(TipoPedestre.VISITANTE);
			pedestre.setStatus(Status.ATIVO);
			if (pedestre.getCliente() == null) {
				pedestre.setCliente(cadastroExterno.getCliente());
			}
			aplicarRegraPadraoVisitante(pedestre);
			if (pedestre.getCodigoCartaoAcesso() == null || pedestre.getCodigoCartaoAcesso().trim().isEmpty()) {
				pedestre.setCodigoCartaoAcesso(cpfLimpo);
			}
		}

		normalizarListasPedestre(pedestre);
		return true;
	}

	private boolean resolverEmpresaVisitada() {
		if (pedestre == null) {
			return false;
		}
		String texto = empresaVisitadaUi != null ? empresaVisitadaUi.trim() : "";
		if (texto.isEmpty()) {
			return false;
		}
		EmpresaEntity ref = pedestre.getEmpresaVisitadaRef();
		if (ref != null && ref.getId() != null && ref.getNome() != null
				&& texto.equalsIgnoreCase(ref.getNome().trim())) {
			EmpresaEntity valida = buscaEmpresaPorIdCliente(ref.getId(), idCliente);
			pedestre.aplicarEmpresaVisitadaInformada(texto, valida != null ? valida : ref);
			return true;
		}
		EmpresaEntity porNome = listarEmpresasDoCliente(idCliente).stream()
				.filter(e -> e.getNome() != null && texto.equalsIgnoreCase(e.getNome().trim()))
				.findFirst().orElse(null);
		if (porNome == null && idEmpresaSelecionada != null) {
			porNome = buscaEmpresaPorIdCliente(idEmpresaSelecionada, idCliente);
		}
		pedestre.aplicarEmpresaVisitadaInformada(texto, porNome);
		return true;
	}

	public List<EmpresaEntity> completeEmpresaVisitadaLink(String query) {
		String q = query != null ? query.trim().toLowerCase() : "";
		return listarEmpresasDoCliente(idCliente).stream()
				.filter(e -> e.getNome() != null && (q.isEmpty() || e.getNome().toLowerCase().contains(q)))
				.limit(20)
				.collect(Collectors.toList());
	}

	public void onEmpresaVisitadaLinkSelect(SelectEvent event) {
		if (pedestre == null) {
			return;
		}
		normalizarEmpresaVisitadaUiLink();
		Object selected = event.getObject();
		if (selected == null) {
			selected = empresaVisitadaUi;
		}
		if (selected == null) {
			return;
		}
		EmpresaEntity ref = null;
		String nome;
		if (selected instanceof EmpresaEntity) {
			ref = (EmpresaEntity) selected;
			nome = ref.getNome() != null ? ref.getNome().trim() : "";
		} else {
			nome = selected.toString().trim();
			if ("null".equalsIgnoreCase(nome)) {
				return;
			}
			if (!nome.isEmpty()) {
				final String nomeBusca = nome;
				ref = listarEmpresasDoCliente(idCliente).stream()
						.filter(emp -> emp.getNome() != null && nomeBusca.equalsIgnoreCase(emp.getNome().trim()))
						.findFirst().orElse(null);
			}
		}
		if (nome.isEmpty()) {
			return;
		}
		empresaVisitadaUi = nome;
		pedestre.aplicarEmpresaVisitadaInformada(nome, ref);
		if (ref != null && ref.getId() != null) {
			idEmpresaSelecionada = ref.getId();
		}
		normalizarEmpresaVisitadaUiLink();
		if (textoEmpresaVisitadaValidoLink(empresaVisitadaUi)) {
			PrimeFaces.current().ajax().addCallbackParam("empresaVisitadaNome", empresaVisitadaUi);
		}
	}

	private static boolean textoEmpresaVisitadaValidoLink(String texto) {
		if (texto == null) {
			return false;
		}
		String t = texto.trim();
		return !t.isEmpty() && !"null".equalsIgnoreCase(t);
	}

	private void normalizarEmpresaVisitadaUiLink() {
		if (!textoEmpresaVisitadaValidoLink(empresaVisitadaUi)) {
			empresaVisitadaUi = null;
		} else {
			empresaVisitadaUi = empresaVisitadaUi.trim();
		}
	}

	public void onEmpresaVisitadaLinkBlur() {
		if (pedestre != null) {
			resolverEmpresaVisitada();
			carregarEmpresaVisitadaUiLink();
		}
	}

	private void carregarEmpresaVisitadaUiLink() {
		if (pedestre == null) {
			empresaVisitadaUi = null;
			return;
		}
		String exib = pedestre.getEmpresaVisitadaExibicao();
		if (exib != null && !exib.isEmpty()) {
			empresaVisitadaUi = exib;
			return;
		}
		if (cadastroExterno != null && cadastroExterno.getEmpresa() != null) {
			empresaVisitadaUi = cadastroExterno.getEmpresa().getNome();
		}
	}

	private void tratarErroHikivision(String resposta) {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
		if (resposta.contains("UsuarioErro")) {
			mensagemAviso("", "msg.info.usuario.nao.enviada");
		} else if (resposta.contains("CartaoErro")) {
			mensagemAviso("", "msg.info.cartao.nao.enviada");
		} else if (resposta.contains("FotoErro")) {
			mensagemAviso("", "msg.info.foto.nao.enviada");
		} else {
			mensagemAviso("", "msg.info.usuario.nao.enviada");
		}
	}

	private boolean validarAcessoParaSalvar() {
		if (cadastroExterno == null || token == null || !isTokenValido(token)) {
			mensagemFatal("", "msg.link.cadastro.facial.invalido");
			return false;
		}

		if (modoPrecadastro) {
			if (pedestre == null || idPedestre == null) {
				mensagemFatal("", "msg.link.cadastro.facial.invalido");
				return false;
			}
			if (!pedestre.autoAtendimentoLiberado()) {
				mensagemFatal("", "msg.link.cadastro.facial.nao.liberado");
				return false;
			}
			if (buscaCadastroExternoPrecadastro(idPedestre, idCliente, token) == null) {
				mensagemFatal("", "msg.link.cadastro.facial.invalido");
				return false;
			}
		} else if (modoConvite) {
			if (buscaCadastroExternoConvite(idCliente, token) == null) {
				mensagemFatal("", "msg.link.cadastro.facial.invalido");
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	private void finalizarCadastroExterno() throws Exception {
		cadastroExterno.setDataCadastroDaFace(new Date());
		cadastroExterno.setToken(null);
		cadastroExterno.setStatusCadastroExterno(StatusCadastroExterno.CADASTRADO);
		if (modoConvite && pedestre != null && pedestre.getId() != null) {
			cadastroExterno.setPedestre(pedestre);
		}
		baseEJB.gravaObjeto(cadastroExterno);
	}

	private void desligarLiberacaoFacialLink() {
		try {
			pedestre.setAutoAtendimento(false);
			pedestre.setAutoAtendimentoAt(null);
			baseEJB.alteraObjeto(pedestre);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Falha ao desligar liberação de cadastro facial por link", e);
		}
	}

	private void aplicarRegraPadraoVisitante(PedestreEntity p) throws Exception {
		if (p.getRegras() != null && !p.getRegras().isEmpty()) {
			return;
		}
		RegraEntity regra = buscaRegraPeloNome(BaseConstant.NOME_REGRA_PADRAO_VISITANTE, idCliente);
		if (regra == null) {
			regra = cadastraNovaRegraVisitante(idCliente);
		}
		PedestreRegraEntity pedestreRegra = new PedestreRegraEntity();
		pedestreRegra.setRegra(regra);
		pedestreRegra.setQtdeTotalDeCreditos(1L);
		pedestreRegra.setPedestre(p);
		p.setRegras(Arrays.asList(pedestreRegra));
	}

	private RegraEntity cadastraNovaRegraVisitante(Long idClienteRef) throws Exception {
		RegraEntity regra = new RegraEntity();
		regra.setNome(BaseConstant.NOME_REGRA_PADRAO_VISITANTE);
		regra.setTipoPedestre(TipoPedestre.VISITANTE);
		regra.setTipo(TipoRegra.ACESSO_UNICO);
		regra.setStatus(Status.ATIVO);
		ClienteEntity cliente = new ClienteEntity();
		cliente.setId(idClienteRef);
		regra.setCliente(cliente);
		return (RegraEntity) baseEJB.gravaObjeto(regra)[0];
	}

	@SuppressWarnings("unchecked")
	private RegraEntity buscaRegraPeloNome(String nomeRegra, Long idClienteRef) {
		Map<String, Object> args = new HashMap<>();
		args.put("NOME_REGRA", nomeRegra);
		args.put("ID_CLIENTE", idClienteRef);
		try {
			List<RegraEntity> regras = (List<RegraEntity>) baseEJB.pesquisaArgFixosLimitado(RegraEntity.class,
					"findByNome", args, 0, 1);
			if (regras != null && !regras.isEmpty()) {
				return regras.get(0);
			}
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Erro ao buscar regra padrão visitante", e);
		}
		return null;
	}

	private void normalizarListasPedestre(PedestreEntity p) {
		if (p.getEndereco() == null || p.getEndereco().getCep() == null
				|| p.getEndereco().getCep().trim().isEmpty()) {
			p.setEndereco(null);
		}
		if (p.isVisitante()) {
			p.aplicarEmpresaVisitadaInformada(
					p.getEmpresaVisitada() != null ? p.getEmpresaVisitada() : empresaVisitadaUi,
					p.getEmpresaVisitadaRef());
		} else if (p.getEmpresa() != null && p.getEmpresa().getId() == null) {
			p.setEmpresa(null);
		}
	}

	private void montarListaEmpresas() {
		listaEmpresas = new ArrayList<>();
		listaEmpresas.add(new SelectItem("", "Selecione..."));
		for (EmpresaEntity e : listarEmpresasDoCliente(idCliente)) {
			if (e.getId() != null && e.getNome() != null) {
				listaEmpresas.add(new SelectItem(e.getId(), e.getNome()));
			}
		}
	}

	private static boolean isTokenValido(Long tokenExpiracao) {
		return tokenExpiracao != null && tokenExpiracao >= System.currentTimeMillis();
	}

	private void carregarHorariosDasRegras(PedestreEntity p) {
		if (p == null || p.getRegras() == null) {
			return;
		}
		for (PedestreRegraEntity regra : p.getRegras()) {
			if (Objects.nonNull(regra.getRegra()) && regra.getRegra().getTipo() == TipoRegra.ACESSO_HORARIO) {
				regra.setHorarios(buscaHorariosByIdPedestreRegra(regra.getId()));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private CadastroExternoEntity buscaCadastroExternoPrecadastro(Long idPedestreRef, Long idClienteRef,
			Long tokenLink) {
		Map<String, Object> args = new HashMap<>();
		args.put("ID_PEDESTRE", idPedestreRef);
		args.put("ID_CLIENTE", idClienteRef);
		args.put("TOKEN", tokenLink);
		args.put("STATUS", StatusCadastroExterno.AGUARDANDO_CADASTRO);
		try {
			List<CadastroExternoEntity> cadastros = (List<CadastroExternoEntity>) pedestreEJB
					.pesquisaArgFixosLimitado(CadastroExternoEntity.class, "findByTokenAndIdPedestreAndIdCliente", args,
							0, 1);
			if (cadastros != null && !cadastros.isEmpty()) {
				return cadastros.get(0);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Erro ao buscar CadastroExterno pré-cadastro", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private CadastroExternoEntity buscaCadastroExternoConvite(Long idClienteRef, Long tokenLink) {
		Map<String, Object> args = new HashMap<>();
		args.put("ID_CLIENTE", idClienteRef);
		args.put("TOKEN", tokenLink);
		args.put("STATUS", StatusCadastroExterno.AGUARDANDO_CADASTRO);
		args.put("TIPO", TipoCadastroExterno.FACIAL_LINK_CONVITE);
		try {
			List<CadastroExternoEntity> cadastros = (List<CadastroExternoEntity>) pedestreEJB
					.pesquisaArgFixosLimitado(CadastroExternoEntity.class, "findByTokenConviteAndIdCliente", args, 0,
							1);
			if (cadastros != null && !cadastros.isEmpty()) {
				return cadastros.get(0);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Erro ao buscar CadastroExterno convite", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private PedestreEntity buscaPedestrePeloId(Long idPedestreRef, Long idClienteRef) {
		Map<String, Object> args = new HashMap<>();
		args.put("ID", idPedestreRef);
		args.put("ID_CLIENTE", idClienteRef);
		try {
			List<PedestreEntity> pedestres = (List<PedestreEntity>) pedestreEJB
					.pesquisaArgFixos(PedestreEntity.class, "findByIdWithEmpRegrasAndHorarios", args);
			if (pedestres != null) {
				return pedestres.stream().findFirst().orElse(null);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Erro ao buscar pedestre do link facial", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private PedestreEntity buscaPedestrePeloCpf(String cpf, Long idClienteRef) {
		Map<String, Object> args = new HashMap<>();
		args.put("CPF", cpf);
		args.put("ID_CLIENTE", idClienteRef);
		try {
			List<PedestreEntity> pedestres = (List<PedestreEntity>) pedestreEJB
					.pesquisaArgFixos(PedestreEntity.class, "findByCpfAndIdCliente", args);
			if (pedestres != null) {
				return pedestres.stream().findFirst().orElse(null);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Erro ao buscar pedestre por CPF", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<HorarioEntity> buscaHorariosByIdPedestreRegra(Long id) {
		Map<String, Object> args = new HashMap<>();
		args.put("ID", id);
		try {
			List<PedestreRegraEntity> pedestreRegras = (List<PedestreRegraEntity>) pedestreEJB
					.pesquisaArgFixos(PedestreRegraEntity.class, "findByIdComHorarios", args);
			if (pedestreRegras != null && !pedestreRegras.isEmpty()) {
				return pedestreRegras.get(0).getHorarios();
			}
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Erro ao buscar horários do pedestre", e);
		}
		return java.util.Collections.emptyList();
	}

	public void onCapture(CaptureEvent event) {
		if (pedestre == null || event == null || event.getData() == null) {
			return;
		}
		pedestre.setFoto(event.getData());
	}

	public StreamedContent getStreamedContent(byte[] foto) {
		if (foto != null) {
			return DefaultStreamedContent.builder().contentType("image/jpeg")
					.stream(() -> new ByteArrayInputStream(foto)).build();
		}
		return null;
	}

	public boolean isAcessoPermitido() {
		return acessoPermitido;
	}

	public boolean isModoConvite() {
		return modoConvite;
	}

	public boolean isModoPrecadastro() {
		return modoPrecadastro;
	}

	public boolean isDadosSomenteLeitura() {
		return dadosSomenteLeitura;
	}

	public boolean isEmpresaFixa() {
		return false;
	}

	public String getEmpresaVisitadaUi() {
		return empresaVisitadaUi;
	}

	public void setEmpresaVisitadaUi(String empresaVisitadaUi) {
		if (textoEmpresaVisitadaValidoLink(empresaVisitadaUi)) {
			this.empresaVisitadaUi = empresaVisitadaUi.trim();
		} else {
			this.empresaVisitadaUi = null;
		}
	}

	public boolean isExibeStepCpf() {
		return modoConvite && step == STEP_CPF;
	}

	public boolean isExibeStepDados() {
		return modoConvite && step == STEP_DADOS;
	}

	public boolean isExibeStepFoto() {
		return (modoConvite && step == STEP_FOTO) || modoPrecadastro;
	}

	public boolean isTemFoto() {
		return pedestre != null && pedestre.getFoto() != null && pedestre.getFoto().length > 0;
	}

	public PedestreEntity getPedestre() {
		return pedestre;
	}

	public CadastroExternoEntity getCadastroExterno() {
		return cadastroExterno;
	}

	public List<SelectItem> getListaEmpresas() {
		return listaEmpresas;
	}

	public Long getIdEmpresaSelecionada() {
		return idEmpresaSelecionada;
	}

	public void setIdEmpresaSelecionada(Long idEmpresaSelecionada) {
		this.idEmpresaSelecionada = idEmpresaSelecionada;
	}

}
