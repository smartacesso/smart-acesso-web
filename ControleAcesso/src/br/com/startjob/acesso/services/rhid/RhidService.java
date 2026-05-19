package br.com.startjob.acesso.services.rhid;

import java.io.Serializable;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.startjob.acesso.to.rhid.RhidLoginDTO;
import br.com.startjob.acesso.to.rhid.RhidPersonDTO;

public class RhidService implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String baseUrl;
    
    // Credenciais armazenadas para automação (Schedule)
    private String email;
    private String senha;
    private String dominio;
    
    // Cache do Token em memória
    private String tokenCache;

    public RhidService(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;
    }

    // Configura as credenciais que o Schedule ou a Tela vão usar
    public void inicializarCredenciais(String email, String senha, String dominio) {
        this.email = email;
        this.senha = senha;
        this.dominio = dominio;
        this.tokenCache = null; // Reseta o token antigo se mudar de credencial
    }

    /**
     * Força a autenticação e extrai o Token
     */
    public String loginAutenticar() {
        if (email == null || senha == null || dominio == null) {
            throw new IllegalStateException("Credenciais do RHID não foram inicializadas.");
        }

        RhidLoginDTO dto = new RhidLoginDTO(email, senha, dominio);
        
        // Faz a requisição POST padrão de login
        String jsonResposta = post("/login.svc/", dto);
        
        // Extrai e guarda o token no cache da instância
        this.tokenCache = extrairToken(jsonResposta);
        
        return jsonResposta;
    }

    /**
     * Método centralizador para requisições que exigem Token Bearer.
     * Implementa auto-login em caso de token expirado (401).
     */
    private <T> String executarPostComToken(String endpoint, T body) {
        // Se for a primeira execução ou limpou o cache, faz login
        if (this.tokenCache == null) {
            loginAutenticar();
        }

        Client client = criarClient();
        Response response = null;

        try {
            response = client.target(baseUrl + endpoint)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + this.tokenCache)
                    .post(Entity.entity(body, MediaType.APPLICATION_JSON));

            // Se o token expirou silenciosamente, tenta renovar UMA vez
            if (response.getStatus() == 401) {
                response.close(); // Fecha a conexão anterior
                
                loginAutenticar(); // Atualiza o tokenCache
                
                // Tenta novamente com o novo token
                response = client.target(baseUrl + endpoint)
                        .request(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + this.tokenCache)
                        .post(Entity.entity(body, MediaType.APPLICATION_JSON));
            }

            return tratarResposta(response);

        } catch (ProcessingException e) {
            throw new RuntimeException("Falha de conexão com RHID", e);
        } finally {
            if (response != null) response.close();
            client.close();
        }
    }

    /**
     * CADASTRAMENTO / ATUALIZAÇÃO DE FUNCIONÁRIO
     */
    public String salvarFuncionario(RhidPersonDTO person) {
        // Dispara o fluxo seguro com token gerenciado
        return executarPostComToken("/customerdb/person.svc/a/", person);
    }

    /**
     * Extrai o valor do token de dentro do JSON retornado pelo RHID
     */
    private String extrairToken(String jsonResposta) {
        try (JsonReader reader = Json.createReader(new StringReader(jsonResposta))) {
            JsonObject jsonObject = reader.readObject();
            if (jsonObject.containsKey("token")) {
                return jsonObject.getString("token");
            } else if (jsonObject.containsKey("accessToken")) {
                return jsonObject.getString("accessToken");
            }
            throw new RuntimeException("Chave do Token não mapeada no JSON do RHID.");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar JSON de login: " + e.getMessage(), e);
        }
    }

    private Client criarClient() {
        return ClientBuilder.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    // Método POST simples sem token (usado no login)
    private <T> String post(String endpoint, T body) {
        Client client = criarClient();
        Response response = null;
        try {
            response = client.target(baseUrl + endpoint)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(body, MediaType.APPLICATION_JSON));
            return tratarResposta(response);
        } finally {
            if (response != null) response.close();
            client.close();
        }
    }

    private String tratarResposta(Response response) {
        int status = response.getStatus();
        String body = response.readEntity(String.class);

        if (status == 200 || status == 201) {
            return body;
        }
        
        switch (status) {
            case 400: throw new RuntimeException("Dados inválidos enviados ao RHID.");
            case 401: throw new RuntimeException("Não autorizado no RHID.");
            case 403: throw new RuntimeException("Acesso negado ao RHID.");
            case 404: throw new RuntimeException("Endpoint não encontrado no RHID.");
            case 500: throw new RuntimeException("Erro interno no servidor do RHID.");
            default: throw new RuntimeException("Erro " + status + ": " + body);
        }
    }
}