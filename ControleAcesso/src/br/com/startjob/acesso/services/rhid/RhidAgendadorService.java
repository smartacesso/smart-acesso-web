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

	public static final String TIMER_EXPORTACAO_PREFIX = "RHID_EXPORTACAO_";
	private static final String TIMER_AFD = "RHID_AFD_TIMER";

	private final Logger logger = Logger.getLogger(getClass().getName());

	@Resource
	private TimerService timerService;

	@PostConstruct
	public void init() {
		logger.info("Serviço de agendamento RHID inicializado.");
		sincronizarTimersExportacao();
	}

	/**
	 * Recria timers diários para cada configuração com exportação automática ativa.
	 */
	public void sincronizarTimersExportacao() {
		cancelarTimersExportacao();
		try {
			BaseEJBRemote baseEJB = (BaseEJBRemote) BaseServlet.getEjb(BaseEJBRemote.class);
			@SuppressWarnings("unchecked")
			List<ConfiguracaoRhidEntity> configs = (List<ConfiguracaoRhidEntity>) baseEJB.pesquisaSimples(
					ConfiguracaoRhidEntity.class, "findAll", new HashMap<>());
			if (configs == null || configs.isEmpty()) {
				return;
			}
			int agendados = 0;
			for (ConfiguracaoRhidEntity config : configs) {
				if (!Boolean.TRUE.equals(config.getExportacaoAutomatica()) || config.getId() == null) {
					continue;
				}
				if (config.getUltimaExportacao() == null) {
					logger.info("Exportação automática marcada para " + config.getEmail()
							+ ", porém aguardando primeira importação completa — timer não agendado.");
					continue;
				}
				int hora = normalizarHora(config.getHoraExecucaoAutomatica());
				agendarExportacaoDiariaConfig(config.getId(), hora);
				agendados++;
			}
			if (agendados > 0) {
				logger.info("Rotinas RHID de exportação agendadas para " + agendados + " configuração(ões).");
			}
		} catch (Exception e) {
			logger.warning("Não foi possível sincronizar timers RHID: " + e.getMessage());
		}
	}

	public void agendarExportacaoDiaria(int horaDoDia) {
		sincronizarTimersExportacao();
	}

	/** @deprecated usar {@link #sincronizarTimersExportacao()} */
	public void agendarExportacaoFuncionarios(long intervaloMinutos) {
		sincronizarTimersExportacao();
	}

	public void cancelarExportacaoAutomatica() {
		cancelarTimersExportacao();
		logger.info("Rotinas RHID de exportação automática canceladas.");
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
		Object info = timer.getInfo();
		if (info != null && info.toString().startsWith(TIMER_EXPORTACAO_PREFIX)) {
			Long configId = extrairConfigIdTimer(info.toString());
			if (configId != null) {
				new br.com.startjob.acesso.tasks.ExportarRhidTask(configId).run();
			}
			return;
		}
		if (TIMER_AFD.equals(info)) {
			executarExtracaoAfd(timer);
		}
	}

	private void agendarExportacaoDiariaConfig(Long configId, int horaDoDia) {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.set(java.util.Calendar.HOUR_OF_DAY, horaDoDia);
		cal.set(java.util.Calendar.MINUTE, 0);
		cal.set(java.util.Calendar.SECOND, 0);
		cal.set(java.util.Calendar.MILLISECOND, 0);
		if (cal.getTime().before(new java.util.Date())) {
			cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
		}

		long intervaloMs = 24L * 60L * 60L * 1000L;
		String timerInfo = TIMER_EXPORTACAO_PREFIX + configId;
		timerService.createIntervalTimer(cal.getTime(), intervaloMs, new TimerConfig(timerInfo, false));
		logger.info("Rotina RHID agendada diariamente às " + horaDoDia + ":00 (config id=" + configId + ").");
	}

	private Long extrairConfigIdTimer(String timerInfo) {
		try {
			return Long.parseLong(timerInfo.substring(TIMER_EXPORTACAO_PREFIX.length()));
		} catch (Exception e) {
			logger.warning("Timer RHID com identificador inválido: " + timerInfo);
			return null;
		}
	}

	private int normalizarHora(Integer horaDoDia) {
		if (horaDoDia == null || horaDoDia < 0 || horaDoDia > 23) {
			return ConfiguracaoRhidEntity.HORA_EXECUCAO_AUTOMATICA_PADRAO;
		}
		return horaDoDia;
	}

	private void cancelarTimersExportacao() {
		for (Timer timer : timerService.getTimers()) {
			Object info = timer.getInfo();
			if (info != null && info.toString().startsWith(TIMER_EXPORTACAO_PREFIX)) {
				timer.cancel();
			}
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

			String dataFim = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			String dataIni = LocalDate.now().withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

			for (ConfiguracaoRhidEntity config : configs) {
				if (!Boolean.TRUE.equals(config.getExportacaoAutomatica())) {
					continue;
				}
				String urlBase = config.getUrlBase() != null ? config.getUrlBase() : "https://rhid.com.br/v2";
				RhidService rhidService = new RhidService(urlBase);

				List<DominioRhidEntity> dominios = config.getDominios();
				if (dominios == null || dominios.isEmpty()) {
					continue;
				}

				for (DominioRhidEntity dominio : dominios) {
					try {
						rhidService.inicializarCredenciais(config.getEmail(), config.getSenha(), dominio.getNomeDominio());
						RhidLoginResponseDTO login = rhidService.loginAutenticar();
						if (login.requerSelecaoDominio()) {
							logger.warning("Domínio inválido para AFD: " + dominio.getNomeDominio());
							continue;
						}
						String ids = "[1]";
						String conteudo = rhidService.baixarAfd(dataIni, dataFim, ids);
						logger.info("AFD do domínio " + dominio.getNomeDominio() + " (config " + config.getEmail()
								+ ") obtido. Tamanho: " + conteudo.length());
					} catch (Exception e) {
						logger.warning("Falha AFD domínio " + dominio.getNomeDominio() + ": " + e.getMessage());
					}
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
