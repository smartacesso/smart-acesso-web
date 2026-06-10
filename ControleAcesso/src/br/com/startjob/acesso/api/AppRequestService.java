package br.com.startjob.acesso.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.startjob.acesso.modelo.ejb.AppEJBRemote;
import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.AvisoAppEntity;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.to.app.EncomendaListItem;
import br.com.startjob.acesso.modelo.enumeration.PerfilAcessoApp;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.to.app.AvisoListItem;
import br.com.startjob.acesso.modelo.to.app.PageResult;
import br.com.startjob.acesso.modelo.utils.CriptografiaAES;
import br.com.startjob.acesso.modelo.utils.EncryptionUtils;
import br.com.startjob.acesso.to.app.AcessoItemDTO;
import br.com.startjob.acesso.to.app.AcessosRequest;
import br.com.startjob.acesso.to.app.AvisoItemDTO;
import br.com.startjob.acesso.to.app.AvisoSalvarRequest;
import br.com.startjob.acesso.to.app.AvisosRequest;
import br.com.startjob.acesso.to.app.CadastroRequest;
import br.com.startjob.acesso.to.app.CadastroVisitanteResponseDTO;
import br.com.startjob.acesso.to.app.ConfirmarRetiradaRequest;
import br.com.startjob.acesso.to.app.DeviceTokenRequest;
import br.com.startjob.acesso.to.app.EmpresaResumoDTO;
import br.com.startjob.acesso.to.app.EncomendaItemDTO;
import br.com.startjob.acesso.to.app.EncomendaRequest;
import br.com.startjob.acesso.to.app.HealthResponse;
import br.com.startjob.acesso.to.app.PushStatusResponse;
import br.com.startjob.acesso.modelo.entity.DeviceTokenEntity;
import br.com.startjob.acesso.utils.ImagemBase64Util;
import br.com.startjob.acesso.utils.MailSendUtils;
import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.service.AppPushNotificationService;
import br.com.startjob.acesso.service.CadastroFacialLinkService;
import br.com.startjob.acesso.service.FirebaseConfig;
import br.com.startjob.acesso.service.FirebasePushResult;
import br.com.startjob.acesso.service.FirebasePushService;
import br.com.startjob.acesso.utils.JwtConfig;
import br.com.startjob.acesso.to.app.LinkConviteVisitanteRequest;
import br.com.startjob.acesso.to.app.LinkConviteVisitanteResponse;
import br.com.startjob.acesso.to.app.LoginRequest;
import br.com.startjob.acesso.to.app.LoginResponse;
import br.com.startjob.acesso.to.app.PushTestRequest;
import br.com.startjob.acesso.to.app.PaginatedResponse;
import br.com.startjob.acesso.to.app.PedestreResumoDTO;
import br.com.startjob.acesso.to.app.ResumoResponse;
import br.com.startjob.acesso.to.app.UsuarioDTO;
import br.com.startjob.acesso.utils.JwtUtil;
import io.jsonwebtoken.Claims;

/**
 * API mobile: base {@code /sistema/restful-services/app}.
 */
@Path("/app")
public class AppRequestService extends BaseService {

	private static final Logger LOG = Logger.getLogger(AppRequestService.class.getName());

	@Context
	private HttpServletRequest request;

	@Context
	private HttpServletResponse response;

	CriptografiaAES cript = new CriptografiaAES();

	@EJB
	private AppEJBRemote appEjb;

	@Resource(mappedName = "java:/mail/suporte")
	private Session mailSession;

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(LoginRequest loginRequest) {

		System.out.println("Validando login");

		try {

			if (loginRequest == null || loginRequest.getLogin() == null || loginRequest.getSenha() == null
					|| loginRequest.getCliente() == null) {
				return AppApiResponses.jsonError(Status.BAD_REQUEST, "Parâmetros inválidos", "INVALID_PARAMS");
			}

			System.out.println("parametros login : " + loginRequest.getLogin() + " , cliente : "
					+ loginRequest.getCliente());

			PedestreEntity usuario = appEjb.buscarPorLoginECliente(loginRequest.getLogin(), loginRequest.getCliente());

			if (usuario == null) {
				return AppApiResponses.jsonError(Status.UNAUTHORIZED, "Usuário ou cliente inválido",
						"INVALID_CREDENTIALS");
			}

			System.out.println("usuario encontrado");

			if (!usuario.getSenha().equals(EncryptionUtils.encrypt(loginRequest.getSenha()))) {
				return AppApiResponses.jsonError(Status.UNAUTHORIZED, "Senha inválida", "INVALID_PASSWORD");
			}
			System.out.println("senha valida");

			if (usuario.getPerfilApp() == null) {
				return AppApiResponses.jsonError(Status.BAD_REQUEST, "Sem perfil de usuario", "NO_APP_PROFILE");
			}

			String token = JwtUtil.gerarToken(usuario.getId(), loginRequest.getCliente(),
					usuario.getPerfilApp().name());

			UsuarioDTO userDto = new UsuarioDTO();
			userDto.setId(usuario.getId());
			userDto.setNome(usuario.getNome() != null && !usuario.getNome().trim().isEmpty()
					? usuario.getNome().trim()
					: usuario.getLogin());
			userDto.setCliente(loginRequest.getCliente());
			userDto.setPerfil(usuario.getPerfilApp().name());

			LoginResponse loginResponse = new LoginResponse();
			loginResponse.setToken(token);
			loginResponse.setTipo("Bearer");
			loginResponse.setUsuario(userDto);

			LOG.info("App login OK: pedestreId=" + usuario.getId() + ", cliente=" + loginRequest.getCliente()
					+ ", perfil=" + usuario.getPerfilApp().name()
					+ " (mobile deve chamar POST /app/device-token em seguida)");

			return Response.ok(loginResponse).build();

		} catch (IllegalStateException e) {
			return AppApiResponses.jsonError(Status.INTERNAL_SERVER_ERROR,
					"Servidor sem jwt.secret configurado", "JWT_NOT_CONFIGURED");
		} catch (Exception e) {
			e.printStackTrace();
			return AppApiResponses.jsonError(Status.INTERNAL_SERVER_ERROR, "Erro ao realizar login", "INTERNAL_ERROR");
		}
	}

	@GET
	@Path("/health")
	@Produces(MediaType.APPLICATION_JSON)
	public Response health() {
		HealthResponse health = new HealthResponse();
		health.setJwtConfigured(JwtConfig.isConfigured());
		String firebasePath = FirebaseConfig.resolveServiceAccountPath();
		health.setFirebasePathConfigured(firebasePath != null && !firebasePath.trim().isEmpty());
		health.setFirebaseFileExists(FirebaseConfig.isServiceAccountConfigured());
		health.setFirebaseReady(FirebasePushService.tryEnsureInitialized());
		if (!health.isJwtConfigured()) {
			health.setStatus("degraded");
		} else if (!health.isFirebaseReady()) {
			health.setStatus("degraded");
		}
		return Response.ok(health).build();
	}

	/**
	 * Envio manual de push (perfil GERENCIAL) para validar Firebase após deploy.
	 */
	@POST
	@Path("/push/test")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response pushTest(@Context HttpHeaders headers, PushTestRequest body) {
		try {
			AppAuthContext auth = resolveAuth(headers);
			if (auth.error != null) {
				return auth.error;
			}
			if (!isPerfilGerencial(auth.perfil)) {
				return AppApiResponses.jsonError(Status.FORBIDDEN, "Perfil não autorizado", "FORBIDDEN");
			}
			if (body == null || body.getFcmToken() == null || body.getFcmToken().trim().isEmpty()) {
				return AppApiResponses.jsonError(Status.BAD_REQUEST, "fcmToken é obrigatório", "INVALID_PARAMS");
			}
			String fcmTokenTeste = body.getFcmToken().trim();
			if (AppPushNotificationService.pareceTokenExpo(fcmTokenTeste)) {
				return AppApiResponses.jsonError(Status.BAD_REQUEST,
						"Token Expo invalido para Firebase Admin SDK. Use getDevicePushTokenAsync() no app.",
						"EXPO_TOKEN_NOT_SUPPORTED");
			}

			String title = body.getTitle() != null && !body.getTitle().trim().isEmpty()
					? body.getTitle().trim() : "Teste Smart Acesso";
			String text = body.getBody() != null && !body.getBody().trim().isEmpty()
					? body.getBody().trim() : "Push de teste do servidor";

			FirebasePushResult result = FirebasePushService.send(appEjb, fcmTokenTeste, title, text,
					AppPushNotificationService.montarPayload("aviso", "/avisos", null, title, text));

			if (result.isSuccess()) {
				java.util.Map<String, Object> ok = new java.util.HashMap<>();
				ok.put("ok", true);
				ok.put("messageId", result.getMessageId());
				return Response.ok(ok).build();
			}
			return AppApiResponses.jsonError(Status.BAD_REQUEST,
					result.getError() != null ? result.getError() : "Falha ao enviar push", "PUSH_FAILED");
		} catch (Exception e) {
			e.printStackTrace();
			return AppApiResponses.jsonError(Status.INTERNAL_SERVER_ERROR, "Erro ao enviar push de teste",
					"INTERNAL_ERROR");
		}
	}

	/**
	 * Diagnóstico: verifica se o usuário logado tem token FCM registrado para receber push.
	 */
	@GET
	@Path("/push/status")
	@Produces(MediaType.APPLICATION_JSON)
	public Response pushStatus(@Context HttpHeaders headers) {
		try {
			AppAuthContext auth = resolveAuth(headers);
			if (auth.error != null) {
				return auth.error;
			}

			List<DeviceTokenEntity> tokens = appEjb.buscarDeviceTokensAtivos(auth.userId);
			PushStatusResponse status = new PushStatusResponse();
			status.setPedestreId(auth.userId);
			status.setTokensAtivos(tokens != null ? tokens.size() : 0);

			if (tokens != null) {
				for (DeviceTokenEntity token : tokens) {
					PushStatusResponse.TokenResumo resumo = new PushStatusResponse.TokenResumo();
					resumo.setPlatform(token.getPlatform());
					resumo.setAppVersion(token.getAppVersion());
					resumo.setTokenMascarado(mascararFcmToken(token.getFcmToken()));
					resumo.setPareceExpoToken(AppPushNotificationService.pareceTokenExpo(token.getFcmToken()));
					status.getTokens().add(resumo);
				}
			}
			return Response.ok(status).build();
		} catch (Exception e) {
			e.printStackTrace();
			return AppApiResponses.jsonError(Status.INTERNAL_SERVER_ERROR, "Erro ao consultar status de push",
					"INTERNAL_ERROR");
		}
	}

	@GET
	@Path("/me")
	@Produces(MediaType.APPLICATION_JSON)
	public Response me(@Context HttpHeaders headers) {
		try {
			AppAuthContext auth = resolveAuth(headers);
			if (auth.error != null) {
				return auth.error;
			}

			PedestreEJBRemote pedestreEJB = (PedestreEJBRemote) getEjb("PedestreEJB");
			PedestreEntity pedestre = buscaPedestrePorId(auth.userId, auth.cliente.getId(), pedestreEJB);
			if (pedestre == null) {
				return AppApiResponses.jsonError(Status.NOT_FOUND, "Usuário não encontrado", "NOT_FOUND");
			}

			UsuarioDTO userDto = new UsuarioDTO();
			userDto.setId(pedestre.getId());
			userDto.setNome(pedestre.getNome() != null && !pedestre.getNome().trim().isEmpty()
					? pedestre.getNome().trim()
					: pedestre.getLogin());
			userDto.setCliente(auth.cliente.getNomeUnidadeOrganizacional());
			userDto.setPerfil(auth.perfil);

			return Response.ok(userDto).build();
		} catch (Exception e) {
			e.printStackTrace();
			return AppApiResponses.jsonError(Status.UNAUTHORIZED, "Token inválido", "TOKEN_INVALID");
		}
	}

	@POST
	@Path("/acessos")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response acessos(@Context HttpHeaders headers, AcessosRequest body) {
		try {
			AppAuthContext auth = resolveAuth(headers);
			if (auth.error != null) {
				return auth.error;
			}
			if (body == null) {
				return AppApiResponses.jsonError(Status.BAD_REQUEST, "Corpo da requisição vazio", "INVALID_BODY");
			}

			DateRange range = parseDateRange(body.getDataInicio(), body.getDataFim(), true);
			List<Long> idsPermitidos = idsPermitidosPorPerfil(auth.userId, auth.perfil);

			int pagina = body.getPagina();
			int tamanho = normalizeTamanho(body.getTamanho());

			PageResult<AcessoEntity> page = appEjb.buscarAcessosPaginados(idsPermitidos, auth.cliente.getId(),
					range.inicio, range.fim, body.getSentido(), body.getBusca(), pagina, tamanho);

			List<AcessoItemDTO> content = page.getItems().stream().map(this::toAcessoItem)
					.collect(Collectors.toList());

			return Response.ok(PaginatedResponse.of(content, page.getTotal(), pagina, tamanho)).build();

		} catch (Exception e) {
			e.printStackTrace();
			return AppApiResponses.jsonError(Status.UNAUTHORIZED, "Token inválido ou erro na consulta",
					"TOKEN_INVALID");
		}
	}

	@POST
	@Path("/encomendas")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response encomendas(@Context HttpHeaders headers, EncomendaRequest body) {
		try {
			AppAuthContext auth = resolveAuth(headers);
			if (auth.error != null) {
				return auth.error;
			}
			if (body == null) {
				return AppApiResponses.jsonError(Status.BAD_REQUEST, "Corpo da requisição vazio", "INVALID_BODY");
			}

			DateRange range = parseDateRange(body.getDataInicio(), body.getDataFim(), false);
			int pagina = body.getPagina();
			int tamanho = normalizeTamanho(body.getTamanho());

			boolean listarTodasDoCliente = isPerfilGerencial(auth.perfil);
			PageResult<EncomendaListItem> page = appEjb.buscarEncomendasPaginada(auth.userId,
					auth.cliente.getId(), range.inicio, range.fim, body.getStatus(), body.getBusca(), pagina,
					tamanho, listarTodasDoCliente);

			boolean perfilGerencial = listarTodasDoCliente;
			List<EncomendaItemDTO> content = page.getItems().stream()
					.map(item -> EncomendaItemDTO.from(item, auth.userId, perfilGerencial))
					.collect(Collectors.toList());
			return Response.ok(PaginatedResponse.of(content, page.getTotal(), pagina, tamanho)).build();

		} catch (Exception e) {
			e.printStackTrace();
			return AppApiResponses.jsonError(Status.UNAUTHORIZED, "Token inválido ou erro na consulta",
					"TOKEN_INVALID");
		}
	}

	@POST
	@Path("/encomendas/confirmar-retirada")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response confirmarRetiradaEncomenda(@Context HttpHeaders headers, ConfirmarRetiradaRequest body) {
		try {
			AppAuthContext auth = resolveAuth(headers);
			if (auth.error != null) {
				return auth.error;
			}
			if (body == null || body.getId() == null) {
				return AppApiResponses.jsonError(Status.BAD_REQUEST, "id é obrigatório", "INVALID_PARAMS");
			}
			if (body.getNomeQuemRetirou() == null || body.getNomeQuemRetirou().trim().isEmpty()
					|| body.getDocumentoQuemRetirou() == null || body.getDocumentoQuemRetirou().trim().isEmpty()) {
				return AppApiResponses.jsonError(Status.BAD_REQUEST,
						"nomeQuemRetirou e documentoQuemRetirou são obrigatórios", "INVALID_PARAMS");
			}

			boolean perfilGerencial = isPerfilGerencial(auth.perfil);
			EncomendaListItem atualizada = appEjb.confirmarRetiradaEncomenda(body.getId(), auth.userId,
					auth.cliente.getId(), body.getNomeQuemRetirou(), body.getDocumentoQuemRetirou(), perfilGerencial);

			try {
				br.com.startjob.acesso.modelo.entity.CorrespondenciaEntity paraEmail = appEjb
						.buscarCorrespondenciaPorIdECliente(body.getId(), auth.cliente.getId());
				if (paraEmail != null) {
					MailSendUtils.enviaConfirmacaoRetirada(paraEmail, mailSession);
				}
			} catch (Exception mailEx) {
				System.err.println("Erro ao enviar e-mail de confirmação de retirada: " + mailEx.getMessage());
				mailEx.printStackTrace();
			}

			new AppPushNotificationService(appEjb).notificarRetiradaEncomenda(atualizada);

			return Response.ok(EncomendaItemDTO.from(atualizada, auth.userId, perfilGerencial)).build();

		} catch (Exception e) {
			String code = e.getMessage();
			if ("NOT_FOUND".equals(code)) {
				return AppApiResponses.jsonError(Status.NOT_FOUND, "Encomenda não encontrada", "NOT_FOUND");
			}
			if ("FORBIDDEN".equals(code)) {
				return AppApiResponses.jsonError(Status.FORBIDDEN,
						"Você não tem permissão para confirmar a retirada desta encomenda", "FORBIDDEN");
			}
			if ("ALREADY_CONFIRMED".equals(code)) {
				return AppApiResponses.jsonError(Status.BAD_REQUEST, "Encomenda já foi retirada", "ALREADY_CONFIRMED");
			}
			if ("INVALID_PARAMS".equals(code)) {
				return AppApiResponses.jsonError(Status.BAD_REQUEST, "Parâmetros inválidos", "INVALID_PARAMS");
			}
			e.printStackTrace();
			return AppApiResponses.jsonError(Status.INTERNAL_SERVER_ERROR, "Erro ao confirmar retirada",
					"INTERNAL_ERROR");
		}
	}

	@POST
	@Path("/avisos")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response avisos(@Context HttpHeaders headers, AvisosRequest body) {
		try {
			AppAuthContext auth = resolveAuth(headers);
			if (auth.error != null) {
				return auth.error;
			}

			int pagina = body != null ? body.getPagina() : 0;
			int tamanho = normalizeTamanho(body != null ? body.getTamanho() : 20);
			String busca = body != null ? body.getBusca() : "";

			PageResult<AvisoListItem> page = appEjb.buscarAvisosPaginados(auth.cliente.getId(), busca, pagina,
					tamanho);

			List<AvisoItemDTO> content = page.getItems().stream().map(this::toAvisoItem).collect(Collectors.toList());

			return Response.ok(PaginatedResponse.of(content, page.getTotal(), pagina, tamanho)).build();

		} catch (Exception e) {
			e.printStackTrace();
			return AppApiResponses.jsonError(Status.UNAUTHORIZED, "Token inválido ou erro na consulta",
					"TOKEN_INVALID");
		}
	}

	/**
	 * Cadastro/edição de aviso pelo app (perfil gerencial). Imagem opcional em {@code imagemBase64}.
	 */
	@POST
	@Path("/avisos/salvar")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response salvarAviso(@Context HttpHeaders headers, AvisoSalvarRequest body) {
		try {
			AppAuthContext auth = resolveAuth(headers);
			if (auth.error != null) {
				return auth.error;
			}
			if (!PerfilAcessoApp.GERENCIAL.name().equals(auth.perfil)) {
				return AppApiResponses.jsonError(Status.FORBIDDEN, "Perfil não autorizado", "FORBIDDEN");
			}
			if (body == null || body.getTitulo() == null || body.getTitulo().trim().isEmpty()) {
				return AppApiResponses.jsonError(Status.BAD_REQUEST, "Título é obrigatório", "INVALID_PARAMS");
			}

			AvisoAppEntity aviso;
			if (body.getId() != null) {
				aviso = appEjb.buscarAvisoAppPorId(body.getId(), auth.cliente.getId());
				if (aviso == null) {
					return AppApiResponses.jsonError(Status.BAD_REQUEST, "Aviso não encontrado", "NOT_FOUND");
				}
			} else {
				aviso = new AvisoAppEntity();
				aviso.setCliente(auth.cliente);
			}

			aviso.setTitulo(body.getTitulo().trim());
			aviso.setDescricao(body.getDescricao());
			aviso.setDataPublicacao(parseDataPublicacao(body.getDataPublicacao()));
			if (body.getImagemBase64() != null && !body.getImagemBase64().trim().isEmpty()) {
				try {
					aviso.setImagem(ImagemBase64Util.decodificar(body.getImagemBase64()));
				} catch (IllegalArgumentException ex) {
					return AppApiResponses.jsonError(Status.BAD_REQUEST, "imagemBase64 inválida", "INVALID_PARAMS");
				}
			}

			boolean novo = body.getId() == null;
			AvisoAppEntity salvo = appEjb.salvarAvisoApp(aviso);
			new AppPushNotificationService(appEjb).notificarAviso(salvo, novo);
			return Response.ok(toAvisoItemFromEntity(salvo)).build();

		} catch (Exception e) {
			e.printStackTrace();
			return AppApiResponses.jsonError(Status.BAD_REQUEST, "Erro ao salvar aviso", "SAVE_ERROR");
		}
	}

	@DELETE
	@Path("/avisos/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response excluirAviso(@Context HttpHeaders headers, @PathParam("id") Long id) {
		try {
			AppAuthContext auth = resolveAuth(headers);
			if (auth.error != null) {
				return auth.error;
			}
			if (!PerfilAcessoApp.GERENCIAL.name().equals(auth.perfil)) {
				return AppApiResponses.jsonError(Status.FORBIDDEN, "Perfil não autorizado", "FORBIDDEN");
			}
			if (id == null) {
				return AppApiResponses.jsonError(Status.BAD_REQUEST, "id é obrigatório", "INVALID_PARAMS");
			}

			appEjb.excluirAvisoApp(id, auth.cliente.getId());
			return Response.ok().build();
		} catch (Exception e) {
			if ("NOT_FOUND".equals(e.getMessage())) {
				return AppApiResponses.jsonError(Status.NOT_FOUND, "Aviso não encontrado", "NOT_FOUND");
			}
			e.printStackTrace();
			return AppApiResponses.jsonError(Status.BAD_REQUEST, "Erro ao excluir aviso", "DELETE_ERROR");
		}
	}

	@GET
	@Path("/avisos/{id}/imagem")
	@Produces({"image/jpeg", "image/png", "application/octet-stream"})
	public Response imagemAviso(@Context HttpHeaders headers, @PathParam("id") Long id) {
		try {
			AppAuthContext auth = resolveAuth(headers);
			if (auth.error != null) {
				return auth.error;
			}
			byte[] imagem = appEjb.buscarImagemAvisoApp(id, auth.cliente.getId());
			if (imagem == null || imagem.length == 0) {
				return Response.status(Status.NOT_FOUND).build();
			}
			return Response.ok(imagem).type("image/jpeg").build();
		} catch (Exception e) {
			e.printStackTrace();
			return AppApiResponses.jsonError(Status.UNAUTHORIZED, "Token inválido", "TOKEN_INVALID");
		}
	}

	@POST
	@Path("/device-token")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response registrarDeviceToken(@Context HttpHeaders headers, DeviceTokenRequest body) {
		try {
			AppAuthContext auth = resolveAuth(headers);
			if (auth.error != null) {
				return auth.error;
			}
			if (body == null || body.getFcmToken() == null || body.getFcmToken().trim().isEmpty()) {
				return AppApiResponses.jsonError(Status.BAD_REQUEST, "fcmToken é obrigatório", "INVALID_PARAMS");
			}
			if (body.getPlatform() == null || !isPlataformaValida(body.getPlatform())) {
				return AppApiResponses.jsonError(Status.BAD_REQUEST, "platform deve ser ANDROID ou IOS", "INVALID_PARAMS");
			}
			String fcmToken = body.getFcmToken().trim();
			if (AppPushNotificationService.pareceTokenExpo(fcmToken)) {
				LOG.warning("Device token rejeitado (Expo): pedestreId=" + auth.userId + ", token="
						+ mascararFcmToken(fcmToken));
				return AppApiResponses.jsonError(Status.BAD_REQUEST,
						"Token Expo nao e aceito. No app use Notifications.getDevicePushTokenAsync() "
								+ "em build nativo (expo run:android / EAS), nao getExpoPushTokenAsync() nem Expo Go",
						"EXPO_TOKEN_NOT_SUPPORTED");
			}

			appEjb.upsertDeviceToken(auth.userId, fcmToken, body.getPlatform(), body.getAppVersion());
			int tokensAtivos = appEjb.buscarDeviceTokensAtivos(auth.userId).size();
			LOG.info("Device token registrado: pedestreId=" + auth.userId + ", platform=" + body.getPlatform()
					+ ", tokensAtivos=" + tokensAtivos + ", token=" + mascararFcmToken(fcmToken));

			Map<String, Object> ok = new HashMap<>();
			ok.put("ok", true);
			ok.put("pedestreId", auth.userId);
			ok.put("tokensAtivos", tokensAtivos);
			ok.put("tokenMascarado", mascararFcmToken(fcmToken));
			return Response.ok(ok).build();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Erro ao registrar device token", e);
			return AppApiResponses.jsonError(Status.INTERNAL_SERVER_ERROR, "Erro ao registrar device token",
					"INTERNAL_ERROR");
		}
	}

	@DELETE
	@Path("/device-token")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removerDeviceToken(@Context HttpHeaders headers, DeviceTokenRequest body) {
		try {
			AppAuthContext auth = resolveAuth(headers);
			if (auth.error != null) {
				return auth.error;
			}

			String fcmToken = body != null ? body.getFcmToken() : null;
			appEjb.invalidarDeviceToken(auth.userId, fcmToken);
			LOG.info("Device token invalidado: pedestreId=" + auth.userId
					+ (fcmToken != null && !fcmToken.trim().isEmpty()
							? ", token=" + mascararFcmToken(fcmToken.trim()) : ", todos os tokens"));
			return Response.ok().build();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Erro ao invalidar device token", e);
			return AppApiResponses.jsonError(Status.INTERNAL_SERVER_ERROR, "Erro ao invalidar device token",
					"INTERNAL_ERROR");
		}
	}

	@POST
	@Path("/resumo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response resumo(@Context HttpHeaders headers) {
		try {
			AppAuthContext auth = resolveAuth(headers);
			if (auth.error != null) {
				return auth.error;
			}

			List<Long> idsPermitidos = idsPermitidosPorPerfil(auth.userId, auth.perfil);

			ResumoResponse resumo = new ResumoResponse();
			resumo.setAcessosHoje(appEjb.contarAcessosHoje(idsPermitidos, auth.cliente.getId()));
			boolean listarTodasDoCliente = isPerfilGerencial(auth.perfil);
			resumo.setEncomendasPendentes(
					appEjb.contarEncomendasPendentes(auth.userId, auth.cliente.getId(), listarTodasDoCliente));

			return Response.ok(resumo).build();

		} catch (Exception e) {
			e.printStackTrace();
			return AppApiResponses.jsonError(Status.UNAUTHORIZED, "Token inválido ou erro na consulta",
					"TOKEN_INVALID");
		}
	}

	@POST
	@Path("/cadastrar")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response cadastrar(@Context HttpHeaders headers, CadastroRequest novoCadastro) {
		try {
			AppAuthContext auth = resolveAuth(headers);
			if (auth.error != null) {
				return auth.error;
			}
			if (!PerfilAcessoApp.GERENCIAL.name().equals(auth.perfil)) {
				return AppApiResponses.jsonError(Status.FORBIDDEN, "Perfil não autorizado", "FORBIDDEN");
			}

			if (novoCadastro == null) {
				return AppApiResponses.jsonError(Status.BAD_REQUEST, "Corpo da requisição vazio", "INVALID_BODY");
			}

			PedestreEntity novo = criarNovoCadastro(auth.cliente, novoCadastro);

			PedestreEntity salva = (PedestreEntity) getEjb("BaseEJB").gravaObjeto(novo)[0];

			return Response.status(Status.CREATED).entity(CadastroVisitanteResponseDTO.from(salva)).build();

		} catch (Exception e) {
			e.printStackTrace();
			return AppApiResponses.jsonError(Status.INTERNAL_SERVER_ERROR,
					"Erro ao salvar cadastro: " + e.getMessage(), "INTERNAL_ERROR");
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
		novo.setObservacoes(novoDto.getOberservacao());
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

	@GET
	@Path("/empresas")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listarEmpresas(@Context HttpHeaders headers) {

		try {
			AppAuthContext auth = resolveAuth(headers);
			if (auth.error != null) {
				return auth.error;
			}
			if (!PerfilAcessoApp.GERENCIAL.name().equals(auth.perfil)) {
				return AppApiResponses.jsonError(Status.FORBIDDEN, "Perfil não autorizado", "FORBIDDEN");
			}

			PedestreEJBRemote pedestreEJB = (PedestreEJBRemote) getEjb("PedestreEJB");

			java.util.Map<String, Object> args = new java.util.HashMap<>();
			args.put("ID_CLIENTE", auth.cliente.getId());
			@SuppressWarnings("unchecked")
			java.util.List<EmpresaEntity> empresas = (java.util.List<EmpresaEntity>) pedestreEJB
					.pesquisaArgFixos(EmpresaEntity.class, "findAllByIdCliente2", args);

			List<EmpresaResumoDTO> resumo = empresas != null
					? empresas.stream().map(EmpresaResumoDTO::from).collect(Collectors.toList())
					: new ArrayList<>();

			return Response.ok(resumo).build();

		} catch (Exception e) {
			e.printStackTrace();
			return AppApiResponses.jsonError(Status.INTERNAL_SERVER_ERROR, "Erro ao listar empresas", "INTERNAL_ERROR");
		}
	}

	@POST
	@Path("/visitante/link-convite")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response gerarLinkConviteVisitante(@Context HttpHeaders headers, LinkConviteVisitanteRequest body) {

		try {
			AppAuthContext auth = resolveAuth(headers);
			if (auth.error != null) {
				return auth.error;
			}

			if (!PerfilAcessoApp.GERENCIAL.name().equals(auth.perfil)) {
				return AppApiResponses.jsonError(Status.FORBIDDEN,
						"Perfil não autorizado a gerar link de visitante", "FORBIDDEN");
			}

			if (body == null || body.getIdEmpresa() == null) {
				return AppApiResponses.jsonError(Status.BAD_REQUEST, "idEmpresa é obrigatório", "INVALID_PARAMS");
			}

			PedestreEJBRemote pedestreEJB = (PedestreEJBRemote) getEjb("PedestreEJB");
			CadastroFacialLinkService linkService = new CadastroFacialLinkService(pedestreEJB);

			EmpresaEntity empresa = linkService.buscaEmpresaPorId(body.getIdEmpresa(), auth.cliente.getId());
			if (empresa == null) {
				return AppApiResponses.jsonError(Status.BAD_REQUEST, "Empresa inválida para este cliente",
						"INVALID_PARAMS");
			}

			PedestreEntity gerador = buscaPedestrePorId(auth.userId, auth.cliente.getId(), pedestreEJB);
			if (gerador == null) {
				return AppApiResponses.jsonError(Status.UNAUTHORIZED, "Pedestre gerador não encontrado",
						"INVALID_CREDENTIALS");
			}

			long tokenConvite = linkService.calcularTokenValidade(auth.cliente.getId());
			linkService.gravarCadastroExternoConvite(auth.cliente, empresa, tokenConvite, gerador);

			String link = linkService.montarUrlConvite(auth.cliente.getId(), empresa.getId(), tokenConvite);
			return Response.ok(new LinkConviteVisitanteResponse(link, tokenConvite, empresa.getId())).build();

		} catch (Exception e) {
			e.printStackTrace();
			return AppApiResponses.jsonError(Status.INTERNAL_SERVER_ERROR,
					"Erro ao gerar link: " + e.getMessage(), "INTERNAL_ERROR");
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

	private AcessoItemDTO toAcessoItem(AcessoEntity entity) {
		AcessoItemDTO dto = new AcessoItemDTO();
		dto.setSentido(entity.getSentido());
		dto.setData(entity.getData());
		dto.setLocal(entity.getLocal());
		PedestreResumoDTO pedestre = new PedestreResumoDTO();
		if (entity.getPedestre() != null) {
			pedestre.setNome(entity.getPedestre().getNome());
		}
		pedestre.setFoto(null);
		dto.setPedestre(pedestre);
		return dto;
	}

	private AvisoItemDTO toAvisoItem(AvisoListItem item) {
		AvisoItemDTO dto = new AvisoItemDTO();
		dto.setId(item.getId());
		dto.setTitulo(item.getTitulo());
		dto.setDescricao(item.getDescricao());
		dto.setDataPublicacao(item.getDataPublicacao());
		dto.setTemImagem(item.isTemImagem());
		return dto;
	}

	private AvisoItemDTO toAvisoItemFromEntity(AvisoAppEntity aviso) {
		AvisoItemDTO dto = new AvisoItemDTO();
		dto.setId(aviso.getId());
		dto.setTitulo(aviso.getTitulo());
		dto.setDescricao(aviso.getDescricao());
		dto.setDataPublicacao(aviso.getDataPublicacao());
		dto.setTemImagem(aviso.isTemImagem());
		return dto;
	}

	private Date parseDataPublicacao(String valor) throws Exception {
		if (valor == null || valor.trim().isEmpty()) {
			return new Date();
		}
		String v = valor.trim();
		if (v.length() > 10 && v.charAt(10) == ' ') {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(v);
		}
		try {
			return javax.xml.bind.DatatypeConverter.parseDateTime(v).getTime();
		} catch (IllegalArgumentException ex) {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(v);
		}
	}

	private List<Long> idsPermitidosPorPerfil(Long userId, String perfil) {
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
		return new ArrayList<>(idsPermitidos);
	}

	private int normalizeTamanho(int tamanho) {
		return tamanho > 0 ? tamanho : 20;
	}

	private DateRange parseDateRange(String dataInicioStr, String dataFimStr, boolean defaultFimHoje)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dataInicio = null;
		Date dataFim = null;

		if (dataInicioStr != null && !dataInicioStr.isEmpty()) {
			dataInicio = sdf.parse(dataInicioStr);
		}
		if (dataFimStr != null && !dataFimStr.isEmpty()) {
			dataFim = sdf.parse(dataFimStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataFim);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			dataFim = cal.getTime();
		} else if (defaultFimHoje) {
			dataFim = new Date();
		}
		return new DateRange(dataInicio, dataFim);
	}

	private static String mascararFcmToken(String token) {
		if (token == null || token.length() <= 8) {
			return "***";
		}
		return token.substring(0, 4) + "..." + token.substring(token.length() - 4);
	}

	private boolean isPlataformaValida(String platform) {
		if (platform == null) {
			return false;
		}
		String p = platform.trim().toLowerCase();
		return "android".equals(p) || "ios".equals(p) || "iphone".equals(p) || "ipad".equals(p);
	}

	private boolean isPerfilGerencial(String perfil) {
		return PerfilAcessoApp.GERENCIAL.name().equals(perfil);
	}

	private AppAuthContext resolveAuth(HttpHeaders headers) throws Exception {
		String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return AppAuthContext.withError(
					AppApiResponses.jsonError(Status.UNAUTHORIZED, "Token não fornecido", "TOKEN_MISSING"));
		}

		Claims claims = JwtUtil.validarToken(authHeader.substring(7));
		Long userId = Long.parseLong(claims.getSubject());
		String clienteNome = (String) claims.get("cliente");
		String perfil = (String) claims.get("perfil");
		ClienteEntity cliente = appEjb.buscaClientesPorUnidadeOrganizacional(clienteNome);

		return new AppAuthContext(userId, perfil, cliente, null);
	}

	private static final class DateRange {
		private final Date inicio;
		private final Date fim;

		private DateRange(Date inicio, Date fim) {
			this.inicio = inicio;
			this.fim = fim;
		}
	}

	private static final class AppAuthContext {
		private final Long userId;
		private final String perfil;
		private final ClienteEntity cliente;
		private final Response error;

		private AppAuthContext(Long userId, String perfil, ClienteEntity cliente, Response error) {
			this.userId = userId;
			this.perfil = perfil;
			this.cliente = cliente;
			this.error = error;
		}

		private static AppAuthContext withError(Response error) {
			return new AppAuthContext(null, null, null, error);
		}
	}
}
