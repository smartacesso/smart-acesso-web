package br.com.startjob.acesso.to.app;

public class CadastroRequest {
	String nome;
	String cpf;
	String celular;
	String oberservacao;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	public String getOberservacao() {
		return oberservacao;
	}
	public void setOberservacao(String oberservacao) {
		this.oberservacao = oberservacao;
	}

}
