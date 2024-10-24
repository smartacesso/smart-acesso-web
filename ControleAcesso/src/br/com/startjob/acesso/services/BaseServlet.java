package br.com.startjob.acesso.services;

import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.mail.Session;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.utils.AppAmbienteUtils;


@SuppressWarnings("serial")
public class BaseServlet extends HttpServlet {

	/**
	 * Mail session.
	 */
	@Resource(mappedName="java:/mail/suporte")
	protected Session mailSession;
	
	/**
	 * EJB para tarefas b�sicas
	 */
	@EJB
	protected BaseEJBRemote baseEJB;
	
	@Override
	public void init() throws ServletException {
		
		//configura timeout diferente passa sess�es de servlets
		//HttpSession session = request.getSession();
	     
	   // session.setMaxInactiveInterval(300);    // session timeout in seconds
		
	}
	
	/**
	 * Retorna facesContext para ervlet.
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

            // Set using our inner class
            InnerFacesContext.setFacesContextAsCurrentInstance(facesContext);

            // set a new viewRoot, otherwise context.getViewRoot returns null
            UIViewRoot view = facesContext.getApplication().getViewHandler().createView(facesContext, "");
            facesContext.setViewRoot(view);                
        }
        return facesContext;
    }
	
	/**
	 * You need an inner class to be able to call FacesContext.setCurrentInstance
	 * since it's a protected method
	 * 		
	 * @author Gustavo
	 *
	 */
    public abstract static class InnerFacesContext extends FacesContext {
        public static void setFacesContextAsCurrentInstance(FacesContext facesContext) {
            FacesContext.setCurrentInstance(facesContext);
        }
    }
    
    /**
	 * Retorna URL que a aplica��o esta rodando
	 * 
	 * @return
	 */
	public String getURLApp(HttpServletRequest request){
		
		try{
			StringBuffer urlBuffer = request.getRequestURL();
			String [] slip = urlBuffer.toString().split("/");
			
			return slip[0]+"/"+slip[1]+"/"+slip[2];
			
		} catch (Throwable e) {
			return AppAmbienteUtils.getConfig(AppAmbienteUtils.CONFIG_AMBIENTE_MAIN_SITE);
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	public static BaseEJBRemote getEjb(Class classEjb) throws Exception {
		

		InitialContext ctx = new InitialContext();
		return (BaseEJBRemote) ctx
				.lookup("java:global/ControleAcesso-ear/ControleAcesso-ejb/"
						+ classEjb.getSimpleName().substring(0,
								classEjb.getSimpleName().indexOf("Remote"))
						+ "!" + classEjb.getName());
	}
	
	protected String geraToken(UsuarioEntity user) {
		return user.getId().toString()+"AE"+new Date().getTime();
	}
	
	protected boolean isNullOrEmpty(String string) {
		if (string == null)
			return true;
		if (string.isEmpty())
			return true;
		return false;
	}
	
	protected boolean isNullOrZero(Number number) {
		if (number == null)
			return true;
		if (number.doubleValue() == 0d)
			return true;
		return false;
	}
}
