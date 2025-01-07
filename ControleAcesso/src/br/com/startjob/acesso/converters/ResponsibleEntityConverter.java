package br.com.startjob.acesso.converters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.naming.InitialContext;

import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.ejb.ResponsibleEJB;
import br.com.startjob.acesso.modelo.entity.RegraEntity;
import br.com.startjob.acesso.modelo.entity.ResponsibleEntity;

@FacesConverter(forClass = ResponsibleEntity.class)
public class ResponsibleEntityConverter implements Converter {

    @Inject
    private ResponsibleEJB responsibleEjb;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
    	Map<String, Object> args = new HashMap<String, Object>();
		args.put("ID", Long.valueOf(value));
        // Buscar a entidade pelo ID
		List<ResponsibleEntity> responblibleList = null;
		try {
			responblibleList = (List<ResponsibleEntity>)  getEjb("BaseEJB").pesquisaArgFixos(ResponsibleEntity.class, "findById", args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responblibleList.get(0);
        
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof ResponsibleEntity) {
            return String.valueOf(((ResponsibleEntity) value).getId());
        } else {
            throw new IllegalArgumentException("O valor não é uma instância de ResponsibleEntity: " + value);
        }
    }
    
    protected BaseEJBRemote getEjb(String name) throws Exception{

		InitialContext ctx = new InitialContext();
		return (BaseEJBRemote) ctx.lookup("java:global" +
				"/ControleAcesso-ear/ControleAcesso-ejb/"+name);
		
	}
}
