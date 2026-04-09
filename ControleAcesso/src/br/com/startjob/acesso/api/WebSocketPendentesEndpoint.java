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


@ServerEndpoint("/ws/pendente/{clienteId}")
public class WebSocketPendentesEndpoint {
	
    public WebSocketPendentesEndpoint() {
        Logger.getLogger(WebSocketCadastroEndpoint.class.getName()).info("WebSocketEndpoint pendentes instanciado.");
    }
	
    private static Map<String, Session> sessoesPorUnidade = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("clienteId") String clienteId) {
        sessoesPorUnidade.put(clienteId, session);
        System.out.println("Conexão de autoatendimento aberta para Cliente: " + clienteId);
    }

    @OnClose
    public void onClose(Session session, @PathParam("clienteId") String clienteId) {
        sessoesPorUnidade.remove(clienteId);
        System.out.println("Conexão de autoatendimento fechada para Cliente: " + clienteId);
    }
    
    @OnMessage
	public void onMessage(String message, Session session, @PathParam("clienteId") String clienteId) {
//		try {
//			CadastroTo cadastro = new ObjectMapper().readValue(message,
//					new TypeReference<CadastroTo>() {
//					});
//
//			HikivisionEquipamentosRepository.adicionarCadastrosPendentesDeLiberacao(clienteId, cadastro);
//
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
	}
    
}
