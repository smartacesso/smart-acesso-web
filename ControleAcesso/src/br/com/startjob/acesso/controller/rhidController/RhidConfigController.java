package br.com.startjob.acesso.controller.rhidController;



import java.io.Serializable;

import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.Date;

import java.util.List;



import javax.annotation.PostConstruct;

import javax.ejb.EJB;

import javax.faces.application.FacesMessage;

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



@Named

@ViewScoped

public class RhidConfigController extends BaseController {



	private static final long serialVersionUID = 1L;



	@EJB

	private RhidIntegracaoEJBRemote rhidIntegracaoEJB;



	private List<ConfiguracaoRhidEntity> configuracoesCadastradas = new ArrayList<>();

	private ConfiguracaoRhidEntity configuracao;

	private String novoDominio;

	private RhidOperacaoResultDTO ultimoResultado;

	private boolean processando;

	private boolean podeSalvarConfig;



	@PostConstruct

	public void init() {

		carregarListaConfiguracoes();

		if (!configuracoesCadastradas.isEmpty()) {

			selecionarConfiguracao(configuracoesCadastradas.get(0).getId());

		} else {

			configuracao = novaConfiguracao();

			atualizarPodeSalvarConfig();

		}

	}



	public void carregarListaConfiguracoes() {

		try {

			configuracoesCadastradas = rhidIntegracaoEJB.listarConfiguracoes();

			if (configuracoesCadastradas == null) {

				configuracoesCadastradas = new ArrayList<>();

			}

		} catch (Exception e) {

			configuracoesCadastradas = new ArrayList<>();

			exibirErro("Falha ao listar configurações RHID: " + extrairMensagem(e));

		}

	}



	public void selecionarConfiguracao(Long id) {

		try {

			configuracao = rhidIntegracaoEJB.buscarConfiguracaoPorId(id);

			if (configuracao.getDominios() == null) {

				configuracao.setDominios(new ArrayList<DominioRhidEntity>());

			} else {

				configuracao.getDominios().size();

			}

			atualizarPodeSalvarConfig();

			exibirInfo("Configuração carregada: " + configuracao.getEmail());

		} catch (Exception e) {

			exibirErro("Falha ao carregar configuração: " + extrairMensagem(e));

		}

	}



	public void novaConfiguracaoAction() {

		configuracao = novaConfiguracao();

		ultimoResultado = null;

		atualizarPodeSalvarConfig();

		exibirInfo("Formulário limpo para nova configuração RHID.");

	}



	private void atualizarPodeSalvarConfig() {

		podeSalvarConfig = configuracao != null

				&& configuracao.getDominios() != null

				&& !configuracao.getDominios().isEmpty();

	}



	private ConfiguracaoRhidEntity novaConfiguracao() {

		ConfiguracaoRhidEntity nova = new ConfiguracaoRhidEntity();

		nova.setDominios(new ArrayList<DominioRhidEntity>());

		nova.setSemDominioAcao(RhidSemDominioAcaoEnum.IGNORAR);

		return nova;

	}



	public void salvarConfiguracaoGlobal() {

		try {

			configuracao = rhidIntegracaoEJB.salvarConfiguracao(configuracao);

			atualizarPodeSalvarConfig();

			carregarListaConfiguracoes();

			agendarRotinaSeNecessario();

			exibirInfo("Configurações RHID salvas com sucesso.");

		} catch (IllegalArgumentException e) {

			exibirErro(e.getMessage());

		} catch (Exception e) {

			exibirErro("Falha ao salvar configurações: " + extrairMensagem(e));

		}

	}



	public void adicionarDominio() {

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

		exibirInfo("Domínio adicionado. Clique em Salvar Configurações para persistir.");

	}



	public void removerDominio(DominioRhidEntity dominio) {

		if (configuracao.getDominios() != null) {

			configuracao.getDominios().remove(dominio);

		}

		atualizarPodeSalvarConfig();

		if (!podeSalvarConfig) {

			exibirErro("Ao menos um domínio é obrigatório para login e importação no RHID.");

		} else {

			exibirInfo("Domínio removido. Clique em Salvar Configurações para persistir.");

		}

	}



	public void executarImportacaoCompleta() {

		executarImportacao(true);

	}



	public void executarImportacaoIncremental() {

		executarImportacao(false);

	}



	public void executarImportacaoCompletaConfig(Long configId) {

		executarImportacaoConfig(configId, true);

	}



	public void executarImportacaoIncrementalConfig(Long configId) {

		executarImportacaoConfig(configId, false);

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

			if (configuracao != null && configId.equals(configuracao.getId())) {

				ultimoResultado = null;

				if (!configuracoesCadastradas.isEmpty()) {

					configuracao = rhidIntegracaoEJB.buscarConfiguracaoPorId(configuracoesCadastradas.get(0).getId());

					if (configuracao.getDominios() != null) {

						configuracao.getDominios().size();

					}

				} else {

					configuracao = novaConfiguracao();

				}

				atualizarPodeSalvarConfig();

			}

			if (tinhaExportacaoAutomatica) {

				cancelarExportacaoAutomatica();

			}

			exibirInfo("Configuração RHID excluída com sucesso.");

		} catch (IllegalArgumentException e) {

			exibirErro(e.getMessage());

		} catch (Exception e) {

			exibirErro("Falha ao excluir configuração: " + extrairMensagem(e));

		}

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

			carregarListaConfiguracoes();

			atualizarPodeSalvarConfig();

			exibirInfo("Importação " + (completa ? "completa" : "incremental") + " concluída para "

					+ configuracao.getEmail() + ". Criados: " + ultimoResultado.getTotalCriados()

					+ ", atualizados: " + ultimoResultado.getTotalAtualizados() + ", erros: "

					+ ultimoResultado.getTotalErros() + ".");

		} catch (IllegalArgumentException e) {

			exibirErro(e.getMessage());

		} catch (Exception e) {

			exibirErro("Falha na importação RHID: " + extrairMensagem(e));

		} finally {

			processando = false;

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

		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(data);

	}



	public boolean configSelecionada(ConfiguracaoRhidEntity config) {

		return configuracao != null && config != null && configuracao.getId() != null

				&& configuracao.getId().equals(config.getId());

	}



	private void agendarRotinaSeNecessario() {

		if (!Boolean.TRUE.equals(configuracao.getExportacaoAutomatica())

				|| configuracao.getIntervaloMinutos() == null

				|| configuracao.getIntervaloMinutos() <= 0) {

			return;

		}

		try {

			RhidAgendadorService agendador = lookupAgendadorService();

			agendador.agendarExportacaoFuncionarios(configuracao.getIntervaloMinutos());

		} catch (Exception e) {

			exibirErro("Configuração salva, porém não foi possível agendar a rotina automática: "

					+ extrairMensagem(e));

		}

	}



	private void cancelarExportacaoAutomatica() {

		try {

			lookupAgendadorService().cancelarExportacaoAutomatica();

		} catch (Exception e) {

			exibirErro("Configuração excluída, porém não foi possível cancelar a rotina automática: "

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

			return "Nunca executada (próxima rotina automática será completa)";

		}

		return formatarUltimaExportacao(configuracao.getUltimaExportacao());

	}



	public List<ConfiguracaoRhidEntity> getConfiguracoesCadastradas() {

		return configuracoesCadastradas;

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



	public boolean isPossuiDominio() {

		return podeSalvarConfig;

	}



	public boolean isPodeSalvarConfig() {

		return podeSalvarConfig;

	}



	public RhidSemDominioAcaoEnum[] getSemDominioAcaoOpcoes() {

		return RhidSemDominioAcaoEnum.values();

	}



	private void exibirInfo(String mensagem) {

		addFacesMessage(FacesMessage.SEVERITY_INFO, mensagem);

	}



	private void exibirErro(String mensagem) {

		addFacesMessage(FacesMessage.SEVERITY_ERROR, mensagem);

	}



	private void addFacesMessage(FacesMessage.Severity severity, String mensagem) {

		javax.faces.context.FacesContext.getCurrentInstance().addMessage(null,

				new FacesMessage(severity, mensagem, null));

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


