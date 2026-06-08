package br.com.startjob.acesso.to.app;

import java.util.List;

/**
 * Resposta paginada padrão da API app ({@code /sistema/restful-services/app}).
 */
public class PaginatedResponse<T> {

	private List<T> content;
	private long total;
	private int pagina;
	private int tamanho;
	private boolean hasMore;

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getPagina() {
		return pagina;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
	}

	public int getTamanho() {
		return tamanho;
	}

	public void setTamanho(int tamanho) {
		this.tamanho = tamanho;
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	public static <T> PaginatedResponse<T> of(List<T> content, long total, int pagina, int tamanho) {
		PaginatedResponse<T> response = new PaginatedResponse<>();
		response.setContent(content);
		response.setTotal(total);
		response.setPagina(pagina);
		response.setTamanho(tamanho);
		response.setHasMore((long) (pagina + 1) * tamanho < total);
		return response;
	}
}
