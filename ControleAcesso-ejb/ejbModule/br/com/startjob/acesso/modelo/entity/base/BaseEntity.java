package br.com.startjob.acesso.modelo.entity.base;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 * Classe base que identifica entidades.
 * Possui diversos campos comuns entre as entidades.
 * 
 * @author Gustavo Diniz
 * @since 02/03/2013
 */
@MappedSuperclass
@SuppressWarnings("serial")
public class BaseEntity implements Serializable {
	
	@Transient
	private Boolean existente = Boolean.FALSE;
	
	@Temporal( TemporalType.TIMESTAMP)
	@Column(name="DATA_ALTERACAO", nullable=false, length=11)
	private Date dataAlteracao = new Date();
	
	@Temporal( TemporalType.TIMESTAMP)
	@Column(name="DATA_CRIACAO", nullable=false, length=11)
	private Date dataCriacao = new Date();
	
	@Version
	@Column(name="VERSAO", nullable=false, length=4)
	private Integer versao = 0;
		
	@Column(name="REMOVIDO", nullable=true, length=11)
	private Boolean removido = null;
	
	@Temporal( TemporalType.TIMESTAMP)
	@Column(name="DATA_REMOVIDO", nullable=true, length=11)
	private Date dataRemovido = null;

	public Boolean getExistente() {
		return existente;
	}

	public void setExistente(Boolean existente) {
		this.existente = existente;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Integer getVersao() {
		return versao;
	}

	public void setVersao(Integer versao) {
		this.versao = versao;
	}

	public Boolean getRemovido() {
		return removido;
	}

	public void setRemovido(Boolean removido) {
		this.removido = removido;
	}

	public Date getDataRemovido() {
		return dataRemovido;
	}

	public void setDataRemovido(Date dataRemovido) {
		this.dataRemovido = dataRemovido;
	}
	
	

}
