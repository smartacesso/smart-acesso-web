package br.com.startjob.acesso.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.StreamedContent;

import com.google.gson.Gson;

import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.service.FacialWebSocketHelper;
import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.CadastroExternoEntity;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.enumeration.StatusCadastroExterno;
import br.com.startjob.acesso.service.TotemAprovacaoPedestreHelper;
import br.com.startjob.acesso.service.TotemAprovacaoService;
import br.com.startjob.acesso.utils.Utils;

/**
 * Aprovação/recusa de visitante do totem via link público ({@code cliente} + {@code token}).
 */
@SuppressWarnings("serial")
@Named("aprovacaoTotemPublicController")
@ViewScoped
public class AprovacaoTotemPublicController extends BaseController {

	private static final Logger LOG = Logger.getLogger(AprovacaoTotemPublicController.class.getName());

	@EJB
	private PedestreEJBRemote pedestreEJB;

	private final Gson gson = new Gson();
	private TotemAprovacaoService totemService;

	private Long idCliente;
	private Long token;
	private boolean acessoPermitido;
	private CadastroExternoEntity solicitacao;
	private String motivoRecusa;
	private boolean envioFacial;

	@PostConstruct
	public void init() {
		baseEJB = pedestreEJB;
		totemService = new TotemAprovacaoService(baseEJB);
		acessoPermitido = false;

		try {
			idCliente = Long.valueOf(getRequest().getParameter("cliente"));
			token = Long.valueOf(getRequest().getParameter("token"));
			solicitacao = totemService.buscarPendentePorToken(idCliente, token);
			acessoPermitido = solicitacao != null;
			if (acessoPermitido) {
				carregarParametroEnvioFacial();
			}
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Link de aprovação totem inválido", e);
			acessoPermitido = false;
		}
	}

	private void carregarParametroEnvioFacial() {
		envioFacial = false;
		try {
			ParametroEntity param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.ENVIO_FACIAL, idCliente);
			if (param != null) {
				envioFacial = Boolean.parseBoolean(param.getValor());
			}
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Erro ao ler ENVIO_FACIAL (público)", e);
		}
	}

	public void aprovar() {
		if (!acessoPermitido || solicitacao == null) {
			return;
		}
		try {
			CadastroExternoEntity sol = totemService.buscarPendentePorToken(idCliente, token);
			if (sol == null) {
				acessoPermitido = false;
				mensagemFatal("", "Link expirado ou solicitação já decidida.");
				return;
			}

			PedestreEntity pedestre = TotemAprovacaoPedestreHelper.montarPedestreFromSolicitacao(sol, baseEJB, null);
			pedestre = TotemAprovacaoPedestreHelper.persistir(pedestre, baseEJB);

			boolean facialOk = !envioFacial || FacialWebSocketHelper.enviarPedestre(pedestre, gson);
			if (facialOk) {
				totemService.marcarAprovado(sol, pedestre);
				acessoPermitido = false;
				mensagemInfo("", "Visitante aprovado com sucesso.");
			} else {
				totemService.manterPendenteAposFalhaFacial(sol, pedestre,
						"Falha ao enviar aos equipamentos faciais — tente aprovar novamente.");
				mensagemAviso("", "Visitante gravado, mas o envio facial falhou. A solicitação permanece pendente.");
			}

		} catch (IllegalStateException e) {
			mensagemFatal("", e.getMessage());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Erro ao aprovar via link público", e);
			mensagemFatal("", "Não foi possível aprovar. Tente novamente ou contate a portaria.");
		}
	}

	public void recusar() {
		if (!acessoPermitido || solicitacao == null) {
			return;
		}
		if (motivoRecusa == null || motivoRecusa.trim().isEmpty()) {
			mensagemAviso("", "Informe o motivo da recusa.");
			return;
		}
		try {
			CadastroExternoEntity sol = totemService.buscarPendentePorToken(idCliente, token);
			if (sol == null) {
				acessoPermitido = false;
				mensagemFatal("", "Link expirado ou solicitação já decidida.");
				return;
			}
			totemService.marcarRecusado(sol, motivoRecusa);
			acessoPermitido = false;
			mensagemInfo("", "Solicitação recusada.");

		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Erro ao recusar via link público", e);
			mensagemFatal("", "Não foi possível recusar a solicitação.");
		}
	}

	public StreamedContent getFotoStream() {
		if (solicitacao == null || solicitacao.getPrimeiraFoto() == null) {
			return null;
		}
		return Utils.getStreamedContent(solicitacao.getPrimeiraFoto());
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

	public boolean isAcessoPermitido() {
		return acessoPermitido;
	}

	public CadastroExternoEntity getSolicitacao() {
		return solicitacao;
	}

	public String getMotivoRecusa() {
		return motivoRecusa;
	}

	public void setMotivoRecusa(String motivoRecusa) {
		this.motivoRecusa = motivoRecusa;
	}

}
