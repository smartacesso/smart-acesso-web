package br.com.startjob.acesso.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/ws/local/{clienteId}")
public class WebSocketCadastroEndpoint {
	
    public WebSocketCadastroEndpoint() {
        Logger.getLogger(WebSocketCadastroEndpoint.class.getName()).info("WebSocketEndpoint cadastros instanciado.");
    }
	
    private static Map<String, Session> sessoesPorUnidade = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("clienteId") String clienteId) {
        sessoesPorUnidade.put(clienteId, session);
        System.out.println("Conexão de cadastro aberta para Cliente: " + clienteId);
    }

    @OnClose
    public void onClose(Session session, @PathParam("clienteId") String clienteId) {
        sessoesPorUnidade.remove(clienteId);
        System.out.println("Conexão de cadastro fechada para Cliente: " + clienteId);
    }

    public static void enviarParaLocal(String clienteId, String json) {
        Session s = sessoesPorUnidade.get(clienteId);
        if (s != null && s.isOpen()) {
            s.getAsyncRemote().sendText(json);
        } else {
            System.out.println("Cliente " + clienteId + " não está conectada.");
        }
    }
	

}
