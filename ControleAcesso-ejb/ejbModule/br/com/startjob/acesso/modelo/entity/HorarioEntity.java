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
import javax.persistence.Transient;

import org.hibernate.annotations.FilterDef;

import br.com.startjob.acesso.modelo.ejb.HorarioSeniorDto;
import br.com.startjob.acesso.modelo.entity.base.BaseEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;

@Entity
@Table(name="TB_HORARIO")
@NamedQueries({
	@NamedQuery(name  = "HorarioEntity.findAll", 
				query = "select obj "
				      + "from HorarioEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "HorarioEntity.findById", 
				query = "select obj from HorarioEntity obj "
					  + "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name  = "HorarioEntity.findAllWithRemovidosByIdRegra",
				query = "select obj from HorarioEntity obj "
					  + "where obj.regra.id = :ID_REGRA "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "HorarioEntity.findAllWithRemovidosByIdRegraSenior",
				query = "select obj from HorarioEntity obj "
					  + "where obj.regra.id = :ID_REGRA "
					  + "and obj.idHorarioSenior = :ID_HORARIO_SENIOR "
					  + "order by obj.id asc")
})
@FilterDef(name="horarioValido", defaultCondition="(removido = 0 or removido is null)")
@SuppressWarnings("serial")
public class HorarioEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_HORARIO", nullable=false, length=4)
	private Long id;
	
	@Column(name="NOME", nullable=true, length=255)
	private String nome;
	
	@Enumerated(EnumType.STRING)
	@Column(name="STATUS", nullable=true, length=100)
	private Status status;
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_REGRA", nullable=true)
	private RegraEntity regra;
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_PEDESTRE_REGRA", nullable=true)
	private PedestreRegraEntity pedestreRegra;
	
	@Column(name="DIAS_SEMANA", nullable=true, length=100)
	private String diasSemana;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="HORARIO_INI", nullable=true, length=11)
	private Date horarioInicio;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="HORARIO_FIM", nullable=true, length=11)
	private Date horarioFim;
	
	@Column(name="QTDE_CREDITOS", nullable=true, length=10)
	private Long qtdeDeCreditos;
	
	@Column(name="ID_HORARIO_SENIOR", nullable=true, length=10)
	private Integer idHorarioSenior;
	
	@Transient
	private Boolean sunday;
	
	@Transient
	private Boolean monday;
	
	@Transient
	private Boolean tuesday;
	
	@Transient
	private Boolean wednesday;
	
	@Transient
	private Boolean thursday;
	
	@Transient
	private Boolean friday;
	
	@Transient
	private Boolean saturday;
	
	public HorarioEntity() {
	}

	public HorarioEntity newHorarioEntity(final PedestreRegraEntity pedestreRegraEntity) {
		final HorarioEntity newHorarioEntity = new HorarioEntity();
		newHorarioEntity.setNome(nome + " pedestre");
		newHorarioEntity.setStatus(status);
		newHorarioEntity.setPedestreRegra(pedestreRegraEntity);
		newHorarioEntity.setDiasSemana(diasSemana);
		newHorarioEntity.setHorarioInicio(horarioInicio);
		newHorarioEntity.setHorarioFim(horarioFim);
		newHorarioEntity.setQtdeDeCreditos(qtdeDeCreditos);
		
		return newHorarioEntity;
	}
	
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
	public RegraEntity getRegra() {
		return regra;
	}
	public void setRegra(RegraEntity regra) {
		this.regra = regra;
	}
	public String getDiasSemana() {
		return diasSemana;
	}
	public void setDiasSemana(String diasSemana) {
		this.diasSemana = diasSemana;
	}
	public Date getHorarioInicio() {
		return horarioInicio;
	}
	public void setHorarioInicio(Date horarioInicio) {
		this.horarioInicio = horarioInicio;
	}
	public Date getHorarioFim() {
		return horarioFim;
	}
	public void setHorarioFim(Date horarioFim) {
		this.horarioFim = horarioFim;
	}
	public Boolean getSunday() {
		return getDia(7);
	}

	public void setSunday(Boolean sunday) {
		setDia(7, sunday);
	}

	public Boolean getMonday() {
		return getDia(1);
	}

	public void setMonday(Boolean monday) {
		setDia(1, monday);
	}

	public Boolean getTuesday() {
		return getDia(2);
	}

	public void setTuesday(Boolean tuesday) {
		setDia(2,tuesday);
	}

	public Boolean getWednesday() {
		return getDia(3);
	}

	public void setWednesday(Boolean wednesday) {
		setDia(3,wednesday);
	}

	public Boolean getThursday() {
		return getDia(4);
	}

	public void setThursday(Boolean thursday) {
		setDia(4, thursday);
	}

	public Boolean getFriday() {
		return getDia(5);
	}

	public void setFriday(Boolean friday) {
		setDia(5, friday);
	}

	public Boolean getSaturday() {
		return getDia(6);
	}

	public void setSaturday(Boolean saturday) {
		setDia(6, saturday);
	}
	
	private Boolean getDia(Integer dia) {
		if (diasSemana != null) {
			return diasSemana.contains(dia.toString());
		}
		return false;
	}
	
	private void setDia(Integer dia, Boolean selecionado) {
		if (diasSemana == null)
			diasSemana = "";
		if (selecionado) {
			if (!diasSemana.contains(dia.toString()))
				diasSemana = diasSemana + dia.toString();
		}
		else
			diasSemana = diasSemana.replaceAll(dia.toString(), "");
	}
	public Long getQtdeDeCreditos() {
		return qtdeDeCreditos;
	}
	public void setQtdeDeCreditos(Long qtdeDeCreditos) {
		this.qtdeDeCreditos = qtdeDeCreditos;
	}

	public PedestreRegraEntity getPedestreRegra() {
		return pedestreRegra;
	}

	public void setPedestreRegra(PedestreRegraEntity pedestreRegra) {
		this.pedestreRegra = pedestreRegra;
	}

	public void update(HorarioSeniorDto dto) {
		// TODO Auto-generated method stub
		this.horarioInicio = dto.getInicio();
		this.horarioFim = dto.getFim();
		
	}

	public Integer getIdHorarioSenior() {
		return idHorarioSenior;
	}

	public void setIdHorarioSenior(Integer idHorarioSenior) {
		this.idHorarioSenior = idHorarioSenior;
	}

}
