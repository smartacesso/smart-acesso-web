package br.com.startjob.acesso.controller.uc018;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;

@FacesConverter(value = "pedestreConverter", managed = true)
public class PedestreConverter implements Converter<PedestreEntity> {

    @Override
    public PedestreEntity getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                InitialContext ctx = new InitialContext();

                PedestreEJBRemote pedestreEJB =
                    (PedestreEJBRemote) ctx.lookup(
                        "java:global/ControleAcesso-ear/ControleAcesso-ejb/PedestreEJB!br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote"
                    );
                
                // Busca o objeto pelo ID no banco
                return pedestreEJB.buscaPedestrePorId(Long.parseLong(value));
            } catch (NumberFormatException e) {
                return null;
            } catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, PedestreEntity object) {
        if (object != null && object.getId() != null) {
            return String.valueOf(object.getId());
        }
        return null;
    }
}