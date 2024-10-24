package com.protreino.luxandserver.to;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CadastroExterno {
	
	private Long id;
	private Long idPedestre;
	private String nome;
	private int imageWidth;
	private int imageHeight;
	
	private String primeiraFoto;
	private String segundaFoto;
	private String terceiraFoto;
	
	private List<String> fotos;
	
	public CadastroExterno() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdPedestre() {
		return idPedestre;
	}

	public void setIdPedestre(Long idPedestre) {
		this.idPedestre = idPedestre;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public List<String> getFotos() {
		return fotos;
	}

	public void setFotos(List<String> fotos) {
		this.fotos = fotos;
	}

	public String getPrimeiraFoto() {
		return primeiraFoto;
	}

	public void setPrimeiraFoto(String primeiraFoto) {
		this.primeiraFoto = primeiraFoto;
	}

	public String getSegundaFoto() {
		return segundaFoto;
	}

	public void setSegundaFoto(String segundaFoto) {
		this.segundaFoto = segundaFoto;
	}

	public String getTerceiraFoto() {
		return terceiraFoto;
	}

	public void setTerceiraFoto(String terceiraFoto) {
		this.terceiraFoto = terceiraFoto;
	}

}
