package br.com.startjob.acesso.api;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.startjob.acesso.modelo.ejb.PedestreEJB;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.to.TotvsEdu.AlunoTotvsTO;
import br.com.startjob.acesso.to.TotvsEdu.MensagemResponseTO;

@Path("/educacional")
public class TotvsEducacionalApiService extends BaseService {
	
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
	                .entity(new MensagemResponseTO(401, "Credenciais inválidas"))
	                .build();
	    }
	    
//	    processaAlunosTotvs(alunos);
	    
	    return Response.status(Response.Status.CREATED)
	    		.entity(new MensagemResponseTO(201,"Alunos processados com sucesso."))
	    		.build();
	}

	private void processaAlunosTotvs(List<AlunoTotvsTO> alunos) {
		Map<String, List<AlunoTotvsTO>> alunosPorUnidade = alunos.stream()
				.filter(a -> a.getUnidade() != null && !a.getUnidade().trim().isEmpty())
				.collect(Collectors.groupingBy(a -> a.getUnidade().trim().toLowerCase()));

		// Extrai os nomes únicos das unidades
		Set<String> nomesUnidades = alunosPorUnidade.keySet();

		// Busca todas as unidades de uma vez
		List<ClienteEntity> unidades = buscaClientesPorUnicades(nomesUnidades);
		Map<String, ClienteEntity> unidadeMap = null;
		// Mapeia nome -> entidade
		if(Objects.nonNull(unidades)) {
			unidadeMap = unidades.stream()
					.collect(Collectors.toMap(c -> c.getNome().trim().toLowerCase(), Function.identity()));
		}

		try {
			salvaOuAtualizaAlunosPorUnidade(alunosPorUnidade, unidadeMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void salvaOuAtualizaAlunosPorUnidade(Map<String, List<AlunoTotvsTO>> alunosPorUnidade,  Map<String, ClienteEntity> unidadeMap) throws Exception {
		
		PedestreEJB pedestreEJB = (PedestreEJB) getEjb("PedestreEJB");
		
	    for (Map.Entry<String, List<AlunoTotvsTO>> entry : alunosPorUnidade.entrySet()) {
	        String nomeUnidade = entry.getKey();
	        ClienteEntity unidade = unidadeMap.get(nomeUnidade);

	        if (unidade == null) {
	            System.out.println("⚠️ Unidade não encontrada: " + nomeUnidade);
	            continue;
	        }

	        for (AlunoTotvsTO aluno : entry.getValue()) {
	            PedestreEntity pedestre = new PedestreEntity();
	            pedestre.setNome(aluno.getNome());
	            pedestre.setCpf(aluno.getCpf());
	            pedestre.setMatricula(aluno.getMatricula());
//	            pedestre.setCliente(unidade); // Associa à unidade real

	            pedestreEJB.gravaObjeto(pedestre);
	        }
	    }
	}
	
	private List<ClienteEntity> buscaClientesPorUnicades(Set<String> nomesUnidades) {
		return null;
	}
	
	private String extrairToken(String authHeader) {
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        return authHeader.substring(7).trim();
	    }
	    return null;
	}

}
