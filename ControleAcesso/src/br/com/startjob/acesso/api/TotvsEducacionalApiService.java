package br.com.startjob.acesso.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.startjob.acesso.services.CredenciaisTotvsService;
import br.com.startjob.acesso.to.TotvsEdu.AlunoTotvsTO;
import br.com.startjob.acesso.to.TotvsEdu.MensagemResponseTO;

@Path("/educacional")
public class TotvsEducacionalApiService extends BaseService {
	
	CredenciaisTotvsService credenciaisTotvsService;
	private final String TOKEN_ACESSO = "KsOywRMRmGrqy903";
	
	
	@POST
	@Path("/alunos")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response receberDadosAluno(
		List<AlunoTotvsTO> alunos,
	    @HeaderParam("Authorization") String authHeader) {

	    String token = extrairToken(authHeader);

	    if (!TOKEN_ACESSO.equals(token)) {
	        return Response.status(Response.Status.UNAUTHORIZED)
	                .entity(new MensagemResponseTO("Credenciais inválidas"))
	                .build();
	    }
	    
	    for (AlunoTotvsTO aluno : alunos) {
	        System.out.println("Recebido: " + aluno.getNome() + ", CPF: " + aluno.getCpf());
	    }

	    return Response.status(Response.Status.CREATED)
	    		.entity(new MensagemResponseTO("Alunos processados com sucesso."))
	    		.build();
	}

	private String extrairToken(String authHeader) {
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        return authHeader.substring(7).trim();
	    }
	    return null;
	}

}
