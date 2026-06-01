package br.com.startjob.acesso.service;

import java.util.logging.Logger;

import br.com.startjob.acesso.modelo.entity.CadastroExternoEntity;

/**
 * Ponto de extensão para envio de link de aprovação (ex.: API WhatsApp).
 */
public class NotificacaoAprovacaoTotemService {

	private static final Logger LOG = Logger.getLogger(NotificacaoAprovacaoTotemService.class.getName());

	/**
	 * Envia link público de aprovação ao contato da empresa visitada.
	 * Implementação futura: integração WhatsApp.
	 */
	public void enviarLinkAprovacao(CadastroExternoEntity solicitacao, String urlPublica, String telefoneDestino) {
		LOG.info(String.format(
				"[stub] Notificação aprovação totem: cadastro=%s url=%s telefone=%s",
				solicitacao != null ? solicitacao.getId() : null,
				urlPublica,
				telefoneDestino));
	}

}
