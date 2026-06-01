package br.com.startjob.acesso.services.rhid;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.rhid.services.RhidService;
import com.rhid.services.dto.RhidLoginResponseDTO;

import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.entity.ConfiguracaoRhidEntity;
import br.com.startjob.acesso.modelo.entity.DominioRhidEntity;
import br.com.startjob.acesso.services.BaseServlet;

@Singleton
@Startup
public class RhidAgendadorService {

	private static final String TIMER_EXPORTACAO = "RHID_EXPORTACAO_TIMER";
	private static final String TIMER_AFD = "RHID_AFD_TIMER";

	private final Logger logger = Logger.getLogger(getClass().getName());

	@Resource
	private TimerService timerService;

	@PostConstruct
	public void init() {
		logger.info("Serviço de agendamento RHID inicializado.");
		inicializarTimersDoBanco();
	}

	public void agendarExportacaoFuncionarios(long intervaloMinutos) {
		cancelarTimer(TIMER_EXPORTACAO);
		long intervaloMs = intervaloMinutos * 60 * 1000L;
		timerService.createIntervalTimer(intervaloMs, intervaloMs,
				new TimerConfig(TIMER_EXPORTACAO, false));
		logger.info("Rotina RHID de exportação agendada a cada " + intervaloMinutos
				+ " minutos (primeira execução após o intervalo).");
	}

	public void cancelarExportacaoAutomatica() {
		cancelarTimer(TIMER_EXPORTACAO);
		logger.info("Rotina RHID de exportação automática cancelada.");
	}

	public void agendarRotina(long intervaloMinutos) {
		agendarExtracaoAfd(intervaloMinutos);
	}

	public void agendarExtracaoAfd(long intervaloMinutos) {
		cancelarTimer(TIMER_AFD);
		long intervaloMs = intervaloMinutos * 60 * 1000L;
		timerService.createIntervalTimer(intervaloMs, intervaloMs, new TimerConfig(TIMER_AFD, false));
		logger.info("Rotina RHID de AFD agendada a cada " + intervaloMinutos + " minutos.");
	}

	@Timeout
	public void executarTimer(Timer timer) {
		if (TIMER_EXPORTACAO.equals(timer.getInfo())) {
			new br.com.startjob.acesso.tasks.ExportarRhidTask().run();
			return;
		}
		if (TIMER_AFD.equals(timer.getInfo())) {
			executarExtracaoAfd(timer);
		}
	}

	private void inicializarTimersDoBanco() {
		try {
			BaseEJBRemote baseEJB = (BaseEJBRemote) BaseServlet.getEjb(BaseEJBRemote.class);
			@SuppressWarnings("unchecked")
			List<ConfiguracaoRhidEntity> configs = (List<ConfiguracaoRhidEntity>) baseEJB.pesquisaSimples(
					ConfiguracaoRhidEntity.class, "findAll", new HashMap<>());
			if (configs == null || configs.isEmpty()) {
				return;
			}
			ConfiguracaoRhidEntity config = configs.get(0);
			if (Boolean.TRUE.equals(config.getExportacaoAutomatica())
					&& config.getIntervaloMinutos() != null
					&& config.getIntervaloMinutos() > 0) {
				agendarExportacaoFuncionarios(config.getIntervaloMinutos());
			}
		} catch (Exception e) {
			logger.warning("Não foi possível restaurar timers RHID: " + e.getMessage());
		}
	}

	private void executarExtracaoAfd(Timer timer) {
		logger.info("Iniciando extração AFD RHID...");
		try {
			BaseEJBRemote baseEJB = (BaseEJBRemote) BaseServlet.getEjb(BaseEJBRemote.class);
			@SuppressWarnings("unchecked")
			List<ConfiguracaoRhidEntity> configs = (List<ConfiguracaoRhidEntity>) baseEJB.pesquisaSimples(
					ConfiguracaoRhidEntity.class, "findAll", new HashMap<>());
			if (configs == null || configs.isEmpty()) {
				return;
			}

			ConfiguracaoRhidEntity config = configs.get(0);
			String urlBase = config.getUrlBase() != null ? config.getUrlBase() : "https://rhid.com.br/v2";
			RhidService rhidService = new RhidService(urlBase);

			String dataFim = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			String dataIni = LocalDate.now().withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

			List<DominioRhidEntity> dominios = config.getDominios();
			if (dominios == null || dominios.isEmpty()) {
				return;
			}

			for (DominioRhidEntity dominio : dominios) {
				try {
					rhidService.inicializarCredenciais(config.getEmail(), config.getSenha(), dominio.getNomeDominio());
					RhidLoginResponseDTO login = rhidService.loginAutenticar();
					if (login.requerSelecaoDominio()) {
						logger.warning("Domínio inválido para AFD: " + dominio.getNomeDominio());
						continue;
					}
					// ids de empresa para AFD: padrão [1] até haver config dedicada
					String ids = "[1]";
					String conteudo = rhidService.baixarAfd(dataIni, dataFim, ids);
					logger.info("AFD do domínio " + dominio.getNomeDominio() + " obtido. Tamanho: " + conteudo.length());
				} catch (Exception e) {
					logger.warning("Falha AFD domínio " + dominio.getNomeDominio() + ": " + e.getMessage());
				}
			}
		} catch (Exception e) {
			logger.severe("Erro na extração AFD RHID: " + e.getMessage());
		}
	}

	private void cancelarTimer(String info) {
		for (Timer timer : timerService.getTimers()) {
			if (info.equals(timer.getInfo())) {
				timer.cancel();
			}
		}
	}
}
