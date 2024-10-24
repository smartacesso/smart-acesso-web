package br.com.startjob.acesso.utils;

import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {
	
	
	
	public static void setCookiePropertie(String name, String value, FacesContext fContext) {
		
		//adiciona cookie da solicitação
		Cookie ck = new Cookie(name, value);
        ck.setMaxAge(60    //segundos  
        			 * 1   //um minuto
        			 * 60  //uma hora
        			 * 24  //um dia
        			 * 365 //um  ano
        			 * 20  );//20 anos
        
        ((HttpServletResponse) fContext
        		.getExternalContext().getResponse())
        		.addCookie(ck);
        
	}
	
	public static String getCookiePropetie(String name, FacesContext fContext) {
		
		Cookie [] cookies = ((HttpServletRequest) fContext
        		.getExternalContext().getRequest())
        		. getCookies();
		
		if(cookies != null) {
			for (Cookie cookie : cookies ) {
				if(cookie.getName().equals(name))
					return cookie.getValue();
			}
		}
		
		return null;
		
	}
	
	

}
