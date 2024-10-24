package br.com.startjob.acesso.to;

import java.io.Serializable;

import br.com.startjob.acesso.modelo.entity.CadastroExternoEntity;

@SuppressWarnings("serial")
public class RequestFacialServiceTO implements Serializable {
	
	private String token;
    private Long   user;
    private CadastroExternoEntity object;
    
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Long getUser() {
		return user;
	}
	public void setUser(Long user) {
		this.user = user;
	}
	public CadastroExternoEntity getObject() {
		return object;
	}
	public void setObject(CadastroExternoEntity object) {
		this.object = object;
	}
    
    
	
	

}
