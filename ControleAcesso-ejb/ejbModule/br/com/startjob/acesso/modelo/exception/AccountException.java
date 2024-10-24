package br.com.startjob.acesso.modelo.exception;

@SuppressWarnings("serial")
public class AccountException extends Exception {
	
	private String [] parans;
	
	public AccountException(String msg, String [] parans){
		super(msg);
		this.parans = parans;
	}

	public String[] getParans() {
		return parans;
	}
	
	

}
