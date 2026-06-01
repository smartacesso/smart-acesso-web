package br.com.startjob.acesso.service;

import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import br.com.startjob.acesso.api.WebSocketCadastroEndpoint;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.to.WebSocketPedestrianAccessTO;

/**
 * Envio do visitante aos equipamentos faciais via WebSocket.
 */
public final class FacialWebSocketHelper {

	private static final Logger LOG = Logger.getLogger(FacialWebSocketHelper.class.getName());

	private FacialWebSocketHelper() {
	}

	/**
	 * @return true se envio desnecessário (pedestre/cliente nulos) ou resposta {@code ok}
	 */
	public static boolean enviarPedestre(PedestreEntity pedestre, Gson gson) {
		if (pedestre == null || pedestre.getCliente() == null || pedestre.getCliente().getId() == null) {
			return true;
		}
		try {
			String jsonStr = gson.toJson(WebSocketPedestrianAccessTO.fromPedestre(pedestre));
			String resposta = WebSocketCadastroEndpoint.enviarEEsperar(pedestre.getCliente().getId().toString(),
					jsonStr);
			return "ok".equals(resposta);
		} catch (TimeoutException e) {
			LOG.log(Level.WARNING, "Timeout WebSocket facial", e);
			return false;
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Erro WebSocket facial", e);
			return false;
		}
	}

}
