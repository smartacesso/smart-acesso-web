package com.senior.services.dto;

public class CargoSeniorDto {
	
//    <codCar>1</codCar>
//    <datCri>01/01/1950</datCri>
//    <titRed>MOTORISTA</titRed>

    private String codigoCargo;
    private String dataCriacao;
    private String cargo;
	public String getCodigoCargo() {
		return codigoCargo;
	}
	public void setCodigoCargo(String codigoCargp) {
		this.codigoCargo = codigoCargp;
	}
	public String getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao(String dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	public String getCargo() {
		return cargo;
	}
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

    
}
