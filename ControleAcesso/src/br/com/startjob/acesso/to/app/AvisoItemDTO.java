package br.com.startjob.acesso.to.app;

import java.util.Date;

public class AvisoItemDTO {

	private Long id;
	private String titulo;
	private String descricao;
	private Date dataPublicacao;
	private boolean temImagem;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDataPublicacao() {
		return dataPublicacao;
	}

	public void setDataPublicacao(Date dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	public boolean isTemImagem() {
		return temImagem;
	}

	public void setTemImagem(boolean temImagem) {
		this.temImagem = temImagem;
	}
}
