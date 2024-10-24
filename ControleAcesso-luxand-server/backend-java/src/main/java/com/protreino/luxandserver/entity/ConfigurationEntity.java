package com.protreino.luxandserver.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.protreino.luxandserver.enumeration.FieldType;

@SuppressWarnings("serial")
@Entity
@Table(name="TB_CONFIGURATION")
public class ConfigurationEntity extends BaseEntity {
	
	@Column(name="NAME", nullable=false, length=100)
	private String name;
	
	@Column(name="DESCRIPTION", nullable=false, length=100)
	private String description;
	
	@Type(type="text")
	@Column(name="VALUE", nullable=false)
	private String value;
	
	@Type(type="text")
	@Column(name="DEFAULT_VALUE", nullable=true)
	private String defaultValue;
	
	@Enumerated(EnumType.STRING)
	@Column(name="TYPE", nullable=false, length=100)
	private FieldType type;
	
	public ConfigurationEntity(){
	}
	
	public ConfigurationEntity(String name, String description, String value, String defaultValue, FieldType type) {
		this.name = name;
		this.description = description;
		this.value = value;
		this.defaultValue = defaultValue;
		this.type = type;
	}
	
	public void update(ConfigurationEntity newConfiguration) {
		this.name = newConfiguration.getName();
		this.description = newConfiguration.getDescription();
		this.value = newConfiguration.getValue();
		this.defaultValue = newConfiguration.getDefaultValue();
		this.type = newConfiguration.getType();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

}
