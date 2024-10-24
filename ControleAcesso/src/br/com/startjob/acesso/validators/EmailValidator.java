package br.com.startjob.acesso.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import br.com.startjob.acesso.utils.ResourceBundleUtils;

/**
 * Validador e e-mails
 * 
 * @author Gustavo Diniz
 *
 */
@FacesValidator("emailValidator")
public class EmailValidator implements Validator {

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\." +
			"[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*" +
			"(\\.[A-Za-z]{2,})$";
 
	private Pattern pattern;
	private Matcher matcher;
 
	public EmailValidator(){
		  pattern = Pattern.compile(EMAIL_PATTERN);
	}
 
	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		
		if(value == null)
			return;
		
		if("".equals(value.toString()))
			return;
		
		matcher = pattern.matcher(value.toString().replace("-", ""));
		if(!matcher.matches()){
 
			FacesMessage msg = 
				new FacesMessage(ResourceBundleUtils.getInstance().recuperaChave("msg.email.incorreto", context));
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
 
		}
 
	}
	
	/**
	 * valida e-mail.
	 * @param email
	 * @return
	 */
	public static boolean validaEmail(String email){
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email.replace("-", ""));
		if(!matcher.matches()){
			return false;
		}
		
		return true;
	}

}
