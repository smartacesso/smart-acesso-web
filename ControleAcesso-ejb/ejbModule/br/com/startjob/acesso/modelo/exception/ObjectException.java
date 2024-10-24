package br.com.startjob.acesso.modelo.exception;

import javax.transaction.RollbackException;

@SuppressWarnings("serial")
public class ObjectException extends RollbackException {
	
	private Object o;
	
	public ObjectException(Object o){
		this.o = o;
	}

	public Object getO() {
		return o;
	}

	public void setO(Object o) {
		this.o = o;
	}
	
	

}
