package br.com.startjob.acesso.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.startjob.acesso.repositories.HikivisionEquipamentosRepository;
import br.com.startjob.acesso.to.HikivisionDeviceSimplificadoTO;

@ServerEndpoint("/ws/liberacao/{clienteId}")
public class WebSocketLiberacaoEndpoint {

	
    public WebSocketLiberacaoEndpoint() {
        Logger.getLogger(WebSocketCadastroEndpoint.class.getName()).info("WebSocketEndpoint liberacao instanciado.");
    }
	
    private static Map<String, Session> sessoesPorUnidade = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("clienteId") String clienteId) {
        sessoesPorUnidade.put(clienteId, session);
        System.out.println("Conexão de liberacao aberta para Cliente: " + clienteId);
    }

    @OnClose
    public void onClose(Session session, @PathParam("clienteId") String clienteId) {
        sessoesPorUnidade.remove(clienteId);
        System.out.println("Conexão de liberacao fechada para Cliente: " + clienteId);
    }
    
    @OnMessage
	public void onMessage(String message, Session session, @PathParam("clienteId") String clienteId) {
		try {
			List<HikivisionDeviceSimplificadoTO> equipamentos = new ObjectMapper().readValue(message,
					new TypeReference<List<HikivisionDeviceSimplificadoTO>>() {
					});

			HikivisionEquipamentosRepository.adicionarEquipamentos(clienteId, equipamentos);

			System.out.println("Equipamentos atuais do cliente " + clienteId + ": "
					+ HikivisionEquipamentosRepository.getEquipamentos(clienteId));

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
    
    
    public static void enviarLiberacao(String clienteId, String deviceID) {
        Session s = sessoesPorUnidade.get(clienteId);
        if (s != null && s.isOpen()) {
            s.getAsyncRemote().sendText(deviceID);
        } else {
            System.out.println("Cliente " + clienteId + " não está conectada.");
        }
    }
}
