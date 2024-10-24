package br.com.startjob.acesso.modelo.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.startjob.acesso.modelo.entity.base.BaseEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;

@Entity
@Table(name="TB_MENSAGEM_EQUIPAMENTO")
@NamedQueries({
	@NamedQuery(name  = "MensagemEquipamentoEntity.findAll", 
				query = "select obj "
				      + "from MensagemEquipamentoEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "MensagemEquipamentoEntity.findById", 
				query = "select obj from MensagemEquipamentoEntity obj "
					  + "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name = "MensagemEquipamentoEntity.findAllAtivas",
				query = "select obj from MensagemEquipamentoEntity obj "
					  + "left join fetch obj.pedestre p "
					  + "where p.id = :ID_PEDESTRE "
					  + "and obj.status = 'ATIVO' "
					  + "and (obj.validade is null or obj.validade >= :DATA_ATUAL) "
					  + "and (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc")
})
@SuppressWarnings("serial")
public class MensagemEquipamentoEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_MENSAGEM_EQUIPAMENTO", nullable=false, length=4)
	private Long id;
	
	@Column(name="NOME", nullable=true, length=255)
	private String nome;
	
	@Enumerated(EnumType.STRING)
	@Column(name="STATUS", nullable=true, length=100)
	private Status status;
	
	@Column(name="MENSAGEM", nullable=true, length=255)
	private String mensagem;
	
	@Column(name="QUANTIDADE", nullable=true, length=10)
	private Long quantidade;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="VALIDADE", nullable=true, length=100)
	private Date validade;
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_PEDESTRE", nullable=true)
	private PedestreEntity pedestre;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Long getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}
	public Date getValidade() {
		return validade;
	}
	public void setValidade(Date validade) {
		this.validade = validade;
	}
	public PedestreEntity getPedestre() {
		return pedestre;
	}
	public void setPedestre(PedestreEntity pedestre) {
		this.pedestre = pedestre;
	}
	
}
