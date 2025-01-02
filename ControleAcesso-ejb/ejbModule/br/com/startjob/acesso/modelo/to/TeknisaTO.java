package br.com.startjob.acesso.modelo.to;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

public class TeknisaTO implements Serializable {

	private Long idAcesso;
	private String nome;
	private String cnpj;
	private Date data;
	private Long idCliente;
	private String idOrganizacao;
	private String idFilial;
	
	public TeknisaTO() {
	}
	
	public TeknisaTO(Long idAcesso, Date data, String nome, String cnpj, Long idCliente, 
			String idOrganizacao, String idFilial) {
		this.idAcesso = idAcesso;
		this.data = data;
		this.nome = nome;
		this.cnpj = cnpj;
		this.idCliente = idCliente;
		this.idOrganizacao = idOrganizacao;
		this.idFilial = idFilial;
	}

	public Long getIdCliente() {
		return idCliente;
	}
	public void settIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}


	public Long getIdAcesso() {
		return idAcesso;
	}


	public void setIdAcesso(Long idAcesso) {
		this.idAcesso = idAcesso;
	}


	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}

	public String getIdOrganizacao() {
		return idOrganizacao;
	}

	public void setIdOrganizacao(String idOrganizacao) {
		this.idOrganizacao = idOrganizacao;
	}

	public String getIdFilial() {
		return idFilial;
	}

	public void setIdFilial(String idFilial) {
		this.idFilial = idFilial;
	}
}
