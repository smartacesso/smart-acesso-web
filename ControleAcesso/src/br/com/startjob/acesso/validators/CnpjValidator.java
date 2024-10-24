package br.com.startjob.acesso.validators;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import br.com.startjob.acesso.utils.ResourceBundleUtils;

@FacesValidator("cnpjValidator")
public class CnpjValidator implements Validator {
    @Override
    public void validate(FacesContext arg0, UIComponent arg1, Object valorTela) throws ValidatorException {
    	
    	if(valorTela == null || "".equals(String.valueOf(valorTela))){
    		return;
    	}
    	
         if (!validaCNPJ(String.valueOf(valorTela))) {
              FacesMessage message = new FacesMessage();
              message.setSeverity(FacesMessage.SEVERITY_ERROR);
              message.setSummary(ResourceBundleUtils.
            		  getInstance().recuperaChave("erro.validacao.cnpj", FacesContext.getCurrentInstance()));
              throw new ValidatorException(message);
         }
    }

    /**
    * Valida CNPJ do usuário.
    *
    * @param cnpj String valor com 14 dígitos
   */
    public static boolean validaCNPJ(String cnpj) {
    	
    	if(cnpj == null)
    		return true;
    	
    	if(cnpj.length() > 14){
    		//altera CNPJ para conter somente os numericos
    		String cnpjAux = "";
    		for (int i = 0; i < cnpj.length(); i++) {
				try{
					Long.parseLong(cnpj.substring(i, i+1));
					cnpjAux += cnpj.substring(i, i+1);
				}catch (NumberFormatException e) {
					//não trata
				}
			}
    		cnpj = cnpjAux;
    	}
    		
    	
        if(cnpj.length() != 14)
             return false;
        
        try {
             Long.parseLong(cnpj);
        } catch (NumberFormatException e) { // CNPJ não possui somente números
             return false;
        }

        int soma = 0;
        String cnpj_calc = cnpj.substring(0, 12);
        char chr_cnpj[] = cnpj.toCharArray();
        for(int i = 0; i < 4; i++)
             if(chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9)
                  soma += (chr_cnpj[i] - 48) * (6 - (i + 1));

        for(int i = 0; i < 8; i++)
            if(chr_cnpj[i + 4] - 48 >= 0 && chr_cnpj[i + 4] - 48 <= 9)
                  soma += (chr_cnpj[i + 4] - 48) * (10 - (i + 1));

        int dig = 11 - soma % 11;
        cnpj_calc = (new StringBuilder(String.valueOf(cnpj_calc))).append(dig != 10 && dig != 11 ? Integer.toString(dig) : "0").toString();
        soma = 0;
        for(int i = 0; i < 5; i++)
            if(chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9)
                 soma += (chr_cnpj[i] - 48) * (7 - (i + 1));

        for(int i = 0; i < 8; i++)
            if(chr_cnpj[i + 5] - 48 >= 0 && chr_cnpj[i + 5] - 48 <= 9)
                 soma += (chr_cnpj[i + 5] - 48) * (10 - (i + 1));

        dig = 11 - soma % 11;
        cnpj_calc = (new StringBuilder(String.valueOf(cnpj_calc))).append(dig != 10 && dig != 11 ? Integer.toString(dig) : "0").toString();

        return cnpj.equals(cnpj_calc);
    }
}
