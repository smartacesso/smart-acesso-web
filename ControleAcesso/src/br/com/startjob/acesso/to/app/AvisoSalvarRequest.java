package br.com.startjob.acesso.to.app;

/**
 * Corpo JSON para {@code POST /app/avisos/salvar} (perfil gerencial no app).
 */
public class AvisoSalvarRequest {

	private Long id;
	private String titulo;
	private String descricao;
	/** ISO-8601 ou yyyy-MM-dd'T'HH:mm:ss */
	private String dataPublicacao;
	/** Imagem opcional em base64 (puro ou data URL). */
	private String imagemBase64;

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

	public String getDataPublicacao() {
		return dataPublicacao;
	}

	public void setDataPublicacao(String dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	public String getImagemBase64() {
		return imagemBase64;
	}

	public void setImagemBase64(String imagemBase64) {
		this.imagemBase64 = imagemBase64;
	}
}
