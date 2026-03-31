package br.com.startjob.acesso.api;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/ws/local/{clienteId}")
public class WebSocketCadastroEndpoint {
	
	
	private static CompletableFuture<String> respostaAtual;
	
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
	
    
    @OnMessage
    public void onMessage(String message, Session session, 
			@PathParam("clienteId") String clienteId) {

		if (respostaAtual != null && !respostaAtual.isDone()) {
			respostaAtual.complete(message);
		}
		System.out.println("Retorno recebido do cliente " + clienteId + ": " + message);

	}
    
    
	public static String enviarEEsperar(String clienteId, String json) throws Exception {

	    respostaAtual = new CompletableFuture<>();

	    enviarParaLocal(clienteId, json);

	    return respostaAtual.get(10, TimeUnit.SECONDS); // timeout 10s
	}
}
