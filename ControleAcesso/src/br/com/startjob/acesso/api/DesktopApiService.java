package br.com.startjob.acesso.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Base64;
import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONException;
import org.primefaces.shaded.json.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.BiometriaEntity;
import br.com.startjob.acesso.modelo.entity.CargoEntity;
import br.com.startjob.acesso.modelo.entity.CentroCustoEntity;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.ConfiguracoesDesktopEntity;
import br.com.startjob.acesso.modelo.entity.DepartamentoEntity;
import br.com.startjob.acesso.modelo.entity.DesktopVersionEntity;
import br.com.startjob.acesso.modelo.entity.DocumentoEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.EnderecoEntity;
import br.com.startjob.acesso.modelo.entity.EquipamentoEntity;
import br.com.startjob.acesso.modelo.entity.HorarioEntity;
import br.com.startjob.acesso.modelo.entity.MensagemEquipamentoEntity;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEquipamentoEntity;
import br.com.startjob.acesso.modelo.entity.PedestreRegraEntity;
import br.com.startjob.acesso.modelo.entity.PlanoEntity;
import br.com.startjob.acesso.modelo.entity.RegraEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.entity.base.BaseEntity;
import br.com.startjob.acesso.modelo.enumeration.Dedo;
import br.com.startjob.acesso.modelo.enumeration.Genero;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.enumeration.TipoQRCode;
import br.com.startjob.acesso.modelo.enumeration.TipoRegra;
import br.com.startjob.acesso.to.DeviceTO;
import br.com.startjob.acesso.to.EmpresaTO;
import br.com.startjob.acesso.to.PedestrianAccessTO;
import br.com.startjob.acesso.to.RegraTO;
import br.com.startjob.acesso.to.UserTO;

@SuppressWarnings("unchecked")
@Path("/access")
public class DesktopApiService extends BaseService {

	@Context
	private HttpServletRequest request;

	@Context
	private HttpServletResponse response;

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:sss");

	/**
	 * Testa webservice.
	 * 
	 * @return string
	 */
	@GET
	@Path("/action")
	@Produces(MediaType.TEXT_PLAIN)
	public Response action() {
		return Response.status(Status.OK).entity("working").build();
	}

	@GET
	@Path("/request")
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestAll(@QueryParam("client") Long idClient, @QueryParam("lastsync") Long lastSync,
			@QueryParam("version") String version) {

		List<PedestrianAccessTO> pedestrianAccessList = new ArrayList<PedestrianAccessTO>();
		Status statusResponse = Status.NOT_FOUND;

		Calendar cLastSync = Calendar.getInstance();
		cLastSync.setTimeInMillis(lastSync);

		if (idClient == null)
			return Response.status(Status.BAD_REQUEST).entity(pedestrianAccessList).build();

		try {
			List<Long> ids = new ArrayList<>();
			ids.add(idClient);

			Double versao = Double.valueOf(version);

			List<Object[]> pedestreList = null;
			if (versao < 3.d) {
				pedestreList = (List<Object[]>) ((PedestreEJBRemote) getEjb("PedestreEJB"))
						.recuperaPedestresControleAcesso(ids, cLastSync.getTime());

			} else if (versao < 3.09d) {
				pedestreList = (List<Object[]>) ((PedestreEJBRemote) getEjb("PedestreEJB"))
						.recuperaPedestresControleAcessoComListas(ids, cLastSync.getTime(), false);
			} else {
				pedestreList = (List<Object[]>) ((PedestreEJBRemote) getEjb("PedestreEJB"))
						.recuperaPedestresControleAcessoComListas(ids, cLastSync.getTime(), true);
			}

			if (pedestreList == null || pedestreList.isEmpty()) {
				return Response.status(Status.OK).entity(pedestrianAccessList).build();
			}

			long id = 0;
			PedestrianAccessTO to = null;

			for (Object[] objects : pedestreList) {
				if (id != Long.valueOf(objects[0].toString())) {
					// cria um TO com os dados
					to = new PedestrianAccessTO(objects, version);
					pedestrianAccessList.add(to);
					id = Long.valueOf(objects[0].toString());

				} else {
					// atualiza dados do TO
					to.adicionaBiometria(objects);
					to.adicionaHorarios(objects);

					to.adicionaMensagem(objects);
					to.adicionaEquipamentos(objects);
					to.adicionaDocumentos(objects);
					to.adicionaPedestreRegras(objects);
					to.adicionaHorarioPedestreRegra(objects);
				}
			}

			if (pedestrianAccessList.isEmpty()) {
				return Response.status(Status.NOT_FOUND).entity(pedestrianAccessList).build();
			}

			statusResponse = Status.OK;

		} catch (Exception e) {
			statusResponse = Status.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}

		return Response.status(statusResponse).entity(pedestrianAccessList).build();
	}

	@GET
	@Path("/requestAllUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestAllUsers(@QueryParam("client") Long idClient, @QueryParam("lastsync") Long lastSync,
			@QueryParam("version") String version) {
		if (idClient == null)
			return Response.status(Status.NOT_FOUND).entity("See status code").build();

		List<UserTO> usersAccessListTO = new ArrayList<>();
		Status statusResponse = Status.NOT_FOUND;
		Calendar cLastSync = Calendar.getInstance();
		cLastSync.setTimeInMillis(lastSync);

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("ID_CLIENTE", idClient);
			args.put("LAST_SYNC", cLastSync.getTime());

			List<UsuarioEntity> usersAccessList = (List<UsuarioEntity>) ((BaseEJBRemote) getEjb("BaseEJB"))
					.pesquisaArgFixos(UsuarioEntity.class, "findAllWithRemovidosByIdCliente", args);

			if (usersAccessList == null || usersAccessList.isEmpty())
				return Response.status(Status.OK).entity(usersAccessListTO).build();

			for (UsuarioEntity u : usersAccessList)
				usersAccessListTO.add(new UserTO(u));

			statusResponse = Status.OK;

		} catch (Exception e) {
			statusResponse = Status.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		return Response.status(statusResponse).entity(usersAccessListTO).build();
	}

	@GET
	@Path("/requestAllEmpresas")
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestAllEmpresas(@QueryParam("client") Long idClient, @QueryParam("lastsync") Long lastSync,
			@QueryParam("version") String version) {

		if (idClient == null)
			return Response.status(Status.NOT_FOUND).entity("See the status code.").build();

		List<EmpresaTO> empresasTO = new ArrayList<EmpresaTO>();
		Status statusResponse = Status.NOT_FOUND;
		Calendar cLastSync = Calendar.getInstance();
		cLastSync.setTimeInMillis(lastSync);

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("ID_CLIENTE", idClient);
			args.put("LAST_SYNC", cLastSync.getTime());
			List<EmpresaEntity> empresas = (List<EmpresaEntity>) ((BaseEJBRemote) getEjb("BaseEJB"))
					.pesquisaArgFixos(EmpresaEntity.class, "findAllByIdClienteAfterLastSync", args);

			statusResponse = Status.OK;

			if (empresas == null || empresas.isEmpty())
				return Response.status(statusResponse).entity(empresasTO).build();

			for (EmpresaEntity empresa : empresas) {
				empresa.setCliente(null);
				empresa.setCargos(buscaCargos(empresa.getId()));
				empresa.setCentros(buscaCentros(empresa.getId()));
				empresa.setDepartamentos(buscaDepartamentos(empresa.getId()));

				empresasTO.add(new EmpresaTO(empresa));
			}

		} catch (Exception e) {
			statusResponse = Status.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}

		return Response.status(statusResponse).entity(empresasTO).build();
	}

	@GET
	@Path("/requestAllRegras")
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestAllRegras(@QueryParam("client") Long idClient, @QueryParam("lastsync") Long lastSync) {

		if (idClient == null)
			return Response.status(Status.NOT_FOUND).entity("See the status code.").build();

		List<RegraTO> regrasTo = new ArrayList<>();
		Calendar cLastSync = Calendar.getInstance();
		cLastSync.setTimeInMillis(lastSync);

		Status statusResponse = Status.NOT_FOUND;

		try {
			Map<String, Object> args = new HashMap<>();
			args.put("ID_CLIENTE", idClient);
			args.put("LAST_SYNC", cLastSync.getTime());

			List<RegraEntity> regras = (List<RegraEntity>) ((BaseEJBRemote) getEjb("BaseEJB"))
					.pesquisaArgFixos(RegraEntity.class, "findAllByIdClienteAfterLastSync", args);

			if (regras == null || regras.isEmpty())
				return Response.status(Status.NOT_FOUND).entity("See the status code.").build();

			for (RegraEntity regra : regras) {
				regra.setHorarios(buscaHorarios(regra.getId()));

				regrasTo.add(new RegraTO(regra));
			}

			statusResponse = Status.OK;

		} catch (Exception e) {
			statusResponse = Status.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}

		return Response.status(statusResponse).entity(regrasTo).build();
	}

	@GET
	@Path("/requestAllParametros")
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestAllParametros(@QueryParam("client") Long idClient, @QueryParam("lastsync") Long lastSync) {

		if (idClient == null)
			return Response.status(Status.NOT_FOUND).entity("See the status code.").build();

		List<ParametroEntity> parametros = null;
		Calendar cLastSync = Calendar.getInstance();
		cLastSync.setTimeInMillis(lastSync);

		Status statusResponse = Status.NOT_FOUND;

		try {
			Map<String, Object> args = new HashMap<>();
			args.put("ID_CLIENTE", idClient);
			args.put("LAST_SYNC", cLastSync.getTime());

			parametros = (List<ParametroEntity>) ((BaseEJBRemote) getEjb("BaseEJB"))
					.pesquisaArgFixos(ParametroEntity.class, "findAllByIdClienteAfterLastSync", args);

			if (parametros == null || parametros.isEmpty())
				return Response.status(Status.NOT_FOUND).entity("See the status code.").build();

			parametros.forEach(parametro -> {
				parametro.setCliente(null);
			});

			statusResponse = Status.OK;

		} catch (Exception e) {
			statusResponse = Status.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}

		return Response.status(statusResponse).entity(parametros).build();
	}

	@GET
	@Path("/requestAllPlanos")
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestAllPlanos(@QueryParam("client") Long idClient, @QueryParam("lastsync") Long lastSync) {

		if (idClient == null)
			return Response.status(Status.NOT_FOUND).entity("See the status code.").build();

		List<PlanoEntity> planos = null;
		Calendar cLastSync = Calendar.getInstance();
		cLastSync.setTimeInMillis(lastSync);

		Status statusResponse = Status.NOT_FOUND;

		try {
			Map<String, Object> args = new HashMap<>();
			args.put("ID_CLIENTE", idClient);
			args.put("LAST_SYNC", cLastSync.getTime());

			planos = (List<PlanoEntity>) ((BaseEJBRemote) getEjb("BaseEJB")).pesquisaArgFixos(PlanoEntity.class,
					"findAllByIdClienteAfterLastSync", args);

			if (planos == null || planos.isEmpty())
				return Response.status(Status.NOT_FOUND).entity("See the status code.").build();

			planos.forEach(plano -> {
				plano.setCliente(null);
			});

			statusResponse = Status.OK;

		} catch (Exception e) {
			statusResponse = Status.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}

		return Response.status(statusResponse).entity(planos).build();
	}

	private List<HorarioEntity> buscaHorarios(Long idRegra) {
		List<HorarioEntity> horarios = null;

		try {
			Map<String, Object> args = new HashMap<>();
			args.put("ID_REGRA", idRegra);

			horarios = (List<HorarioEntity>) ((BaseEJBRemote) getEjb("BaseEJB")).pesquisaArgFixos(HorarioEntity.class,
					"findAllWithRemovidosByIdRegra", args);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return horarios;
	}

	private List<DepartamentoEntity> buscaDepartamentos(Long id) {
		List<DepartamentoEntity> departamentos = null;

		try {
			Map<String, Object> args = new HashMap<>();
			args.put("ID_EMPRESA", id);

			departamentos = (List<DepartamentoEntity>) ((BaseEJBRemote) getEjb("BaseEJB"))
					.pesquisaArgFixos(DepartamentoEntity.class, "findAllWithRemovidosByIdEmpresa", args);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return departamentos;
	}

	private List<CentroCustoEntity> buscaCentros(Long id) {
		List<CentroCustoEntity> centros = null;

		try {
			Map<String, Object> args = new HashMap<>();
			args.put("ID_EMPRESA", id);

			centros = (List<CentroCustoEntity>) ((BaseEJBRemote) getEjb("BaseEJB"))
					.pesquisaArgFixos(CentroCustoEntity.class, "findAllWithRemovidosByIdEmpresa", args);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return centros;
	}

	private List<CargoEntity> buscaCargos(Long id) {
		List<CargoEntity> cargos = null;

		try {
			Map<String, Object> args = new HashMap<>();
			args.put("ID_EMPRESA", id);

			cargos = (List<CargoEntity>) ((BaseEJBRemote) getEjb("BaseEJB")).pesquisaArgFixos(CargoEntity.class,
					"findAllWithRemovidosByIdEmpresa", args);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return cargos;
	}

	@POST
	@Path("/registerlog")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerlog(String parans) {
		Status statusResponse = Status.OK;

		List<AcessoEntity> logs = new ArrayList<>();
		UsuarioEntity user = null;

		try {
			JSONArray jsonArray = new JSONArray(parans);
			System.out.println("Quantide de logs recebidos: " + jsonArray.length());

			if (jsonArray.length() <= 0) {
				return Response.status(Status.BAD_REQUEST).entity("See status code.").build();
			}

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);

				if (jsonObject == null || jsonObject.getString("idLoggedUser").isEmpty()
						|| jsonObject.getString("accessDate").isEmpty() || jsonObject.getString("status").isEmpty()) {
					continue;
				}

				if (user == null) {
					user = (UsuarioEntity) getEjb("BaseEJB").recuperaObjeto(UsuarioEntity.class,
							Long.parseLong(jsonObject.getString("idLoggedUser")));
				}

				Long idPedestre = null;
				if (!jsonObject.isNull("idPedestrian") && !jsonObject.getString("idPedestrian").isEmpty()) {

					idPedestre = Long.parseLong(jsonObject.getString("idPedestrian"));
				}

				AcessoEntity log = new AcessoEntity(user, idPedestre, new Date(jsonObject.getLong("accessDate")),
						jsonObject.getString("status"), getLocation(jsonObject), getReason(jsonObject),
						jsonObject.getString("direction"), jsonObject.getString("equipament"),
						jsonObject.getBoolean("bloquearSaida"));
				try {
					String cartaoAcessoRecebido = jsonObject.getString("cartaoAcessoRecebido");
					if (cartaoAcessoRecebido != null && !cartaoAcessoRecebido.isEmpty()) {
						log.setCartaoAcessoRecebido(cartaoAcessoRecebido);
					}
				} catch (Exception e) {
				}

				logs.add(log);
			}

			if (!logs.isEmpty()) {
				getEjb("BaseEJB").saveRegisterLogs(logs);
				getEjb("BaseEJB").enviaNotificacao(logs);
			}

		} catch (Exception e) {
			statusResponse = Status.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}

		return Response.status(statusResponse).entity("See status code.").build();
	}

	private String getLocation(JSONObject jsonObject) throws JSONException {
		try {
			return jsonObject.getString("location");
		} catch (Exception e) {
			return "--";
		}
	}

	private String getReason(JSONObject jsonObject) throws JSONException {
		try {
			return jsonObject.getString("reason");
		} catch (Exception e) {
			return null;
		}
	}

	@GET
	@Path("/getLastVersion")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLastVersionServices() {

		Status statusResponse = Status.NOT_FOUND;
		DesktopVersionEntity lastVersion = null;

		try {
			List<DesktopVersionEntity> list = (List<DesktopVersionEntity>) getEjb("BaseEJB").pesquisaArgFixosLimitado(
					DesktopVersionEntity.class, "findAll", new HashMap<String, Object>(), 0, 1);

			if (list != null && !list.isEmpty()) {
				lastVersion = list.get(0);
				statusResponse = Status.OK;
			}
		} catch (Exception e) {
			statusResponse = Status.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		return Response.status(statusResponse).entity(lastVersion).build();
	}

	@POST
	@Path("/uploadPhotos")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response uploadPhotos(String parans) {
		// mantido para manter compatibilidade com versoes anteriores
		return Response.status(Status.NOT_FOUND).entity("See status code.").build();
	}

	@POST
	@Path("/downloadBackedUpPhotos")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response downloadBackedUpPhotos(String parans) {
		// mantido para manter compatibilidade com versoes anteriores
		JsonObject response = new JsonObject();
		return Response.status(Status.NOT_FOUND).entity(response.toString()).build();
	}

	@POST
	@Path("/deleteFacialPhotos")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deletePhotos(String parans) {
		// mantido para manter compatibilidade com versoes anteriores
		return Response.status(Status.NOT_FOUND).entity("See status code.").build();
	}

	@POST
	@Path("/saveBiometry")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveBiometry(String parans) {

		Status statusResponse = Status.OK;

		// System.out.println("BIOMETRIA: " + parans);

		try {
			JSONArray jsonArray = new JSONArray(parans);
			if (jsonArray.length() > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					if (jsonObject != null && jsonObject.getLong("idUser") != 0
							&& !jsonObject.getString("finger").isEmpty()
							&& !jsonObject.getString("template").isEmpty()) {

						// procura o usuário da biometria
						PedestreEntity user = (PedestreEntity) getEjb("BaseEJB").recuperaObjeto(PedestreEntity.class,
								jsonObject.getLong("idUser"));
						if (user != null) {

							if (user.getBiometrias() == null)
								user.setBiometrias(new ArrayList<BiometriaEntity>());

							// boolean fingerAlreadyCollected = false;
							Dedo finger = Dedo.valueFromImport(jsonObject.getString("finger"));
							byte[] template = Base64.decodeBase64(jsonObject.getString("template"));
							byte[] sample = Base64.decodeBase64(
									jsonObject.isNull("sample") ? "null" : jsonObject.getString("sample"));

							BiometriaEntity b = (BiometriaEntity) getEjb("BaseEJB")
									.gravaObjeto(new BiometriaEntity(user, finger, template, sample))[0];

							// caso sucesso, atualiza data de alteração do pedestre/visitante
							try {
								getEjb("BaseEJB").updateDataAlteracao(PedestreEntity.class, new Date(), user.getId());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			} else {
				statusResponse = Status.BAD_REQUEST;
			}
		} catch (Exception e) {
			statusResponse = Status.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		return Response.status(statusResponse).entity("See status code.").build();
	}

	@POST
	@Path("/uploadVisitantes")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response uploadVisitantes(String parans) {
		Status statusResponse = Status.OK;

		JSONArray jsonArray = new JSONArray(parans);
		if (jsonArray.length() <= 0) {
			return Response.status(Status.BAD_REQUEST).entity("See status code.").build();
		}

		try {
			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject jsonObject = jsonArray.getJSONObject(i);
				PedestreEntity visitante = new PedestreEntity();
				try {
					if (!jsonObject.getString("id").isEmpty()) {
						Long idVisitante = Long.parseLong(jsonObject.getString("id"));

						visitante = buscaVisitantePeloId(idVisitante);

						setNovosDadosVisitante(jsonObject, visitante);

						if (TipoPedestre.VISITANTE.equals(visitante.getTipo())) {
							alteraCreditosPedestreRegra(visitante, jsonObject);
						}

						getEjb("BaseEJB").alteraObjeto(visitante);

						visitante = (PedestreEntity) getEjb("PedestreEJB").recuperaObjeto(PedestreEntity.class,
								"findByIdComplete", idVisitante);

					} else {
						Long idCliente = Long.parseLong(jsonObject.getString("idCliente"));
						PedestreEntity pedestreExistente = null;

						// tenta buscar por visitante já existente
						try {
							Long idTempVisitante = Long.valueOf(jsonObject.getString("idTemp"));
							pedestreExistente = buscaPedestrePorIdTemp(idTempVisitante, idCliente);
						} catch (Exception e) {
						}

						if (pedestreExistente != null) {

							visitante = pedestreExistente;

						} else {
							visitante.setCliente(buscaClientePeloId(idCliente));

							setNovosDadosVisitante(jsonObject, visitante);

							if (TipoPedestre.VISITANTE.equals(visitante.getTipo())
									&& (visitante.getRegras() == null || visitante.getRegras().isEmpty())) {
								adicionaRegraParaVisitante(visitante, jsonObject);
							}

							visitante = (PedestreEntity) getEjb("BaseEJB").gravaObjeto(visitante)[0];

						}

					}

					gravaAcessos(visitante, jsonObject);
					gravaBiometrias(visitante, jsonObject);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			statusResponse = Status.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}

		return Response.status(statusResponse).entity("See status code.").build();
	}

	private PedestreEntity buscaPedestrePorIdTemp(Long idTemp, Long idCliente) {
		if (idTemp == null || idCliente == null)
			return null;

		Map<String, Object> args = new HashMap<>();
		args.put("ID_TEMP", idTemp);
		args.put("ID_CLIENTE", idCliente);

		List<PedestreEntity> visitantes = null;

		try {
			visitantes = (List<PedestreEntity>) getEjb("PedestreEJB").pesquisaArgFixos(PedestreEntity.class,
					"findByIdTemp", args);
			if (visitantes != null)
				return visitantes.stream().findFirst().orElse(null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private PedestreEntity buscaVisitantePeloId(Long idVisitante) {
		Map<String, Object> args = new HashMap<>();
		args.put("ID", idVisitante);

		List<PedestreEntity> visitantes = null;

		try {
			visitantes = (List<PedestreEntity>) getEjb("PedestreEJB").pesquisaArgFixos(PedestreEntity.class,
					"findByIdComplete", args);
			if (visitantes != null)
				return visitantes.stream().findFirst().orElse(null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private void adicionaRegraParaVisitante(PedestreEntity visitante, JSONObject jsonObject) throws Exception {
		Map<String, Object> arg = new HashMap<>();
		arg.put("NOME_REGRA", "ACESSO_UNICO_VISITANTE");
		arg.put("ID_CLIENTE", visitante.getCliente().getId());

		List<RegraEntity> listaRegras = (List<RegraEntity>) getEjb("BaseEJB")
				.pesquisaArgFixosLimitado(RegraEntity.class, "findByNome", arg, 0, 1);
		RegraEntity regraVisitante;

		if (listaRegras != null && !listaRegras.isEmpty()) {
			regraVisitante = listaRegras.get(0);

		} else {
			regraVisitante = new RegraEntity();
			regraVisitante.setNome("ACESSO_UNICO_VISITANTE");
			regraVisitante.setStatus(br.com.startjob.acesso.modelo.enumeration.Status.ATIVO);
			regraVisitante.setTipo(TipoRegra.ACESSO_UNICO);
			regraVisitante.setTipoPedestre(TipoPedestre.VISITANTE);
			regraVisitante.setCliente(visitante.getCliente());
			regraVisitante = (RegraEntity) getEjb("BaseEJB").gravaObjeto(regraVisitante)[0];
		}

		PedestreRegraEntity pedestreRegra = new PedestreRegraEntity();
		pedestreRegra.setRegra(regraVisitante);
		pedestreRegra.setQtdeDeCreditos(Long.parseLong(jsonObject.getString("qtdeCreditos")));
		pedestreRegra.setQtdeTotalDeCreditos(Long.parseLong(jsonObject.getString("qtdeCreditos")));
		pedestreRegra.setPedestre(visitante);

		visitante.setRegras(new ArrayList<>());
		visitante.getRegras().add(pedestreRegra);
	}

	private void alteraCreditosPedestreRegra(PedestreEntity visitante, JSONObject jsonObject) {
		if (visitante.getRegras() == null || visitante.getRegras().isEmpty())
			return;

		try {
			String idRegraStr = jsonObject.getString("idRegra");

			if (idRegraStr == null || idRegraStr.isEmpty()) {
				return;
			}

			Long idRegra = Long.parseLong(idRegraStr);
			for (PedestreRegraEntity pedestreRegra : visitante.getRegras()) {
				if (pedestreRegra.getId().equals(idRegra)) {
					pedestreRegra.setQtdeDeCreditos(Long.parseLong(jsonObject.getString("qtdeCreditos")));
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Visitante não possui regras.");
		}
	}

	@GET
	@Path("/deleteBiometry")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteBiometry(@QueryParam("idPedestrian") Long idPedestrian) {

		Status statusResponse = Status.NOT_FOUND;

		try {
			Map<String, Object> arg = new HashMap<String, Object>();
			arg.put("ID_PEDESTRE", idPedestrian);
			List<BiometriaEntity> biometrias = (List<BiometriaEntity>) getEjb("BaseEJB")
					.pesquisaArgFixos(BiometriaEntity.class, "findByUserId", arg);

			if (biometrias != null && !biometrias.isEmpty()) {
				for (BiometriaEntity biometria : biometrias) {
					getEjb("BaseEJB").excluiObjeto(biometria);
				}
			}

			// caso sucesso, atualiza data de alteração do pedestre/visitante
			try {
				getEjb("BaseEJB").updateDataAlteracao(PedestreEntity.class, new Date(), idPedestrian);
			} catch (Exception e) {
				e.printStackTrace();
			}

			statusResponse = Status.OK;

		} catch (Exception e) {
			statusResponse = Status.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		return Response.status(statusResponse).build();
	}

	@POST
	@Path("/saveBackup")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveBackup(@QueryParam("client") Long idClient, String parans) {

		Status statusResponse = Status.OK;

		try {
			BaseEJBRemote baseEJB = getEjb("BaseEJB");

			Map<String, Object> args = new HashMap<String, Object>();
			args.put("ID_CLIENTE", idClient);
			List<ClienteEntity> lista = (List<ClienteEntity>) baseEJB.pesquisaArgFixos(ClienteEntity.class,
					"findByIdComConfiguracoes", args);

			if (lista != null && !lista.isEmpty()) {

				// monta de grava configurações
				ClienteEntity cliente = lista.get(0);
				JSONObject jsonObject = new JSONObject(parans);
				if (cliente.getConfiguracoesDesktop() == null)
					cliente.setConfiguracoesDesktop(new ConfiguracoesDesktopEntity());
				cliente.getConfiguracoesDesktop().setBackupPreferences(jsonObject.get("backupPreferences").toString());
				cliente.getConfiguracoesDesktop().setBackupDevices(jsonObject.get("backupDevices").toString());
				cliente = (ClienteEntity) baseEJB.alteraObjeto(cliente)[0];

				// monta e grava equipamentos
				if (cliente.getConfiguracoesDesktop().getBackupDevices() != null) {

					args = new HashMap<String, Object>();
					args.put("cliente.id", idClient);
					List<EquipamentoEntity> equipamentos = (List<EquipamentoEntity>) baseEJB
							.pesquisaSimples(EquipamentoEntity.class, "findAll", args);

					List<DeviceTO> devices = getGSonConverter().fromJson(
							cliente.getConfiguracoesDesktop().getBackupDevices(), new TypeToken<List<DeviceTO>>() {
							}.getType());

					if (equipamentos != null && !equipamentos.isEmpty()) {

						// novos e edição
						for (DeviceTO d : devices) {
							EquipamentoEntity eq = null;
							for (EquipamentoEntity e : equipamentos) {
								if (e.getIdentificador().equals(d.getIdentifier())) {
									// encontrou
									eq = e;
									break;
								}
							}
							if (eq != null) {
								// edita
								eq.setCliente(cliente);
								eq.setIdentificador(d.getIdentifier());
								eq.setMarca(d.getManufacturer());
								eq.setNome(d.getName());
								eq.setLocal(d.getLocation());
								baseEJB.alteraObjeto(eq);
							} else {
								// cria
								eq = new EquipamentoEntity();
								eq.setCliente(cliente);
								eq.setIdentificador(d.getIdentifier());
								eq.setMarca(d.getManufacturer());
								eq.setNome(d.getName());
								eq.setLocal(d.getLocation());
								baseEJB.gravaObjeto(eq);
							}
						}
						// atualiza para excluir
						equipamentos = (List<EquipamentoEntity>) baseEJB.pesquisaSimples(EquipamentoEntity.class,
								"findAll", args);

						for (EquipamentoEntity e : equipamentos) {
							boolean encontrou = false;
							for (DeviceTO d : devices) {
								if (e.getIdentificador().equals(d.getIdentifier())) {
									encontrou = true;
									break;
								}
							}
							if (!encontrou) {
								e.setRemovido(true);
								e.setDataRemovido(new Date());
								baseEJB.gravaObjeto(e);
							}
						}
					} else {
						// cria novos equipamentos
						for (DeviceTO d : devices) {
							EquipamentoEntity e = new EquipamentoEntity();
							e.setCliente(cliente);
							e.setIdentificador(d.getIdentifier());
							e.setMarca(d.getManufacturer());
							e.setNome(d.getName());
							e.setLocal(d.getLocation());

							baseEJB.gravaObjeto(e);
						}
					}
				}
			}
		} catch (Exception e) {
			statusResponse = Status.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		return Response.status(statusResponse).entity("See status code.").build();
	}

	@GET
	@Path("/getBackup")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBackup(@QueryParam("client") Long idClient) {

		Status statusResponse = Status.NOT_FOUND;
		String retorno = null;

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("ID_CLIENTE", idClient);
			List<ClienteEntity> lista = (List<ClienteEntity>) getEjb("BaseEJB").pesquisaArgFixos(ClienteEntity.class,
					"findByIdComConfiguracoes", args);

			if (lista != null && !lista.isEmpty()) {
				ClienteEntity cliente = lista.get(0);
				if (cliente.getConfiguracoesDesktop() != null) {
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("backupPreferences",
							cliente.getConfiguracoesDesktop().getBackupPreferences());
					jsonObject.addProperty("backupDevices", cliente.getConfiguracoesDesktop().getBackupDevices());
					retorno = jsonObject.toString();
					statusResponse = Status.OK;
				}
			}

		} catch (Exception e) {
			statusResponse = Status.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}

		return Response.status(statusResponse).entity(retorno).build();
	}

	@POST
	@Path("/saveBackupByUser")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveBackuByUser(@QueryParam("idUser") Long idUser, String parans) {

		Status statusResponse = Status.OK;

		try {
			BaseEJBRemote baseEJB = getEjb("BaseEJB");

			Map<String, Object> args = new HashMap<String, Object>();
			args.put("ID_USER", idUser);
			List<UsuarioEntity> lista = (List<UsuarioEntity>) baseEJB.pesquisaArgFixos(UsuarioEntity.class,
					"findByIdComConfiguracoes", args);

			if (Objects.isNull(lista) || lista.isEmpty()) {
				return Response.status(statusResponse).entity("See status code.").build();
			}

			// monta de grava configurações
			UsuarioEntity usuario = lista.get(0);
			JSONObject jsonObject = new JSONObject(parans);
			if (usuario.getConfiguracoesDesktop() == null) {
				usuario.setConfiguracoesDesktop(new ConfiguracoesDesktopEntity());
			}

			if (!"".equals(jsonObject.get("backupPreferences").toString())) {
				usuario.getConfiguracoesDesktop().setBackupPreferences(jsonObject.get("backupPreferences").toString());
			}

			if (!"".equals(jsonObject.get("backupDevices").toString())) {
				usuario.getConfiguracoesDesktop().setBackupDevices(jsonObject.get("backupDevices").toString());
			}

			usuario = (UsuarioEntity) baseEJB.alteraObjeto(usuario)[0];

			if (usuario.getConfiguracoesDesktop().getBackupDevices() == null) {
				return Response.status(Status.OK).entity("See status code.").build();
			}

			args = new HashMap<String, Object>();
			args.put("cliente.id", usuario.getCliente().getId());
			args.put("usuario.id", usuario.getId());
			List<EquipamentoEntity> equipamentos = (List<EquipamentoEntity>) baseEJB
					.pesquisaSimples(EquipamentoEntity.class, "findAll", args);

			List<DeviceTO> devices = getGSonConverter().fromJson(usuario.getConfiguracoesDesktop().getBackupDevices(),
					new TypeToken<List<DeviceTO>>() {
					}.getType());

			if (devices == null || devices.isEmpty()) {
				return Response.status(statusResponse).entity("See status code.").build();
			}

			if (equipamentos != null && !equipamentos.isEmpty()) {
				for (DeviceTO d : devices) {
					EquipamentoEntity eq = null;
					for (EquipamentoEntity e : equipamentos) {
						if (e.getIdentificador().equals(d.getIdentifier())) {
							eq = e;
							break;
						}
					}

					if (eq != null) {
						eq.setCliente(usuario.getCliente());
						eq.setUsuario(usuario);
						eq.setIdentificador(d.getIdentifier());
						eq.setMarca(d.getManufacturer());
						eq.setNome(d.getName());
						eq.setLocal(d.getLocation());

						try {
							baseEJB.alteraObjeto(eq);

						} catch (Exception e) {
						}

					} else {
						eq = new EquipamentoEntity();
						eq.setCliente(usuario.getCliente());
						eq.setUsuario(usuario);
						eq.setIdentificador(d.getIdentifier());
						eq.setMarca(d.getManufacturer());
						eq.setNome(d.getName());
						eq.setLocal(d.getLocation());
						baseEJB.gravaObjeto(eq);
					}
				}

				equipamentos = (List<EquipamentoEntity>) baseEJB.pesquisaSimples(EquipamentoEntity.class, "findAll",
						args);

				for (EquipamentoEntity e : equipamentos) {
					boolean encontrou = false;
					for (DeviceTO d : devices) {
						if (e.getIdentificador().equals(d.getIdentifier())) {
							encontrou = true;
							break;
						}
					}

					if (!encontrou) {
						e.setRemovido(true);
						e.setDataRemovido(new Date());
						baseEJB.gravaObjeto(e);
					}
				}
			} else {
				for (DeviceTO d : devices) {
					EquipamentoEntity e = new EquipamentoEntity();
					e.setCliente(usuario.getCliente());
					e.setUsuario(usuario);
					e.setIdentificador(d.getIdentifier());
					e.setMarca(d.getManufacturer());
					e.setNome(d.getName());
					e.setLocal(d.getLocation());

					baseEJB.gravaObjeto(e);
				}
			}

		} catch (Exception e) {
			statusResponse = Status.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		return Response.status(statusResponse).entity("See status code.").build();
	}

	@GET
	@Path("/getBackupByUser")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBackupByUser(@QueryParam("idUser") Long idUser) {

		Status statusResponse = Status.NOT_FOUND;
		String retorno = null;

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("ID_USER", idUser);
			List<UsuarioEntity> lista = (List<UsuarioEntity>) getEjb("BaseEJB").pesquisaArgFixos(UsuarioEntity.class,
					"findByIdComConfiguracoes", args);

			if (lista != null && !lista.isEmpty()) {
				UsuarioEntity usuario = lista.get(0);
				if (usuario.getConfiguracoesDesktop() != null) {
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("backupPreferences",
							usuario.getConfiguracoesDesktop().getBackupPreferences());
					jsonObject.addProperty("backupDevices", usuario.getConfiguracoesDesktop().getBackupDevices());
					retorno = jsonObject.toString();
					statusResponse = Status.OK;
				}
			}

		} catch (Exception e) {
			statusResponse = Status.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}

		return Response.status(statusResponse).entity(retorno).build();
	}

	private ClienteEntity buscaClientePeloId(Long idCliente) {
		ClienteEntity cliente = null;

		try {
			cliente = (ClienteEntity) getEjb("BaseEJB").recuperaObjeto(ClienteEntity.class, idCliente);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cliente;
	}

	private void setNovosDadosVisitante(JSONObject jsonObject, PedestreEntity visitante) {
		try {
			visitante.setIdTemp(Long.valueOf(jsonObject.getString("idTemp")));
		} catch (Exception e) {
		}

		// Dados basicos
		visitante.setNome(jsonObject.getString("nome"));
		try {
			visitante.setDataNascimento(sdf.parse(jsonObject.getString("dataNascimento")));
		} catch (Exception e) {
		}

		visitante.setEmail(jsonObject.getString("email"));
		visitante.setCpf(jsonObject.getString("cpf"));
		try {
			visitante.setGenero(Genero.valueOf(jsonObject.getString("genero")));
		} catch (Exception e) {
			// se não tiver o campo, não faz nada
		}
		visitante.setRg(jsonObject.getString("rg"));
		visitante.setTelefone(jsonObject.getString("telefone"));
		visitante.setCelular(jsonObject.getString("celular"));
		visitante.setObservacoes(jsonObject.getString("observacoes"));

		// Dados da empresa
		if (jsonObject.getString("idEmpresa") != null && !jsonObject.getString("idEmpresa").isEmpty())
			visitante.setEmpresa(
					(EmpresaEntity) buscaPeloId(EmpresaEntity.class, Long.valueOf(jsonObject.getString("idEmpresa"))));

		if (jsonObject.getString("idDepartamento") != null && !jsonObject.getString("idDepartamento").isEmpty())
			visitante.setDepartamento((DepartamentoEntity) buscaPeloId(DepartamentoEntity.class,
					Long.valueOf(jsonObject.getString("idDepartamento"))));

		if (jsonObject.getString("idCentroCusto") != null && !jsonObject.getString("idCentroCusto").isEmpty())
			visitante.setCentroCusto((CentroCustoEntity) buscaPeloId(CentroCustoEntity.class,
					Long.valueOf(jsonObject.getString("idCentroCusto"))));

		if (jsonObject.getString("idCargo") != null && !jsonObject.getString("idCargo").isEmpty())
			visitante.setCargo(
					(CargoEntity) buscaPeloId(CargoEntity.class, Long.valueOf(jsonObject.getString("idCargo"))));

		try {
			if (jsonObject.getString("idUsuario") != null && !jsonObject.getString("idUsuario").isEmpty())
				visitante.setUsuario((UsuarioEntity) buscaPeloId(UsuarioEntity.class,
						Long.valueOf(jsonObject.getString("idUsuario"))));
		} catch (Exception e) {
		}

		// Itens aba lateral
		if (!jsonObject.getString("foto").isEmpty()) {
			visitante.setFoto(Base64.decodeBase64(jsonObject.getString("foto")));
		}

		visitante.setTipo(TipoPedestre.valueOf(jsonObject.getString("tipo")));
		visitante.setStatus(br.com.startjob.acesso.modelo.enumeration.Status.valueOf(jsonObject.getString("status")));
		visitante.setMatricula(jsonObject.getString("matricula"));
		visitante.setCodigoCartaoAcesso(jsonObject.getString("numeroCartao"));
		visitante.setSempreLiberado(Boolean.valueOf(jsonObject.getString("sempreLiberado")));
		visitante.setHabilitarTeclado(Boolean.valueOf(jsonObject.getString("habilitarTeclado")));
		visitante.setEnviaSmsAoPassarNaCatraca(Boolean.valueOf(jsonObject.getString("enviaSmsAoPassarNaCatraca")));

		try {
			visitante.setLuxandIdentifier(jsonObject.getString("luxandIdentifier"));
		} catch (Exception e) {
			/* se não tiver o campo, não faz nada */ }

		try {
			visitante.setQtdAcessoAntesSinc(Integer.valueOf(jsonObject.getString("qtdeAcessosAntesSinc")));
		} catch (Exception e) {
		}

		// Endereco
		if (!jsonObject.getString("cep").isEmpty()) {
			if (visitante.getEndereco() == null) {
				visitante.setEndereco(new EnderecoEntity());
			}
			visitante.getEndereco().setCep(jsonObject.getString("cep"));
			visitante.getEndereco().setLogradouro(jsonObject.getString("logradouro"));
			visitante.getEndereco().setNumero(jsonObject.getString("numero"));
			visitante.getEndereco().setComplemento(jsonObject.getString("complemento"));
			visitante.getEndereco().setBairro(jsonObject.getString("bairro"));
			visitante.getEndereco().setCidade(jsonObject.getString("cidade"));
			visitante.getEndereco().setEstado(jsonObject.getString("estado"));

		} else {
			visitante.setEndereco(null);
		}

		try {
			visitante.setLogin(jsonObject.getString("login"));
			if ("".equals(visitante.getLogin()))
				visitante.setLogin(null);
		} catch (Exception e) {
		}
		try {
			visitante.setSenha(jsonObject.getString("senha"));
			if ("".equals(visitante.getSenha())) {
				visitante.setSenha(null);
				visitante.setSenhaLivre(null);
			}
		} catch (Exception e) {
		}
		try {
			visitante.setTipoAcesso(jsonObject.getString("tipoAcesso"));
			if ("".equals(visitante.getTipoAcesso()))
				visitante.setTipoAcesso(null);
		} catch (Exception e) {
		}
		try {
			visitante.setTipoQRCode(TipoQRCode.valueOf(jsonObject.getString("tipoQRCode")));
		} catch (Exception e) {
		}
		try {
			visitante.setQrCodeParaAcesso(jsonObject.getString("qrCodeParaAcesso"));
			if ("".equals(visitante.getQrCodeParaAcesso()))
				visitante.setQrCodeParaAcesso(null);
		} catch (Exception e) {
		}

		try {
			visitante.setDataCadastroFotoNaHikivision(sdf.parse(jsonObject.getString("dataCadastroFotoNaHikivision")));
		} catch (Exception e) {
		}

		setRegrasVisitante(visitante, jsonObject.getJSONArray("pedestresRegras"));

		setDocumentosVisitante(visitante, jsonObject.getJSONArray("documentos"));

		setMensagensVisitante(visitante, jsonObject.getJSONArray("mensagens"));

		setListaEquipamentosVisitante(visitante, jsonObject.getJSONArray("equipamentos"));
	}

	private void setRegrasVisitante(PedestreEntity visitante, JSONArray regrasArray) {
		List<PedestreRegraEntity> pedestreRegras = new ArrayList<>();

		for (int i = 0; i < regrasArray.length(); i++) {
			JSONObject regrasObject = regrasArray.getJSONObject(i);

			Boolean removido = Boolean.valueOf(regrasObject.getString("removido"));

			if (removido) {
				Long idPedestreRegra = Long.valueOf(regrasObject.getString("idPedestreRegra"));
				removePedestreRegra(idPedestreRegra, visitante.getRegras());
				continue;
			}

			PedestreRegraEntity pedestreRegra = new PedestreRegraEntity();
			pedestreRegra.setPedestre(visitante);

			try {
				Long idRegra = Long.valueOf(regrasObject.getString("idRegraPR"));
				pedestreRegra.setRegra((RegraEntity) buscaPeloId(RegraEntity.class, idRegra));
			} catch (Exception e) {
			}

			try {
				pedestreRegra.setValidade(sdf.parse(regrasObject.getString("validadeRegraPR")));
			} catch (Exception e) {
			}

			pedestreRegra.setQtdeTotalDeCreditos(!regrasObject.getString("qtdeTotalDeCreditosPR").isEmpty()
					? Long.valueOf(regrasObject.getString("qtdeTotalDeCreditosPR"))
					: null);
			pedestreRegra.setQtdeDeCreditos(!regrasObject.getString("qtdeDeCreditosPR").isEmpty()
					? Long.valueOf(regrasObject.getString("qtdeDeCreditosPR"))
					: null);
			pedestreRegra.setDiasValidadeCredito(!regrasObject.getString("diasValidadeCreditoPR").isEmpty()
					? Long.valueOf(regrasObject.getString("diasValidadeCreditoPR"))
					: null);

			try {
				pedestreRegra.setDataInicioPeriodo(sdf.parse(regrasObject.getString("dataInicioPeriodoPR")));
			} catch (Exception e) {
			}

			try {
				pedestreRegra.setDataFimPeriodo(sdf.parse(regrasObject.getString("dataFimPeriodo")));
			} catch (Exception e) {
			}

			pedestreRegras.add(pedestreRegra);
		}

		if (!pedestreRegras.isEmpty()) {
			if (visitante.getRegras() == null)
				visitante.setRegras(new ArrayList<>());

			visitante.getRegras().addAll(pedestreRegras);
		}
	}

	private void setDocumentosVisitante(PedestreEntity visitante, JSONArray documentosArray) {
		List<DocumentoEntity> documentos = new ArrayList<>();

		for (int i = 0; i < documentosArray.length(); i++) {
			JSONObject documentoObject = documentosArray.getJSONObject(i);

			Boolean removido = Boolean.valueOf(documentoObject.getString("removido"));

			if (removido) {
				Long idDocumento = Long.valueOf(documentoObject.getString("idDocumento"));
				removeDocumento(idDocumento, visitante.getDocumentos());
				continue;
			}

			DocumentoEntity documento = new DocumentoEntity();
			documento.setPedestre(visitante);
			documento.setNome(documentoObject.getString("nomeDoc"));
			try {
				documento.setValidade(sdf.parse(documentoObject.getString("validadeDoc")));
			} catch (Exception e) {
			}

			documento.setArquivo(Base64.decodeBase64(documentoObject.getString("arquivoDoc")));

			documentos.add(documento);
		}

		if (!documentos.isEmpty()) {
			if (visitante.getDocumentos() == null)
				visitante.setDocumentos(new ArrayList<>());

			visitante.getDocumentos().addAll(documentos);
		}
	}

	private void setListaEquipamentosVisitante(PedestreEntity visitante, JSONArray equipamentosArray) {
		List<PedestreEquipamentoEntity> equipamentos = new ArrayList<>();

		for (int i = 0; i < equipamentosArray.length(); i++) {
			JSONObject equipamentoObject = equipamentosArray.getJSONObject(i);

			Boolean removido = Boolean.valueOf(equipamentoObject.getString("removido"));

			if (removido) {
				Long idEquipamento = Long.valueOf(equipamentoObject.getString("idEquipamento"));
				removeEquipamento(idEquipamento, visitante.getEquipamentos());
				continue;
			}

			PedestreEquipamentoEntity equipamento = new PedestreEquipamentoEntity();
			equipamento.setPedestre(visitante);
			try {
				equipamento.setValidade(sdf.parse(equipamentoObject.getString("validadeEquipamento")));

			} catch (Exception e) {
			}

			try {
				String idEquipamento = equipamentoObject.getString("idEquipamento");
				equipamento.setEquipamento(buscaEquipamentoPeloIdentificador(idEquipamento)); // buscar equipamento pelo
																								// idEquipamento

			} catch (Exception e) {
			}

			equipamentos.add(equipamento);
		}

		if (!equipamentos.isEmpty()) {
			if (visitante.getEquipamentos() == null)
				visitante.setEquipamentos(new ArrayList<>());

			visitante.getEquipamentos().addAll(equipamentos);
		}
	}

	private void setMensagensVisitante(PedestreEntity visitante, JSONArray mensagensArray) {
		List<MensagemEquipamentoEntity> mensagens = new ArrayList<>();

		for (int i = 0; i < mensagensArray.length(); i++) {
			JSONObject mensagemObject = mensagensArray.getJSONObject(i);

			Boolean removido = Boolean.valueOf(mensagemObject.getString("removido"));

			if (removido) {
				Long idMensagem = Long.valueOf(mensagemObject.getString("idMensagem"));
				removeMensagem(idMensagem, visitante.getMensagensPersonalizadas());
				continue;
			}

			MensagemEquipamentoEntity mensagem = new MensagemEquipamentoEntity();
			mensagem.setPedestre(visitante);
			mensagem.setNome(mensagemObject.getString("nomeMsg"));
			try {
				mensagem.setStatus(br.com.startjob.acesso.modelo.enumeration.Status
						.valueOf(mensagemObject.getString("statusMsg")));
			} catch (Exception e) {
			}
			mensagem.setMensagem(mensagemObject.getString("mensagemMsg"));
			mensagem.setQuantidade(mensagemObject.getString("quantidadeMsg").isEmpty() ? null
					: Long.valueOf(mensagemObject.getString("quantidadeMsg")));
			try {
				mensagem.setValidade(sdf.parse(mensagemObject.getString("validadeMsg")));
			} catch (Exception e) {
			}

			mensagens.add(mensagem);
		}

		if (!mensagens.isEmpty()) {
			if (visitante.getMensagensPersonalizadas() == null)
				visitante.setMensagensPersonalizadas(new ArrayList<>());

			visitante.getMensagensPersonalizadas().addAll(mensagens);
		}
	}

	private void removePedestreRegra(Long idPedestreRegra, List<PedestreRegraEntity> pedestreRegras) {
		if (pedestreRegras == null || pedestreRegras.isEmpty())
			return;

		for (PedestreRegraEntity pr : pedestreRegras) {
			if (idPedestreRegra.equals(pr.getId())) {
				pr.setRemovido(true);
				pr.setDataRemovido(new Date());
			}
		}
	}

	private void removeDocumento(Long idDocumento, List<DocumentoEntity> documentos) {
		if (documentos == null || documentos.isEmpty())
			return;

		for (DocumentoEntity doc : documentos) {
			if (idDocumento.equals(doc.getId())) {
				documentos.remove(doc);
				break;
			}
		}
	}

	private void removeEquipamento(Long idEquipamento, List<PedestreEquipamentoEntity> equipamentos) {
		if (equipamentos == null || equipamentos.isEmpty())
			return;

		for (PedestreEquipamentoEntity pe : equipamentos) {
			if (idEquipamento.equals(pe.getId())) {
				equipamentos.remove(pe);
				break;
			}
		}
	}

	private void removeMensagem(Long idMensagem, List<MensagemEquipamentoEntity> mensagensPersonalizadas) {
		if (mensagensPersonalizadas == null || mensagensPersonalizadas.isEmpty())
			return;

		for (MensagemEquipamentoEntity me : mensagensPersonalizadas) {
			if (idMensagem.equals(me.getId())) {
				mensagensPersonalizadas.remove(me);
				break;
			}
		}
	}

	private void gravaAcessos(PedestreEntity visitante, JSONObject jsonObject) {
		JSONArray acessosArray = jsonObject.getJSONArray("acessos");
//		try {
//		System.out.println("Acessos cadastro: " + visitante.getNome() + " - " + acessosArray.toString() );
//		}catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
		for (int i = 0; i < acessosArray.length(); i++) {
			JSONObject acessoObject = acessosArray.getJSONObject(i);

			AcessoEntity acesso = new AcessoEntity();
			acesso.setPedestre(visitante);
			acesso.setCliente(visitante.getCliente());
			try {
				acesso.setData(sdf.parse(acessoObject.getString("dataAcesso")));
			} catch (Exception e) {
				acesso.setData(new Date());
			}

			acesso.setSentido(acessoObject.getString("direcao"));
			acesso.setEquipamento(acessoObject.getString("equipamento"));
			acesso.setTipo(acessoObject.getString("statusAcesso"));
			acesso.setLocal(acessoObject.getString("localizacao"));
			acesso.setRazao(acessoObject.getString("razao"));
			try {
				String cartaoAcessoRecebido = jsonObject.getString("cartaoAcessoRecebido");
				if (cartaoAcessoRecebido != null && !cartaoAcessoRecebido.isEmpty())
					acesso.setCartaoAcessoRecebido(cartaoAcessoRecebido);
			} catch (Exception e) {
			}

			try {
				getEjb("BaseEJB").gravaObjeto(acesso);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void gravaBiometrias(PedestreEntity visitante, JSONObject jsonObject) {
		JSONArray biometriasArray = jsonObject.getJSONArray("biometrias");
		for (int i = 0; i < biometriasArray.length(); i++) {
			JSONObject biometriaObject = biometriasArray.getJSONObject(i);

			BiometriaEntity biometria = new BiometriaEntity();
			biometria.setPedestre(visitante);
			biometria.setDedo(Dedo.valueFromImport(biometriaObject.getString("finger")));

			biometria.setSample(Base64
					.decodeBase64(biometriaObject.isNull("sample") ? "null" : biometriaObject.getString("sample")));
			biometria.setTemplate(Base64.decodeBase64(biometriaObject.getString("template")));

			try {
				getEjb("BaseEJB").gravaObjeto(biometria);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private EquipamentoEntity buscaEquipamentoPeloIdentificador(String idEquipamento) {
		try {
			Map<String, Object> args = new HashMap<>();
			args.put("ID_EQUIPAMENTO", idEquipamento);

			List<EquipamentoEntity> equipamentos = (List<EquipamentoEntity>) getEjb("BaseEJB")
					.pesquisaArgFixosLimitado(EquipamentoEntity.class, "findByIdentificador", args, 0, 1);

			if (equipamentos != null)
				return equipamentos.stream().findFirst().orElse(null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	private BaseEntity buscaPeloId(Class classeEntidade, Object id) {
		BaseEntity entidade = null;

		try {
			entidade = getEjb("BaseEJB").recuperaObjeto(classeEntidade, id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return entidade;
	}

	@POST
	@Path("/uploadRegras")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateRegras(String parans) {
		JSONArray jsonArray = new JSONArray(parans);
		if (jsonArray.length() <= 0) {
			return Response.status(Status.BAD_REQUEST).entity("See status code.").build();
		}

		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				try {
					if (Objects.nonNull(jsonObject.getLong("id"))) {
						Long idRegra = jsonObject.getLong("id");

						final RegraEntity regra = buscaRegraPeloId(idRegra);
						regra.setIdPlano(jsonObject.getInt("idPlano"));
						regra.setIdTemplate(jsonObject.getInt("idTemplate"));

						getEjb("BaseEJB").alteraObjeto(regra);

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("See status code.").build();
		}

		return Response.status(Status.OK).entity("See status code.").build();
	}

	private RegraEntity buscaRegraPeloId(Long idRegra) {
		Map<String, Object> args = new HashMap<>();
		args.put("ID", idRegra);

		List<RegraEntity> regras = null;

		try {
			regras = (List<RegraEntity>) getEjb("BaseEJB").pesquisaArgFixos(RegraEntity.class, "findByIdComplete",
					args);
			;
			if (regras != null)
				return regras.stream().findFirst().orElse(null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
