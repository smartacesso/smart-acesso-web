package br.com.startjob.acesso.modelo.to.app;

import java.io.Serializable;
import java.util.List;

public class PageResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<T> items;
	private final long total;

	public PageResult(List<T> items, long total) {
		this.items = items;
		this.total = total;
	}

	public List<T> getItems() {
		return items;
	}

	public long getTotal() {
		return total;
	}
}
