package br.com.startjob.acesso.to;

import java.util.Date;

import br.com.startjob.acesso.modelo.entity.DepartamentoEntity;

public class DepartamentoTO {

	private Long id;
	private String nome;
	private String status;
	private Boolean removed;
	private Date dataRemovido;
	
	public DepartamentoTO(DepartamentoEntity departamento) {
		this.id = departamento.getId();
		this.nome = departamento.getNome();
		this.status = departamento.getStatus().toString();
		this.removed = departamento.getRemovido();
		this.dataRemovido = departamento.getDataRemovido();
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
