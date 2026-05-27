package com.rhid.services.dto;

import java.util.ArrayList;
import java.util.List;

public class RhidLoginResponseDTO {

	private String accessToken;
	private Integer code;
	private String error;
	private boolean expiredPassword;
	private boolean isPerson;
	private boolean revendaInadimplente;
	private List<RhidCustomerDTO> listCustomer = new ArrayList<>();

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean isExpiredPassword() {
		return expiredPassword;
	}

	public void setExpiredPassword(boolean expiredPassword) {
		this.expiredPassword = expiredPassword;
	}

	public boolean isPerson() {
		return isPerson;
	}

	public void setPerson(boolean isPerson) {
		this.isPerson = isPerson;
	}

	public boolean isRevendaInadimplente() {
		return revendaInadimplente;
	}

	public void setRevendaInadimplente(boolean revendaInadimplente) {
		this.revendaInadimplente = revendaInadimplente;
	}

	public List<RhidCustomerDTO> getListCustomer() {
		return listCustomer;
	}

	public void setListCustomer(List<RhidCustomerDTO> listCustomer) {
		this.listCustomer = listCustomer;
	}

	public boolean requerSelecaoDominio() {
		return listCustomer != null && !listCustomer.isEmpty();
	}
}
