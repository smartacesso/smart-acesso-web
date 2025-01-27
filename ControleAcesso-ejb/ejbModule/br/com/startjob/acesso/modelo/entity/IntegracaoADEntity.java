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
@Table(name="TB_INTEGRACAO_AD")
@NamedQueries({
	@NamedQuery(name  = "IntegracaoADEntity.findAll", 
				query = "select obj "
				      + "from IntegracaoADEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "IntegracaoADEntity.findById", 
				query = "select obj from IntegracaoADEntity obj "
					  + "where obj.id = :ID order by obj.id asc")
})
@SuppressWarnings("serial")
public class IntegracaoADEntity  extends BaseEntity{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_INTEGRACAO_AD", nullable=false, length=4)
	private Long id;
	
	@Column(name="USUARIO", nullable=true, length=255)
	private String usuario;
	
	@Column(name="SENHA", nullable=true, length=255)
	private String senha;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
}
