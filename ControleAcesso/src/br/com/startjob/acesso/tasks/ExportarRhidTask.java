package br.com.startjob.acesso.tasks;

import java.util.TimerTask;
import java.util.logging.Logger;

import br.com.startjob.acesso.modelo.ejb.RhidIntegracaoEJBRemote;
import br.com.startjob.acesso.services.BaseServlet;

import com.rhid.services.dto.RhidOperacaoResultDTO;

public class ExportarRhidTask extends TimerTask {

	private final Logger logger = Logger.getLogger(getClass().getName());
	private final Long configId;

	public ExportarRhidTask(Long configId) {
		this.configId = configId;
	}

	@Override
	public void run() {
		logger.info("Iniciando exportação automática RHID (config id=" + configId + ")...");
		try {
			RhidIntegracaoEJBRemote rhidEJB = (RhidIntegracaoEJBRemote) BaseServlet.getEjb(RhidIntegracaoEJBRemote.class);
			RhidOperacaoResultDTO resultado = rhidEJB.exportarRhidAutomaticoPorId(configId);
			logger.info("Exportação RHID finalizada (config id=" + configId + "). Processados: "
					+ resultado.getTotalProcessados()
					+ ", criados: " + resultado.getTotalCriados()
					+ ", atualizados: " + resultado.getTotalAtualizados()
					+ ", erros: " + resultado.getTotalErros());
		} catch (Exception e) {
			logger.severe("Erro na exportação automática RHID (config id=" + configId + "): " + e.getMessage());
			e.printStackTrace();
		}
	}
}
