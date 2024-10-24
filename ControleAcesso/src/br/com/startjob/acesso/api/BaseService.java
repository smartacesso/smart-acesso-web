package br.com.startjob.acesso.api;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.mail.Session;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.spi.UnauthorizedException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;

/**
 * Classe base para confecção se serviços RESTeasy
 * 
 * @author Gustavo Diniz
 * @since 30/03/2013
 *
 */

public class BaseService {
	
	public static final String ERROR_MESSAGE = "error";
	public static final String SUCESS_MESSAGE = "sucess";
	
	/**
	 * Formatação de datas json.
	 */
	protected SimpleDateFormat sdfJson = new SimpleDateFormat("ddMMyyyy-HHmmss");
	
	/**
	 * Formatação de datas.
	 */
	protected SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	/**
	 * Retorna o EJB solicitado.
	 * Procura EJB do EAR do projeto FitnessProject
	 * 
	 * @param name - nome do EJB
	 * @return instancia do EJB
	 */
	protected BaseEJBRemote getEjb(String name) throws Exception{

		InitialContext ctx = new InitialContext();
		return (BaseEJBRemote) ctx.lookup("java:global" +
				"/ControleAcesso-ear/ControleAcesso-ejb/"+name);
		
	}
	
	/**
	 * Retorna javamail.
	 * 
	 * @return sessão do java email
	 */
	protected Session getEmailSession() throws Exception{

		InitialContext ctx = new InitialContext();
		return (Session) ctx.lookup("java:jboss/mail/suporte");
		
	}
	
	/**
	 * Retorna o EJB solicitado.
	 * Procura EJB do EAR do projeto FitnessProject
	 * 
	 * @param name - nome do EJB
	 * @return instancia do EJB
	 */
	@SuppressWarnings("rawtypes")
	protected BaseEJBRemote getEjb(Class classEjb) throws Exception{

		InitialContext ctx = new InitialContext();
		return (BaseEJBRemote) ctx.lookup(
				"java:global/FitnessProject-ear/FitnessProject-ejb/"+
						classEjb.getSimpleName().substring(0, 
						classEjb.getSimpleName().indexOf("Remote"))+
				"!"+classEjb.getName());
		
	}
	
	
	
	/**
	 * Valida token de autorização.
	 *
	 * @return true se válido.
	 * @throws Exception e 
	 */
	protected UsuarioEntity validaToken(String token) throws Exception{
		
		String [] tokens = token.split("-");
		
		if(tokens.length == 3){
		
			try{
				
				Long time = Long.valueOf(tokens[0]);
				Calendar dataAtual = Calendar.getInstance(BaseConstant.PT_BR);
				dataAtual.add(Calendar.MINUTE, 30);
				
				//recupera usuário
				UsuarioEntity user = (UsuarioEntity) ((BaseEJBRemote)
						getEjb("BaseEJB")).recuperaObjeto(UsuarioEntity.class, new Long(tokens[1]));
				
				if(/*time <= dataAtual.getTimeInMillis()
						&& */user != null
						&& user.getSenha().equals(tokens[2]))
					return user;
			
			}catch (Exception e) {
				throw new Exception("Erro na validação Token: " + e.getMessage());
			}
		
		}
			
		throw new UnauthorizedException("Token inválido!");
		
	}
	
	/**
	 * Valida token de autorização do pedestre.
	 *
	 * @return true se válido.
	 * @throws Exception e 
	 */
	protected PedestreEntity validaTokenPedestre(String token) throws Exception{
		
		String [] tokens = token.split("-");
		
		if(tokens.length == 3){
		
			try{
				
				Long time = Long.valueOf(tokens[0]);
				Calendar dataAtual = Calendar.getInstance(BaseConstant.PT_BR);
				dataAtual.add(Calendar.MINUTE, 30);
				
				//recupera usuário
				PedestreEntity user = (PedestreEntity) ((BaseEJBRemote)
						getEjb("BaseEJB")).recuperaObjeto(PedestreEntity.class, "findByIdComplete", new Long(tokens[1]));
				
				if(/*time <= dataAtual.getTimeInMillis()
						&& */user != null
						&& user.getSenha().equals(tokens[2]))
					return user;
			
			}catch (Exception e) {
				e.printStackTrace();
				throw new Exception("Erro na validação Token: " + e.getMessage());
			}
		
		}
			
		throw new UnauthorizedException("Token inválido!");
		
	}
	
	/**
	 * Retorna URL que a aplicação esta rodando
	 * 
	 * @return
	 */
	public String getURLApp(HttpServletRequest request){
		
		StringBuffer urlBuffer = request.getRequestURL();
		String [] slip = urlBuffer.toString().split("/");
		
		return slip[0]+"/"+slip[1]+"/"+slip[2];
	}
	
	/**
	 * Retorna GSON conforme parametros
	 * @param object
	 * @return
	 */
	public Gson getGSonConverter() {
		return new GsonBuilder().serializeNulls()
				.setDateFormat("ddMMyyyy-HHmmss").create();
	}
	
	/**
	 * Retorna facesContext para o servlet.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	protected FacesContext getFacesContext(HttpServletRequest request, HttpServletResponse response) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null) {

            FacesContextFactory contextFactory  = (FacesContextFactory)FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
            LifecycleFactory lifecycleFactory = (LifecycleFactory)FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY); 
            Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

            facesContext = contextFactory.getFacesContext(request.getSession().getServletContext(), request, response, lifecycle);

            //TODO : Set using our inner class 
            //InnerFacesContext.setFacesContextAsCurrentInstance(facesContext);

            // set a new viewRoot, otherwise context.getViewRoot returns null
            UIViewRoot view = facesContext.getApplication().getViewHandler().createView(facesContext, "");
            facesContext.setViewRoot(view);                
        }
        return facesContext;
    }
	
	public static String getDeviceType(HttpServletRequest request){
		
		String userAgent = request.getHeader("User-Agent");
		//System.out.println("USER-AGENT:" + userAgent);
		
		String device = BaseConstant.WEB;
		if(userAgent.contains("Apache-HttpClient/UNAVAILABLE"))
			device = BaseConstant.ANDROID;
		else if(userAgent.contains("Pro-Treino-Mobile") 
				|| (userAgent.contains("Pro-Treino") && userAgent.contains("Mobile")))
			device = BaseConstant.IPHONE;
		else{
			device = userAgent;
		}
		
		return device;
		
	}
	
	/**
	 * Converte array de bytes.
	 * @author gustavo.diniz
	 *
	 */
	public static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return org.apache.commons.codec.binary.Base64.decodeBase64(json.getAsString());
        }
 
        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(org.apache.commons.codec.binary.Base64.encodeBase64String(src));
        }
    }
	
}