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
@Table(name="TB_CONFIG_RHID")
@NamedQueries({
	@NamedQuery(name  = "ConfiguracaoRhidEntity.findAll", 
				query = "select obj "
				      + "from ConfiguracaoRhidEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "ConfiguracaoRhidEntity.findById", 
				query = "select obj from ConfiguracaoRhidEntity obj "
					  + "where obj.id = :ID order by obj.id asc")
})
@SuppressWarnings("serial")
public class ConfiguracaoRhidEntity  extends BaseEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_CONFIG_RHID", nullable=false, length=4)
	private Long id;
	
	@Column(name="EMAIL", nullable=true, length=255)
	private String email;
	
	@Column(name="SENHA", nullable=true, length=255)
	private String senha;
	
	@Column(name = "DOMINIO", nullable = true)
	private String dominio;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getDominio() {
		return dominio;
	}

	public void setDominio(String dominio) {
		this.dominio = dominio;
	}
	
	
}
