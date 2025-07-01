package br.com.startjob.acesso.to;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.startjob.acesso.modelo.entity.LocalEntity;

public class LocalTo {

	private Long id;
	private String nome;
	private String idClient;
	
	@JsonProperty("hikivisionDeviceNames")
	private List<String> deviceNames;
	private Boolean removed;
	
	private Date dataRemovido;
	
	
	public LocalTo(LocalEntity local) {
		this.id = local.getId();
		this.nome = local.getNome() != null ? local.getNome() : "";
		this.idClient = local.getCliente().getId().toString();
		this.removed = local.getRemovido();
		this.dataRemovido = local.getDataRemovido();
		this.setDeviceNames(local.getHikivisionDeviceNames());
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
	public String getIdClient() {
		return idClient;
	}
	public void setIdClient(String idClient) {
		this.idClient = idClient;
	}
	public List<String> getDeviceNames() {
		return deviceNames;
	}
	public void setDeviceNames(List<String> deviceNames) {
		this.deviceNames = deviceNames;
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
	
	
}
