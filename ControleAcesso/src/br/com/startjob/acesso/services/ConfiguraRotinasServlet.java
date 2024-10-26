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
import br.com.startjob.acesso.tasks.ImportacaoSocTask;
import br.com.startjob.acesso.tasks.ExportacaoSocTask;

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
	
	private Calendar getInicio(final String hourOfDay) {
		Calendar inicio = Calendar.getInstance();
		inicio.set(Calendar.DAY_OF_MONTH, inicio.get(Calendar.DAY_OF_MONTH) + 1);
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
