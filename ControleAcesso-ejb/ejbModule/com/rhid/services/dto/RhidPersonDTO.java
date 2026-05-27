package com.rhid.services.dto;

import java.io.Serializable;

public class RhidPersonDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer status;
	private String dateShiftsStartStr;
	private Integer newIdShift;
	private Integer idCompany;
	private String name;
	private String pis;
	private String cpf;
	private String admissionDateStr;
	private String registration;
	private String email;
	private Boolean excluded;

	public RhidPersonDTO() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getDateShiftsStartStr() {
		return dateShiftsStartStr;
	}

	public void setDateShiftsStartStr(String dateShiftsStartStr) {
		this.dateShiftsStartStr = dateShiftsStartStr;
	}

	public Integer getNewIdShift() {
		return newIdShift;
	}

	public void setNewIdShift(Integer newIdShift) {
		this.newIdShift = newIdShift;
	}

	public Integer getIdCompany() {
		return idCompany;
	}

	public void setIdCompany(Integer idCompany) {
		this.idCompany = idCompany;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPis() {
		return pis;
	}

	public void setPis(String pis) {
		this.pis = pis;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getAdmissionDateStr() {
		return admissionDateStr;
	}

	public void setAdmissionDateStr(String admissionDateStr) {
		this.admissionDateStr = admissionDateStr;
	}

	public String getRegistration() {
		return registration;
	}

	public void setRegistration(String registration) {
		this.registration = registration;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getExcluded() {
		return excluded;
	}

	public void setExcluded(Boolean excluded) {
		this.excluded = excluded;
	}
}
