package br.com.startjob.acesso.api;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.startjob.acesso.modelo.ejb.TopMixEJBRemote;
import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.to.AccessTO;

@Path("/topmix")
public class TopMixRequestServiceApi extends BaseService {

	private static final String CREDENCIAL = "0d6b16fd-13e6-4eeb-a76e-8f72bb1d4da6";
	

	@GET
	@Path("/access")
	@Produces(MediaType.APPLICATION_JSON)
	public Response access(@HeaderParam("token") String token, 
	                       @QueryParam("cpf") final String cpf,
	                       @QueryParam("data_inicial") final String dataInicial, 
	                       @QueryParam("data_final") final String dataFinal,
	                       @QueryParam("page") @DefaultValue("0") int page, 
	                       @QueryParam("size") @DefaultValue("100") int size)
	        throws Exception {

	    // Validação do token
	    if (!validarToken(token)) {
	        return Response.status(Response.Status.UNAUTHORIZED).entity("Token inválido").build();
	    }

	    // Validação do CPF
	    if (!validarCpf(cpf)) {
	        return Response.status(Response.Status.BAD_REQUEST).entity("CPF inválido").build();
	    }

	    // Validação e conversão de datas
	    Date[] datasConvertidas = validarEConverterDatas(dataInicial, dataFinal);
	    if (datasConvertidas == null) {
	        return Response.status(Response.Status.BAD_REQUEST)
	                .entity("Formato de data inválido ou data inicial posterior à data final. Use o formato dd/MM/yyyy.")
	                .build();
	    }

	    // Datas válidas
	    Date dateInicial = datasConvertidas[0];
	    Date dateFinal = datasConvertidas[1];

	    // Buscar acessos no EJB
	    TopMixEJBRemote topMixEjb = (TopMixEJBRemote) getEjb("TopMixEJB");
	    List<AcessoEntity> acessos = topMixEjb.findAccessByCPF(cpf, dateInicial, dateFinal, page, size);
	    
	    // Verificação se a lista está vazia
	    if (Objects.isNull(acessos) || acessos.isEmpty()) {
	        return Response.status(Response.Status.NO_CONTENT).build();
	    }
	    
	    // Conversão dos resultados
	    List<AccessTO> acessosTo = new ArrayList<>();
	    for (AcessoEntity acesso : acessos) {
	        AccessTO acessoTO = new AccessTO();
	        acessoTO.convertToAccessTO(acesso);
	        acessosTo.add(acessoTO);
	    }

	    // Retornar os resultados
	    return Response.ok(acessosTo).build();
	}

	/**
	 * Valida e converte strings de datas no formato "dd/MM/yyyy" para objetos Date.
	 *
	 * @param dataInicial String da data inicial.
	 * @param dataFinal String da data final.
	 * @return Um array de objetos Date (dataInicial e dataFinal), ou null se inválido.
	 */
	private Date[] validarEConverterDatas(String dataInicial, String dataFinal) {
	    try {
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	        sdf.setLenient(false); // Validação estrita para evitar datas inválidas

	        // Conversão
	        Date dateInicial = sdf.parse(dataInicial);
	        Date dateFinal = sdf.parse(dataFinal);

	        // Verificar se a data inicial é anterior ou igual à data final
	        if (dateInicial.after(dateFinal)) {
	            System.out.println("Data inicial não pode ser posterior à data final.");
	            return null;
	        }

	        return new Date[]{dateInicial, dateFinal};
	    } catch (Exception e) {
	        System.out.println("Erro ao validar ou converter as datas: " + e.getMessage());
	        return null;
	    }
	}

	/**
	 * Valida o token recebido.
	 */
	private boolean validarToken(String token) {
	    return CREDENCIAL.equals(token);
	}

	/**
	 * Valida o CPF recebido.
	 */
	private boolean validarCpf(String cpf) {
	    return cpf != null && cpf.matches("\\d{11}"); // Exemplo: CPF com 11 dígitos
	}


}
