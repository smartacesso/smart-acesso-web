package br.com.startjob.acesso.modelo.entity.base;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import br.com.startjob.acesso.modelo.entity.ClienteEntity;

@MappedSuperclass
@SuppressWarnings("serial")
public class ClienteBaseEntity extends BaseEntity {
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_CLIENTE", nullable=true)
	protected ClienteEntity cliente;

	public ClienteEntity getCliente() {
		return cliente;
	}

	public void setCliente(ClienteEntity cliente) {
		this.cliente = cliente;
	}
	
}
