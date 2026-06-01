package br.com.startjob.acesso.controller.uc008;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.model.StreamedContent;

import com.google.gson.Gson;

import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.controller.MenuController;
import br.com.startjob.acesso.service.FacialWebSocketHelper;
import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.CadastroExternoEntity;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.enumeration.StatusCadastroExterno;
import br.com.startjob.acesso.service.TotemAprovacaoPedestreHelper;
import br.com.startjob.acesso.service.TotemAprovacaoService;
import br.com.startjob.acesso.utils.Utils;

@SuppressWarnings("serial")
@Named("aprovacaoTotemController")
@ViewScoped
public class AprovacaoTotemController extends BaseController {

	private static final Logger LOG = Logger.getLogger(AprovacaoTotemController.class.getName());

	@EJB
	private PedestreEJBRemote pedestreEJB;

	@Inject
	private MenuController menuController;

	private final Gson gson = new Gson();
	private TotemAprovacaoService totemService;
	private List<CadastroExternoEntity> pendentes = new ArrayList<>();
	private CadastroExternoEntity selecionado;
	private String motivoRecusa;
	private boolean envioFacial;

	@PostConstruct
	public void init() {
		baseEJB = pedestreEJB;
		totemService = new TotemAprovacaoService(baseEJB);
		carregarParametroEnvioFacial();
		carregarPendentes();
		atualizarIndicadorMenu();
	}

	private void carregarParametroEnvioFacial() {
		envioFacial = false;
		try {
			ParametroEntity param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.ENVIO_FACIAL,
					getUsuarioLogado().getCliente().getId());
			if (param != null) {
				envioFacial = Boolean.parseBoolean(param.getValor());
			}
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Erro ao ler ENVIO_FACIAL", e);
		}
	}

	public void carregarPendentes() {
		try {
			pendentes = totemService.listarPendentes(getUsuarioLogado().getCliente().getId());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Erro ao listar solicitações totem", e);
			pendentes = new ArrayList<>();
			mensagemFatal("", "Não foi possível carregar as solicitações pendentes.");
		}
	}

	public void prepararAprovacao(CadastroExternoEntity item) {
		selecionado = item;
	}

	public void prepararRecusa(CadastroExternoEntity item) {
		selecionado = item;
		motivoRecusa = null;
	}

	public void aprovar() {
		if (selecionado == null || selecionado.getId() == null) {
			return;
		}
		try {
			Long idCliente = getUsuarioLogado().getCliente().getId();
			CadastroExternoEntity sol = totemService.buscarPorId(selecionado.getId(), idCliente);
			if (sol == null || !StatusCadastroExterno.AGUARDANDO_APROVACAO.equals(sol.getStatusCadastroExterno())) {
				mensagemAviso("", "Solicitação não encontrada ou já decidida.");
				carregarPendentes();
				return;
			}

			PedestreEntity pedestre = TotemAprovacaoPedestreHelper.montarPedestreFromSolicitacao(sol, baseEJB,
					getUsuarioLogado());
			pedestre = TotemAprovacaoPedestreHelper.persistir(pedestre, baseEJB);

			boolean facialOk = !envioFacial || FacialWebSocketHelper.enviarPedestre(pedestre, gson);
			if (facialOk) {
				totemService.marcarAprovado(sol, pedestre);
				mensagemInfo("", "Visitante aprovado e liberado com sucesso.");
			} else {
				totemService.manterPendenteAposFalhaFacial(sol, pedestre,
						"Falha ao enviar aos equipamentos faciais — tente aprovar novamente.");
				mensagemAviso("", "Visitante gravado, mas o envio facial falhou. A solicitação permanece pendente.");
			}
			carregarPendentes();
			atualizarIndicadorMenu();
			PrimeFaces.current().executeScript(
					"PF('dlgAprovarTotem').hide(); if (typeof rcAtualizarBadgePendentes === 'function') { rcAtualizarBadgePendentes(); }");

		} catch (IllegalStateException e) {
			mensagemFatal("", e.getMessage());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Erro ao aprovar solicitação totem", e);
			mensagemFatal("", "Não foi possível aprovar a solicitação.");
		}
	}

	public void recusar() {
		if (selecionado == null || selecionado.getId() == null) {
			return;
		}
		if (motivoRecusa == null || motivoRecusa.trim().isEmpty()) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Informe o motivo da recusa.", null);
			getFacesContext().addMessage(null, msg);
			return;
		}
		try {
			Long idCliente = getUsuarioLogado().getCliente().getId();
			CadastroExternoEntity sol = totemService.buscarPorId(selecionado.getId(), idCliente);
			if (sol == null || !StatusCadastroExterno.AGUARDANDO_APROVACAO.equals(sol.getStatusCadastroExterno())) {
				mensagemAviso("", "Solicitação não encontrada ou já decidida.");
				carregarPendentes();
				return;
			}
			totemService.marcarRecusado(sol, motivoRecusa);
			mensagemInfo("", "Solicitação recusada.");
			carregarPendentes();
			atualizarIndicadorMenu();
			PrimeFaces.current().executeScript(
					"PF('dlgRecusarTotem').hide(); if (typeof rcAtualizarBadgePendentes === 'function') { rcAtualizarBadgePendentes(); }");

		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Erro ao recusar solicitação totem", e);
			mensagemFatal("", "Não foi possível recusar a solicitação.");
		}
	}

	private void atualizarIndicadorMenu() {
		if (menuController != null) {
			try {
				menuController.recarregarMenuPendentesAjax();
			} catch (Exception e) {
				menuController.invalidarMenuAprovacoesPendentes();
			}
		}
	}

	public StreamedContent fotoSolicitacao(CadastroExternoEntity item) {
		if (item == null || item.getPrimeiraFoto() == null) {
			return null;
		}
		return Utils.getStreamedContent(item.getPrimeiraFoto());
	}

	public String formatarCpf(String cpf) {
		if (cpf == null) {
			return "";
		}
		String v = cpf.replaceAll("\\D", "");
		if (v.length() != 11) {
			return cpf;
		}
		return v.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
	}

	public List<CadastroExternoEntity> getPendentes() {
		return pendentes;
	}

	public CadastroExternoEntity getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(CadastroExternoEntity selecionado) {
		this.selecionado = selecionado;
	}

	public String getMotivoRecusa() {
		return motivoRecusa;
	}

	public void setMotivoRecusa(String motivoRecusa) {
		this.motivoRecusa = motivoRecusa;
	}

}
