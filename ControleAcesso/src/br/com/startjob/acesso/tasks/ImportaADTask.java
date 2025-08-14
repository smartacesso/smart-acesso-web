package br.com.startjob.acesso.tasks;

import java.util.TimerTask;
import java.util.logging.Logger;

import javax.ejb.EJB;

import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.services.BaseServlet;

public class ImportaADTask extends TimerTask {

	@EJB
	private PedestreEJBRemote pedestreEJB;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public void run() {
		logger.info("Iniciando processo de importação com o AD...");

		try {
			pedestreEJB = ((PedestreEJBRemote) BaseServlet.getEjb(PedestreEJBRemote.class));
			try {
				pedestreEJB.importarAD();

			} catch (Exception e) {
				e.printStackTrace();
			}

			logger.info("Processo de importação do AD finalizado...");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
