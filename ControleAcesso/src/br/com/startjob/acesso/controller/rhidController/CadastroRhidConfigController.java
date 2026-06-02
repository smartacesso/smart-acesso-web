package br.com.startjob.acesso.controller.rhidController;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.naming.InitialContext;

import com.rhid.services.dto.RhidOperacaoResultDTO;
import com.rhid.services.RhidSemDominioAcaoEnum;

import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.ejb.RhidIntegracaoEJBRemote;
import br.com.startjob.acesso.modelo.entity.ConfiguracaoRhidEntity;
import br.com.startjob.acesso.modelo.entity.DominioRhidEntity;
import br.com.startjob.acesso.services.rhid.RhidAgendadorService;

@Named("cadastroRhidConfigController")
@ViewScoped
public class CadastroRhidConfigController extends BaseController {

	private static final long serialVersionUID = 1L;

	private static final String URL_PESQUISA = "/paginas/sistema/rhid/pesquisaRhidConfig.xhtml";
	private static final String URL_CADASTRO = "/paginas/sistema/rhid/cadastroRhidConfig.xhtml";

	@EJB
	private RhidIntegracaoEJBRemote rhidIntegracaoEJB;

	private ConfiguracaoRhidEntity configuracao;
	private String novoDominio;
	private RhidOperacaoResultDTO ultimoResultado;
	private boolean processando;
	private boolean podeSalvarConfig;
	private String acao;

	@PostConstruct
	public void init() {
		acao = getRequest().getParameter("acao");
		String idParam = getRequest().getParameter("id");
		if (idParam != null && !idParam.trim().isEmpty()) {
			try {
				carregarConfiguracao(Long.valueOf(idParam.trim()));
			} catch (NumberFormatException e) {
				exibirErro("Identificador de configuração inválido.");
				configuracao = novaConfiguracao();
				atualizarPodeSalvarConfig();
			}
		} else {
			configuracao = novaConfiguracao();
			atualizarPodeSalvarConfig();
		}
	}

	@Override
	public String salvar() {
		try {
			boolean cadastroNovo = configuracao == null || configuracao.getId() == null;
			configuracao = rhidIntegracaoEJB.salvarConfiguracao(configuracao);
			atualizarPodeSalvarConfig();
			novoDominio = null;
			agendarRotinaSeNecessario();
			if (cadastroNovo) {
				redirect(URL_CADASTRO + "?id=" + configuracao.getId() + "&acao=OK");
			} else {
				redirect(URL_PESQUISA + "?acao=OK");
			}
		} catch (IllegalArgumentException e) {
			exibirErro(e.getMessage());
		} catch (Exception e) {
			exibirErro("Falha ao salvar configurações: " + extrairMensagem(e));
		}
		return "";
	}

	@Override
	public void exibeMensagens(ComponentSystemEvent evt) throws AbortProcessingException {
		super.exibeMensagens(evt);
		if ("OK".equals(acao)) {
			exibirInfo("Configuração RHID salva com sucesso. Você já pode executar a importação.");
		}
		acao = null;
	}

	public void adicionarDominio() {
		if (configuracao == null) {
			configuracao = novaConfiguracao();
		}
		if (novoDominio == null || novoDominio.trim().isEmpty()) {
			exibirErro("Informe o nome do domínio RHID.");
			return;
		}

		String nome = novoDominio.trim();
		if (configuracao.getDominios() == null) {
			configuracao.setDominios(new ArrayList<DominioRhidEntity>());
		}

		for (DominioRhidEntity dom : configuracao.getDominios()) {
			if (nome.equalsIgnoreCase(dom.getNomeDominio())) {
				exibirErro("Domínio já incluído na lista.");
				return;
			}
		}

		DominioRhidEntity dominio = new DominioRhidEntity();
		dominio.setNomeDominio(nome);
		dominio.setConfiguracao(configuracao);
		configuracao.getDominios().add(dominio);
		atualizarPodeSalvarConfig();

		novoDominio = "";
		exibirInfo("Domínio adicionado. Clique em Salvar para persistir.");
	}

	public void removerDominio(DominioRhidEntity dominio) {
		if (configuracao.getDominios() != null) {
			configuracao.getDominios().remove(dominio);
		}
		atualizarPodeSalvarConfig();
		if (!podeSalvarConfig) {
			exibirErro("Ao menos um domínio é obrigatório para login e importação no RHID.");
		} else {
			exibirInfo("Domínio removido. Clique em Salvar para persistir.");
		}
	}

	public void executarImportacaoCompleta() {
		executarImportacao(true);
	}

	public void executarImportacaoIncremental() {
		executarImportacao(false);
	}

	private void carregarConfiguracao(Long id) {
		try {
			configuracao = rhidIntegracaoEJB.buscarConfiguracaoPorId(id);
			if (configuracao.getDominios() == null) {
				configuracao.setDominios(new ArrayList<DominioRhidEntity>());
			} else {
				configuracao.getDominios().size();
			}
			novoDominio = null;
			atualizarPodeSalvarConfig();
		} catch (Exception e) {
			exibirErro("Falha ao carregar configuração: " + extrairMensagem(e));
			configuracao = novaConfiguracao();
			atualizarPodeSalvarConfig();
		}
	}

	private void atualizarPodeSalvarConfig() {
		podeSalvarConfig = configuracao != null
				&& configuracao.getDominios() != null
				&& !configuracao.getDominios().isEmpty();
	}

	private ConfiguracaoRhidEntity novaConfiguracao() {
		ConfiguracaoRhidEntity nova = new ConfiguracaoRhidEntity();
		nova.setDominios(new ArrayList<DominioRhidEntity>());
		nova.setSemDominioAcao(RhidSemDominioAcaoEnum.ENVIAR_TODOS);
		nova.setHoraExecucaoAutomatica(ConfiguracaoRhidEntity.HORA_EXECUCAO_AUTOMATICA_PADRAO);
		nova.setCodColigadaTotvs("1");
		nova.setCodSentencaTotvs("API.PTO.001");
		nova.setCodSistemaTotvs("A");
		nova.setUrlBase("https://rhid.com.br/v2");
		return nova;
	}

	private void executarImportacao(boolean completa) {
		if (configuracao == null || configuracao.getId() == null) {
			exibirErro("Salve a configuração antes de executar a importação manual.");
			return;
		}
		executarImportacaoConfig(configuracao.getId(), completa);
	}

	private void executarImportacaoConfig(Long configId, boolean completa) {
		if (processando) {
			return;
		}
		processando = true;
		try {
			ultimoResultado = rhidIntegracaoEJB.exportarRhidPorId(configId, completa);
			configuracao = rhidIntegracaoEJB.buscarConfiguracaoPorId(configId);
			if (configuracao.getDominios() != null) {
				configuracao.getDominios().size();
			}
			atualizarPodeSalvarConfig();
			sincronizarTimersAposImportacao();
			exibirInfo("Importação " + (completa ? "completa" : "incremental") + " concluída para "
					+ configuracao.getEmail() + ". Criados: " + ultimoResultado.getTotalCriados()
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

	private void sincronizarTimersAposImportacao() {
		if (configuracao == null || !Boolean.TRUE.equals(configuracao.getExportacaoAutomatica())) {
			return;
		}
		if (ultimoResultado == null || ultimoResultado.getTotalErros() > 0) {
			return;
		}
		if (configuracao.getUltimaExportacao() == null) {
			return;
		}
		try {
			lookupAgendadorService().sincronizarTimersExportacao();
		} catch (Exception e) {
			exibirErro("Importação concluída, porém não foi possível sincronizar timers RHID: "
					+ extrairMensagem(e));
		}
	}

	public String formatarUltimaExportacao(Date data) {
		if (data == null) {
			return "Nunca";
		}
		return new SimpleDateFormat("dd/MM/yyyy").format(data);
	}

	private void agendarRotinaSeNecessario() {
		try {
			lookupAgendadorService().sincronizarTimersExportacao();
		} catch (Exception e) {
			exibirErro("Configuração salva, porém não foi possível sincronizar a rotina automática: "
					+ extrairMensagem(e));
		}
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

	public String getUltimaExportacaoFormatada() {
		if (configuracao == null || configuracao.getUltimaExportacao() == null) {
			return "Nunca executada";
		}
		return formatarUltimaExportacao(configuracao.getUltimaExportacao());
	}

	public boolean isEdicao() {
		return configuracao != null && configuracao.getId() != null;
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

	public ConfiguracaoRhidEntity getConfiguracao() {
		return configuracao;
	}

	public void setConfiguracao(ConfiguracaoRhidEntity configuracao) {
		this.configuracao = configuracao;
	}

	public String getNovoDominio() {
		return novoDominio;
	}

	public void setNovoDominio(String novoDominio) {
		this.novoDominio = novoDominio;
	}

	public RhidOperacaoResultDTO getUltimoResultado() {
		return ultimoResultado;
	}

	public boolean isProcessando() {
		return processando;
	}

	public boolean isPodeSalvarConfig() {
		return podeSalvarConfig;
	}

	public boolean isPossuiDominios() {
		return configuracao != null
				&& configuracao.getDominios() != null
				&& !configuracao.getDominios().isEmpty();
	}

	public RhidSemDominioAcaoEnum[] getSemDominioAcaoOpcoes() {
		return RhidSemDominioAcaoEnum.values();
	}

	public static class DominioRhidDTO implements Serializable {
		private static final long serialVersionUID = 1L;
		private String nome;
		private String ids;

		public DominioRhidDTO() {
		}

		public DominioRhidDTO(String nome, String ids) {
			this.nome = nome;
			this.ids = ids;
		}

		public String getNome() {
			return nome;
		}

		public String getIds() {
			return ids;
		}
	}
}
