package com.protreino.luxandserver.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="TB_USER")
public class UserEntity extends BaseEntity {
	
	@Column(name="USERNAME", nullable=false, length=100)
	private String username;
	
	@Column(name="PASSWORD", nullable=false, length=100)
	private String password;
	
	@Column(name="UNIT_NAME", nullable=false, length=100)
	private String unitName;
	
	@Column(name="NAME", nullable=false, length=100)
	private String name;
	
	@Column(name="CLIENT_ID", nullable=false, length=100)
	private Long clientId;
	
	public UserEntity() {}
	
	public UserEntity(String username, String password, String unitName, String name, Long clientId) {
		this.username = username;
		this.password = password;
		this.unitName = unitName;
		this.name = name;
		this.clientId = clientId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

}
