package br.com.startjob.acesso.api;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@SuppressWarnings("unchecked")
@Path("/teknisa")
public class TeknisaRequestService extends BaseService {
	
	@GET
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@HeaderParam("nome") String nome, @HeaderParam("senha") String senha,
			@HeaderParam("filial") String filial) {
		
		// converter em token
		// com o cliente achado, fazer o de para no banco com a filial
		return Response.status(Status.OK).entity("working").build();
	}
	
	@GET
	@Path("/access")
	@Produces(MediaType.APPLICATION_JSON)
	public Response access(@HeaderParam("token") String token) {
		// converter token
		
		
		return Response.status(Status.OK).entity("working").build();
	}

}
