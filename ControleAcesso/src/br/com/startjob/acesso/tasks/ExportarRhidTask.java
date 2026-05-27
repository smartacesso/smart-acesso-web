package br.com.startjob.acesso.tasks;

import java.util.TimerTask;
import java.util.logging.Logger;

import br.com.startjob.acesso.modelo.ejb.RhidIntegracaoEJBRemote;
import br.com.startjob.acesso.services.BaseServlet;

import com.rhid.services.dto.RhidOperacaoResultDTO;

public class ExportarRhidTask extends TimerTask {

	private final Logger logger = Logger.getLogger(getClass().getName());

	@Override
	public void run() {
		logger.info("Iniciando exportação automática RHID...");
		try {
			RhidIntegracaoEJBRemote rhidEJB = (RhidIntegracaoEJBRemote) BaseServlet.getEjb(RhidIntegracaoEJBRemote.class);
			RhidOperacaoResultDTO resultado = rhidEJB.exportarRhidAutomatico();
			logger.info("Exportação RHID finalizada. Processados: " + resultado.getTotalProcessados()
					+ ", criados: " + resultado.getTotalCriados()
					+ ", atualizados: " + resultado.getTotalAtualizados()
					+ ", erros: " + resultado.getTotalErros());
		} catch (Exception e) {
			logger.severe("Erro na exportação automática RHID: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
