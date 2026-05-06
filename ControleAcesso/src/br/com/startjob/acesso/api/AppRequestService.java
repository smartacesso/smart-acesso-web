package br.com.startjob.acesso.api;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.startjob.acesso.modelo.ejb.AppEJBRemote;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.utils.CriptografiaAES;
import br.com.startjob.acesso.to.app.LoginRequest;
import br.com.startjob.acesso.to.app.LoginResponse;
import br.com.startjob.acesso.to.app.UsuarioDTO;
import br.com.startjob.acesso.utils.JwtUtil;


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

	        if (request == null ||
	            request.getLogin() == null ||
	            request.getSenha() == null ||
	            request.getCliente() == null) {

	            return Response.status(Status.BAD_REQUEST)
	                    .entity("Parâmetros inválidos")
	                    .build();
	        }
	        
	        System.out.println("parametros login : " +  request.getLogin()+ " , cliente : " +  request.getCliente());

	        PedestreEntity usuario = appEjb.buscarPorLoginECliente(
	                request.getLogin(),
	                request.getCliente()
	        );

	        if (usuario == null) {
	            return Response.status(Status.UNAUTHORIZED)
	                    .entity("Usuário ou cliente inválido")
	                    .build();
	        }
	        
	        System.out.println("usuario encontrado");

	        // ⚠️ ideal: usar BCrypt
	        if (!usuario.getSenha().equals(request.getSenha())) {
	            return Response.status(Status.UNAUTHORIZED)
	                    .entity("Senha inválida")
	                    .build();
	        }
	        System.out.println("senha valida");

	        String token = JwtUtil.gerarToken(
	                usuario.getId(),
	                request.getCliente()
	        );

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
	        return Response.status(Status.INTERNAL_SERVER_ERROR)
	                .entity("Erro ao realizar login")
	                .build();
	    }
	}

}
