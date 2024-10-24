package br.com.startjob.acesso.modelo.collections;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.startjob.acesso.modelo.ejb.BaseEJBRemote;

@SuppressWarnings("rawtypes")
public class LazyLoadingList extends AbstractList {
	
	private BaseEJBRemote baseEJB;
	private Class classeEntidade;
	private String namedQuery;
	private Map<String, Object> arg;
	private Map<String, String> order;

	private List firstList;
	private List viewList;

	private int currentPage = -1;
	private int numResults;
	private int pageSize;
	
	private LazyLoadingItemListener listener;

	public LazyLoadingList(BaseEJBRemote baseEJB, Class classeEntidade,
			String namedQuery, Map<String, Object> arg, Map<String, String> order, 
			int pageSize, int numResults) {
		this.classeEntidade = classeEntidade;
		this.namedQuery = namedQuery;
		//copia argumentos
		this.arg = new HashMap<String, Object>();
		for (String key : arg.keySet()) {
			this.arg.put(key, arg.get(key));
		}
		this.order = order;
		this.baseEJB = baseEJB;
		this.numResults = numResults;
		this.pageSize = pageSize;
	}
	
	public LazyLoadingList(BaseEJBRemote baseEJB, Class classeEntidade,
			String namedQuery, Map<String, Object> arg, Map<String, String> order, 
			int pageSize, int numResults, LazyLoadingItemListener listener) {
		this.classeEntidade = classeEntidade;
		this.namedQuery = namedQuery;
		//copia argumentos
		this.arg = new HashMap<String, Object>();
		if(arg != null){
			for (String key : arg.keySet()) {
				this.arg.put(key, arg.get(key));
			}
		}
		this.order = order;
		this.baseEJB = baseEJB;
		this.numResults = numResults;
		this.pageSize = pageSize;
		this.listener = listener;
	}

	@Override
	public Object get(int i) {
		//System.out.println("POSIÇÃO: " + i + " / QUERY: " + classeEntidade + "." + namedQuery);
		try {
			if (i < pageSize) {
				if (firstList == null){
					if(order == null)
						firstList = baseEJB.pesquisaSimplesLimitado(classeEntidade,
								namedQuery, arg, i, pageSize);
					else
						firstList = baseEJB.pesquisaSimplesLimitado(classeEntidade,
								namedQuery, arg, order, i, pageSize);
				}
				Object item = firstList.get(i);
				if(listener != null)
					listener.process(item, i);
				return item;
			}
			int page = i / pageSize;
			if (page != currentPage) {
				currentPage = page;
				if(order == null)
					viewList = baseEJB.pesquisaSimplesLimitado(
							classeEntidade, namedQuery, arg, i, pageSize);
				else
					viewList = baseEJB.pesquisaSimplesLimitado(
							classeEntidade, namedQuery, arg, order, i, pageSize);
				
			}
			Object item = viewList.get(i % pageSize);
			if(listener != null)
				listener.process(item, i);
			return item;
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
		

	}

	@Override
	public int size() {
		return numResults;
	}
	
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	public void setNumResults(int numResults) {
		this.numResults = numResults;
	}
	
	/**
	 * Listener para processamentos necessários no item
	 * recuperado.
	 * @author gustavo
	 *
	 */
	public interface LazyLoadingItemListener{
		
		public void process(Object item, int position);
		
	}

	public LazyLoadingItemListener getListener() {
		return listener;
	}

	public void setListener(LazyLoadingItemListener listener) {
		this.listener = listener;
	}
	

}