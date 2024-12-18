package br.com.startjob.acesso.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.startjob.acesso.modelo.utils.CriptografiaAES;
import br.com.startjob.acesso.to.teknisa.TeknisaToken;

@SuppressWarnings("unchecked")
@Path("/teknisa")
public class TeknisaRequestService extends BaseService {
	
	@Context
	private HttpServletRequest request;
	
	@Context 
	private HttpServletResponse response;
	
	CriptografiaAES cript = new CriptografiaAES();
	
	@GET
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@HeaderParam("nome") String nome, @HeaderParam("senha") String senha,
			@HeaderParam("filial") String filial) {
		
		encriptToken(nome, senha, filial);
		// converter em token
		// com o cliente achado, fazer o de para no banco com a filial
		return Response.status(Status.OK).entity("working").build();
	}
	
	@GET
	@Path("/access")
	@Produces(MediaType.APPLICATION_JSON)
	public Response access(@HeaderParam("token") String token) throws Exception {
		// converter token
		decriptToken(token);
		
		return Response.status(Status.OK).entity("working").build();
	}
	
	
	private String encriptToken(final String nome, final String senha, final String filial) {
		//trocar nome por login
		TeknisaToken tokenRequest = new TeknisaToken();
		tokenRequest.setClient(filial);
		tokenRequest.setSenha(senha);
		tokenRequest.setNome(nome);
		Gson gson = new GsonBuilder().create();
		String token = tokenRequest.toString();
		return  cript.encript(token);
	}
	
	private TeknisaToken decriptToken(final String token) throws Exception {
		//colocar classe generica depois
		Gson gson = new GsonBuilder().create();
		String tokenR = cript.decript(token);
		TeknisaToken tokenResponse = gson.fromJson(tokenR, TeknisaToken.class); 
		return tokenResponse;
	}

}
