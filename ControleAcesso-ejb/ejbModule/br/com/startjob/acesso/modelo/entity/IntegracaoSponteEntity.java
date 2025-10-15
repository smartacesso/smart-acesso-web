package br.com.startjob.acesso.modelo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import br.com.startjob.acesso.modelo.entity.base.BaseEntity;

@Entity
@Table(name = "TB_INTEGRACAO_SPONTE")
@NamedQueries({
		@NamedQuery(name = "IntegracaoSponteEntity.findAll", query = "select obj " + "from IntegracaoSponteEntity obj "
				+ "where (obj.removido = false or obj.removido is null) " + "order by obj.id asc"),
		@NamedQuery(name = "IntegracaoSponteEntity.findById", query = "select obj from IntegracaoSponteEntity obj "
				+ "where obj.id = :ID order by obj.id asc") })
@SuppressWarnings("serial")
public class IntegracaoSponteEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_INTEGRACAO_SPONTE", nullable = false, length = 4)
	private Long id;

	@Column(name = "ID_USUARIO", nullable = true, length = 255)
	private String idCliente;

	@Column(name = "TOKEN", nullable = true, length = 255)
	private String token;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(String idCliente) {
		this.idCliente = idCliente;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
