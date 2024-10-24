package br.com.startjob.acesso.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.startjob.acesso.modelo.utils.DateUtils;

@FacesConverter("timeToLongConverter")
public class TimeToLongConverter implements Converter  {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		if(arg2 != null && !"".equals(arg2))
			return DateUtils.getInstance().getTimeMillis(arg2);
		return null;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		if(arg2 == null)
			return null;
		return DateUtils.getInstance().getTimeMinutsFormat((Long) arg2);
	}

}
