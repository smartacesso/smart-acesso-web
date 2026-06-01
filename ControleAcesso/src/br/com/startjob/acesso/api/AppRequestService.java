package br.com.startjob.acesso.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.CorrespondenciaEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.enumeration.PerfilAcessoApp;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.utils.CriptografiaAES;
import br.com.startjob.acesso.modelo.utils.EncryptionUtils;
import br.com.startjob.acesso.to.app.AcessosRequest;
import br.com.startjob.acesso.to.app.CadastroRequest;
import br.com.startjob.acesso.to.app.EncomendaRequest;
import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.service.CadastroFacialLinkService;
import br.com.startjob.acesso.to.app.LinkConviteVisitanteRequest;
import br.com.startjob.acesso.to.app.LinkConviteVisitanteResponse;
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
			if (!usuario.getSenha().equals(EncryptionUtils.encrypt(request.getSenha()))) {
				return Response.status(Status.UNAUTHORIZED).entity("Senha inválida").build();
			}
			System.out.println("senha valida");

			if (usuario.getPerfilApp() == null) {
				return Response.status(Status.BAD_REQUEST).entity("Sem perfil de usuario").build();
			}
			
			String token = JwtUtil.gerarToken(usuario.getId(), request.getCliente(), usuario.getPerfilApp().name());

			// montar resposta
			UsuarioDTO userDto = new UsuarioDTO();
			userDto.setId(usuario.getId());
			userDto.setNome(usuario.getLogin());
			userDto.setCliente(request.getCliente());
			userDto.setPerfil(usuario.getPerfilApp().name());

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
	public Response acessos(
	        @Context HttpHeaders headers,
	        AcessosRequest request) {

	    String authHeader =
	            headers.getHeaderString(
	                    HttpHeaders.AUTHORIZATION);

	    if (authHeader == null
	            || !authHeader.startsWith("Bearer ")) {

	        return Response
	                .status(Status.UNAUTHORIZED)
	                .entity("Token não fornecido")
	                .build();
	    }

	    String token = authHeader.substring(7);

	    try {

	        Claims claims =
	                JwtUtil.validarToken(token);

	        Long userId =
	                Long.parseLong(
	                        claims.getSubject());

	        String clienteNome =
	                (String) claims.get("cliente");

	        String perfil =
	                (String) claims.get("perfil");

	        ClienteEntity cliente =
	                appEjb.buscaClientesPorUnidadeOrganizacional(
	                        clienteNome);

	        if (request == null) {

	            return Response
	                    .status(Status.BAD_REQUEST)
	                    .entity("Corpo da requisição vazio")
	                    .build();
	        }

	        SimpleDateFormat sdf =
	                new SimpleDateFormat(
	                        "yyyy-MM-dd");

	        Date dataInicio = null;
	        Date dataFim = null;

	        if (request.getDataInicio() != null
	                && !request.getDataInicio().isEmpty()) {

	            dataInicio =
	                    sdf.parse(
	                            request.getDataInicio());
	        }

	        if (request.getDataFim() != null
	                && !request.getDataFim().isEmpty()) {

	            dataFim =
	                    sdf.parse(
	                            request.getDataFim());

	            Calendar cal =
	                    Calendar.getInstance();

	            cal.setTime(dataFim);

	            cal.set(
	                    Calendar.HOUR_OF_DAY,
	                    23);

	            cal.set(
	                    Calendar.MINUTE,
	                    59);

	            cal.set(
	                    Calendar.SECOND,
	                    59);

	            cal.set(
	                    Calendar.MILLISECOND,
	                    999);

	            dataFim = cal.getTime();

	        } else {

	            dataFim = new Date();
	        }

	        Set<Long> idsPermitidos = new HashSet<>();

	        PerfilAcessoApp perfilApp = PerfilAcessoApp.valueOf(perfil);

	        switch (perfilApp) {
	            case RESPONSAVEL:
	                idsPermitidos.add(userId);
	                idsPermitidos.addAll(appEjb.buscarIdsTutorados(userId));
	                break;
	            case GERENCIAL:
	                idsPermitidos.add(userId);
	              idsPermitidos.addAll(appEjb.buscarIdsFuncionarios(userId));
	                break;
	            case COMUM:
	            default:
	                idsPermitidos.add(userId);
	        }

	        // Converte o Set de volta para List só para passar para a consulta
	        List<Long> listaIdsParaBusca = new ArrayList<>(idsPermitidos);

	        List<AcessoEntity> lista = appEjb.buscarAcessosPaginados(
	                listaIdsParaBusca, // Passa a lista limpa aqui!
	                cliente.getId(),
	                dataInicio,
	                dataFim,
	                request.getPagina(),
	                request.getTamanho());

	        return Response
	                .ok(lista)
	                .build();

	    } catch (Exception e) {

	        e.printStackTrace();

	        return Response
	                .status(Status.UNAUTHORIZED)
	                .entity(
	                        "Token inválido ou erro na consulta")
	                .build();
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
	        String clienteNome = (String) claims.get("cliente");
	        
	        // Dica: Cacheie esse ID do cliente se possível para não ir ao banco toda hora
	        ClienteEntity cliente = appEjb.buscaClientesPorUnidadeOrganizacional(clienteNome);

	        if (request == null) {
	            return Response.status(Status.BAD_REQUEST).entity("Corpo da requisição vazio").build();
	        }
	        
	        // Mudança para CorrespondenciaEntity
	        List<CorrespondenciaEntity> lista = appEjb.buscarEncomendasPaginada(
	                userId,
	                cliente.getId(), 
	                request.getPagina(),
	                request.getTamanho()
	            );

	        return Response.ok(lista).build();
	        
	    } catch (Exception e) {
	        e.printStackTrace(); // Importante para logar erros de cast ou query
	        return Response.status(Status.UNAUTHORIZED).entity("Token inválido ou erro na consulta").build();
	    }
	}
	
	@POST
	@Path("/cadastrar")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response cadastrar(@Context HttpHeaders headers, CadastroRequest novoCadastro) {

		String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return Response.status(Status.UNAUTHORIZED).entity("Token não fornecido").build();
		}

		String token = authHeader.substring(7);

		try {
			Claims claims = JwtUtil.validarToken(token);
			String cliente = (String) claims.get("cliente");

			// Dica: Cacheie esse ID do cliente se possível para não ir ao banco toda hora
			ClienteEntity clienteEntity = appEjb.buscaClientesPorUnidadeOrganizacional(cliente);

			if (request == null) {
				return Response.status(Status.BAD_REQUEST).entity("Corpo da requisição vazio").build();
			}

			PedestreEntity novo = criarNovoCadastro(clienteEntity, novoCadastro);

			// 3. Salvar no Banco via EJB
			PedestreEntity salva = (PedestreEntity) getEjb("BaseEJB").gravaObjeto(novo)[0];
			

			// 4. Retornar 201 Created com o objeto/ID
			return Response.status(Status.CREATED).entity(salva).build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao salvar cadastro: " + e.getMessage())
					.build();
		}
	}

	private PedestreEntity criarNovoCadastro(ClienteEntity cliente, CadastroRequest novoDto) {
		PedestreEntity novo = new PedestreEntity();

		String cpfTratado = tratarCpf(novoDto.getCpf());

		novo.setTipo(TipoPedestre.VISITANTE);
		novo.setCliente(cliente);
		novo.setNome(novoDto.getNome());
		novo.setCpf(cpfTratado);
		novo.setCelular(novoDto.getCelular());
		novo.setObservacoes(novo.getObservacoes());
		novo.setStatus(br.com.startjob.acesso.modelo.enumeration.Status.ATIVO);
		novo.setCodigoCartaoAcesso(cpfTratado);

		return novo;
	}

	private String tratarCpf(String cpf) {
		if (cpf == null) {
			return null;
		}

		return cpf.replaceAll("\\D", "");
	}

	/**
	 * Lista empresas do cliente para o gerencial escolher ao gerar link de convite.
	 */
	@GET
	@Path("/empresas")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listarEmpresas(@Context HttpHeaders headers) {

		String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return Response.status(Status.UNAUTHORIZED).entity("Token não fornecido").build();
		}

		try {
			Claims claims = JwtUtil.validarToken(authHeader.substring(7));
			String perfil = (String) claims.get("perfil");
			if (!PerfilAcessoApp.GERENCIAL.name().equals(perfil)) {
				return Response.status(Status.FORBIDDEN).entity("Perfil não autorizado").build();
			}

			String clienteNome = (String) claims.get("cliente");
			ClienteEntity clienteEntity = appEjb.buscaClientesPorUnidadeOrganizacional(clienteNome);
			PedestreEJBRemote pedestreEJB = (PedestreEJBRemote) getEjb("PedestreEJB");

			java.util.Map<String, Object> args = new java.util.HashMap<>();
			args.put("ID_CLIENTE", clienteEntity.getId());
			@SuppressWarnings("unchecked")
			java.util.List<EmpresaEntity> empresas = (java.util.List<EmpresaEntity>) pedestreEJB
					.pesquisaArgFixos(EmpresaEntity.class, "findAllByIdCliente2", args);

			return Response.ok(empresas != null ? empresas : new ArrayList<>()).build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao listar empresas").build();
		}
	}

	/**
	 * Gera link de convite para cadastro facial de visitante (sem pré-cadastro).
	 * Requer perfil app {@link PerfilAcessoApp#GERENCIAL}.
	 */
	@POST
	@Path("/visitante/link-convite")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response gerarLinkConviteVisitante(@Context HttpHeaders headers, LinkConviteVisitanteRequest body) {

		String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return Response.status(Status.UNAUTHORIZED).entity("Token não fornecido").build();
		}

		try {
			Claims claims = JwtUtil.validarToken(authHeader.substring(7));
			Long userId = Long.parseLong(claims.getSubject());
			String perfil = (String) claims.get("perfil");
			String clienteNome = (String) claims.get("cliente");

			if (!PerfilAcessoApp.GERENCIAL.name().equals(perfil)) {
				return Response.status(Status.FORBIDDEN).entity("Perfil não autorizado a gerar link de visitante")
						.build();
			}

			if (body == null || body.getIdEmpresa() == null) {
				return Response.status(Status.BAD_REQUEST).entity("idEmpresa é obrigatório").build();
			}

			ClienteEntity clienteEntity = appEjb.buscaClientesPorUnidadeOrganizacional(clienteNome);
			PedestreEJBRemote pedestreEJB = (PedestreEJBRemote) getEjb("PedestreEJB");
			CadastroFacialLinkService linkService = new CadastroFacialLinkService(pedestreEJB);

			EmpresaEntity empresa = linkService.buscaEmpresaPorId(body.getIdEmpresa(), clienteEntity.getId());
			if (empresa == null) {
				return Response.status(Status.BAD_REQUEST).entity("Empresa inválida para este cliente").build();
			}

			PedestreEntity gerador = buscaPedestrePorId(userId, clienteEntity.getId(), pedestreEJB);
			if (gerador == null) {
				return Response.status(Status.UNAUTHORIZED).entity("Pedestre gerador não encontrado").build();
			}

			long token = linkService.calcularTokenValidade(clienteEntity.getId());
			linkService.gravarCadastroExternoConvite(clienteEntity, empresa, token, gerador);

			String link = linkService.montarUrlConvite(clienteEntity.getId(), empresa.getId(), token);
			return Response.ok(new LinkConviteVisitanteResponse(link, token, empresa.getId())).build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao gerar link: " + e.getMessage())
					.build();
		}
	}

	@SuppressWarnings("unchecked")
	private PedestreEntity buscaPedestrePorId(Long id, Long idCliente, PedestreEJBRemote pedestreEJB)
			throws Exception {
		java.util.Map<String, Object> args = new java.util.HashMap<>();
		args.put("ID", id);
		args.put("ID_CLIENTE", idCliente);
		java.util.List<PedestreEntity> lista = (java.util.List<PedestreEntity>) pedestreEJB
				.pesquisaArgFixos(PedestreEntity.class, "findByIdPedestreAndIdCliente", args);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}
}
