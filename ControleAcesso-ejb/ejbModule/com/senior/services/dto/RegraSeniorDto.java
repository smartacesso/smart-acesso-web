package com.senior.services.dto;

import br.com.startjob.acesso.modelo.entity.RegraEntity;

public class RegraSeniorDto {
	String id;
	String nomeRegra;
	

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getNomeRegra() {
		return nomeRegra;
	}


	public void setNomeRegra(String nomeRegra) {
		this.nomeRegra = nomeRegra;
	}
	

	public RegraEntity toRegraEntity() {
		// TODO Auto-generated method stub
		return null;
	}


}
