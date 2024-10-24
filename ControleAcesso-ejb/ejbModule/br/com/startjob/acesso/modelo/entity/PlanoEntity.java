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
@Table(name="TB_PLANO")
@NamedQueries({
	@NamedQuery(name  = "PlanoEntity.findAll", 
				query = "select obj "
				      + "from PlanoEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id desc"),
	@NamedQuery(name  = "PlanoEntity.findById", 
				query = "select obj from PlanoEntity obj "
					  + "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name  = "PlanoEntity.findAllByIdClienteAfterLastSync",
				query = "select obj from PlanoEntity obj "
					  + "where obj.cliente.id = :ID_CLIENTE "
					  + "and obj.dataAlteracao >= :LAST_SYNC "
					  + "and (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc")
})
@SuppressWarnings("serial")
public class PlanoEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_PLANO", nullable=false, length=4)
	private Long id;
	
	@Column(name="NOME", nullable=true, length=255)
	private String nome;
	
	@Enumerated(EnumType.STRING)
	@Column(name="STATUS", nullable=true, length=10)
	private Status status;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_INICIO", nullable=true, length=11)
	private Date inicio;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_FIM", nullable=true, length=11)
	private Date fim;
	
	@Column(name="PERIODICIDADE", nullable=true, length=255)
	private String periodicidadeCobranca;
	
	@Column(name="DIA_VENCIMENTO", nullable=true, length=255)
	private Long diaVencimento;
	
	@Column(name="VALOR", nullable=true, length=10, precision = 5)
	private Double valor;
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_CLIENTE", nullable=true)
	private ClienteEntity cliente;
	
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
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Date getInicio() {
		return inicio;
	}
	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}
	public Date getFim() {
		return fim;
	}
	public void setFim(Date fim) {
		this.fim = fim;
	}
	public String getPeriodicidadeCobranca() {
		return periodicidadeCobranca;
	}
	public void setPeriodicidadeCobranca(String periodicidadeCobranca) {
		this.periodicidadeCobranca = periodicidadeCobranca;
	}
	public Long getDiaVencimento() {
		return diaVencimento;
	}
	public void setDiaVencimento(Long diaVencimento) {
		this.diaVencimento = diaVencimento;
	}
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	public ClienteEntity getCliente() {
		return cliente;
	}
	public void setCliente(ClienteEntity cliente) {
		this.cliente = cliente;
	}
	
}
