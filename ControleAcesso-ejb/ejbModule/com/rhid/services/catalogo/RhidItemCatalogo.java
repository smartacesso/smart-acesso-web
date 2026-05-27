package com.rhid.services.catalogo;

import java.io.Serializable;

/**
 * Item genérico de catálogo RHID (empresa, departamento, cargo, horário).
 */
public class RhidItemCatalogo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
	private String tradingName;
	private String cnpj;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTradingName() {
		return tradingName;
	}

	public void setTradingName(String tradingName) {
		this.tradingName = tradingName;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
}
