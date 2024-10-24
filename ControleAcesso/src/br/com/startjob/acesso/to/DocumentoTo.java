package br.com.startjob.acesso.to;

import java.util.Date;

public class DocumentoTo {

	private Long id;
	private String nome;
	private String arquivo;
	private Date validade;
	
	public DocumentoTo() {}

	public DocumentoTo(Long id, String nome, Date validade) {
		this.id = id;
		this.nome = nome;
		this.validade = validade;
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
	public String getArquivo() {
		return arquivo;
	}
	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}
	public Date getValidade() {
		return validade;
	}
	public void setValidade(Date validade) {
		this.validade = validade;
	}
}
