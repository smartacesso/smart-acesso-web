package br.com.startjob.acesso.to;

import java.io.Serializable;
import java.util.Date;

import br.com.startjob.acesso.modelo.entity.CadastroExternoEntity;
import br.com.startjob.acesso.modelo.enumeration.StatusCadastroExterno;

public class CadastroExternoTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String nome;
	private Long idPedestre;
	private Long token;
	private StatusCadastroExterno statusCadastroExterno;
	private Date dataCadastroDaFace;
	private String primeiraFoto;
	private String segundaFoto;
	private String terceiraFoto;
	
	private int imageWidth;
	private int imageHeight;
	
	public CadastroExternoTO(CadastroExternoEntity cadastroExterno) {
		this.id = cadastroExterno.getId();
		this.nome = cadastroExterno.getPedestre().getNome();
		this.idPedestre = cadastroExterno.getPedestre().getId();
		this.token = cadastroExterno.getToken();
		this.statusCadastroExterno = cadastroExterno.getStatusCadastroExterno();
		this.dataCadastroDaFace = cadastroExterno.getDataCadastroDaFace();
		this.primeiraFoto = org.apache.commons.codec.binary.Base64.encodeBase64String( cadastroExterno.getPrimeiraFoto() );
		this.segundaFoto = org.apache.commons.codec.binary.Base64.encodeBase64String(cadastroExterno.getSegundaFoto() );
		this.terceiraFoto = org.apache.commons.codec.binary.Base64.encodeBase64String(cadastroExterno.getTerceiraFoto() );
		this.imageHeight = cadastroExterno.getImageHeight();
		this.imageWidth = cadastroExterno.getImageWidth();
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

	public Long getToken() {
		return token;
	}

	public void setToken(Long token) {
		this.token = token;
	}

	public StatusCadastroExterno getStatusCadastroExterno() {
		return statusCadastroExterno;
	}

	public void setStatusCadastroExterno(StatusCadastroExterno statusCadastroExterno) {
		this.statusCadastroExterno = statusCadastroExterno;
	}

	public Date getDataCadastroDaFace() {
		return dataCadastroDaFace;
	}

	public void setDataCadastroDaFace(Date dataCadastroDaFace) {
		this.dataCadastroDaFace = dataCadastroDaFace;
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
}
