package br.com.startjob.acesso.modelo.entity;

import java.util.Date;

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
@Table(name="TB_INTEGRACAO_TOTVS")
@NamedQueries({
	@NamedQuery(name  = "IntegracaoTotvsEntity.findAll", 
				query = "select obj "
				      + "from IntegracaoTotvsEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "IntegracaoTotvsEntity.findById", 
				query = "select obj from IntegracaoTotvsEntity obj "
					  + "where obj.id = :ID order by obj.id asc")
})
@SuppressWarnings("serial")
public class IntegracaoTotvsEntity extends BaseEntity{
	

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_INTEGRACAO_TOTVS", nullable=false, length=4)
	private Long id;
	
	@Column(name="URL_CONEXAO", nullable=true, length=255)
	private String url;
	
	@Column(name="USUARIO", nullable=true, length=255)
	private String usuario;
	
	@Column(name="SENHA", nullable=true, length=255)
	private String senha;
	
	@Column(name="EMPRESA", nullable=true, length=255)
	private String empresa;
	
	@Column(name="ULTIMA_IMORTACAO", nullable=true, length=255)
	private Date ultimaImportacao;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public Date getUltimaImportacao() {
		return ultimaImportacao;
	}

	public void setUltimaImportacao(Date ultimaImportacao) {
		this.ultimaImportacao = ultimaImportacao;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}
	
}
