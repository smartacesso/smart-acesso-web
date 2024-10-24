package com.age.soc.services.exportaDados.parameters;

public class FuncionariosPorEmpresaParam {
	
	//valores fixos
	public String empresa 	= "312493";
	public String codigo 	= "144389";
	public String chave 	= "96a7db3a136f11d2f2bb";
	public String tipoSaida = "json";
	
	//parametros para enviar
	public String empresaTrabalho;
	public String cpf;
	public String parametroData = Boolean.FALSE.toString();
	public String dataInicio;
	public String dataFim;
	
	public FuncionariosPorEmpresaParam() {
		
	}
	
	public FuncionariosPorEmpresaParam(String empresaTrabalho) {
		this.empresaTrabalho = empresaTrabalho;
	}

}
