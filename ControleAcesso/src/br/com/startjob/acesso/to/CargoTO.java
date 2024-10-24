package br.com.startjob.acesso.to;

import java.io.Serializable;
import java.util.Date;

import br.com.startjob.acesso.modelo.entity.CargoEntity;

public class CargoTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String nome;
	private String status;
	private Boolean removed;
	private Date dataRemovido;
	
	public CargoTO(CargoEntity cargo) {
		this.id = cargo.getId();
		this.nome = cargo.getNome();
		this.status = cargo.getStatus().toString();
		this.removed = cargo.getRemovido();
		this.dataRemovido = cargo.getDataRemovido();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Boolean getRemoved() {
		return removed;
	}

	public void setRemoved(Boolean removed) {
		this.removed = removed;
	}

	public Date getDataRemovido() {
		return dataRemovido;
	}

	public void setDataRemovido(Date dataRemovido) {
		this.dataRemovido = dataRemovido;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
