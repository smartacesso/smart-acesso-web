package br.com.startjob.acesso.modelo.to.app;

import java.io.Serializable;
import java.util.Date;

/**
 * Projeção de aviso do app para listagem sem carregar imagem.
 */
public class AvisoListItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String titulo;
	private String descricao;
	private Date dataPublicacao;
	private boolean temImagem;

	public AvisoListItem(Long id, String titulo, String descricao, Date dataPublicacao, Boolean temImagem) {
		this.id = id;
		this.titulo = titulo;
		this.descricao = descricao;
		this.dataPublicacao = dataPublicacao;
		this.temImagem = Boolean.TRUE.equals(temImagem);
	}

	public Long getId() {
		return id;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public Date getDataPublicacao() {
		return dataPublicacao;
	}

	public boolean isTemImagem() {
		return temImagem;
	}
}
