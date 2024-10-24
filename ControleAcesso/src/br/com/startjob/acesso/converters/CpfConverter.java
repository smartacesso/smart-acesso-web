package br.com.startjob.acesso.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("cpfConverter")
public class CpfConverter implements Converter {
    public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
        /*
         * Irá converter CPF formatado para um sem pontos e traço.
         * Ex.: 355.245.198-87 torna-se 35524519887.
         */
         String cpf = value;
         if (value!= null && !value.equals(""))
              cpf = value.replaceAll("\\.", "").replaceAll("\\-", "");

         return cpf;
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
        /*
         * Irá converter CPF não formatado para um com pontos e traço.
         * Ex.: 35524519887 torna-se 355.245.198-87.
         */
         String cpf = (String) value;
         if (cpf != null && cpf.length() == 11)
              cpf = cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);

         return cpf;
    }
}
