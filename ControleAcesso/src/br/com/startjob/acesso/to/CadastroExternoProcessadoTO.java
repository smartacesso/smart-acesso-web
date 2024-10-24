package br.com.startjob.acesso.to;

import java.util.List;

public class CadastroExternoProcessadoTO {

	private Long idCliente;
	private List<ResultadoProcessamento> resultados;

	public Long getIdCliente() {
		return idCliente;
	}
	
	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}
	
	public List<ResultadoProcessamento> getResultados() {
		return resultados;
	}
	
	public void setResultados(List<ResultadoProcessamento> resultados) {
		this.resultados = resultados;
	}
	
}
