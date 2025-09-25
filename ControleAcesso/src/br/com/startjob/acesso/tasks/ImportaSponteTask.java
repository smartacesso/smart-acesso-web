package br.com.startjob.acesso.tasks;

import java.util.TimerTask;
import java.util.logging.Logger;

import javax.ejb.EJB;

import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.services.BaseServlet;

public class ImportaSponteTask extends TimerTask {

	@EJB
	private PedestreEJBRemote pedestreEJB;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public void run() {
		logger.info("Iniciando processo de importação com o Sponte...");

		try {
			pedestreEJB = ((PedestreEJBRemote) BaseServlet.getEjb(PedestreEJBRemote.class));
			try {
				pedestreEJB.importarSponte();

			} catch (Exception e) {
				e.printStackTrace();
			}

			logger.info("Processo de importação do Sponte finalizado...");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
