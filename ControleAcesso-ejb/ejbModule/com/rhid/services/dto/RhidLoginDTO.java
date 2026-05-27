package com.rhid.services.dto;

public class RhidLoginDTO {

	public String email;
	public String password;
	public String domain;

	public RhidLoginDTO() {
	}

	public RhidLoginDTO(String email, String password, String domain) {
		this.email = email;
		this.password = password;
		this.domain = domain;
	}
}
