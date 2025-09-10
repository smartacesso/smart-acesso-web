package br.com.startjob.acesso.modelo.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import br.com.startjob.acesso.modelo.entity.base.ClienteBaseEntity;


@Entity
@Table(name="TB_CREDENCIAIS_TOTVS_ENTITY")
@NamedQueries({
	@NamedQuery(name  = "CredenciaisIntegracaoTotvsEntity.findAll", 
				query = "select obj "
				      + "from CredenciaisIntegracaoTotvsEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "CredenciaisIntegracaoTotvsEntity.findById", 
				query = "select obj from CredenciaisIntegracaoTotvsEntity obj "
					  + "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name  = "CredenciaisIntegracaoTotvsEntity.findByUsuario", 
				query = "select obj from CredenciaisIntegracaoTotvsEntity obj "
					  + "where obj.usuario = :USUARIO order by obj.id asc")
})
@SuppressWarnings("serial")
public class CredenciaisIntegracaoTotvsEntity extends ClienteBaseEntity{

    @Id
    private Long id;
    private String usuario;
    private String senha; 
    private String token;
    
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
