package br.com.startjob.acesso.modelo.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Filter;

import br.com.startjob.acesso.modelo.entity.base.BaseEntity;

@Entity
@Table(name="RE_PEDESTRE_REGRA")
@NamedQueries({
	@NamedQuery(name  = "PedestreRegraEntity.findAll", 
				query = "select obj "
				      + "from PedestreRegraEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "PedestreRegraEntity.findById", 
				query = "select obj from PedestreRegraEntity obj "
					  + "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name = "PedestreRegraEntity.findPedestreRegraAtivo",
				query = "select obj from PedestreRegraEntity obj "
					  + "left join fetch obj.regra r "
					  + "left join fetch obj.pedestre p "
					  + "where p.id = :ID_PEDESTRE "
					  + "and (obj.removido = false or obj.removido is null) ")
})
@SuppressWarnings("serial")
public class PedestreRegraEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_PEDESTRE_REGRA", nullable=false, length=4)
	private Long id;
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_REGRA", nullable=true)
	private RegraEntity regra;
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_PEDESTRE", nullable=true)
	private PedestreEntity pedestre;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="VALIDADE", nullable=true, length=11)
	private Date validade;
	
	@Column(name="QTDE_CREDITOS", nullable=true, length=10)
	private Long qtdeDeCreditos;
	
	@Column(name="QTDE_TOTAL_CREDITOS", nullable=true, length=10)
	private Long qtdeTotalDeCreditos;
	
	@Column(name="DIAS_VALIDADE_CREDITO", nullable=true, length=10)
	private Long diasValidadeCredito;
	
	@Column(name="BLOQUEADO", nullable=true, length=30)
	private Boolean bloqueado;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_INICIO_PERIODO", nullable=true, length=11)
	private Date dataInicioPeriodo;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_FIM_PERIODO", nullable=true, length=11)
	private Date dataFimPeriodo;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_INICIO_ESCALA_3_3", nullable=true, length=11)
	private Date dataInicioEscala3_3;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, 
			   orphanRemoval=false, targetEntity=HorarioEntity.class,
			   mappedBy="pedestreRegra")
	@Fetch(FetchMode.SUBSELECT)
	private List<HorarioEntity> horarios;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public RegraEntity getRegra() {
		return regra;
	}
	public void setRegra(RegraEntity regra) {
		this.regra = regra;
	}
	public PedestreEntity getPedestre() {
		return pedestre;
	}
	public void setPedestre(PedestreEntity pedestre) {
		this.pedestre = pedestre;
	}
	public Date getValidade() {
		return validade;
	}
	public void setValidade(Date validade) {
		this.validade = validade;
	}
	public Long getQtdeDeCreditos() {
		return qtdeDeCreditos;
	}
	public void setQtdeDeCreditos(Long qtdeDeCreditos) {
		this.qtdeDeCreditos = qtdeDeCreditos;
	}
	public Long getDiasValidadeCredito() {
		return diasValidadeCredito;
	}
	public void setDiasValidadeCredito(Long diasValidadeCredito) {
		this.diasValidadeCredito = diasValidadeCredito;
	}
	public Boolean getBloqueado() {
		return bloqueado;
	}
	public void setBloqueado(Boolean bloqueado) {
		this.bloqueado = bloqueado;
	}
	public Date getDataInicioPeriodo() {
		return dataInicioPeriodo;
	}
	public void setDataInicioPeriodo(Date dataInicioPeriodo) {
		this.dataInicioPeriodo = dataInicioPeriodo;
	}
	public Date getDataFimPeriodo() {
		return dataFimPeriodo;
	}
	public void setDataFimPeriodo(Date dataFimPeriodo) {
		this.dataFimPeriodo = dataFimPeriodo;
	}
	public Long getQtdeTotalDeCreditos() {
		return qtdeTotalDeCreditos;
	}
	public void setQtdeTotalDeCreditos(Long qtdeTotalDeCreditos) {
		this.qtdeDeCreditos = qtdeTotalDeCreditos;
		this.qtdeTotalDeCreditos = qtdeTotalDeCreditos;
	}
	public Date getDataInicioEscala3_3() {
		return dataInicioEscala3_3;
	}
	public void setDataInicioEscala3_3(Date dataInicioEscala3_3) {
		this.dataInicioEscala3_3 = dataInicioEscala3_3;
	}
	public List<HorarioEntity> getHorarios() {
		return horarios;
	}
	public void setHorarios(List<HorarioEntity> horarios) {
		this.horarios = horarios;
	}
	
}
