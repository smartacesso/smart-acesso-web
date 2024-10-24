package br.com.startjob.acesso.to;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ResponseServiceTO implements Serializable {
	
	private String status;
	
	private String message;
	
	private Serializable object;
	
	public ResponseServiceTO(){
		
	}
	
	public ResponseServiceTO(String status, String message, Serializable object){
		this.status = status;
		this.message = message;
		this.object = object;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Serializable getObject() {
		return object;
	}

	public void setObject(Serializable object) {
		this.object = object;
	}

}
