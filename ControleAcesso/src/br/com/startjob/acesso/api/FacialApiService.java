package br.com.startjob.acesso.api;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.mail.Session;
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
import org.primefaces.shaded.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.entity.CadastroExternoEntity;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.enumeration.StatusCadastroExterno;
import br.com.startjob.acesso.modelo.utils.AppAmbienteUtils;
import br.com.startjob.acesso.modelo.utils.MailUtils;
import br.com.startjob.acesso.to.CadastroExternoProcessadoTO;
import br.com.startjob.acesso.to.CadastroExternoTO;
import br.com.startjob.acesso.to.ResultadoProcessamento;
import br.com.startjob.acesso.utils.ResourceBundleUtils;

@SuppressWarnings("unchecked")
@Path("/facial")
public class FacialApiService extends BaseService {

	/**
	 * Mail session.
	 */
	@Resource(mappedName="java:/mail/suporte")
	protected Session mailSession;
	
	@Context
	private HttpServletRequest request;
	
	@Context 
	private HttpServletResponse response;
	
	private static Gson gson = new GsonBuilder().create();;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:sss");
	
	private static final String FOLDER_NAME_BACKUP_FILE_TRACKER = "backupFileTracker/";
	private static final String FILE_NAME_BACKUP_TRACKER = "tracker.dat";
	
	private Long idCliente;
	
	@GET
	@Path("/requestBackupFileTracker")
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestBackupFileTracker(@QueryParam("client")Long idClient) {
		
		if(idClient == null)
			return Response.status(Status.NOT_FOUND).build();
		
		Status statusResponse = Status.NOT_FOUND;
		JsonObject response = new JsonObject();
		
		String saveFilePath = AppAmbienteUtils.getResourcesFolder()+ FOLDER_NAME_BACKUP_FILE_TRACKER 
					+ idClient + "/" + FILE_NAME_BACKUP_TRACKER;
		try {
			byte[] trackerFileByteArray = Files.readAllBytes(Paths.get(saveFilePath));
			response.addProperty("fileTracker", trackerFileByteArray != null ? 
						Base64.encodeBase64String(trackerFileByteArray) : "");
			
			statusResponse = Status.OK;
			
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Status.NOT_FOUND).build();
		}
		
		return Response.status(statusResponse).entity(response.toString()).build();
	}
	
	@POST
	@Path("/uploadFileTracker")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response uploadFileTracker(String parans) {
		Status statusResponse = Status.OK;
		
		JSONObject jsonObject = new JSONObject(parans);
		
		if(jsonObject.get("idCliente").toString().isEmpty()
				|| jsonObject.getString("fileTracker").isEmpty())
			return Response.status(Status.NOT_ACCEPTABLE).entity("See status code.").build();
		
		Long idCliente = jsonObject.getLong("idCliente");
		byte[] fileTracker = Base64.decodeBase64(jsonObject.getString("fileTracker"));
		
		try {
			salvarArquivoFileTracker(idCliente, fileTracker);

		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("See status code.").build();
		}
		
		return Response.status(statusResponse).entity("See status code.").build();
	}
	
	private void salvarArquivoFileTracker(Long idCliente, byte[] fileTracker) 
							throws IOException {
		String saveFilePath = AppAmbienteUtils.getResourcesFolder()+ FOLDER_NAME_BACKUP_FILE_TRACKER + idCliente;
		
		Files.createDirectories(Paths.get(saveFilePath));
		
		saveFilePath += "/" + FILE_NAME_BACKUP_TRACKER;
		
		try (FileOutputStream fos = new FileOutputStream(saveFilePath)) {
			fos.write(fileTracker);
		}
	}

	@GET
	@Path("/requestAllPedestresCadastrados")
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestAllUsers(@QueryParam("client")Long idClient) {

		if(idClient == null)
			return Response.status(Status.NOT_FOUND).build();

		Status statusResponse = Status.NOT_FOUND;
		List<CadastroExternoTO> cadastrosExternosListTO = new ArrayList<>();
		
		try {
			Map<String, Object> args = new HashMap<>();
			args.put("ID_CLIENTE", idClient);
			args.put("STATUS", StatusCadastroExterno.CADASTRADO);
			
			List<CadastroExternoEntity> cadastrosExternosList = (List<CadastroExternoEntity>) ((BaseEJBRemote) 
					getEjb("BaseEJB")).pesquisaArgFixos(CadastroExternoEntity.class, "findAllByIdClienteAndStatus", args);
			
			if(cadastrosExternosList == null || cadastrosExternosList.isEmpty())
				return Response.status(Status.NOT_FOUND).build();
			
			statusResponse = Status.OK;
			cadastrosExternosList.forEach(cadastro -> {
				cadastrosExternosListTO.add(new CadastroExternoTO(cadastro));
			});
		
		} catch(Exception e) {
			statusResponse = Status.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		
		return Response.status(statusResponse).entity(cadastrosExternosListTO).build();
	}
	
	@POST
	@Path("/registerAllCadastrosExternosProcessados")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerAllCadastrosExternosProcessados(String parans) {
		Status statusResponse = Status.OK;
		
		CadastroExternoProcessadoTO to = gson.fromJson(parans, CadastroExternoProcessadoTO.class);
		
		if(to == null)
			return Response.status(Status.NO_CONTENT).entity("See status code.").build();
		
		idCliente = to.getIdCliente();
		
		for(ResultadoProcessamento c : to.getResultados()) {
			List<CadastroExternoEntity> cadastros = buscaCadastrosParaProcessar(to.getIdCliente(), c.getIdCadastrado());
			
			if(cadastros == null || cadastros.isEmpty())
				continue;
			
			processaCadastros(c, cadastros.get(0));
		}
		
		return Response.status(statusResponse).entity("See status code.").build();
	}

	private void processaCadastros(ResultadoProcessamento c, CadastroExternoEntity cadastro) {
		
		if(c.getCodigoResultado() >= 0) {
			//sucesso
			cadastro.setStatusCadastroExterno(StatusCadastroExterno.PROCESSADO);
			cadastro.getPedestre().setLuxandIdentifier(c.getLuxandIdentifier().toString());

		} else {
			//erro
			cadastro.setStatusCadastroExterno(StatusCadastroExterno.ERRO);
			cadastro.setCodigoResultadoProcessamento(c.getCodigoResultado());
			cadastro.setDescricaoResultadoProcessamento(c.getDescricaoResultado());
		}
		
		try {
			
			BaseEJBRemote ejb = ((BaseEJBRemote) getEjb("BaseEJB"));
			
			ejb.alteraObjeto(cadastro);
			if(StatusCadastroExterno.PROCESSADO.equals(cadastro.getStatusCadastroExterno()))
				ejb.alteraObjeto(cadastro.getPedestre());
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		try {
			ClienteEntity cliente = buscaClientePeloId(idCliente);
			
			if(cliente == null)
				return;
			
			enviarEmailCadastroProcessado(cliente.getEmail(), cliente.getNome(), cadastro.getStatusCadastroExterno(),
											cadastro.getPedestre());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void enviarEmailCadastroProcessado(String email, String nomeCliente, StatusCadastroExterno statusCadastro,
			PedestreEntity pedestre) throws Exception {
		
		String subject = ResourceBundleUtils.getInstance()
				.recuperaChave("mail.title.cadastro.facial.externo.concluido", FacesContext.getCurrentInstance(), 
						new Object[]{});

		String nomePedestre = pedestre.getNome() != null ? pedestre.getNome() : "Sem nome";
		String cpf = pedestre.getCpf() != null ? pedestre.getCpf() : "Sem CPF";
		String rg = pedestre.getRg() != null ? pedestre.getRg() : "Sem RG";
		String dataFormatada = sdf.format(new Date());
		
		String msg = "";
		if(StatusCadastroExterno.PROCESSADO.equals(statusCadastro))
			msg = ResourceBundleUtils.getInstance()
					.recuperaChave("mail.msg.cadastro.facial.externo.concluido.sucesso", FacesContext.getCurrentInstance(),
							new Object[]{nomeCliente, nomePedestre, cpf, rg, dataFormatada});
		else
			msg = ResourceBundleUtils.getInstance()
					.recuperaChave("mail.msg.cadastro.facial.externo.concluido.erro", FacesContext.getCurrentInstance(),
							new Object[]{nomeCliente, nomePedestre, cpf, rg, dataFormatada});
		
		MailUtils.getInstance(mailSession).send(email, subject, msg, "");
	}
	
	private ClienteEntity buscaClientePeloId(Long idCliente) {
		ClienteEntity cliente = null;
		
		try {
			cliente = (ClienteEntity) ((BaseEJBRemote) getEjb("BaseEJB")).recuperaObjeto(ClienteEntity.class, idCliente);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return cliente;
	}

	private List<CadastroExternoEntity> buscaCadastrosParaProcessar(Long idCliente, Long idProcessados) {
		Map<String, Object> args = new HashMap<>();
		args.put("id", idProcessados);
		args.put("cliente.id", idCliente);
		args.put("statusCadastroExterno", StatusCadastroExterno.CADASTRADO);
		
		List<CadastroExternoEntity> cadastrosParaProcessar = null;
		
		try {
			cadastrosParaProcessar = (List<CadastroExternoEntity>) ((BaseEJBRemote) 
						getEjb("BaseEJB")).pesquisaSimples(CadastroExternoEntity.class, "findAllComplete", args);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return cadastrosParaProcessar;
	}
}
