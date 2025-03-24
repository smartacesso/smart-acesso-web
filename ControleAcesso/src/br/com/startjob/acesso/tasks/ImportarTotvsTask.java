package br.com.startjob.acesso.tasks;

import java.util.TimerTask;
import java.util.logging.Logger;

import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.services.BaseServlet;

public class ImportarTotvsTask extends TimerTask{
	 private Logger logger = Logger.getLogger(this.getClass().getName());
	    public ImportarTotvsTask() {

	    }

	    @Override
	    public void run() {
	        logger.info("Iniciando processo da integração Totvs...");

	        try {
	        	processaTotvs();
	            logger.info("Processo da integração Totvs finalizado...");
	            
	        } catch (Exception e) {
	            logger.severe("Erro ao executar a integração Totvs");
	            e.printStackTrace();
	        }
	    }

	    private void processaTotvs() {
			try {
				
				PedestreEJBRemote pedestreEJB = ((PedestreEJBRemote) BaseServlet.getEjb(PedestreEJBRemote.class));
				try {
					pedestreEJB.importarTotvs();

				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	        
	    }

}
