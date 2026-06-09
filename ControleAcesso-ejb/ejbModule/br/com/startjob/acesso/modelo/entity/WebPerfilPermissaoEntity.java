package br.com.startjob.acesso.modelo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.startjob.acesso.modelo.entity.base.ClienteBaseEntity;
import br.com.startjob.acesso.modelo.enumeration.PerfilAcesso;

@Entity
@Table(name = "TB_WEB_PERFIL_PERMISSAO", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "ID_CLIENTE", "PERFIL", "CODIGO_PERMISSAO" }) })
@NamedQueries({
		@NamedQuery(name = "WebPerfilPermissaoEntity.findByIdCliente", query = "select obj "
				+ "from WebPerfilPermissaoEntity obj "
				+ "where obj.cliente.id = :ID_CLIENTE "
				+ "and (obj.removido = false or obj.removido is null)"),
		@NamedQuery(name = "WebPerfilPermissaoEntity.findByIdClientePerfil", query = "select obj "
				+ "from WebPerfilPermissaoEntity obj "
				+ "where obj.cliente.id = :ID_CLIENTE "
				+ "and obj.perfil = :PERFIL "
				+ "and (obj.removido = false or obj.removido is null)"),
		@NamedQuery(name = "WebPerfilPermissaoEntity.findAllByIdCliente", query = "select obj "
				+ "from WebPerfilPermissaoEntity obj "
				+ "where obj.cliente.id = :ID_CLIENTE") })
@SuppressWarnings("serial")
public class WebPerfilPermissaoEntity extends ClienteBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_WEB_PERFIL_PERMISSAO", nullable = false)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "PERFIL", nullable = false, length = 20)
	private PerfilAcesso perfil;

	@Column(name = "CODIGO_PERMISSAO", nullable = false, length = 80)
	private String codigoPermissao;

	@Column(name = "HABILITADO", nullable = false)
	private Boolean habilitado = Boolean.TRUE;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PerfilAcesso getPerfil() {
		return perfil;
	}

	public void setPerfil(PerfilAcesso perfil) {
		this.perfil = perfil;
	}

	public String getCodigoPermissao() {
		return codigoPermissao;
	}

	public void setCodigoPermissao(String codigoPermissao) {
		this.codigoPermissao = codigoPermissao;
	}

	public Boolean getHabilitado() {
		return habilitado;
	}

	public void setHabilitado(Boolean habilitado) {
		this.habilitado = habilitado;
	}
}
