package br.com.startjob.acesso.api;

import java.util.Optional;
import java.util.stream.Collectors;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


import br.com.startjob.acesso.modelo.ejb.ResponsibleEJBRemote;
import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.ResponsibleEntity;
import br.com.startjob.acesso.modelo.utils.EncryptionUtils;
import br.com.startjob.acesso.to.PedestrianAccessTO;
import br.com.startjob.acesso.to.AccessTO;
import br.com.startjob.acesso.to.responsible.ResponsibleLoginInput;
import br.com.startjob.acesso.to.responsible.ResponsibleLoginOutput;
import br.com.startjob.acesso.to.responsible.TokenOutput;
import br.com.startjob.acesso.modelo.utils.*;

@Path("/responsible")
public class RensponsibleApiService extends BaseService {
	@Context
	private HttpServletRequest request;
	
	@Context 
	private HttpServletResponse response;
	
	CriptografiaAES cript = new CriptografiaAES();
	
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Response login(final ResponsibleLoginInput responsibleInput) throws Exception {
		ResponsibleEJBRemote responsibleEJB = (ResponsibleEJBRemote) getEjb("ResponsibleEJB");
		
		Optional<ResponsibleEntity> responsible = responsibleEJB.findResponsibleByLoginAndPassword(
				responsibleInput.getLogin(), EncryptionUtils.encrypt(responsibleInput.getPassword()));
		
		if(!responsible.isPresent()) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		final ResponsibleLoginOutput response = new ResponsibleLoginOutput();
		response.setToken(encriptToken(responsible.get()));
		response.setNome(responsible.get().getNome());
		
		return Response.status(Status.OK).entity(response).build();
	}

	private String encriptToken(final ResponsibleEntity responsibleEntity) {
		TokenOutput tokenRequest = new TokenOutput();
		tokenRequest.setLogin(responsibleEntity.getLogin());
		tokenRequest.setSenha(responsibleEntity.getPassword());
		tokenRequest.setIdResponsible(responsibleEntity.getId());
		Gson gson = new GsonBuilder().create();
		String token = tokenRequest.toString();
		return  cript.encript(token);
	}
	
	private TokenOutput decriptToken(final String token) throws Exception {
		Gson gson = new GsonBuilder().create();
		String tokenR = cript.decript(token);
		TokenOutput tokenResponse = gson.fromJson(tokenR, TokenOutput.class); 
		return tokenResponse;
	}
	
	@GET
	@Path("/dependents")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllDependents(@HeaderParam("token") String token, @HeaderParam("size") int size,
			@HeaderParam("page") int page) throws Exception {
		ResponsibleEJBRemote responsibleEJB = (ResponsibleEJBRemote) getEjb("ResponsibleEJB");
		
		TokenOutput tokenResponse =(TokenOutput) decriptToken(token);
		if(Objects.isNull(tokenResponse)) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		List<PedestreEntity> dependencies = responsibleEJB.findAllDependentsPageable(tokenResponse.getIdResponsible(), page, size);
		
		List<PedestrianAccessTO> pedestres = convertPedestrianTO(dependencies);
		
		GenericEntity <List<PedestrianAccessTO>> list =new GenericEntity<List<PedestrianAccessTO>>(pedestres) {};
		return Response.ok(list).build();
	}
	
	private List<PedestrianAccessTO> convertPedestrianTO(List<PedestreEntity> dependencies) throws ParseException {
		List<PedestrianAccessTO> pedestresTO = new ArrayList<>();
		
		return dependencies.stream()
			.map(pedestre -> PedestrianAccessTO.convertPedestrianAccess(pedestre))
			.collect(Collectors.toList());
	}

	@GET
	@Path("/access")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllDependentsAccess(@HeaderParam("token") String token, @HeaderParam("size") int size,
			@HeaderParam("page") int page) throws Exception {
		ResponsibleEJBRemote responsibleEJB = (ResponsibleEJBRemote) getEjb("ResponsibleEJB");
		
		List<AcessoEntity> accessList = responsibleEJB.findAllAccessPageable(
				"Teste", "8D969EEF6ECAD3C29A3A629280E686CF0C3F5D5A86AFF3CA12020C923ADC6C92", size, page);
		
		List<AccessTO> accessTOList = new ArrayList<>();
		AccessTO accessTO = new AccessTO();
		
		accessList.forEach(aacess -> {
			try {
				accessTOList.add(accessTO.convertToAccessTO(aacess));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		return Response.ok(accessTOList).build();
	} 


}
