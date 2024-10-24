package br.com.startjob.acesso.to;

import java.util.Date;

import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;

public class UserTO {

	private Long id;
	private String email;
	private String loginName;
	private String password;
	private String status;
	private Date creationDate;
	private String name;
	private String idClient;
	
	private Boolean removed;
	private Date dataRemovido;
	private String perfilAcesso;
	
	public UserTO(UsuarioEntity usuario) {
		this.id = usuario.getId();
		this.email = usuario.getEmail() != null ? usuario.getEmail() : null;
		this.loginName = usuario.getLogin() != null ? usuario.getLogin() : "";
		this.password = usuario.getSenha() != null ? usuario.getSenha() : null;
		this.status = usuario.getStatus() != null ? usuario.getStatus().toString() : Status.INATIVO.toString();
		this.creationDate = usuario.getDataCriacao() != null ? usuario.getDataCriacao() : new Date();
		this.name = usuario.getNome() != null ? usuario.getNome() : "";
		this.removed = usuario.getRemovido();
		this.dataRemovido = usuario.getDataRemovido();
		this.perfilAcesso = usuario.getPerfil().name();
	}
	
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
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getIdClient() {
		return idClient;
	}

	public void setIdClient(String idClient) {
		this.idClient = idClient;
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

	public String getPerfilAcesso() {
		return perfilAcesso;
	}

	public void setPerfilAcesso(String perfilAcesso) {
		this.perfilAcesso = perfilAcesso;
	}

}
