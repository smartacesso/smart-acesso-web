package com.protreino.luxandserver.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalLoginResponse {
	
	private String status; // "OK" ou "Internal Server Error"
	private String message;
	private ExternalLoginResponseObject object;
	
	public ExternalLoginResponse() {}
	
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

	public ExternalLoginResponseObject getObject() {
		return object;
	}

	public void setObject(ExternalLoginResponseObject object) {
		this.object = object;
	}
	
	public String getTranslatedMessage() {
		if ("msgs.account.usuario.nao.encontrado".equals(message))
			return "Usuário não encontrado";
		if ("msgs.account.usuario.senha.invalida".equals(message))
			return "Senha inválida";
		if ("msgs.account.unidade.nao.encontrada".equals(message))
			return "Unidade não encontrada";
		return message;
	}

}
