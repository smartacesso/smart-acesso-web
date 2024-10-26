package br.com.startjob.acesso.to.responsible;

public class ResponsibleLoginOutput {

	private String nome;
	
	private String phone;
	
	private String token;
	
	private String email;
	
	private String role;
	
	private Long idResponsilbe;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Long getIdResponsilbe() {
		return idResponsilbe;
	}

	public void setIdResponsilbe(Long idResponsilbe) {
		this.idResponsilbe = idResponsilbe;
	}

}