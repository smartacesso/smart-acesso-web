package br.com.startjob.acesso.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/ws/local/{unidadeId}")
public class WebSocketEndpoint {
	
    public WebSocketEndpoint() {
        Logger.getLogger(WebSocketEndpoint.class.getName()).info("WebSocketEndpoint instanciado.");
    }
	
    private static Map<String, Session> sessoesPorUnidade = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("unidadeId") String unidadeId) {
        sessoesPorUnidade.put(unidadeId, session);
        System.out.println("Conexão aberta para unidade: " + unidadeId);
    }

    @OnClose
    public void onClose(Session session, @PathParam("unidadeId") String unidadeId) {
        sessoesPorUnidade.remove(unidadeId);
        System.out.println("Conexão fechada para unidade: " + unidadeId);
    }

    public static void enviarParaLocal(String unidadeId, String json) {
        Session s = sessoesPorUnidade.get(unidadeId);
        if (s != null && s.isOpen()) {
            s.getAsyncRemote().sendText(json);
        } else {
            System.out.println("Unidade " + unidadeId + " não está conectada.");
        }
    }
	

}
