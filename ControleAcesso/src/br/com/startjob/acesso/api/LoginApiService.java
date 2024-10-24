package br.com.startjob.acesso.api;

import java.util.Date;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.ejb.LoginEJBRemote;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.utils.EncryptionUtils;
import br.com.startjob.acesso.to.ResponseServiceTO;
import br.com.startjob.acesso.utils.ResourceBundleUtils;

@Path("/login")
public class LoginApiService extends BaseService {
	
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
	@Path("/do")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@QueryParam("unidadeName")String unidade, @QueryParam("loginName")String login, 
							@QueryParam("passwd")String passwd) {
		
		try {
			//recupera ejb e valida usuário
			LoginEJBRemote loginEJB = (LoginEJBRemote) getEjb("LoginEJB");
			UsuarioEntity usuario = loginEJB.validaUsuario(unidade, login, EncryptionUtils.encrypt(passwd),
								BaseConstant.ACCESS_TYPES.API, getDeviceType(request));
			
			//prepara retorno
			if(usuario != null) {
				
				//gerar token de acesso
				usuario.setToken(new Date().getTime()+"-"+
								usuario.getId()+"-"+usuario.getSenha());
				
				usuario.getCliente().setPlanos(null);
				usuario.getCliente().setEndereco(null);
				usuario.setEndereco(null);
				
//				usuario.setSenha(null);
				
				usuario.setQtdePadraoDigitosCartao(getQuantidadeDigitosCartao(usuario.getCliente().getId()));
				usuario.setChaveIntegracaoComtele(getChaveDeIntegracaoComtele(usuario.getCliente().getId()));
				usuario.getCliente().setIntegracaoSoc(null);
				
				return Response.status(Status.OK).entity(
						new ResponseServiceTO(Status.OK.toString(), null, usuario)).build();
			}
			
			return Response.status(Status.UNAUTHORIZED).entity(
					new ResponseServiceTO(Status.UNAUTHORIZED.toString(), "Usuário não autorizado!", null)).build();
			
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
	@Path("/interno")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loginInterno(@QueryParam("unidadeName")String unidade, @QueryParam("loginName")String login, 
				@QueryParam("passwd")String passwd) {
		
		try {
			//recupera ejb e valida usuário
			LoginEJBRemote loginEJB = (LoginEJBRemote) getEjb("LoginEJB");
			UsuarioEntity usuario = loginEJB.validaUsuario(unidade, login, passwd,
								BaseConstant.ACCESS_TYPES.WEB,getDeviceType(request));
			
			//prepara retorno
			if(usuario != null){
				
				//gerar token de acesso
				usuario.setToken(new Date().getTime()+"-"+
								usuario.getId()+"-"+usuario.getSenha());
				
				usuario.getCliente().setPlanos(null);
				usuario.getCliente().setEndereco(null);
				usuario.setEndereco(null);
				
				usuario.setSenha(null);
				
				return Response.status(Status.OK).entity(
						new ResponseServiceTO(Status.OK.toString(), null,usuario)).build();
			}
			
			return Response.status(Status.UNAUTHORIZED).entity(
					new ResponseServiceTO(Status.UNAUTHORIZED.toString(), "Usuário não autorizado!",null)).build();
			
			
		} catch (Exception e) {
			//caso aconteça algum erro de validação
			//imprimi o erro e retorna nulo.
			System.out.println(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new ResponseServiceTO(Status.INTERNAL_SERVER_ERROR.toString(), ResourceBundleUtils.getInstance()
							.recuperaChave(e.getMessage(),FacesContext.getCurrentInstance()),null)).build();
		}
	}

	private Integer getQuantidadeDigitosCartao(Long idCliente) throws Exception {
		Integer qtdeDigitosCartao = null;
		BaseEJBRemote baseEJB = getEjb("BaseEJB");
		
		ParametroEntity param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.ESCOLHER_QTDE_DIGITOS_CARTAO, idCliente);

		if(param != null)
			qtdeDigitosCartao = Integer.valueOf(param.getValor());
		
		return qtdeDigitosCartao;
	}
	
	private String getChaveDeIntegracaoComtele(Long idCliente) throws Exception {
		String chaveIntegracaoComtele = null;
		BaseEJBRemote baseEJB = getEjb("BaseEJB");
		
		ParametroEntity param = baseEJB.getParametroSistema(BaseConstant.PARAMETERS_NAME.CHAVE_DE_INTEGRACAO_COMTELE, idCliente);

		if(param != null)
			chaveIntegracaoComtele = param.getValor();
		
		return chaveIntegracaoComtele;
	}
}
