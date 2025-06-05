package br.com.startjob.acesso.modelo.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Filter;

import br.com.startjob.acesso.modelo.entity.base.ClienteBaseEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.TipoEscala;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.enumeration.TipoRegra;

@Entity
@Table(name="TB_REGRA")
@NamedQueries({
	@NamedQuery(name  = "RegraEntity.findAll", 
				query = "select obj "
				      + "from RegraEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "RegraEntity.findById", 
				query = "select obj from RegraEntity obj "
					  + "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name  = "RegraEntity.findByIdComplete", 
				query = "select obj from RegraEntity obj "
					  + "	left join fetch obj.horarios h "
					  + "	left join fetch obj.empresa e "
					  + "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name = "RegraEntity.findAllComEmpresa",
				query = "select obj from RegraEntity obj "
					  + " left join fetch obj.empresa e "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name = "RegraEntity.findAllByNome",
				query = "select obj from RegraEntity obj "
					  + "left join fetch obj.horarios "
					  + "where obj.nome like :NOME "
					  + " and (obj.removido = false or obj.removido is null) "
					  + " and obj.status = 'ATIVO' "
					  + " and obj.cliente.id = :ID_CLIENTE "
					  + " and (obj.tipoPedestre = :TIPO_PEDESTRE or obj.tipoPedestre = 'AMBOS') "
					  + "order by obj.id asc"),
	@NamedQuery(name = "RegraEntity.findByNome", 
				query = "select obj from RegraEntity obj "
					  + "where obj.nome = :NOME_REGRA "
					  + " and obj.cliente.id = :ID_CLIENTE "
					  + " and (obj.removido = false or obj.removido is null) "
					  + " and obj.status = 'ATIVO' "
					  + "order by obj.id asc"),
	@NamedQuery(name = "RegraEntity.findByIdEscala", 
				query = "select obj from RegraEntity obj "
					  + "where obj.idEscala = :ID_ESCALA "
					  + " and obj.cliente.id = :ID_CLIENTE "
					  + " and (obj.removido = false or obj.removido is null) "
					  + " and obj.status = 'ATIVO' "
					  + "order by obj.id asc"),
	@NamedQuery(name = "RegraEntity.findAllRegrasDePedestreComEmpresa",
				query = "select obj from RegraEntity obj "
					  + " left join fetch obj.empresa e "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name = "RegraEntity.findAllByIdClienteAfterLastSync",
				query = "select distinct obj from RegraEntity obj "
					  + "	left join fetch obj.empresa e "
					  + "where obj.cliente.id = :ID_CLIENTE "
					  + "and obj.dataAlteracao >= :LAST_SYNC "
					  + "order by obj.id asc")
})
@SuppressWarnings("serial")
public class RegraEntity extends ClienteBaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_REGRA", nullable=false, length=4)
	private Long id;
	
	@Column(name="NOME", nullable=true, length=255)
	private String nome;
	
	@Enumerated(EnumType.STRING)
	@Column(name="STATUS", nullable=true, length=100)
	private Status status;
	
	@Temporal(TemporalType.DATE)
	@Column(name="VALIDADE", nullable=true, length=11)
	private Date validade;
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_EMPRESA", nullable=true)
	private EmpresaEntity empresa;
	
	@Enumerated(EnumType.STRING)
	@Column(name="TIPO", nullable=true, length=100)
	private TipoRegra tipo;
	
	@Enumerated(EnumType.STRING)
	@Column(name="TIPO_PEDESTRE", nullable=true, length=100)
	private TipoPedestre tipoPedestre;
	
	@Column(name="DESCRICAO", nullable=true, length=255)
	private String descricao;
	
	@Temporal(TemporalType.DATE)
	@Column(name="DATA_INICIO_PERIODO", nullable=true, length=11)
	private Date dataInicioPeriodo;
	
	@Temporal(TemporalType.DATE)
	@Column(name="DATA_FIM_PERIODO", nullable=true, length=11)
	private Date dataFimPeriodo;
	
	@Enumerated(EnumType.STRING)
	@Column(name="TIPO_ESCALA", nullable=true, length=100)
	private TipoEscala tipoEscala;
	
	@Column(name="PERMITE_BLOQUEIO", nullable=true, length=30)
	private Boolean permiteBloqueio;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="HORARIO_INICIO_TURNO", nullable=true, length=11)
	private Date horarioInicioTurno;
	
	@Column(name="QTDE_CREDITOS", nullable=true, length=10)
	private Long qtdeDeCreditos;
	
	@Column(name="DIAS_VALIDADE_CREDITO", nullable=true, length=10)
	private Long diasValidadeCredito;
	
	@Column(name="ID_TEMPLATE", nullable=true, length=10)
	private Integer idTemplate;
	
	@Column(name="ID_PLANO", nullable=true, length=10)
	private Integer idPlano;
	
	@Column(name="ID_ESCALA", nullable=true, length=10)
	private Integer idEscala;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, 
			   orphanRemoval=false, targetEntity=HorarioEntity.class,
			   mappedBy="regra")
	@Fetch(FetchMode.SUBSELECT)
	@Filter(name="horarioValido")
	private List<HorarioEntity> horarios;
	
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
	public Date getValidade() {
		return validade;
	}
	public void setValidade(Date validade) {
		this.validade = validade;
	}
	public EmpresaEntity getEmpresa() {
		return empresa;
	}
	public void setEmpresa(EmpresaEntity empresa) {
		this.empresa = empresa;
	}
	public TipoRegra getTipo() {
		return tipo;
	}
	public void setTipo(TipoRegra tipo) {
		this.tipo = tipo;
	}
	public TipoPedestre getTipoPedestre() {
		return tipoPedestre;
	}
	public void setTipoPedestre(TipoPedestre tipoPedestre) {
		this.tipoPedestre = tipoPedestre;
	}
	public List<HorarioEntity> getHorarios() {
		return horarios;
	}
	public void setHorarios(List<HorarioEntity> horarios) {
		this.horarios = horarios;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
	public TipoEscala getTipoEscala() {
		return tipoEscala;
	}
	public Boolean getPermiteBloqueio() {
		return permiteBloqueio;
	}
	public void setPermiteBloqueio(Boolean permiteBloqueio) {
		this.permiteBloqueio = permiteBloqueio;
	}
	public void setTipoEscala(TipoEscala tipoEscala) {
		this.tipoEscala = tipoEscala;
	}
	public Date getHorarioInicioTurno() {
		return horarioInicioTurno;
	}
	public void setHorarioInicioTurno(Date horarioInicioTurno) {
		this.horarioInicioTurno = horarioInicioTurno;
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
	public Integer getIdTemplate() {
		return idTemplate;
	}
	public void setIdTemplate(Integer idTemplate) {
		this.idTemplate = idTemplate;
	}
	public Integer getIdPlano() {
		return idPlano;
	}
	public void setIdPlano(Integer idPlano) {
		this.idPlano = idPlano;
	}
	public Integer getIdEscala() {
		return idEscala;
	}
	public void setIdEscala(Integer idEscala) {
		this.idEscala = idEscala;
	}
	
}
