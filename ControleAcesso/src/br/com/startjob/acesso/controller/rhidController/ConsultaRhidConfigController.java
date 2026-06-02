package br.com.startjob.acesso.controller.rhidController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.naming.InitialContext;

import com.rhid.services.dto.RhidOperacaoResultDTO;

import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.ejb.RhidIntegracaoEJBRemote;
import br.com.startjob.acesso.modelo.entity.ConfiguracaoRhidEntity;
import br.com.startjob.acesso.modelo.entity.DominioRhidEntity;
import br.com.startjob.acesso.services.rhid.RhidAgendadorService;

@Named("consultaRhidConfigController")
@ViewScoped
public class ConsultaRhidConfigController extends BaseController {

	private static final long serialVersionUID = 1L;

	private static final String URL_CADASTRO = "/paginas/sistema/rhid/cadastroRhidConfig.xhtml";
	private static final String URL_PESQUISA = "/paginas/sistema/rhid/pesquisaRhidConfig.xhtml";

	@EJB
	private RhidIntegracaoEJBRemote rhidIntegracaoEJB;

	private List<ConfiguracaoRhidEntity> configuracoesCadastradas = new ArrayList<>();
	private Long configIdParaExclusao;
	private RhidOperacaoResultDTO ultimoResultado;
	private boolean processando;
	private String acao;
	private String paramBuscaEmail;

	@PostConstruct
	public void init() {
		carregarListaConfiguracoes();
		acao = getRequest().getParameter("acao");
	}

	public void novo() throws Exception {
		salvarUrlRetornoLista();
		redirect(URL_CADASTRO);
	}

	public void editar(Long id) throws Exception {
		if (id == null) {
			return;
		}
		salvarUrlRetornoLista();
		redirect(URL_CADASTRO + "?id=" + id);
	}

	@Override
	public String buscar() {
		carregarListaConfiguracoes();
		return "";
	}

	@Override
	public String limpar() {
		paramBuscaEmail = null;
		carregarListaConfiguracoes();
		return "";
	}

	public void excluirConfiguracaoConfirmada() {
		excluirConfiguracao(configIdParaExclusao);
	}

	public void excluirConfiguracao(Long configId) {
		if (configId == null) {
			exibirErro("Configuração inválida para exclusão.");
			return;
		}
		try {
			ConfiguracaoRhidEntity configExcluida = rhidIntegracaoEJB.buscarConfiguracaoPorId(configId);
			boolean tinhaExportacaoAutomatica = configExcluida != null
					&& Boolean.TRUE.equals(configExcluida.getExportacaoAutomatica());

			rhidIntegracaoEJB.excluirConfiguracao(configId);
			carregarListaConfiguracoes();
			configIdParaExclusao = null;

			if (tinhaExportacaoAutomatica) {
				try {
					lookupAgendadorService().sincronizarTimersExportacao();
				} catch (Exception e) {
					exibirErro("Configuração excluída, porém não foi possível sincronizar timers RHID: "
							+ extrairMensagem(e));
				}
			}

			exibirInfo("Configuração RHID excluída com sucesso.");
		} catch (IllegalArgumentException e) {
			exibirErro(e.getMessage());
		} catch (Exception e) {
			exibirErro("Falha ao excluir configuração: " + extrairMensagem(e));
		}
	}

	public void executarImportacaoCompletaConfig(Long configId) {
		executarImportacaoConfig(configId, true);
	}

	public void executarImportacaoIncrementalConfig(Long configId) {
		executarImportacaoConfig(configId, false);
	}

	@Override
	public void exibeMensagens(ComponentSystemEvent evt) throws AbortProcessingException {
		super.exibeMensagens(evt);
		if ("OK".equals(acao)) {
			exibirInfo("Configuração RHID salva com sucesso.");
		}
		acao = null;
	}

	public void redirecionarParaPesquisa() {
		redirect(URL_PESQUISA);
	}

	private void carregarListaConfiguracoes() {
		try {
			configuracoesCadastradas = rhidIntegracaoEJB.listarConfiguracoes();
			if (configuracoesCadastradas == null) {
				configuracoesCadastradas = new ArrayList<>();
			}
			if (paramBuscaEmail != null && !paramBuscaEmail.trim().isEmpty()) {
				String filtro = paramBuscaEmail.trim().toLowerCase();
				List<ConfiguracaoRhidEntity> filtradas = new ArrayList<>();
				for (ConfiguracaoRhidEntity cfg : configuracoesCadastradas) {
					if (cfg.getEmail() != null && cfg.getEmail().toLowerCase().contains(filtro)) {
						filtradas.add(cfg);
					}
				}
				configuracoesCadastradas = filtradas;
			}
		} catch (Exception e) {
			configuracoesCadastradas = new ArrayList<>();
			exibirErro("Falha ao listar configurações RHID: " + extrairMensagem(e));
		}
	}

	private void executarImportacaoConfig(Long configId, boolean completa) {
		if (processando) {
			return;
		}
		processando = true;
		try {
			ultimoResultado = rhidIntegracaoEJB.exportarRhidPorId(configId, completa);
			carregarListaConfiguracoes();
			ConfiguracaoRhidEntity config = rhidIntegracaoEJB.buscarConfiguracaoPorId(configId);
			sincronizarTimersAposImportacao(config);
			exibirInfo("Importação " + (completa ? "completa" : "incremental") + " concluída para "
					+ config.getEmail() + ". Criados: " + ultimoResultado.getTotalCriados()
					+ ", atualizados: " + ultimoResultado.getTotalAtualizados() + ", erros: "
					+ ultimoResultado.getTotalErros() + ".");
		} catch (IllegalArgumentException e) {
			ultimoResultado = criarResultadoErro(completa, e.getMessage());
			exibirErro(e.getMessage());
		} catch (Exception e) {
			String msg = extrairMensagem(e);
			ultimoResultado = criarResultadoErro(completa, msg);
			exibirErro("Falha na importação RHID: " + msg);
		} finally {
			processando = false;
		}
	}

	private RhidOperacaoResultDTO criarResultadoErro(boolean completa, String mensagem) {
		RhidOperacaoResultDTO dto = new RhidOperacaoResultDTO();
		dto.setCompleta(completa);
		dto.incrementErros();
		dto.addMensagem(mensagem);
		return dto;
	}

	private void sincronizarTimersAposImportacao(ConfiguracaoRhidEntity config) {
		if (config == null || !Boolean.TRUE.equals(config.getExportacaoAutomatica())) {
			return;
		}
		if (ultimoResultado == null || ultimoResultado.getTotalErros() > 0) {
			return;
		}
		if (config.getUltimaExportacao() == null) {
			return;
		}
		try {
			lookupAgendadorService().sincronizarTimersExportacao();
		} catch (Exception e) {
			exibirErro("Importação concluída, porém não foi possível sincronizar timers RHID: "
					+ extrairMensagem(e));
		}
	}

	public String formatarDominios(ConfiguracaoRhidEntity config) {
		if (config == null || config.getDominios() == null || config.getDominios().isEmpty()) {
			return "-";
		}
		StringBuilder sb = new StringBuilder();
		for (DominioRhidEntity dominio : config.getDominios()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(dominio.getNomeDominio());
		}
		return sb.toString();
	}

	public String formatarUltimaExportacao(Date data) {
		if (data == null) {
			return "Nunca";
		}
		return new SimpleDateFormat("dd/MM/yyyy").format(data);
	}

	private RhidAgendadorService lookupAgendadorService() throws Exception {
		InitialContext ctx = new InitialContext();
		try {
			return (RhidAgendadorService) ctx.lookup(
					"java:global/ControleAcesso/ControleAcesso/RhidAgendadorService!br.com.startjob.acesso.services.rhid.RhidAgendadorService");
		} catch (Exception e) {
			return (RhidAgendadorService) ctx.lookup(
					"java:module/RhidAgendadorService!br.com.startjob.acesso.services.rhid.RhidAgendadorService");
		}
	}

	private String extrairMensagem(Exception e) {
		Throwable causa = e;
		while (causa != null) {
			if (causa instanceof javax.persistence.OptimisticLockException) {
				return "Registro alterado por outra operação. Recarregue a página e tente novamente.";
			}
			if (causa.getMessage() != null && !causa.getMessage().startsWith("msg.")) {
				return causa.getMessage();
			}
			causa = causa.getCause();
		}
		return "Erro inesperado ao processar a operação.";
	}

	private void exibirInfo(String mensagem) {
		addFacesMessage(FacesMessage.SEVERITY_INFO, mensagem);
	}

	private void exibirErro(String mensagem) {
		addFacesMessage(FacesMessage.SEVERITY_ERROR, mensagem);
	}

	private void addFacesMessage(FacesMessage.Severity severity, String mensagem) {
		javax.faces.context.FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(severity, "*", mensagem));
	}

	public List<ConfiguracaoRhidEntity> getConfiguracoesCadastradas() {
		return configuracoesCadastradas;
	}

	public Long getConfigIdParaExclusao() {
		return configIdParaExclusao;
	}

	public void setConfigIdParaExclusao(Long configIdParaExclusao) {
		this.configIdParaExclusao = configIdParaExclusao;
	}

	public RhidOperacaoResultDTO getUltimoResultado() {
		return ultimoResultado;
	}

	public boolean isProcessando() {
		return processando;
	}

	public String getParamBuscaEmail() {
		return paramBuscaEmail;
	}

	public void setParamBuscaEmail(String paramBuscaEmail) {
		this.paramBuscaEmail = paramBuscaEmail;
	}
}
