package br.com.startjob.acesso.controller.uc018;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;

import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;

@FacesConverter(value = "empresaConverter", managed = true)
public class EmpresaConverter implements Converter<EmpresaEntity> {

	@Override
	public EmpresaEntity getAsObject(FacesContext context, UIComponent component, String value) {
		if (value != null && value.trim().length() > 0) {
			try {
				InitialContext ctx = new InitialContext();
				BaseEJBRemote baseEJB = (BaseEJBRemote) ctx.lookup(
						"java:global/ControleAcesso-ear/ControleAcesso-ejb/BaseEJB!br.com.startjob.acesso.modelo.ejb.BaseEJBRemote");
				@SuppressWarnings("unchecked")
				java.util.List<EmpresaEntity> lista = (java.util.List<EmpresaEntity>) baseEJB
						.pesquisaArgFixos(EmpresaEntity.class, "findById",
								java.util.Collections.singletonMap("ID", Long.parseLong(value)));
				if (lista != null && !lista.isEmpty()) {
					return lista.get(0);
				}
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, EmpresaEntity object) {
		if (object != null && object.getId() != null) {
			return String.valueOf(object.getId());
		}
		return null;
	}
}
