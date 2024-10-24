package br.com.startjob.acesso.to;

import java.util.Date;

public class PedestrianMessagesTO {
	
	private Long id;
	private String nome;
	private String status;
	private String mensagem;
	private Long quantidade;
	private Date validade;
	
	public PedestrianMessagesTO(String mensagem, long quantidade) {
		this.mensagem   = mensagem;
		this.quantidade = quantidade;
	}
	
	public PedestrianMessagesTO(Long id, String mensagem, Long quantidade) {
		this.id = id;
		this.mensagem = mensagem;
		this.quantidade = quantidade;
	}
	
	public PedestrianMessagesTO(Long id, String nome, String status, String mensagem, Long quantidade, Date validade) {
		this.id = id;
		this.nome = nome;
		this.status = status;
		this.mensagem = mensagem;
		this.quantidade = quantidade;
		this.validade = validade;
	}

	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public Long getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getValidade() {
		return validade;
	}

	public void setValidade(Date validade) {
		this.validade = validade;
	}
	
}
