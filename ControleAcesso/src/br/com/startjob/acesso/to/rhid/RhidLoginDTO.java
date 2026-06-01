package br.com.startjob.acesso.to.rhid;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement // Importante para o JAX-RS entender o mapeamento se não houver Jackson explícito
public class RhidLoginDTO {
    public String email;
    public String password;
    public String domain;

    public RhidLoginDTO(String email, String password, String domain) {
        this.email = email;
        this.password = password;
        this.domain = domain;
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}


}