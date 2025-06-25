package com.totvs.dto;

import java.time.LocalDate;

import br.com.startjob.acesso.modelo.entity.CargoEntity;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;

public class FuncionarioTotvsDto {

	private String companyKey;
	private String branch;
	private String code;
	private String id;
	private String name;
	private String fullName;
	private String badgeNumber;
	private String departamentCode;
	private String departamentInternalId;
	private String departmentDescription;
	private String hiringDate;
	private String demissionDate;
	private String birthDate;
	private String gender;
	private String street;
	private String streetNumber;
	private String complement;
	private String neighborhood;
	private String homeState;
	private String naturalCity;
	private String employeeSituation;
	private String city;
	private String zipCode;
	private String areaCode;
	private String telephone;
	private String areaCodeMobile;
	private String mobileNumber;
	private String socialIntegProgCode;
	private String workCardNumber;
	private String workCardSerie;
	private String workCardStateIssuing;
	private String costCenterCode;
	private String costCenterInternalId;
	private String costCenterDescription;
	private String employeePositionCode;
	private String employeePositionCodeInternalId;
	private String employeePositionDescription;
	private String salaryCategory;
	private String workShiftCode;
	private String workShiftInternalId;
	private String workshiftDescription;
	private String workShiftSequence;
	private String roleCode;
	private String roleInternalId;
	private String roleDescription;
	private String contractEnd;
	private String email;
	private String employeeCpf;
	private String esocialCategoryCode;
	// Getters and Setters

	public PedestreEntity toPedestreEntity(ClienteEntity cliente) {
		PedestreEntity pedestre = new PedestreEntity();
		pedestre.setNome(name);
		pedestre.setMatricula(code);
		pedestre.setCodigoCartaoAcesso(code);
		pedestre.setCpf(employeeCpf);
		pedestre.setEmail(email);
		pedestre.setEmpresa(null);
		pedestre.setCargo(null);
		pedestre.setCliente(cliente);
		pedestre.setTipo(TipoPedestre.PEDESTRE);
		pedestre.setExistente(true);
		pedestre.setVersao(0);
		
		if(employeeSituation.trim().equals("")) {
			pedestre.setObservacoes("Importado dia " + LocalDate.now().toString());
			pedestre.setStatus(Status.ATIVO);

		}else {
			pedestre.setStatus(Status.INATIVO);
			pedestre.setObservacoes("Funcionario com situação : " + employeeSituation);
		}
		
		return pedestre;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmployeeCpf() {
		return employeeCpf;
	}

	public void setEmployeeCpf(String employeeCpf) {
		this.employeeCpf = employeeCpf;
	}

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	public String getName() {
		return name.trim().replaceAll("\\s+", " ");
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmployeeSituation() {
		return employeeSituation;
	}

	public void setEmployeeSituation(String employeeSituation) {
		this.employeeSituation = employeeSituation;
	}
	
}
