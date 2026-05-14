package br.com.startjob.acesso.services.rhid;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.startjob.acesso.to.rhid.RhidLoginDTO;

import java.io.Serializable;

public class RhidService implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String baseUrl;

    public RhidService(String baseUrl) {
        // Garante que não termine com barra para não duplicar nos métodos
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    public String login(String email, String password, String domain) {
        // Criamos o DTO com os dados recebidos
        RhidLoginDTO loginData = new RhidLoginDTO(email, password, domain);
        
        Client client = ClientBuilder.newClient();
        try {
            // No Wildfly, o Entity.json(loginData) já serializa o objeto automaticamente
            Response response = client.target(baseUrl + "/login.svc/")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(loginData, MediaType.APPLICATION_JSON));

            if (response.getStatus() == 200 || response.getStatus() == 201) {
                return response.readEntity(String.class);
            } else {
                String error = response.readEntity(String.class);
                throw new RuntimeException("Falha no login RHID. Status: " + response.getStatus() + " - " + error);
            }
        } finally {
            client.close();
        }
    }
    
    // Aqui você criaria os próximos métodos seguindo o mesmo padrão
    // public List<Funcionario> getFuncionarios(String token) { ... }
}