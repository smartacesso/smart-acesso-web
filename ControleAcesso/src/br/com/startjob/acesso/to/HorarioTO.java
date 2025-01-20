package br.com.startjob.acesso.to;

import java.util.Date;

import br.com.startjob.acesso.modelo.entity.HorarioEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;

public class HorarioTO {
	
	private Long id;
	private String nome;
	private String status;
	private String diasSemana;
	private Date horarioInicio;
	private Date horarioFim;
	
	private Boolean removed;
	private Date dataRemovido;
	
	private Long qtdeDeCreditos;
	private Long idPedestreRegra;
	
	public HorarioTO(HorarioEntity horario) {
		this.id = horario.getId();
		this.nome = horario.getNome();
		this.status = horario.getStatus().toString();
		this.diasSemana = horario.getDiasSemana();
		this.horarioInicio = horario.getHorarioInicio();
		this.horarioFim = horario.getHorarioFim();
		
		this.removed = horario.getRemovido();
		this.dataRemovido = horario.getDataRemovido();
		this.qtdeDeCreditos = horario.getQtdeDeCreditos();
	}
	
	public HorarioTO(final Long idHorario, final String nome, final String status, final String diasSemna, final Date horarioInicio,
			final Date horarioFim, final Long qtdeDeCreditos, final Long idPedestreRegra) {
		this.id = idHorario;
		this.nome = nome;
		this.status = status;
		this.diasSemana = diasSemna;
		this.horarioInicio = horarioInicio;
		this.horarioFim = horarioFim;
		this.qtdeDeCreditos = qtdeDeCreditos;
		this.idPedestreRegra = idPedestreRegra;
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

	public Boolean getRemoved() {
		return removed;
	}

	public void setRemoved(Boolean removed) {
		this.removed = removed;
	}

	public Date getDataRemovido() {
		return dataRemovido;
	}

	public void setDataRemovido(Date dataRemovido) {
		this.dataRemovido = dataRemovido;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getQtdeDeCreditos() {
		return qtdeDeCreditos;
	}

	public void setQtdeDeCreditos(Long qtdeDeCreditos) {
		this.qtdeDeCreditos = qtdeDeCreditos;
	}

	public Long getIdPedestreRegra() {
		return idPedestreRegra;
	}

	public void setIdPedestreRegra(Long idPedestreRegra) {
		this.idPedestreRegra = idPedestreRegra;
	}
	
}
