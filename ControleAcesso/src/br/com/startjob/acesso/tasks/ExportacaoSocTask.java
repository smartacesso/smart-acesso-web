package br.com.startjob.acesso.tasks;

import java.util.List;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.ejb.EJB;
import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.services.BaseServlet;

public class ExportacaoSocTask extends TimerTask {

	@EJB
	private PedestreEJBRemote pedestreEJB;
	
	private final List<ClienteEntity> clientes;

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public ExportacaoSocTask(final List<ClienteEntity> clientes) {
		this.clientes = clientes;
	}
	
	@Override
	public void run() {
		logger.info("Iniciando processo de integração com o SOC...");

		try {
			pedestreEJB = ((PedestreEJBRemote) BaseServlet.getEjb(PedestreEJBRemote.class));

			if (clientes != null && !clientes.isEmpty()) {
				for(ClienteEntity cliente : clientes) {
					try {
						pedestreEJB.exportaSOC(cliente);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				logger.info("Processo de exportação do SOC finalizado...");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
