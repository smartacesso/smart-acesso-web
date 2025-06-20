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
import br.com.startjob.acesso.tasks.ImportacaoSocTask;
import br.com.startjob.acesso.tasks.ImportarTotvsTask;
import br.com.startjob.acesso.tasks.ExportacaoSocTask;
import br.com.startjob.acesso.tasks.ImportaSeniorTask;

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

		registraTimersParaSOC();
		registraTimersParaSenior();
		registraTimersParaTovs();
		registraTimersAutoAtendimento();
	}

	@SuppressWarnings("unchecked")
	private void registraTimersParaSOC() {
		log.info("Registra Integração com SOC");

		// limpa antes de iniciar
		ActivatedTasks.getInstancia().limpaTimersSOC();

		List<ClienteEntity> clientes = null;
		try {
			BaseEJBRemote ejb = getEjb(BaseEJBRemote.class);
			clientes = (List<ClienteEntity>) ejb.pesquisaSimples(ClienteEntity.class, "findAllComIntegracaoSOC", new HashMap<>());

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
	
	@SuppressWarnings("unchecked")
	private void registraTimersParaSenior() {
	    log.info("Registra Integração Senior");

//	     Limpa os timers anteriores relacionados à nova função
	    ActivatedTasks.getInstancia().limpaTimersSenior();

	    // Define o período para 30 minutos (30 * 60 * 1000 ms)
	    Long period =  1 * 60 * 1000L;
	    Timer timer = new Timer();
	    
	    // Define a nova tarefa
	    TimerTask SeniorTask = new ImportaSeniorTask();
	    
	    // Agenda a tarefa para rodar a cada 30 minutos
	    timer.scheduleAtFixedRate(SeniorTask, 0, period);
	    
	    log.info("Registrando rotina da Senior");

	    ActivatedTasks.getInstancia().timers.put("importacaoSENIOR_cliente", timer);
	}
	
	
	@SuppressWarnings("unchecked")
	private void registraTimersParaTovs() {
	    log.info("Registra Integração Totvs");

//	     Limpa os timers anteriores relacionados à nova função
	    ActivatedTasks.getInstancia().limpaTimersTovs();

	    // Define o período para 30 minutos (30 * 60 * 1000 ms)
	    Long period =  15 * 60 * 1000L;
	    Timer timer = new Timer();
	    
	    // Define a nova tarefa
	    TimerTask TotvsTask = new ImportarTotvsTask();
	    
	    // Agenda a tarefa para rodar a cada 30 minutos
	    timer.scheduleAtFixedRate(TotvsTask, 0, period);
	    
	    log.info("Registrando rotina da Totvs");

	    ActivatedTasks.getInstancia().timers.put("importacaoTOTVS_cliente", timer);
	}

	
	private void registraTimersAutoAtendimento() {
		log.info("Registrando rotina de reset do autoAtendimento...");

		ActivatedTasks.getInstancia().limpaTimersAutoAtendimento();

		Long period = 30 * 60 * 1000L; // a cada 15 minutos
		Timer timer = new Timer();
		TimerTask task = new AutoAtendimentoResetTask();

		timer.scheduleAtFixedRate(task, 0, period);

		ActivatedTasks.getInstancia().timers.put("AUTO_ATENDIMENTO", timer);
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

}
