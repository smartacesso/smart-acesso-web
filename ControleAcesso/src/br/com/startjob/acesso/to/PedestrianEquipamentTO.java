package br.com.startjob.acesso.to;

import java.util.Date;

public class PedestrianEquipamentTO {
	
	private Long id;
	private String idEquipamento;
	private Date validadeEquipamento;
	private String nomeEquipamento;
	
	public PedestrianEquipamentTO() {
	}
	
	public PedestrianEquipamentTO(Long id, String idEquipamento, Date validadeEquipamento, String nomeEquipamento) {
		this.id = id;
		this.idEquipamento = idEquipamento;
		this.validadeEquipamento = validadeEquipamento;
		this.nomeEquipamento = nomeEquipamento;
	}
	
	public PedestrianEquipamentTO(String idEquipamento) {
		this.idEquipamento = idEquipamento;
	}

	public String getIdEquipamento() {
		return idEquipamento;
	}

	public void setIdEquipamento(String id) {
		this.idEquipamento = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getValidadeEquipamento() {
		return validadeEquipamento;
	}

	public void setValidadeEquipamento(Date validadeEquipamento) {
		this.validadeEquipamento = validadeEquipamento;
	}

	public String getNomeEquipamento() {
		return nomeEquipamento;
	}

	public void setNomeEquipamento(String nomeEquipamento) {
		this.nomeEquipamento = nomeEquipamento;
	}

}
