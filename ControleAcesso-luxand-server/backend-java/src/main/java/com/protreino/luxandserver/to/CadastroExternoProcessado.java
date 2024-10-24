package com.protreino.luxandserver.to;

import java.util.List;

public class CadastroExternoProcessado {

	private Long idCliente;
	private List<ResultadoProcessamentoCadastroExterno> resultados;
	
	public CadastroExternoProcessado() {
	}

	public Long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}

	public List<ResultadoProcessamentoCadastroExterno> getResultados() {
		return resultados;
	}

	public void setResultados(List<ResultadoProcessamentoCadastroExterno> resultados) {
		this.resultados = resultados;
	}
	
}
