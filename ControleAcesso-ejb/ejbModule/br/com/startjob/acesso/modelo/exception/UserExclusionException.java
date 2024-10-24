package br.com.startjob.acesso.modelo.exception;

/**
 * Exceção para exclusão de usuários
 * @author Gustavo
 *
 */
@SuppressWarnings("serial")
public class UserExclusionException extends Exception {
	
	private String [] parans;
	
	public UserExclusionException(String msg, String [] parans){
		super(msg);
		this.parans = parans;
	}

	
	public String[] getParans() {
		return parans;
	}

}
