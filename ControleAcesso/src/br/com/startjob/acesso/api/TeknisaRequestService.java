package br.com.startjob.acesso.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.ejb.TeknisaEJBRemote;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.to.TeknisaTO;
import br.com.startjob.acesso.modelo.utils.CriptografiaAES;


@SuppressWarnings("unchecked")
@Path("/teknisa")
public class TeknisaRequestService extends BaseService {
	
	private static final String CREDENCIAL = "0d6b16fd-13e6-4eeb-a76e-8f72bb1d4da6";
	
	@Context
	private HttpServletRequest request;
	
	@Context 
	private HttpServletResponse response;
	
	CriptografiaAES cript = new CriptografiaAES();
	
	@GET
	@Path("/access")
	@Produces(MediaType.APPLICATION_JSON)
	public Response access(
			@HeaderParam("token") String token, 
			@QueryParam("id_organizacao") final String idOrganizacao,
			@QueryParam("id_filial") final String idFilial) throws Exception {
		
		if(!CREDENCIAL.equals(token)) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		TeknisaEJBRemote teknisaRemote = (TeknisaEJBRemote) getEjb("TeknisaEJB");
		BaseEJBRemote baseEJB = getEjb("BaseEJB");
		
		// converter token
		//TeknisaToken tokenResponse = decriptToken(token);
		
		if(Objects.isNull(idOrganizacao) || idOrganizacao.trim().isEmpty()
				|| Objects.isNull(idFilial) || idFilial.trim().isEmpty()) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("ORGANIZACAO_TEKNISA", idOrganizacao);
		args.put("FILIAL_TEKNISA", idFilial);

		List<ClienteEntity> clientes = (List<ClienteEntity>) baseEJB.pesquisaArgFixos(ClienteEntity.class,
				"findComFilialTeknisa", args);
		
		if(Objects.isNull(clientes) || clientes.isEmpty()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		Long idCliente = clientes.get(0).getId();
		List<TeknisaTO> acessos = teknisaRemote.findAccessByClientId(idCliente);
		return Response.status(Status.OK).entity(acessos).build();
	}
}	
	/*
	 
	@GET
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@HeaderParam("nome") String nome, @HeaderParam("senha") String senha,
			@HeaderParam("cliente") String cliente) {
		
		String token = encriptToken(nome, senha, cliente);
		// converter em token
		// com o cliente achado, fazer o de para no banco com a filial
		return Response.status(Status.OK).entity(token).build();
	}
	
	private String encriptToken(final String nome, final String senha, final String cliente) {
		//trocar nome por login
		TeknisaToken tokenRequest = new TeknisaToken();
		tokenRequest.setClient(cliente);
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
	*/

