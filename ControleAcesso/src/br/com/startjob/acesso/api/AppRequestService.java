package br.com.startjob.acesso.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.startjob.acesso.modelo.ejb.AppEJBRemote;
import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.CorrespondenciaEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.utils.CriptografiaAES;
import br.com.startjob.acesso.to.app.AcessosRequest;
import br.com.startjob.acesso.to.app.EncomendaRequest;
import br.com.startjob.acesso.to.app.LoginRequest;
import br.com.startjob.acesso.to.app.LoginResponse;
import br.com.startjob.acesso.to.app.UsuarioDTO;
import br.com.startjob.acesso.utils.JwtUtil;
import io.jsonwebtoken.Claims;


@Path("/app")
public class AppRequestService extends BaseService {

	@Context
	private HttpServletRequest request;

	@Context
	private HttpServletResponse response;

	CriptografiaAES cript = new CriptografiaAES();
	
	@EJB
	private AppEJBRemote appEjb;
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(LoginRequest request) {

		System.out.println("Validando login");

		try {

			if (request == null || request.getLogin() == null || request.getSenha() == null
					|| request.getCliente() == null) {

				return Response.status(Status.BAD_REQUEST).entity("Parâmetros inválidos").build();
			}

			System.out.println("parametros login : " + request.getLogin() + " , cliente : " + request.getCliente());

			PedestreEntity usuario = appEjb.buscarPorLoginECliente(request.getLogin(), request.getCliente());

			if (usuario == null) {
				return Response.status(Status.UNAUTHORIZED).entity("Usuário ou cliente inválido").build();
			}

			System.out.println("usuario encontrado");

			// ⚠️ ideal: usar BCrypt
			if (!usuario.getSenha().equals(request.getSenha())) {
				return Response.status(Status.UNAUTHORIZED).entity("Senha inválida").build();
			}
			System.out.println("senha valida");

			String token = JwtUtil.gerarToken(usuario.getId(), request.getCliente());

			// montar resposta
			UsuarioDTO userDto = new UsuarioDTO();
			userDto.setId(usuario.getId());
			userDto.setNome(usuario.getLogin());
			userDto.setCliente(request.getCliente());

			LoginResponse response = new LoginResponse();
			response.setToken(token);
			response.setTipo("Bearer");
			response.setUsuario(userDto);

			return Response.ok(response).build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao realizar login").build();
		}
	}
	
	@POST
	@Path("/acessos")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response acessos(@Context HttpHeaders headers, AcessosRequest request) {

	    // 1. Extrair o Header de Autorização
	    String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);

	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        return Response.status(Status.UNAUTHORIZED).entity("Token não fornecido").build();
	    }

	    String token = authHeader.substring(7); // Remove o "Bearer "

	    try {
	        // 2. Validar o Token usando sua JwtUtil
	        Claims claims = JwtUtil.validarToken(token);
	        
	        // Recuperar dados que você guardou no token
	        Long userId = Long.parseLong(claims.getSubject());
	        String cliente = (String) claims.get("cliente");
	        
	        Long idCliente = appEjb.buscaClientesPorUnidadeOrganizacional(cliente);

	        // 3. Validar o corpo da requisição
	        if (request == null) {
	            return Response.status(Status.BAD_REQUEST).entity("Corpo da requisição vazio").build();
	        }

	        // 4. Lógica de Negócio (exemplo: buscar acessos no banco)
	        // 4. Chamar o EJB com filtros de data e paginação
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        
	        Date dataInicio = null;
	        Date dataFim = null;

	     // 1. Trata Data Início
	        if (request.getDataInicio() != null && !request.getDataInicio().isEmpty()) {
	            dataInicio = sdf.parse(request.getDataInicio());
	        }

	        // 2. Trata Data Fim
	        if (request.getDataFim() != null && !request.getDataFim().isEmpty()) {
	            dataFim = sdf.parse(request.getDataFim());
	            
	            // Ajusta para o final do dia
	            Calendar cal = Calendar.getInstance();
	            cal.setTime(dataFim);
	            cal.set(Calendar.HOUR_OF_DAY, 23);
	            cal.set(Calendar.MINUTE, 59);
	            cal.set(Calendar.SECOND, 59);
	            cal.set(Calendar.MILLISECOND, 999);
	            dataFim = cal.getTime();
	        } else {
	             dataFim = new Date(); 
	        }

	        List<AcessoEntity> lista = appEjb.buscarAcessosPaginados(
	        	userId,
	        	idCliente, 
	            dataInicio, 
	            dataFim,
	            request.getPagina(),
	            request.getTamanho()
	        );

	        return Response.ok(lista).build();
	        
	    } catch (Exception e) {
	        // Se o token estiver expirado ou a assinatura for inválida, cairá aqui
	        return Response.status(Status.UNAUTHORIZED).entity("Token inválido ou expirado").build();
	    }
	}
	
	@POST
	@Path("/encomendas")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response encomendas(@Context HttpHeaders headers, EncomendaRequest request) {

	    String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        return Response.status(Status.UNAUTHORIZED).entity("Token não fornecido").build();
	    }

	    String token = authHeader.substring(7);

	    try {
	        Claims claims = JwtUtil.validarToken(token);
	        Long userId = Long.parseLong(claims.getSubject());
	        String cliente = (String) claims.get("cliente");
	        
	        // Dica: Cacheie esse ID do cliente se possível para não ir ao banco toda hora
	        Long idCliente = appEjb.buscaClientesPorUnidadeOrganizacional(cliente);

	        if (request == null) {
	            return Response.status(Status.BAD_REQUEST).entity("Corpo da requisição vazio").build();
	        }
	        
	        // Mudança para CorrespondenciaEntity
	        List<CorrespondenciaEntity> lista = appEjb.buscarEncomendasPaginada(
	                userId,
	                idCliente, 
	                request.getPagina(),
	                request.getTamanho()
	            );

	        return Response.ok(lista).build();
	        
	    } catch (Exception e) {
	        e.printStackTrace(); // Importante para logar erros de cast ou query
	        return Response.status(Status.UNAUTHORIZED).entity("Token inválido ou erro na consulta").build();
	    }
	}
}
