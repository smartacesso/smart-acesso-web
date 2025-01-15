package br.com.startjob.acesso.tasks;

import java.util.List;
import java.util.TimerTask;
import java.util.logging.Logger;

import br.com.startjob.acesso.modelo.ejb.PedestreEJB;
import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.services.BaseServlet;

public class ImportaSeniorTask extends TimerTask {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    public ImportaSeniorTask() {

    }

    @Override
    public void run() {
        logger.info("Iniciando processo da integração Senior...");

        try {
        	processarSenior();
            logger.info("Processo da integração senior finalizado...");
            
        } catch (Exception e) {
            logger.severe("Erro ao executar a integração senior");
            e.printStackTrace();
        }
    }

    private void processarSenior() {
		try {
			
			PedestreEJBRemote pedestreEJB = ((PedestreEJBRemote) BaseServlet.getEjb(PedestreEJBRemote.class));
			try {
				pedestreEJB.importarSenior();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    }
}

