package br.com.startjob.acesso.to.teknisa;


public class TeknisaToken {
	private String nome;
	private String senha;
	//client que vai ser responsavel pelo de-para
	private String client;
	
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	
	public String toString() {
		return "{'nome': '"+ nome + "', 'senha' : '"+ senha + "', 'client' : '"+ client +"' }";
	}
}
