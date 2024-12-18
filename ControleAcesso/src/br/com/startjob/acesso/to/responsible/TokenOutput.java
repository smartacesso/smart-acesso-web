package br.com.startjob.acesso.to.responsible;

import com.google.gson.annotations.SerializedName;

public class TokenOutput {
	
	@SerializedName("LOGIN")
	private String login;
	@SerializedName("SENHA")
	private String senha;
	private Long idResponsible;
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public void setIdResponsible(Long idResponsible) {
		this.idResponsible = idResponsible;
	}
	public Long getIdResponsible() {
		return idResponsible;
	}

	public String toString() {
		return "{ 'idResponsible': '" + idResponsible + "', 'login' : '" + login + "', 'senha' : '" + senha + "'}";
	}
	
	

}
