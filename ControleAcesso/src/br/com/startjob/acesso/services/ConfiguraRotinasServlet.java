package br.com.startjob.acesso.services;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.utils.AppAmbienteUtils;
import br.com.startjob.acesso.tasks.ActivatedTasks;
import br.com.startjob.acesso.tasks.AutoAtendimentoResetTask;
import br.com.startjob.acesso.tasks.ExportacaoSocTask;
import br.com.startjob.acesso.tasks.ImportaADTask;
import br.com.startjob.acesso.tasks.ImportaSeniorTask;
import br.com.startjob.acesso.tasks.ImportaSponteTask;
import br.com.startjob.acesso.tasks.ImportacaoSocTask;
import br.com.startjob.acesso.tasks.ImportarSalesianoTask;
import br.com.startjob.acesso.tasks.ImportarTotvsTask;
import br.com.startjob.acesso.tasks.ImportarUsuarioTask;

@SuppressWarnings("serial")
@WebServlet(loadOnStartup = 1, asyncSupported = true, urlPatterns = { "/configuraRotinas" })
public class ConfiguraRotinasServlet extends BaseServlet {

	private Logger log = Logger.getLogger(this.getClass().getName());

	@Override
	public void init() throws ServletException {
		log.info("Registra Timers do sistema...");

		if (!AppAmbienteUtils.isProdution() || AppAmbienteUtils.isServices()) {
			log.info("Não registra rotinas no ambiente de desenvolvimento.");
			return;
		}

		log.info("Registra rotinas recorrentes...");
//		registraTimersParaSenior();
//		registraTimersParaSOC();
//		registraTimersParaTotvs();
//		registraTimerSalesiano();
//		registraPrimeiroUsuario();
//		registraTimersAutoAtendimento();
//		timerRegisterAD();
//		timerRegiserSponte();

	}

	@SuppressWarnings("unchecked")
	private void registraTimersParaSOC() {
		log.info("Registra Integração com SOC");

		// limpa antes de iniciar
		ActivatedTasks.getInstancia().limpaTimersSOC();

		List<ClienteEntity> clientes = null;
		try {
			BaseEJBRemote ejb = getEjb(BaseEJBRemote.class);
			clientes = (List<ClienteEntity>) ejb.pesquisaSimples(ClienteEntity.class, "findAllComIntegracaoSOC",
					new HashMap<>());

		} catch (Exception e) {
			log.warning("Erro ao procurar ambientes configurados, rotinas não serão executadas");
		}

		if (clientes == null || clientes.isEmpty()) {
			log.info("Nenhum cliente com integração SOC configurada");
			return;
		}

		Calendar inicio = getInicio("2");
		Long periodExpirar = 24 * 60 * 60 * 1000L;
		Timer timer = new Timer();

		TimerTask uTimerTask = new ImportacaoSocTask();
		timer.scheduleAtFixedRate(uTimerTask, inicio.getTime(), periodExpirar.longValue());

		log.info("Registrando rotina de importação SOC");

		ActivatedTasks.getInstancia().timers.put("importacaoSOC_cliente", timer);

		inicio = getInicio("3");
		timer = new Timer();

		uTimerTask = new ExportacaoSocTask(clientes);
		timer.scheduleAtFixedRate(uTimerTask, inicio.getTime(), periodExpirar.longValue());

		log.info("Registrando rotina de exportação SOC");

		ActivatedTasks.getInstancia().timers.put("exportacaoSOC_cliente", timer);
	}

	private void registraTimersParaSenior() {
		log.info("Registra Integração Senior");

//	     Limpa os timers anteriores relacionados à nova função
		ActivatedTasks.getInstancia().limpaTimersSenior();

		// Define o período para 30 minutos (30 * 60 * 1000 ms)
		Long period = 5  * 60 * 1000L;
		Timer timer = new Timer();

		// Define a nova tarefa
		TimerTask SeniorTask = new ImportaSeniorTask();

		// Agenda a tarefa para rodar a cada 30 minutos
		timer.scheduleAtFixedRate(SeniorTask, 0, period);

		log.info("Registrando rotina da Senior");

		ActivatedTasks.getInstancia().timers.put("importacaoSENIOR_cliente", timer);
	}

	private void registraTimersParaTotvs() {
		log.info("Registra Integração TOTVS PROTHEUS");

//	     Limpa os timers anteriores relacionados à nova função
		ActivatedTasks.getInstancia().limpaTimersTovs();

		// Define o período para 30 minutos (30 * 60 * 1000 ms)
		Long period = 30 * 60 * 1000L;
		Timer timer = new Timer();

		TimerTask totvsTask = new ImportarTotvsTask();
		timer.scheduleAtFixedRate(totvsTask, 0, period);

		log.info("Registrando rotina da TOVS PROTHEUS");

		ActivatedTasks.getInstancia().timers.put("importacaoTOTVS_cliente", timer);
	}

	private void registraTimerSalesiano() {
		log.info("Registra Integração Salesiano");

		ActivatedTasks.getInstancia().limpaTimersTovs();

		// Define o período para 2 minutos (30 * 60 * 1000 ms)
		Long period = 3 * 60 * 1000L;
		Timer timer = new Timer();

		try {
			TimerTask salesianoTask = new ImportarSalesianoTask();
			timer.scheduleAtFixedRate(salesianoTask, 0, period);

			log.info("Registrando rotina da Salesiano");

			ActivatedTasks.getInstancia().timers.put("importacaoSalesiano_cliente", timer);

		} catch (Exception e) {
			log.info("Erro ao instanciar base ejb para salesiano task");
		}

	}

	private void registraTimersAutoAtendimento() {
		log.info("Registrando rotina de reset do autoAtendimento...");

		ActivatedTasks.getInstancia().limpaTimersAutoAtendimento();

		//(pegar o numero e adicionar na hora)
		Long period = 1 * 60 * 1000L; // a cada 30 minutos
		Timer timer = new Timer();
		TimerTask task = new AutoAtendimentoResetTask();

		timer.scheduleAtFixedRate(task, 0, period);

		ActivatedTasks.getInstancia().timers.put("AUTO_ATENDIMENTO", timer);
	}

	private void registraPrimeiroUsuario() {
		log.info("Registra Integração primeiro usuario");

		ActivatedTasks.getInstancia().limpaTimersTovs();

		// Define o período para 2 minutos (30 * 60 * 1000 ms)
		Long period = 24 * 60 * 60 * 60 * 1000L;
		Timer timer = new Timer();

		try {
			TimerTask usuarioTask = new ImportarUsuarioTask();
			timer.scheduleAtFixedRate(usuarioTask, 0, period);

			log.info("Registrando rotina da Usuario");

			ActivatedTasks.getInstancia().timers.put("importacaoUsuario_cliente", timer);

		} catch (Exception e) {
			log.info("Erro ao instanciar base ejb para salesiano task");
		}

	}

	private Calendar getInicio(final String hourOfDay) {
		Calendar inicio = Calendar.getInstance();
		inicio.set(Calendar.DAY_OF_MONTH, inicio.get(Calendar.DAY_OF_MONTH));
		inicio.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourOfDay));
		inicio.set(Calendar.MINUTE, 0);
		inicio.set(Calendar.SECOND, 0);

		return inicio;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest ,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// m�todo vazio, este servlet somente registra timer no tomcat
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest ,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	@SuppressWarnings("unchecked")
	private void timerRegisterAD() {
		log.info("Registra Integração AD");

//	     Limpa os timers anteriores relacionados à nova função
		ActivatedTasks.getInstancia().limpaTimersAD();

		// Define o período para 30 minutos (30 * 60 * 1000 ms)
		Long period = 5 * 60 * 1000L;
		Timer timer = new Timer();

		// Define a nova tarefa
		TimerTask aDTask = new ImportaADTask();

		// Agenda a tarefa para rodar a cada 30 minutos
		timer.scheduleAtFixedRate(aDTask, 0, period);

		log.info("Registrando rotina da AD");

		ActivatedTasks.getInstancia().timers.put("importacaoAD_cliente", timer);
	}

	@SuppressWarnings("unchecked")
	private void timerRegiserSponte() {
		log.info("Registra Integração Sponte");

		ActivatedTasks.getInstancia().limpaTimersAD();

		Long period = 30 * 60 * 1000L;
		Timer timer = new Timer();

		TimerTask aDTask = new ImportaSponteTask();

		timer.scheduleAtFixedRate(aDTask, 0, period);

		log.info("Registrando rotina da AD");

		ActivatedTasks.getInstancia().timers.put("importacaoAD_cliente", timer);
	}

}
