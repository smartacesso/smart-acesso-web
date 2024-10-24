package br.com.startjob.acesso.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
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

import com.google.gson.Gson;
import com.itextpdf.text.pdf.codec.Base64;

import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.ejb.LoginEJBRemote;
import br.com.startjob.acesso.modelo.entity.CadastroExternoEntity;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.enumeration.StatusCadastroExterno;
import br.com.startjob.acesso.modelo.utils.EncryptionUtils;
import br.com.startjob.acesso.to.CadastroExternoTO;
import br.com.startjob.acesso.to.RequestFacialServiceTO;
import br.com.startjob.acesso.to.ResponseServiceTO;
import br.com.startjob.acesso.utils.ResourceBundleUtils;

@Path("/pedestre")
public class PedestreApiService extends BaseService {
	
	@Context
	private HttpServletRequest request;
	
	@Context 
	private HttpServletResponse response;
	
	@GET
	@Path("/action")
	@Produces(MediaType.TEXT_PLAIN)
	public Response action(){
		
		return Response.status(Status.OK).entity("working").build();
	}
	
	/**
	 * Reliza login no sistema.
	 * 
	 * @param email - e-mail para login
	 * @param passwd - senha utilizada
	 * @return
	 */
	@GET
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@QueryParam("unidadeName")String unidade, 
						  @QueryParam("login")String login, 
						  @QueryParam("passwd")String passwd) {
		
		try {
			//recupera ejb e valida usuário
			LoginEJBRemote loginEJB = (LoginEJBRemote) getEjb("LoginEJB");
			PedestreEntity usuario = loginEJB.validaPedestre(unidade, login, EncryptionUtils.encrypt(passwd),
								BaseConstant.ACCESS_TYPES.API, getDeviceType(request));
			
			//prepara retorno
			if(usuario != null){
				
				//gerar token de acesso
				usuario.setToken(new Date().getTime()+"-"+
								usuario.getId()+"-"+usuario.getSenha());
				usuario.setTempoRenovacaoQRCode(getTempoRenovacaoQRCode(usuario.getCliente().getId()));
				
				preenchePedestre(usuario);
				
				return Response.status(Status.OK).entity(
						new ResponseServiceTO(Status.OK.toString(), null, usuario)).build();
			}
			
			return Response.status(Status.UNAUTHORIZED).entity(
					new ResponseServiceTO(Status.UNAUTHORIZED.toString(), "Usuário não autorizado!",null)).build();
			
		} catch (Exception e) {
			//caso aconteça algum erro de validação
			//imprimi o erro e retorna nulo.
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new ResponseServiceTO(Status.INTERNAL_SERVER_ERROR.toString(), ResourceBundleUtils.getInstance()
							.recuperaChave(e.getMessage(),FacesContext.getCurrentInstance()),null)).build();
		}
	}
	
	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPedestre(@QueryParam("token")String token, @QueryParam("lastSync")Long lastSync) {
		
		try {
			//recupera ejb e valida usuário
			PedestreEntity usuario = validaTokenPedestre(token);

			//prepara retorno
			if(usuario != null){
				
				if(usuario.getDataAlteracao().getTime() < lastSync)
					return Response.status(Status.NOT_MODIFIED).entity(
							new ResponseServiceTO(Status.NOT_MODIFIED.toString(), "Pedestre não alterado!", null)).build();
				
				//gerar token de acesso
				usuario.setToken(new Date().getTime()+"-"+
								usuario.getId()+"-"+usuario.getSenha());
				usuario.setTempoRenovacaoQRCode(getTempoRenovacaoQRCode(usuario.getCliente().getId()));
				
				preenchePedestre(usuario);
				
				return Response.status(Status.OK).entity(
						new ResponseServiceTO(Status.OK.toString(), null, usuario)).build();
			}
			
			return Response.status(Status.UNAUTHORIZED).entity(
					new ResponseServiceTO(Status.UNAUTHORIZED.toString(), "Pedestre não autorizado!",null)).build();
			
		} catch (Exception e) {
			//caso aconteça algum erro de validação
			//imprimi o erro e retorna nulo.
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new ResponseServiceTO(Status.INTERNAL_SERVER_ERROR.toString(), ResourceBundleUtils.getInstance()
							.recuperaChave(e.getMessage(),FacesContext.getCurrentInstance()),null)).build();
		}
		
	}
	
	@POST
	@Path("/registra/facial")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registraFacial(String parans){
		Status statusResponse = Status.OK;
		
		try {
			
			BaseEJBRemote ejb = (BaseEJBRemote) getEjb("BaseEJB");
			
			//registra
			Gson gson = getGSonConverter();
			RequestFacialServiceTO to = gson.fromJson(parans, RequestFacialServiceTO.class);
			if(to != null) {
				PedestreEntity pedestre = validaTokenPedestre(to.getToken().toString());
				
				CadastroExternoEntity recebido = to.getObject();
				CadastroExternoEntity original = (CadastroExternoEntity)
						ejb.recuperaObjeto(CadastroExternoEntity.class, "findByIdComplete", recebido.getId());
				PedestreEntity pedestreOriginal = original.getPedestre(); 
				
				recebido.setPedestre(pedestre);
				recebido.setVersao(original.getVersao());
				recebido.setDataCadastroDaFace(new Date());
				recebido.setStatusCadastroExterno(StatusCadastroExterno.CADASTRADO);
				recebido.setToken(null);
				
				ejb.gravaObjeto(recebido);
				ejb.gravaObjeto(pedestreOriginal);
				
				
				//regarrega
				original = (CadastroExternoEntity)
						ejb.recuperaObjeto(CadastroExternoEntity.class, "findByIdComplete", recebido.getId());
				
				return Response.status(statusResponse)
						.entity(new ResponseServiceTO(Status.OK.toString(), 
								"Face cadastrada com sucesso! Aguarde processamento", 
								null))
						.build();
			}
			
		} catch(Exception e){
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new ResponseServiceTO(Status.INTERNAL_SERVER_ERROR.toString(), ResourceBundleUtils.getInstance()
							.recuperaChave(e.getMessage(),FacesContext.getCurrentInstance()),null)).build();
		}
		
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ResponseServiceTO(Status.INTERNAL_SERVER_ERROR.toString(), 
						"Registro de face não encontrado",null)).build();
	}
	
	@GET
	@Path("/get/facial")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFacialStatus(@QueryParam("token")String token, @QueryParam("idExterno")String idExterno){
		try {
			//recupera ejb e valida usuário
			PedestreEntity usuario = validaTokenPedestre(token);
			if(usuario != null){
				CadastroExternoEntity e = (CadastroExternoEntity) getEjb("BaseEJB")
						.recuperaObjeto(CadastroExternoEntity.class, Long.valueOf(idExterno));
				if(e != null) {
					//processa
					CadastroExternoTO to = new CadastroExternoTO(e);
					to.setPrimeiraFoto(null);
					to.setSegundaFoto(null);
					to.setTerceiraFoto(null);
					
					return Response.status(Status.OK).entity(
							new ResponseServiceTO(Status.OK.toString(), null, to)).build();
				}
			}
			return Response.status(Status.NOT_FOUND).entity(
					new ResponseServiceTO(Status.NOT_FOUND.toString(), "Face não encontrada!",null)).build();
			
		} catch (Exception e) {
			//caso aconteça algum erro de validação
			//imprimi o erro e retorna nulo.
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new ResponseServiceTO(Status.INTERNAL_SERVER_ERROR.toString(), ResourceBundleUtils.getInstance()
							.recuperaChave(e.getMessage(),FacesContext.getCurrentInstance()),null)).build();
		}
	}

	private void preenchePedestre(PedestreEntity usuario) {
		
		if(usuario.getCliente() != null)
			usuario.setUnidade(usuario.getCliente().getNome());
		usuario.setCliente(null);
		usuario.setEndereco(null);
		usuario.setCargo(null);
		usuario.setCentroCusto(null);
		usuario.setEmpresa(null);
		usuario.setDepartamento(null);
		
		usuario.setBiometrias(null);
		usuario.setDocumentos(null);
		usuario.setMensagensPersonalizadas(null);
		usuario.setEquipamentos(null);
		usuario.setRegras(null);
		usuario.setUsuario(null);
		
		usuario.setSenha(null);
		
		processaCadastroFacial(usuario);
		
		
		
	}

	@SuppressWarnings("unchecked")
	private void processaCadastroFacial(PedestreEntity p) {
		
		try {
			
			Map<String, Object> args = new HashMap<>();
			args.put("ID_PEDESTRE", p.getId());
			
			BaseEJBRemote ejb = (BaseEJBRemote) getEjb("BaseEJB");
			List<CadastroExternoEntity> cadastrosExternos = (List<CadastroExternoEntity>) ejb
									.pesquisaArgFixosLimitado(CadastroExternoEntity.class, "findLastByIdPedestre", args, 0, 1);
			
			if(cadastrosExternos != null && !cadastrosExternos.isEmpty()) {
				//processa ultimo registro
				CadastroExternoEntity facialAtual = cadastrosExternos.get(0);
				facialAtual.setPedestre(null);
				facialAtual.setCliente(null);
				p.setFacialAtual(facialAtual);
			}else {
				p.setFacialAtual(null);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private String getTempoRenovacaoQRCode(Long idCliente) throws Exception {
		ParametroEntity param = getEjb("BaseEJB")
				.getParametroSistema(BaseConstant.PARAMETERS_NAME.TEMPO_QRCODE_DINAMICO, idCliente);
		if(param != null)
			return param.getValor();
		return "5";
	}
	
}
