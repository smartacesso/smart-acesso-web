package com.protreino.luxandserver.to;

import java.io.Serializable;

public class LoginResponse  implements Serializable {
	
	private static final long serialVersionUID = -8091879091924046844L;
	
	private String message;
	private String name;
	private String accessToken;
	
	public LoginResponse() {}
	
	public LoginResponse(String message) {
		this.message = message;
	}

	public LoginResponse(String name, String accessToken) {
		this.name = name;
		this.accessToken = accessToken;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

}
