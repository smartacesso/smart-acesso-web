package br.com.startjob.acesso.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("cnpjConverter")
public class CnpjConverter implements Converter {
	
    public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
        /*
         * Irá converter CNPJ formatado para um sem pontos, traço e barra.
         * Ex.: 07.374.998/0001-33 torna-se 07374998000133.
         */
         String cnpj = value;
         if (value!= null && !value.equals(""))
              cnpj = value.replaceAll("\\.", "").replaceAll("\\-", "").replaceAll("/", "");

         return cnpj;
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
        /*
         * Irá converter CNPJ não formatado para um com pontos, traço e barra.
         * Ex.: 07374998000133 torna-se 07.374.998/0001-33.
         */
         String cnpj = (String) value;
         if (cnpj != null && cnpj.length() == 14)
             cnpj = cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." + cnpj.substring(5, 8 ) + "/" + cnpj.substring(8, 12) + "-" + cnpj.substring(12, 14);

         return cnpj;
    }
}
