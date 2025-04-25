package br.com.startjob.acesso.tasks;

import java.util.TimerTask;
import java.util.logging.Logger;

import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.services.BaseServlet;

public class AutoAtendimentoResetTask extends TimerTask {

    private final Logger log = Logger.getLogger(getClass().getName());
    
    public AutoAtendimentoResetTask() {
    	
    }

    @Override
    public void run() {
        log.info("Executando rotina para desmarcar autoAtendimento...");

        try {
        	PedestreEJBRemote pedestreEJB = ((PedestreEJBRemote) BaseServlet.getEjb(PedestreEJBRemote.class));
        	pedestreEJB.resetAutoAtendimento();
        } catch (Exception e) {
            log.severe("Erro ao executar rotina de reset do autoAtendimento: " + e.getMessage());
        }
    }
}
