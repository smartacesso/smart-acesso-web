package br.com.startjob.acesso.converters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;

import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.entity.RegraEntity;

@FacesConverter(forClass = RegraEntity.class)
public class RegraConverter implements Converter {
	

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		RegraEntity regra = null;
		
		if(value != null) {
			try {
				Map<String, Object> args = new HashMap<String, Object>();
				args.put("ID", new Long(value));
				
				@SuppressWarnings("unchecked")
				List<RegraEntity> regras = (List<RegraEntity>) getEjb("BaseEJB")
					.pesquisaArgFixos(RegraEntity.class, "findById", args);
				
				regra = regras.get(0);
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return regra;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value != null) {
			return ((RegraEntity) value).getId().toString();
		}
		return "";
	}
	
	protected BaseEJBRemote getEjb(String name) throws Exception{

		InitialContext ctx = new InitialContext();
		return (BaseEJBRemote) ctx.lookup("java:global" +
				"/ControleAcesso-ear/ControleAcesso-ejb/"+name);
		
	}
	
}
